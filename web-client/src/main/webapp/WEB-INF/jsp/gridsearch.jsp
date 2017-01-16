<%--@elvariable id="scengenerator" type="eu.cityopt.DTO.ScenarioGeneratorDTO"--%>
<%--@elvariable id="mp" type="eu.cityopt.DTO.ModelParamDTO"--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<c:set var="req" value="${pageContext.request}"/>
<c:set var="baseURL" value="${req.scheme}://${req.serverName}:${req.serverPort}${req.contextPath}"/>
<script language="javascript"><%@ include file="cityopt.js"%></script>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Cityopt <spring:message code="grid_search"/></title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<script>
function openInfoWindow() {
	window.open("gridsearch_info.html",'<spring:message code="grid_search"/> info','width=510,height=600,scrollbars=yes');
}
</script>

<body onLoad="showComponent('ModelParameter', ${usersession.componentId})">
<table cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>
		<td valign="top">
			<div style="overflow: auto; height: 100%; width: 1200px; overflow: auto;">
			<table class="maintablewide">			
				<%@ include file="toprow.inc"%>
				<tr class="titlerow">
					<td class="spacecolumn"></td>
					<td>
						<table width="100%">
							<tr>
								<td><font class="activeproject">${project.name}</font>&nbsp;&nbsp;<spring:message code="grid_search_small"/></td>
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
						<form:form name="mainform" method="post" action="geneticalgorithm.html" modelAttribute="scengenerator">
						<form:input type="hidden" path="version"/>
						<form:input type="hidden" path="scengenid"/>
			            <input type="hidden" name="objid" id="objid" value="0">
			            <input type="hidden" name="constrid" id="constrid" value="0">
			            <input type="hidden" name="decisionvarid" id="decisionvarid" value="0">
						<table style="width: 900px">
							<col style="width: 450px;">
							<col style="width: 450px;">
							<tr>
								<td colspan="2">
									<table>
										<col style="width: 400px;">
										<col style="width: 550px;">
										<tr>
											<td>
												<table>
													<tr>
														<td class="infosmall"><spring:message code="name"/></td>
													</tr>
													<tr>
														<td><form:input type="text" path="name" style="width:200px"/></td>
													</tr>
						                            <tr class="spacerow"></tr>
													<tr>
						                            	<td class="infosmall">
						                            		<spring:message code="type"/>
						                            	</td>
													</tr>
													<tr>
						                            	<td>
						                                    <select name="algorithmid">
						                                        <c:forEach items="${algorithms}" var="algo">
						                                            <c:choose>
						                                                <c:when test="${algo.algorithmid == scengenerator.algorithm.algorithmid}">
						                                                   <option value="${algo.algorithmid}" selected>${algo.description}</option>
						                                                </c:when>
						                                                <c:otherwise>
						                                                   <option value="${algo.algorithmid}">${algo.description}</option>
						                                                </c:otherwise>
						                                            </c:choose>
						                                        </c:forEach>
						                                    </select>
						                                </td>
						                            </tr>
													<tr class="spacerow"></tr>
						                            <tr>
														<td class="infosmall"><spring:message code="grid_search_progress"/></td>
													</tr>
													<tr>
														<td class="activeline">${runinfo}</td>
													</tr>
													<tr class="spacerow"></tr>
													<form:form method="POST" action="importoptimizationproblem.html" enctype="multipart/form-data">
						        					<tr>
						        						<!-- Import optimization set file CSV -->
						        						<td class="infosmall"><spring:message code="import_optimization_problem"/></td>
														<td><input id="file" name="file" type="file"/></td>
														<td align="right"></td>
													</tr>
													<tr>	
						       							<td></td>
						        						<td>
						        							<input type="submit" value="Import file">
						       							</td>
						   							</tr>	
						    						</form:form>
												</table>
											</td>
											<td valign="top" align="right">
												<table>
													<tr>
													    <td align="right"><input type="submit" value="<spring:message code="save" />" style="width: 150px"></td>
												    </tr>
												    <tr>
						                                <td align="right">
						                                	<a href="exportoptimizationproblem.html"><button type="button" style="width: 150px"><spring:message code="export_optimization_problem"/></button></a>
														</td>
													</tr>	
													<tr>
														<td align="right">
															<input type="submit" name="run" value="<spring:message code="run_algorithm" />" style="width: 150px">
														</td>
													</tr>															
												</table>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>						
								<td valign="top">
									<table style="width: 100%">
										<col style="width: 180px;">
										<col style="width: 270px;">
										
										<tr>
											<td><b><spring:message code="model_input_parameters"/></b></td>
										</tr>
										<tr height="10"></tr>
										<tr><td colspan="2"><spring:message code="component"/>:
									      <select name="selectedcompid" onChange="selectComponent('ModelParameter',this.value)">
										   <c:forEach items="${inputcomponents}" var="component">
										     <c:choose>
										         <c:when test="${component.componentid == usersession.componentId}">
										             <option value="${component.componentid}" selected>${component.name}</option>
									             </c:when>
										         <c:otherwise>
										             <option value="${component.componentid}">${component.name}</option>
										         </c:otherwise>
									         </c:choose>
										   </c:forEach>
									      </select>
									      </td>
									    </tr>
										<tr>
											<td colspan="2">
												<table class="tablestyle" style="width: 450px">
													<tr>
														<th><spring:message code="parameter"/></th>
														<th>Value(s)</th>
			                                            <th><spring:message code="unit"/></th>
														<th>Group</th>
													</tr>
			                                        <c:forEach items="${modelparams}" var="mp">
			                                        <tr class="Component${mp.inputparameter.componentComponentid} ModelParameter">
			                                            <td>${mp.inputparameter.name}</td>
			                                            <c:choose>
			                                                <c:when test="${not empty mp.value}">
			                                                  <td align="right">${mp.value}</td>
			                                                </c:when>
			                                               <c:when test="${not empty paramgrouping.decisionValued[mp.inputparameter.inputid]}">
			                                                  <td align="right"><i>decision variable</i></td>
			                                               </c:when>
			                                               <c:when test="${not empty paramgrouping.expressionValued[mp.inputparameter.inputid]}">
			                                                  <td>${mp.expression}</td>
			                                               </c:when>
			                                               <c:when test="${not empty paramgrouping.multiValued[mp.inputparameter.inputid]}">
			                                                  <td align="right">${paramgrouping.multiValued[mp.inputparameter.inputid].valueString}</td>
			                                               </c:when>
			                                               <c:otherwise>
			                                                  <td><div class="error">INTERNAL ERROR</div></td>
			                                               </c:otherwise>
			                                            </c:choose>
			                                            <td>${empty mp.inputparameter.unit ? '' : mp.inputparameter.unit.name}</td>
			                                            <td>${empty paramgrouping.multiValued[mp.inputparameter.inputid] ? ''
			                                                      : paramgrouping.multiValued[mp.inputparameter.inputid].group.name}</td>
			                                        </tr>
			                                        </c:forEach>
												</table>
											</td>
										</tr>
										<tr>
			                                <td colspan="2"><a href="editsgmodelparams.html">
			                                <button type="button" style="width:100px"><spring:message code="edit"/></button></a></td>
			                            </tr>
										<tr height="10"></tr>
										<tr>
											<!--Algorithm parameters-->
											<td colspan="2"><b><spring:message code="algorithm_parameters"/></b> (${scengenerator.algorithm.description})</td>
										</tr>
										<tr>
											<td colspan="2">
												<table class="tablestyle" style="width: 450px">
												   <col>
												   <col align="right">
												   <col align="right">
													<tr>
														<th><spring:message code="parameter"/></th>
														<th><spring:message code="value"/></th>
														<th><spring:message code="default_value"/></th>
													</tr>
			
			                                        <c:forEach items="${algoparamvals}" var="algoparamval">
			                                        <tr>
			                                            <td>${algoparamval.algoparam.name}</td>
			                                            <td align="right">
				                                            <c:choose>
					                                           <c:when test="${algoparamval.value ne algoparamval.algoparam.defaultvalue}">
					                                              ${algoparamval.value}
					                                           </c:when>
				                                            </c:choose>
			                                            </td>
			                                            <td align="right">${algoparamval.algoparam.defaultvalue}</td>
			                                        </tr>
			                                        </c:forEach>
												</table>
											</td>
											<td></td>
										</tr>
										<tr>
										 <td colspan="2">
										     <a href="editsgalgoparamval.html">
										     <button type="button" style="width: 100px"><spring:message code="edit"/></button></a>
										 </td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</form:form>
				</td>
			</tr>
			</table>
			</div>
		</td>
	</tr>
</table>
</body>
</html>