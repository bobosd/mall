package com.jiezipoi.mall.controller.vo;

import com.jiezipoi.mall.entity.Goods;

import java.math.BigDecimal;

public class IndexConfigGoodsVO {
    private Long goodsId;
    private String goodsName;
    private String goodsIntro;
    private String goodsCoverImg;
    private BigDecimal sellingPrice;
    private String tag;

    public IndexConfigGoodsVO() {

    }

    public IndexConfigGoodsVO(Goods goods) {
        this.goodsId = goods.getId();
        this.goodsName = goods.getGoodsName();
        this.goodsIntro = goods.getGoodsIntro();
        this.goodsCoverImg = goods.getGoodsCoverImg();
        this.sellingPrice = goods.getSellingPrice();
        this.tag = goods.getTag();
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsIntro() {
        return goodsIntro;
    }

    public void setGoodsIntro(String goodsIntro) {
        this.goodsIntro = goodsIntro;
    }

    public String getGoodsCoverImg() {
        return goodsCoverImg;
    }

    public void setGoodsCoverImg(String goodsCoverImg) {
        this.goodsCoverImg = goodsCoverImg;
    }

    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
