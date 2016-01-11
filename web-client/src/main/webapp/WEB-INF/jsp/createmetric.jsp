<%--@elvariable id="metric" type="eu.cityopt.DTO.MetricDTO"--%>
<%--@elvariable id="usersession" type="eu.cityopt.web.UserSession"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt create metric</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />

</head>

<body>
<table cellspacing="0px" cellpadding="0px">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>
		<td width=30></td>
		<td valign="top">
			<div style="overflow:scroll;height:100%;width:1000px;overflow:auto">
			<table>
				<tr>
					<td>
						<!-- Create metric -->
						<h2><spring:message code="create_metric"/> step 1</h2>
					</td>
				</tr>
				<tr>
					<td>
						<p>Create an expression for the metric by adding parameters, operators and numbers to it.</p>
					</td>
				</tr>
				<tr>
					<td>
						<table class="tablegroup">
							<tr>
								<td>
									<table>
										<col style="width:250px">
										<col style="width:20px">
										<col style="width:250px">
										<col style="width:20px">
										<col style="width:250px">
			
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
															<c:set var="tooltip_selector"><spring:message code="tooltip_select"/></c:set>
															<td><a href="<c:url value='createmetric.html?selectedcompid=${component.componentid}'/>" title="${tooltip_selector}">
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
													<col style="width:150px">
													<col style="width:100px">
													<col style="width:100px">
													<col style="width:100px">
													<tr>
														<!-- Input parameter, Id, Default value -->
														<th><spring:message code="input_parameter"/></th>
														<th><spring:message code="default_value"/></th>
														<th><spring:message code="unit"/></th>
														<th>Add</th>
													</tr>
													
													<c:forEach items="${inputParameters}" var="inputParam">
													<tr>
														<td>${inputParam.name}</td>
												    	<td>${inputParam.defaultvalue}</td>
												    	<td>${inputParam.unit.name}</td>
												   		<td><a href="createmetric.html?inputparamid=${inputParam.inputid}"><button type="button">Add</button></a></td>
												    </tr>
													</c:forEach>
												</table>
											</td>
											<td></td>
											<td valign="top">
												<table class="tablestyle">
													<col style="width:200px">
													<col style="width:50px">
													<tr>
														<!--Output variable-->
														<th><spring:message code="output_variable"/></th>
														<th>Add</th>
													</tr>
								
													<c:forEach items="${outputVars}" var="outputVar">
													<tr>
														<td>${outputVar.name}</td>
														<td><a href="createmetric.html?outputparamid=${outputVar.outvarid}"><button type="button">Add</button></a></td>
												    </tr>
													</c:forEach>
												</table>
											</td>	
										</tr>
									</table>
								</td>
							</tr>
						</table>
						<table>
							<col style="width:30px">
							<col style="width:250px">
							<col style="width:20px">
							<col style="width:250px">
							<col style="width:20px">
							<col style="width:250px">

							<tr height="20">
							</tr>	
							<tr>
								<td></td>
								<td>
									<!--Metrics-->
									<spring:message code="metrics"/>
								</td>
								<td></td>
									<!-- External parameters -->
								<td><spring:message code="external_parameters"/></td>
								<td></td>
								<td>Numbers and operators</td>
							</tr>
							<tr>
								<td></td>
								<td valign="top">
									<table class="tablestyle">
										<col style="width:200px">
										<col style="width:50px">
										
										<tr height="20">
											<!-- Name -->
										    <th><spring:message code="name"/></th>
										    <th><spring:message code="add"/></th>
										</tr>
									
										<c:forEach items="${metrics}" var="metric">
										<tr>
											<td>${metric.name}</td>
											<td><a href="createmetric.html?metricid=${metric.metid}"><button type="button">Add</button></a></td>
									   	</tr>
										</c:forEach>
									</table>										
								</td>
								<td></td>
								<td valign="top">
									<table class="tablestyle" width="250">
										<col style="width:200px">
										<col style="width:50px">
												
										<tr height="20">
											<!-- Name -->
										    <th><spring:message code="name"/></th>
										    <th>Add</th>
										</tr>
										
										<c:forEach items="${extParams}" var="extParam">
										<tr>
											<td>${extParam.name}</td>
											<td><a href="createmetric.html?extparamid=${extParam.extparamid}"><button type="button">Add</button></a></td>
									    </tr>
										</c:forEach>
									</table>
								</td>
								<td></td>
								<td valign="top">
									<table width=180>
										<tr>
											<td><a href="createmetric.html?text=7"><button class="number-button" type="button">7</button></a></td>
											<td><a href="createmetric.html?text=8"><button class="number-button" type="button">8</button></a></td>
											<td><a href="createmetric.html?text=9"><button class="number-button" type="button">9</button></a></td>
											<td><a href="createmetric.html?text=/"><button class="number-button" type="button">/</button></a></td>
									    </tr>
										<tr>
											<td><a href="createmetric.html?text=4"><button class="number-button" type="button">4</button></a></td>
											<td><a href="createmetric.html?text=5"><button class="number-button" type="button">5</button></a></td>
											<td><a href="createmetric.html?text=6"><button class="number-button" type="button">6</button></a></td>
											<td><a href="createmetric.html?text=*"><button class="number-button" type="button">*</button></a></td>
									    </tr>
										<tr>
											<td><a href="createmetric.html?text=1"><button class="number-button" type="button">1</button></a></td>
											<td><a href="createmetric.html?text=2"><button class="number-button" type="button">2</button></a></td>
											<td><a href="createmetric.html?text=3"><button class="number-button" type="button">3</button></a></td>
											<td><a href="createmetric.html?text=-"><button class="number-button" type="button">-</button></a></td>
									    </tr>
									    <tr>
											<td colspan="2"><a href="createmetric.html?text=0"><button class="number-button-wide" type="button">0</button></a></td>
											<td><a href="createmetric.html?text=."><button class="number-button" type="button">.</button></a></td>
											<td><a href="createmetric.html?text=plus"><button class="number-button" type="button">+</button></a></td>
									    </tr>
									</table>
								</td>
							</tr>
							<tr height="20"></tr>
							<tr>
								<td></td>
								<!-- Expression -->
								<td><spring:message code="expression"/></td>
								<td></td>
								<td></td>
								<td></td>
							</tr>					
							<tr>
								<td></td>
								<c:set var="expressiontip"><spring:message code="tooltip_expression"/></c:set>
								<td colspan="3">
									<table class="tablestyle" width="600">
										<tr>
											<td>${usersession.getExpression()}</td>
										</tr>
									</table>
								</td>
								<td></td>
							</tr>
							<tr>
								<td></td>
								<td colspan="3"> 
								</td>
								<td></td>
							</tr>					
							<tr>
								<td></td>
								<td></td>
								<td></td>
								<td align="right">
									<a href="updatemetric.html"><button type="button" style="width:100px">Next</button></a>
									<a href="metricdefinition.html"><button style="width:100px" type="button" value="Cancel">
									<spring:message code="cancel"/></button></a>
								</td>
								<td></td>
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