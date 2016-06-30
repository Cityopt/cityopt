<%--@elvariable id="extParam" type="eu.cityopt.DTO.ExtParamDTO"--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>CityOpt <spring:message code="add_external_param_set"/></title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0px" cellpadding="0px">
	<tr>
		<td>
			<%@ include file="mainmenu.inc"%>
		</td>
		<td width=30></td>
		<td>
			<div style="overflow:scroll;height:500px;width:500px;overflow:auto">
			<h1><spring:message code="create_external_param_set"/></h1>

			<table align="center">
				<col style="width:30px">
				<col style="width:250px">
				<col style="width:30px">
				<col style="width:250px">
				<tr>
					<td></td>
					<td>
						<spring:message code="external_param_sets"/>
					</td>
					<td></td>
					<td>
						<spring:message code="variables"/>
					</td>
				</tr>
				<tr>
					<td></td>
					<td>
						<table class="tablestyle">
							<col style="width:250px">
									
							<c:forEach items="${extparamsets}" var="extParamSet">
							<tr>
								<td>${extParamSet.name}</td>
						   	</tr>
							</c:forEach>
						</table>		
					</td>
					<td></td>
					<td>
						<table class="tablestyle">
							<col style="width:250px">
									
							<c:forEach items="${extparams}" var="extParam">
							<tr>
								<td>${extParam.name}</td>
						   	</tr>
							</c:forEach>
						</table>		
					</td>
				</tr>
				<tr height="10">
					<td>
					</td>
				</tr>
				<tr>
					<td></td>
					<td align="right"><a href="addextparamsets.html"><button type="button"><spring:message code="accept"/></button></a>
					<a href="editoptimizationset.html"><button style="width:100px" type="button" value="Cancel"><spring:message code="cancel"/></button></a></td>
				</tr>
			</table>
			</div>
		</td>
     </tr>
</table>
</body>
</html>