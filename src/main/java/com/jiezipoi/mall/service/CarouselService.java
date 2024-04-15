package com.jiezipoi.mall.service;

import com.jiezipoi.mall.config.CarouselConfig;
import com.jiezipoi.mall.dao.CarouselDao;
import com.jiezipoi.mall.entity.Carousel;
import com.jiezipoi.mall.exception.NotFoundException;
import com.jiezipoi.mall.utils.CommonResponse;
import com.jiezipoi.mall.utils.FileNameGenerator;
import com.jiezipoi.mall.utils.Response;
import com.jiezipoi.mall.utils.dataTable.DataTableResult;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    public Response<DataTableResult> getCarouselPage(Integer start, Integer limit, String orderBy) {
        if (start == null || limit == null) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        if (orderBy == null || orderBy.contains(";")) {
            orderBy = "carousel_rank DESC"; //default order by
        }
        List<Carousel> carousels = carouselDao.findCarouselList(start, limit, orderBy);
        int total = carouselDao.getTotalCarousels();
        Response<DataTableResult> response = new Response<>(CommonResponse.SUCCESS);
        response.setData(new DataTableResult(carousels, total));
        return response;
    }

    public void createCarousel(MultipartFile file, String redirectUrl, Integer order, int userId) throws IOException {
        String carouselUrl = saveCarouselImage(file);
        Carousel carousel = new Carousel();
        carousel.setCarouselRank(order);
        carousel.setRedirectUrl(redirectUrl);
        carousel.setCreateUser(userId);
        carousel.setCarouselUrl(carouselUrl);
    }

    private String saveCarouselImage(MultipartFile file) throws IOException {
        String uploadDirString = carouselConfig.getImageDirectory();
        Path uploadDir = Paths.get(uploadDirString);
        String filename = FileNameGenerator.generateFileName() + ".jpg";
        Path imagePath = Paths.get(uploadDirString + filename);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        Files.write(imagePath, file.getBytes());
        return filename;
    }

    private void deleteCarouselImage(String fileName) {
        Path imagePath = Paths.get(carouselConfig.getImageDirectory() + fileName);
        try {
            Files.delete(imagePath);
        } catch (IOException ignored) {
            
        }
    }

    public void updateCarousel(Integer id, MultipartFile image, String redirectUrl, Integer order, Integer userId)
            throws NotFoundException, IOException {
        Carousel carousel = carouselDao.selectByPrimaryKey(id);
        if (carousel == null) {
            throw new NotFoundException();
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
        carouselDao.updateByPrimaryKeySelective(carousel);
    }

    public Response<?> getCarouselById(Integer id) {
        if (id == null) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        Carousel carousel = carouselDao.selectByPrimaryKey(id);
        if (carousel == null) {
            return new Response<>(CommonResponse.DATA_NOT_EXIST);
        } else {
            Response<Carousel> response = new Response<>(CommonResponse.SUCCESS);
            response.setData(carousel);
            return response;
        }
    }

    public Response<?> deleteBatch(Integer[] ids) {
        if (ids.length < 1) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        carouselDao.deleteBatch(ids);
        return new Response<>(CommonResponse.SUCCESS);
    }
}