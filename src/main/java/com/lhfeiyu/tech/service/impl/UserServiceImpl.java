package com.lhfeiyu.tech.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zom.statistics.DTO.InterfaceDoc.BaseReqDto;
import com.zom.statistics.DTO.InterfaceDoc.UserAllDto;
import com.zom.statistics.DTO.InterfaceDoc.UserRequestParams;
import com.zom.statistics.DTO.RtvUnit;
import com.zom.statistics.dao.mapper.common.RtvUnitMapper;
import com.zom.statistics.dao.mapper.logMapper.StaDayUserMapper;
import com.zom.statistics.dao.po.StaDayUser;
import com.zom.statistics.service.IUserService;
import com.zom.statistics.tools.CommonUnit;
import com.zom.statistics.tools.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private RtvUnitMapper rtvUnitMapper;
    @Autowired
    private StaDayUserMapper staDayUserMapper;

    private static final ThreadLocal<DateFormat> ym = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyyMM");
        }
    };

    private static final ThreadLocal<DateFormat> ymd = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMdd");
        }
    };

    private static final ThreadLocal<SimpleDateFormat> ymdh = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMddHH");
        }
    };

    private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public JSONObject getAllUser(UserAllDto param) {
        JSONObject dateJson = CommonUnit.DateParamCheck(param);
        if (dateJson.get("error") != null) {
            return dateJson;
        }
        if (param.getDept_id() != null) {
            List<RtvUnit> units = rtvUnitMapper.selectByUniqueIn(param.getDept_id());
            if (units.size() == 0) {// 部门不存在
                dateJson.put("error", "部门不存在");
                return dateJson;
            }
        }
        Map<String, Map<Long, StaDayUser>> map = new HashMap<>();
        Integer endTime, startTime = 0;
        List<String> dateList = new ArrayList<>();
        try {
            if ("ym".equalsIgnoreCase(param.getTime_level())) {
                startTime = Integer.parseInt(ym.get().format(ym.get().parse(param.getStart_time().toString())));
                if (param.getEnd_time() == null) {
                    endTime = Integer.parseInt(ym.get().format(new Date()));
                } else {
                    endTime = param.getEnd_time();
                }
                dateList = DateUtils.getDate(startTime.toString(), endTime.toString(), "ym");
                logger.info("开始时间与结束时间相差的月份为：" + dateList);
            } else if ("ymd".equalsIgnoreCase(param.getTime_level())) {
                startTime = param.getStart_time();
                endTime = Integer.parseInt(ymd.get().format(new Date()));
                dateList = DateUtils.getDate(startTime.toString(), endTime.toString(), "ymd");
                logger.info("开始时间与结束时间相差的日期为：" + dateList);
            }
            for (String s : dateList) {
                List<StaDayUser> staDayUsers = staDayUserMapper.selectByUniqueIdAndUidsAndDate(param.getDept_id(), param.getUids(), s);
                Map<Long, StaDayUser> staDayUserMap = staDayUsers.stream().collect(Collectors.toMap(v -> v.getUid(), v -> v));
                map.put(s, staDayUserMap);
            }
            dateJson.put("data", map);
        } catch (Exception e) {
            logger.error("查询部门信息时发生异常，Exception：" + e.getMessage());
            e.printStackTrace();
            dateJson.put("error", "查询部门信息时发生异常");
        }

        return dateJson;
    }


    @Override
    public JSONObject audioCount(UserRequestParams param) {
        JSONObject dateJson = this.getDateList(param);
        if (dateJson.get("error") != null) {
            return dateJson;
        }
        Map<String, Map<Long, JSONObject>> map = new HashMap<>();
        List<String> dateList = (List<String>) dateJson.get("dateList");

        for (String date : dateList) {
            List<StaDayUser> value = staDayUserMapper.selectAudioCountByUniqueIdAndDate(param.getUids(), date);
            if (param.getType() == 1) {// 单呼
                if (param.getMin() != null) {
                    value = value.stream().filter(v -> v.getIndividualTalkCount() >= param.getMin()).collect(Collectors.toList());
                }
                if (param.getMax() != null) {
                    value = value.stream().filter(v -> v.getIndividualTalkCount() <= param.getMax()).collect(Collectors.toList());
                }
                value.stream().map(v -> v.setTalkCount(v.getIndividualTalkCount())).collect(Collectors.toList());
            } else if (param.getType() == 2) {// 组呼
                if (param.getMin() != null) {
                    value = value.stream().filter(v -> v.getGroupTalkCount() >= param.getMin()).collect(Collectors.toList());
                }
                if (param.getMax() != null) {
                    value = value.stream().filter(v -> v.getGroupTalkCount() <= param.getMax()).collect(Collectors.toList());
                }
                value.stream().map(v -> v.setTalkCount(v.getGroupTalkCount())).collect(Collectors.toList());
            } else {
                // 单呼 & 组呼
                if (param.getMin() != null) {
                    value = value.stream().filter(v -> v.getIndividualTalkCount() >= param.getMin() || v.getGroupTalkCount() >= param.getMin()).collect(Collectors.toList());
                }
                if (param.getMax() != null) {
                    value = value.stream().filter(v -> v.getIndividualTalkCount() <= param.getMax() || v.getGroupTalkCount() <= param.getMax()).collect(Collectors.toList());
                }
                value.stream().map(v -> v.setTalkCount(v.getGroupTalkCount() + v.getIndividualTalkCount())).collect(Collectors.toList());
            }
            Map<Long, JSONObject> jsonObjects = new HashMap<>();
            value.forEach(v -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("dept_id", v.getDeptUniqueId());
                jsonObject.put("dept_name", v.getDeptName());
                jsonObject.put("uid", v.getUid());
                jsonObject.put("name", v.getName());
                if (param.getType() == 1) {// 单呼
                    jsonObject.put("user_talk_count", v.getIndividualTalkCount());
                } else if (param.getType() == 2) {// 组呼
                    jsonObject.put("group_talk_count", v.getGroupTalkCount());
                } else {
                    jsonObject.put("user_talk_count", v.getIndividualTalkCount());
                    jsonObject.put("group_talk_count", v.getGroupTalkCount());
                }
                jsonObject.put("all_count", v.getTalkCount());
                jsonObjects.put(v.getUid(), jsonObject);
            });
            map.put(date, jsonObjects);
        }
        dateJson.put("data", map);
        return dateJson;
    }


    @Override
    public JSONObject audioDuration(UserRequestParams param) {
        JSONObject dateJson = this.getDateList(param);
        if (dateJson.get("error") != null) {
            return dateJson;
        }
        Map<String, Map<Long, JSONObject>> map = new HashMap<>();
        List<String> dateList = (List<String>) dateJson.get("dateList");

        for (String date : dateList) {
            List<StaDayUser> value = staDayUserMapper.selectAudioDurationByUniqueIdAndDate(param.getUids(), date);
            if (param.getType() == 1) {// 单呼
                if (param.getMin() != null) {
                    value = value.stream().filter(v -> v.getIndividualTalkDuration() >= param.getMin()).collect(Collectors.toList());
                }
                if (param.getMax() != null) {
                    value = value.stream().filter(v -> v.getIndividualTalkDuration() <= param.getMax()).collect(Collectors.toList());
                }
                value.stream().map(v -> v.setTalkCount(v.getIndividualTalkDuration())).collect(Collectors.toList());
            } else if (param.getType() == 2) {// 组呼
                if (param.getMin() != null) {
                    value = value.stream().filter(v -> v.getGroupTalkDuration() >= param.getMin()).collect(Collectors.toList());
                }
                if (param.getMax() != null) {
                    value = value.stream().filter(v -> v.getGroupTalkDuration() <= param.getMax()).collect(Collectors.toList());
                }
                value.stream().map(v -> v.setTalkCount(v.getGroupTalkDuration())).collect(Collectors.toList());
            } else {
                // 单呼 & 组呼
                if (param.getMin() != null) {
                    value = value.stream().filter(v -> v.getIndividualTalkDuration() >= param.getMin() || v.getGroupTalkDuration() >= param.getMin()).collect(Collectors.toList());
                }
                if (param.getMax() != null) {
                    value = value.stream().filter(v -> v.getIndividualTalkDuration() <= param.getMax() || v.getGroupTalkDuration() <= param.getMax()).collect(Collectors.toList());
                }
                value.stream().map(v -> v.setTalkDuration(v.getGroupTalkDuration() + v.getIndividualTalkDuration())).collect(Collectors.toList());
            }
            Map<Long, JSONObject> jsonObjects = new HashMap<>();
            value.forEach(v -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("dept_id", v.getDeptUniqueId());
                jsonObject.put("dept_name", v.getDeptName());
                jsonObject.put("uid", v.getUid());
                jsonObject.put("name", v.getName());
                if (param.getType() == 1) {// 单呼
                    jsonObject.put("user_talk_duration", v.getIndividualTalkDuration());
                } else if (param.getType() == 2) {// 组呼
                    jsonObject.put("group_talk_duration", v.getGroupTalkDuration());
                } else {
                    jsonObject.put("user_talk_duration", v.getIndividualTalkDuration());
                    jsonObject.put("group_talk_duration", v.getGroupTalkDuration());
                }
                jsonObject.put("all_duration", v.getTalkDuration());
                jsonObjects.put(v.getUid(), jsonObject);
            });
            map.put(date, jsonObjects);
        }
        dateJson.put("data", map);
        return dateJson;
    }


    @Override
    public JSONObject videoCount(UserRequestParams param) {
        JSONObject dateJson = this.getDateList(param);
        if (dateJson.get("error") != null) {
            return dateJson;
        }
        Map<String, Map<Long, JSONObject>> map = new HashMap<>();
        List<String> dateList = (List<String>) dateJson.get("dateList");
        for (String date : dateList) {
            List<StaDayUser> value = staDayUserMapper.selectVideoCountByUniqueIdAndDate(param.getUids(), date);
            if (param.getType() == 1) {// 视频通话
                if (param.getMin() != null) {
                    value = value.stream().filter(v -> v.getVideoCallCount() >= param.getMin()).collect(Collectors.toList());
                }
                if (param.getMax() != null) {
                    value = value.stream().filter(v -> v.getVideoCallCount() <= param.getMax()).collect(Collectors.toList());
                }
                value.stream().map(v -> v.setTalkCount(v.getVideoCallCount())).collect(Collectors.toList());
            } else if (param.getType() == 2) {// 视频回传
                if (param.getMin() != null) {
                    value = value.stream().filter(v -> v.getVideoUploadCount() >= param.getMin()).collect(Collectors.toList());
                }
                if (param.getMax() != null) {
                    value = value.stream().filter(v -> v.getVideoUploadCount() <= param.getMax()).collect(Collectors.toList());
                }
                value.stream().map(v -> v.setTalkCount(v.getVideoUploadCount())).collect(Collectors.toList());
            } else if (param.getType() == 3){// 视频点名
                if (param.getMin() != null) {
                    value = value.stream().filter(v -> v.getVideoRollcallCount() >= param.getMin()).collect(Collectors.toList());
                }
                if (param.getMax() != null) {
                    value = value.stream().filter(v -> v.getVideoRollcallCount() <= param.getMax()).collect(Collectors.toList());
                }
                value.stream().map(v -> v.setTalkCount(v.getVideoRollcallCount())).collect(Collectors.toList());
            } else if (param.getType() == 4){// 视频会商
                if (param.getMin() != null) {
                    value = value.stream().filter(v -> v.getVideoConfCount() >= param.getMin()).collect(Collectors.toList());
                }
                if (param.getMax() != null) {
                    value = value.stream().filter(v -> v.getVideoConfCount() <= param.getMax()).collect(Collectors.toList());
                }
                value.stream().map(v -> v.setTalkCount(v.getVideoConfCount())).collect(Collectors.toList());
            } else {
                if (param.getMin() != null) {
                    value = value.stream().filter(v -> v.getVideoCallCount() >= param.getMin()
                            || v.getVideoUploadCount() >= param.getMin()
                            || v.getVideoRollcallCount() >= param.getMin()
                            || v.getVideoConfCount() >= param.getMin()).collect(Collectors.toList());
                }
                if (param.getMax() != null) {
                    value = value.stream().filter(v -> v.getVideoCallCount() <= param.getMax()
                            || v.getVideoUploadCount() <= param.getMax()
                            || v.getVideoRollcallCount() <= param.getMax()
                            || v.getVideoConfCount() <= param.getMax()).collect(Collectors.toList());
                }
                value.stream().map(v -> v.setVideoAllCount(v.getVideoCallCount() + v.getVideoUploadCount() + v.getVideoRollcallCount() + v.getVideoConfCount())).collect(Collectors.toList());
            }
            Map<Long, JSONObject> jsonObjects = new HashMap<>();
            value.forEach(v -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("dept_id", v.getDeptUniqueId());
                jsonObject.put("dept_name", v.getDeptName());
                jsonObject.put("uid", v.getUid());
                jsonObject.put("name", v.getName());
                if (param.getType() == 1) {// 单呼
                    jsonObject.put("video_call_count", v.getVideoCallCount());
                } else if (param.getType() == 2) {// 组呼
                    jsonObject.put("video_upload_count", v.getVideoUploadCount());
                } else if (param.getType() == 3) {
                    jsonObject.put("video_rollcall_count", v.getVideoRollcallCount());
                } else if (param.getType() == 4) {
                    jsonObject.put("video_conf_count", v.getVideoConfCount());
                } else {
                    jsonObject.put("video_call_count", v.getVideoCallCount());
                    jsonObject.put("video_upload_count", v.getVideoUploadCount());
                    jsonObject.put("video_rollcall_count", v.getVideoRollcallCount());
                    jsonObject.put("video_conf_count", v.getVideoConfCount());
                }
                jsonObject.put("all_count", v.getVideoAllCount());
                jsonObjects.put(v.getUid(), jsonObject);
            });
            map.put(date, jsonObjects);
        }
        dateJson.put("data", map);
        return dateJson;
    }


    @Override
    public JSONObject videoDuration(UserRequestParams param) {
        JSONObject dateJson = this.getDateList(param);
        if (dateJson.get("error") != null) {
            return dateJson;
        }
        Map<String, Map<Long, JSONObject>> map = new HashMap<>();
        List<String> dateList = (List<String>) dateJson.get("dateList");

        for (String date : dateList) {
            Map<Long, JSONObject> jsonObjects = new HashMap<>();
            List<StaDayUser> value = staDayUserMapper.selectVideoDurationByUniqueIdAndDate(param.getUids(), date);

            if (param.getMin() != null) {
                value = value.stream().filter(v -> v.getVideoDuration() >= param.getMin()).collect(Collectors.toList());
            }
            if (param.getMax() != null) {
                value = value.stream().filter(v -> v.getVideoDuration() <= param.getMax()).collect(Collectors.toList());
            }
            value.forEach(v -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("dept_id", v.getDeptUniqueId());
                jsonObject.put("dept_name", v.getDeptName());
                jsonObject.put("uid", v.getUid());
                jsonObject.put("name", v.getName());
                jsonObject.put("all_duration", v.getVideoDuration());
                jsonObjects.put(v.getUid(), jsonObject);
            });
            map.put(date, jsonObjects);
        }
        dateJson.put("data", map);
        return dateJson;
    }



    @Override
    public JSONObject photoCount(UserRequestParams param) {
        JSONObject dateJson = this.getDateList(param);
        if (dateJson.get("error") != null) {
            return dateJson;
        }
        Map<String, Map<Long, JSONObject>> map = new HashMap<>();
        List<String> dateList = (List<String>) dateJson.get("dateList");

        for (String date : dateList) {
            Map<Long, JSONObject> jsonObjects = new HashMap<>();
            List<StaDayUser> value = staDayUserMapper.selectPhotoCountByUniqueIdAndDate(param.getUids(), date);

            if (param.getMin() != null) {
                value = value.stream().filter(v -> v.getPhotoUploadCount() >= param.getMin()).collect(Collectors.toList());
            }
            if (param.getMax() != null) {
                value = value.stream().filter(v -> v.getPhotoUploadCount() <= param.getMax()).collect(Collectors.toList());
            }
            value.forEach(v -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("dept_id", v.getDeptUniqueId());
                jsonObject.put("dept_name", v.getDeptName());
                jsonObject.put("uid", v.getUid());
                jsonObject.put("name", v.getName());
                jsonObject.put("all_count", v.getPhotoUploadCount());
                jsonObjects.put(v.getUid(), jsonObject);
            });
            map.put(date, jsonObjects);
        }
        dateJson.put("data", map);
        return dateJson;
    }


    @Override
    public JSONObject sosCount(UserRequestParams param) {
        JSONObject dateJson = this.getDateList(param);
        if (dateJson.get("error") != null) {
            return dateJson;
        }
        Map<String, Map<Long, JSONObject>> map = new HashMap<>();
        List<String> dateList = (List<String>) dateJson.get("dateList");
        for (String date : dateList) {
            Map<Long, JSONObject> jsonObjects = new HashMap<>();
            List<StaDayUser> value = staDayUserMapper.selectSosCountByUniqueIdAndDate(param.getUids(), date);

            if (param.getMin() != null) {
                value = value.stream().filter(v -> v.getSosCount() >= param.getMin()).collect(Collectors.toList());
            }
            if (param.getMax() != null) {
                value = value.stream().filter(v -> v.getSosCount() <= param.getMax()).collect(Collectors.toList());
            }
            value.forEach(v -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("dept_id", v.getDeptUniqueId());
                jsonObject.put("dept_name", v.getDeptName());
                jsonObject.put("uid", v.getUid());
                jsonObject.put("name", v.getName());
                jsonObject.put("all_count", v.getSosCount());
                jsonObjects.put(v.getUid(), jsonObject);
            });
            map.put(date, jsonObjects);
        }
        dateJson.put("data", map);
        return dateJson;
    }

    @Override
    public JSONObject imCount(UserRequestParams param) {
        JSONObject dateJson = this.getDateList(param);
        if (dateJson.get("error") != null) {
            return dateJson;
        }
        Map<String, Map<Long, JSONObject>> map = new HashMap<>();
        List<String> dateList = (List<String>) dateJson.get("dateList");
        for (String date : dateList) {
            Map<Long, JSONObject> jsonObjects = new HashMap<>();
            List<StaDayUser> value = staDayUserMapper.selectImCountByUniqueIdAndDate(param.getUids(), date);

            if (param.getType() == 1) {// 个人
                if (param.getMin() != null) {
                    value = value.stream().filter(v -> v.getIndividualImCount() >= param.getMin()).collect(Collectors.toList());
                }
                if (param.getMax() != null) {
                    value = value.stream().filter(v -> v.getIndividualImCount() <= param.getMax()).collect(Collectors.toList());
                }
                value.stream().map(v -> v.setImCount(v.getIndividualImCount())).collect(Collectors.toList());
            } else if (param.getType() == 2) {// 群组
                if (param.getMin() != null) {
                    value = value.stream().filter(v -> v.getGroupImCount() >= param.getMin()).collect(Collectors.toList());
                }
                if (param.getMax() != null) {
                    value = value.stream().filter(v -> v.getGroupImCount() <= param.getMax()).collect(Collectors.toList());
                }
                value.stream().map(v -> v.setImCount(v.getGroupImCount())).collect(Collectors.toList());
            } else {
                if (param.getMin() != null) {
                    value = value.stream().filter(v -> v.getGroupImCount() >= param.getMin()
                            || v.getIndividualImCount() >= param.getMin()).collect(Collectors.toList());
                }
                if (param.getMax() != null) {
                    value = value.stream().filter(v -> v.getGroupImCount() <= param.getMax()
                            || v.getIndividualImCount() <= param.getMax()).collect(Collectors.toList());
                }
                value.stream().map(v -> v.setImCount(v.getGroupImCount() + v.getIndividualImCount())).collect(Collectors.toList());
            }
            value.forEach(v -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("dept_id", v.getDeptUniqueId());
                jsonObject.put("dept_name", v.getDeptName());
                jsonObject.put("uid", v.getUid());
                jsonObject.put("name", v.getName());
                if (param.getType() == 1) {
                    jsonObject.put("user_im_count", v.getIndividualImCount());
                } else if (param.getType() == 2) {
                    jsonObject.put("group_im_count", v.getGroupImCount());
                } else {
                    jsonObject.put("user_im_count", v.getIndividualImCount());
                    jsonObject.put("group_im_count", v.getGroupImCount());
                }
                jsonObject.put("all_count", v.getImCount());
                jsonObjects.put(v.getUid(), jsonObject);
            });
            map.put(date, jsonObjects);
        }
        dateJson.put("data", map);
        return dateJson;
    }

    @Override
    public JSONObject onlineDuration(UserRequestParams param) {
        JSONObject dateJson = this.getDateList(param);
        if (dateJson.get("error") != null) {
            return dateJson;
        }
        Map<String, Map<Long, JSONObject>> map = new HashMap<>();
        List<String> dateList = (List<String>) dateJson.get("dateList");
        for (String date : dateList) {
            Map<Long, JSONObject> jsonObjects = new HashMap<>();
            List<StaDayUser> value = staDayUserMapper.selectOnlineDurationByUniqueIdAndDate(param.getUids(), date);

            if (param.getMin() != null) {
                value = value.stream().filter(v -> v.getOnlineDuration() >= param.getMin()).collect(Collectors.toList());
            }
            if (param.getMax() != null) {
                value = value.stream().filter(v -> v.getOnlineDuration() <= param.getMax()).collect(Collectors.toList());
            }
            value.forEach(v -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("dept_id", v.getDeptUniqueId());
                jsonObject.put("dept_name", v.getDeptName());
                jsonObject.put("uid", v.getUid());
                jsonObject.put("name", v.getName());
                jsonObject.put("all_duration", v.getOnlineDuration());
                jsonObjects.put(v.getUid(), jsonObject);
            });
            map.put(date, jsonObjects);
        }
        dateJson.put("data", map);
        return dateJson;
    }

    @Override
    public JSONObject onpostDuration(UserRequestParams param) {
        JSONObject dateJson = this.getDateList(param);
        if (dateJson.get("error") != null) {
            return dateJson;
        }
        Map<String, Map<Long, JSONObject>> map = new HashMap<>();
        List<String> dateList = (List<String>) dateJson.get("dateList");

        for (String date : dateList) {
            Map<Long, JSONObject> jsonObjects = new HashMap<>();
            List<StaDayUser> value = staDayUserMapper.selectOnpostDurationByUniqueIdAndDate(param.getUids(), date);

            if (param.getMin() != null) {
                value = value.stream().filter(v -> v.getOnpostDuration() >= param.getMin()).collect(Collectors.toList());
            }
            if (param.getMax() != null) {
                value = value.stream().filter(v -> v.getOnpostDuration() <= param.getMax()).collect(Collectors.toList());
            }
            value.forEach(v -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("dept_id", v.getDeptUniqueId());
                jsonObject.put("dept_name", v.getDeptName());
                jsonObject.put("uid", v.getUid());
                jsonObject.put("name", v.getName());
                jsonObject.put("all_duration", v.getOnpostDuration());
                jsonObjects.put(v.getUid(), jsonObject);
            });
            map.put(date, jsonObjects);
        }
        dateJson.put("data", map);
        return dateJson;
    }


    @Override
    public JSONObject mileageCount(UserRequestParams param) {
        JSONObject dateJson = this.getDateList(param);
        if (dateJson.get("error") != null) {
            return dateJson;
        }
        Map<String, Map<Long, JSONObject>> map = new HashMap<>();
        List<String> dateList = (List<String>) dateJson.get("dateList");

        for (String date : dateList) {
            Map<Long, JSONObject> jsonObjects = new HashMap<>();
            List<StaDayUser> value = staDayUserMapper.selectMileageCountByUniqueIdAndDate(param.getUids(), date);

            if (param.getMin() != null) {
                value = value.stream().filter(v -> v.getMileage() >= param.getMin()).collect(Collectors.toList());
            }
            if (param.getMax() != null) {
                value = value.stream().filter(v -> v.getMileage() <= param.getMax()).collect(Collectors.toList());
            }
            value.forEach(v -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("dept_id", v.getDeptUniqueId());
                jsonObject.put("dept_name", v.getDeptName());
                jsonObject.put("uid", v.getUid());
                jsonObject.put("name", v.getName());
                jsonObject.put("all_count", v.getMileage());
                jsonObjects.put(v.getUid(), jsonObject);
            });
            map.put(date, jsonObjects);
        }
        dateJson.put("data", map);
        return dateJson;
    }


    @Override
    public JSONObject tmpGroupCount(UserRequestParams param) {
        JSONObject dateJson = this.getDateList(param);
        if (dateJson.get("error") != null) {
            return dateJson;
        }
        Map<String, Map<Long, JSONObject>> map = new HashMap<>();
        List<String> dateList = (List<String>) dateJson.get("dateList");

        for (String date : dateList) {
            Map<Long, JSONObject> jsonObjects = new HashMap<>();
            List<StaDayUser> value = staDayUserMapper.selectTmpGroupCountByUniqueIdAndDate(param.getUids(), date);

            if (param.getMin() != null) {
                value = value.stream().filter(v -> v.getTmpGroupCount() >= param.getMin()).collect(Collectors.toList());
            }
            if (param.getMax() != null) {
                value = value.stream().filter(v -> v.getTmpGroupCount() <= param.getMax()).collect(Collectors.toList());
            }
            value.forEach(v -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("dept_id", v.getDeptUniqueId());
                jsonObject.put("dept_name", v.getDeptName());
                jsonObject.put("uid", v.getUid());
                jsonObject.put("name", v.getName());
                jsonObject.put("all_count", v.getTmpGroupCount());
                jsonObjects.put(v.getUid(), jsonObject);
            });
            map.put(date, jsonObjects);
        }
        dateJson.put("data", map);
        return dateJson;
    }

    @Override
    public JSONObject selectByUnitId(Map<String, Object> requestParams) {
        JSONObject jsonObject = new JSONObject();
        Integer pageSize = 0;
        Integer page = 1;
        String uniqueId = requestParams.get("dept_id") == null ? "" : String.valueOf(requestParams.get("dept_id"));
        if (requestParams.get("key_count") == null) {
            pageSize = Integer.parseInt(requestParams.get("count").toString());
        } else {
            pageSize = Integer.parseInt(requestParams.get(requestParams.get("key_count")).toString());
        }
        String startTime = "";
        String entTime = "";
        if (requestParams.get("start_time") != null) {
            startTime = requestParams.get("start_time").toString();
        }
        if (requestParams.get("end_time") != null) {
            entTime = requestParams.get("end_time").toString();
        }
        page = Integer.parseInt(requestParams.get("page").toString());
        PageHelper.startPage(page, pageSize);
        List<StaDayUser> list = staDayUserMapper.selectByUniqueId(uniqueId, startTime, entTime);
        PageInfo<StaDayUser> pageInfo = new PageInfo<>(list);

        jsonObject.put("data", list);
        jsonObject.put("total_page", (pageInfo.getTotal() / pageSize) == 0 ? 1 : (pageInfo.getTotal() / pageSize + 1));
        jsonObject.put("total", pageInfo.getTotal());

        return jsonObject;
    }

    private JSONObject getDateList (BaseReqDto param) {
        JSONObject dateJson = CommonUnit.DateParamCheck(param);
        if (dateJson.get("error") != null) {
            return dateJson;
        }
        Integer endTime, startTime = 0;
        try {
            List<String> dateList = new ArrayList<>();
            if ("ym".equalsIgnoreCase(param.getTime_level())) {
                startTime = Integer.parseInt(ym.get().format(ym.get().parse(param.getStart_time().toString())));
                if (param.getEnd_time() == null) {
                    endTime = Integer.parseInt(ym.get().format(new Date()));
                } else {
                    endTime = param.getEnd_time();
                }
                dateList = CommonUnit.getDate(startTime.toString(), endTime.toString(), "ym");
                logger.info("开始时间与结束时间相差的月份为：" + dateList);

            } else if ("ymd".equalsIgnoreCase(param.getTime_level())) {
                startTime = param.getStart_time();
                endTime = Integer.parseInt(ymd.get().format(new Date()));
                dateList = CommonUnit.getDate(startTime.toString(), endTime.toString(), "ymd");
                logger.info("开始时间与结束时间相差的日期为：" + dateList);
            }
            dateList = dateList.stream().sorted().collect(Collectors.toList());
            dateJson.put("dateList", dateList);
        } catch (Exception e) {
            logger.error("查询部门信息时发生异常，Exception：" + e.getMessage());
            e.printStackTrace();
            dateJson.put("error", "查询部门信息时发生异常");
        }
        return dateJson;
    }
}
