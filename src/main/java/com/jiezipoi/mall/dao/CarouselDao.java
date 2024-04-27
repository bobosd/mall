package com.jiezipoi.mall.dao;

import com.jiezipoi.mall.dto.CarouselDTO;
import com.jiezipoi.mall.entity.Carousel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CarouselDao {
    int deleteByPrimaryKey(Integer carouselId);

    int insert(Carousel carousel);

    int insertSelective(Carousel carousel);

    Carousel selectByPrimaryKey(Long carouselId);

    int updateByPrimaryKeySelective(Carousel record);

    int updateByPrimary(Carousel carousel);

    List<CarouselDTO> findCarouselList(@Param("start") Integer start,
                                       @Param("limit") Integer limit,
                                       @Param("colNumber") Integer orderBy,
                                       @Param("dir") String dir);

    int getTotalCarousels();

    int deleteBatch(Long id);

    List<Carousel> findCarouselByNum(@Param("number") int number);

    List<Carousel> findAllCarousel();
}
