package com.lhfeiyu.tech.handler;

import com.alibaba.druid.util.StringUtils;
import com.lhfeiyu.tech.controller.WebSocketStompController;
import com.lhfeiyu.tech.domain.StaConst;
import com.lhfeiyu.tech.service.StaLog1SourceService;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 获取原始统计日志数据
 * Logstash将数据保存到Redis，这里定时从Redis中获取原始数据，然后保存到数据库
 *
 * @author airson
 */
@Component
public class StaLog1SourceHandler {

    @Autowired
    private WebSocketStompController webSocketStompController;
    @Autowired
    private StaLog1SourceService staLog1SourceService;

    private static RedisTemplate redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private static Logger logger = LoggerFactory.getLogger(StaLog1SourceHandler.class);

    public void insertOriginalLog() {
        List<String> dataList = getOriginalLog();
        if (null == dataList || dataList.size() <= 0) {
            return;
        }
        try {
            webSocketStompController.trigger_notice(dataList);
        } catch (Exception e) {
            logger.warn("ws trigger_notice error:{}", e.getMessage());
            e.printStackTrace();
        }
        // insert origin data
        insertBatch(dataList);
    }

    private List<String> getOriginalLog() {
        //logger.info("getStaDataSource:{} ", key);
        //List<String> dataList = new ArrayList<>();
        /*dataList.add("{\"id\":1600,\"sub_id\":1,\"uid\":83656,\"target\":0,\"type\":0,\"start_time\":\"\",\"end_time\":\"\",\"url\":\"\",\"flag\":0,\"session\":\"\",\"conference_id\":\"\",\"time\":\"2020-06-09 17:58:09\",\"handle_uid\":0,\"im_type\":0,\"mileage\":70}");
        dataList.add("{\"id\":1600,\"sub_id\":2,\"uid\":83656,\"target\":0,\"type\":0,\"start_time\":\"\",\"end_time\":\"\",\"url\":\"\",\"flag\":0,\"session\":\"\",\"conference_id\":\"\",\"time\":\"2020-06-09 17:58:09\",\"handle_uid\":0,\"im_type\":0,\"mileage\":70}");
        dataList.add("{\"id\":1600,\"sub_id\":3,\"uid\":83656,\"target\":0,\"type\":0,\"start_time\":\"\",\"end_time\":\"\",\"url\":\"\",\"flag\":0,\"session\":\"\",\"conference_id\":\"\",\"time\":\"2020-06-09 17:58:09\",\"handle_uid\":0,\"im_type\":0,\"mileage\":70}");
        dataList.add("{\"id\":1600,\"sub_id\":4,\"uid\":83656,\"target\":0,\"type\":0,\"start_time\":\"\",\"end_time\":\"\",\"url\":\"\",\"flag\":0,\"session\":\"\",\"conference_id\":\"\",\"time\":\"2020-06-09 17:58:09\",\"handle_uid\":0,\"im_type\":0,\"mileage\":70}");
        dataList.add("{\"id\":1600,\"sub_id\":5,\"uid\":83656,\"target\":0,\"type\":0,\"start_time\":\"\",\"end_time\":\"\",\"url\":\"\",\"flag\":0,\"session\":\"\",\"conference_id\":\"\",\"time\":\"2020-06-09 17:58:09\",\"handle_uid\":0,\"im_type\":0,\"mileage\":70}");
        dataList.add("{\"id\":1600,\"sub_id\":6,\"uid\":83656,\"target\":0,\"type\":0,\"start_time\":\"\",\"end_time\":\"\",\"url\":\"\",\"flag\":0,\"session\":\"\",\"conference_id\":\"\",\"time\":\"2020-06-09 17:58:09\",\"handle_uid\":0,\"im_type\":0,\"mileage\":70}");
        dataList.add("{\"id\":1600,\"sub_id\":7,\"uid\":83656,\"target\":0,\"type\":0,\"start_time\":\"\",\"end_time\":\"\",\"url\":\"\",\"flag\":0,\"session\":\"\",\"conference_id\":\"\",\"time\":\"2020-06-09 17:58:09\",\"handle_uid\":0,\"im_type\":0,\"mileage\":70}");
        dataList.add("{\"id\":1600,\"sub_id\":8,\"uid\":83656,\"target\":0,\"type\":0,\"start_time\":\"\",\"end_time\":\"\",\"url\":\"\",\"flag\":0,\"session\":\"\",\"conference_id\":\"\",\"time\":\"2020-06-09 17:58:09\",\"handle_uid\":0,\"im_type\":0,\"mileage\":70}");
        */

        boolean sta_contains = redisTemplate.hasKey(StaConst.REDIS_STA_LIST);
        if (!sta_contains) {
            // logstash在往redis添加数据时会自动创建集合，所以这里不用创建一个空集合
            // 数据被消费完后，列表为空时，Redis会自动删除Key，所以Key不存在是常见场景
            logger.debug("source redis empty1: {}", StaConst.REDIS_STA_LIST);
            return null;
        }
        Long sta_count = redisTemplate.opsForList().size(StaConst.REDIS_STA_LIST);
        if (null == sta_count || sta_count == 0) {
            logger.debug("source redis empty2"); // there is no new data on this turn
            return null;
        }

        // XXX Redis中的原始统计数据最长保留7天，每次获取数据时刷新过期时间，logstash只能由congestion_threshold来判断最大容量后阻塞
        redisTemplate.expire(StaConst.REDIS_STA_LIST, 7, TimeUnit.DAYS);

        if (sta_count > 10000) {
            logger.warn("source redis cur count is large:{}", sta_count);
        } else {
            logger.debug("source redis cur count:{}", sta_count);
        }

        int max_count = StaConst.REDIS_SINGLE_MAX;
        if (sta_count < StaConst.REDIS_SINGLE_MAX) {
            max_count = sta_count.intValue();
        }
        List<String> dataList = redisTemplate.opsForList().range(StaConst.REDIS_STA_LIST, 0, (max_count - 1));
        int real_count = dataList.size();
        if (real_count == 0) {
            logger.debug("source empty,start_index:{},max_index:{}", 0, (max_count - 1));
            return null;
        }
        logger.debug("source get max:{}, real:{}", max_count, real_count);
        return dataList;
    }

