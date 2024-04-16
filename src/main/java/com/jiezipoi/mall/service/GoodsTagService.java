package com.jiezipoi.mall.service;

import com.jiezipoi.mall.dao.GoodsTagDao;
import com.jiezipoi.mall.entity.GoodsTag;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GoodsTagService {
    private final GoodsTagDao goodsTagDao;

    public GoodsTagService(GoodsTagDao goodsTagDao) {
        this.goodsTagDao = goodsTagDao;
    }

    /**
     * 传递无ID的GoodsTag对象集合，该方法将对集合内所有的对象赋予goodsTagId值
     * 工作方式是：如果存在该tag_name则直接使用select返回的id值，如果不存在则创建并赋予ID值
     * @param tagsToCreate Id为null的GoodsTag对象集合
     * @return 返回已经赋予完ID值的GoodsTag集合
     */
    public List<GoodsTag> getOrCreateGoodsTagByTagName(List<GoodsTag> tagsToCreate) {
        if (tagsToCreate == null || tagsToCreate.isEmpty()) {
            return new ArrayList<>();
        }
        List<GoodsTag> existingTag = getGoodsTagByName(tagsToCreate);
        tagsToCreate.removeAll(existingTag);
        if (!tagsToCreate.isEmpty()) {
            goodsTagDao.insertBatch(tagsToCreate);
        }
        tagsToCreate.addAll(existingTag);
        return tagsToCreate;
    }

    public void assignTagToGoodsInDB(long goodsId, List<GoodsTag> goodsTags) {
        goodsTagDao.insertGoodsHasTag(goodsId, goodsTags);
    }

    public List<GoodsTag> getGoodsTagByName(List<GoodsTag> tagNameList) {
        String[] tagNameArr = tagNameList.stream()
                .map(GoodsTag::getTagName)
                .toArray(String[]::new);
        return getGoodsTagByName(tagNameArr);
    }

    public List<GoodsTag> getGoodsTagByName(String... tagName) {
        return goodsTagDao.selectGoodsTagByName(tagName);
    }

    public List<GoodsTag> getRecordedGoodsHasTag(Long goodsId) {
        return goodsTagDao.selectGoodsHasTagByGoodsId(goodsId);
    }

    public void updateGoodsHasTag(Long goodsId, List<GoodsTag> tagsToLink) {
        List<GoodsTag> tagsToUnlink = getRecordedGoodsHasTag(goodsId);
        List<GoodsTag> retainList = new ArrayList<>(tagsToLink);
        retainList.retainAll(tagsToUnlink); //重复的元素
        tagsToUnlink.removeAll(retainList);//需要删除的关联
        tagsToLink.removeAll(retainList);//需要新增的关联
        if (!tagsToUnlink.isEmpty()) {
            goodsTagDao.deleteGoodsHasTagByGoodsIdAndTagId(goodsId, tagsToUnlink);
        }
        if (!tagsToLink.isEmpty()) {
            getOrCreateGoodsTagByTagName(tagsToLink);
            assignTagToGoodsInDB(goodsId, tagsToLink);
        }
    }
}
