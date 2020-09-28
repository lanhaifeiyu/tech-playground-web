package com.lhfeiyu.tech.dao.mapper.common;

import com.zom.statistics.DTO.RtvUnit;
import com.zom.statistics.DTO.UnitSearchDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RtvUnitMapper {

    // -- old below --

    List<RtvUnit> selectByUniqueIn(@Param("uniqueIdIn") String uniquIdIn);

    String selectCommonUniqueUp(@Param("uniqueId") String uniqueId);

    String selectNameByUniqueId(@Param("uniqueId") String uniqueId);

    String selectCommonUniqueDown(@Param("uniqueId") String uniqueId);

    List<String> selectUniqueIdByParentId(@Param("parentId") String parentId);

    List<RtvUnit> selectNameByUniqueIn(@Param("uniqueIdIn") String uniquIdIn);

    List<String> selectUniqueIdByCorpId(@Param("corpId") int corpId);

    List<RtvUnit> selectLike(UnitSearchDto unitSearchDto);
}
