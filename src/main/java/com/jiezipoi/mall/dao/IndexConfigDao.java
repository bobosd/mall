package com.jiezipoi.mall.dao;

import com.jiezipoi.mall.dto.IndexConfigDTO;
import com.jiezipoi.mall.entity.IndexConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IndexConfigDao {
    int deleteByPrimaryKey(Long configId);

    int insert(IndexConfig record);

    int insertSelective(IndexConfig record);

    IndexConfig selectByPrimaryKey(Long configId);

    int updateByPrimaryKeySelective(IndexConfig record);

    int updateByPrimaryKey(IndexConfig record);

    List<IndexConfigDTO> listIndexConfig(@Param("start") int start,
                                         @Param("limit") int limit,
                                         @Param("config_type") int configType);

    int getTotalIndexConfigs(@Param("config_type") int configType);

    int deleteBatch(long[] ids);

    List<IndexConfig> findByType(@Param("configType") int configType);
}