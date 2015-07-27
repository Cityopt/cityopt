<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="constraint" type="eu.cityopt.DTO.OptConstraintDTO"--%>
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
<title>CityOpt external parameter sets</title>
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
						<!--External parameter sets  -->
						<h2><spring:message code="external_parameter_sets"/></h2>
					</td>
				</tr>
				<tr>
					<td>
						<table>
							<col style="width:250px">
							<col style="width:30px">
							<col style="width:350px">

							<tr>
								<!--External parameter sets  -->
								<td><b><spring:message code="external_parameter_sets"/></b></td>
								<td></td>
								<!-- External parameters -->
								<td><b><spring:message code="external_parameters"/></b></td>
							</tr>
							<tr>
								<td valign="top">
									<table class="tablestyle">
										<col style="width:60px">
										<col style="width:190px">
										<tr>
											<!-- Select -->
											<th><spring:message code="select"/></th>
											<!--External parameter set  -->
											<th><spring:message code="external_parameter_set"/></th>
										</tr>
							
									</table>
								</td>
								<td></td>
								<td valign="top">
									<table class="tablestyle">
										
										<tr>
											<!--External parameter-->
											<th><spring:message code="external_parameter"/></th>
										</tr>
					
									</table>
								</td>
							</tr>							
							<!-- Create Data Entries -->
							<tr height="20" width="125">
								<td>
									<table style="width:125,5px" border="1">				
										<col style="width:68px">
										<col style="width:176px">														
										
										<!-- ForEachElement: Select element // External parameterset -->										
										<!-- Example:<tr><td>Select</td><td>Dataentry</td></tr> -->		
															
										<c:forEach items="${extParamValSets}" var="extParamValSet">
											<c:choose>
												<c:when test="${selectedextparamsetid == extParamValSet.extparamvalsetid}">
													<tr style="background-color: #D4D4D4"><td>
													<spring:message code="selected"/></td>
												</c:when>
												<c:otherwise>
													<tr>
														<td><a href="<c:url value='extparamsets.html?selectedextparamsetid=${extParamValSet.extparamvalsetid}'/>">
														<spring:message code="select"/></a></td>
												</c:otherwise>
											</c:choose>	
											<td>${extParamValSet.name}</td>
									   	</tr>
									   	</c:forEach>
										
																						
									</table>
									<td></td>
									<td>
										<table style="width:125,5px" border="1">
										<!-- For each element get the External parameter Parameter -->
																																								
											<tr><td>External parameter 1</td></tr>
											<tr><td>External parameter 2</td></tr>			
											</tr>
										</table>
									</td>				
								<tr>	
									
								<!-- Close button -->
								<td></td>
								<td></td>							
								<td align="right"><a href="editoptimizationset.html"><button type="button">
								<spring:message code="close"/></button></a></td>
							</tr>
						</table>
						</td>
					</td>
				</tr>
				<tr height=20></tr>
			</table>
		</td>
	</tr>
</table>
</body>
</html>