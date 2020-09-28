package com.lhfeiyu.tech.service;

import com.zom.statistics.DTO.RtvUnit;
import com.zom.statistics.dao.mapper.common.GroupMapper;
import com.zom.statistics.dao.mapper.common.RtvUnitMapper;
import com.zom.statistics.dao.mapper.common.UserMapper;
import com.zom.statistics.model.UserCache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 这个类只提供给CacheHandler内部使用，请使用CacheHandler
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, timeout = 30, readOnly = true, rollbackFor = Exception.class)
public class CacheService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private RtvUnitMapper unitMapper;

    /**
     * https://blog.csdn.net/u012198209/article/details/88869735
     * 【踩坑记录】使用ehcache缓存@Cacheable注解不生效的问题
     *
     * @param uid
     * @return
     */
    public String getUsername(long uid) {
        //可以缓存,提升性能
        return userMapper.selectNameById(uid);
    }

    public String getGroupName(long tgid) {
        //可以缓存,提升性能
        return groupMapper.selectNameById(tgid);
    }

    /*public Map<Long, UserCache> getUserList(Set<Long> uidSet) {
        return null;
    }*/

    public UserCache getUserCache(Long uid){
        return userMapper.loadToCache(uid);
    }

    public Map<String, String> getDeptParentUniqueIds(String deptUniqueId) {
        if (StringUtils.isEmpty(deptUniqueId)) {
            return null;
        }
        String deptParentUniqueIds = unitMapper.selectCommonUniqueUp(deptUniqueId);
        List<RtvUnit> deptList = unitMapper.selectByUniqueIn(deptParentUniqueIds);
        Map<String, String> deptMap = new HashMap<>();
        for (RtvUnit dept : deptList) {
            deptMap.put(dept.getUniqueId(), StringUtils.isNotEmpty(dept.getShortName()) ? dept.getShortName() : dept.getName());
        }
        return deptMap;
    }


}
