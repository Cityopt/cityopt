<%--@elvariable id="epv" type="eu.cityopt.DTO.ExtParamValDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<script language="javascript"><%@ include file="cityopt.js"%></script>
<head>
<title>CityOpt <spring:message code="edit_input_parameters"/></title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<script>
    function openInfoWindow() {
    	   window.open("editsgmodelparams_info.html", '<spring:message code="input_parameters" /> info', 'width=600, height=600');
    }
</script>

<body onLoad="showComponent('ModelParameter',${usersession.componentId})">
<form:form modelAttribute="modelparamform" method="post" action="editsgmodelparams.html">
<table cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>
		<td valign="top">
			<div style="overflow: auto; height: 100%; width: 1200px; overflow: auto;">
			<table class="maintable">			
				<%@ include file="toprow.inc"%>
				<tr class="titlerow">
					<td class="spacecolumn"></td>
					<td>
						<table width="100%">
							<tr>
								<td>
                           			<spring:message code="edit_input_parameters"/>
								</td>
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
					  <table style="width: 660px">
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
			              <tr height="10"></tr>
			              <tr>
			              	<td class="active"><spring:message code="input_parameters"/></td>
			              <tr>
			                <td>
				              <table class="tablestyle" style="width: 660px">
			                     	<col style="width:200px">
									<col style="width:100px">
									<col style="width:100px">
									<col style="width:100px">
									<col style="width:80px">
									<col style="width:80px">
								<tr>
			                         <th><spring:message code="parameter"/></th>
			                         <th>Min</th>
			                         <th>Max</th>
			                         <th><spring:message code="value"/></th>
			                         <th><spring:message code="unit"/></th>
			                         <th><spring:message code="group"/></th>
			                     </tr>
			                     <c:forEach items="${modelparams}" var="mp">
			                     <tr class="Component${mp.inputparameter.componentComponentid} ModelParameter">
			                         <td>${mp.inputparameter.name}</td>
			                         <td>${mp.inputparameter.lowerBound}</td>
			                         <td>${mp.inputparameter.upperBound}</td>
			                         <td>
			                             <form:input align="right" style="width:100px" type="text" path="valueByInputId[${mp.inputparameter.inputid}]"/>
			                         </td>
			                         <td>${empty mp.inputparameter.unit ? '' : mp.inputparameter.unit.name}</td>
			                         <td>
			                            <c:choose>
			                            <c:when test="${empty groups}">
			                                <form:hidden path="groupByInputId[${mp.inputparameter.inputid}]"/>
			                                --
			                            </c:when>
			                            <c:otherwise>
				                         <form:select path="groupByInputId[${mp.inputparameter.inputid}]">
					                         <form:option value="">--</form:option>
					                         <form:options items="${groups}"></form:options>
				                         </form:select>
				                         </c:otherwise>
			                            </c:choose>
			                         </td>
			                     </tr>
			                     </c:forEach>
				              </table>
			                </td>
			              </tr>
			              <tr>
			                  <td align="right">
			                    <input type="submit" value="Ok">
			                    <a href="geneticalgorithm.html"><button type="button" style="width: 100px"><spring:message code="cancel"/></button></a>
			                  </td>
			              </tr>
			              <tr>
			              	<td><b><spring:message code="groups"/></b></td>
			           	  </tr>   
			              <tr>
			              	<td>
			              	  <table class="tablestyle" style="width: 200px">
			              	  	<th><spring:message code="group_name"/></th>
								 <c:forEach items="${groups}" var="group">
			                       <tr>
			                       	 <td>${group}</td>
			                       </tr>
			                     </c:forEach>
			                   </table>
			                </td>
			              </tr>        			
			              <tr>
			                  <td>
			                  	<input type="submit" name="newgroup" value="<spring:message code="new_group"/>">
			                  	<input type="submit" name="cleangroups" value="<spring:message code="delete_empty_groups"/>">
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
</form:form>
</body>
</html>