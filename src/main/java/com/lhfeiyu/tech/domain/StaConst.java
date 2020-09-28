package com.lhfeiyu.tech.domain;

import java.time.ZoneId;

/**
 *
 * @author airson
 */
public class StaConst {

    public StaConst() {
        // forbid new instance
    }

    // sta table names
    public static final String sta_original_log = "sta_original_log";
    public static final String sta_user_audio_record = "sta_user_audio_record";
    public static final String sta_user_audio_record_origin = "sta_user_audio_record_origin";
    public static final String sta_user_im_record = "sta_user_im_record";
    public static final String sta_user_logon_record_origin = "sta_user_logon_record_origin";
    public static final String sta_user_mileage_record = "sta_user_mileage_record";
    public static final String sta_user_online_record = "sta_user_online_record";
    public static final String sta_user_photo_record = "sta_user_photo_record";
    public static final String sta_user_sos_record = "sta_user_sos_record";
    public static final String sta_user_sos_record_origin = "sta_user_sos_record_origin";
    public static final String sta_user_tmpgroup_record = "sta_user_tmpgroup_record";
    public static final String sta_user_video_record = "sta_user_video_record";
    public static final String sta_user_video_record_origin = "sta_user_video_record_origin";

    // sta source
    public static final String REDIS_STA_LIST = "sta-origin-list";
    public static final int REDIS_SINGLE_MAX = 300;// 每轮从Redis中读取数据的最大条数 8 FOR TEST
    public static final int MYSQL_SINGLE_PAGE = 50;// 每次批量新增到DB的最大条数 2 FOR TEST

    // sta parse
    public static final int PARSE_SINGLE_PAGE_COUNT = 300;// 每轮解析的条数 10 FOR TEST

    // sta pair
    public static final int DURATION_MAX_SEC = 864000; // 时长的最大期限为10天，超过就视为无效 1小时3600秒 1天-86400 10天-864000 20天-1728000

    // sta sum
    public static final int SUM_SINGLE_PAGE_COUNT = 100;// 每轮解析的条数 10 FOR TEST

    // common
    public static final String DF_SECOND = "yyyy-MM-dd HH:mm:ss";
    public static final String DF_DAY = "yyyy-MM-dd";
    public static final String DF_DAY_NUM = "yyyyMMdd";
    public static final ZoneId TIME_ZONE_ID = ZoneId.systemDefault();


    // 记录未结束的持续业务的缓存更新时间，定期更新缓存
    public static long sustainingTs = 0l;

}
