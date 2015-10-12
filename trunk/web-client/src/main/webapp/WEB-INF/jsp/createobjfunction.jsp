<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="function" type="eu.cityopt.DTO.ObjectiveFunctionDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt create objective function</title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
<form:form method="post" action="createobjfunction.html" modelAttribute="function">
<table cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>
		<td width="30"></td>
		<td valign="top">
			<table>
				<tr>
					<td>
						<!--Create objective function-->
						<h2><spring:message code="create_objective_function"/></h2>
					</td>
				</tr>
				<tr>
					<td>
						<table>
							<col style="width:30px">
							<col style="width:250px">
							<col style="width:20px">
							<col style="width:250px">
							<col style="width:80px">
							<col style="width:250px">

							<tr>
								<td></td>
								<td>
									<!--Components-->
									<spring:message code="components"/>
									
								</td>
								<td></td>
								<td>
									<!--Input parameters-->
									<spring:message code="input_parameters"/>
									
								</td>
								<td></td>
								<td>
									<!--Output variables-->
									<spring:message code="output_variables"/>
									
								</td>
							</tr>
							<tr>
								<td></td>
								<td>
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
												<tr style="background-color: #D4D4D4"><td>Selected</td>
											</c:if>
											<c:if test="${selectedcompid != component.componentid}">
												<tr>
												<td><a href="<c:url value='createobjfunction.html?selectedcompid=${component.componentid}'/>">
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
										<col style="width:60px">
										<col style="width:150px">
										<tr>
											<th>
											<!--Input parameter-->
											<spring:message code="input_parameters"/></th>
											<th>
											<!-- Id -->
											<spring:message code="id"/></th>											
											<th>
											<!--Default value-->
											<spring:message code="default_value"/></th>
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
							<tr height="20">
							</tr>
							<tr>
								<td></td>
								<td>
								<!-- Metrics -->
								<spring:message code="metrics"/>
								</td>
								<td></td>
								<td>
								<!--External parameters-->
								<spring:message code="external_parameters"/></td>
								<td></td>
							</tr>
							<tr>
								<td></td>
								<td>
									<table class="tablestyle">
										<col style="width:250px">
										
										<tr>
											<th>
											<!--Metric name-->
											<spring:message code="metric_name"/></th>
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
									<table class="tablestyle" width="450">
										<col style="width:200px">
												
										<tr height="20">
											<!-- Name -->
										    <th><spring:message code="name"/></th>
										</tr>
										
										<c:forEach items="${extParamVals}" var="extParamVal">
										<tr>
											<td>${extParamVal.extparam.name}</td>
									    </tr>
										</c:forEach>
									</table>
								</td>
								<td></td>
							</tr>
							<tr height="20">
							</tr>
							<tr>
								<td></td>
								<!-- Name -->
								<td><spring:message code="name"/></td>
								<td></td>
								<td></td>
								<td></td>
							</tr>					
							<tr>
								<td></td>
								<td colspan="3"><form:input style="width:520px" type="text" path="name"/></td>
								<td></td>
							</tr>
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
								<td colspan="3"><form:input style="width:520px" type="text" path="expression"/></td>
								<td></td>
							</tr>
							<tr>
								<td></td>
								<!-- Optimization sense: Minimize / Maximize -->
								<td colspan="3"><spring:message code="optimization_sense"/>: 
									<select name="optsense" id="optsense" size="1">
										<option value="1" selected><spring:message code="minimize"/></option>
										<option value="2"><spring:message code="maximize"/></option>
									</select>
								</td>
								<td></td>
							</tr>					
							<tr>
								<td></td>
								<td></td>
								<td></td>
								<!-- Ok and cansel - submit buttons -->
								<td align="right"><input type="submit" value="<spring:message code="ok"/>"></input>
								<a href="editoptimizationset.html"><button type="button"><spring:message code="cancel"/></button></a></td>
								<td></td>
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