
toastr.options = {
    "closeButton": true,
    "debug": false,
    "progressBar": true,
    "preventDuplicates": false,
    "positionClass": "toast-top-right",
    "onclick": null,
    "showDuration": "400",
    "hideDuration": "1000",
    "timeOut": "3000",
    "extendedTimeOut": "1000",
    "showEasing": "swing",
    "hideEasing": "linear",
    "showMethod": "fadeIn",
    "hideMethod": "fadeOut"
}

loadPage("./dashboard");
$.ajaxSetup ({
    cache: false //关闭AJAX相应的缓存
});

function loadPage(url){
    $(".wrapper-content").load(url);
}
function changeNavi(naviHtml){
    naviHtml = ' <li><a href="./main">首页</a></li>' + naviHtml;
    $(".breadcrumb").html(naviHtml);
    $("#pageHeading").show();
}

function goMenu(obj){
    $("#side-menu .active").removeClass("active");
    $(obj).parent().addClass("active");
    loadPage($(obj).attr("data"));
    // console.log($(obj).html());
}

function nullToString(obj){
    if(obj){
        return obj;
    }else{
        return "";
    }
}