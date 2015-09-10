<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="constraint" type="eu.cityopt.DTO.SearchConstraintDTO"--%>
<%--@elvariable id="component" type="eu.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="inputParam" type="eu.cityopt.DTO.InputParameterDTO"--%>
<%--@elvariable id="outputVar" type="eu.cityopt.DTO.OutputVariableDTO"--%>
<%--@elvariable id="metric" type="eu.cityopt.DTO.MetricDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt create optimization constraint</title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
<form:form method="post" action="createconstraint.html?action=create" modelAttribute="constraint">
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
						<!-- Create constraint -->
						<h2><spring:message code="create_constraint"/></h2>
					</td>
				</tr>
				<tr>
					<td>
						<table>
							<col style="width:250px">
							<col style="width:30px">
							<col style="width:250px">
							<col style="width:30px">
							<col style="width:250px">

							<tr>
								<!-- Components -->
								<td><b><spring:message code="components"/></b></td>
								<td></td>
								<!-- Input parameters -->
								<td><b><spring:message code="input_parameters"/></b></td>
								<td></td>
								<!-- Output variables -->
								<td><b><spring:message code="output_variables"/></b></td>
							</tr>
							<tr>
								<td valign="top">
									<table class="tablestyle">
										<col style="width:60px">
										<col style="width:190px">
										<tr>
											<!-- Select -->
											<th><spring:message code="select"/></th>
											<!-- Component -->
											<th><spring:message code="component"/></th>
										</tr>
							
										<c:forEach items="${components}" var="component">
										<tr>
											<c:if test="${selectedcompid == component.componentid}">
												<tr style="background-color: #D4D4D4"><td>
												<spring:message code="selected"/></td>
											</c:if>
											<c:if test="${selectedcompid != component.componentid}">
												<tr>
												<td><a href="<c:url value='createconstraint.html?selectedcompid=${component.componentid}'/>">
												<spring:message code="select"/></a></td>
											</c:if>
												<td>${component.name}</td>
									   	</tr>
										</c:forEach>
									</table>
								</td>
								<td></td>
								<td valign="top">
									<table class="tablestyle">
										<col style="width:250px">
										<tr>
											<!-- Input parameter -->
											<th><spring:message code="input_parameter"/></th>
										</tr>
					
										<c:forEach items="${inputParams}" var="inputParam">
										<tr>
											<td>${inputParam.name}</td>
										</tr>
										</c:forEach>
									</table>
								</td>
								<td></td>
								<td valign="top">
									<table class="tablestyle">
										<col style="width:250px">
										<tr>
											<!-- Output variable -->
											<th><spring:message code="output_variable"/></th>
										</tr>
					
										<c:forEach items="${outputVars}" var="outputVar">
										<tr>
											<td>${outputVar.name}</td>
										</tr>
										</c:forEach>
									</table>
								</td>
							</tr>
							<tr height="20"></tr>
							<tr>
								<!-- Metrics -->
								<td><b><spring:message code="metrics"/></b></td>
							</tr>
							<tr>
								<td>
									<table class="tablestyle">
										<col style="width:250px">
										
										<tr>
											<!-- Metric -->
											<th><spring:message code="metric"/></th>
										</tr>
					
										<c:forEach items="${metrics}" var="metric">
										<tr>
											<td>${metric.name}</td>
									   	</tr>
										</c:forEach>
									</table>										
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr height=20></tr>
				<tr>
					<td>
						<table>						
							<col style="width:30px">
							<col style="width:150px">
							<col style="width:400px">
							<col style="width:240px">
							<tr>
								<td></td>
								<!-- Name -->
								<td><spring:message code="name"/></td>
								<td><form:input style="width:400px" type="text" path="name"/></td>
								<td></td>
							</tr>
							<tr height=10></tr>
							<tr>
								<td></td>
								<!-- Lower bound -->
								<td><spring:message code="lower_bound"/></td>
								<td><form:input style="width:400px" type="text" path="lowerbound"/></td>
								<td></td>
							</tr>
							<tr height=10></tr>
							<tr>
								<td></td>
								<!-- Expression -->
								<td><spring:message code="expression"/></td>
								<td><form:input style="width:400px" type="text" path="expression"/></td>
								<td></td>
							</tr>
							<tr height=10></tr>
							<tr>
								<td></td>
								<!-- Upper bound -->
								<td><spring:message code="upper_bound"/></td>
								<td><form:input style="width:400px" type="text" path="upperbound"/></td>
								<td></td>
							</tr>
							<tr>
								<td></td>
								<td></td>
								<td></td>
								<!-- Ok and Cansel submit -buttons -->
								<td align=right><input type="submit" value="<spring:message code="ok"/>"/>
								<a href="editoptimizationset.html"><button type="button">
								<spring:message code="cancel"/></button></a></td>
							</tr>					
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</form:form>
</body>
</html>