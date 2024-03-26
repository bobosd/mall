package com.jiezipoi.mall.service;

import com.jiezipoi.mall.dao.GoodsBrandDao;
import com.jiezipoi.mall.entity.GoodsBrand;
import com.jiezipoi.mall.exception.ForeignKeyConstraintException;
import com.jiezipoi.mall.utils.dataTable.request.DataTableRequest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class GoodsBrandService {
    private final GoodsBrandDao goodsBrandDao;

    public GoodsBrandService(GoodsBrandDao goodsBrandDao) {
        this.goodsBrandDao = goodsBrandDao;
    }

    public GoodsBrand getGoodsBrandById(long id) {
        return goodsBrandDao.selectGoodsBrandById(id);
    }

    public List<GoodsBrand> getGoodsBrandList(DataTableRequest request) {
        if (request == null || request.getStart() == null || request.getLength() == null) {
            throw new IllegalArgumentException();
        }
        String search = request.getSearch().getValue() + "%";
        return goodsBrandDao.findAllGoodsBrand(request.getStart(), request.getLength(), search);
    }

    public List<GoodsBrand> getGoodsBrandList(String search) {
        return goodsBrandDao.findByGoodsNameContaining(search);
    }

    public int getTotalBrandCount() {
        return goodsBrandDao.selectGoodsBrandCount();
    }

    public void createGoodsBrand(String brandName) {
        GoodsBrand temp = goodsBrandDao.selectGoodsBrandByName(brandName);
        if (temp != null) {
            throw new DuplicateKeyException("Duplicated row '" + brandName + "'");
        }
        goodsBrandDao.insertGoodsBrand(brandName);
    }

    public void updateGoodsBrand(long id, String brandName) {
        GoodsBrand temp = goodsBrandDao.selectGoodsBrandByName(brandName);
        if (temp != null) {
            throw new DuplicateKeyException("Duplicated row '" + brandName + "'");
        }
        int rowAffected = goodsBrandDao.updateGoodsBrandById(id, brandName);
        if (rowAffected < 1) {
            throw new IllegalArgumentException();
        }
    }

    public void deleteGoodsBrand(long id) throws ForeignKeyConstraintException {
        int associatedGoodsCount = goodsBrandDao.countAssociatedGoodsByBrandId(id);
        System.out.println(associatedGoodsCount);
        if (associatedGoodsCount > 0) {
            throw new ForeignKeyConstraintException();
        }
        int deletedRowCount = goodsBrandDao.deleteGoodsBrandById(id);
        if (deletedRowCount < 1) {
            throw new NoSuchElementException();
        }
    }
}
