package com.jiezipoi.mall.service;

import com.jiezipoi.mall.config.GoodsConfig;
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

@Service
public class GoodsService {
    final GoodsDao goodsDao;
    final GoodsConfig goodsConfig;

    public GoodsService(GoodsDao goodsDao, GoodsConfig goodsConfig) {
        this.goodsDao = goodsDao;
        this.goodsConfig = goodsConfig;
    }

    public Response<?> createGoods(Goods goods, int userId) {
        if (!isValidGoods(goods)) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        if (goodsDao.insertSelective(goods) > 0) {
            try {
                deleteTempFile(userId);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new Response<>(CommonResponse.SUCCESS);
        } else {
            return new Response<>(CommonResponse.ERROR);
        }
    }

    private void deleteTempFile(int userId) throws IOException {
        Path tempDir = getUserTempDir(userId);
        Files.walkFileTree(tempDir, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
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

    public Response<?> saveTempGoods(Goods goods, int userId) {
        Path uploadDir = getUserTempDir(userId);
        String fileDir = uploadDir + "/" + goodsConfig.getUserTempDataFilename();

        try {
            if (!Files.exists(uploadDir)) {
                Files.createFile(uploadDir);
            }
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileDir));
            outputStream.writeObject(goods);
            outputStream.close();
            return new Response<>(CommonResponse.SUCCESS);
        } catch (IOException e) {
            e.printStackTrace();
            return new Response<>(CommonResponse.ERROR);
        }
    }

    /**
     * 上传的临时的商品封面，该图
     * @param file 用户上传的图片
     * @param userId 用户id
     * @return 图片在服务器的地址
     */
    public Response<?> uploadCoverImage(MultipartFile file, int userId) {
        Response<String> response = new Response<>();
        Path uploadDir = getUserTempDir(userId);
        String filename = goodsConfig.getCoverImageFilename();
        Path imagePath = Paths.get(uploadDir + "/" + filename);
        try {
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            Files.write(imagePath, file.getBytes());
            response.setResponse(CommonResponse.SUCCESS);
            response.setData(goodsConfig.getUserTempFileName(userId) + "/" + filename);
        } catch (IOException e) {
            e.printStackTrace();
            response.setResponse(CommonResponse.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    private Path getUserTempDir(int userId) {
        Path uploadDirString = goodsConfig.getFileStorePath();
        return Paths.get(uploadDirString + goodsConfig.getUserTempFileName(userId));//return: {config_path}/temp_{userid}
    }

    public Goods getUserTempGoods(int userId) {
        Path userTempFile = getUserTempDir(userId);
        String serializedGoodsDir = userTempFile + "/" + goodsConfig.getUserTempDataFilename();
        if (!Files.exists(Paths.get(serializedGoodsDir))) {
            return null;
        }
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(serializedGoodsDir))) {
            return (Goods) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Response<?> uploadGoodsDetailImage(MultipartFile image, Integer goodsId, int userId) {
        Path uploadDir;
        if (goodsId != null && goodsId != 0) {
            uploadDir = getGoodsDir(goodsId);
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
            response.setData(goodsConfig.getUserTempFileName(userId) + "/" + fileName);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return new Response<>(CommonResponse.INTERNAL_SERVER_ERROR);
        }
    }

    private Path getGoodsDir(Integer goodsId) {
        return Paths.get(goodsId.toString());
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
        if (!isValidGoods(goods)) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }

        if (file != null) {
            String coverImagePath = saveCoverImage(file, goods.getId());
        }

        if (goodsDao.updateByPrimaryKeySelective(goods) > 0) {
            return new Response<>(CommonResponse.SUCCESS);
        } else {
            return new Response<>(CommonResponse.DATA_NOT_EXIST);
        }
    }

    private String saveCoverImage(MultipartFile image, Long goodsId) {
        Path storePath = goodsConfig.getFileStorePath();

        return null;
    }
}