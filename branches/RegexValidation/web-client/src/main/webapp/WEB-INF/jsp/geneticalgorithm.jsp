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
	var ret = confirm('<spring:message code="confirm_delete"/>')
	if (ret) {
		document.mainform.objid.value = objid
    	document.mainform.action = "${baseURL}/deletesgobjfunction.html"
	}
}
function deleteConstraint(constrid) {
	var ret = confirm('<spring:message code="confirm_delete_constraint"/>')
	if (ret == true) {
	    document.mainform.constrid.value = constrid
    	document.mainform.action = "${baseURL}/deletesgconstraint.html"
	}
}
function deleteDecisionVariable(decisionvarid) {
	var ret = confirm('<spring:message code="confirm_delete"/>')
	if (ret == true) {
		document.mainform.decisionvarid.value = decisionvarid
    	document.mainform.action = "${baseURL}/deletesgdecisionvariable.html"
	}
}

function openInfoWindow() {
	window.open("ga_info.html",'Genetic optimization info','width=600,height=600,scrollbars=yes');
}

</script>
<script language="javascript"><%@ include file="cityopt.js"%></script>
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
			<table style="width: 1070px">
				<col style="width: 620px;">
				<col style="width: 450px;">
				<!--Edit genetic algorithm optimization set-->
				<tr>
					<td colspan="2"><h2><spring:message code="edit_genetic_algorithm_optimization_set"/></h2></td>
					<td align="right">
						<div class="round-button">
							<div class="round-button-circle">
								<a href="" onclick="openInfoWindow()">?</a>		
							</div>
						</div>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<table>
							<col style="width: 150px;">
							<col style="width: 250px;">
							<col style="width: 650px;">
	
							<tr>
								<!-- Name -->
								<td><spring:message code="name"/>:</td>
								<td><form:input type="text" path="name" style="width:200px"/></td>
                                <td align="right"><input type="submit" value="<spring:message code="save"/>" style="width: 150px"></td>
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
                                <td align="right">
                                	<a href="exportoptimizationproblem.html"><button type="button" style="width: 150px"><spring:message code="export_optimization_problem"/></button></a>
								</td>
                            </tr>
                            <tr>
								<td><spring:message code="optimization_info"/>:</td>
								<td><font color="red">${error}</font>${runinfo}</td>
								<td align="right">
									<input type="submit" name="run" value="<spring:message code="run_algorithm"/>" style="width: 150px">
								</td>
							</tr>
							<form:form method="POST" action="importoptimizationproblem.html" enctype="multipart/form-data">
        					<tr>
        						<!-- Import optimization set file CSV -->
        						<td><spring:message code="import_optimization_problem"/></td>
								<td><input id="file" name="file" type="file"/></td>
								<td align="right"><a href="gachart.html?resetselections=true"><button type="button" style="width: 150px"><spring:message code="show_results"/></button></a></td>
							</tr>
							<tr>	
       							<td></td>
        						<td>
        							<input type="submit" value="<spring:message code="import_file"/>">
       							</td>
   							</tr>	
    						</form:form>
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
									<table class="tablestyle" style="width: 620px">
										<col style="width: 100px;">
										<col style="width: 50px;">
										<col style="width: 250px;">
										<col style="width: 80px;">
										<col style="width: 80px;">
										<tr>
                                            <th><spring:message code="name"/></th>
											<!--Optimization Sense -->
											<th><spring:message code="optimization_sense"/></th>
											<!-- Expression -->
											<th><spring:message code="expression"/></th>
											<th><spring:message code="edit"/></th>
                                            <th><spring:message code="remove"/></th>
										</tr>
										
										<c:forEach items="${objFuncs}" var="function">
										<tr>
                                            <td>${function.name}</td>
                                            <td>
                                                 <c:choose>
                                                     <c:when test="${function.ismaximise}"><spring:message code="maximize"/></c:when>
                                                     <c:otherwise><spring:message code="minimize"/></c:otherwise>
                                                 </c:choose>
                                            </td>
											<td>${function.expression}</td>
                                            <td><a href="editsgobjfunction.html?obtfunctionid=${function.obtfunctionid}">
                                            <button type="button"><spring:message code="edit"/></button></a></td>
                                            <td><input type="submit" value="<spring:message code="remove"/>" onClick="deleteObjective(${function.obtfunctionid})"></td>
                                        </tr>	
										</c:forEach>
									</table>
								</td>
							</tr>
							<tr>
								<td>
									<!-- Optimization sense and expression Create,Delete and Import functions-->
									<a href="createobjfunction.html?reset=true&type=ga">
										<button type="button" style="width: 100px">
											<spring:message code="create"/>
										</button>
									</a>
									<a href="addsgobjfunction.html"><button type="button" style="width: 100px"><spring:message code="import"/></button></a>
								</td>
							</tr>
							<tr height="10"></tr>
							<tr>
								<!-- Decision variables -->
								<td><b><spring:message code="decision_variables"/></b></td>
							</tr>
							<tr>
								<td>
									<table class="tablestyle" style="width: 620px">
										<col style="width: 110px;">
										<col style="width: 150px;">
										<col style="width: 100px;">
										<col style="width: 100px;">
										<col style="width: 80px;">
										<col style="width: 80px;">
										<tr>
                                            <th><spring:message code="name"/></th>
											<th><spring:message code="type"/></th>
											<th><spring:message code="lower_bound"/></th>
											<th><spring:message code="upper_bound"/></th>
                                            <th><spring:message code="edit"/></th>
                                            <th><spring:message code="remove"/></th>
										</tr>
                                        <c:forEach items="${decVars}" var="decvar">
                                        <tr>
                                            <td>${decvar.name}</td>
                                            <td>${decvar.type.name}</td>
                                            <td align="right">${decvar.lowerbound}</td>
                                            <td align="right">${decvar.upperbound}</td>
                                            <td><a href="editsgdecisionvariable.html?decisionvarid=${decvar.decisionvarid}">
                                            <button type="button"><spring:message code="edit"/></button></a></td>
                                            <td><input type="submit" value="<spring:message code="remove"/>" onClick="deleteDecisionVariable(${decvar.decisionvarid})"></td>
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
									<table class="tablestyle" style="width: 620px">
										<col style="width: 150px;">
										<col style="width: 160px;">
										<col style="width: 75px;">
										<col style="width: 75px;">
										<col style="width: 80px;">
										<col style="width: 80px;">
										<tr>
											<!-- Name -->
											<th><spring:message code="name"/></th>
											<!-- Expression -->
											<th><spring:message code="expression"/></th>
											<th><spring:message code="lower_bound"/></th>
											<th><spring:message code="upper_bound"/></th>
											<th><spring:message code="edit"/></th>
                                            <th><spring:message code="remove"/></th>
										</tr>
										
										<c:forEach items="${constraints}" var="constraint">
										<tr>
											<td>${constraint.name}</td>
											<td>${constraint.expression}</td>
											<td align="right">${constraint.lowerbound}</td>
                                            <td align="right">${constraint.upperbound}</td>
                                            <td><a href="editsgconstraint.html?constrid=${constraint.optconstid}">
                                            <button type="button"><spring:message code="edit"/></button></a></td>
                                            <td><input type="submit" value="<spring:message code="remove"/>" onClick="deleteConstraint(${constraint.optconstid})"></td>
									   	</tr>
										</c:forEach>
									</table>
								</td>
							</tr>
							<tr>
								<td>
                                    <a href="editsgconstraint.html"><button type="button" style="width: 100px"><spring:message code="create"/></button></a>
                                    <a href="importgaconstraint.html"><button type="button" style="width: 100px"><spring:message code="import"/></button></a>
								</td>
							</tr>
							<tr height="10"></tr>
							<tr>
								<td><b><spring:message code="external_parameter_set"/></b></td>
							</tr>
							<tr>
								<td>
									${scengenerator.extparamvalset == null ? "(unset)" : scengenerator.extparamvalset.name}
								</td>
							</tr>
	
							<tr>
								<td>
                                 <a href="gaextparamsets.html?extparamvalsetid=${extparamvalsetid}&context=geneticalgorithm.html">
                                 <button type="button" style="width: 100px"><spring:message code="select"/></button></a>
								</td>
							</tr>
						</table>
					</td>
					<td valign="top">
						<table style="width: 100%">
							<col style="width: 180px;">
							<col style="width: 270px;">
							
							<tr>
								<td><b><spring:message code="model_input_parameters"/></b></td>
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
									<table class="tablestyle" style="width: 450px">
										<tr>
											<th><spring:message code="parameter"/></th>
											<th><spring:message code="value"/></th>
                                            <th><spring:message code="unit"/></th>
											<th><spring:message code="group"/></th>
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
</body>
</html>