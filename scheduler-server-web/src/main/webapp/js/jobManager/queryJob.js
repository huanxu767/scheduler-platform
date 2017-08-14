//初始化
var curPage = 1;
var pageSize = 20;

$(document).ready(function () {
    // console.log("queryJob");
    init();
});

function init() {
    var html = '<li><a class="active"><strong>任务管理</strong></a></li>';
    changeNavi(html);

    alSearch();
    //提交
    $("#searchButton").click(function () {
        queryPager();
    });
    //新增
    $("#addButton").click(function () {
        loadPage("./addJob");
    });
}
function alSearch() {
    curPage = 1;
    queryPager();
}

function queryPager() {
    $('#ibox').children('.ibox-content').toggleClass('sk-loading');
    var projectId = $("#projectId").val();
    var jobAliasName = $("#jobAliasName").val();
    $.ajax({
        type: "POST",
        url: "./queryJob",
        data: {curPage: curPage, pageSize: pageSize, projectId: projectId, jobAliasName: jobAliasName},
        dataType: "json",
        success: function (data) {
            $('#ibox').children('.ibox-content').toggleClass('sk-loading');
            ogniseTable(data);
        }
    });
}


function ogniseTable(data) {
    // console.log(data.result.pager.queryList);
    if (!data.success) {
        return;
    }
    var pageCounts = data.result.pager.totalPage;
    pageControl(pageCounts);

    var pagerList = data.result.pager.queryList;
    $("#jobList").html("");
    $.each(pagerList, function (index, item) {
        var statusHtml = "";
        var statusControlHtml = "";
        if (item.STATUS == 3) {
            statusHtml = "<span class='label label-primary'>执行中</span>";
            statusControlHtml = "<button class='btn btn-default btn-outline btn-xs' data='"+item.SCHEDULE_JOB_ID+"' type='button' onclick='pause(this)'><i class='fa fa-paste' ></i> &nbsp;暂停</button>&nbsp;";
        } else {
            statusHtml = "<span class='label  label-default'>暂停</span>";
            statusControlHtml =  "<button class='btn btn-default btn-outline btn-xs' data='"+item.SCHEDULE_JOB_ID+"' type='button' onclick='resume(this)'><i class='fa fa-play'></i> &nbsp;&nbsp;执行</button>&nbsp;";
        }
        var rowHtml = "<tr><td>" + item.JOB_ALIAS_NAME + "</td>" +
            "<td>" + item.CRON_EXPRESSION + "</td>" +
            "<td>" + item.PROJECT_NAME + "</td>" +
            "<td>" + item.URL + "</td>" +
            // "<td>" + item.JOB_TYPE + "</td>" +
            "<td>" + item.CLASS_NAME + "</td>" +
            "<td>" + item.METHOD_NAME + "</td>" +
            "<td>" + statusHtml + "</td>" +
            // "<td>" + item.CREATE_TIME + "</td>" +
            "<td>" +
                statusControlHtml+
            "<button class='btn btn-default btn-outline btn-xs' data='"+item.SCHEDULE_JOB_ID+"' onclick='runOnce(this)' type='button'><i class='fa fa-play'></i> &nbsp;单次</button>&nbsp;"+
            "<button class='btn btn-default btn-outline btn-xs' data='"+item.SCHEDULE_JOB_ID+"' onclick='modify(this)' type='button'><i class='fa fa-pencil'></i> &nbsp;修改</button>&nbsp;"+
            "<button class='btn btn-default btn-outline btn-xs' data='"+item.SCHEDULE_JOB_ID+"' onclick='detail(this)' type='button'><i class='fa fa-folder'></i> &nbsp;明细</button>&nbsp;"+
            "<button class='btn btn-default btn-outline btn-xs' data='"+item.SCHEDULE_JOB_ID+"' onclick='deleteJob(this)' type='button'><i class='fa fa-times'></i> &nbsp;删除</button>&nbsp;"+
            "</td>" +
            "</tr>";
        $("#jobList").append(rowHtml);
        // console.log(rowHtml);
    });

}

function pageControl(pageCounts) {
    $(".tcdPageCode").createPage({
        pageCount: pageCounts,
        current: curPage,
        backFn: function (p) {
            curPage = p;
            queryPager();
        }
    });
}

function pause(obj){
    var id = $(obj).attr("data");
    $.ajax({
        type: "GET",
        url: "./pauseJob",
        data: {scheduleJobId:id},
        dataType: "json",
        success: function (data) {
            if(data.success){
                //成功
                toastr["success"]("任务暂停成功");
                $(obj).parent().siblings().eq(6).html('<span class="label  label-default">暂停</span>');
                $(obj).replaceWith("<button class='btn btn-default btn-outline btn-xs' data='"+id+"' type='button' onclick='resume(this)'><i class='fa fa-play'></i> &nbsp;&nbsp;执行</button>");
            }else{
                //失败
                toastr["error"]("请稍后再试","任务暂停失败");
            }
        }
    });
}

function resume(obj){
    var id = $(obj).attr("data");
    $.ajax({
        type: "GET",
        url: "./resumeJob",
        data: {scheduleJobId:id},
        dataType: "json",
        success: function (data) {
            // console.log(data);
            if(data.success){
                toastr["success"]("任务调度成功");
                $(obj).parent().siblings().eq(6).html('<span class="label label-primary">执行中</span>');
                //成功
                $(obj).replaceWith("<button class='btn btn-default btn-outline btn-xs' data='"+id+"' type='button' onclick='pause(this)'><i class='fa fa-paste' ></i> &nbsp;暂停</button>");
            }else{
                toastr["error"]("请稍后再试","任务启动失败");
            }
        }
    });
}

function deleteJob(obj){
    var id = $(obj).attr("data");
    swal({
            title: "确定吗？",
            text: "你将无法恢复该任务！",
            type: "warning",
            showCancelButton: true,
            closeOnConfirm: false,
            showLoaderOnConfirm: false,
            confirmButtonColor: "#DD6B55",
            confirmButtonText: "是的, 删掉它!",
            cancelButtonText:"取消"
        },
        function(){
            $.ajax({
                type: "GET",
                url: "./deleteJob",
                data: {scheduleJobId:id},
                dataType: "json",
                success: function (data) {
                    swal.close();
                    if(data.success){
                        //成功
                        toastr["success"]("任务删除成功");
                        $(obj).parent().parent().remove();
                    }else{
                        //失败
                        toastr["error"]("请稍后再试","任务删除失败");
                    }
                }
            });
        });
}


function runOnce(obj){
    var id = $(obj).attr("data");
    swal({
            title: "确定吗？",
            text: "你将无法撤销该次执行！",
            type: "warning",
            showCancelButton: true,
            closeOnConfirm: false,
            showLoaderOnConfirm: false,
            confirmButtonColor: "#DD6B55",
            confirmButtonText: "是的, 执行一次!",
            cancelButtonText:"取消"
        },
        function(){
            $.ajax({
                type: "GET",
                url: "./runOnce",
                data: {scheduleJobId:id},
                dataType: "json",
                success: function (data) {
                    // console.log(data);
                    swal.close();
                    if(data.success){
                        toastr["success"]("单次执行成功");
                    }else{
                        toastr["error"]("请稍后再试","单次执行失败");
                    }
                }
            });
        });

}


function modify(obj){
    var id = $(obj).attr("data");
    loadPage("./modifyJobInit?scheduleJobId="+id)
}

function detail(obj){
    var id = $(obj).attr("data");
    $("#ct").html("").load("./jobDetail?scheduleJobId="+id);
    $('#myModal5').modal()
}