package com.lhfeiyu.tech.service;

import com.alibaba.fastjson.JSONObject;
import com.zom.statistics.handler.CacheHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    @Autowired
    private CacheHandler cacheHandler;

    public String getBuildMessage(String message) {
        JSONObject json = JSONObject.parseObject(message);
        String result = null;

        int id = json.getInteger("id");
        Long uid = json.getLong("uid");
        String username = cacheHandler.getUsername(uid);

        if (id == 1000) {
            Integer real = json.getInteger("real");
            if (null != real) {
                result = "【" + username + "】登录";
            }

        } else if (id == 1001) {
            Integer real = json.getInteger("real");
            if (null != real) {
                result = "【" + username + "】退出登录";
            }

        } else if (id == 1002) {
            result = "【" + username + "】发起一键告警";

        } else if (id == 1003) {
            result = "【" + username + "】取消一键告警";

        } else if (id == 1004) {
            Long handle_uid = json.getLong("handle_uid");
            String handle_name = cacheHandler.getUsername(handle_uid);
            result = "【" + handle_name + "】已处理【" + username + "】发起的一键告警";

        } else if (id == 1100) {
            Integer type = json.getInteger("type"); //1个呼，2组呼
            Integer flag = json.getInteger("flag"); //标识：1开始，2结束
            Long target = json.getLong("target");
            if (type == 1) {
                String target_name = cacheHandler.getUsername(target);
                if (flag == 1) {
                    result = "【" + username + "】向【" + target_name + "】发起语音通话";
                } else if (flag == 2) {
                    result = "【" + username + "】结束与【" + target_name + "】的语音通话";
                }
            } else if (type == 2) {
                String target_name = cacheHandler.getGroupName(target);
                if (flag == 1) {
                    result = "【" + username + "】在群组【" + target_name + "】发起语音通话";
                } else if (flag == 2) {
                    result = "【" + username + "】结束在群组【" + target_name + "】的语音通话";
                }
            }

        } else if (id == 1200) {
            Integer sub_id = json.getInteger("sub_id");
            Integer flag = json.getInteger("flag"); //标识：1开始，2结束
            if (null != sub_id) {
                if (sub_id == 1201) {
                    Long target = json.getLong("target");
                    String target_name = cacheHandler.getUsername(target);
                    if (flag == 1) {
                        result = "【" + username + "】向【" + target_name + "】发起视频通话";
                    } else if (flag == 2) {
                        result = "【" + username + "】结束与【" + target_name + "】的视频通话";
                    }
                } else if (sub_id == 1202) {
                    if (flag == 1) {
                        result = "【" + username + "】发起视频回传";
                    } else if (flag == 2) {
                        result = "【" + username + "】结束视频回传";
                    }
                } else if (sub_id == 1203) {
                    if (flag == 1) {
                        result = "【" + username + "】发起视频点名";
                    } else if (flag == 2) {
                        result = "【" + username + "】结束视频点名";
                    }
                } else if (sub_id == 1204) {
                    if (flag == 1) {
                        result = "【" + username + "】发起视频会商";
                    } else if (flag == 2) {
                        result = "【" + username + "】结束视频会商";
                    }
                }
            }

        } else if (id == 1300) {
            result = "【" + username + "】照片回传";

        } else if (id == 1400) {
            String name = json.getString("name");
            result = "【" + username + "】创建临时组【" + name + "】";

        } else if (id == 1500) {
            Integer type = json.getInteger("type"); //1个呼，2组呼
            Integer im_type = json.getInteger("im_type"); //消息类型：1文字，2文件，3离线语音
            Long target = json.getLong("target");
            String im_name = im_type == 1 ? "文字" : (im_type == 2 ? "文件" : (im_type == 3 ? "离线语音" : ""));
            if (type == 1) {
                String target_name = cacheHandler.getUsername(target);
                result = "【" + username + "】向【" + target_name + "】发送" + im_name + "消息";
            } else if (type == 2) {
                String target_name = cacheHandler.getGroupName(target);
                result = "【" + username + "】在群组【" + target_name + "】发送" + im_name + "消息";
            }

        } else if (id == 1600) {
            Long mileage = json.getLong("mileage");
            result = "【" + username + "】里程数更新【" + mileage + "】米";

        }
        return result;
    }

}
