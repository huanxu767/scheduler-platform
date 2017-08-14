//初始化
var curPage = 1;
var pageSize = 10;

$(document).ready(function () {
    init();
});

function init() {
    var html = '<li><a class="active"><strong>日志管理</strong></a></li>';
    changeNavi(html);

    alSearch();
    //提交
    $("#searchButton").click(function () {
        queryPager();
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
        url: "./queryLogger",
        data: {curPage: curPage, pageSize: pageSize, projectId: projectId, jobAliasName: jobAliasName},
        dataType: "json",
        success: function (data) {
            $('#ibox').children('.ibox-content').toggleClass('sk-loading');
            ogniseTable(data);
        }
    });
}


function ogniseTable(data) {
    if (!data.success) {
        return;
    }
    var pageCounts = data.result.pager.totalPage;
    pageControl(pageCounts);
    var pagerList = data.result.pager.queryList;
    $("#loggerList").html("");
    $.each(pagerList, function (index, item) {
        var statusHtml = "";
        var successHtml = "";
        if (item.STATUS == 1) {
            statusHtml = "<span class='label label-primary'>已触发</span>";
        } else if(item.STATUS == 2) {
            statusHtml = "<span class='label  label-default'>已执行</span>";
        }else{
            statusHtml = "<span class='label  label-default'>已结束</span>";
        }
        if (item.SUCCESS == 1) {
            successHtml = "<span class='label label-primary'>已触发</span>";
        }else if (item.SUCCESS == 2){
            successHtml = "<span class='label  label-default'>触发失败</span>";
        }else if (item.SUCCESS == 3){
            successHtml = "<span class='label  label-default'>执行成功</span>";
        }else if (item.SUCCESS == 4){
            successHtml = "<span class='label  label-default'>执行失败</span>";
        }
        var rowHtml = "<tr><td>" + nullToString(item.JOB_ALIAS_NAME) + "</td>" +
            "<td>" + nullToString(item.PROJECT_NAME) + "</td>" +
            "<td>" + statusHtml + "</td>" +
            "<td>" + nullToString(item.NODE_NAME) + "</td>" +
            "<td>" + successHtml + "</td>" +
            "<td>" + nullToString(item.ERROR_MESSAGE) + "</td>" +
            "<td>" + nullToString(item.CREATE_TIME) + "</td>" +
            "<td>" + nullToString(item.END_TIME) + "</td>" +
            "</tr>";
        $("#loggerList").append(rowHtml);
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


