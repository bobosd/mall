package com.jiezipoi.mall.service;

import com.jiezipoi.mall.config.CarouselConfig;
import com.jiezipoi.mall.dao.CarouselDao;
import com.jiezipoi.mall.dto.CarouselDTO;
import com.jiezipoi.mall.entity.Carousel;
import com.jiezipoi.mall.exception.NotFoundException;
import com.jiezipoi.mall.utils.FileNameGenerator;
import com.jiezipoi.mall.utils.dataTable.DataTableResult;
import com.jiezipoi.mall.utils.dataTable.Order;
import com.jiezipoi.mall.utils.dataTable.request.DataTableRequest;
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

    public List<Carousel> getCarouselList() {
        return carouselDao.findAllCarousel();
    }

    public DataTableResult getCarouselPage(DataTableRequest request) {
        Integer start = request.getStart();
        Integer limit = request.getLength();
        Order order = request.getOrder().get(0);
        Integer colNumber = order.getColumn();
        String dir = order.getDir();
        List<CarouselDTO> carousels = carouselDao.findCarouselList(start, limit, colNumber, dir);
        int total = carouselDao.getTotalCarousels();
        return new DataTableResult(carousels, total);
    }

    public void createCarousel(MultipartFile file, Long goodsId, Integer order, long userId) throws IOException {
        String carouselUrl = saveCarouselImage(file);
        Carousel carousel = new Carousel();
        carousel.setCarouselRank(order);
        carousel.setCreateUser(userId);
        carousel.setCarouselUrl(carouselUrl);
        carousel.setGoodsId(goodsId);
        carouselDao.insertSelective(carousel);
    }

    private String saveCarouselImage(MultipartFile file) throws IOException {
        Path uploadDir = carouselConfig.getImageDirectory();
        String filename = FileNameGenerator.generateFileName() + ".jpg";
        Path imagePath = uploadDir.resolve(filename);
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

    public void updateCarousel(Long carouselId, MultipartFile image, Long goodsId, Integer order, Long userId)
            throws NotFoundException, IOException {
        Carousel carousel = carouselDao.selectByPrimaryKey(carouselId);
        if (carousel == null) {
            throw new NotFoundException();
        }
        if (image != null) {
            deleteCarouselImage(carousel.getCarouselUrl());
            String imageName = saveCarouselImage(image);
            carousel.setCarouselUrl(imageName);
        }
        carousel.setCarouselRank(order);
        carousel.setGoodsId(goodsId);
        carousel.setUpdateTime(LocalDateTime.now());
        carousel.setUpdateUser(userId);
        carouselDao.updateByPrimaryKeySelective(carousel);
    }

    public Carousel getCarouselById(Long carouselId) throws NotFoundException{
        Carousel carousel = carouselDao.selectByPrimaryKey(carouselId);
        if (carousel == null) {
            throw new NotFoundException();
        }
        return carousel;
    }

    public void removeCarousel(Long id) {
        carouselDao.deleteBatch(id);
    }
}