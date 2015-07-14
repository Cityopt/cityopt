<%--@elvariable id="scengenerator" type="eu.cityopt.DTO.ScenarioGeneratorDTO"--%>
<%--@elvariable id="constraint" type="eu.cityopt.DTO.OptConstraintDTO"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Cityopt create genetic algorithm optimization set</title>
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
			<form:form method="post" action="geneticalgorithm.html" modelAttribute="scengenerator">
			<table style="width: 950px">
				<col style="width: 400px;">
				<col style="width: 450px;">
				<!--Edit genetic algorithm optimization set-->
				<tr><td colspan="2"><h2><spring:message code="edit_genetic_algorithm_optimization_set"/></h2></td></tr>
				<tr>
					<td colspan="2">
						<table>
							<col style="width: 80px;">
							<col style="width: 200px;">
							<col style="width: 80px;">
							<col style="width: 300px;">
							<col style="width: 175px;">
							<tr>
								<!-- Name -->
								<td><spring:message code="name"/>:</td>
								<td><form:input type="text" path="name" style="width:200px"/></td>
								<!-- Description -->
								<td><spring:message code="description"/>:</td>
								<!-- Run GA Generation submit -->
								<td rowspan="2"><textarea id="description" rows="2" style="width: 300px"></textarea></td>
								<td align="right"><input type="submit" value="<spring:message code="run_ga_generation"/>" style="width: 150px"></td>
							</tr>
							<tr>
								<!-- User: -->						
								<td><spring:message code="user"/>:</td>
								<td><input type="text" id="user" style="width:200px"></td>
								<td></td>
								<td align="right"></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>						
					<td>
						<table>
							<tr>
								<!-- Objective function -->
								<td><b><spring:message code="objective_function"/></b></td>
							</tr>
							<tr>
								<td>
									<table class="tablestyle" style="width: 390px">
										<tr>
											<!--Optimization Sense -->
											<th><spring:message code="optimization_sense"/></th>
											<!-- Expression -->
											<th><spring:message code="expression"/></th>
										</tr>
										
										<c:forEach items="${objFuncs}" var="function">
										<tr>
                                            <td>
                                                 <c:choose>
                                                     <c:when test="${function.ismaximise}">Maximize</c:when>
                                                     <c:otherwise>Minimize</c:otherwise>
                                                 </c:choose>
                                            </td>
											<td>${function.expression}</td>
									   	</tr>
										</c:forEach>
									</table>
								</td>
							</tr>
							<tr>
								<td>
									<!-- Optimization sense and expression Create,Delete and Import functions-->
									<a href="creategaobjfunction.html"><button type="button" style="width: 100px">
									<spring:message code="create"/></button></a>
									<input type="submit" value="<spring:message code="delete"/>"  style="width: 100px">
									<a href="importgaobjfunction.html"><button type="button" style="width: 100px">
									<spring:message code="import"/></button></a>
								</td>
							</tr>
							<tr height="10"></tr>
							<tr>
								<!-- Decision variables -->
								<td><b><spring:message code="decision_variables"/></b></td>
							</tr>
							<tr>
								<td>
									<table class="tablestyle" style="width: 390px">
										<tr>
											<!-- Expression -->
											<th><spring:message code="expression"/></th>
											<!-- Unit -->
											<th><spring:message code="unit"/></th>
											<!-- Type -->
											<th><spring:message code="type"/></th>
										</tr>
										<tr>
											<td>x</td>
											<td>x</td>
											<td>x</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td>
									<!-- Add, delete and Import Decision Variables -->
									<input type="submit" value="<spring:message code="add"/>" style="width: 100px">
									<input type="submit" value="<spring:message code="delete"/>" style="width: 100px">
									<input type="submit" value="<spring:message code="import"/>" style="width: 100px">
								</td>
							</tr>
							<tr height="10"></tr>
							<tr>
								<!-- Constraints -->
								<td><b><spring:message code="constraints"/></b></td>
							</tr>
							<tr>
								<td>
									<table class="tablestyle" style="width: 390px">
										<tr>
											<!-- Name -->
											<th><spring:message code="name"/></th>
											<!-- Expression -->
											<th><spring:message code="expression"/></th>
										</tr>
										
										<c:forEach items="${constraints}" var="constraint">
										<tr>
											<td>${constraint.name}</td>
											<td>${constraint.expression}</td>
									   	</tr>
										</c:forEach>
									</table>
								</td>
							</tr>
							<tr>
								<td>
									<!-- Add, Delete and Import Constraints -->
									<input type="submit" value="<spring:message code="add"/>" style="width: 100px">
									<input type="submit" value="<spring:message code="delete"/>" style="width: 100px">
									<input type="submit" value="<spring:message code="import"/>" style="width: 100px">
								</td>
							</tr>
							<tr height="10"></tr>
							<tr>
								<!--External parameter value set-->
								<td><b><spring:message code="external_parameter_value_set"/></b></td>
							</tr>
							<tr>
								<td>
									<table class="tablestyle" style="width: 390px">
										<tr>
											<!-- Expression -->
											<th><spring:message code="expression"/></th>
											<!-- Type -->
											<th><spring:message code="type"/></th>
											<!-- Unit -->
											<th><spring:message code="unit"/></th>
										</tr>
										<tr>
											<td>x</td>
											<td>x</td>
											<td>x</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td>
									<input type="submit" value="<spring:message code="add"/>" style="width: 100px">
									<input type="submit" value="<spring:message code="delete"/>" style="width: 100px">
								</td>
							</tr>
						</table>
					</td>
					<td valign="top">
						<table style="width: 450px">
							<col style="width: 180px;">
							<col style="width: 270px;">
							<tr>
								<!--Set the model parameters-->
								<td><spring:message code="set_model_parameters"/></td>
								<!-- Set -button -->
								<td><a href=""><button type="button" style="width:100px"><spring:message code="set"/></button></a></td>
							</tr>
							<tr height="10"></tr>
							<tr>
								<td>
									<table class="tablestyle" style="width: 180px; height: 150px">
										<tr>
											<!-- Component -->
											<th><spring:message code="component"/></th>
										</tr>
										<tr>
											<td>x</td>
										</tr>
										<tr>
											<td>x</td>
										</tr>
										<tr>
											<td>x</td>
										</tr>
										<tr>
											<td>x</td>
										</tr>
									</table>
								<!-- Component -->	
								</td><spring:message code="component"/><td>
									<table class="tablestyle" style="width: 270px; height: 150px">
										<tr>
											<!-- Parameter, value, unit -->
											<th><spring:message code="parameter"/></th>
											<th><spring:message code="value"/></th>
											<th><spring:message code="unit"/></th>
										</tr>
										<tr>
											<td>x</td>
											<td>x</td>
											<td>x</td>
										</tr>
										<tr>
											<td>x</td>
											<td>x</td>
											<td>x</td>
										</tr>
										<tr>
											<td>x</td>
											<td>x</td>
											<td>x</td>
										</tr>
										<tr>
											<td>x</td>
											<td>x</td>
											<td>x</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr height="10"></tr>
							<tr>
								<!--Algorithm parameters-->
								<td colspan="2"><b><spring:message code="algorithm_parameters"/></b></td>
							</tr>
							<tr>
								<td colspan="2">
									<table class="tablestyle" style="width: 450px">
										<tr>
											<!-- Parameter,Value,Default value and Type -->
											<th><spring:message code="parameter"/></th>
											<th><spring:message code="value"/></th>
											<th><spring:message code="default_value"/></th>
											<th><spring:message code="type"/></th>
										</tr>
										<tr>
											<td>x</td>
											<td>x</td>
											<td>x</td>
											<td>x</td>
										</tr>
									</table>
								</td>
								<td></td>
							</tr>
							<tr height="2"></tr>
						</table>
						<table>
							<col style="width: 225px;">
							<col style="width: 225px;">
							<tr>
								<td></td>
								<td align="right">
									<!-- Create & Select existing one -->
									<a href=""><button type="button"  style="width: 50px"><spring:message code="create"/></button></a>
									<a href=""><button type="button"  style="width: 150px"><spring:message code="select_existing_one"/></button></a>
								</td>
							</tr>
							<tr height="10"></tr>
							<tr>
								<!-- Visualize parento diagram -->
								<td><input type="submit" value="<spring:message code="visualize_parento_diagram"/>"></td>
								<td align="right"><input type="submit" value="<spring:message code="create_ga_optimization_set"/>" style="width: 200px"></td>
							</tr>
							<tr>
								<!-- Create,Cancel and clone Genetic Algorithm set -->
								<td><input type="submit" value="<spring:message code="search_optimal_solution"/>"></td>
								<td align="right"><input type="submit" value="<spring:message code="cancel_ga_optimization_set"/>" style="width: 200px"></td>
							</tr>
							<tr>
								<td></td>
								<td align="right"><input type="submit" value="<spring:message code="clone_ga_optimization_set"/>" style="width: 200px"></td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</form:form>
		</td>
	</tr>
</table>
</body>
</html>