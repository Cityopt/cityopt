<%--@elvariable id="user" type="eu.cityopt.DTO.AppUserDTO"--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>CityOpt <spring:message code="edit_user"/></title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0px" cellpadding="0px">
<form:form action="changepassword.html?userid=${user.userid}" method="post" modelAttribute="user">
	<tr>
		<td>
			<%@ include file="mainmenu.inc"%>
		</td>
		<td valign="top">
			<div style="overflow: auto; height: 100%; width: 1200px; overflow: auto;">
			<table class="maintable">			
				<%@ include file="toprow.inc"%>
				<tr class="titlerow">
					<td class="spacecolumn"></td>
					<td>
						<table width="100%">
							<tr>
								<td>
                           			<spring:message code="change_password"/>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td class="spacecolumn"></td>
					<td class="error">${error}</td>
				</tr>
				<tr>
					<td class="spacecolumn"></td>
					<td class="info">${info}</td>
				</tr>
				<tr>
					<td class="spacecolumn"></td>
					<td valign="top">
						<table>
							<tr>
								<td>
									<table align="left">
										<col style="width:80px">
										<col style="width:250px">
									
										<tr>
											<td class="infosmall">
												<!--Name-->
												<spring:message code="name"/>
											</td>
										</tr>
										<tr>
											<td class="activeline">
												${user.name}
											</td>
										</tr>
										<tr class="spacerow"></tr>
										<tr>
											<td class="infosmall">
												<spring:message code="new_password"/>*
											</td>
										</tr>
										<tr>
											<td>
												<form:input style="width:300px" title="${tooltip_edit}" type="text" path="password"/>
											</td>
										</tr>
										<tr height="10">
											<td>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td align="left">
									<button class="activebutton" type="submit" style="width:160px"><spring:message code="change_password" /></button>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
			</div>
		</td>
     </tr>
   </form:form>
</table>
</body>
</html>