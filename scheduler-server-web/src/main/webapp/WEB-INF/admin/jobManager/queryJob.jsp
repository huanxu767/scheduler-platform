<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
    <div class="ibox" id="ibox">
        <div class="ibox-title">
            <h5>任务查询</h5>
        </div>
        <div class="ibox-content">
            <div class="sk-spinner sk-spinner-wave">
                <div class="sk-rect1"></div>
                <div class="sk-rect2"></div>
                <div class="sk-rect3"></div>
                <div class="sk-rect4"></div>
                <div class="sk-rect5"></div>
            </div>
            <div class="form-horizontal">
                <div class="form-group">

                    <label class="col-md-1 control-label">名称</label>
                    <div class="col-md-2"><input type="text" id="jobAliasName"  class="form-control">
                    </div>

                    <label class="col-md-1 control-label">所属项目</label>
                    <div class="col-md-2">
                        <select class="form-control m-b" name="projectId" id="projectId">
                            <option value="" >请选择</option>
                            <c:forEach var="project" items="${projects}">
                                <option value="${project.id}" >${project.name}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="col-md-6">
                        <button type="button" class="btn btn-w-m btn-primary" id="searchButton">查询</button>
                        <button type="button" class="btn btn-w-m btn-default" id="addButton">新增</button>
                    </div>

                </div>
            </div>

            <div class="table-responsive">
                <table class="table table-striped">
                    <thead>
                    <tr>
                        <th>名称 </th>
                        <th>Cron表达式</th>
                        <th>项目</th>
                        <th>URL</th>
                        <th>类名</th>
                        <th>方法名</th>
                        <th>状态</th>
                        <%--<th>创建时间</th>--%>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody id="jobList">
                    </tbody>
                    <tfoot>
                    <tr>
                        <td colspan="11" class="footable-visible">
                            <div class="tcdPageCode pagination">
                            </div>
                        </td>
                    </tr>
                    </tfoot>
                </table>
            </div>
        </div>
    </div>



<div class="modal inmodal fade" id="myModal5" tabindex="-1" role="dialog" aria-hidden="true" style="display: none;">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">×</span><span class="sr-only">Close</span></button>
                <div id="ct">

                </div>
            </div>
        </div>
    </div>
</div>

    <script src="${pageContext.request.contextPath}/js/jobManager/queryJob.js"></script>



