package com.lhfeiyu.tech.service.impl;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.metadata.Table;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lhfeiyu.tech.DTO.DataAndTotal;
import com.lhfeiyu.tech.DTO.User;
import com.lhfeiyu.tech.DTO.UserSearchDto;
import com.lhfeiyu.tech.dao.mapper.common.RtvUnitMapper;
import com.lhfeiyu.tech.dao.mapper.common.UserMapper;
import com.lhfeiyu.tech.dao.mapper.logMapper.StaDayUserMapper;
import com.lhfeiyu.tech.dao.po.StaDayUser;
import com.lhfeiyu.tech.exception.ParamErrorException;
import com.lhfeiyu.tech.handler.StyleExcelHandler;
import com.lhfeiyu.tech.service.IUser1Service;
import com.lhfeiyu.tech.tools.DateUtils;
import com.lhfeiyu.tech.tools.JwtTokenUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class User1ServiceImpl implements IUser1Service {

    @Autowired
    private StaDayUserMapper userMapper;
    @Autowired
    private RtvUnitMapper rtvUnitMapper;
    @Autowired
    private UserMapper rtvUserMapper;

    private static Logger logger = LoggerFactory.getLogger(User1ServiceImpl.class);

    private static final ThreadLocal<SimpleDateFormat> ymdhms = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMddHHmmss");
        }
    };

    @Override
    public JSONObject selectAllMsg(Map<String, Object> requestParams) {
        JSONObject jsonObject = new JSONObject();
        DataAndTotal<StaDayUser> result = this.dealCommonParams(requestParams, 100);
        jsonObject.put("data", result.getData());
        jsonObject.put("total", result.getTotal());
        return jsonObject;
    }

    @Override
    public JSONObject audioCount(Map<String, Object> requestParams) {
        JSONObject jsonObject = new JSONObject();
        DataAndTotal<StaDayUser> result = this.dealCommonParams(requestParams, 2);
        jsonObject.put("data", result.getData());
        jsonObject.put("total", result.getTotal());
        return jsonObject;
    }

    @Override
    public JSONObject audioDuration(Map<String, Object> requestParams) {
        JSONObject jsonObject = new JSONObject();
        DataAndTotal<StaDayUser> result = this.dealCommonParams(requestParams, 5);
        jsonObject.put("data", result.getData());
        jsonObject.put("total", result.getTotal());
        return jsonObject;
    }

    @Override
    public JSONObject videoCount(Map<String, Object> requestParams) {
        JSONObject jsonObject = new JSONObject();
        DataAndTotal<StaDayUser> result = this.dealCommonParams(requestParams, 9);
        jsonObject.put("data", result.getData());
        jsonObject.put("total", result.getTotal());
        return jsonObject;
    }

    @Override
    public JSONObject videoDuration(Map<String, Object> requestParams) {
        JSONObject jsonObject = new JSONObject();
        DataAndTotal<StaDayUser> result = this.dealCommonParams(requestParams, 13);
        jsonObject.put("data", result.getData());
        jsonObject.put("total", result.getTotal());
        return jsonObject;
    }

    @Override
    public JSONObject photoCount(Map<String, Object> requestParams) {
        JSONObject jsonObject = new JSONObject();
        DataAndTotal<StaDayUser> result = this.dealCommonParams(requestParams, 15);
        jsonObject.put("data", result.getData());
        jsonObject.put("total", result.getTotal());
        return jsonObject;
    }

    @Override
    public JSONObject sosCount(Map<String, Object> requestParams) {
        JSONObject jsonObject = new JSONObject();
        DataAndTotal<StaDayUser> result = this.dealCommonParams(requestParams, 14);
        jsonObject.put("data", result.getData());
        jsonObject.put("total", result.getTotal());
        return jsonObject;
    }

    @Override
    public JSONObject imCount(Map<String, Object> requestParams) {
        JSONObject jsonObject = new JSONObject();
        DataAndTotal<StaDayUser> result = this.dealCommonParams(requestParams, 16);
        jsonObject.put("data", result.getData());
        jsonObject.put("total", result.getTotal());
        return jsonObject;
    }

    @Override
    public JSONObject onlineDuration(Map<String, Object> requestParams) {
        JSONObject jsonObject = new JSONObject();
        DataAndTotal<StaDayUser> result = this.dealCommonParams(requestParams, 1);
        jsonObject.put("data", result.getData());
        jsonObject.put("total", result.getTotal());
        return jsonObject;
    }

    @Override
    public JSONObject onpostDuration(Map<String, Object> requestParams) {
        JSONObject jsonObject = new JSONObject();
        DataAndTotal<StaDayUser> result = this.dealCommonParams(requestParams, 19);
        jsonObject.put("data", result.getData());
        jsonObject.put("total", result.getTotal());
        return jsonObject;
    }

    @Override
    public JSONObject mileageCount(Map<String, Object> requestParams) {
        JSONObject jsonObject = new JSONObject();
        DataAndTotal<StaDayUser> result = this.dealCommonParams(requestParams, 20);
        jsonObject.put("data", result.getData());
        jsonObject.put("total", result.getTotal());
        return jsonObject;
    }

    @Override
    public JSONObject logonCount(Map<String, Object> requestParams) {
        JSONObject jsonObject = new JSONObject();
        Map<String, Object> paramMap = new HashMap<>();
        List<String> uniqueIds = new ArrayList<>();
        List<String> uids = new ArrayList<>();

        if (requestParams.get("dept_id") != null) {
            if (requestParams.get("dept_chain") == null) {
                String ids = rtvUnitMapper.selectCommonUniqueDown(requestParams.get("dept_id").toString());
                uniqueIds.addAll(Arrays.asList(ids.split(",")));

            } else if (Integer.parseInt(requestParams.get("dept_chain").toString()) == 2) {// 直系部门
                uniqueIds = rtvUnitMapper.selectUniqueIdByParentId(requestParams.get("dept_id").toString());

            } else if (Integer.parseInt(requestParams.get("dept_chain").toString()) == 1) {// 所有下级部门
                String ids = rtvUnitMapper.selectCommonUniqueDown(requestParams.get("dept_id").toString());
                uniqueIds.addAll(Arrays.asList(ids.split(",")));

            } else if (Integer.parseInt(requestParams.get("dept_chain").toString()) == 0) {// 当前部门
                uniqueIds.add(requestParams.get("dept_id").toString());
            }
        } else {
            throw new ParamErrorException("部门ID不能为空");
        }
        uniqueIds = uniqueIds.stream().distinct().collect(Collectors.toList());
        uniqueIds.forEach(v -> v = "'" + v + "'");

        if (requestParams.get("uids") != null) {
            uids = Arrays.asList(requestParams.get("uids").toString().split(","));
        }

        paramMap.put("uidList", uids);
        paramMap.put("deptIdList", uniqueIds);
        paramMap.put("time_level", requestParams.get("time_level"));
        paramMap.put("start_time", requestParams.get("start_time"));
        paramMap.put("end_time", requestParams.get("end_time"));
        paramMap.put("min", requestParams.get("min"));
        paramMap.put("max", requestParams.get("max"));
        paramMap.put("count", requestParams.get("key_count") == null ? requestParams.get("count") : requestParams.get(requestParams.get("key_count").toString()));
        paramMap.put("start", requestParams.get("key_start") == null ? requestParams.get("start") : requestParams.get(requestParams.get("key_start").toString()));
        logger.info("dept requset params:" + JSON.toJSONString(paramMap));

        List<StaDayUser> list = userMapper.selectLogonCountByUniqueIdAndDate(paramMap);
        if (requestParams.get("min") != null) {
            list = list.stream().filter(v -> v.getTalkCount() >= Integer.parseInt(requestParams.get("min").toString())).collect(Collectors.toList());
        }
        if (requestParams.get("max") != null) {
            list = list.stream().filter(v -> v.getTalkCount() <= Integer.parseInt(requestParams.get("max").toString())).collect(Collectors.toList());
        }

        List<User> users = new ArrayList<>();
        // 获取所有的用户id
        String idIn = list.stream().map(v -> v.getUid().toString()).distinct().collect(Collectors.joining(","));
        if (StringUtils.isNotEmpty(idIn)) {
            users = rtvUserMapper.selectByIdIn(idIn);
        }
        Map<Long, User> uidMap = users.stream().collect(Collectors.toMap(v -> v.getId(), v -> v));

        JSONArray array = new JSONArray();
        list.forEach(v -> {
            User user = uidMap.get(v.getUid());
            v.setName(user.getDisplayName())
                    .setDeptUniqueId(user.getDepartmentId())
                    .setDeptName(user.getDeptName());
            JSONObject json = new JSONObject();
            json.put("uid", v.getUid());
            json.put("name", v.getName());
            json.put("dept_id", v.getDeptUniqueId());
            json.put("dept_namedept_name", v.getDeptName());
            json.put("all_count", v.getTalkCount());
            array.add(json);
        });

        jsonObject.put("data", array);
        jsonObject.put("data", list);
        return jsonObject;
    }

    @Override
    public JSONObject tmpGroupCount(Map<String, Object> requestParams) {
        JSONObject jsonObject = new JSONObject();
        DataAndTotal<StaDayUser> result = this.dealCommonParams(requestParams, 21);
        jsonObject.put("data", result.getData());
        jsonObject.put("total", result.getTotal());
        return jsonObject;
    }

    @Override
    public JSONObject selectByUnitId(Map<String, Object> requestParams, HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        Integer pageSize = 0;
        Integer page = 1;
        List<String> uniqueIds = new ArrayList<>();

        if (requestParams.get("dept_id") != null) {
            if (requestParams.get("dept_chain") == null) {
                String ids = rtvUnitMapper.selectCommonUniqueDown(requestParams.get("dept_id").toString());
                uniqueIds.addAll(Arrays.asList(ids.split(",")));

            } else if (Integer.parseInt(requestParams.get("dept_chain").toString()) == 2) {// 直系部门
                uniqueIds = rtvUnitMapper.selectUniqueIdByParentId(requestParams.get("dept_id").toString());

            } else if (Integer.parseInt(requestParams.get("dept_chain").toString()) == 1) {// 所有下级部门
                String ids = rtvUnitMapper.selectCommonUniqueDown(requestParams.get("dept_id").toString());
                uniqueIds.addAll(Arrays.asList(ids.split(",")));

            } else if (Integer.parseInt(requestParams.get("dept_chain").toString()) == 0) {// 当前部门
                uniqueIds.add(requestParams.get("dept_id").toString());
            }
        }
        uniqueIds = uniqueIds.stream().distinct().collect(Collectors.toList());
        String uniqueString = null;
        if (uniqueIds.size() > 0) {
            uniqueString = uniqueIds.stream().collect(Collectors.joining(","));
            uniqueString = "'" + uniqueString.replaceAll(",", "','") + "'";
        }
        if (requestParams.get("key_count") == null) {
            pageSize = Integer.parseInt(requestParams.get("count").toString());
        } else {
            pageSize = Integer.parseInt(requestParams.get(requestParams.get("key_count")).toString());
        }
        String token = request.getHeader("Authorization");
        Integer corpId = JwtTokenUtil.getCorpId(token);

        page = Integer.parseInt(requestParams.get("page").toString());
        PageHelper.startPage(page, pageSize);
        List<User> users = rtvUserMapper.selectByUniqueIdIn(uniqueString, corpId);
        PageInfo<User> pageInfo = new PageInfo<>(users);
        JSONArray array = new JSONArray();
        users.forEach(v -> {
            JSONObject json = new JSONObject();
            json.put("uid", v.getId());
            json.put("name", v.getDisplayName());
            json.put("dept_id", v.getDepartmentId());
            array.add(json);
        });
        jsonObject.put("data", array);
        jsonObject.put("total_page", (pageInfo.getTotal() / pageSize) == 0 ? 1 : (pageInfo.getTotal() / pageSize + 1));
        jsonObject.put("total", pageInfo.getTotal());

        return jsonObject;
    }

    private DataAndTotal<StaDayUser> dealCommonParams (Map<String, Object> requestParams, int flag) {
        DataAndTotal<StaDayUser> result = new DataAndTotal<>();
        Map<String, Object> paramMap = new HashMap<>();
        List<String> uniqueIds = new ArrayList<>();
        List<String> uids = new ArrayList<>();

        if (requestParams.get("dept_id") != null && !"".equals(requestParams.get("dept_id"))) {
            if (requestParams.get("dept_chain") == null && !"".equals(requestParams.get("dept_chain"))) {
                String ids = rtvUnitMapper.selectCommonUniqueDown(requestParams.get("dept_id").toString());
                uniqueIds.addAll(Arrays.asList(ids.split(",")));

            } else if (Integer.parseInt(requestParams.get("dept_chain").toString()) == 2) {// 直系部门
                uniqueIds = rtvUnitMapper.selectUniqueIdByParentId(requestParams.get("dept_id").toString());

            } else if (Integer.parseInt(requestParams.get("dept_chain").toString()) == 1) {// 所有下级部门
                String ids = rtvUnitMapper.selectCommonUniqueDown(requestParams.get("dept_id").toString());
                uniqueIds.addAll(Arrays.asList(ids.split(",")));

            } else if (Integer.parseInt(requestParams.get("dept_chain").toString()) == 0) {// 当前部门
                String[] dept_ids = requestParams.get("dept_id").toString().split(",");
                uniqueIds.addAll(Arrays.asList(dept_ids));
            }
        }
        uniqueIds = uniqueIds.stream().distinct().collect(Collectors.toList());
        uniqueIds.forEach(v -> v = "'" + v + "'");

        if (requestParams.get("uids") != null) {
            uids = Arrays.asList(requestParams.get("uids").toString().split(","));
        }
        paramMap.put("flag", flag);
        paramMap.put("uidList", uids);
        paramMap.put("deptIdList", uniqueIds);
        paramMap.put("time_level", requestParams.get("time_level"));
        paramMap.put("start_time", requestParams.get("start_time"));
        paramMap.put("end_time", requestParams.get("end_time"));
        paramMap.put("min", requestParams.get("min"));
        paramMap.put("max", requestParams.get("max"));
        paramMap.put("count", requestParams.get("key_count") == null ? requestParams.get("count") : requestParams.get(requestParams.get("key_count").toString()));
        paramMap.put("start", requestParams.get("key_start") == null ? requestParams.get("start") : requestParams.get(requestParams.get("key_start").toString()));
        logger.info("user requset params:" + JSON.toJSONString(paramMap));
        List<StaDayUser> list = new ArrayList<>();
        int qty = 0;
        list = userMapper.selectAllByCondition(paramMap);
        qty = userMapper.selectAllByConditionCount(paramMap);
        // 获取所有的用户id
        String idIn = list.stream().map(v -> v.getUid().toString()).distinct().collect(Collectors.joining(","));
        List<User> users = new ArrayList<>();
        if (StringUtils.isNotEmpty(idIn)) {
            users = rtvUserMapper.selectByIdIn(idIn);
        }
        Map<Long, User> uidMap = users.stream().collect(Collectors.toMap(v -> v.getId(), v -> v));

        list.forEach(v -> {
            User user = uidMap.get(v.getUid());
            if (user != null) {
                v.setName(user.getDisplayName())
                        .setDeptUniqueId(user.getDepartmentId())
                        .setDeptName(user.getDeptName());
            }

        });
        result.setData(list);
        result.setTotal(qty);
        return result;
    }


    @Override
    public List<User> select(UserSearchDto userSearchDto) {
        return rtvUserMapper.select(userSearchDto);
    }

    @Override
    public Map<String, Object> userByTime(String uids, String startTime, String endTime, String type) {
        List<String> uidList =
                Arrays.asList(uids.split(","));
        List<String> dates = DateUtils.getDate(startTime, endTime, type);

        Map<String, Object> userMap = new HashMap<>();
        uidList.forEach(v -> {
            Map<String, Object> dateMap = new LinkedHashMap<>();
            dates.forEach(k -> {
                StaDayUser staDayUser = userMapper.selectByUidAndDate(v, k);
                dateMap.put(k, staDayUser == null ? new StaDayUser() : staDayUser);
            });
            userMap.put(v, dateMap);
        });

        return userMap;
    }


    @Override
    public Map<String, Object> deptByTime(String uniqueIds, Integer deptChain, String startTime, String endTime, String type) {
        Map<String, Object> userMap = new HashMap<>();
        List<Integer> uidList = new ArrayList<>();
        List<String> uniqueIdList = new ArrayList<>();
        if (deptChain == 0) {
            uniqueIdList.addAll(Arrays.asList(uniqueIds.split(",")));
        } else if (deptChain == 1) {// 所有下级部门
            if (uniqueIds.indexOf(",") != -1) {
                throw new ParamErrorException("deptChain为0时，uniqueIds 只能传递一个部门！");
            }
            String ids = rtvUnitMapper.selectCommonUniqueDown(uniqueIds);
            uniqueIdList.addAll(Arrays.asList(ids.split(",")));
        } else if (deptChain == 2) {// 直系部门
            if (uniqueIds.indexOf(",") != -1) {
                throw new ParamErrorException("deptChain为0时，uniqueIds 只能传递一个部门！");
            }
            uniqueIdList = rtvUnitMapper.selectUniqueIdByParentId(uniqueIds);
        }
        if (uniqueIdList.size() > 0) {
            String uniqueId = uniqueIdList.stream().collect(Collectors.joining(","));
            uniqueId = "'" + uniqueId.replaceAll(",", "','") + "'";
            uidList = userMapper.selectUidByUniqueIdIn(uniqueId);
        } else {
            return userMap;
        }
        List<String> dates = DateUtils.getDate(startTime, endTime, type);
        if ("ym".equalsIgnoreCase(type)) {
            uidList.forEach(v -> {
                Map<String, Object> dateMap = new LinkedHashMap<>();
                String uidString = v.toString();
                dates.forEach(k -> {
                    StaDayUser staDayUser = userMapper.selectByUidAndDate(uidString, k);
                    dateMap.put(k, staDayUser == null ? new StaDayUser() : staDayUser);
                });
                userMap.put(uidString, dateMap);
            });
        } else {
            List<StaDayUser> staDayUsers = userMapper.selectUserByDateAndUid(uidList, dates);
            List<Long> collect1 = staDayUsers.stream().map(v -> v.getUid()).distinct().collect(Collectors.toList());

            for (Integer uid : uidList) {
                if (collect1.indexOf(uid.longValue()) == -1) {
                    for (String date : dates) {
                        StaDayUser newUser = new StaDayUser();
                        newUser.setUid(uid.longValue());
                        newUser.setTimeYmd(Integer.parseInt(date));
                        staDayUsers.add(newUser);
                    }
                }
            }

            Map<Long, List<StaDayUser>> staDayUserMap = new HashMap<>();
            staDayUsers.forEach(v -> {
                List<StaDayUser> staDayUsers1 = staDayUserMap.get(v.getUid());
                if (staDayUsers1 == null) {
                    staDayUsers1 = new ArrayList<>();
                    staDayUsers1.add(v);
                    staDayUserMap.put(v.getUid(), staDayUsers1);
                } else {
                    staDayUsers1.add(v);
                }
            });
            for (Map.Entry<Long, List<StaDayUser>> longListEntry : staDayUserMap.entrySet()) {
                List<StaDayUser> value = longListEntry.getValue();
                if (value.size() != dates.size()) {
                    Map<Integer, StaDayUser> collect = value.stream().collect(Collectors.toMap(v -> v.getTimeYmd(), v -> v));
                    for (String date : dates) {
                        StaDayUser staDayUser = collect.get(Integer.parseInt(date));
                        if (staDayUser == null) {
                            staDayUser = new StaDayUser();
                            staDayUser.setTimeYmd(Integer.parseInt(date));
                            collect.put(Integer.parseInt(date), staDayUser);
                        }
                    }
                }
            }

            uidList.forEach(v -> {
                List<StaDayUser> userList = staDayUserMap.get(v.longValue());
                if (userList == null) {
                    userList = new ArrayList<>();
                }
                Map<Integer, StaDayUser> collect = userList.stream().collect(Collectors.toMap(k -> k.getTimeYmd(), val -> val));


                Map<Integer, StaDayUser> map1 = new LinkedHashMap<>();
                collect.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .forEachOrdered(x -> map1.put(x.getKey(), x.getValue()));

                userMap.put(v.toString(), map1);
            });
        }
        return userMap;
    }

    @Override
    public void exportExcel(HttpServletRequest request, HttpServletResponse response,String uids,
                            String uniqueIds, Integer deptChain, String startTime, String endTime, String type, String columns) {
        List<String> uniqueIdList = new ArrayList<>();
        if (deptChain == 0) {
            uniqueIdList.addAll(Arrays.asList(uniqueIds.split(",")));
        } else if (deptChain == 1) {// 所有下级部门
            if (uniqueIds.indexOf(",") != -1) {
                throw new ParamErrorException("deptChain为0时，uniqueIds 只能传递一个部门！");
            }
            String ids = rtvUnitMapper.selectCommonUniqueDown(uniqueIds);
            uniqueIdList.addAll(Arrays.asList(ids.split(",")));
        } else if (deptChain == 2) {// 直系部门
            if (uniqueIds.indexOf(",") != -1) {
                throw new ParamErrorException("deptChain为0时，uniqueIds 只能传递一个部门！");
            }
            uniqueIdList = rtvUnitMapper.selectUniqueIdByParentId(uniqueIds);
        }
        List<Integer> list = new ArrayList<>();
        if (uniqueIdList.size() > 0) {
            String uniqueId = uniqueIdList.stream().collect(Collectors.joining(","));
            uniqueId = "'" + uniqueId.replaceAll(",", "','") + "'";
            list = userMapper.selectUidByUniqueIdIn(uniqueId);
        }
        List<String> dates = DateUtils.getDate(startTime, endTime, type);
//        Map<String, List<StaDayUser>> map = new HashMap<>();
        if (StringUtils.isNotEmpty(uids)) {
            List<Integer> uidList = Arrays.asList(uids.split(","))
                    .stream().map(y -> Integer.parseInt(y)).collect(Collectors.toList());
            list.addAll(uidList);
        }
        List<StaDayUser> staDayUsers = new ArrayList<>();
        for (String date : dates) {
            List<StaDayUser> staDayUsers1 = userMapper.selectByUidAndUniqueIdsAndUidsAndDate(null, list, date);
            staDayUsers.addAll(staDayUsers1);
        }
        Map<Long, List<StaDayUser>> map1 = new HashMap<>();

        staDayUsers.forEach(v -> {
            List<StaDayUser> staDayUsers1 = map1.get(v.getUid());
            if (staDayUsers1 == null) {
                staDayUsers1 = new ArrayList<>();
                staDayUsers1.add(v);
                map1.put(v.getUid(), staDayUsers1);
            } else {
                staDayUsers1.add(v);
            }
        });

        List<StaDayUser> excelParams = new ArrayList<>();
        for (Long aLong : map1.keySet()) {
            StaDayUser staDayUser = new StaDayUser();
            List<StaDayUser> staDayUsers1 = map1.get(aLong);
            staDayUser.setUid(aLong);
            staDayUsers1.forEach(v -> {
                staDayUser.setName(v.getName())
                        .setDeptUniqueId(v.getDeptUniqueId())
                        .setDeptName(v.getDeptName())
                        .setOnlineDuration(staDayUser.getOnlineDuration() + v.getOnlineDuration())
                        .setTalkCount(staDayUser.getTalkCount() + v.getTalkCount())
                        .setIndividualTalkCount(staDayUser.getIndividualTalkCount() + v.getIndividualTalkCount())
                        .setGroupTalkCount(staDayUser.getGroupTalkCount() + v.getGroupTalkCount())
                        .setTalkDuration(staDayUser.getTalkDuration() + v.getTalkDuration())
                        .setIndividualTalkDuration(staDayUser.getIndividualTalkDuration() + v.getIndividualTalkDuration())
                        .setGroupTalkDuration(staDayUser.getGroupTalkDuration() + v.getGroupTalkDuration())
                        .setVideoAllCount(staDayUser.getVideoAllCount() + v.getVideoAllCount())
                        .setVideoCallCount(staDayUser.getVideoCallCount() + v.getVideoCallCount())
                        .setVideoUploadCount(staDayUser.getVideoUploadCount() + v.getVideoUploadCount())
                        .setVideoRollcallCount(staDayUser.getVideoRollcallCount() + v.getVideoRollcallCount())
                        .setVideoConfCount(staDayUser.getVideoConfCount() + v.getVideoConfCount())
                        .setVideoDuration(staDayUser.getVideoDuration() + v.getVideoDuration())
                        .setSosCount(staDayUser.getSosCount() + v.getSosCount())
                        .setPhotoUploadCount(staDayUser.getPhotoUploadCount() + v.getPhotoUploadCount())
                        .setImCount(staDayUser.getImCount() + v.getImCount())
                        .setIndividualImCount(staDayUser.getIndividualImCount() + v.getIndividualImCount())
                        .setGroupImCount(staDayUser.getGroupImCount() + v.getGroupImCount())
                        .setOnpostDuration(staDayUser.getOnpostDuration() + v.getOnpostDuration())
                        .setMileage(staDayUser.getMileage() + v.getMileage())
                        .setTmpGroupCount(staDayUser.getTmpGroupCount() + v.getTmpGroupCount())
                        .setTmpGroupFileCount(staDayUser.getTmpGroupFileCount() + v.getTmpGroupFileCount());

            });
            excelParams.add(staDayUser);
        }

        if (excelParams.size() > 0) {
            DecimalFormat df = new DecimalFormat("0.000");//格式化小数
            for (StaDayUser excelParam : excelParams) {
                excelParam.setOnlineDurationH(df.format((float) excelParam.getOnlineDuration() / 3600));// 秒转小时
                excelParam.setTalkDurationH(df.format((float) excelParam.getTalkDuration() / 3600));
                excelParam.setIndividualTalkDurationH(df.format((float) excelParam.getIndividualTalkDuration() / 3600));
                excelParam.setGroupTalkDurationH(df.format((float) excelParam.getGroupTalkDuration() / 3600));
                excelParam.setVideoDurationH(df.format((float) excelParam.getVideoDuration() / 3600));
                excelParam.setOnpostDurationH(df.format((float) excelParam.getOnpostDuration() / 3600));
                excelParam.setMileageKm(df.format((float) excelParam.getMileage() / 1000));
            }
        }




        //创建excel
        ExcelWriter excelWriter = null;
        OutputStream outputStream = null;

        try {
            //添加响应头信息
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 设置响应头
            response.setHeader("Content-disposition", "attachment; filename="
                    + URLEncoder.encode("用户信息汇总", "UTF-8")
                    + "_" + startTime + "_" + endTime + ".xls");


            outputStream = response.getOutputStream();
            StyleExcelHandler styleExcelHandler = new StyleExcelHandler();
            excelWriter = new ExcelWriter(null, outputStream, ExcelTypeEnum.XLS, true, styleExcelHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<List<String>> headList = new ArrayList<List<String>>();
        List<String> list2 = Arrays.asList(columns.split(","));
        list2.forEach(v -> {
            List<String> headTitle0 = new ArrayList<String>();
            switch (v) {
                case "name":
                    headTitle0.add("人员姓名");
                    break;
                case "deptName":
                    headTitle0.add("部门名称");
                    break;
                case "onlineDuration":
                    headTitle0.add("在线时长(单位：小时)");
                    break;
                case "talkCount":
                    headTitle0.add("通话次数(单呼+组呼)");
                    break;
                case "individualTalkCount":
                    headTitle0.add("单呼通话次数");
                    break;
                case "groupTalkCount":
                    headTitle0.add("组呼通话次数");
                    break;
                case "talkDuration":
                    headTitle0.add("通话时间(单位：小时)");
                    break;
                case "individualTalkDuration":
                    headTitle0.add("单呼通话时长(单位：小时)");
                    break;
                case "groupTalkDuration":
                    headTitle0.add("组呼通话时长(单位：小时)");
                    break;
                case "videoAllCount":
                    headTitle0.add("所有视频业务次数");
                    break;
                case "videoCallCount":
                    headTitle0.add("视频通话次数");
                    break;
                case "videoUploadCount":
                    headTitle0.add("视频上传次数");
                    break;
                case "videoRollcallCount":
                    headTitle0.add("视频点名次数");
                    break;
                case "videoConfCount":
                    headTitle0.add("视频会商次数");
                    break;
                case "videoDuration":
                    headTitle0.add("视频通话时长(单位：小时)");
                    break;
                case "sosCount":
                    headTitle0.add("一键报警次数");
                    break;
                case "photoUploadCount":
                    headTitle0.add("照片回传次数");
                    break;
                case "imCount":
                    headTitle0.add("即时消息次数");
                    break;
                case "individualImCount":
                    headTitle0.add("个人即时消息次数");
                    break;
                case "groupImCount":
                    headTitle0.add("群组即时消息次数");
                    break;
                case "onpostDuration":
                    headTitle0.add("在岗时长(单位：小时)");
                    break;
                case "mileage":
                    headTitle0.add("里程数(单位：千米)");
                    break;
                case "tmpGroupCount":
                    headTitle0.add("临时组总数");
                    break;

            }
            headList.add(headTitle0);
        });
//        for (int i = 0; i < dates.size(); i++) {
        // 所有行的集合
        List<List<Object>> listAll = new ArrayList<List<Object>>();
        // 创建表单
        Sheet sheet = new Sheet(1, 1);
        sheet.setSheetName(startTime + "_" + endTime);
        // 创建表格
        Table table = new Table(1);
        table.setHead(headList);
        for (StaDayUser staDayDept : excelParams) {
            List<Object> row = new ArrayList<Object>();
            list2.forEach(v -> {
                switch (v) {
                    case "name":
                        row.add(staDayDept.getName());
                        break;
                    case "deptName":
                        row.add(staDayDept.getDeptName());
                        break;
                    case "onlineDuration":
                        row.add(staDayDept.getOnlineDurationH());
                        break;
                    case "talkCount":
                        row.add(staDayDept.getTalkCount());
                        break;
                    case "individualTalkCount":
                        row.add(staDayDept.getIndividualTalkCount());
                        break;
                    case "groupTalkCount":
                        row.add(staDayDept.getGroupTalkCount());
                        break;
                    case "talkDuration":
                        row.add(staDayDept.getTalkDurationH());
                        break;
                    case "individualTalkDuration":
                        row.add(staDayDept.getIndividualTalkDurationH());
                        break;
                    case "groupTalkDuration":
                        row.add(staDayDept.getGroupTalkDurationH());
                        break;
                    case "videoAllCount":
                        row.add(staDayDept.getVideoAllCount());
                        break;
                    case "videoCallCount":
                        row.add(staDayDept.getVideoCallCount());
                        break;
                    case "videoUploadCount":
                        row.add(staDayDept.getVideoUploadCount());
                        break;
                    case "videoRollcallCount":
                        row.add(staDayDept.getVideoCallCount());
                        break;
                    case "videoConfCount":
                        row.add(staDayDept.getVideoConfCount());
                        break;
                    case "videoDuration":
                        row.add(staDayDept.getVideoDurationH());
                        break;
                    case "sosCount":
                        row.add(staDayDept.getSosCount());
                        break;
                    case "photoUploadCount":
                        row.add(staDayDept.getPhotoUploadCount());
                        break;
                    case "imCount":
                        row.add(staDayDept.getImCount());
                        break;
                    case "individualImCount":
                        row.add(staDayDept.getIndividualImCount());
                        break;
                    case "groupImCount":
                        row.add(staDayDept.getImCount());
                        break;
                    case "onpostDuration":
                        row.add(staDayDept.getOnpostDurationH());
                        break;
                    case "mileage":
                        row.add(staDayDept.getMileageKm());
                        break;
                    case "tmpGroupCount":
                        row.add(staDayDept.getTmpGroupCount());
                        break;

                }
            });
            listAll.add(row);
        }
        excelWriter.write1(listAll,sheet,table);
//        }
        excelWriter.finish();
    }
}
