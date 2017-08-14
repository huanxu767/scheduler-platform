<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>

<%--<head>--%>

    <%--<meta charset="utf-8">--%>
    <%--<meta name="viewport" content="width=device-width, initial-scale=1.0">--%>

    <%--<title>任务明细</title>--%>

    <%--&lt;%&ndash;<link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">&ndash;%&gt;--%>
    <%--&lt;%&ndash;<link href="${pageContext.request.contextPath}/font-awesome/css/font-awesome.css" rel="stylesheet">&ndash;%&gt;--%>
    <%--&lt;%&ndash;<link href="${pageContext.request.contextPath}/css/plugins/iCheck/custom.css" rel="stylesheet">&ndash;%&gt;--%>
    <%--&lt;%&ndash;<link href="${pageContext.request.contextPath}/css/animate.css" rel="stylesheet">&ndash;%&gt;--%>
    <%--&lt;%&ndash;<link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">&ndash;%&gt;--%>
    <%--&lt;%&ndash;<link href="${pageContext.request.contextPath}/css/plugins/ladda/ladda-themeless.min.css" rel="stylesheet">&ndash;%&gt;--%>
    <%--&lt;%&ndash;<link href="${pageContext.request.contextPath}/css/plugins/awesome-bootstrap-checkbox/awesome-bootstrap-checkbox.css" rel="stylesheet">&ndash;%&gt;--%>

<%--</head>--%>

<body>


<div class="row">
    <div class="col-lg-12">
        <div class="ibox float-e-margins">
            <div class="ibox-content">
                <form method="post" class="form-horizontal" id="jobForm">
                    <div class="form-group"><label class="col-sm-2 control-label">名称</label>
                        <div class="col-sm-10">
                            <div class="input-group">
                                <p class="form-control-static">${scheduleJob.jobAliasName}</p>
                            </div>
                            </div>
                    </div>
                    <div class="hr-line-dashed"></div>
                    <div class="form-group"><label class="col-sm-2 control-label">项目</label>
                        <div class="col-sm-10">
                            <div class="input-group">
                            <p class="form-control-static">
                                    <c:forEach var="project" items="${projects}">
                                        <c:choose >
                                            <c:when test="${project.id == scheduleJob.projectId}">
                                                ${project.name}
                                            </c:when>
                                            <c:otherwise>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </p>
                            </div>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">功能描述</label>
                        <div class="col-sm-10">
                            <div class="input-group">
                            <p class="form-control-static">${scheduleJob.jobDesc}</p>
                            </div>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>

                    <div class="form-group"><label class="col-sm-2 control-label">Cron表达式</label>
                        <div class="col-sm-10">   <div class="input-group"><p class="form-control-static">${scheduleJob.cronExpression}</p>
                        </div>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>

                    <div class="form-group"><label class="col-sm-2 control-label">URL</label>
                        <div class="col-sm-10">
                            <div class="input-group">
                                <p class="form-control-static">http://${scheduleJob.url}</p>
                            </div>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">调用类型</label>
                        <div class="col-sm-10">
                            <div class="input-group">
                                <p class="form-control-static"><c:if test="${scheduleJob.jobType eq 1}"> 静态方法 </c:if>
                            <c:if test="${scheduleJob.jobType eq 2}"> SpringBean </c:if>
                                </p>
                            </div>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>
                    <div class="form-group"><label class="col-sm-2 control-label">类名</label>
                        <div class="col-sm-10">
                            <div class="input-group">
                            <p class="form-control-static">${scheduleJob.className}</p>
                            </div>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>

                    <div class="form-group"><label class="col-sm-2 control-label">方法名</label>
                        <div class="col-sm-10">
                            <div class="input-group">
                            <p class="form-control-static">${scheduleJob.methodName}</p>
                            </div>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">参数（字符串类型）</label>
                        <div class="col-sm-10" >
                            <c:forEach var="params" items="${scheduleJob.paramsList}" varStatus="status" >
                                <div class="row">
                                    <div class="col-md-4">
                                        <div class="input-group">
                                        <p class="form-control-static">${params.value}</p>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>

                    <div class="hr-line-dashed"></div>
                </form>

            </div>
        </div>
    </div>
</div>


<!-- Mainly scripts -->
<%--<script src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>--%>
<%--<script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>--%>
<%--<script src="${pageContext.request.contextPath}/js/plugins/metisMenu/jquery.metisMenu.js"></script>--%>
<%--<script src="${pageContext.request.contextPath}/js/plugins/slimscroll/jquery.slimscroll.min.js"></script>--%>

<%--<!-- Custom and plugin javascript -->--%>
<%--<script src="${pageContext.request.contextPath}/js/inspinia.js"></script>--%>
<%--<script src="${pageContext.request.contextPath}/js/plugins/pace/pace.min.js"></script>--%>

<%--<!-- iCheck -->--%>
<%--<script src="${pageContext.request.contextPath}/js/plugins/iCheck/icheck.min.js"></script>--%>
<%--<!-- Ladda -->--%>
<%--<script src="${pageContext.request.contextPath}/js/plugins/ladda/spin.min.js"></script>--%>
<%--<script src="${pageContext.request.contextPath}/js/plugins/ladda/ladda.min.js"></script>--%>
<%--<script src="${pageContext.request.contextPath}/js/plugins/ladda/ladda.jquery.min.js"></script>--%>

<%--<script src="${pageContext.request.contextPath}/js/jobManager/modifyJob.js"></script>--%>

</body>

</html>


