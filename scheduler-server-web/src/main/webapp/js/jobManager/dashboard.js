$(document).ready(function () {
    circleToLineChart(7);
    countAll();
});

function triggerDiv(data){
    $("#totalNum").html(data.COUNTS);
    $("#successNum").html(data.SUCESS_COUNTS);
    $("#failureNum").html(data.FAILURE_COUNTS);

    $("#totalProgressBar").css("width",'100%');
    $("#successProgressBar").css("width",data.SUCESS_COUNTS*100/data.COUNTS+'%');
    $("#failureProgressBar").css("width",data.FAILURE_COUNTS*100/data.COUNTS+'%');

}

function circleToLineChart(days,obj){
    if(obj){
        if($(obj).hasClass("active")){
            return;
        }else {
            $(".btn-group button").removeClass("active");
            $(obj).addClass("active")
        }
    }
    var lineChart = echarts.init(document.getElementById("triggleCharts"));
    lineChart.showLoading();
    var kParams = { days: days};
    $("#totalProgressBar").css("width",'0%');
    $("#successProgressBar").css("width",'0%');
    $("#failureProgressBar").css("width",'0%');


    $.ajax({
        type : "GET",
        url : "./countTriggers",
        data : kParams ,
        dataType:"json",
        success:function(data){
            lineChart.hideLoading();
            triggerDiv(data.map);
            var date =[]
            var actual = [];
            $.each(data.list, function(idx, obj) {
                date.push(obj.CREATE_DAY);
                actual.push(obj.COUNTS);
            });
            var barOption = {
                tooltip : {
                    trigger: 'axis',
                    axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                        type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                    }
                },
                legend: {
                    align:'right',
                    right:80,
//                        top:30,
                    data:[""]
                },
                grid: [
                    {x: '0%', y: '0%', width: '100%', height: '90%'}
                ],
                color:['#a3e1d4','#8ac8e6'],
                calculable : true,
                xAxis : [
                    {
                        type : 'category',
                        data:date
                    }
                ],
                yAxis : [
                    {
                        type : 'value'
                    }
                ],
                series : [
                    {
                        name:"触发次数",
                        type:'line',
                        data:actual
                    }
                ]
            };
            lineChart.setOption(barOption);
        }
    });
}

function countAll(){
    $.ajax({
        type : "GET",
        url : "./countAllTriggers",
        dataType:"json",
        success:function(data){
            console.dir(data);
            $("#jobPause").html(objectToint(data.job2));
            $("#jobDoing").html(objectToint(data.job3) + objectToint(data.job1));
            $("#clientDoing").html(objectToint(data.trigger1));
            $("#triggerFailure").html(objectToint(data.trigger2))   ;
            $("#clientDoneSuccess").html(objectToint(data.trigger3))   ;
            $("#clientDoneFailure").html(objectToint(data.trigger4))   ;
        }
    });
}

function objectToint(obj){
    if(obj){
        return obj;
    }else{
        return 0 ;
    }
}

