package com.jiezipoi.mall.dao;

import com.jiezipoi.mall.entity.Carousel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CarouselDao {
    int deleteByPrimaryKey(Integer carouselId);

    int insert(Carousel carousel);

    int insertSelective(Carousel carousel);

    Carousel selectByPrimaryKey(Integer carouselId);

    int updateByPrimaryKeySelective(Carousel record);

    int updateByPrimary(Carousel carousel);

    List<Carousel> findCarouselList(@Param("start") Integer start, @Param("limit") Integer limit);

    int getTotalCarousels();

    int deleteBatch(Integer[] ids);

    List<Carousel> findCarouselByNum(@Param("number") int number);
}
