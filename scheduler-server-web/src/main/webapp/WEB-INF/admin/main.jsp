<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>

<head>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>调度平台</title>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/font-awesome/css/font-awesome.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/plugins/iCheck/custom.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/animate.css" rel="stylesheet">
    <!-- Toastr style -->
    <link href="${pageContext.request.contextPath}/css/plugins/toastr/toastr.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/plugins/ladda/ladda-themeless.min.css" rel="stylesheet">
    <!-- Sweet Alert -->
    <link href="${pageContext.request.contextPath}/css/plugins/sweetalert/sweetalert.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/plugins/awesome-bootstrap-checkbox/awesome-bootstrap-checkbox.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/main.css" rel="stylesheet">

</head>


<body class="">

<div id="wrapper">

    <nav class="navbar-default navbar-static-side" role="navigation">
        <div class="sidebar-collapse">
            <ul class="nav metismenu" id="side-menu">
                <li class="nav-header">
                    <div class="dropdown profile-element"> <span>
                            <img alt="image" class="img-circle" src="${pageContext.request.contextPath}/img/headermy.png" width="40px"/>
                             </span>
                        <a data-toggle="dropdown" class="dropdown-toggle" href="#">
                            <span class="clear"> <span class="block m-t-xs"> <strong class="font-bold">JAVA组</strong>
                             </span>
                                <span class="text-muted text-xs block">系统管理员 <b class="caret"></b></span>
                            </span> </a>
                        <ul class="dropdown-menu animated fadeInRight m-t-xs">
                            <%--<li><a href="profile.html">Profile</a></li>--%>
                            <%--<li><a href="contacts.html">Contacts</a></li>--%>
                            <%--<li><a href="mailbox.html">Mailbox</a></li>--%>
                            <%--<li class="divider"></li>--%>
                            <li><a href="login.html">退出</a></li>
                        </ul>
                    </div>
                    <div class="logo-element">
                        IN+
                    </div>
                </li>
                <li class="active">
                    <a href="./main"><i class="fa fa-th-large"></i> <span class="nav-label">首页</span></a>
                </li>
                <li>
                    <a href="javascript:void(0)" onclick="goMenu(this)" data="./queryJobInit"><i class="fa fa-diamond"></i> <span class="nav-label">任务管理</span></a>
                </li>
                <li>
                    <a href="javascript:void(0)" onclick="goMenu(this)" data="./queryLoggerInit"><i class="fa fa-pie-chart"></i> <span class="nav-label">日志管理</span></a>
                </li>
                <li>
                    <a href="#"><i class="fa fa-laptop"></i> <span class="nav-label">系统管理 </span><span class="fa arrow"></span></a>
                    <ul class="nav nav-second-level collapse">
                        <li><a href="#">项目管理</a></li>
                    </ul>
                </li>
            </ul>
        </div>
    </nav>

    <div id="page-wrapper" class="gray-bg">


        <div class="row border-bottom">
            <nav class="navbar navbar-static-top  " role="navigation" style="margin-bottom: 0">
                <div class="navbar-header">
                    <a class="navbar-minimalize minimalize-styl-2 btn btn-primary " href="#"><i class="fa fa-bars"></i> </a>
                    <form role="search" class="navbar-form-custom" action="search_results.html">
                        <div class="form-group">
                            <%--<input type="text" placeholder="Search for something..." class="form-control" name="top-search" id="top-search">--%>
                        </div>
                    </form>
                </div>
                <ul class="nav navbar-top-links navbar-right">
                    <li>
                        <span class="m-r-sm text-muted welcome-message">欢迎来到 调度平台</span>
                    </li>
                    <li>
                        <a href="login.html">
                            <i class="fa fa-sign-out"></i>
                        </a>
                    </li>
                </ul>

            </nav>
        </div>

        <div class="row wrapper border-bottom white-bg my-page-heading" id="pageHeading" style="display: none;">
            <div class="col-lg-10">
                <ol class="breadcrumb">
                    <li>
                        <a href="./main">首页</a>
                    </li>
                </ol>
            </div>
            <div class="col-lg-2">

            </div>
        </div>

        <div class="wrapper wrapper-content">
                <%--<iframe src="./eryJobInit" id="iframepage" name="iframepage" frameBorder=0  width="100%" onLoad="iFrameHeight()" ></iframe>--%>
        </div>
    </div>
</div>




<!-- Mainly scripts -->
<script src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/js/plugins/metisMenu/jquery.metisMenu.js"></script>
<script src="${pageContext.request.contextPath}/js/plugins/slimscroll/jquery.slimscroll.min.js"></script>

<!-- Custom and plugin javascript -->
<script src="${pageContext.request.contextPath}/js/inspinia.js"></script>
<script src="${pageContext.request.contextPath}/js/plugins/pace/pace.min.js"></script>

<!-- iCheck -->
<script src="${pageContext.request.contextPath}/js/plugins/iCheck/icheck.min.js"></script>
<!-- Ladda -->
<script src="${pageContext.request.contextPath}/js/plugins/ladda/spin.min.js"></script>
<script src="${pageContext.request.contextPath}/js/plugins/ladda/ladda.min.js"></script>
<script src="${pageContext.request.contextPath}/js/plugins/ladda/ladda.jquery.min.js"></script>
<!-- Toastr script -->
<script src="${pageContext.request.contextPath}/js/plugins/toastr/toastr.min.js"></script>
<!-- Sweet alert -->
<script src="${pageContext.request.contextPath}/js/plugins/sweetalert/sweetalert.min.js"></script>
<script src="${pageContext.request.contextPath}/js/plugins/echarts/echarts.js"></script>
<script src="${pageContext.request.contextPath}/js/lib/jquery.page.js"></script>
<!-- Jquery Validate -->
<script src="${pageContext.request.contextPath}/js/plugins/validate/jquery.validate.min.js"></script>

<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>


