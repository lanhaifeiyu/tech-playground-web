webpackJsonp([0],{"+XAE":function(e,t){},B1va:function(e,t,s){"use strict";var a=s("Dd8w"),n=s.n(a),i=s("NYxO"),o={name:"pageComp",data:function(){return{pageSizes:[5,10,20,50,100,200]}},computed:n()({total:function(){return"dept"===this.pageType?this.$store.getters.deptTotal:"user"===this.pageType?this.$store.getters.userTotal:"当前业务管理"===this.pageName?this.$store.getters.onGoingTotal:this.$store.getters.logTotal},pageSize:{get:function(){return"dept"===this.pageType?this.$store.state.deptSize:"user"===this.pageType?this.$store.state.userSize:"当前业务管理"===this.pageName?this.$store.state.onGoingSize:this.$store.state.logSize},set:function(e){"dept"===this.pageType?this.$store.commit("changeDeptSize",e):"user"===this.pageType?this.$store.commit("changeUserSize",e):"当前业务管理"===this.pageName?this.$store.commit("changeOngoingSize",e):this.$store.commit("changelogSize",e)}},currentPage:{get:function(){return"dept"===this.pageType?this.$store.state.deptPage:"user"===this.pageType?this.$store.state.userPage:"当前业务管理"===this.pageName?this.$store.state.onGoingPage:this.$store.state.logPage},set:function(e){"dept"===this.pageType?this.$store.commit("changeDeptPage",e):"user"===this.pageType?this.$store.commit("changeUserPage",e):"当前业务管理"===this.pageName?this.$store.commit("changeOngoingPage",e):this.$store.commit("changelogPage",e)}}},Object(i.d)(["pageName"]),Object(i.b)(["pageType","color"])),watch:{total:function(e){Math.ceil(e/this.pageSize)<this.currentPage?this.currentPage=Math.ceil(e/this.pageSize):0===this.currentPage&&(this.currentPage=1)}}},r={render:function(){var e=this,t=e.$createElement,s=e._self._c||t;return s("Row",{staticClass:"noPrint",style:{color:e.color.contentText}},[s("Col",{attrs:{lg:10}},[s("Page",{attrs:{"page-size":e.pageSize,current:e.currentPage,total:e.total,simple:""},on:{"update:current":function(t){e.currentPage=t}}})],1),e._v(" "),s("Select",{style:{width:"100px",float:"right"},attrs:{size:"small"},model:{value:e.pageSize,callback:function(t){e.pageSize=t},expression:"pageSize"}},e._l(e.pageSizes,function(t,a){return s("Option",{key:a,attrs:{value:t}},[e._v(e._s(t)+"条/页")])}),1)],1)},staticRenderFns:[]};var c=s("VU/8")(o,r,!1,function(e){s("RG1v")},"data-v-50f69a4c",null);t.a=c.exports},Cdx3:function(e,t,s){var a=s("sB3e"),n=s("lktj");s("uqUo")("keys",function(){return function(e){return n(a(e))}})},FVPG:function(e,t,s){"use strict";var a=s("Dd8w"),n=s.n(a),i=s("Xxa5"),o=s.n(i),r=s("mvHQ"),c=s.n(r),u=s("exGp"),h=s.n(u),l=s("NYxO"),d={name:"searchBoxes",components:{TableCheckBox:s("hNNa").a},data:function(){return{config:{},url:"",timeUrl:"",commitType:"",timeCommit:"",timeSelect:[{value:"year",label:"按年"},{value:"hYear",label:"按半年"},{value:"season",label:"按季"},{value:"month",label:"按月"},{value:"day",label:"按日"}]}},methods:{show:function(){this.$store.commit("showChange",!0)},autoDefault:function(){var e=new Date(this.end_time),t=new Date(e),s=e.getMonth(),a=e.getFullYear();switch(this.dateRange){case"day":t=e;break;case"month":t.setMonth(s-1);break;case"season":t.setMonth(s-3);break;case"hYear":t.setMonth(s-6);break;case"year":t.setFullYear(a-1)}this.start_time=t},clearSearch:function(){this.dateRange="month",this.end_time=new Date,this.sub="2",this.autoDefault()},setConfig:function(e,t){var s=this;return h()(o.a.mark(function a(){return o.a.wrap(function(a){for(;;)switch(a.prev=a.next){case 0:e.start_time=s.dateFormat(s.start_time),e.end_time=s.dateFormat(s.end_time),"dept"===t?s.multiple?(e.dept_id=s.checkedDepts,e.dept_chain=0):(e.dept_id=s.selectedDeptID,e.dept_chain=s.sub):s.multiple?"dept"===s.checkedUsers.type?(e.dept_id=s.checkedUsers.value,e.dept_chain=0,e.uids&&delete e.uids):(e.uids=s.checkedUsers.value,e.dept_id&&delete e.dept_id):"dept"===s.selectedUsers.type?(e.dept_chain=s.sub,e.dept_id=s.selectedUsers.id,e.uids&&delete e.uids):"user"===s.selectedUsers.type&&(e.uids=s.selectedUsers.id,e.dept_id&&delete e.dept_id),s.config=JSON.parse(c()(e));case 4:case"end":return a.stop()}},a,s)}))()},setTimeConfig:function(e){var t=this;return h()(o.a.mark(function s(){var a,n;return o.a.wrap(function(s){for(;;)switch(s.prev=s.next){case 0:return a={},n=new Date,s.next=4,n.setMonth(parseInt(e.start_time.slice(4,6)));case 4:return s.next=6,n.setFullYear(parseInt(e.start_time.slice(0,4)));case 6:return s.next=8,n.setDate(parseInt(e.start_time.slice(6,8)));case 8:return t.dateFormat(n)-e.end_time<0?(a.startTime=e.start_time.slice(0,6),a.endTime=e.end_time.slice(0,6),a.type="ym"):(a.startTime=e.start_time,a.endTime=e.end_time,a.type="ymd"),e.dept_id&&(a.uniqueIds=e.dept_id),e.uids&&(a.uids=e.uids,t.timeUrl=t.$host+"/user/userByTime"),e.dept_chain?a.deptChain=e.dept_chain:a.deptChain=0,s.abrupt("return",a);case 13:case"end":return s.stop()}},s,t)}))()},newQuery:function(){var e=this;return h()(o.a.mark(function t(){return o.a.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:if("dept"!==e.pageType){t.next=9;break}return e.commitType="changeDeptData",e.url=e.$host+"/dept/all",e.timeUrl=e.$host+"/dept/deptByTime",e.timeCommit="changeDeptTimeData",t.next=7,e.setConfig(e.deptConfig,"dept");case 7:t.next=15;break;case 9:return e.commitType="changeUserData",e.url=e.$host+"/user/all",e.timeUrl=e.$host+"/user/deptByTime",e.timeCommit="changeUserTimeData",t.next=15,e.setConfig(e.userConfig,"user");case 15:return e.startQuery(e.config,e.commitType),t.t0=e,t.next=19,e.setTimeConfig(e.config);case 19:t.t1=t.sent,t.t2=e.timeCommit,t.t0.timeQuery.call(t.t0,t.t1,t.t2),e.currentPage=1;case 23:case"end":return t.stop()}},t,e)}))()},startQuery:function(e,t){var s=this;this.multiple&&this.$store.dispatch("updateName"),this.$store.commit("loadingStatus",!0),this.$axios({method:"get",headers:{Authorization:"Bearer "+sessionStorage.getItem("token")},url:this.url,params:e}).then(function(e){if(console.log(e),e.data.success){var a=e.data.data.map(function(e){return e.onlineDuration=(e.onlineDuration/3600).toFixed(2),e.videoDuration=(e.videoDuration/3600).toFixed(2),e.groupTalkDuration=(e.groupTalkDuration/3600).toFixed(2),e.individualTalkDuration=(e.individualTalkDuration/3600).toFixed(2),e.mileage=(e.mileage/1e3).toFixed(2),e});s.$store.commit(t,a)}else s.$Message.warning({content:"无法获取数据，请稍后重试"});s.$store.commit("loadingStatus",!1)}).catch(function(e){console.log(e),s.$Message.warning({content:"无法获取数据，请稍后重试"}),s.$store.commit("loadingStatus",!1)})},timeQuery:function(e,t){var s=this;this.$store.commit("timeLStatus",!0),this.$axios({method:"get",headers:{Authorization:"Bearer "+sessionStorage.getItem("token")},url:this.timeUrl,params:e}).then(function(e){console.log(e),e.data.failure?(s.$store.commit(t,{}),s.$Message.warning("查询时间趋势失败，请稍后重试")):s.$store.commit(t,e.data),s.$store.commit("timeLStatus",!1)}).catch(function(e){console.log(e),s.$Message.warning({content:"无法获取时间趋势数据，请稍后重试"}),s.$store.commit("timeLStatus",!1)})},usageQuery:function(e,t){var s=this;return h()(o.a.mark(function a(){return o.a.wrap(function(a){for(;;)switch(a.prev=a.next){case 0:return a.next=2,s.$axios({method:"get",headers:{Authorization:"Bearer "+sessionStorage.getItem("token")},url:s.$host+"/dept/useOfCount",params:e}).then(function(e){console.log(e),e.data.success?e.data.data.length>10?s.$store.commit(t,e.data.data.slice(0,10)):s.$store.commit(t,e.data.data):s.$Message.warning("查询活跃度失败，请稍后重试")}).catch(function(e){console.log(e),s.$Message.warning({content:"无法获取活跃度数据，请稍后重试"}),s.$store.commit("timeLStatus",!1)});case 2:case"end":return a.stop()}},a,s)}))()},setExcelConfig:function(){var e=this;return h()(o.a.mark(function t(){var s,a;return o.a.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return s=void 0,s="user"===e.pageType?e.userConfig:e.deptConfig,a={startTime:s.start_time,endTime:s.end_time,type:"ymd",columns:e.colKeys},s.dept_id&&(a.uniqueIds=s.dept_id),s.uids&&(a.uids=s.uids),s.dept_chain?a.deptChain=s.dept_chain:a.deptChain=0,t.abrupt("return",a);case 7:case"end":return t.stop()}},t,e)}))()},exportExcel:function(){var e=this;return h()(o.a.mark(function t(){var s;return o.a.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return t.next=2,e.setExcelConfig();case 2:s=t.sent,e.$axios({method:"get",headers:{Authorization:"Bearer "+sessionStorage.getItem("token")},url:e.excelUrl,params:s,responseType:"arraybuffer"}).then(function(t){console.log(t);var s=new Blob([t.data],{type:"application/vnd.ms-excel;"}),a=document.createElement("a"),n=window.URL.createObjectURL(s);a.href=n;var i=t.headers["content-disposition"].split(";")[1].split("=")[1].split(".")[0];a.download=e.excelName+decodeURIComponent(i),document.body.appendChild(a),a.click(),document.body.removeChild(a),window.URL.revokeObjectURL(n)});case 4:case"end":return t.stop()}},t,e)}))()},generateMainConfigs:function(){var e=this;return h()(o.a.mark(function t(){var s,a,n,i,r;return o.a.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return s=e.dateFormat(new Date),a=new Date,t.next=4,a.setMonth(a.getMonth()-1);case 4:if(a=e.dateFormat(a),n=[],!(e.diyDeptList.length>0)){t.next=10;break}return t.next=9,e.seriesConfig({dept_id:sessionStorage.getItem("deptId"),start_time:s,end_time:s,time_level:"ymd",dept_chain:"0"},e.dayDeptList);case 9:n=t.sent;case 10:if(i=[],!(e.diyDeptList.length>0)){t.next=15;break}return t.next=14,e.seriesConfig({dept_id:sessionStorage.getItem("deptId"),start_time:a,end_time:s,time_level:"ymd",dept_chain:"0"},e.monthDeptList);case 14:i=t.sent;case 15:if(r=[],!(e.diyDeptList.length>0)){t.next=22;break}return t.next=19,e.seriesConfig({dept_id:sessionStorage.getItem("deptId"),start_time:e.$store.state.mainConfig.diyStart,end_time:e.$store.state.mainConfig.diyEnd,time_level:"ymd",dept_chain:"0"},e.diyDeptList);case 19:r=t.sent,t.next=23;break;case 22:r={};case 23:return console.log({month:i,day:n,diy:r}),t.abrupt("return",{month:i,day:n,diy:r});case 25:case"end":return t.stop()}},t,e)}))()},mainQuery:function(){var e=this;return h()(o.a.mark(function t(){var s;return o.a.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:if(e.url=e.$host+"/dept/all",e.timeUrl=e.$host+"/dept/deptByTime",!e.queryConfigs.month.usage){t.next=5;break}return t.next=5,e.usageQuery(e.usageConfig.month,"changeUsageMonth");case 5:if(!e.queryConfigs.day.usage){t.next=8;break}return t.next=8,e.usageQuery(e.usageConfig.day,"changeUsageDay");case 8:if(!e.queryConfigs.diy.usage){t.next=11;break}return t.next=11,e.usageQuery(e.usageConfig.diy,"changeUsageDiy");case 11:return t.next=13,e.generateMainConfigs();case 13:s=t.sent,e.queryConfigs.month.main&&e.startQuery(s.month.main,"changeMainMonth"),e.queryConfigs.day.main&&e.startQuery(s.day.main,"changeMainDay"),e.queryConfigs.month.sub&&e.startQuery(s.month.sub,"changeSubMonth"),e.queryConfigs.month.time&&e.timeQuery(s.month.time,"changeTimeMonth"),e.queryConfigs.day.sub&&e.startQuery(s.day.sub,"changeSubDay"),e.diy&&(e.queryConfigs.diy.main&&e.startQuery(s.diy.main,"changeMainDiy"),e.queryConfigs.diy.sub&&e.startQuery(s.diy.sub,"changeSubDiy"),e.queryConfigs.diy.time&&e.timeQuery(s.diy.time,"changeTimeDiy"));case 20:case"end":return t.stop()}},t,e)}))()},updateDate:function(e){var t=new Date(this.start_time),s=new Date(this.end_time);switch(e=parseInt(e),this.dateRange){case"day":t.setDate(t.getDate()+e),s.setDate(s.getDate()+e);break;case"month":t.setMonth(t.getMonth()+e),s.setMonth(s.getMonth()+e);break;case"season":t.setMonth(t.getMonth()+3*e),s.setMonth(s.getMonth()+3*e);break;case"hYear":t.setMonth(t.getMonth()+6*e),s.setMonth(s.getMonth()+6*e);break;case"year":t.setFullYear(t.getFullYear()+e),s.setFullYear(s.getFullYear()+e)}this.end_time=s,this.start_time=t},dateFormat:function(e){if(!e)return"";var t=e.getFullYear(),s=e.getDate()<10?"0"+e.getDate():e.getDate();return t+""+(e.getMonth()+1<10?"0"+(e.getMonth()+1):e.getMonth()+1)+s},seriesConfig:function(e,t){var s=this;return h()(o.a.mark(function a(){var n,i;return o.a.wrap(function(a){for(;;)switch(a.prev=a.next){case 0:return a.next=2,JSON.parse(c()(e));case 2:return(n=a.sent).dept_id=t.join(","),a.next=6,s.setTimeConfig(n);case 6:return i=a.sent,a.abrupt("return",{main:e,sub:n,time:i});case 8:case"end":return a.stop()}},a,s)}))()}},mounted:function(){var e=this;return h()(o.a.mark(function t(){return o.a.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:if(e.start_time){t.next=3;break}return t.next=3,e.autoDefault();case 3:case"end":return t.stop()}},t,e)}))()},watch:{selectedDeptID:function(){this.multiple||this.newQuery()},selectedUsers:function(){this.multiple||this.newQuery()},multiple:function(e){e?(this.selectedUserNode[0]&&(this.selectedUserNode[0].selected=!1),this.selectedDeptNode[0]&&(this.selectedDeptNode[0].selected=!1)):(this.selectedUserNode[0]&&(this.selectedUserNode[0].selected=!0),this.selectedDeptNode[0]&&(this.selectedDeptNode[0].selected=!0))}},computed:n()({allCheck:{get:function(){return this.$store.state.allCheck},set:function(e){this.$store.commit("changeAllCheck",e)}},dateRange:{get:function(){return this.$store.state.dateRange},set:function(e){this.$store.commit("changeRange",e)}},start_time:{get:function(){return this.$store.state.start_time},set:function(e){this.$store.commit("changeStart",e)}},end_time:{get:function(){return this.$store.state.end_time},set:function(e){this.$store.commit("changeEnd",e)}},sub:{get:function(){return this.$store.state.sub},set:function(e){this.$store.commit("changeSub",e)}},currentPage:{get:function(){return"dept"===this.pageType?this.$store.state.deptPage:this.$store.state.userPage},set:function(e){"dept"===this.pageType?this.$store.commit("changeDeptPage",e):this.$store.commit("changeUserPage",e)}},selectedUserNode:{get:function(){return this.$store.state.selectedUserNode},set:function(e){this.$store.commit("changeUserNode",e)}},selectedDeptNode:{get:function(){return this.$store.state.selectedDeptNode},set:function(e){this.$store.commit("changeDeptNode",e)}},excelUrl:function(){return"user"===this.pageType?this.$host+"/user/exportExcel":this.$host+"/dept/exportExcel"},excelName:function(){return"dept"===this.pageType?this.deptName:this.userName},usageConfig:function(){var e=this.dateFormat(new Date),t=new Date,s=sessionStorage.getItem("deptId");return t.setMonth(t.getMonth()-1),{day:{uniqueIds:s,startTime:e,endTime:e,type:"ymd",deptChain:2},month:{uniqueIds:s,startTime:this.dateFormat(t),endTime:e,type:"ymd",deptChain:2},diy:{uniqueIds:s,startTime:this.$store.state.mainConfig.diyStart,endTime:this.$store.state.mainConfig.diyEnd,type:"ymd",deptChain:2}}},deptConfig:{get:function(){return this.$store.state.deptConfig},set:function(e){this.$store.commit("changeDeptConfig",e)}},userConfig:{get:function(){return this.$store.state.userConfig},set:function(e){this.$store.commit("changeUserConfig",e)}},queryConfigs:function(){var e={month:{main:!1,sub:!1,time:!1,usage:!1},day:{main:!1,sub:!1,usage:!1},diy:{main:!1,sub:!1,time:!1,usage:!1}};for(var t in this.grid){var s=void 0;if(this.grid[t].type.timeSpan)switch(e[s=this.grid[t].type.timeSpan].usage=!0,this.grid[t].comp){case"circleGraph":e[s].main=!0;break;case"pointGraph":case"horizontalGraph":e[s].sub=!0;break;case"lineGraph":e[s].time=!0}}return console.log(e),e}},Object(l.d)(["depShow","selectedUsers","timeSpan","diyStart","diyEnd","deptName","userName"]),Object(l.b)(["colKeys","pageType","selectedDeptID","multiple","checkedDepts","pageType2","selectedUsers","checkedUsers","grid","diy","diyDeptList","dayDeptList","monthDeptList"]))},p={render:function(){var e=this,t=e.$createElement,s=e._self._c||t;return s("Row",{staticClass:"noPrint",staticStyle:{"margin-bottom":"2px"},attrs:{align:"middle",type:"flex"}},[s("Button",{directives:[{name:"show",rawName:"v-show",value:!e.depShow,expression:"!depShow"}],style:{margin:"0 20px 0 0"},attrs:{type:"primary",ghost:""},on:{click:e.show}},[e._v("显示")]),e._v(" "),s("Select",{style:{width:"104px"},attrs:{placeholder:"子部门？"},model:{value:e.sub,callback:function(t){e.sub=t},expression:"sub"}},[s("Option",{attrs:{value:"0"}},[e._v("当前部门")]),e._v(" "),s("Option",{attrs:{value:"2"}},[e._v("直属部门")]),e._v(" "),s("Option",{attrs:{value:"1"}},[e._v("所有子部门")]),e._v(" "),s("Option",{attrs:{value:"diy"}},[e._v("自定义")])],1),e._v(" "),s("Button",{staticClass:"noBorder",attrs:{shape:"circle",icon:"ios-arrow-back",size:"small",type:"text"},on:{click:function(t){return e.updateDate(-1)}}}),e._v(" "),s("DatePicker",{staticClass:"search",staticStyle:{"margin-right":"3px"},attrs:{type:"date",placeholder:"起始时间"},model:{value:e.start_time,callback:function(t){e.start_time=t},expression:"start_time"}}),e._v(" "),s("DatePicker",{staticClass:"search",attrs:{type:"date",placeholder:"结束时间"},on:{"on-change":e.autoDefault},model:{value:e.end_time,callback:function(t){e.end_time=t},expression:"end_time"}}),e._v(" "),s("Button",{staticClass:"noBorder",attrs:{shape:"circle",icon:"ios-arrow-forward",size:"small",type:"text"},on:{click:function(t){return e.updateDate(1)}}}),e._v(" "),s("Select",{staticClass:"search",staticStyle:{width:"78px","margin-right":"4px"},attrs:{placeholder:"快捷区间"},on:{"on-change":e.autoDefault},model:{value:e.dateRange,callback:function(t){e.dateRange=t},expression:"dateRange"}},e._l(e.timeSelect,function(t){return s("Option",{key:t.value,attrs:{value:t.value}},[e._v(e._s(t.label))])}),1),e._v(" "),s("Button",{staticStyle:{"box-shadow":"none","margin-right":"4px"},attrs:{type:"primary",size:"small"},on:{click:e.newQuery}},[e._v("查询")]),e._v(" "),s("Button",{staticStyle:{"box-shadow":"none","margin-right":"20px"},attrs:{type:"primary",size:"small"},on:{click:e.clearSearch}},[e._v("重置")]),e._v(" "),s("TableCheckBox")],1)},staticRenderFns:[]};var m=s("VU/8")(d,p,!1,function(e){s("myUP")},"data-v-1c627c1b",null);t.a=m.exports},FZWW:function(e,t,s){"use strict";var a=s("Dd8w"),n=s.n(a),i=s("wVqy"),o=s("NYxO"),r={name:"fullButtons",components:{PageSwitcher:i.a},data:function(){return{icon:""}},computed:n()({fullscreen:{get:function(){return this.$store.state.fullscreen},set:function(e){this.$store.commit("changeFullscreen",e)}},searchShow:{get:function(){return this.$store.state.searchShow},set:function(e){this.$store.commit("showSearch",e)}}},Object(o.d)(["messageShow","pageName"]),Object(o.b)(["pageType","color"])),methods:n()({searchShowHandle:function(){"md-remove"===this.icon?(this.searchShow=!1,this.icon="md-add"):(this.searchShow=!0,this.icon="md-remove")}},Object(o.c)(["showMessage"])),mounted:function(){this.searchShow?this.icon="md-remove":this.icon="md-add";var e=this;this.$nextTick(function(){document.addEventListener("keyup",function(t){27===t.keyCode&&e.fullscreen&&(e.fullscreen=!1)})})}},c={render:function(){var e=this,t=e.$createElement,s=e._self._c||t;return s("Row",{directives:[{name:"show",rawName:"v-show",value:e.fullscreen&&"others"!==e.pageType,expression:"fullscreen&&pageType!=='others'"}],staticStyle:{"margin-bottom":"10px"}},[s("Button",{style:{float:"right",color:e.color.text},attrs:{type:"text",icon:e.icon},on:{click:e.searchShowHandle}}),e._v(" "),s("Button",{style:{float:"right",color:e.color.text},attrs:{type:"text",icon:"md-exit"},on:{click:function(t){e.fullscreen=!1}}}),e._v(" "),s("PageSwitcher",{style:{color:e.color.text,marginTop:"4px"}}),e._v(" "),s("Button",{directives:[{name:"show",rawName:"v-show",value:!e.messageShow,expression:"!messageShow"}],style:{float:"right",color:e.color.text,marginTop:"4px"},attrs:{size:"small",type:"text"},on:{click:function(t){return e.showMessage(!0)}}},[s("p",[s("Icon",{attrs:{type:"ios-notifications"}}),e._v("更新提醒")],1)])],1)},staticRenderFns:[]};var u=s("VU/8")(r,c,!1,function(e){s("+XAE")},"data-v-37cb6be5",null);t.a=u.exports},JrJb:function(e,t){},RG1v:function(e,t){},TmV0:function(e,t,s){s("fZOM"),e.exports=s("FeBl").Object.values},dXKt:function(e,t,s){"use strict";var a,n=s("Dd8w"),i=s.n(n),o=s("NYxO"),r=(a=0,function(e,t){clearTimeout(a),a=setTimeout(e,t)}),c={name:"deptList",data:function(){return{userDept:sessionStorage.getItem("deptId"),searched:"",loading:!1,depOptions:[]}},computed:i()({departments:{get:function(){return this.$store.state.departments},set:function(e){this.$store.commit("loadDepartments",e)}},type:{get:function(){return this.$store.state.deptSearchType},set:function(e){this.$store.commit("changeDeptSearch",e)}},back:function(){return!this.departments[0]||this.userDept!==this.departments[0].id}},Object(o.b)(["treeHeight","multiple","selectedDept","color"]),Object(o.d)(["disableNodes"])),methods:{hide:function(){this.$store.commit("showChange",!1)},selectDept:function(e){e&&this.initialData(e.value,!this.multiple)},selectedNodes:function(e){this.$store.commit("changeDeptNode",e),this.multiple||this.$store.dispatch("updateName")},checkNodes:function(e){this.$store.commit("changeDeptNodes",e)},searchDept:function(e){var t=this;r(function(e){if(""!==e){t.loading=!0;var s={};s[t.type]=e,t.$axios({method:"get",headers:{Authorization:"Bearer "+sessionStorage.getItem("token")},url:t.$host+"/dept/like",params:s}).then(function(e){console.log(e),t.depOptions=e.data.data,t.loading=!1}).catch(function(e){console.log(e),t.loading=!1})}},500)},loadTreeData:function(e,t){var s=this,a=[];this.$axios({method:"get",headers:{Authorization:"Bearer "+sessionStorage.getItem("token")},url:this.$host+"/dept/base",params:{dept_id:e.id,dept_chain:2,count:1e3,page:1}}).then(function(e){if(console.log(e),e.data.success){for(var n=0;n<e.data.data.length;n++){var i=s.isDisabled(e.data.data[n].dept_id),o={title:i?e.data.data[n].dept_name+"*":e.data.data[n].dept_name,loading:!1,id:e.data.data[n].dept_id,children:[],disabled:i};a.push(o)}t(a)}else s.$Message.warning({content:"服务器繁忙，请稍后再试"})}).catch(function(e){console.log(e)})},initialData:function(e,t){var s=this;this.departments=[],this.$axios({method:"get",headers:{Authorization:"Bearer "+sessionStorage.getItem("token")},url:this.$host+"/dept/base",params:{dept_id:e,dept_chain:0,count:1,page:1}}).then(function(e){if(console.log(e),e.data.success){var a=s.isDisabled(e.data.data[0].dept_id),n={title:a?e.data.data[0].dept_name+"*":e.data.data[0].dept_name,loading:!1,id:e.data.data[0].dept_id,selected:t,children:[],disabled:a};s.departments=[n],t?s.$store.commit("changeDeptNode",s.departments):s.$store.commit("changeDeptNode",[]),s.searched="",s.multiple||s.$store.dispatch("updateName")}else s.$Message.warning({content:"服务器繁忙，请稍后再试"})}).catch(function(e){console.log(e)})},isDisabled:function(e){for(var t=0;t<this.disableNodes.length;t++)if(e===this.disableNodes[t].id)return!0;return!1}},mounted:function(){this.selectedDeptID||(this.selectedDeptID=this.userDept),0===this.departments.length&&this.initialData(this.userDept,!1),this.$store.dispatch("checkForDisable")}},u={render:function(){var e=this,t=e.$createElement,s=e._self._c||t;return s("Card",{style:{width:"90%",minWidth:"200px",maxWidth:"400px",background:e.color.card},attrs:{bordered:!1}},[s("p",{style:{color:e.color.text},attrs:{slot:"title"},slot:"title"},[s("Icon",{attrs:{type:"md-list"}}),e._v("\n    部门树形结构\n  ")],1),e._v(" "),s("a",{directives:[{name:"show",rawName:"v-show",value:e.back,expression:"back"}],attrs:{slot:"extra",href:"#"},on:{click:function(t){t.preventDefault(),e.initialData(e.userDept),e.searched=""}},slot:"extra"},[s("Icon",{attrs:{type:"ios-loop-strong"}}),e._v("\n    重置\n  ")],1),e._v(" "),s("a",{attrs:{slot:"extra",href:"#"},on:{click:function(t){return t.preventDefault(),e.hide(t)}},slot:"extra"},[s("Icon",{attrs:{type:"ios-loop-strong"}}),e._v("\n    隐藏\n  ")],1),e._v(" "),s("p",{directives:[{name:"show",rawName:"v-show",value:!e.multiple,expression:"!multiple"}],style:{margin:"0px 0px 13px 0px",color:e.color.highlight}},[e._v("\n    当前选择："+e._s(e.selectedDept)+"\n  ")]),e._v(" "),s("Row",{staticStyle:{margin:"10px 0"}},[s("Select",{staticStyle:{width:"auto"},model:{value:e.type,callback:function(t){e.type=t},expression:"type"}},[s("Option",{attrs:{value:"name"}},[e._v("全名")]),e._v(" "),s("Option",{attrs:{value:"shortName"}},[e._v("简称")])],1),e._v(" "),s("Select",{style:{width:"65%"},attrs:{filterable:"",remote:"","remote-method":e.searchDept,placeholder:"输入要搜索的...",loading:e.loading,clearable:""},on:{"on-select":e.selectDept},model:{value:e.searched,callback:function(t){e.searched=t},expression:"searched"}},e._l(e.depOptions,function(t,a){return s("Option",{key:a,style:{width:"200px"},attrs:{value:t.uniqueId}},[e._v(e._s(t.name))])}),1)],1),e._v(" "),s("div",{staticClass:"tree"},[s("Tree",{ref:"tree4",style:{maxHeight:e.treeHeight+"px",color:e.color.text},attrs:{data:e.departments,"load-data":e.loadTreeData,"show-checkbox":e.multiple,"check-directly":"","check-strictly":""},on:{"on-check-change":e.checkNodes,"on-select-change":e.selectedNodes}})],1)],1)},staticRenderFns:[]};var h=s("VU/8")(c,u,!1,function(e){s("JrJb")},"data-v-d5da4954",null);t.a=h.exports},fZOM:function(e,t,s){var a=s("kM2E"),n=s("mbce")(!1);a(a.S,"Object",{values:function(e){return n(e)}})},fZjL:function(e,t,s){e.exports={default:s("jFbC"),__esModule:!0}},gRE1:function(e,t,s){e.exports={default:s("TmV0"),__esModule:!0}},hNNa:function(e,t,s){"use strict";var a=s("Dd8w"),n=s.n(a),i=s("NYxO"),o={name:"tableCheckBox",data:function(){return{graphCheckSize:{bar:{all:8,video_count:6,audio_count:5,im_count:3},pie:{all:6,video_count:5,audio_count:3,im_count:3},line:{all:8,video_count:6,audio_count:5,im_count:3}}}},computed:n()({selectType:{get:function(){return this.$store.state.selectType},set:function(e){this.$store.commit("changeType",e)}},checkedGroup:{get:function(){return this.$store.state.checkedGroup},set:function(e){this.$store.commit("changeChecked",e)}},allCheck:{get:function(){return this.$store.state.allCheck},set:function(e){this.$store.commit("changeAllCheck",e)}},checked:function(){return this.checkedGroup[this.selectType]}},Object(i.b)(["pageType2","color"]),Object(i.d)(["checkboxGroup","searchShow","fullscreen","graphType"])),watch:{selectType:function(){this.checkChange(this.checkedGroup[this.selectType])},checked:function(){"graph"===this.pageType2&&this.checkChange(this.checkedGroup[this.selectType])},pageType2:function(){this.checkChange(this.checkedGroup[this.selectType])},graphType:function(){this.checkChange(this.checkedGroup[this.selectType])}},methods:{selectAll:function(){if(this.allCheck){for(var e=[],t=0;t<this.checkboxGroup[this.selectType].length;t++)e.push(t);this.checkedGroup[this.selectType]=e.sort(this.compare)}else this.checkedGroup[this.selectType]=[]},checkChange:function(e){var t=void 0;t="graph"===this.pageType2?this.graphCheckSize[this.graphType][this.selectType]-1:this.checkboxGroup[this.selectType].length-1,"table"===this.pageType2&&(this.checkedGroup[this.selectType]=e.sort(this.compare)),this.allCheck=this.checkedGroup[this.selectType].indexOf(t)===t},selectGraphAll:function(){if(this.allCheck){for(var e=[],t=0;t<this.graphCheckSize[this.graphType][this.selectType];t++)e.push(t);this.checkedGroup[this.selectType]=e.sort(this.compare)}else this.checkedGroup[this.selectType]=[]},compare:function(e,t){return e-t}}},r={render:function(){var e=this,t=e.$createElement,s=e._self._c||t;return s("div",{style:{color:e.color.contentText}},[e._v("\n  过滤：\n  "),s("Select",{staticStyle:{width:"90px","margin-right":"4px"},attrs:{placeholder:"业务种类"},model:{value:e.selectType,callback:function(t){e.selectType=t},expression:"selectType"}},[s("Option",{attrs:{value:"all"}},[e._v("综合")]),e._v(" "),s("Option",{attrs:{value:"audio_count"}},[e._v("语音通话")]),e._v(" "),s("Option",{attrs:{value:"video_count"}},[e._v("视频业务")]),e._v(" "),s("Option",{attrs:{value:"im_count"}},[e._v("即时消息")])],1),e._v(" "),s("Dropdown",{directives:[{name:"show",rawName:"v-show",value:"table"===e.pageType2,expression:"pageType2==='table'"}],style:{margin:"12px  0 "},attrs:{placement:"bottom-start"}},[s("Checkbox",{on:{"on-change":e.selectAll},model:{value:e.allCheck,callback:function(t){e.allCheck=t},expression:"allCheck"}},[e._v("全选")]),e._v(" "),e._l(e.checkboxGroup[e.selectType],function(t,a){return s("CheckboxGroup",{key:a,style:{width:"auto",margin:"2px"},attrs:{slot:"list"},on:{"on-change":e.checkChange},slot:"list",model:{value:e.checkedGroup[e.selectType],callback:function(t){e.$set(e.checkedGroup,e.selectType,t)},expression:"checkedGroup[selectType]"}},[s("Checkbox",{attrs:{label:a}},[s("span",[e._v(e._s(t.title))])])],1)})],2),e._v(" "),s("Checkbox",{directives:[{name:"show",rawName:"v-show",value:"graph"===e.pageType2,expression:"pageType2==='graph'"}],on:{"on-change":e.selectGraphAll},model:{value:e.allCheck,callback:function(t){e.allCheck=t},expression:"allCheck"}},[e._v("全选")])],1)},staticRenderFns:[]};var c=s("VU/8")(o,r,!1,function(e){s("s1Go")},"data-v-55e6bfe0",null);t.a=c.exports},jFbC:function(e,t,s){s("Cdx3"),e.exports=s("FeBl").Object.keys},mbce:function(e,t,s){var a=s("+E39"),n=s("lktj"),i=s("TcQ7"),o=s("NpIQ").f;e.exports=function(e){return function(t){for(var s,r=i(t),c=n(r),u=c.length,h=0,l=[];u>h;)s=c[h++],a&&!o.call(r,s)||l.push(e?[s,r[s]]:r[s]);return l}}},myUP:function(e,t){},s1Go:function(e,t){},uqUo:function(e,t,s){var a=s("kM2E"),n=s("FeBl"),i=s("S82l");e.exports=function(e,t){var s=(n.Object||{})[e]||Object[e],o={};o[e]=t(s),a(a.S+a.F*i(function(){s(1)}),"Object",o)}}});