package com.jiezipoi.mall.service;

import com.jiezipoi.mall.config.GoodsConfig;
import com.jiezipoi.mall.dao.GoodsDao;
import com.jiezipoi.mall.dto.MallGoodsDTO;
import com.jiezipoi.mall.entity.Goods;
import com.jiezipoi.mall.entity.GoodsTag;
import com.jiezipoi.mall.exception.NotFoundException;
import com.jiezipoi.mall.utils.FileNameGenerator;
import com.jiezipoi.mall.utils.dataTable.DataTableResult;
import com.jiezipoi.mall.utils.dataTable.Order;
import com.jiezipoi.mall.utils.dataTable.request.GoodsTableRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GoodsService {
    private final GoodsDao goodsDao;
    private final GoodsConfig goodsConfig;
    private final GoodsTagService goodsTagService;

    public GoodsService(GoodsDao goodsDao, GoodsConfig goodsConfig, GoodsTagService goodsTagService) {
        this.goodsDao = goodsDao;
        this.goodsConfig = goodsConfig;
        this.goodsTagService = goodsTagService;
    }

    public boolean isGoodsExist(long id) {
        return goodsDao.selectGoodsById(id) != null;
    }

    /**
     * 创建商品，把储存的临时数据和图片都转移到goods文件夹中
     * @param goods 商品
     * @param userId 用户ID
     */
    @Transactional
    public void createGoods(Goods goods, long userId) throws IOException {
        String tempCoverImgUrl = goods.getGoodsCoverImg();
        goods.setGoodsCoverImg(convertTempUrlToPermanentUrl(tempCoverImgUrl));
        String tempDetail = goods.getGoodsDetailContent();
        goods.setGoodsDetailContent(convertTempUrlToPermanentUrl(tempDetail));
        goodsDao.insertSelective(goods);
        if (goods.getTag() != null && !goods.getTag().isEmpty()) {
            List<GoodsTag> tagWithId = goodsTagService.getOrCreateGoodsTagByTagName(goods.getTag());
            goodsTagService.assignTagToGoodsInDB(goods.getGoodsId(), tagWithId);
        }
        walkUserTempFile(userId);
    }

    /**
     * 临时商品的图片是保存在Temp文件里的，当正式创建的时候需要把该含有临时文件地址转为正确的地址
     * @param text 含有Temp地址的字符串
     * @return 将字符串中所有的Temp地址替换为正式地址
     */
    private String convertTempUrlToPermanentUrl(String text) {
        String tempUrlRegex =
                goodsConfig.getExposeUrl() +
                        "(" + goodsConfig.getUserTempFilePrefix() + "\\w+/)?\\w+" +
                        goodsConfig.getImageSuffix();
        Pattern pattern = Pattern.compile(tempUrlRegex);
        Matcher matcher = pattern.matcher(text);
        StringBuilder stringBuilder = new StringBuilder();
        while (matcher.find()) {
            String url = matcher.group(0);
            String tempDir = matcher.group(1);
            String replacement = url.replaceAll(tempDir, "");
            matcher.appendReplacement(stringBuilder, replacement);
        }
        matcher.appendTail(stringBuilder);
        return stringBuilder.toString();
    }

    private void walkUserTempFile(long userId) throws IOException {
        Path tempDir = getUserTempDir(userId);
        Files.walkFileTree(tempDir, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String imageSuffix = goodsConfig.getImageSuffix();
                if (file.toString().endsWith(imageSuffix)) {
                    Path fileName = file.getFileName();
                    Files.move(file, goodsConfig.getGoodsFilePath().resolve(fileName));
                } else {
                    Files.delete(file);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }



    public void saveTempGoods(Goods goods, long userId) throws IOException {
        Path uploadDir = getUserTempDir(userId);
        Path fileDir = uploadDir.resolve(goodsConfig.getUserTempDataFilename());
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileDir.toFile()));
        outputStream.writeObject(goods);
        outputStream.close();
    }

    /**
     * 上传的临时的商品封面，
     *
     * @param file   用户上传的图片
     * @param userId 用户id
     * @return 图片在服务器的地址
     */
    public String saveTempCoverImage(MultipartFile file, long userId) throws IOException {
        Path uploadDir = getUserTempDir(userId);
        String filename = generateImageFileName();
        Path savedLocation = saveFile(uploadDir, filename, file);
        return goodsConfig.getExposeUrl(savedLocation);
    }

    /**
     * 长传临时的商品介绍文件（创建时保存的）
     *
     * @param file      用户上传的文件
     * @param userId    用户ID
     * @return 对应的地址
     */
    public String saveTempCKEditorFile(MultipartFile file, long userId) throws IOException {
        Path uploadDir = getUserTempDir(userId);
        String filename = FileNameGenerator.generateFileName() + goodsConfig.getUserTempFilePrefix();
        Path savedLocation = saveFile(uploadDir, filename, file);
        return goodsConfig.getExposeUrl(savedLocation);
    }

    public String saveCKEditorFile(MultipartFile file, String goodsId) throws IOException {
        Path uploadDir = goodsConfig.getGoodsFilePath();
        String filename = generateImageFileName(goodsId);
        Path savedLocation = saveFile(uploadDir, filename, file);
        return goodsConfig.getExposeUrl(savedLocation);
    }

    private Path getUserTempDir(long userId) {
        Path uploadDir = goodsConfig.getGoodsFilePath();
        return uploadDir.resolve(goodsConfig.getUserTempFileName(userId));
    }

    public Goods getUserTempGoods(long userId) throws IOException, ClassNotFoundException {
        Path userTempFile = getUserTempDir(userId);
        Path serializedGoodsDir = userTempFile.resolve(goodsConfig.getUserTempDataFilename());
        if (!Files.exists(serializedGoodsDir)) {
            return null;
        }
        FileInputStream fileInputStream = new FileInputStream(serializedGoodsDir.toFile());
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        Goods tempGoods = (Goods) objectInputStream.readObject();
        objectInputStream.close();
        return tempGoods;
    }

    public DataTableResult getGoodsPage(GoodsTableRequest request) {
        Integer start = request.getStart();
        Integer limit = request.getLength();
        String path = request.getPath();
        String keyword = request.getSearch().getValue();
        Order order = request.getOrder().get(0);
        Integer colNumber = order.getColumn() + 1;
        String dir = order.getDir();
        List<Goods> goods = goodsDao.list(start, limit, path, keyword, colNumber, dir);
        int totalCount = goodsDao.selectCountTotalOfList(path, keyword);
        return new DataTableResult(goods, totalCount);
    }

    public Goods getGoods(Long id) throws NotFoundException {
        if (id == null) {
            throw new NullPointerException();
        }
        Goods goods = goodsDao.selectGoodsById(id);
        if (goods == null) {
            throw new NotFoundException();
        }
        return goods;
    }

    public Goods getGoodsWithTagList(Long id) throws NotFoundException {
        if (id == null) {
            throw new NullPointerException();
        }
        Goods goods = goodsDao.selectGoodsWithTagByGoodsId(id);
        if (goods == null) {
            throw new NotFoundException();
        }
        return goods;
    }

    @Transactional
    public void updateGoods(Goods goods, MultipartFile file) throws IOException {
        if (file != null) {
            //删除旧图片，保存新图片并且修改Goods对象值
            Path uploadDir = goodsConfig.getGoodsFilePath();
            Goods queryGoods = goodsDao.selectGoodsById(goods.getGoodsId());
            String oldImageUrl = queryGoods.getGoodsCoverImg();
            String oldFileName = getFileNameByUrl(oldImageUrl);
            deleteGoodsImage(oldFileName);
            String fileName = generateImageFileName();
            Path imagePath = saveFile(uploadDir, fileName, file);
            String goodsImageUrl = goodsConfig.getExposeUrl(imagePath);
            goods.setGoodsCoverImg(goodsImageUrl);
        }
        goodsDao.updateByPrimaryKeySelective(goods);
        goodsTagService.updateGoodsHasTag(goods.getGoodsId(), goods.getTag());
    }

    private String getFileNameByUrl(String url) {
        String[] temp = url.split("/");
        return temp[temp.length - 1];
    }

    private Path saveFile(Path directory, String fileName, MultipartFile file) throws IOException{
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
        Path filePath = Paths.get(directory + File.separator + fileName);
        Files.write(filePath, file.getBytes());
        return filePath;
    }

    private void deleteGoodsImage(String fileName) throws IOException {
        Path storePath = goodsConfig.getGoodsFilePath();
        Files.delete(Path.of(storePath + File.separator + fileName));
    }

    /**
     * @param prefix （可选）前缀
     * 随即生成带有后缀的文件名
     * @return 带有后缀的文件名
     */
    private String generateImageFileName(String prefix) {
        String fileName = FileNameGenerator.generateFileName() + goodsConfig.getImageSuffix();
        if (prefix != null) {
            fileName = prefix + "_" + fileName;
        }
        return fileName;
    }

    private String generateImageFileName() {
        return generateImageFileName(null);
    }

    public List<MallGoodsDTO> getGoodsListByTag(String keyword) {
        String[] keywordArray = keyword.split(" ");
        return goodsDao.findByTagContaining(keywordArray);
    }

    public List<MallGoodsDTO> getGoodsListByCategory(Long categoryId) {
        if (categoryId == null) {
            throw new NullPointerException();
        }
        return goodsDao.findByCategory(categoryId);
    }

    public List<Goods> getGoodsListById(List<Long> ids) {
        return goodsDao.findGoodsByIds(ids);
    }

    public List<Goods> getGoodsListByGoodsName(String keyword) {
        return goodsDao.findByNameContaining(keyword);
    }

    public void reduceGoodsStock(Map<Long, Integer> quantityList) {
        goodsDao.reduceStockByPrimaryKey(quantityList);
    }
}
