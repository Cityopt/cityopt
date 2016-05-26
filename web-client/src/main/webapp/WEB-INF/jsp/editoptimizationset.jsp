<%--@elvariable id="optimizationset" type="eu.cityopt.DTO.OptimizationSetDTO"--%>
<%--@elvariable id="constraint" type="eu.cityopt.DTO.OptConstraintDTO"--%>
<%--@elvariable id="resultScenario" type="eu.cityopt.DTO.ScenarioDTO"--%>
<%--@elvariable id="scenarioWithValue" type="eu.cityopt.DTO.ScenarioWithObjFuncValueDTO"--%>
<%--@elvariable id="usersession" type="eu.cityopt.web.UserSession"--%>
<%--@elvariable id="optresults" type="eu.cityopt.service.SearchOptimizationResults"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt <spring:message code="edit_database_optimization_set"/></title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
<script>
    function openInfoWindow() {
    	   window.open("dboptimization_info.html",'<spring:message code="database_optimization_info"/>','width=600,height=600,scrollbars=yes');
    }
</script>
</head>
<body>
<table cellspacing="0" cellpadding="0">
	<tr>	
		<td valign="top"><%@ include file="mainmenu.inc"%></td>
		<td valign="top">
			<div style="overflow: auto; height: 100%; width: 1200px; overflow: auto;">
			<table class="maintablewide">			
				<%@ include file="toprow.inc"%>
				<tr class="titlerow">
					<td class="spacecolumn"></td>
					<td>
						<table width="100%">
							<tr>
								<td><spring:message code="edit_database_optimization_set"/></td>
								<td align="left" width="40">
									<div class="round-button">
										<div class="round-button-circle">
											<a href="" onclick="openInfoWindow()">?</a>
										</div>
									</div>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td class="spacecolumn"></td>
					<td class="error">${error}</td>
				</tr>
				<tr>
					<td class="spacecolumn"></td>
					<td class="info">${info}</td>
				</tr>
				<tr>
					<td class="spacecolumn"></td>
					<td valign="top">
						<table>
							<col style="width: 400px;">
							<col style="width: 450px;">
							
							<!-- Tool tips -->
							<c:set var="tooltip_name">
									<spring:message code="tooltip_edit_optimizationset_name" />
								</c:set>
							<c:set var="tooltip_description">
									<spring:message code="tooltip_edit_optimizationset_description" />
								</c:set>
							<c:set var="tooltip_run">
									<spring:message code="tooltip_run_optimization" />
								</c:set>
							<c:set var="tooltip_user">
									<spring:message code="tooltip_edit_optimizationset_user" />
								</c:set>
							<c:set var="tooltip_targetfunction">
								<spring:message code="tooltip_edit_optimizationset_targetfunction" />
							</c:set>
							<c:set var="tooltip_create_objectivefunction">
								<spring:message code="tooltip_editoptimizationset_create_objectivefunction" />
							</c:set>
							<c:set var="tooltip_edit_objectivefunction">
								<spring:message code="tooltip_editoptimizationset_edit_objectivefunction" />
							</c:set>
							<c:set var="tooltip_delete_objectivefunction">
								<spring:message code="tooltip_editoptimizationset_delete_objectivefunction" />
							</c:set>
							<c:set var="tooltip_import_objectivefunction">
								<spring:message code="tooltip_editoptimizationset_import_objectivefunction" />
							</c:set>
							<c:set var="tooltip_create_constraints">
								<spring:message code="tooltip_create_constraints" />
							</c:set>
							<c:set var="tooltip_edit_constraints">
								<spring:message code="tooltip_edit_constraints" />
							</c:set>
							<c:set var="tooltip_delete_constraints">
								<spring:message code="tooltip_delete_constraints" />
							</c:set>
							<c:set var="tooltip_import_constraints">
								<spring:message code="tooltip_import_constraints" />
							</c:set>
							<c:set var="tooltip_add_externalparameter">
								<spring:message code="tooltip_add_externalparameter" />
							</c:set>
							<c:set var="tooltip_delete_externalparameter">
								<spring:message code="tooltip_delete_externalparameter" />
							</c:set>		
									
							<tr>
								<td colspan="2" valign="top">
									<table>
										<col style="width: 380px;">
										<col style="width: 20px;">
										<col style="width: 270px;">
										<col style="width: 150px;">
										<form:form method="post" action="editoptimizationset.html" modelAttribute="optimizationset">
										<tr>
											<td class="infosmall"><spring:message code="name"/>*</td>
											<td></td>
											<td class="infosmall"><spring:message code="optimization_info"/></td>
											<td align="right">
												<input type="submit" value="<spring:message code="save"/>" style="width: 120px"/>
											</td>
										</tr>
										<tr>
											<td><form:input type="text" title="${tooltip_name}" path="name" style="width:100%"/></td>
											<td></td>
											<td rowspan="4" valign="top">
												<table class="tablestyle" width="270" height="120">
													<tr>
														<td>${usersession.getOptResultString()}</td>
													</tr>
												</table>
											</td>
											<td align="right">
												<a href="exportoptimizationset.html">
													<button type="button" style="width: 120px">
														<spring:message code="export_optimization_set"/>
													</button>
												</a>
											</td>
										</tr>
										<tr>
											<td class="infosmall"><spring:message code="description"/></td>
											<td></td>
											<td align="right">
												<a href="databaseoptimization.html"><button style="width: 120px" title="${tooltip_run}" type="button">
													<spring:message code="run_search"/></button>
												</a>
											</td>
										</tr>
										<tr>
											<td><form:textarea id="description" title="${tooltip_description}" rows="2" style="width: 100%" path="description"></form:textarea></td>
										</tr>
										</form:form>
										<tr height="20">
										</tr>
									</table>
								</td>
							</tr>
							<tr>						
								<td valign="top">
									<table>
										<tr>
											<!-- Objective function -->
											<td class="active"><spring:message code="objective_function"/></td>
										</tr>
										<tr>
											<td>
												<table width="100%" height="25">
													<col style="width: 20%;">
													<col style="width: 80%;">
													<tr>
														<c:choose>
															<c:when test="${optimizationset.objectivefunction.ismaximise}">
																<!-- Maximize -->
																<td valign="top"><spring:message code="maximize"/></td>
															</c:when>
															<c:otherwise>
																<!-- Minimize -->
																<td valign="top"><spring:message code="minimize"/></td>
															</c:otherwise>
														</c:choose>
														<td>
															<table class="tablestyle" title="${tooltip_targetfunction}" width="100%" height="25">
																<tr>
																	<td>
																		${optimizationset.objectivefunction.expression}
																	</td>
																</tr>
															</table>
														</td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td>
												<a href="createobjfunction.html?reset=true&type=db"><button title="${tooltip_create_objectivefunction}" type="button">
												<spring:message code="create"/></button></a>
												<c:if test="${optimizationset.objectivefunction != null}">
													<a href="editobjfunction.html"><button title="${tooltip_edit_objectivefunction}" type="button">
														<spring:message code="edit"/></button>
													</a>
												</c:if>
												<a href="importobjfunction.html"><button title="${tooltip_import_objectivefunction}" type="button">
												<spring:message code="import"/></button></a>
											</td>
										</tr>
										<tr height="20"></tr>
										<tr>
											<td class="active"><spring:message code="constraints"/>
											</td>
										</tr>
										<tr>
											<td>
												<table class="tablestyle" style="width: 390px">
													<col style="width: 20%;">
													<col style="width: 40%;">
													<col style="width: 10%;">
													<col style="width: 10%;">
													<col style="width: 10%;">
													<col style="width: 10%;">
													<tr>
														<!-- Name -->
														<th><spring:message code="name"/></th>
														<!-- Expression -->
														<th><spring:message code="expression"/></th>
														<!-- Lower bound -->
														<th><spring:message code="lower_bound"/></th>
														<!-- Upper bound -->
														<th><spring:message code="upper_bound"/></th>
														<!-- Edit -->
														<th><spring:message code="edit"/></th>
														<!-- Delete -->
														<th><spring:message code="delete"/></th>
													</tr>
													
													<c:forEach items="${constraints}" var="constraint">
													<tr>
														<td>${constraint.name}</td>
														<td>${constraint.expression}</td>
														<td>${constraint.lowerbound}</td>
														<td>${constraint.upperbound}</td>
														<!-- Edit button -->
														<td><a href="editconstraint.html?constraintid=${constraint.optconstid}">
														<button title="${tooltip_edit_constraints}" type="button">
														<spring:message code="edit"/></button></a>
														<!-- Delete button -->
														<td><a href="deleteconstraint.html?constraintid=${constraint.optconstid}">
														<button title="${tooltip_delete_constraints}" type="button"
														onclick="return confirm('<spring:message code="confirm_delete_constraint"/>')">
														<spring:message code="delete"/></button></a>
												   	</tr>
													</c:forEach>						
												</table>
											</td>
										</tr>
										<tr>
											<td>
												<a href="createconstraint.html">
													<button title="${tooltip_create_constraints}" type="button">
														<spring:message code="create"/>
													</button>
												</a>
												<a href="importsearchconstraint.html">
													<button title="${tooltip_import_constraints}" type="button">
														<spring:message code="import"/>
													</button>
												</a>
											</td>
										</tr>
										<tr height="20"></tr>
										<tr>
											<td class="active"><spring:message code="external_parameter_set"/></td>
										</tr>
										<tr>
											<td>
												<table class="tablestyle" width="100%">
													<tr>
														<td>${optimizationset.extparamvalset == null ? "(unset)" : optimizationset.extparamvalset.name}</td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td>
												<a href="extparamsets.html">
													<button  title="${tooltip_add_externalparameter}" type="button">
														<spring:message code="set"/>
													</button>
												</a>
											</td>
										</tr>
									</table>
								</td>
								<td>
									<table style="width: 440px">
										<col style="width: 100%;">
										<tr>
											<td class="active"><spring:message code="results"/></td>
										</tr>
										<tr>
											<td>
												<table>
													<col style="width: 120px">
													<col style="width: 320px">
													<col style="width: 80px">
													<tr>
														<td valign="top" class="regular">
															<spring:message code="best_scenario"/>
														</td>
														<td>
															<table class="tablestyle" width="100%" height="25">
																<tr>
																	<td>
																		${bestScenarioWithValue.name}
																	</td>
																</tr>
															</table>
														</td>
														<td valign="top" align="right">
															<c:choose>
																<c:when test="${bestScenarioWithValue.scenid > 0}">
																	<a href="showresults.html?scenarioid=${bestScenarioWithValue.scenid}">
																		<button type="button"><spring:message code="show"/></button>
																	</a>
																</c:when>
																<c:otherwise>
																	<a href="showresults.html?scenarioid=${bestScenarioWithValue.scenid}">
																		<button type="button" disabled="disabled" ><spring:message code="show"/></button>
																	</a>
																</c:otherwise>
															</c:choose>
														</td>
													</tr>
												</table>
											</td>
										</tr>
										<tr height="10"></tr>
										<tr>
											<td class="active"><spring:message code="scenarios"/></td>
										</tr>
										<tr>
											<td>
												<table class="tablestyle">
													<col style="width: 290px;">
													<col style="width: 100px;">
													<col style="width: 50px;">
													<tr>
														<!-- Scenario -->
														<th><spring:message code="scenario"/></th>
														<!-- Function value -->
														<th><spring:message code="function_value"/></th>
														<!-- Show -->
														<th><spring:message code="show"/></th>
													</tr>
											
													<c:forEach items="${resultScenariosWithValue}" var="scenarioWithValue">
														<tr>
															<td>${scenarioWithValue.name}</td>
															<td><fmt:formatNumber value="${scenarioWithValue.value}" maxFractionDigits="2"/></td>
															<td><a href="showresults.html?scenarioid=${scenarioWithValue.scenid}"><button type="button"><spring:message code="show"/></button></a></td>
													   	</tr>
													</c:forEach>
												</table>
											</td>
										</tr>
										<tr><td><br></td></tr>
										<tr><td class="active"><spring:message code="metrics"/></td></tr>
										<tr>
											<td colspan="3">
												<div style="overflow:scroll;height:250px;width:440px;overflow:auto">
												<table class="tablestyle" width="100%">
													<col style="width: 33%">
													<col style="width: 33%">
													<col style="width: 33%">
						
													<tr height="20">
													<!-- Name -->
													    <th><spring:message code="name"/></th>
													<!-- Value -->
													     <th><spring:message code="value"/></th>
													<!-- Scenario related -->
														 <th><spring:message code="scenario_related"/></th>
													</tr>
																
													<c:forEach items="${metricVals}" var="metricVal">
														<c:choose>
															<c:when test="${metricVal.scenariometrics.scenario.scenid == bestScenarioWithValue.scenid}">
																<tr>
																	<td>${metricVal.metric.name}</td>
																	<td><fmt:formatNumber type="NUMBER" groupingUsed="false" maxFractionDigits="2" value="${metricVal.value}" /></td>
															    	<td>${metricVal.scenariometrics.scenario.name}</td>
															</c:when>
															<c:otherwise>
																<tr>
																	<td class="dark">${metricVal.metric.name}</td>
																	<td class="dark"><fmt:formatNumber type="NUMBER" groupingUsed="false" maxFractionDigits="2" value="${metricVal.value}" /></td>
															    	<td class="dark">${metricVal.scenariometrics.scenario.name}</td>
															</c:otherwise>
														</c:choose>
												    	</tr>						    	
													</c:forEach>
												</table>
												</div>
											</td>
										</tr>
										<tr><td><br></td></tr>
									</table>
									<table>
										<col style="width: 220px;">
										<col style="width: 220px;">
										<tr>							
										</tr>
										<tr>
											<td></td>
											<td align="right"></td>
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