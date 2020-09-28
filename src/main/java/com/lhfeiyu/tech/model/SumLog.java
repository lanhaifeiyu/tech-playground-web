package com.lhfeiyu.tech.model;

import com.zom.statistics.dao.po.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
public class SumLog implements Serializable {

    private static Logger logger = LoggerFactory.getLogger(SumLog.class);

    private List<StaUserImRecord> imRecordList;
    private List<StaUserMileageRecord> mileageRecordList;
    private List<StaUserOnlineRecord> onlineRecordList;
    private List<StaUserPhotoRecord> photoRecordList;
    private List<StaUserTmpgroupRecord> tmpgroupRecordList;
    private List<StaUserVideoRecord> videoRecordList;
    private List<StaUserAudioRecord> audioRecordList;
    private List<StaUserSosRecord> sosRecordList;

    private boolean buildLastPkIdDone = false;
    private boolean dataEmpty = true;
    private Long imRecordLastPkId;
    private Long mileageRecordLastPkId;
    private Long onlineRecordLastPkId;
    private Long photoRecordLastPkId;
    private Long tmpgroupRecordLastPkId;
    private Long videoRecordLastPkId;
    private Long audioRecordLastPkId;
    private Long sosRecordLastPkId;

    /**
     * 各个LIST的数据应该是递增或者递减的，从数据库中查询出来时会设置一次，就不可更改，然后在刷新位置，确保是一致的
     */
    public boolean buildAllLastPkId() {
        if (buildLastPkIdDone) {
            logger.warn("already buildAllLastPkId, ignore");
            return false;
        }
        //logger.debug("buildAllLastPkId");
        buildLastPkIdDone = true;
        this.imRecordLastPkId = null;
        this.mileageRecordLastPkId = null;
        this.onlineRecordLastPkId = null;
        this.photoRecordLastPkId = null;
        this.tmpgroupRecordLastPkId = null;
        this.videoRecordLastPkId = null;
        this.audioRecordLastPkId = null;
        this.sosRecordLastPkId = null;
        if (null != imRecordList && imRecordList.size() > 0) {
            dataEmpty = false;
            long startPkId = imRecordList.get(0).getId();
            long endPkId = imRecordList.get(imRecordList.size() - 1).getId();
            this.imRecordLastPkId = endPkId > startPkId ? endPkId : startPkId;
        }
        if (null != mileageRecordList && mileageRecordList.size() > 0) {
            dataEmpty = false;
            long startPkId = mileageRecordList.get(0).getId();
            long endPkId = mileageRecordList.get(mileageRecordList.size() - 1).getId();
            this.mileageRecordLastPkId = endPkId > startPkId ? endPkId : startPkId;
        }
        if (null != onlineRecordList && onlineRecordList.size() > 0) {
            dataEmpty = false;
            long startPkId = onlineRecordList.get(0).getId();
            long endPkId = onlineRecordList.get(onlineRecordList.size() - 1).getId();
            this.onlineRecordLastPkId = endPkId > startPkId ? endPkId : startPkId;
        }
        if (null != photoRecordList && photoRecordList.size() > 0) {
            dataEmpty = false;
            long startPkId = photoRecordList.get(0).getId();
            long endPkId = photoRecordList.get(photoRecordList.size() - 1).getId();
            this.photoRecordLastPkId = endPkId > startPkId ? endPkId : startPkId;
        }
        if (null != tmpgroupRecordList && tmpgroupRecordList.size() > 0) {
            dataEmpty = false;
            long startPkId = tmpgroupRecordList.get(0).getId();
            long endPkId = tmpgroupRecordList.get(tmpgroupRecordList.size() - 1).getId();
            this.tmpgroupRecordLastPkId = endPkId > startPkId ? endPkId : startPkId;
        }
        if (null != videoRecordList && videoRecordList.size() > 0) {
            dataEmpty = false;
            long startPkId = videoRecordList.get(0).getId();
            long endPkId = videoRecordList.get(videoRecordList.size() - 1).getId();
            this.videoRecordLastPkId = endPkId > startPkId ? endPkId : startPkId;
        }
        if (null != audioRecordList && audioRecordList.size() > 0) {
            dataEmpty = false;
            long startPkId = audioRecordList.get(0).getId();
            long endPkId = audioRecordList.get(audioRecordList.size() - 1).getId();
            this.audioRecordLastPkId = endPkId > startPkId ? endPkId : startPkId;
        }
        if (null != sosRecordList && sosRecordList.size() > 0) {
            dataEmpty = false;
            long startPkId = sosRecordList.get(0).getId();
            long endPkId = sosRecordList.get(sosRecordList.size() - 1).getId();
            this.sosRecordLastPkId = endPkId > startPkId ? endPkId : startPkId;
        }
        return true;
    }

}
