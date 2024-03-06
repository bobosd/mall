package com.jiezipoi.mall.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jiezipoi.mall.dao.GoodsCategoryDao;
import com.jiezipoi.mall.entity.GoodsCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class MyTest {

    @Autowired
    private GoodsCategoryDao goodsCategoryDao;

    @Test
    @Transactional()
    public void test() throws JsonProcessingException {
        GoodsCategory category = new GoodsCategory();
        category.setCategoryLevel((byte) 1);
        category.setCategoryName("test");
        category.setCategoryRank(1);
        System.out.println(category);
        long id = goodsCategoryDao.insertSelective(category);
        category.setId(id);
        category.setPath("0.0");
    }
}
