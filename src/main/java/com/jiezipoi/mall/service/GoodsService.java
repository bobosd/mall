package com.jiezipoi.mall.service;

import com.jiezipoi.mall.config.GoodsConfig;
import com.jiezipoi.mall.dao.GoodsCategoryDao;
import com.jiezipoi.mall.dao.GoodsDao;
import com.jiezipoi.mall.entity.Goods;
import com.jiezipoi.mall.utils.CommonResponse;
import com.jiezipoi.mall.utils.FileNameGenerator;
import com.jiezipoi.mall.utils.Response;
import com.jiezipoi.mall.utils.dataTable.DataTableResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GoodsService {
    final GoodsDao goodsDao;
    final GoodsCategoryDao goodsCategoryDao;
    final GoodsConfig goodsConfig;

    public GoodsService(GoodsDao goodsDao, GoodsConfig goodsConfig, GoodsCategoryDao goodsCategoryDao) {
        this.goodsCategoryDao = goodsCategoryDao;
        this.goodsDao = goodsDao;
        this.goodsConfig = goodsConfig;
    }

    /**
     * 创建商品，把储存的临时数据和图片都转移到goods文件夹中
     * @param goods 商品
     * @param userId 用户ID
     * @return 服务器响应
     */
    public Response<?> createGoods(Goods goods, int userId) {
        if (!isValidGoods(goods)) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }

        String tempCoverImgUrl = goods.getGoodsCoverImg();
        goods.setGoodsCoverImg(replaceTempUrl(tempCoverImgUrl));
        String tempDetail = goods.getGoodsDetailContent();
        goods.setGoodsDetailContent(replaceTempUrl(tempDetail));

        try {
            walkUserTempFile(userId);
        } catch (IOException e) {
            return new Response<>(CommonResponse.INTERNAL_SERVER_ERROR);
        }

        if (goodsDao.insertSelective(goods) > 0) {
            return new Response<>(CommonResponse.SUCCESS);
        } else {
            return new Response<>(CommonResponse.ERROR);
        }
    }

    /**
     * 临时商品的图片是保存在Temp文件里的，当正式创建的时候需要把该含有临时文件地址转为正确的地址
     * @param text 含有Temp地址的字符串
     * @return 将字符串中所有的Temp地址替换为正式地址
     */
    private String replaceTempUrl(String text) {
        System.out.println(text);
        String tempUrlRegex =
                "/goods/img/(" + goodsConfig.getUserTempFilePrefix() + "\\w+/)?\\w+" + goodsConfig.getImageSuffix();
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
        System.out.println(stringBuilder);
        return stringBuilder.toString();
    }

    private void walkUserTempFile(int userId) throws IOException {
        Path tempDir = getUserTempDir(userId);
        Files.walkFileTree(tempDir, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String imageSuffix = goodsConfig.getImageSuffix();
                if (file.toString().endsWith(imageSuffix)) {
                    Path fileName = file.getFileName();
                    Files.move(file, goodsConfig.getFileStorePath().resolve(fileName));
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

    private boolean isValidGoods(Goods goods) {
        return goods.getGoodsName() != null &&
                !goods.getGoodsName().isBlank() &&
                goods.getGoodsIntro() != null &&
                !goods.getGoodsIntro().isBlank() &&
                goods.getOriginalPrice() != null &&
                goods.getGoodsCategoryId() != null &&
                goods.getSellingPrice() != null &&
                goods.getStockNum() != null &&
                goods.getGoodsSellStatus() != null &&
                goods.getGoodsCoverImg() != null &&
                !goods.getGoodsCoverImg().isBlank() &&
                goods.getGoodsDetailContent() != null &&
                !goods.getGoodsDetailContent().isBlank();
    }

    private boolean isValidForUpdate(Goods goods) {
        return goods.getGoodsName() != null &&
                !goods.getGoodsName().isBlank() &&
                goods.getGoodsIntro() != null &&
                !goods.getGoodsIntro().isBlank() &&
                goods.getOriginalPrice() != null &&
                goods.getGoodsCategoryId() != null &&
                goods.getSellingPrice() != null &&
                goods.getStockNum() != null &&
                goods.getGoodsSellStatus() != null &&
                goods.getGoodsDetailContent() != null &&
                !goods.getGoodsDetailContent().isBlank();
    }

    public Response<?> saveTempGoods(Goods goods, int userId) {
        Path uploadDir = getUserTempDir(userId);
        String fileDir = uploadDir + File.separator + goodsConfig.getUserTempDataFilename();
        try {
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileDir));
            outputStream.writeObject(goods);
            outputStream.close();
            return new Response<>(CommonResponse.SUCCESS);
        } catch (IOException e) {
            return new Response<>(CommonResponse.ERROR);
        }
    }

    /**
     * 上传的临时的商品封面，
     *
     * @param file   用户上传的图片
     * @param userId 用户id
     * @return 图片在服务器的地址
     */
    public Response<?> saveTempCoverImage(MultipartFile file, int userId) {
        Response<String> response = new Response<>();
        Path uploadDir = getUserTempDir(userId);
        String filename = FileNameGenerator.generateFileName() + ".jpg";
        try {
            Path savedLocation = saveImage(uploadDir, filename, file);
            String url = goodsConfig.getExposeUrl(savedLocation);
            response.setResponse(CommonResponse.SUCCESS);
            response.setData(url);
        } catch (IOException e) {
            response.setResponse(CommonResponse.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    private Path getUserTempDir(int userId) {
        Path uploadDirString = goodsConfig.getFileStorePath();
        return Paths.get(uploadDirString + "/" + goodsConfig.getUserTempFileName(userId));
        //return: {config_path}/temp_{userid}
    }

    public Goods getUserTempGoods(int userId) {
        Path userTempFile = getUserTempDir(userId);
        String serializedGoodsDir = userTempFile + "/" + goodsConfig.getUserTempDataFilename();
        if (!Files.exists(Paths.get(serializedGoodsDir))) {
            return null;
        }
        try (FileInputStream fileInputStream = new FileInputStream(serializedGoodsDir);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            return (Goods) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    public Response<?> uploadGoodsDetailImage(MultipartFile image, Integer goodsId, int userId) {
        Path uploadDir;
        if (goodsId != null && goodsId != 0) {
            uploadDir = getGoodsStoragePath();
        } else {
            uploadDir = getUserTempDir(userId);
        }
        String fileName = FileNameGenerator.generateFileName() + ".jpg";
        Path imagePath = Paths.get(uploadDir + "/" + fileName);
        try {
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            Files.write(imagePath, image.getBytes());
            Response<String> response = new Response<>();
            response.setResponse(CommonResponse.SUCCESS);
            response.setData(goodsConfig.getUserTempFileName(userId) + fileName);
            return response;
        } catch (IOException e) {
            return new Response<>(CommonResponse.INTERNAL_SERVER_ERROR);
        }
    }

    private Path getGoodsStoragePath() {
        return goodsConfig.getFileStorePath();
    }

    public Response<?> list(Integer start, Integer limit, String categoryPath, Integer level) {
        if (start == null || limit == null) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        List<Goods> goods;
        int totalCount;
        if (categoryPath == null && level == null) {
            goods = goodsDao.list(start, limit);
            totalCount = goodsDao.getGoodsCount();
        } else {
            goods = goodsDao.listByCategory(start, limit, categoryPath, level);
            totalCount = goodsDao.getCategoryGoodsCount(categoryPath);
        }


        DataTableResult dataTableResult = new DataTableResult(goods, totalCount);
        Response<DataTableResult> response = new Response<>(CommonResponse.SUCCESS);
        response.setData(dataTableResult);
        return response;
    }

    public Response<?> getGoodsById(Long id) {
        if (id == null) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        Goods goods = goodsDao.selectGoodsById(id);
        if (goods == null) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        Response<Goods> response = new Response<>(CommonResponse.SUCCESS);
        response.setData(goods);
        return response;
    }

    public Response<?> updateGoods(Goods goods, MultipartFile file) {
        if (!isValidForUpdate(goods)) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        if (file != null) {
            //删除旧图片，保存新图片并且修改Goods对象值
            try {
                Path uploadDir = goodsConfig.getFileStorePath();
                Goods queryGoods = goodsDao.selectGoodsById(goods.getId());
                String oldImageUrl = queryGoods.getGoodsCoverImg();
                String oldFileName = getFileNameByUrl(oldImageUrl);
                deleteGoodsImage(oldFileName);
                String fileName = FileNameGenerator.generateFileName();
                Path imagePath = saveImage(uploadDir, fileName, file);
                String goodsImageUrl = goodsConfig.getExposeUrl(imagePath);
                goods.setGoodsCoverImg(goodsImageUrl);
            } catch (IOException e) {
                return new Response<>(CommonResponse.INTERNAL_SERVER_ERROR);
            }
        }
        if (goodsDao.updateByPrimaryKeySelective(goods) > 0) {
            return new Response<>(CommonResponse.SUCCESS);
        } else {
            return new Response<>(CommonResponse.DATA_NOT_EXIST);
        }
    }

    private String getFileNameByUrl(String url) {
        String[] temp = url.split("/");
        return temp[temp.length - 1];
    }

    private Path saveImage(Path directory, String fileName, MultipartFile file) throws IOException{
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
        Path filePath = Paths.get(directory + File.separator + fileName);
        Files.write(filePath, file.getBytes());
        return filePath;
    }

    private void deleteGoodsImage(String fileName) throws IOException {
        Path storePath = goodsConfig.getFileStorePath();
        Files.delete(Path.of(storePath + File.separator + fileName));
    }
}