<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="component" type="com.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="selectedComponent" type="com.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="outputVar" type="com.cityopt.DTO.OutputVariableDTO"--%>
<%--@elvariable id="selectedcompid" type="int"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt Output variables</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>
		<td width="30"></td>
		<td valign="top">
			<div style="overflow:scroll;height:600px;width:1100px;overflow:auto">
			<table>
				<col style="width:40px">
				<col style="width:30px">
				<col style="width:850px">	
				<tr>
					<td></td>
					<td colspan="2" height="80">
						<!-- Output variables -->
						<h2><spring:message code="output_variables"/></h2>
					</td>
					<td></td>
				</tr>
				<tr>
					<td></td>
					<td></td>
					<td>
						<table>
							<tr>
								<td>
									<table width="550">
										<col style="width:150px">
										<col style="width:50px">
										<col style="width:350px">
										<tr>
											<!-- Components -->
											<td><b><spring:message code="components"/></b></td>
											<td></td>
											<!-- Output variables -->
											<td><b><spring:message code="output_variables"/></b></td>
										</tr>
										<tr>						
											<td>
												<table class="tablestyle">
													<col style="width:60px">
													<col style="width:150px">
													<col style="width:60px">
													<tr>
														<!-- Select -->
														<th><spring:message code="select"/></th>
														<!-- Component -->
														<th><spring:message code="component"/></th>
													</tr>
										
													<c:forEach items="${components}" var="component">
													<tr>
														<c:if test="${selectedcompid == component.componentid}">
															<tr style="background-color: #D4D4D4"><td>Selected</td>
														</c:if>
														<c:if test="${selectedcompid != component.componentid}">
															<tr>
															<td><a href="<c:url value='outputvariables.html?selectedcompid=${component.componentid}'/>">Select</a></td>
														</c:if>
															<td>${component.name}</td>
													</tr>
													</c:forEach>
												</table>
											</td>
											<td>
											</td>
											<td valign="top">
												<table class="tablestyle">
													<col style="width:250px">
													<col style="width:100px">
													<col style="width:50px">
													<tr>
														<!-- Output variable -->
														<th><spring:message code="output_variable"/></th>
														<!-- Unit -->
														<th><spring:message code="unit"/></th>
														<th><spring:message code="edit"/></th>
													</tr>
								
													<c:forEach items="${outputVariables}" var="outputVar">
													<tr>
														<td>${outputVar.name}</td>
												    	<td>${outputVar.unit.name}</td>
												    	<td><a href="editoutputvariable.html?outputvarid=${outputVar.outvarid}">
												    		<button type="button"><spring:message code="edit"/></button>
											    			</a>
										    			</td>
													</tr>
													</c:forEach>
												</table>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td>		
									<table width="100%">
									
										<tr height="30">
											<td></td>
										</tr>
										<tr>
											<td align="right">
												<a href="editproject.html"><button type="button"><spring:message code="close"/></button></a>
										    </td>
										</tr>							      
									</table>
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