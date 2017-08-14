$(document).ready(function () {
    init();
});

function init(){

    var html = '<li><a href="javascript:void();" onclick="loadPage(\'./queryJobInit\')" >任务管理</a></li><li><a class="active"><strong>任务新增</strong></a></li>';
    changeNavi(html);


    validation();
    $('.i-checks').iCheck({
        checkboxClass: 'icheckbox_square-green',
        radioClass: 'iradio_square-green',
    });
    //添加条件
    $("#addParamsRow").click(function(){
        var len= $(".pa-f").length;
        // console.log(len);
        var paramsHtml = '<div class="row m-t pa-f">'+
            '<div class="col-md-4"><input type="text" placeholder="参数值" class="form-control" name="paramsList['+len+'].value"></div>'+
            '</div>';
        $("#paramsContent").append(paramsHtml);
    });
    //删除条件
    $("#deleteParamsRow").click(function(){
        $(".pa-f:last-child").remove();
    });

    //提交
    $("#submitButton").click(function(){
        if($("#jobForm").valid()){
            var ladda = Ladda.create(this);
            submitForm(ladda);
        };
    });
    //提交
    $("#cancelButton").click(function(){
        loadPage("./queryJobInit");
    });


}

function validation(){
    $("#jobForm").validate({
        rules: {
            jobAliasName: "required",
            projectId:"required",
            cronExpression:{
                required:true,
                remote:{
                    url: "./validExpression",     //后台处理程序
                    type: "post",               //数据发送方式
                    dataType: "json",           //接受数据格式
                    data: {                     //要传递的数据
                        cronExpression: function() {
                            return $("#cronExpression").val();
                        }
                    }
                }
            },
            url:"required",
            className:"required",
            methodName:"required"
        },
        messages: {
            jobAliasName: "请输入您的名字",
            projectId:"请选择您的项目",
            cronExpression:{
                required:"请输入表达式",
                remote:"请输入正确的Cron表达式"
            },
            url:"请输入url",
            className:"请输入类名",
            methodName:"请输入方法名"
        }
    });
}

function submitForm(ladda){
    ladda.start();
    var json = $("#jobForm").serialize();
    $.ajax({
        type : "POST",
        url : "./addJobDetail",
        data : $("#jobForm").serialize(),
        dataType:"json",
        success:function(data){
            ladda.stop();
            if(data.success){
                //成功
                toastr["success"]("新增任务成功");
                loadPage("./queryJobInit");
            }else{
                //失败
                toastr["error"]("请稍后再试","新增任务失败");
            }
        }
    });
}