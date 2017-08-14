<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<div class="row">

<div class="col-lg-12">
    <div class="ibox float-e-margins">
        <div class="ibox-title">
            <h5>触发统计</h5>
            <div class="pull-right">
                <div class="btn-group">
                    <button type="button" class="btn btn-xs btn-white active" onclick="circleToLineChart(7,this)">最近7天</button>
                    <button type="button" class="btn btn-xs btn-white" onclick="circleToLineChart(30,this)">最近30天</button>
                </div>
            </div>
        </div>
        <div class="ibox-content">
            <div class="row">
                <div class="col-lg-9">
                    <div class="flot-chart" id="triggleCharts">

                    </div>
                </div>
                <div class="col-lg-3">
                    <ul class="stat-list">
                        <li>
                            <h2 class="no-margins" id="totalNum">-</h2>
                            <small>总触发笔数</small>
                            <div class="progress progress-mini">
                                <div  class="progress-bar" id="totalProgressBar"></div>
                            </div>
                        </li>
                        <li>
                            <h2 class="no-margins " id="successNum">-</h2>
                            <small>执行成功笔数</small>
                            <div class="progress progress-mini">
                                <div  class="progress-bar" id="successProgressBar"></div>
                            </div>
                        </li>
                        <li>
                            <h2 class="no-margins " id="failureNum">-</h2>
                            <small>失败笔数(触发失败、执行失败)</small>
                            <div class="progress progress-mini">
                                <div  class="progress-bar progress-bar-danger" id="failureProgressBar"></div>
                            </div>
                        </li>
                    </ul>
                </div>
            </div>
        </div>

    </div>
</div>
</div>

<div class="row">
        <div class="col-lg-3">
            <div class="ibox float-e-margins">
                <div class="ibox-title">
                    <span class="label label-info pull-right">执行中</span>
                    <h5>任务数</h5>
                </div>
                <div class="ibox-content">
                    <h1 class="no-margins" id="jobDoing">-</h1>
                </div>
            </div>
        </div>
        <div class="col-lg-3">
            <div class="ibox float-e-margins">
                <div class="ibox-title">
                    <span class="label label-warn  pull-right">暂停</span>
                    <h5>任务数</h5>
                </div>
                <div class="ibox-content">
                    <h1 class="no-margins" id="jobPause">-</h1>
                </div>
            </div>
        </div>

    </div>

<%--<div class="row">--%>
    <%--<div class="col-lg-3">--%>
        <%--<div class="ibox float-e-margins">--%>
            <%--<div class="ibox-title">--%>
                <%--<span class="label label-info pull-right"></span>--%>
                <%--<h5>客户端执行中</h5>--%>
            <%--</div>--%>
            <%--<div class="ibox-content">--%>
                <%--<h1 class="no-margins" id="clientDoing">-</h1>--%>
                <%--&lt;%&ndash;<div class="stat-percent font-bold text-success">98% <i class="fa fa-bolt"></i></div>&ndash;%&gt;--%>
                <%--&lt;%&ndash;<small>总触发次数</small>&ndash;%&gt;--%>
            <%--</div>--%>
        <%--</div>--%>
    <%--</div>--%>
    <%--<div class="col-lg-3">--%>
        <%--<div class="ibox float-e-margins">--%>
            <%--<div class="ibox-title">--%>
                <%--<span class="label label-danger pull-right">失败</span>--%>
                <%--<h5>触发次数</h5>--%>
            <%--</div>--%>
            <%--<div class="ibox-content">--%>
                <%--<h1 class="no-margins" id="triggerFailure">-</h1>--%>
            <%--</div>--%>
        <%--</div>--%>
    <%--</div>--%>

    <%--<div class="col-lg-3">--%>
        <%--<div class="ibox float-e-margins">--%>
            <%--<div class="ibox-title">--%>
                <%--<span class="label label-success pull-right">成功</span>--%>
                <%--<h5>执行次数</h5>--%>
            <%--</div>--%>
            <%--<div class="ibox-content">--%>
                <%--<h1 class="no-margins" id="clientDoneSuccess">-</h1>--%>
            <%--</div>--%>
        <%--</div>--%>
    <%--</div>--%>

    <%--<div class="col-lg-3">--%>
        <%--<div class="ibox float-e-margins">--%>
            <%--<div class="ibox-title">--%>
                <%--<span class="label label-danger pull-right">失败</span>--%>
                <%--<h5>执行次数</h5>--%>
            <%--</div>--%>
            <%--<div class="ibox-content">--%>
                <%--<h1 class="no-margins" id="clientDoneFailure">-</h1>--%>
            <%--</div>--%>
        <%--</div>--%>
    <%--</div>--%>
<%--</div>--%>

<div class="row">
    <div class="col-lg-12">
    <div class="ibox float-e-margins">
        <div class="ibox-title">
            <%--<span class="label label-primary pull-right">任务执行分析</span>--%>
            <h5>任务执行分析</h5>
        </div>
        <div class="ibox-content">

            <div class="row">
                <div class="col-md-3">
                    <h1 class="no-margins" id="clientDoing">-</h1>
                    <div class="font-bold text-navy"><small>客户端执行中</small></div>
                </div>
                <div class="col-md-3">
                    <h1 class="no-margins" id="triggerFailure">-</h1>
                    <div class="font-bold text-navy"><small>触发失败</small></div>
                </div>
                <div class="col-md-3">
                    <h1 class="no-margins" id="clientDoneSuccess">-</h1>
                    <div class="font-bold text-navy"><small>执行成功</small></div>
                </div>
                <div class="col-md-3">
                    <h1 class="no-margins" id="clientDoneFailure">-</h1>
                    <div class="font-bold text-navy"><small>执行失败</small></div>
                </div>
            </div>


        </div>
    </div>
</div>
</div>
<script src="${pageContext.request.contextPath}/js/jobManager/dashboard.js"></script>






