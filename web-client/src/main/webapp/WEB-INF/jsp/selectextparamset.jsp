<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="constraint" type="eu.cityopt.DTO.OptConstraintDTO"--%>
<%--@elvariable id="extParamValSet" type="eu.cityopt.DTO.ExtParamValSetDTO"--%>
<%--@elvariable id="selectedExtParamSetId" type="int"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt select external parameter set</title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
<table cellspacing="0" cellpadding="0">
	<tr>
		<td>
			<%@ include file="mainmenu.inc"%>
		</td>
		<td width="30"></td>
		<td valign="top">
			<table>
				<tr>
					<td>
						<!-- Select external parameter set -->
						<h2><spring:message code="select_external_parameter_set"/></h2>
					</td>
				</tr>
				<tr>
					<td>
						<table>
							<col style="width:250px">
							<col style="width:30px">
							<col style="width:350px">

							<tr>
								<!--External parameter sets-->
								<td><b><spring:message code="external_parameter_sets"/></b></td>
								<td></td>
								<td></td>
							</tr>
							<tr>
								<td valign="top">
									<table class="tablestyle">
										<col style="width:60px">
										<col style="width:190px">
										<tr>
											<!-- Select -->
											<th><spring:message code="select"/></th>
											<!-- Name -->
											<th><spring:message code="name"/></th>
										</tr>
							
										<!-- Select & Selected  -->
										<c:forEach items="${extParamValSets}" var="extParamValSet">
											<c:choose>
												<c:when test="${selectedExtParamSetId == extParamValSet.extparamvalsetid}">
													<tr style="background-color: #D4D4D4"><td>
													<spring:message code="selected"/></td>
												</c:when>
												<c:otherwise>
													<tr>
														<td><a href="<c:url value='selectextparamset.html?selectedextparamsetid=${extParamValSet.extparamvalsetid}'/>">
														<spring:message code="select"/></a></td>
												</c:otherwise>
											</c:choose>	

											<td>${extParamValSet.name}</td>
									   	</tr>
									   	</c:forEach>
									</table>
								</td>
								<td></td>
							</tr>
							<tr height="20"></tr>
							<tr>
								<!-- Create external parameter set & back button -->
								<td align="right">
									<a href="createextparamset.html"><button type="button"><spring:message code="create_external_parameter_set"/></button></a>
									<a href="extparams.html"><button type="button"><spring:message code="back"/></button></a>
								</td>
								<td></td>
								<td></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr height=20></tr>
			</table>
		</td>
	</tr>
</table>
</body>
</html>