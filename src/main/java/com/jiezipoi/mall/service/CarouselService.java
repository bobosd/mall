package com.jiezipoi.mall.service;

import com.jiezipoi.mall.config.CarouselConfig;
import com.jiezipoi.mall.dao.CarouselDao;
import com.jiezipoi.mall.entity.Carousel;
import com.jiezipoi.mall.utils.CommonResponse;
import com.jiezipoi.mall.utils.FileNameGenerator;
import com.jiezipoi.mall.utils.DatatableResult;
import com.jiezipoi.mall.utils.Result;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CarouselService {
    @Resource
    private CarouselDao carouselDao;
    private final CarouselConfig carouselConfig;
    public CarouselService(CarouselConfig carouselConfig) {
        this.carouselConfig = carouselConfig;
    }

    public Result<DatatableResult> getCarouselPage(Integer start, Integer limit, String orderBy) {
        if (start == null || limit == null) {
            return new Result<>(CommonResponse.INVALID_DATA);
        }
        if (orderBy == null) {
            orderBy = "carousel_rank DESC"; //default order by
        }
        List<Carousel> carousels = carouselDao.findCarouselList(start, limit, orderBy);
        int total = carouselDao.getTotalCarousels();
        Result<DatatableResult> response = new Result<>(CommonResponse.SUCCESS);
        response.setData(new DatatableResult(carousels, total));
        return response;
    }

    public Result<?> saveCarousel(MultipartFile file, String redirectUrl, Integer order, int userId) {
        if (!StringUtils.hasLength(redirectUrl) || order == null) {
            return new Result<>(CommonResponse.INVALID_DATA);
        }
        String carouselUrl = saveCarouselImage(file);
        if (carouselUrl == null)
            return new Result<>(CommonResponse.INTERNAL_SERVER_ERROR);
        Carousel carousel = new Carousel();
        carousel.setCarouselRank(order);
        carousel.setRedirectUrl(redirectUrl);
        carousel.setCreateUser(userId);
        carousel.setCarouselUrl(carouselUrl);
        if (carouselDao.insertSelective(carousel) > 0) {
            return new Result<>(CommonResponse.SUCCESS);
        } else {
            return new Result<>(CommonResponse.ERROR);
        }
    }

    private String saveCarouselImage(MultipartFile file) {
        String uploadDirString = carouselConfig.getImageDirectory();
        Path uploadDir = Paths.get(uploadDirString);
        String filename = FileNameGenerator.generateFileName() + ".jpg";
        Path imagePath = Paths.get(uploadDirString + filename);
        try {
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            Files.write(imagePath, file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return filename;
    }

    private void deleteCarouselImage(String fileName) {
        Path imagePath = Paths.get(carouselConfig.getImageDirectory() + fileName);
        try {
            Files.delete(imagePath);
        } catch (IOException ignored) {

        }
    }

    public Result<?> updateCarousel(Integer id, MultipartFile image, String redirectUrl, Integer order, Integer userId) {
        if (id == null || redirectUrl == null || order == null) {
            return new Result<>(CommonResponse.INVALID_DATA);
        }

        Carousel carousel = carouselDao.selectByPrimaryKey(id);
        if (carousel == null) {
            return new Result<>(CommonResponse.DATA_NOT_EXIST);
        }

        if (image != null) {
            deleteCarouselImage(carousel.getCarouselUrl());
            String imageName = saveCarouselImage(image);
            carousel.setCarouselUrl(imageName);
        }
        carousel.setCarouselRank(order);
        carousel.setRedirectUrl(redirectUrl);
        carousel.setUpdateTime(LocalDateTime.now());
        carousel.setUpdateUser(userId);
        if (carouselDao.updateByPrimaryKeySelective(carousel) > 0) {
            return new Result<>(CommonResponse.SUCCESS);
        } else {
            return new Result<>(CommonResponse.ERROR);
        }
    }

    public Result<?> getCarouselById(Integer id) {
        if (id == null) {
            return new Result<>(CommonResponse.INVALID_DATA);
        }
        Carousel carousel = carouselDao.selectByPrimaryKey(id);
        if (carousel == null) {
            return new Result<>(CommonResponse.DATA_NOT_EXIST);
        } else {
            Result<Carousel> response = new Result<>(CommonResponse.SUCCESS);
            response.setData(carousel);
            return response;
        }
    }

    public Result<?> deleteBatch(Integer[] ids) {
        if (ids.length < 1) {
            return new Result<>(CommonResponse.INVALID_DATA);
        }
        carouselDao.deleteBatch(ids);
        return new Result<>(CommonResponse.SUCCESS);
    }
}