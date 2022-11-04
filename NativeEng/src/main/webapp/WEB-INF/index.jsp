<%--
  Created by IntelliJ IDEA.
  User: coffe
  Date: 01.11.2022
  Time: 23:34
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String pageBody = "/WEB-INF/" + request.getAttribute("pageBody");
    String home = request.getContextPath();
%>
<html>
<head>
    <meta charset="UTF-8" />
    <title>NativeEnglish</title>
    <link rel="stylesheet" href="<%=home%>/resources/css/header.css">
</head>
<body>
<jsp:include page="header.jsp"/>
<jsp:include page="<%= pageBody%>"/>
</body>
</html>