    private int insertBatch(List<String> dataList) {
        if (null == dataList || dataList.size() <= 0) {
            return 0;
        }

        // logstash产生的数据有很多内容，原始数据只是其中一部分，这里需要把原始数据截取出来，原始数据： ``%{DATA:content_json}``
        List<String> originList = new ArrayList<>(dataList.size());
        for (String msg : dataList) {
            if (StringUtils.isEmpty(msg)) {
                logger.warn("origin data empty ignore");
                continue;
            }
            int start_index = msg.indexOf("``{");
            int end_index = msg.indexOf("}``");
            if (start_index < 0 || end_index < 0) {
                logger.warn("invalid origin data ignore:{}", msg);
                continue;
            }

            //这里只修改msg是不会修改到dataList里面的内容的，String是不可变对象，循环时已经是新建的一个对象
            // ``{\"id\":1600,\"sub_id\":0,\"uid\":65551,\"target\":0,\"type\":0,\"start_time\":\"\",\"end_time\":\"\",\"url\":\"\",\"flag\":0,\"session\":\"\",\"conference_id\":\"\",\"time\":\"2020-09-22 10:34:46\",\"handle_uid\":0,\"im_type\":0,\"mileage\":13}``
            String origin = msg.substring(start_index + 2, end_index + 1);
            //在json反序列化时存在转义字符，先去除转义字符
            origin = StringEscapeUtils.unescapeJava(origin);
            // 需要判断一下数据是不是以{号开头(是否是JSON格式)，如果不是就过滤掉无效数据，也可以这里不检查，在解析的时候再检查
            if (StringUtils.isEmpty(origin) || !origin.startsWith("{")) {
                logger.warn("invalid json data ignore:{}", origin);
                continue;
            }
            originList.add(origin);
        }
        dataList = originList;

        int real_count = dataList.size();
        if (real_count <= StaConst.MYSQL_SINGLE_PAGE) {
            logger.debug("source insert_single:{}", real_count);
            int count = staLog1SourceService.insertOriginalLog(dataList);
            removeFromRedis(real_count);
            return count;
        }

        logger.debug("source insert:{},by page:{}", real_count, StaConst.MYSQL_SINGLE_PAGE);
        // 如果数据超过100条，就切分为每100条数据新增一次，每页条数由 MYSQL_SINGLE_PAGE 指定。
        List<String> bucket;
        int real_max_index = real_count - 1;
        int consume_index = 0;
        while (consume_index <= real_max_index) {
            bucket = new ArrayList<>(StaConst.MYSQL_SINGLE_PAGE);
            int end_index = consume_index + (StaConst.MYSQL_SINGLE_PAGE - 1);
            if (end_index > real_max_index) {
                end_index = real_max_index;
            }
            for (int i = consume_index; i <= end_index; i++) {
                bucket.add(dataList.get(i));
            }
            staLog1SourceService.insertOriginalLog(bucket);
            removeFromRedis(bucket.size());
            consume_index = end_index + 1;
        }
        return real_count;
    }

    /**
     * redis更新操作不放到Service执行，是为了保证数据库事务成功提交后再更新Redis，但是更新Redis也是有可能失败的，这目前没做处理。
     * 如果Redis一开始就不可用，那在获取数据时就会报错，这种场景数据不会重复新增也不会漏，
     * 如果从Redis获取了数据，在删除数据的时候突然不可用了，这时数据库已经新增了数据，Redis没成功删除，
     * 那在下次获取数据时就会造成有重复数据新增到DB，这是一个限制暂未解决。
     *
     * @param real_count
     */
    private void removeFromRedis(int real_count) {
        //int redis_index = real_count - 1;
        //start参数直接用real_count而不用real_index是因为，这里是指需要保留的数据的起始索引值
        redisTemplate.opsForList().trim(StaConst.REDIS_STA_LIST, real_count, -1);
        logger.debug("redis trim end");
    }

}
