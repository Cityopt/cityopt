<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt settings</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0px" cellpadding="0px">
	<tr>
		<td>
			<%@ include file="mainmenu.inc"%>
		</td>

		<td width=30></td>
		<td valign="top">
			<div style="overflow:scroll;height:100%;width:600px;overflow:auto">
			<table>
				<tr>
					<td>
						<h2><spring:message code="settings"/></h2>
					</td>
				</tr>
				<tr>
					<td>
						<table class="tablestyle" width="250px">
							<col style="width:100px">	
							<col style="width:150px">	
							
							<tr height="20">
								<th><spring:message code="select"/></th>
							    <th><spring:message code="language"/></th>
							</tr>
							<tr>
								<td>
								    <a href="?lang=fi"><button type=button><spring:message code="set"/></button></a>
								</td>
						   		<td>
						   			Finnish
							    </td>
						    </tr>
						    <tr>
								<td>
								    <a href="?lang=en"><button type=button><spring:message code="set"/></button></a>
								</td>
						   		<td>
						   			English
							    </td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
			</div>
		</td>
     </tr>
</table>
</body>
</html>