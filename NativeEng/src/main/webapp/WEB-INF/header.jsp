<%--
  Created by IntelliJ IDEA.
  User: coffe
  Date: 02.11.2022
  Time: 19:56
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String home = request.getContextPath() ;
%>
<header>
    <img class="logotype" src="<%=home%>/resources/img/native_english.jpg" alt="logotype">
    <div id="menu">
        <ul>
            <li><a href="#">Main</a></li>
            <li><a href="#">About us</a></li>
        </ul>
    </div>
</header>
