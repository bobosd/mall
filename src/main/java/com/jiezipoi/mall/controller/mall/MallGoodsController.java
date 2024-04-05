package com.jiezipoi.mall.controller.mall;

import com.jiezipoi.mall.config.GoodsConfig;
import com.jiezipoi.mall.dto.GoodsSearchBrandsDTO;
import com.jiezipoi.mall.dto.GoodsSearchCategoryDTO;
import com.jiezipoi.mall.dto.GoodsSearchDTO;
import com.jiezipoi.mall.entity.Goods;
import com.jiezipoi.mall.service.GoodsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Controller
public class MallGoodsController {
    private final GoodsService goodsService;
    private final GoodsConfig goodsConfig;

    public MallGoodsController(GoodsService goodsService, GoodsConfig goodsConfig) {
        this.goodsService = goodsService;
        this.goodsConfig = goodsConfig;
    }

    @GetMapping("/search")
    public String searchPage(ModelMap modelMap,
                             @RequestParam("keyword") String keyword,
                             @RequestParam(value = "brand", required = false) Long[] requiredBrandIds,
                             @RequestParam(value = "category", required = false) Long[] requiredCategoryIds,
                             @RequestParam(value = "priceFrom", required = false) Integer priceFrom,
                             @RequestParam(value = "priceTo", required = false) Integer priceTo) {
        List<GoodsSearchDTO> goodsList = goodsService.getGoodsListByKeyword(keyword);
        processSearchData(modelMap, goodsList, requiredBrandIds, requiredCategoryIds, priceFrom, priceTo);
        return "mall/goods-search";
    }

    private void processSearchData(ModelMap modelMap,
                                   List<GoodsSearchDTO> goodsList,
                                   Long[] requiredBrandIds,
                                   Long[] requiredCategoryIds,
                                   Integer priceFrom,
                                   Integer priceTo) {

        HashSet<GoodsSearchBrandsDTO> brandSet = new HashSet<>();
        HashSet<GoodsSearchCategoryDTO> categorySet = new HashSet<>();
        HashSet<Long> requiredBrandsIdSet =
                requiredBrandIds == null ? new HashSet<>() : new HashSet<>(Arrays.stream(requiredBrandIds).toList());
        HashSet<Long> requiredCategoryIdSet =
                requiredCategoryIds == null ? new HashSet<>() : new HashSet<>(Arrays.stream(requiredCategoryIds).toList());
        AtomicReference<BigDecimal> minPrice = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> maxPrice = new AtomicReference<>(BigDecimal.ZERO);

        //initialize max and min value
        if (goodsList.size() > 0) {
            GoodsSearchDTO firstGoods = goodsList.get(0);
            minPrice.set(firstGoods.getSellingPrice());
            maxPrice.set(firstGoods.getSellingPrice());
        }

        //filtering & generate filter options
        List<GoodsSearchDTO> filteredList = goodsList.stream().filter((goods -> {
            GoodsSearchBrandsDTO brand = goods.getGoodsBrand();
            GoodsSearchCategoryDTO category = goods.getGoodsCategory();
            BigDecimal price = goods.getSellingPrice();
            brandSet.add(brand);
            categorySet.add(category);
            if (price.compareTo(minPrice.get()) < 0) {
                minPrice.set(price);
            }
            if (price.compareTo(maxPrice.get()) > 0) {
                maxPrice.set(price);
            }
            return doGoodsSearchFilter(goods, requiredBrandsIdSet, requiredCategoryIdSet, priceFrom, priceTo);
        })).toList();

        //let frontend know which option need to be high-lighted
        setHighLightFlagToGoodsBrands(brandSet, requiredBrandsIdSet);
        setHighLightFlagToGoodsCategories(categorySet, requiredCategoryIdSet);
        modelMap.put("goodsList", filteredList);
        modelMap.put("brands", brandSet);
        modelMap.put("categories", categorySet);
        modelMap.put("priceFrom", priceFrom == null ? minPrice.get().intValue() : priceFrom);
        modelMap.put("priceTo", priceTo == null ? maxPrice.get().intValue() : priceTo);
    }

    private boolean doGoodsSearchFilter(GoodsSearchDTO goods,
                                        HashSet<Long> requiredBrandIdSet,
                                        HashSet<Long> requiredCategoryIdSet,
                                        Integer priceFrom,
                                        Integer priceTo) {
        if (!requiredBrandIdSet.isEmpty() &&
                !requiredBrandIdSet.contains(goods.getGoodsBrand().getGoodsBrandId())) {
            return false;
        }

        if (!requiredCategoryIdSet.isEmpty() && 
                !requiredCategoryIdSet.contains(goods.getGoodsCategory().getGoodsCategoryId())) {
            return false;
        }
        
        if (priceFrom != null && priceTo != null) {
            int sellingPrice = goods.getSellingPrice().intValue();
            if (sellingPrice < priceFrom || sellingPrice > priceTo) {
                return false;
            }
        }
        return true;
    }

    private void setHighLightFlagToGoodsCategories(HashSet<GoodsSearchCategoryDTO> set, HashSet<Long> idSet) {
        set.forEach(category -> {
            if (idSet.contains(category.getGoodsCategoryId())) {
                category.setNeedHighLight(true);
            }
        });
    }

    private void setHighLightFlagToGoodsBrands(HashSet<GoodsSearchBrandsDTO> set, HashSet<Long> idSet) {
        set.forEach(goodsBrand -> {
            if (idSet.contains(goodsBrand.getGoodsBrandId())) {
                goodsBrand.setNeedHighLight(true);
            }
        });
    }

    @GetMapping("/category/{categoryId}")
    public String categoryPage(ModelMap modelMap,
                               @PathVariable Long categoryId,
                               @RequestParam(value = "brand", required = false) Long[] requiredBrandIds,
                               @RequestParam(value = "category", required = false) Long[] requiredCategoryIds,
                               @RequestParam(value = "priceFrom", required = false) Integer priceFrom,
                               @RequestParam(value = "priceTo", required = false) Integer priceTo) {
        List<GoodsSearchDTO> list = goodsService.getGoodsListByCategory(categoryId);
        processSearchData(modelMap, list, requiredBrandIds, requiredCategoryIds, priceFrom, priceTo);
        return "mall/goods-category";
    }

    @GetMapping("/goods/detail/{goodsId}")
    public String goodsDetailPage(ModelMap modelMap, @PathVariable Long goodsId) {
        try {
            Goods goods = goodsService.getGoodsById(goodsId);
            modelMap.put("goods", goods);
            modelMap.put("currencySymbol", goodsConfig.getCurrencySymbol());
            return "mall/goods-detail";
        } catch (NullPointerException e) {
            return "mall/fallback";
        }
    }
}