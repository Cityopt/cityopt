<%--@elvariable id="scengenerator" type="eu.cityopt.DTO.ScenarioGeneratorDTO"--%>
<%--@elvariable id="function" type="eu.cityopt.DTO.ObjectiveFunctionDTO"--%>
<%--@elvariable id="constraint" type="eu.cityopt.DTO.OptConstraintDTO"--%>
<%--@elvariable id="epv" type="eu.cityopt.DTO.ExtParamValDTO"--%>
<%--@elvariable id="decvar" type="eu.cityopt.DTO.DecisionVariableDTO"--%>
<%--@elvariable id="mp" type="eu.cityopt.DTO.ModelParamDTO"--%>
<%--@elvariable id="algoparamval" type="eu.cityopt.DTO.AlgoParamValDTO"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<c:set var="req" value="${pageContext.request}"/>
<c:set var="baseURL" value="${req.scheme}://${req.serverName}:${req.serverPort}${req.contextPath}"/>
<script language="javascript">
function deleteObjective(objid) {
	document.mainform.objid.value = objid
    document.mainform.action = "${baseURL}/deletesgobjfunction.html"
}
function deleteConstraint(constrid) {
    document.mainform.constrid.value = constrid
    document.mainform.action = "${baseURL}/deletesgconstraint.html"
}
function deleteDecisionVariable(decisionvarid) {
    document.mainform.decisionvarid.value = decisionvarid
    document.mainform.action = "${baseURL}/deletesgdecisionvariable.html"
}
</script>
<%@ include file="cityopt.js"%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Cityopt edit genetic algorithm optimization set</title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body onLoad="showComponent('ModelParameter', ${usersession.componentId})">
<table cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>
		<td style="width: 30px"></td>
		<td valign="top">
			<form:form name="mainform" method="post" action="geneticalgorithm.html" modelAttribute="scengenerator">
			<form:input type="hidden" path="version"/>
			<form:input type="hidden" path="scengenid"/>
            <input type="hidden" name="objid" id="objid" value="0">
            <input type="hidden" name="constrid" id="constrid" value="0">
            <input type="hidden" name="decisionvarid" id="decisionvarid" value="0">
			<table style="width: 950px">
				<col style="width: 400px;">
				<col style="width: 450px;">
				<!--Edit genetic algorithm optimization set-->
				<tr><td colspan="2"><h2><spring:message code="edit_genetic_algorithm_optimization_set"/></h2></td></tr>
				<tr>
					<td colspan="2">
						<table>
							<col style="width: 100px;">
							<col style="width: 200px;">
							<col style="width: 580px;">
							<tr>
								<!-- Name -->
								<td><spring:message code="name"/>:</td>
								<td><form:input type="text" path="name" style="width:200px"/></td>
                                <td align="right"><input type="submit" value="Save" style="width: 150px"></td>
							</tr>
                            <tr>
                            	<td>
                            		<spring:message code="type"/>:
                            	</td>
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
                                <td align="right"><input type="submit" name="run" value="Run algorithm" style="width: 150px"></td>
                            </tr>
						</table>
					</td>
				</tr>
				<tr>						
					<td valign="top">
						<table>
							<tr>
								<!-- Objective function -->
								<td><b><spring:message code="objective_function"/></b></td>
							</tr>
							<tr>
								<td>
									<table class="tablestyle" style="width: 100%">
										<tr>
                                            <th>Name</th>
											<!--Optimization Sense -->
											<th><spring:message code="optimization_sense"/></th>
											<!-- Expression -->
											<th><spring:message code="expression"/></th>
											<th></th>
                                            <th></th>
										</tr>
										
										<c:forEach items="${objFuncs}" var="function">
										<tr>
                                            <td>${function.name}</td>
                                            <td>
                                                 <c:choose>
                                                     <c:when test="${function.ismaximise}">Maximize</c:when>
                                                     <c:otherwise>Minimize</c:otherwise>
                                                 </c:choose>
                                            </td>
											<td>${function.expression}</td>
                                            <td><a href="editsgobjfunction.html?objid=${function.obtfunctionid}">
                                            <button type="button"><spring:message code="edit"/></button></a></td>
                                            <td><input type="submit" value="Delete" onClick="deleteObjective(${function.obtfunctionid})"></td>
                                        </tr>
										</c:forEach>
									</table>
								</td>
							</tr>
							<tr>
								<td>
									<!-- Optimization sense and expression Create,Delete and Import functions-->
									<a href="editsgobjfunction.html"><button type="button" style="width: 100px">
									<spring:message code="create"/></button></a>
									<a href="addsgobjfunction.html"><button type="button" style="width: 100px">Add</button></a>
								</td>
							</tr>
							<tr height="10"></tr>
							<tr>
								<!-- Decision variables -->
								<td><b><spring:message code="decision_variables"/></b></td>
							</tr>
							<tr>
								<td>
									<table class="tablestyle" style="width: 100%">
										<tr>
                                            <th>Name</th>
											<th>Type</th>
											<!-- Type -->
											<th><spring:message code="type"/></th>
											<th>Lower bound</th>
											<th>Upper bound</th>
                                            <th></th>
                                            <th></th>
										</tr>
                                        <c:forEach items="${decVars}" var="decvar">
                                        <tr>
                                            <td>${decvar.name}</td>
                                            <td>${decvar.type.name}</td>
                                            <td align="right">${decvar.lowerbound}</td>
                                            <td align="right">${decvar.upperbound}</td>
                                            <td><a href="editsgdecisionvariable.html?decisionvarid=${decvar.decisionvarid}">
                                            <button type="button"><spring:message code="edit"/></button></a></td>
                                            <td><input type="submit" value="Delete" onClick="deleteDecisionVariable(${decvar.decisionvarid})"></td>
                                        </tr>
                                        </c:forEach>
									</table>
								</td>
							</tr>
                            <tr>
                                <td>
                                    <a href="editsgdecisionvariable.html"><button type="button" style="width: 100px">
                                    <spring:message code="create"/></button></a>
                                </td>
                            </tr>
							<tr height="10"></tr>
							<tr>
								<!-- Constraints -->
								<td><b><spring:message code="constraints"/></b></td>
							</tr>
							<tr>
								<td>
									<table class="tablestyle" style="width: 100%">
										<tr>
											<!-- Name -->
											<th><spring:message code="name"/></th>
											<!-- Expression -->
											<th><spring:message code="expression"/></th>
											<th>Lower bound</th>
											<th>Upper bound</th>
											<th></th>
                                            <th></th>
										</tr>
										
										<c:forEach items="${constraints}" var="constraint">
										<tr>
											<td>${constraint.name}</td>
											<td>${constraint.expression}</td>
											<td align="right">${constraint.lowerbound}</td>
                                            <td align="right">${constraint.upperbound}</td>
                                            <td><a href="editsgconstraint.html?constrid=${constraint.optconstid}">
                                            <button type="button"><spring:message code="edit"/></button></a></td>
                                            <td><input type="submit" value="Delete" onClick="deleteConstraint(${constraint.optconstid})"></td>
									   	</tr>
										</c:forEach>
									</table>
								</td>
							</tr>
							<tr>
								<td>
                                    <a href="editsgconstraint.html"><button type="button" style="width: 100px"><spring:message code="create"/></button></a>
                                    <a href="addsgconstraint.html"><button type="button" style="width: 100px"><spring:message code="add"/></button></a>
								</td>
							</tr>
							<tr height="10"></tr>
							<tr>
								<td><b>External parameter values</b></td>
							</tr>
							<tr>
								<td>
				                  <table class="tablestyle" style="width: 100%">
				                      <col>
				                      <col align="right">
				                      <col align="right">
				                      <tr>
				                          <th>Parameter</th>
				                          <th>Value</th>
				                          <th>Comment</th>
				                      </tr>
				    
				                      <c:forEach items="${extparamvals}" var="epv">
				                      <tr>
				                          <td>${epv.extparam.name}</td>
				                          <td align="right">${epv.value}</td>
                                          <td>${epv.comment}</td>
				                      </tr>
				                      </c:forEach>
				                  </table>
								</td>
							</tr>
							<tr>
								<td>
                                 <a href="editextparamvalset.html?extparamvalsetid=${extparamvalsetid}&context=geneticalgorithm.html">
                                 <button type="button" style="width: 100px"><spring:message code="edit"/></button></a>
								</td>
							</tr>
						</table>
					</td>
					<td valign="top">
						<table style="width: 100%">
							<col style="width: 180px;">
							<col style="width: 270px;">
							<tr>
								<td><b>Model input parameters</b></td>
							</tr>
							<tr height="10"></tr>
							<tr><td colspan="2">Component:
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
									<table class="tablestyle" style="width: 100%">
										<tr>
											<th><spring:message code="parameter"/></th>
											<th>Value(s)</th>
											<th>Group</th>
											<th><spring:message code="unit"/></th>
										</tr>
                                        <c:forEach items="${modelparams}" var="mp">
                                        <tr class="Component${mp.inputparameter.componentComponentid} ModelParameter">
                                            <td>${mp.inputparameter.name}</td>
                                            <c:choose>
                                                <c:when test="${not empty mp.value}">
                                                  <td align="right">${mp.value}</td>
                                                </c:when>
                                               <c:when test="${paramgrouping.decisionValued.contains(mp.inputparameter.inputid)}">
                                                  <td align="right"><i>decision variable</i></td>
                                               </c:when>
                                               <c:when test="${empty paramgrouping.multiValued[mp.inputparameter.inputid]}">
                                                  <td>${mp.expression}</td>
                                               </c:when>
                                               <c:otherwise>
                                                  <td align="right">${paramgrouping.multiValued[mp.inputparameter.inputid].values}</td>
                                               </c:otherwise>
                                            </c:choose>
                                            <td>${empty paramgrouping.multiValued[mp.inputparameter.inputid] ? ''
                                                      : paramgrouping.multiValued[mp.inputparameter.inputid].groupName}</td>
                                            <td>${empty mp.inputparameter.unit ? '' : mp.inputparameter.unit.name}</td>
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
									<table class="tablestyle" style="width: 100%">
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
						<table>
							<tr height="10"></tr>
							<tr>
								<td><input type="submit" value="Visualize pareto diagram"></td>
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