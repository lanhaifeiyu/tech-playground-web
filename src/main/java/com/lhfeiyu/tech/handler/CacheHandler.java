package com.lhfeiyu.tech.handler;

import com.alibaba.druid.util.StringUtils;
import com.zom.statistics.model.UserCache;
import com.zom.statistics.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CacheHandler {

    @Autowired
    private CacheService cacheService;
    /*@Resource
    private CacheManager cacheManager;*/

    /**
     * https://blog.csdn.net/u012198209/article/details/88869735
     * 【踩坑记录】使用ehcache缓存@Cacheable注解不生效的问题
     *
     * @param uid
     * @return
     */
    @Cacheable(value = "staUser")
    public String getUsername(long uid) {
        //可以缓存,提升性能
        return cacheService.getUsername(uid);
    }

    @Cacheable(value = "staGroup")
    public String getGroupName(long tgid) {
        //可以缓存,提升性能
        return cacheService.getGroupName(tgid);
    }

    /*public Map<Long, UserCache> getUserList(Set<Long> uidSet) {
        return null;
    }*/

    @Cacheable(value = "staUserCache")
    public UserCache getUserCache(Long uid){
        return cacheService.getUserCache(uid);
    }

    @Cacheable(value = "staDeptParentUniqueIds")
    public Map<String, String> getDeptParentUniqueIds(String deptUniqueId) {
        if (StringUtils.isEmpty(deptUniqueId)) {
            return null;
        }
        return cacheService.getDeptParentUniqueIds(deptUniqueId);
    }

}
