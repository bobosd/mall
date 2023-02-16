package com.jiezipoi.mall.service;

import com.jiezipoi.mall.dao.CarouselDao;
import com.jiezipoi.mall.entity.Carousel;
import com.jiezipoi.mall.utils.CommonResponse;
import com.jiezipoi.mall.utils.PageResult;
import com.jiezipoi.mall.utils.Result;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CarouselService {
    @Resource
    private CarouselDao carouselDao;

    private final MessageSource i18n;

    public CarouselService(MessageSource i18n) {
        this.i18n = i18n;
    }

    public Result<PageResult> getCarouselPage(Integer start, Integer limit) {
        if (start == null || limit == null) {
            return new Result<>(CommonResponse.INVALID_DATA);
        }
        List<Carousel> carousels = carouselDao.findCarouselList(start, limit);
        int total = carouselDao.getTotalCarousels();

        Result<PageResult> response = new Result<>(CommonResponse.SUCCESS);
        response.setData(new PageResult(carousels, total, limit, start));
        return response;
    }

    public Result<?> saveCarousel(Carousel carousel) {
        if (StringUtils.hasLength(carousel.getCarouselUrl()) || carousel.getCarouselRank() == null) {
            return new Result<>(CommonResponse.INVALID_DATA);
        }

        if (carouselDao.insertSelective(carousel) > 0) {
            return new Result<>(CommonResponse.SUCCESS);
        } else {
            return new Result<>(CommonResponse.ERROR);
        }
    }

    public Result<?> updateCarousel(Carousel carousel) {
        if (carousel.getCarouselId() == null ||
                carousel.getCarouselUrl() == null ||
                carousel.getCarouselRank() == null) {
            return new Result<>(CommonResponse.INVALID_DATA);
        }
        Carousel temp = carouselDao.selectByPrimaryKey(carousel.getCarouselId());
        if (temp == null) {
            return new Result<>(CommonResponse.DATA_NOT_EXIST);
        }
        temp.setCarouselRank(carousel.getCarouselRank());
        temp.setRedirectUrl(carousel.getRedirectUrl());
        temp.setCarouselUrl(carousel.getCarouselUrl());
        temp.setUpdateTime(carousel.getUpdateTime());
        if (carouselDao.updateByPrimaryKeySelective(temp) > 0) {
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