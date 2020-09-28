package com.lhfeiyu.tech.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 获取原始统计日志数据
 * Logstash将数据保存到Redis，这里定时从Redis中获取原始数据，然后保存到数据库
 *
 * @author airson
 */
@Component
public class StaTestHandler {

    private static RedisTemplate redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private static Logger logger = LoggerFactory.getLogger(StaTestHandler.class);


    public void test_mock_data() {
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

        /**
         * 测试数据：
         *
         * TODO: 在自动任务里面加一个任务，自动添加测试数据，这样方便测试
         *
         *
         * 场景1：业务分类测试，验证数据正确性：
         * 65537：测试登录，登出
         * 65538：SOS相关
         * 65539：语音通话
         * 65540：视频通话
         * 65541：视频回传
         * 65542：视频点名Í
         * 65543：视频会商
         * 65544：照片上传
         * 65545：临时组
         * 65546：IM
         * 65547：里程
         *
         * 场景2：单一用户按时间顺序进行业务操作，验证数据正确性：
         * 65537：
         * 登录-SOS发起-SOS取消-SOS发起-SOS结束-SOS发起-语音通话发起-语音通话结束-语音通话发起-SOS结束-语音通话结束----------退出
         * ÍÍ
         *
         * {"id":1600,"sub_id":0,"uid":65537,"target":0,"type":0,"start_time":"","end_time":"","url":"","flag":0,"session":"","conference_id":"","time":"2020-06-09 17:58:09","handle_uid":0,"im_type":0,"mileage":70}
         * {"id":1600,"sub_id":0,"uid":65537,"target":0,"type":0,"start_time":"","end_time":"","url":"","flag":0,"session":"","conference_id":"","time":"2020-06-09 17:58:09","handle_uid":0,"im_type":0,"mileage":70}
         * {"id":1600,"sub_id":0,"uid":65537,"target":0,"type":0,"start_time":"","end_time":"","url":"","flag":0,"session":"","conference_id":"","time":"2020-06-09 17:58:09","handle_uid":0,"im_type":0,"mileage":70}
         * {"id":1600,"sub_id":8,"uid":65539,"target":0,"type":0,"start_time":"","end_time":"","url":"","flag":0,"session":"","conference_id":"","time":"2020-06-09 17:58:09","handle_uid":0,"im_type":0,"mileage":73}
         * {"id":1600,"sub_id":8,"uid":65538,"target":0,"type":0,"start_time":"","end_time":"","url":"","flag":0,"session":"","conference_id":"","time":"2020-06-09 17:58:09","handle_uid":0,"im_type":0,"mileage":73}
         * {"uid":65538,"id":1000,"real":1,"time":"2020-09-20 09:56:49"}
         * {"uid":65539,"id":1000,"real":1,"time":"2020-09-20 10:56:41"}
         * {"uid":65539,"id":1001,"real":1,"time":"2020-09-20 11:56:41"}
         * {"id":1002,"sub_id":0,"uid":65538,"target":0,"type":0,"start_time":"","end_time":"","url":"","flag":1,"session":"","conference_id":"","time":"2020-09-20 14:23:12","handle_uid":0,"im_type":0,"mileage":0}
         * {"id":1004,"sub_id":0,"uid":65538,"target":0,"type":0,"start_time":"","end_time":"","url":"","flag":3,"session":"","conference_id":"","time":"2020-09-20 15:23:12","handle_uid":65539,"im_type":0,"mileage":0}
         * {"id":1100,"sub_id":0,"uid":65538,"target":65539,"type":1,"start_time":"2020-09-20 09:51:18","end_time":"","url":"","flag":1,"session":"","conference_id":"","time":"","handle_uid":0,"im_type":0,"mileage":0}
         * {"id":1100,"sub_id":0,"uid":65538,"target":65539,"type":1,"start_time":"","end_time":"2020-09-20 10:51:18","url":"","flag":2,"session":"","conference_id":"","time":"","handle_uid":0,"im_type":0,"mileage":0}
         * {"id":1200,"sub_id":1201,"uid":65538,"target":65539,"type":0,"start_time":"2020-09-20 12:13:52","end_time":"","url":"","flag":1,"session":"1415553","conference_id":"","time":"","handle_uid":0,"im_type":0,"mileage":0}
         * {"id":1200,"sub_id":1201,"uid":65538,"target":65539,"type":0,"start_time":"","end_time":"2020-09-20 13:13:52","url":"","flag":2,"session":"1415553","conference_id":"","time":"","handle_uid":0,"im_type":0,"mileage":0}
         * {"uid":65538,"flag":2,"sub_id":1203,"session":1591779987000,"end_time":"2020-09-20 17:08:26","id":1200}
         * {"uid":65539,"id":1300,"time":"2020-09-20 14:45:59"}
         * {"uid":65539,"name":"临时组","id":1400,"time":"2020-09-20 16:07:12"}
         * {"uid":65538,"im_type":3,"id":1500,"time":"2020-09-20 12:00:08","type":1,"target":65537}
         * {"uid":65537,"im_type":3,"id":1500,"time":"2020-09-20 12:02:20","type":2,"target":99869}
         * {"id":1600,"sub_id":0,"uid":65540,"target":0,"type":0,"start_time":"","end_time":"","url":"","flag":0,"session":"","conference_id":"","time":"2020-09-20 17:58:09","handle_uid":0,"im_type":0,"mileage":70}
         * {"id":1200,"sub_id":1202,"uid":65538,"target":0,"type":0,"start_time":"2020-09-20 15:13:52","end_time":"","url":"","flag":1,"session":"1415551","conference_id":"","time":"","handle_uid":0,"im_type":0,"mileage":0}
         * {"id":1200,"sub_id":1203,"uid":65538,"target":0,"type":0,"start_time":"2020-09-20 16:13:52","end_time":"","url":"","flag":1,"session":"1415552","conference_id":"","time":"","handle_uid":0,"im_type":0,"mileage":0}
         * {"id":1200,"sub_id":1204,"uid":65538,"target":0,"type":0,"start_time":"2020-09-20 17:13:52","end_time":"","url":"","flag":1,"session":"1415554","conference_id":"","time":"","handle_uid":0,"im_type":0,"mileage":0}
         * {"uid":65538,"id":1000,"time":"2020-09-20 18:56:49"}
         * {"uid":65538,"id":1001,"time":"2020-09-20 18:56:49"}
         * {"uid":65538,"id":1000,"time":"2020-09-20 19:56:49"}
         * {"uid":65538,"id":1001,"time":"2020-09-20 19:57:49"}
         * {"id":1200,"sub_id":1202,"uid":65538,"target":0,"type":0,"start_time":"","end_time":"2020-09-20 15:13:52","url":"","flag":2,"session":"1415551","conference_id":"","time":"","handle_uid":0,"im_type":0,"mileage":0}
         * {"id":1200,"sub_id":1203,"uid":65538,"target":0,"type":0,"start_time":"","end_time":"2020-09-20 16:13:52","url":"","flag":2,"session":"1415552","conference_id":"","time":"","handle_uid":0,"im_type":0,"mileage":0}
         * {"id":1200,"sub_id":1204,"uid":65538,"target":0,"type":0,"start_time":"","end_time":"2020-09-20 17:13:52","url":"","flag":2,"session":"1415554","conference_id":"","time":"","handle_uid":0,"im_type":0,"mileage":0}
         * {"id":1600,"sub_id":8,"uid":65538,"target":0,"type":0,"start_time":"","end_time":"","url":"","flag":0,"session":"","conference_id":"","time":"2020-06-09 17:58:09","handle_uid":0,"im_type":0,"mileage":73}
         */

        // TODO mock data

        // 可以只执行一次任务，然后任务一直循环新增，这样能相对平滑的产生测试数据

        String content = "";
        //redisTemplate.opsForList().rightPush(StaConst.REDIS_STA_LIST, content);
    }

}
