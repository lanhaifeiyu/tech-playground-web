package com.lhfeiyu.tech.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 *
 * @author airson
 */
public class StaTools {

    public StaTools() {
        // forbid new instance
    }

    private static Logger logger = LoggerFactory.getLogger(StaTools.class);

    /**
     * @param endTime
     * @param startTime
     * @param type            only for log
     * @param pkId            only for log
     * @param markMaxDuration 如果为True，则时间段超过DURATION_MAX_SEC后就返回-1，表明这是无效时段，需要将状态改为关闭
     * @return
     */
    public static int getDuration(Date endTime, Date startTime, String type, Long pkId, boolean markMaxDuration) {
        int duration = 0;
        if (null != endTime && null != startTime) {
            duration = (int) ((endTime.getTime() - startTime.getTime()) / 1000);
        }
        if (duration < 0) {
            logger.warn("duration invalid reset to 0, duration:{}, type:{}, pkId:{}, ", duration, type, pkId);
            duration = 0;
        } else if (duration > StaConst.DURATION_MAX_SEC) {
            if (markMaxDuration) {
                logger.warn("duration invalid reset to -1, need to end, duration:{}, type:{}, pkId:{}, ", duration, type, pkId);
                duration = -1;
            } else {
                logger.warn("duration invalid reset to 0, duration:{}, type:{}, pkId:{}, ", duration, type, pkId);
                duration = 0;
            }
        } else if (duration > 3600) { // 超过一个小时的时长，记录log
            logger.info("large duration:{}, type:{}, pkId:{}, ", duration, type, pkId);
        }
        return duration;
    }

}
