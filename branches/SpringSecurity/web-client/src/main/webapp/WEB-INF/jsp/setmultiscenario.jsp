<%--@elvariable id="scenGen" type="com.cityopt.DTO.ScenarioGeneratorDTO"--%>
<%--@elvariable id="algorithm" type="com.cityopt.DTO.AlgorithmDTO"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt set multi-scenario</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>
		<td style="width: 30px"></td>
		<td valign="top">
			<div style="overflow:scroll;height:800px;width:800px;overflow:auto">
			<table>
				<col style="width:30px">
				<col style="width:750px">	
				<tr>
					<td></td>
					<td height="80">
						<h2><spring:message code="set_multi_scenario"/></h2>
					</td>
				</tr>
				<tr>
					<td></td>
					<td>
						<table>
							<col style="width:500px">
							<tr>
								<td>
									<!-- Multi-scenarios -->
									<b><spring:message code="multi_scenarios"/></b>
								</td>
							</tr>
							<tr>
								<td valign="top">
									<table class="tablestyle">
										<col style="width:100px">
										<col style="width:200px">
										<col style="width:100px">
										<tr>
											<!-- Select -->
											<th><spring:message code="select"/></th>
											<!-- Name -->
											<th><spring:message code="name"/></th>
											<th><spring:message code="delete"/></th>
										</tr>
										
										<c:forEach items="${scenGens}" var="scenGen">
											<c:if test="${scenGen.algorithm.algorithmid == 1}">
												<c:choose>
													<c:when test="${multiscenarioid == scenGen.scengenid}">
														<tr style="background-color: #D4D4D4">
															<td><spring:message code="selected"/></td>
													</c:when>
													<c:otherwise>
														<tr>
															<td>
																<a href="<c:url value='setmultiscenario.html?multiscenarioid=${scenGen.scengenid}'/>">
																	<spring:message code="select"/>
																</a>
															</td>
													</c:otherwise>
												</c:choose>
												<td>${scenGen.name}</td>
												<td><a href="<c:url value='deletemultiscenario.html?multiscenarioid=${scenGen.scengenid}'/>"
													 onclick="return confirm('<spring:message code="confirm_project_deletion"/>')">
														<button type="button" value="Delete">
														<!-- Delete button -->
														<spring:message code="delete"/></button>
													</a>
												</td>
										   		</tr>
									   		</c:if>
										</c:forEach>
									</table>
								</td>
							</tr>
							<tr>
								<td>
									<a href="createmultiscenario.html"><button style="width:100px" type="button" value="Create">
									<spring:message code="create"/></button></a>
								</td>
							</tr>
							<tr height="10"></tr>
							<tr>
								<td>
									<!-- Multi variables -->
									<b><spring:message code="multi_variables"/></b>									
								</td>
							</tr>
							<tr>
								<td valign="top">
									<table class="tablestyle">
										<col style="width:200px">
										<col style="width:200px">
										<tr>
											<!--Select-->
											<th><spring:message code="expression"/></th>
											<th><spring:message code="input_parameters"/></th>
										</tr>
										
										<c:forEach items="${modelparameters}" var="modelParam">
											<tr>
												<td>${modelParam.expression}</td>
												<td>${modelParam.inputparameter.name}</td>
												<!-- href="<c:url value='deletemultivariable.html?multivariableid=${modelParam.modelparamid}'/>"
													 onclick="return confirm('<spring:message code="confirm_project_deletion"/>')">
														<button type="button" value="Delete">
														<!-- Delete button -->
														<!--<spring:message code="delete"/></button>-->
													
												
										   	</tr>
										</c:forEach>
									</table>
								</td>
							</tr>
							<tr>
							    <td>
									<c:choose>
										<c:when test="${multiscenarioid > 0}">
											<a href="createmultivariable.html?multiscenarioid=${multiscenarioid}"><button style="width:100px" type="button" value="Create">
											<spring:message code="create"/></button></a>
										</c:when>
										<c:otherwise>
											<button type="button" style="width:100px"><spring:message code="create"/></button>
										</c:otherwise>
									</c:choose>
									
								</td>
							</tr>
							<tr height="20"></tr>
							<tr>
								<!-- Close -button -->
								<td align="right">
									<a href="editscenario.html"><button type="button">
									<spring:message code="close"/></button></a>
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