<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<div class="row">
    <div class="col-lg-12">
        <div class="ibox float-e-margins">
            <div class="ibox-title">
                <h5>修改任务
                    <%--<small>With custom checbox and radion elements.</small>--%>
                </h5>
            </div>
            <div class="ibox-content">
                <form method="post" class="form-horizontal" id="jobForm">
                    <input type="hidden" name="scheduleJobId" value="${scheduleJob.scheduleJobId}">
                    <div class="form-group"><label class="col-sm-2 control-label">名称</label>
                        <div class="col-sm-10"><input type="text" class="form-control" name="jobAliasName" value="${scheduleJob.jobAliasName}"/></div>
                    </div>
                    <div class="hr-line-dashed"></div>

                    <div class="form-group"><label class="col-sm-2 control-label">项目</label>
                        <div class="col-sm-10">
                            <select class="form-control m-b" name="projectId">
                                <c:forEach var="project" items="${projects}">
                                    <c:choose >
                                        <c:when test="${project.id == scheduleJob.projectId}">
                                            <option value="${project.id}" selected="selected">${project.name}</option>
                                        </c:when>
                                        <c:otherwise>
                                            <option value="${project.id}" >${project.name}</option>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>

                    <div class="form-group"><label class="col-sm-2 control-label">功能描述</label>
                        <div class="col-sm-10"><input type="text" class="form-control" name="jobDesc" value="${scheduleJob.jobDesc}"></div>
                    </div>
                    <div class="hr-line-dashed"></div>

                    <div class="form-group"><label class="col-sm-2 control-label">Cron表达式</label>
                        <div class="col-sm-10"><input type="text" class="form-control" name="cronExpression" id="cronExpression" placeholder="" value="${scheduleJob.cronExpression}"></div>
                    </div>
                    <div class="hr-line-dashed"></div>

                    <div class="form-group"><label class="col-sm-2 control-label">URL</label>
                        <div class="col-sm-10">
                            <div class="input-group m-b"><span class="input-group-addon">http://</span> <input type="text" name="url" class="form-control" value="${scheduleJob.url}"></div>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">调用类型</label>
                        <div class="col-sm-10">
                            <div class="i-checks"><label> <input type="radio" value="1" name="jobType"  <c:if test="${scheduleJob.jobType eq 1}"> checked="checked" </c:if> > <i></i> 静态方法 </label></div>
                            <div class="i-checks"><label> <input type="radio"  value="2" name="jobType" <c:if test="${scheduleJob.jobType eq 2}"> checked="checked" </c:if> > <i></i> SpringBean </label></div>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>
                    <div class="form-group"><label class="col-sm-2 control-label">类名</label>
                        <div class="col-sm-10"><input type="text" class="form-control" name="className" value="${scheduleJob.className}" /></div>
                    </div>
                    <div class="hr-line-dashed"></div>

                    <div class="form-group"><label class="col-sm-2 control-label">方法名</label>
                        <div class="col-sm-10"><input type="text" class="form-control" name="methodName" value="${scheduleJob.methodName}"/></div>
                    </div>
                    <div class="hr-line-dashed"></div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">参数（字符串类型）</label>
                        <div class="col-sm-10" id="paramsContent">

                            <div class="row" >
                                <div class="col-md-4">
                                    <button type="button" class="btn btn-outline btn-sm btn-primary" id="addParamsRow">添加</button>
                                    <button type="button" class="btn btn-outline  btn-sm btn-danger" id="deleteParamsRow">删除</button>
                                </div>
                            </div>

                            <c:forEach var="params" items="${scheduleJob.paramsList}" varStatus="status" >
                                <div class="row m-t  pa-f">
                                    <div class="col-md-4"><input type="text" placeholder="参数值" class="form-control" name="paramsList[${status.index}].value" value="${params.value}"></div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>

                    <div class="hr-line-dashed"></div>
                    <div class="form-group">
                        <div class="col-sm-4 col-sm-offset-2">
                            <button class="btn btn-white" type="button" id="cancelButton">取消</button>
                            <button class="ladda-button btn btn-primary " id="submitButton" data-style="expand-right"><span
                                    class="ladda-label">修改</span><span class="ladda-spinner"></span>
                                <div class="ladda-progress" style="width: 0px;"></div>
                            </button>
                        </div>
                    </div>
                </form>

            </div>
        </div>
    </div>
</div>


<script src="${pageContext.request.contextPath}/js/jobManager/modifyJob.js"></script>


