<%--@elvariable id="metric" type="eu.cityopt.DTO.MetricDTO"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt edit metric</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
	<table cellspacing="0px" cellpadding="0px">
		<tr>
			<td><%@ include file="mainmenu.inc"%></td>
			<td width=30></td>
			<td valign="top">
				<div
					style="overflow: scroll; height: 800px; width: 800px; overflow: auto">
					<form:form method="post"
						action="editmetric.html?metricid=${metric.metid}"
						modelAttribute="metric">
						<table>
							<tr>
								<td>
									<!--Edit metric  -->
									<h2>
										<spring:message code="edit_metric" />
									</h2>
								</td>
							</tr>
							<tr>
								<td>
									<table>
										<col style="width: 30px">
										<col style="width: 250px">
										<col style="width: 20px">
										<col style="width: 250px">
										<col style="width: 20px">
										<col style="width: 250px">
										<tr>
											<td></td>
											<!-- Components -->
											<td><spring:message code="components" /></td>
											<td></td>
											<!-- Input variables -->
											<td><spring:message code="input_variables" /></td>
											<td></td>
											<!--Output variables  -->
											<td><spring:message code="output_variables" /></td>
											<td></td>
										</tr>
										<td></td>
										<td valign="top">
											<table class="tablestyle">
												<col style="width: 60px">
												<col style="width: 190px">
												<tr>
													<!-- Select -->
													<th><spring:message code="select" /></th>
													<!-- Component -->
													<th><spring:message code="component" /></th>

													<c:forEach items="${components}" var="component">
														<tr>
															<c:if test="${selectedcompid == component.componentid}">
																<tr style="background-color: #D4D4D4">
																	<td>Selected</td>
															</c:if>
															<c:if test="${selectedcompid != component.componentid}">
																<tr>
																	<td><a
																		href="<c:url value='editmetric.html?selectedcompid=${component.componentid}&metricid=${metric.metid}'/>">Select</a></td>
															</c:if>
															<td>${component.name}</td>
														</tr>
													</c:forEach>
												</tr>
											</table>
										</td>
										<td></td>
										<td valign="top">
											<table class="tablestyle">
												<col style="width: 150px">
												<col style="width: 60px">
												<col style="width: 150px">
												<tr>
													<!-- Input parameter, Id, Default value -->
													<th><spring:message code="input_parameter" /></th>
													<th><spring:message code="id" /></th>
													<th><spring:message code="default_value" /></th>
												</tr>
												<c:forEach items="${inputParameters}" var="inputParam">
													<tr>
														<td>${inputParam.name}</td>
														<td>${inputParam.inputid}</td>
														<td>${inputParam.defaultvalue}</td>
													</tr>
												</c:forEach>
											</table>
										</td>
										<td></td>
										<td valign="top">
											<table class="tablestyle">
												<col style="width: 250px">
												<tr>
													<!--Output variable-->
													<th><spring:message code="output_variable" /></th>
												</tr>
												<c:forEach items="${outputVars}" var="outputVar">
													<tr>
														<td>${outputVar.name}</td>
													</tr>
												</c:forEach>
											</table>
										<tr>
											<td></td>
											<!--Metrics-->
											<td><spring:message code="metrics" /></td>
											<td></td>
											<!-- External parameters -->
											<td><spring:message code="external_parameters" /></td>
											<td></td>
										</tr>
										<td></td>
										<td>
											<table class="tablestyle">
												<col style="width: 250px">
												<tr height="20">
													<!-- Name -->
													<th><spring:message code="name" /></th>
												</tr>
												<c:forEach items="${metrics}" var="metric">
													<tr>
														<td>${metric.name}</td>
													</tr>
												</c:forEach>
											</table>
										</td>
										<td></td>
										<td valign="top">
											<table class="tablestyle" width="250">
												<col style="width: 110px">

												<tr height="20">
													<!-- Name -->
													<th><spring:message code="name" /></th>
												</tr>

												<c:forEach items="${extParamVals}" var="extParamVal">
													<tr>
														<td>${extParamVal.extparam.name}</td>
													</tr>
												</c:forEach>
											</table>
										</td>								
										
										<table align="center">
											<col style="width: 150px">
											<col style="width: 80px">
											<col style="width: 80px">
											<tr>
												<td>
													<!--Name--> <spring:message code="name" />
												</td>
												<td><form:input style="width:300px" type="text"
														path="name" /></td>
														
											</tr>
											<tr>
												<td>
													<!--Expression--> <spring:message code="expression_name" />
												</td>
												<td><form:input style="width:300px" type="text"
														path="expression" /></td>
											</tr>
											<tr>
												<td>
													<!--Expression--> <spring:message code="expression" />
												</td>
												<td><form:input style="width:300px" type="text"
														path="expression" /></td>
											</tr>
											<tr height="10">
												<td></td>
											</tr>
											<tr>
												<td></td>
												<!-- Update submit & Cancel button -->
												<td align="right"><input style="width: 100px"
													type="submit" value="<spring:message code="update"/>" /> <a
													href="metricdefinition.html"><button
															style="width: 100px" type="button" value="Cancel">
															<spring:message code="cancel" />
														</button></a></td>
											</tr>
										</table>
									</table> </form:form>
									</div>
								</td>
							</tr>
						</table>
</body>
</html>