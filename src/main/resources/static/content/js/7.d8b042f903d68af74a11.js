webpackJsonp([7],{luub:function(t,e,o){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var a=o("Dd8w"),s=o.n(a),n=o("NYxO"),l=o("dXKt"),r=o("FVPG"),i=o("FZWW"),c={components:{PageComp:o("B1va").a,FullButtons:i.a,SearchBoxes:r.a,DeptList:l.a},mounted:function(){this.$store.commit("changeName","部门统计表格"),0===this.deptTotal&&this.$refs.search.newQuery()},computed:s()({},Object(n.d)(["depShow","deptData","fullscreen","searchShow","loading"]),Object(n.b)(["display","depCol","contentHeight","deptStart","deptEnd","deptPdfTitle","color","deptTotal"])),methods:{exportExcel:function(){this.$refs.search.exportExcel()},toGetPdf:function(){window.scrollTo(0,0),this.getPdf(this.deptPdfTitle)}}},d={render:function(){var t=this,e=t.$createElement,o=t._self._c||e;return o("Content",{style:{padding:"10px 20px 10px",minHeight:t.contentHeight+"px",background:t.color.content}},[o("FullButtons"),t._v(" "),o("Row",[o("Col",{directives:[{name:"show",rawName:"v-show",value:t.depShow,expression:"depShow"}],staticClass:"noPrint",attrs:{lg:7}},[o("DeptList")],1),t._v(" "),o("Col",{attrs:{lg:t.display,id:"mainPage"}},[o("SearchBoxes",{directives:[{name:"show",rawName:"v-show",value:!t.fullscreen||t.searchShow,expression:"!fullscreen||searchShow"}],ref:"search"}),t._v(" "),o("Row",[o("Col",{attrs:{lg:24}},[o("div",{attrs:{id:"pdfDom"}},[o("Table",{ref:"table",attrs:{id:"print",loading:t.loading,columns:t.depCol,data:t.deptData.slice(t.deptStart,t.deptEnd)}})],1)])],1),t._v(" "),o("PageComp"),t._v(" "),o("ButtonGroup",{staticClass:"noPrint",staticStyle:{float:"right"},attrs:{shape:"circle"}},[o("Button",{staticStyle:{"margin-top":"5px","box-shadow":"none"},attrs:{size:"small",type:"primary"},on:{click:t.exportExcel}},[t._v("导出")]),t._v(" "),o("Button",{style:{marginTop:"5px",boxShadow:"none",background:t.color.slider,color:t.color.text},attrs:{size:"small"},on:{click:t.toGetPdf}},[t._v("打印")])],1)],1)],1)],1)},staticRenderFns:[]},p=o("VU/8")(c,d,!1,null,null,null);e.default=p.exports}});