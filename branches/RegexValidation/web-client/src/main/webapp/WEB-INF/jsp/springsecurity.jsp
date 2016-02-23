<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix='c' uri='http://java.sun.com/jsp/jstl/core' %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt security test page</title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>

<!-- Username display -->
Your username is <sec:authentication property="principal.username"/>
<!-- Credentials display -->
Your password is <sec:authentication property="principal.password"/>
<!-- Roles display -->
<sec:authentication property="authorities" var="roles" scope="page" />
Your roles are:
<ul>
    <c:forEach var="role" items="${roles}">
    <li>${role.authority}</li>
    </c:forEach>
</ul>

<sec:authorize access="@securityService.hasPermission('JSP')">						


</sec:authorize>
<sec:authorize access="hasPermission(#project,'ROLE_VIEWER')">


</sec:authorize>
<sec:authorize access="hasRole('ROLE_VIEWER')">


</sec:authorize>						
 

</body>
</html>