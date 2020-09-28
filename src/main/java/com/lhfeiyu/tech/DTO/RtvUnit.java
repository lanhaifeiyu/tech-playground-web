package com.lhfeiyu.tech.DTO;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
public class RtvUnit implements Serializable {

    private Integer id;
    private String  objectId;
    private String  name;
    private String  code;
    private String  shortName;
    private String  parentId;
    private String  extension;
    private Integer state;
    private Integer corpId;
    private String  resourceId;
    private String  rtype;
    private String  system;
    private String  uniqueId;
    private String updateType;
    private Integer defGid;
    private String unitId;
    private Integer parentPkId;
    private String  lastUpdateTime;
    private Integer ordernum;
    private Date personUpdateTime;
    private Date    deptUpdateTime;
    private Integer active;
    private Integer dumyDefGid;
    private Integer isParent;
    private String  joinlinkage;
    private Integer inactiveno;
    private Integer activeno;

    // 调度台gps信息，1：默认， 2：非默认
    private Integer isDefault;

    //此域不存数据库
    private String parentName;

    private String roleIds;

    private String roleNames;

    private Integer zoneId;

    private String orgType;
    private String orgTag;
}
