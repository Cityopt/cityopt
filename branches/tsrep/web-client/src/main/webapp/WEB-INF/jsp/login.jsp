<%--@elvariable id="user" type="com.cityopt.DTO.AppUserDTO"--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><spring:message code="virtual_city_login"/></title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
<style>
td.login{
baground-color:#FBBA00;
}

.login{
color: #4D4D4D;

}

}
</style>

</head>

<!-- onload='document.f.username.focus();'  -->

<body>
<!-- Logout? -->
	<c:if test="${param.logout != null}">
	<b><spring:message code="good_bye"/></b>	
	</c:if>

	<form:form name='f' action='login' method="post" modelAttribute="user">
		<sec:csrfInput />
		<table height="600px" align="center">
			<tr height="200px">
			</tr>
			<tr>
				<td>
					<div class="login">
						<table>
							<tr>
								<td align="center"><img  src="assets/img/icon_logo_big.jpg" />
								</td>
							</tr>
							<tr>
								<td>
									<h2 align="center" class="error">${errorMsg}</h2>
								</td>
							</tr>
							<tr>
								<td align="center"><form:input class="login" id="name"
										path="name" type="text" value="admin" style="width: 100px" /></td>
							</tr>
							<tr>
								<td align="center"><form:input class="login" id="password"
										path="password" type="password" value="admin"
										style="width: 100px" /></td>
							
							</tr>
							
							<tr align="left">
								<td>
									<font color="red">
									<!-- Bad Credentials -->
											<c:if test="${param.error != null}">				   					
							   						<c:if test="${SPRING_SECURITY_LAST_EXCEPTION != null }">					   						
									     				<c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"></c:out> 
													</c:if>
											</c:if>		
									</font>															
								</td>
							</tr>
							
							
							<tr height="10"></tr>
							<tr>
								<td class="login" align="center">
									<button class="activebutton" type="submit" style="width: 100px"><spring:message code="login"/></button>
								</td>
							</tr>
							
							<tr>

							</tr>
						</table>
					</div>
				</td>
			</tr>
		</table>
	</form:form>
</body>
</html>

