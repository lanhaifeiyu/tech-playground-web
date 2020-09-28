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
import com.zom.statistics.DTO.DataAndTotal;
import com.zom.statistics.DTO.InterfaceDoc.DeptRequestParams;
import com.zom.statistics.DTO.RtvUnit;
import com.zom.statistics.DTO.UnitSearchDto;
import com.zom.statistics.dao.mapper.common.RtvUnitMapper;
import com.zom.statistics.dao.mapper.logMapper.StaDayDeptMapper;
import com.zom.statistics.dao.po.StaDayDept;
import com.zom.statistics.exception.ParamErrorException;
import com.zom.statistics.handler.StyleExcelHandler;
import com.zom.statistics.service.IUnit1Service;
import com.zom.statistics.tools.*;
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
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class Unit1ServiceImpl implements IUnit1Service {

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

    private static final ThreadLocal<SimpleDateFormat> ymdhms = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMddHHmmss");
        }
    };

    private static Logger logger = LoggerFactory.getLogger(UnitServiceImpl.class);

    @Override
    public JSONObject selectAllMsg(Map<String, Object> requestParams) {
        JSONObject jsonObject = new JSONObject();
        DataAndTotal<StaDayDept> result = this.dealCommonParams(requestParams, 100);
        jsonObject.put("data", result.getData());
        jsonObject.put("total", result.getTotal());
        return jsonObject;
    }


    /**
     * 查询下一级
     *
     * @param requestParam
     * @return
     */
    @Override
    public JSONObject subAll(Map<String, Object> requestParam) {
        JSONObject jsonObject = new JSONObject();
        DataAndTotal<StaDayDept> result = this.dealCommonParams(requestParam, 100);
        jsonObject.put("data", result.getData());
        jsonObject.put("total", result.getTotal());
        return jsonObject;
    }

    /**
     * 语音通话次数
     *
     * @param requestParams
     * @return
     */
    @Override
    public JSONObject audioCount(Map<String, Object> requestParams) {
        JSONObject jsonObject = new JSONObject();
        DataAndTotal<StaDayDept> result = this.dealCommonParams(requestParams, 2);
        jsonObject.put("data", result.getData());
        jsonObject.put("total", result.getTotal());
        return jsonObject;
    }


    /**
     * 语音通话时长
     *
     * @param requestParams
     * @return
     */
    @Override
    public JSONObject audioDuration(Map<String, Object> requestParams) {
        JSONObject jsonObject = new JSONObject();
        DataAndTotal<StaDayDept> result = this.dealCommonParams(requestParams, 5);
        jsonObject.put("data", result.getData());
        jsonObject.put("total", result.getTotal());
        return jsonObject;
    }

    /**
     * 视频通话次数
     *
     * @param requestParams
     * @return
     */
    @Override
    public JSONObject videoCount(Map<String, Object> requestParams) {
        JSONObject jsonObject = new JSONObject();
        DataAndTotal<StaDayDept> result = this.dealCommonParams(requestParams, 9);
        jsonObject.put("data", result.getData());
        jsonObject.put("total", result.getTotal());
        return jsonObject;
    }

    /**
     * 视频通话时长
     *
     * @param requestParams
     * @return
     */
    @Override
    public JSONObject videoDuration(Map<String, Object> requestParams) {
        JSONObject jsonObject = new JSONObject();
        DataAndTotal<StaDayDept> result = this.dealCommonParams(requestParams, 13);
        jsonObject.put("data", result.getData());
        jsonObject.put("total", result.getTotal());
        return jsonObject;
    }

    /**
     * 照片回传次数
     *
     * @param requestParams
     * @return
     */
    @Override
    public JSONObject photoCount(Map<String, Object> requestParams) {
        JSONObject jsonObject = new JSONObject();
        DataAndTotal<StaDayDept> result = this.dealCommonParams(requestParams, 15);
        jsonObject.put("data", result.getData());
        jsonObject.put("total", result.getTotal());
        return jsonObject;
    }

    /**
     * 一键告警次数
     *
     * @param requestParams
     * @return
     */
    @Override
    public JSONObject sosCount(Map<String, Object> requestParams) {
        JSONObject jsonObject = new JSONObject();
        DataAndTotal<StaDayDept> result = this.dealCommonParams(requestParams, 14);
        jsonObject.put("data", result.getData());
        jsonObject.put("total", result.getTotal());
        return jsonObject;
    }

    /**
     * 即时通话次数
     *
     * @param requestParams
     * @return
     */
    @Override
    public JSONObject imCount(Map<String, Object> requestParams) {
        JSONObject jsonObject = new JSONObject();
        DataAndTotal<StaDayDept> result = this.dealCommonParams(requestParams, 16);
        jsonObject.put("data", result.getData());
        jsonObject.put("total", result.getTotal());
        return jsonObject;
    }

    /**
     * 在线时长
     *
     * @param requestParams
     * @return
     */
    @Override
    public JSONObject onlineDuration(Map<String, Object> requestParams) {
        JSONObject jsonObject = new JSONObject();
        DataAndTotal<StaDayDept> result = this.dealCommonParams(requestParams, 1);
        jsonObject.put("data", result.getData());
        jsonObject.put("total", result.getTotal());
        return jsonObject;
    }

    /**
     * 在岗时长
     *
     * @param requestParams
     * @return
     */
    @Override
    public JSONObject onpostDuration(Map<String, Object> requestParams) {
        JSONObject jsonObject = new JSONObject();
        DataAndTotal<StaDayDept> result = this.dealCommonParams(requestParams, 19);
        jsonObject.put("data", result.getData());
        jsonObject.put("total", result.getTotal());
        return jsonObject;
    }

    /**
     * 里程数
     *
     * @param requestParams
     * @return
     */
    @Override
    public JSONObject mileageCount(Map<String, Object> requestParams) {
        JSONObject jsonObject = new JSONObject();
        DataAndTotal<StaDayDept> result = this.dealCommonParams(requestParams, 20);
        jsonObject.put("data", result.getData());
        jsonObject.put("total", result.getTotal());
        return jsonObject;
    }

    /**
     * 登录次数
     *
     * @param requestParams
     * @return
     */
    @Override
    public JSONObject logonCount(Map<String, Object> requestParams) {
        JSONObject jsonObject = new JSONObject();
        Map<String, Object> paramMap = new HashMap<>();
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
        } else {
            throw new ParamErrorException("部门ID不能为空");
        }
        uniqueIds = uniqueIds.stream().distinct().collect(Collectors.toList());
        uniqueIds.forEach(v -> v = "'" + v + "'");

        paramMap.put("deptIdList", uniqueIds);
        paramMap.put("time_level", requestParams.get("time_level"));
        paramMap.put("start_time", requestParams.get("start_time"));
        paramMap.put("end_time", requestParams.get("end_time"));
        paramMap.put("min", requestParams.get("min"));
        paramMap.put("max", requestParams.get("max"));
        paramMap.put("count", requestParams.get("key_count") == null ? requestParams.get("count") : requestParams.get(requestParams.get("key_count").toString()));
        paramMap.put("start", requestParams.get("key_start") == null ? requestParams.get("start") : requestParams.get(requestParams.get("key_start").toString()));
        logger.info("dept requset params:" + JSON.toJSONString(paramMap));

        List<StaDayDept> list = deptMapper.selectLogonCountByUniqueIdAndDate(paramMap);
        if (requestParams.get("min") != null) {
            list = list.stream().filter(v -> v.getTalkCount() >= Integer.parseInt(requestParams.get("min").toString())).collect(Collectors.toList());
        }
        if (requestParams.get("max") != null) {
            list = list.stream().filter(v -> v.getTalkCount() <= Integer.parseInt(requestParams.get("max").toString())).collect(Collectors.toList());
        }
        // 获取dept的名字
        String uniques = list.stream().map(StaDayDept::getDeptUniqueId).distinct().collect(Collectors.joining(","));
        List<RtvUnit> rtvUnits = new ArrayList<>();
        if (StringUtils.isNotEmpty(uniques)) {
            rtvUnits = rtvUnitMapper.selectNameByUniqueIn(uniques);
        }
        Map<String, String> uniqueNameMap = rtvUnits.stream().collect(Collectors.toMap(v -> v.getUniqueId(), v -> v.getName()));
        list.forEach(v -> {
            String uniqueName = uniqueNameMap.get(v.getDeptUniqueId());
            if (StringUtils.isNotEmpty(uniqueName)) {
                v.setDeptName(uniqueName);
            }
        });

        JSONArray array = new JSONArray();
        list.forEach(v -> {
            JSONObject json = new JSONObject();
            json.put("dept_id", v.getDeptUniqueId());
            json.put("dept_namedept_name", v.getDeptName());
            json.put("all_count", v.getTalkCount());
            array.add(json);
        });
        jsonObject.put("data", array);
        return jsonObject;
    }

    /**
     * 临时组创建次数次数
     *
     * @param requestParams
     * @return
     */
    @Override
    public JSONObject tmpGroupCount(Map<String, Object> requestParams) {
        JSONObject jsonObject = new JSONObject();
        DataAndTotal<StaDayDept> result = this.dealCommonParams(requestParams, 21);
        jsonObject.put("data", result.getData());
        jsonObject.put("total", result.getTotal());
        return jsonObject;
    }

    /**
     * 当前在线人数
     *
     * @param deptRequestParams
     * @return
     */
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
    public int onlineCount(String uniqueId) {
        List<RtvUnit> rtvUnits = rtvUnitMapper.selectByUniqueIn(uniqueId);
        if (rtvUnits.size() == 0) {
            throw new ParamErrorException("部门不存在!");
        }
        String ids = rtvUnitMapper.selectCommonUniqueDown(uniqueId);
        List<String> list = Arrays.asList(ids.split(","));
        if (list.size() == 0)
            return 0;
        int i = deptMapper.selectUnitOnlineAll(list);
        return i;
    }

    @Override
    public JSONObject selectByUnitId(HttpServletRequest request) {
        Map<String, Object> requestParams = ActionUtil.getRequeseParams(request);
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
        } else {
            String token = request.getHeader("Authorization");
            Integer corpId = JwtTokenUtil.getCorpId(token);
            uniqueIds = rtvUnitMapper.selectUniqueIdByCorpId(corpId);
        }
        String uniqueIdIn = uniqueIds.stream().distinct().collect(Collectors.joining(","));
        uniqueIdIn = "'" + uniqueIdIn.replaceAll(",", "','") + "'";

        if (requestParams.get("key_count") == null) {
            pageSize = Integer.parseInt(requestParams.get("count").toString());
        } else {
            pageSize = Integer.parseInt(requestParams.get(requestParams.get("key_count")).toString());
        }

        page = Integer.parseInt(requestParams.get("page").toString());

        PageHelper.startPage(page, pageSize);
        List<RtvUnit> rtvUnits = rtvUnitMapper.selectByUniqueIn(uniqueIdIn);
        PageInfo<RtvUnit> pageInfo = new PageInfo<>(rtvUnits);

        JSONArray array = new JSONArray();
        rtvUnits.forEach(v -> {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("dept_id", v.getUniqueId());
            jsonObject1.put("dept_name", v.getName());
            jsonObject1.put("parent_id", v.getParentId());
            array.add(jsonObject1);
        });

        jsonObject.put("data", array);
        jsonObject.put("total_page", (pageInfo.getTotal() / pageSize) == 0 ? 1 : (pageInfo.getTotal() / pageSize + 1));
        jsonObject.put("total", pageInfo.getTotal());

        return jsonObject;
    }

    private DataAndTotal<StaDayDept> dealCommonParams(Map<String, Object> requestParams, int flag) {
        DataAndTotal<StaDayDept> result = new DataAndTotal<>();
        Map<String, Object> paramMap = new HashMap<>();
        List<String> uniqueIds = new ArrayList<>();

        Integer deptChain = Integer.parseInt(requestParams.get("dept_chain").toString());

        if (requestParams.get("dept_id") != null) {
            if (requestParams.get("dept_chain") == null) {
                String ids = rtvUnitMapper.selectCommonUniqueDown(requestParams.get("dept_id").toString());
                uniqueIds.addAll(Arrays.asList(ids.split(",")));

            } else if (deptChain == 2) {// 直系部门
                uniqueIds = rtvUnitMapper.selectUniqueIdByParentId(requestParams.get("dept_id").toString());

            } else if (deptChain == 1) {// 所有下级部门
                String ids = rtvUnitMapper.selectCommonUniqueDown(requestParams.get("dept_id").toString());
                uniqueIds.addAll(Arrays.asList(ids.split(",")));

            } else if (deptChain == 0) {// 当前部门
                String[] dept_ids = requestParams.get("dept_id").toString().split(",");
                uniqueIds.addAll(Arrays.asList(dept_ids));
            }
        } else {
            throw new ParamErrorException("部门ID不能为空");
        }
        uniqueIds = uniqueIds.stream().distinct().collect(Collectors.toList());
        uniqueIds.forEach(v -> v = "'" + v + "'");

        paramMap.put("flag", flag);
        paramMap.put("deptIdList", uniqueIds);
        paramMap.put("time_level", requestParams.get("time_level"));
        paramMap.put("start_time", requestParams.get("start_time"));
        paramMap.put("end_time", requestParams.get("end_time"));
        paramMap.put("min", requestParams.get("min"));
        paramMap.put("max", requestParams.get("max"));
        paramMap.put("count", requestParams.get("key_count") == null ? requestParams.get("count") : requestParams.get(requestParams.get("key_count").toString()));
        paramMap.put("start", requestParams.get("key_start") == null ? requestParams.get("start") : requestParams.get(requestParams.get("key_start").toString()));
        logger.info("dept requset params:" + JSON.toJSONString(paramMap));
        List<StaDayDept> list = new ArrayList<>();
        int qty = 0;
        if ((deptChain == 2)) {
            if ((uniqueIds.size() > 0)) {
                list = deptMapper.selectAllByCondition(paramMap);
                qty = deptMapper.selectAllByConditionCount(paramMap);
            }
        } else {
            list = deptMapper.selectAllByCondition(paramMap);
            qty = deptMapper.selectAllByConditionCount(paramMap);
        }

        // 获取所有部门的uniqueid，来查询部门名，通过uniqueId作为键值，获取对应的名字，并设置
        String uniques = list.stream().map(StaDayDept::getDeptUniqueId).distinct().collect(Collectors.joining(","));
        uniques = "'" + uniques.replaceAll(",", "','") + "'";
        if (StringUtils.isNotEmpty(uniques)) {
            List<RtvUnit> rtvUnits = rtvUnitMapper.selectNameByUniqueIn(uniques);
            Map<String, String> uniqueNameMap = rtvUnits.stream().collect(Collectors.toMap(v -> v.getUniqueId(), v -> v.getName()));
            list.forEach(v -> {
                String uniqueName = uniqueNameMap.get(v.getDeptUniqueId());
                if (StringUtils.isNotEmpty(uniqueName)) {
                    v.setDeptName(uniqueName);
                }
            });
        }
        result.setData(list)
                .setTotal(qty);
        return result;
    }

    @Override
    public List<RtvUnit> selectLike(UnitSearchDto unitSearchDto) {
        return rtvUnitMapper.selectLike(unitSearchDto);
    }

    /**
     * 查询部门按照日期格式显示数据
     * @param uniqueIds
     * @param deptChain
     * @param startTime
     * @param endTime
     * @param type
     * @return
     */
    @Override
    public Map<String, Object> deptByTime(String uniqueIds, Integer deptChain, String startTime, String endTime, String type) {
        if (deptChain == null) {
            throw new ParamErrorException("deptChin 参数未传递！");
        }
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
        List<String> dates = DateUtils.getDate(startTime, endTime, type);


        Map<String, Object> uniqueMap = new HashMap<>();
        if ("ym".equalsIgnoreCase(type)) {
            uniqueIdList.forEach(v -> {
                Map<String, Object> dateMap = new LinkedHashMap<>();
                for (String date : dates) {
                    StaDayDept result = deptMapper.selectByDateAndUniqueId(v, date);
                    dateMap.put(date, result == null ? new StaDayDept() : result);
                }
                uniqueMap.put(v, dateMap);
            });
        } else {
            List<StaDayDept> staDayUsers = deptMapper.selectByUniqueIdInAndDates(uniqueIdList, dates);
            List<String> collect1 = staDayUsers.stream().map(v -> v.getDeptUniqueId()).distinct().collect(Collectors.toList());

            for (String uid : uniqueIdList) {
                if (collect1.indexOf(uid) == -1) {
                    for (String date : dates) {
                        StaDayDept newUser = new StaDayDept();
                        newUser.setDeptUniqueId(uid);
                        newUser.setTimeYmd(Integer.parseInt(date));
                        staDayUsers.add(newUser);
                    }
                }
            }

            Map<String, List<StaDayDept>> staDayUserMap = new HashMap<>();
            staDayUsers.forEach(v -> {
                List<StaDayDept> staDayUsers1 = staDayUserMap.get(v.getDeptUniqueId());
                if (staDayUsers1 == null) {
                    staDayUsers1 = new ArrayList<>();
                    staDayUsers1.add(v);
                    staDayUserMap.put(v.getDeptUniqueId(), staDayUsers1);
                } else {
                    staDayUsers1.add(v);
                }
            });
            for (Map.Entry<String, List<StaDayDept>> longListEntry : staDayUserMap.entrySet()) {
                List<StaDayDept> value = longListEntry.getValue();
                if (value.size() != dates.size()) {
                    Map<Integer, StaDayDept> collect = value.stream().collect(Collectors.toMap(v -> v.getTimeYmd(), v -> v));
                    for (String date : dates) {
                        StaDayDept staDayUser = collect.get(Integer.parseInt(date));
                        if (staDayUser == null) {
                            staDayUser = new StaDayDept();
                            staDayUser.setTimeYmd(Integer.parseInt(date));
                            collect.put(Integer.parseInt(date), staDayUser);
                        }
                    }
                }
            }

            uniqueIdList.forEach(v -> {
                List<StaDayDept> userList = staDayUserMap.get(v);
                if (userList == null) {
                    userList = new ArrayList<>();
                }
                Map<Integer, StaDayDept> collect = userList.stream().collect(Collectors.toMap(k -> k.getTimeYmd(), val -> val));


                Map<Integer, StaDayDept> map1 = new LinkedHashMap<>();
                collect.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .forEachOrdered(x -> map1.put(x.getKey(), x.getValue()));

                uniqueMap.put(v, map1);
            });
        }

        return uniqueMap;
    }

    /**
     * 按部门导出excel
     * @param request
     * @param response
     * @param uniqueIds
     * @param deptChain
     * @param startTime
     * @param endTime
     * @param type
     */
    @Override
    public void exportExcel(HttpServletRequest request, HttpServletResponse response,
                            String uniqueIds, Integer deptChain, String startTime, String endTime, String type, String columns) {
        if (deptChain == null) {
            throw new ParamErrorException("deptChin 参数未传递！");
        }
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

        List<String> dates = DateUtils.getDate(startTime, endTime, type);
//        Map<String, List<StaDayDept>> dateMap = new HashMap<>();
        String uniqueString = uniqueIdList.stream().collect(Collectors.joining(","));
        uniqueString = "'" + uniqueString.replaceAll(",", "','") + "'";
        String finalUniqueString = uniqueString;
        List<StaDayDept> listAll = new ArrayList<>();
        if (StringUtils.isNotEmpty(finalUniqueString)) {
            dates.forEach(v -> {
                List<StaDayDept> list = deptMapper.selectByUniqueIdAndDate(finalUniqueString, v);
                listAll.addAll(list);
            });
        }
        Map<String, List<StaDayDept>> map1 = new HashMap<>();

        listAll.forEach(v -> {
            List<StaDayDept> list = map1.get(v.getDeptUniqueId());
            if (list != null) {
                list.add(v);
            } else {
                list = new ArrayList<>();
                list.add(v);
                map1.put(v.getDeptUniqueId(), list);
            }
        });


        List<StaDayDept> excelParams = new ArrayList<>();

        for (String uniqueId : map1.keySet()) {
            StaDayDept staDayUser = new StaDayDept();
            List<StaDayDept> list = map1.get(uniqueId);
            staDayUser.setDeptUniqueId(uniqueId);
            list.forEach(v -> {
                staDayUser.setDeptName(v.getDeptName())
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
            for (StaDayDept excelParam : excelParams) {
                excelParam.setOnlineDurationH(df.format((float) excelParam.getOnlineDuration() / 3600));// 秒转小时
                excelParam.setTalkDurationH(df.format((float) excelParam.getTalkDuration() / 3600));
                excelParam.setIndividualTalkDurationH(df.format((float) excelParam.getIndividualTalkDuration() / 3600));
                excelParam.setGroupTalkDurationH(df.format((float) excelParam.getGroupTalkDuration() / 3600));
                excelParam.setVideoDurationH(df.format((float) excelParam.getVideoDuration() / 3600));
                excelParam.setOnpostDurationH(df.format((float) excelParam.getOnpostDuration() / 3600));
                excelParam.setMileageKm(df.format((float) excelParam.getMileage() / 1000));
            }
        }

        ExcelWriter excelWriter = null;
        OutputStream outputStream = null;

        try {
            //添加响应头信息
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 设置响应头
            response.setHeader("Content-disposition", "attachment; filename="
                    + URLEncoder.encode("部门信息汇总", "UTF-8")
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
        List<List<Object>> list = new ArrayList<List<Object>>();
            // 创建表单
        Sheet sheet = new Sheet(1, 1);
        sheet.setSheetName(startTime + "_" + endTime);
            // 创建表格
        Table table = new Table(1);
        table.setHead(headList);
        for (StaDayDept staDayDept : excelParams) {
            List<Object> row = new ArrayList<Object>();
            list2.forEach(v -> {
                switch (v) {
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
            list.add(row);
        }
        excelWriter.write1(list,sheet,table);
//        }
        excelWriter.finish();

    }

    @Override
    public List<JSONObject> selectUseOfCount(String uniqueIds, Integer deptChain, String startTime, String endTime, String type) {
        if (deptChain == null) {
            throw new ParamErrorException("deptChin 参数未传递！");
        }
        List<String> uniqueIdList = new ArrayList<>();
        if (deptChain == 0) {
            uniqueIdList.addAll(Arrays.asList(uniqueIds.split(",")));
        } else if (deptChain == 2) {// 直系部门
            if (uniqueIds.indexOf(",") != -1) {
                throw new ParamErrorException("deptChain为0时，uniqueIds 只能传递一个部门！");
            }
            uniqueIdList = rtvUnitMapper.selectUniqueIdByParentId(uniqueIds);
        }
        List<String> dates = null;
        if ("ym".equalsIgnoreCase(type)) {
            String lastDayByMonth = DateUtils.getLastDayByMonth(endTime);
            dates = DateUtils.getDate(startTime + "01", endTime + lastDayByMonth, "ymd");
        } else {
            dates = DateUtils.getDate(startTime, endTime, type);
        }
        List<JSONObject> list = new ArrayList<>();
        if (uniqueIdList.size() > 0 && dates.size() > 0) {
            List<StaDayDept> rtvUnits = deptMapper.selectUseOfCount(uniqueIdList, dates);
            String collect = uniqueIdList.stream().collect(Collectors.joining(","));
            collect = "'" + collect.replaceAll(",", "','") + "'";
            List<RtvUnit> rtvUnits1 = rtvUnitMapper.selectNameByUniqueIn(collect);
            Map<String, String> uniqueNameMap = rtvUnits1.stream().collect(Collectors.toMap(v -> v.getUniqueId(), v -> v.getName()));
            rtvUnits.forEach(v -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("uniqueId", v.getDeptUniqueId());
                jsonObject.put("count", v.getTalkCount());
                jsonObject.put("unitName", uniqueNameMap.get(v.getDeptUniqueId()));
                list.add(jsonObject);
            });
        };
        return list;
    }
}
