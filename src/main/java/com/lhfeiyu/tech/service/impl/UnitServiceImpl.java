package com.lhfeiyu.tech.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zom.statistics.DTO.InterfaceDoc.DeptRequestParams;
import com.zom.statistics.DTO.RtvUnit;
import com.zom.statistics.dao.mapper.common.RtvUnitMapper;
import com.zom.statistics.dao.mapper.logMapper.StaDayDeptMapper;
import com.zom.statistics.dao.po.StaDayDept;
import com.zom.statistics.service.IUnitService;
import com.zom.statistics.tools.CommonUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UnitServiceImpl implements IUnitService {

    @Autowired
    private StaDayDeptMapper deptMapper;
    @Autowired
    private RtvUnitMapper rtvUnitMapper;

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

    private static Logger logger = LoggerFactory.getLogger(UnitServiceImpl.class);



    /**
     * 对yyyyMMdd格式的日期进行加多天操作
     * @param simpleDateFormat
     * @param value
     * @param day
     * @return
     */
    private static String timeAddDate (DateFormat simpleDateFormat, Integer value, int day) throws ParseException {
        Calendar c = Calendar.getInstance();
        Date date = simpleDateFormat.parse(value.toString());
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, day);
        return simpleDateFormat.format(c.getTime());
    }


    /**
     * 对yyyyMM格式进行月份加一操作
     * @param simpleDateFormat
     * @param value
     * @param month
     * @return
     */
    private static String timeAddMonth (DateFormat simpleDateFormat, String value, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(value.substring(0, 4)), Integer.parseInt(value.substring(4, 6)) - 1, 01);
        calendar.add(Calendar.MONTH, month);
        return simpleDateFormat.format(calendar.getTime());
    }


    private static String getMonthLastDay (DateFormat simpleDateFormat, String value) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(value.substring(0, 4)), Integer.parseInt(value.substring(4, 6)) - 1, 01);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return simpleDateFormat.format(calendar.getTime());
    }

    private JSONObject getDateList (DeptRequestParams param) {
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
        List<String> uniqueIds = new ArrayList<>();
        Integer endTime, startTime = 0;
        Map<String, Map<String, StaDayDept>> map = new HashMap<>();

        if (param.getDept_id() != null) {
            if (param.getDept_chain() == 0) {
                uniqueIds.add(param.getDept_id());
            } else if (param.getDept_chain() == 3) {// 此参数是sub_all传递过来的值
                uniqueIds = rtvUnitMapper.selectUniqueIdByParentId(param.getDept_id());
            } else {
                String ids = rtvUnitMapper.selectCommonUniqueDown(param.getDept_id());
                uniqueIds.addAll(Arrays.asList(ids.split(",")));
            }
        }
        uniqueIds = uniqueIds.stream().distinct().collect(Collectors.toList());
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
            dateJson.put("uniqueIds", uniqueIds);
        } catch (Exception e) {
            logger.error("查询部门信息时发生异常，Exception：" + e.getMessage());
            e.printStackTrace();
            dateJson.put("error", "查询部门信息时发生异常");
        }
        return dateJson;
    }


    @Override
    public JSONObject getCurrentUnit(DeptRequestParams param) {

        JSONObject dateJson = this.getDateList(param);
        if (dateJson.get("error") != null) {
            return dateJson;
        }
        Map<String, Map<String, StaDayDept>> map = new HashMap<>();
        List<String> dateList = (List<String>) dateJson.get("dateList");
        List<String> uniqueIds = (List<String>) dateJson.get("uniqueIds");
        String uniqueIdIn = uniqueIds.stream().collect(Collectors.joining(","));
        if (uniqueIds.size() == 0) {
            uniqueIdIn = null;
        } else {
            uniqueIdIn = "'" + uniqueIdIn.replaceAll(",", "','") + "'";
        }

        for (String s : dateList) {
            List<StaDayDept> value = deptMapper.selectByUniqueIdAndDate(uniqueIdIn, s);
            Map<String, StaDayDept> uniqueMonthData = value.stream().collect(Collectors.toMap(v -> v.getDeptUniqueId(), v -> v));
            map.put(s, uniqueMonthData);
        }
        dateJson.put("data", map);
        return dateJson;

    }

    @Override
    public JSONObject audioCount(DeptRequestParams deptRequestParams) {
        JSONObject dateJson = this.getDateList(deptRequestParams);
        if (dateJson.get("error") != null) {
            return dateJson;
        }
        Map<String, Map<String, JSONObject>> map = new HashMap<>();
        List<String> dateList = (List<String>) dateJson.get("dateList");
        List<String> uniqueIds = (List<String>) dateJson.get("uniqueIds");

        for (String date : dateList) {
            String uniqueIdIn = uniqueIds.stream().collect(Collectors.joining(","));
            uniqueIdIn = "'" + uniqueIdIn.replaceAll(",", "','") + "'";
            List<StaDayDept> value = deptMapper.selectAudioCountByUniqueIdAndDate(uniqueIdIn, date);
            if (deptRequestParams.getType() == 1) {// 单呼
                if (deptRequestParams.getMin() != null) {
                    value = value.stream().filter(v -> v.getIndividualTalkCount() >= deptRequestParams.getMin()).collect(Collectors.toList());
                }
                if (deptRequestParams.getMax() != null) {
                    value = value.stream().filter(v -> v.getIndividualTalkCount() <= deptRequestParams.getMax()).collect(Collectors.toList());
                }
                value.stream().map(v -> v.setTalkCount(v.getIndividualTalkCount())).collect(Collectors.toList());
            } else if (deptRequestParams.getType() == 2) {// 组呼
                if (deptRequestParams.getMin() != null) {
                    value = value.stream().filter(v -> v.getGroupTalkCount() >= deptRequestParams.getMin()).collect(Collectors.toList());
                }
                if (deptRequestParams.getMax() != null) {
                    value = value.stream().filter(v -> v.getGroupTalkCount() <= deptRequestParams.getMax()).collect(Collectors.toList());
                }
                value.stream().map(v -> v.setTalkCount(v.getGroupTalkCount())).collect(Collectors.toList());
            } else {
                // 单呼 & 组呼
                if (deptRequestParams.getMin() != null) {
                    value = value.stream().filter(v -> v.getIndividualTalkCount() >= deptRequestParams.getMin() || v.getGroupTalkCount() >= deptRequestParams.getMin()).collect(Collectors.toList());
//                    value = value.stream().filter(v -> v.getGroupTalkCount() >= deptRequestParams.getMin()).collect(Collectors.toList());
                }
                if (deptRequestParams.getMax() != null) {
                    value = value.stream().filter(v -> v.getIndividualTalkCount() <= deptRequestParams.getMax() || v.getGroupTalkCount() <= deptRequestParams.getMax()).collect(Collectors.toList());
//                    value = value.stream().filter(v -> v.getGroupTalkCount() <= deptRequestParams.getMax()).collect(Collectors.toList());
                }
                value.stream().map(v -> v.setTalkCount(v.getGroupTalkCount() + v.getIndividualTalkCount())).collect(Collectors.toList());
            }
            Map<String, JSONObject> jsonObjects = new HashMap<>();
            value.forEach(v -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("dept_id", v.getDeptUniqueId());
                jsonObject.put("dept_name", v.getDeptName());
                if (deptRequestParams.getType() == 1) {// 单呼
                    jsonObject.put("user_talk_count", v.getIndividualTalkCount());
                } else if (deptRequestParams.getType() == 2) {// 组呼
                    jsonObject.put("group_talk_count", v.getGroupTalkCount());
                } else {
                    jsonObject.put("user_talk_count", v.getIndividualTalkCount());
                    jsonObject.put("group_talk_count", v.getGroupTalkCount());
                }
                jsonObject.put("all_count", v.getTalkCount());
                jsonObjects.put(v.getDeptUniqueId(), jsonObject);
            });
            map.put(date, jsonObjects);
        }
        dateJson.put("data", map);
        return dateJson;
    }


    @Override
    public JSONObject audioDuration(DeptRequestParams deptRequestParams) {
        JSONObject dateJson = this.getDateList(deptRequestParams);
        if (dateJson.get("error") != null) {
            return dateJson;
        }
        Map<String, Map<String, JSONObject>> map = new HashMap<>();
        List<String> dateList = (List<String>) dateJson.get("dateList");
        List<String> uniqueIds = (List<String>) dateJson.get("uniqueIds");

        for (String date : dateList) {
            String uniqueIdIn = uniqueIds.stream().collect(Collectors.joining(","));
            uniqueIdIn = "'" + uniqueIdIn.replaceAll(",", "','") + "'";
            List<StaDayDept> value = deptMapper.selectAudioDurationByUniqueIdAndDate(uniqueIdIn, date);
            if (deptRequestParams.getType() == 1) {// 单呼
                if (deptRequestParams.getMin() != null) {
                    value = value.stream().filter(v -> v.getIndividualTalkDuration() >= deptRequestParams.getMin()).collect(Collectors.toList());
                }
                if (deptRequestParams.getMax() != null) {
                    value = value.stream().filter(v -> v.getIndividualTalkDuration() <= deptRequestParams.getMax()).collect(Collectors.toList());
                }
                value.stream().map(v -> v.setTalkCount(v.getIndividualTalkDuration())).collect(Collectors.toList());
            } else if (deptRequestParams.getType() == 2) {// 组呼
                if (deptRequestParams.getMin() != null) {
                    value = value.stream().filter(v -> v.getGroupTalkDuration() >= deptRequestParams.getMin()).collect(Collectors.toList());
                }
                if (deptRequestParams.getMax() != null) {
                    value = value.stream().filter(v -> v.getGroupTalkDuration() <= deptRequestParams.getMax()).collect(Collectors.toList());
                }
                value.stream().map(v -> v.setTalkCount(v.getGroupTalkDuration())).collect(Collectors.toList());
            } else {
                // 单呼 & 组呼
                if (deptRequestParams.getMin() != null) {
                    value = value.stream().filter(v -> v.getIndividualTalkDuration() >= deptRequestParams.getMin() || v.getGroupTalkDuration() >= deptRequestParams.getMin()).collect(Collectors.toList());
//                    value = value.stream().filter(v -> v.getGroupTalkDuration() >= deptRequestParams.getMin()).collect(Collectors.toList());
                }
                if (deptRequestParams.getMax() != null) {
                    value = value.stream().filter(v -> v.getIndividualTalkDuration() <= deptRequestParams.getMax() || v.getGroupTalkDuration() <= deptRequestParams.getMax()).collect(Collectors.toList());
//                    value = value.stream().filter(v -> v.getGroupTalkDuration() <= deptRequestParams.getMax()).collect(Collectors.toList());
                }
                value.stream().map(v -> v.setTalkDuration(v.getGroupTalkDuration() + v.getIndividualTalkDuration())).collect(Collectors.toList());
            }
            Map<String, JSONObject> jsonObjects = new HashMap<>();
            value.forEach(v -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("dept_id", v.getDeptUniqueId());
                jsonObject.put("dept_name", v.getDeptName());
                if (deptRequestParams.getType() == 1) {// 单呼
                    jsonObject.put("user_talk_duration", v.getIndividualTalkDuration());
                } else if (deptRequestParams.getType() == 2) {// 组呼
                    jsonObject.put("group_talk_duration", v.getGroupTalkDuration());
                } else {
                    jsonObject.put("user_talk_duration", v.getIndividualTalkDuration());
                    jsonObject.put("group_talk_duration", v.getGroupTalkDuration());
                }
                jsonObject.put("all_duration", v.getTalkDuration());
                jsonObjects.put(v.getDeptUniqueId(), jsonObject);
            });
            map.put(date, jsonObjects);
        }
        dateJson.put("data", map);
        return dateJson;
    }

    @Override
    public JSONObject videoCount(DeptRequestParams deptRequestParams) {
        JSONObject dateJson = this.getDateList(deptRequestParams);
        if (dateJson.get("error") != null) {
            return dateJson;
        }
        Map<String, Map<String, JSONObject>> map = new HashMap<>();
        List<String> dateList = (List<String>) dateJson.get("dateList");
        List<String> uniqueIds = (List<String>) dateJson.get("uniqueIds");
        for (String date : dateList) {
            String uniqueIdIn = uniqueIds.stream().collect(Collectors.joining(","));
            uniqueIdIn = "'" + uniqueIdIn.replaceAll(",", "','") + "'";
            List<StaDayDept> value = deptMapper.selectVideoCountByUniqueIdAndDate(uniqueIdIn, date);
            if (deptRequestParams.getType() == 1) {// 视频通话
                if (deptRequestParams.getMin() != null) {
                    value = value.stream().filter(v -> v.getVideoCallCount() >= deptRequestParams.getMin()).collect(Collectors.toList());
                }
                if (deptRequestParams.getMax() != null) {
                    value = value.stream().filter(v -> v.getVideoCallCount() <= deptRequestParams.getMax()).collect(Collectors.toList());
                }
                value.stream().map(v -> v.setTalkCount(v.getVideoCallCount())).collect(Collectors.toList());
            } else if (deptRequestParams.getType() == 2) {// 视频回传
                if (deptRequestParams.getMin() != null) {
                    value = value.stream().filter(v -> v.getVideoUploadCount() >= deptRequestParams.getMin()).collect(Collectors.toList());
                }
                if (deptRequestParams.getMax() != null) {
                    value = value.stream().filter(v -> v.getVideoUploadCount() <= deptRequestParams.getMax()).collect(Collectors.toList());
                }
                value.stream().map(v -> v.setTalkCount(v.getVideoUploadCount())).collect(Collectors.toList());
            } else if (deptRequestParams.getType() == 3){// 视频点名
                if (deptRequestParams.getMin() != null) {
                    value = value.stream().filter(v -> v.getVideoRollcallCount() >= deptRequestParams.getMin()).collect(Collectors.toList());
                }
                if (deptRequestParams.getMax() != null) {
                    value = value.stream().filter(v -> v.getVideoRollcallCount() <= deptRequestParams.getMax()).collect(Collectors.toList());
                }
                value.stream().map(v -> v.setTalkCount(v.getVideoRollcallCount())).collect(Collectors.toList());
            } else if (deptRequestParams.getType() == 4){// 视频会商
                if (deptRequestParams.getMin() != null) {
                    value = value.stream().filter(v -> v.getVideoConfCount() >= deptRequestParams.getMin()).collect(Collectors.toList());
                }
                if (deptRequestParams.getMax() != null) {
                    value = value.stream().filter(v -> v.getVideoConfCount() <= deptRequestParams.getMax()).collect(Collectors.toList());
                }
                value.stream().map(v -> v.setTalkCount(v.getVideoConfCount())).collect(Collectors.toList());
            } else {
                if (deptRequestParams.getMin() != null) {
                    value = value.stream().filter(v -> v.getVideoCallCount() >= deptRequestParams.getMin()
                            || v.getVideoUploadCount() >= deptRequestParams.getMin()
                            || v.getVideoRollcallCount() >= deptRequestParams.getMin()
                            || v.getVideoConfCount() >= deptRequestParams.getMin()).collect(Collectors.toList());
                }
                if (deptRequestParams.getMax() != null) {
                    value = value.stream().filter(v -> v.getVideoCallCount() <= deptRequestParams.getMax()
                            || v.getVideoUploadCount() <= deptRequestParams.getMax()
                            || v.getVideoRollcallCount() <= deptRequestParams.getMax()
                            || v.getVideoConfCount() <= deptRequestParams.getMax()).collect(Collectors.toList());
                }
                value.stream().map(v -> v.setVideoAllCount(v.getVideoCallCount() + v.getVideoUploadCount() + v.getVideoRollcallCount() + v.getVideoConfCount())).collect(Collectors.toList());
            }
            Map<String, JSONObject> jsonObjects = new HashMap<>();
            value.forEach(v -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("dept_id", v.getDeptUniqueId());
                jsonObject.put("dept_name", v.getDeptName());
                if (deptRequestParams.getType() == 1) {// 单呼
                    jsonObject.put("video_call_count", v.getVideoCallCount());
                } else if (deptRequestParams.getType() == 2) {// 组呼
                    jsonObject.put("video_upload_count", v.getVideoUploadCount());
                } else if (deptRequestParams.getType() == 3) {
                    jsonObject.put("video_rollcall_count", v.getVideoRollcallCount());
                } else if (deptRequestParams.getType() == 4) {
                    jsonObject.put("video_conf_count", v.getVideoConfCount());
                } else {
                    jsonObject.put("video_call_count", v.getVideoCallCount());
                    jsonObject.put("video_upload_count", v.getVideoUploadCount());
                    jsonObject.put("video_rollcall_count", v.getVideoRollcallCount());
                    jsonObject.put("video_conf_count", v.getVideoConfCount());
                }
                jsonObject.put("all_count", v.getVideoAllCount());
                jsonObjects.put(v.getDeptUniqueId(), jsonObject);
            });
            map.put(date, jsonObjects);
        }
        dateJson.put("data", map);
        return dateJson;
    }

    @Override
    public JSONObject videoDuration(DeptRequestParams deptRequestParams) {
        JSONObject dateJson = this.getDateList(deptRequestParams);
        if (dateJson.get("error") != null) {
            return dateJson;
        }
        Map<String, Map<String, JSONObject>> map = new HashMap<>();
        List<String> dateList = (List<String>) dateJson.get("dateList");
        List<String> uniqueIds = (List<String>) dateJson.get("uniqueIds");

        for (String date : dateList) {
            Map<String, JSONObject> jsonObjects = new HashMap<>();
            String uniqueIdIn = uniqueIds.stream().collect(Collectors.joining(","));
            uniqueIdIn = "'" + uniqueIdIn.replaceAll(",", "','") + "'";
            List<StaDayDept> value = deptMapper.selectVideoDurationByUniqueIdAndDate(uniqueIdIn, date);

            if (deptRequestParams.getMin() != null) {
                value = value.stream().filter(v -> v.getVideoDuration() >= deptRequestParams.getMin()).collect(Collectors.toList());
            }
            if (deptRequestParams.getMax() != null) {
                value = value.stream().filter(v -> v.getVideoDuration() <= deptRequestParams.getMax()).collect(Collectors.toList());
            }
            value.forEach(v -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("dept_id", v.getDeptUniqueId());
                jsonObject.put("dept_name", v.getDeptName());
                jsonObject.put("all_duration", v.getVideoDuration());
                jsonObjects.put(v.getDeptUniqueId(), jsonObject);
            });
            map.put(date, jsonObjects);
        }
        dateJson.put("data", map);
        return dateJson;
    }

    @Override
    public JSONObject photoCount(DeptRequestParams deptRequestParams) {
        JSONObject dateJson = this.getDateList(deptRequestParams);
        if (dateJson.get("error") != null) {
            return dateJson;
        }
        Map<String, Map<String, JSONObject>> map = new HashMap<>();
        List<String> dateList = (List<String>) dateJson.get("dateList");
        List<String> uniqueIds = (List<String>) dateJson.get("uniqueIds");

        for (String date : dateList) {
            Map<String, JSONObject> jsonObjects = new HashMap<>();
            String uniqueIdIn = uniqueIds.stream().collect(Collectors.joining(","));
            uniqueIdIn = "'" + uniqueIdIn.replaceAll(",", "','") + "'";
            List<StaDayDept> value = deptMapper.selectPhotoCountByUniqueIdAndDate(uniqueIdIn, date);

            if (deptRequestParams.getMin() != null) {
                value = value.stream().filter(v -> v.getPhotoUploadCount() >= deptRequestParams.getMin()).collect(Collectors.toList());
            }
            if (deptRequestParams.getMax() != null) {
                value = value.stream().filter(v -> v.getPhotoUploadCount() <= deptRequestParams.getMax()).collect(Collectors.toList());
            }
            value.forEach(v -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("dept_id", v.getDeptUniqueId());
                jsonObject.put("dept_name", v.getDeptName());
                jsonObject.put("all_count", v.getPhotoUploadCount());
                jsonObjects.put(v.getDeptUniqueId(), jsonObject);
            });
            map.put(date, jsonObjects);
        }
        dateJson.put("data", map);
        return dateJson;
    }

    @Override
    public JSONObject sosCount(DeptRequestParams deptRequestParams) {
        JSONObject dateJson = this.getDateList(deptRequestParams);
        if (dateJson.get("error") != null) {
            return dateJson;
        }
        Map<String, Map<String, JSONObject>> map = new HashMap<>();
        List<String> dateList = (List<String>) dateJson.get("dateList");
        List<String> uniqueIds = (List<String>) dateJson.get("uniqueIds");

        for (String date : dateList) {
            Map<String, JSONObject> jsonObjects = new HashMap<>();
            String uniqueIdIn = uniqueIds.stream().collect(Collectors.joining(","));
            uniqueIdIn = "'" + uniqueIdIn.replaceAll(",", "','") + "'";
            List<StaDayDept> value = deptMapper.selectSosCountByUniqueIdAndDate(uniqueIdIn, date);

            if (deptRequestParams.getMin() != null) {
                value = value.stream().filter(v -> v.getSosCount() >= deptRequestParams.getMin()).collect(Collectors.toList());
            }
            if (deptRequestParams.getMax() != null) {
                value = value.stream().filter(v -> v.getSosCount() <= deptRequestParams.getMax()).collect(Collectors.toList());
            }
            value.forEach(v -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("dept_id", v.getDeptUniqueId());
                jsonObject.put("dept_name", v.getDeptName());
                jsonObject.put("all_count", v.getSosCount());
                jsonObjects.put(v.getDeptUniqueId(), jsonObject);
            });
            map.put(date, jsonObjects);
        }
        dateJson.put("data", map);
        return dateJson;
    }

    @Override
    public JSONObject imCount(DeptRequestParams deptRequestParams) {
        JSONObject dateJson = this.getDateList(deptRequestParams);
        if (dateJson.get("error") != null) {
            return dateJson;
        }
        Map<String, Map<String, JSONObject>> map = new HashMap<>();
        List<String> dateList = (List<String>) dateJson.get("dateList");
        List<String> uniqueIds = (List<String>) dateJson.get("uniqueIds");

        for (String date : dateList) {
            Map<String, JSONObject> jsonObjects = new HashMap<>();
            String uniqueIdIn = uniqueIds.stream().collect(Collectors.joining(","));
            uniqueIdIn = "'" + uniqueIdIn.replaceAll(",", "','") + "'";
            List<StaDayDept> value = deptMapper.selectImCountByUniqueIdAndDate(uniqueIdIn, date);

            if (deptRequestParams.getType() == 1) {// 个人
                if (deptRequestParams.getMin() != null) {
                    value = value.stream().filter(v -> v.getIndividualImCount() >= deptRequestParams.getMin()).collect(Collectors.toList());
                }
                if (deptRequestParams.getMax() != null) {
                    value = value.stream().filter(v -> v.getIndividualImCount() <= deptRequestParams.getMax()).collect(Collectors.toList());
                }
                value.stream().map(v -> v.setImCount(v.getIndividualImCount())).collect(Collectors.toList());
            } else if (deptRequestParams.getType() == 2) {// 群组
                if (deptRequestParams.getMin() != null) {
                    value = value.stream().filter(v -> v.getGroupImCount() >= deptRequestParams.getMin()).collect(Collectors.toList());
                }
                if (deptRequestParams.getMax() != null) {
                    value = value.stream().filter(v -> v.getGroupImCount() <= deptRequestParams.getMax()).collect(Collectors.toList());
                }
                value.stream().map(v -> v.setImCount(v.getGroupImCount())).collect(Collectors.toList());
            } else {
                if (deptRequestParams.getMin() != null) {
                    value = value.stream().filter(v -> v.getGroupImCount() >= deptRequestParams.getMin()
                            || v.getIndividualImCount() >= deptRequestParams.getMin()).collect(Collectors.toList());
                }
                if (deptRequestParams.getMax() != null) {
                    value = value.stream().filter(v -> v.getGroupImCount() <= deptRequestParams.getMax()
                            || v.getIndividualImCount() <= deptRequestParams.getMax()).collect(Collectors.toList());
                }
                value.stream().map(v -> v.setImCount(v.getGroupImCount() + v.getIndividualImCount())).collect(Collectors.toList());
            }
            value.forEach(v -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("dept_id", v.getDeptUniqueId());
                jsonObject.put("dept_name", v.getDeptName());
                jsonObject.put("user_im_count", v.getIndividualImCount());
                jsonObject.put("group_im_count", v.getGroupImCount());
                jsonObject.put("all_count", v.getImCount());
                jsonObjects.put(v.getDeptUniqueId(), jsonObject);
            });
            map.put(date, jsonObjects);
        }
        dateJson.put("data", map);
        return dateJson;
    }

    @Override
    public JSONObject onlineDuration(DeptRequestParams deptRequestParams) {
        JSONObject dateJson = this.getDateList(deptRequestParams);
        if (dateJson.get("error") != null) {
            return dateJson;
        }
        Map<String, Map<String, JSONObject>> map = new HashMap<>();
        List<String> dateList = (List<String>) dateJson.get("dateList");
        List<String> uniqueIds = (List<String>) dateJson.get("uniqueIds");

        for (String date : dateList) {
            Map<String, JSONObject> jsonObjects = new HashMap<>();
            String uniqueIdIn = uniqueIds.stream().collect(Collectors.joining(","));
            uniqueIdIn = "'" + uniqueIdIn.replaceAll(",", "','") + "'";
            List<StaDayDept> value = deptMapper.selectOnlineDurationByUniqueIdAndDate(uniqueIdIn, date);

            if (deptRequestParams.getMin() != null) {
                value = value.stream().filter(v -> v.getOnlineDuration() >= deptRequestParams.getMin()).collect(Collectors.toList());
            }
            if (deptRequestParams.getMax() != null) {
                value = value.stream().filter(v -> v.getOnlineDuration() <= deptRequestParams.getMax()).collect(Collectors.toList());
            }
            value.forEach(v -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("dept_id", v.getDeptUniqueId());
                jsonObject.put("dept_name", v.getDeptName());
                jsonObject.put("all_duration", v.getOnlineDuration());
                jsonObjects.put(v.getDeptUniqueId(), jsonObject);
            });
            map.put(date, jsonObjects);
        }
        dateJson.put("data", map);
        return dateJson;
    }

    @Override
    public JSONObject onpostDuration(DeptRequestParams deptRequestParams) {
        JSONObject dateJson = this.getDateList(deptRequestParams);
        if (dateJson.get("error") != null) {
            return dateJson;
        }
        Map<String, Map<String, JSONObject>> map = new HashMap<>();
        List<String> dateList = (List<String>) dateJson.get("dateList");
        List<String> uniqueIds = (List<String>) dateJson.get("uniqueIds");

        for (String date : dateList) {
            Map<String, JSONObject> jsonObjects = new HashMap<>();
            String uniqueIdIn = uniqueIds.stream().collect(Collectors.joining(","));
            uniqueIdIn = "'" + uniqueIdIn.replaceAll(",", "','") + "'";
            List<StaDayDept> value = deptMapper.selectOnpostDurationByUniqueIdAndDate(uniqueIdIn, date);

            if (deptRequestParams.getMin() != null) {
                value = value.stream().filter(v -> v.getOnpostDuration() >= deptRequestParams.getMin()).collect(Collectors.toList());
            }
            if (deptRequestParams.getMax() != null) {
                value = value.stream().filter(v -> v.getOnpostDuration() <= deptRequestParams.getMax()).collect(Collectors.toList());
            }
            value.forEach(v -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("dept_id", v.getDeptUniqueId());
                jsonObject.put("dept_name", v.getDeptName());
                jsonObject.put("all_duration", v.getOnpostDuration());
                jsonObjects.put(v.getDeptUniqueId(), jsonObject);
            });
            map.put(date, jsonObjects);
        }
        dateJson.put("data", map);
        return dateJson;
    }


    @Override
    public JSONObject mileageCount(DeptRequestParams deptRequestParams) {
        JSONObject dateJson = this.getDateList(deptRequestParams);
        if (dateJson.get("error") != null) {
            return dateJson;
        }
        Map<String, Map<String, JSONObject>> map = new HashMap<>();
        List<String> dateList = (List<String>) dateJson.get("dateList");
        List<String> uniqueIds = (List<String>) dateJson.get("uniqueIds");

        for (String date : dateList) {
            Map<String, JSONObject> jsonObjects = new HashMap<>();
            String uniqueIdIn = uniqueIds.stream().collect(Collectors.joining(","));
            uniqueIdIn = "'" + uniqueIdIn.replaceAll(",", "','") + "'";
            List<StaDayDept> value = deptMapper.selectMileageCountByUniqueIdAndDate(uniqueIdIn, date);

            if (deptRequestParams.getMin() != null) {
                value = value.stream().filter(v -> v.getMileage() >= deptRequestParams.getMin()).collect(Collectors.toList());
            }
            if (deptRequestParams.getMax() != null) {
                value = value.stream().filter(v -> v.getMileage() <= deptRequestParams.getMax()).collect(Collectors.toList());
            }
            value.forEach(v -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("dept_id", v.getDeptUniqueId());
                jsonObject.put("dept_name", v.getDeptName());
                jsonObject.put("all_count", v.getMileage());
                jsonObjects.put(v.getDeptUniqueId(), jsonObject);
            });
            map.put(date, jsonObjects);
        }
        dateJson.put("data", map);
        return dateJson;
    }

    /*@Override
    public JSONObject logonCount(DeptRequestParams deptRequestParams) {
        JSONObject dateJson = this.getDateList(deptRequestParams);
        if (dateJson.get("error") != null) {
            return dateJson;
        }
        Map<String, Map<String, JSONObject>> map = new HashMap<>();
        List<String> dateList = (List<String>) dateJson.get("dateList");
        List<String> uniqueIds = (List<String>) dateJson.get("uniqueIds");

        for (String date : dateList) {
            Map<String, JSONObject> jsonObjects = new HashMap<>();
            String uniqueIdIn = uniqueIds.stream().collect(Collectors.joining(","));
            uniqueIdIn = "'" + uniqueIdIn.replaceAll(",", "','") + "'";
            List<StaDayDept> value = deptMapper.selectLogonCountByUniqueIdAndDate(uniqueIdIn, date);

            if (deptRequestParams.getMin() != null) {
                value = value.stream().filter(v -> v.getTalkCount() >= deptRequestParams.getMin()).collect(Collectors.toList());
            }
            if (deptRequestParams.getMax() != null) {
                value = value.stream().filter(v -> v.getTalkCount() <= deptRequestParams.getMax()).collect(Collectors.toList());
            }
            value.forEach(v -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("dept_id", v.getDeptUniqueId());
                jsonObject.put("dept_name", v.getDeptName());
                jsonObject.put("all_count", v.getTalkCount());
                jsonObjects.put(v.getDeptUniqueId(), jsonObject);
            });
            map.put(date, jsonObjects);
        }
        dateJson.put("data", map);
        return dateJson;
    }*/

    @Override
    public JSONObject tmpGroupCount(DeptRequestParams deptRequestParams) {
        JSONObject dateJson = this.getDateList(deptRequestParams);
        if (dateJson.get("error") != null) {
            return dateJson;
        }
        Map<String, Map<String, JSONObject>> map = new HashMap<>();
        List<String> dateList = (List<String>) dateJson.get("dateList");
        List<String> uniqueIds = (List<String>) dateJson.get("uniqueIds");

        for (String date : dateList) {
            Map<String, JSONObject> jsonObjects = new HashMap<>();
            String uniqueIdIn = uniqueIds.stream().collect(Collectors.joining(","));
            uniqueIdIn = "'" + uniqueIdIn.replaceAll(",", "','") + "'";
            List<StaDayDept> value = deptMapper.selectTmpGroupCountByUniqueIdAndDate(uniqueIdIn, date);

            if (deptRequestParams.getMin() != null) {
                value = value.stream().filter(v -> v.getTmpGroupCount() >= deptRequestParams.getMin()).collect(Collectors.toList());
            }
            if (deptRequestParams.getMax() != null) {
                value = value.stream().filter(v -> v.getTmpGroupCount() <= deptRequestParams.getMax()).collect(Collectors.toList());
            }
            value.forEach(v -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("dept_id", v.getDeptUniqueId());
                jsonObject.put("dept_name", v.getDeptName());
                jsonObject.put("all_count", v.getTmpGroupCount());
                jsonObjects.put(v.getDeptUniqueId(), jsonObject);
            });
            map.put(date, jsonObjects);
        }
        dateJson.put("data", map);
        return dateJson;
    }

    @Override
    public JSONObject currentOnline(DeptRequestParams deptRequestParams) {
        JSONObject dateJson = new JSONObject();
        Map<String, JSONObject> map = new HashMap<>();

        List<String> uniqueIds = new ArrayList<>();

        if (deptRequestParams.getDept_id() != null) {
            if (deptRequestParams.getDept_chain() == 0) {
                uniqueIds.add(deptRequestParams.getDept_id());
            } else {
                String ids = rtvUnitMapper.selectCommonUniqueDown(deptRequestParams.getDept_id());
                uniqueIds.addAll(Arrays.asList(ids.split(",")));
            }
        }
        uniqueIds = uniqueIds.stream().distinct().collect(Collectors.toList());
        String uniqueIdIn = uniqueIds.stream().collect(Collectors.joining(","));
        uniqueIdIn = "'" + uniqueIdIn.replaceAll(",", "','") + "'";

        List<StaDayDept> value = deptMapper.selectCurrentOnlineByUniqueIdAndDate(uniqueIdIn);
        if (deptRequestParams.getMin() != null) {
            value = value.stream().filter(v -> v.getTalkCount() >= deptRequestParams.getMin()).collect(Collectors.toList());
        }
        if (deptRequestParams.getMax() != null) {
            value = value.stream().filter(v -> v.getTalkCount() <= deptRequestParams.getMax()).collect(Collectors.toList());
        }

        value.forEach(v -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("dept_id", v.getDeptUniqueId());
            jsonObject.put("dept_name", v.getDeptName());
            jsonObject.put("all_count", v.getTalkCount());
            map.put(v.getDeptUniqueId(), jsonObject);
        });
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
        List<StaDayDept> list = deptMapper.selectByUniqueId(uniqueId, startTime, entTime);
        PageInfo<StaDayDept> pageInfo = new PageInfo<>(list);

        jsonObject.put("data", list);
        jsonObject.put("total_page", (pageInfo.getTotal() / pageSize) == 0 ? 1 : (pageInfo.getTotal() / pageSize + 1));
        jsonObject.put("total", pageInfo.getTotal());

        return jsonObject;
    }

    @Override
    public JSONObject selectAllMsg(Map<String, Object> requestParams) {
        JSONObject jsonObject = new JSONObject();
        Map<String, Object> paramMap = new HashMap<>();
        List<String> uniqueIds = new ArrayList<>();

        if (requestParams.get("dept_id") != null) {
            if (requestParams.get("dept_chain") == null) {
                String ids = rtvUnitMapper.selectCommonUniqueDown(requestParams.get("dept_id").toString());
                uniqueIds.addAll(Arrays.asList(ids.split(",")));
            } else {
                if (Integer.parseInt(requestParams.get("dept_chain").toString()) == 1) {
                    String ids = rtvUnitMapper.selectCommonUniqueDown(requestParams.get("dept_id").toString());
                    uniqueIds.addAll(Arrays.asList(ids.split(",")));
                } else {
                    uniqueIds.add(requestParams.get("dept_id").toString());
                }
            }
        }
        uniqueIds = uniqueIds.stream().distinct().collect(Collectors.toList());

        paramMap.put("flag", requestParams.get("flag"));
        paramMap.put("deptIdList", uniqueIds);
        paramMap.put("time_level", requestParams.get("time_level"));
        paramMap.put("start_time", requestParams.get("start_time"));
        paramMap.put("end_time", requestParams.get("end_time"));
        paramMap.put("min", requestParams.get("min"));
        paramMap.put("max", requestParams.get("max"));
        paramMap.put("count", requestParams.get("key_count") == null ? requestParams.get("count") : requestParams.get(requestParams.get("key_count").toString()));
        paramMap.put("start", requestParams.get("key_start") == null ? requestParams.get("start") : requestParams.get(requestParams.get("key_start").toString()));
        logger.info("dept requset params:" + JSON.toJSONString(paramMap));
        List<StaDayDept> list = deptMapper.selectAllByCondition(paramMap);
        jsonObject.put("data", list);
        return jsonObject;
    }



}
