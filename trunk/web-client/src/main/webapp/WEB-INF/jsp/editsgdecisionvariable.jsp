<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="component" type="eu.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="inputParam" type="eu.cityopt.DTO.InputParameterDTO"--%>
<%--@elvariable id="typechoice" type="eu.cityopt.DTO.TypeDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<c:choose>
  <c:when test="${decisionvarid > 0}">
    <title>CityOpt <spring:message code="edit_decision_variable"/></title>
  </c:when>
  <c:otherwise>
    <title>CityOpt <spring:message code="create_decision_variable"/></title>
  </c:otherwise>
</c:choose>  
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
<form:form method="post" action="editsgdecisionvariable.html" modelAttribute="decVar">
<form:input type="hidden" path="decisionvarid"/>
<form:input type="hidden" path="version"/>
<table cellspacing="0" cellpadding="0">
	<tr>
		<td>
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
									<c:choose>
	                        			<c:when test="${decisionvarid > 0}">
		                            		<spring:message code="edit_decision_variable"/>
			                        	</c:when>
		        	                  	<c:otherwise>
	                            			<spring:message code="create_decision_variable"/>
			                			</c:otherwise>
	                        		</c:choose> 
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
						<table>
							<tr>
								<td>
									<table>						
										<col style="width:150px">
										<col style="width:400px">
										<col style="width:240px">
										<tr>
											<td class="infosmall"><spring:message code="name"/>*</td>
											<td></td>
										</tr>
			                            <tr>
			                            	<td><form:input style="width:400px" type="text" path="name"/></td>
		                            	</tr>
										<tr height=5></tr>
			                            <tr>
			                                <td class="infosmall"><spring:message code="type"/></td>
		                                </tr>
		                                <tr>
			                                <td> 
			                                    <select name="typeid" id="typeid" size="1">
			                                    <c:forEach items="${typechoices}" var="typechoice">
				                                    <c:choose>
				                                        <c:when test="${decVar.type.typeid == typechoice.typeid}">
				                                           <option value="${typechoice.typeid}" selected>${typechoice.name}</option>
				                                        </c:when>
				                                        <c:otherwise>
			                                               <option value="${typechoice.typeid}">${typechoice.name}</option>
				                                        </c:otherwise>
				                                    </c:choose>
				                                </c:forEach>
			                                    </select>
			                                </td>
			                                <td></td>
			                            </tr>                   
										<tr height=5></tr>
										<tr>
											<td class="infosmall"><spring:message code="lower_bound"/>*</td>
										</tr>
										<tr>
											<td><form:input style="width:400px" type="text" path="lowerbound"/></td>
											<td></td>
										</tr>
										<tr height=5></tr>
										<tr>
											<td class="infosmall"><spring:message code="upper_bound"/>*</td>
										</tr>
										<tr>
											<td><form:input style="width:400px" type="text" path="upperbound"/></td>
											<td></td>
										</tr>
										<tr>
											<td align=right><input type="submit" value="Ok"/>
											<a href="geneticalgorithm.html"><button type="button"><spring:message code="cancel"/></button></a></td>
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
</form:form>
</body>
</html>