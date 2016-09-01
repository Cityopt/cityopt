<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="constraint" type="eu.cityopt.DTO.OptConstraintDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<c:choose>
  <c:when test="${optconstid > 0}">
    <title>CityOpt <spring:message code="edit_constraint"/></title>
  </c:when>
  <c:otherwise>
    <title>CityOpt <spring:message code="create_constraint"/></title>
  </c:otherwise>
</c:choose>  
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
<form:form method="post" action="editsgconstraint.html" modelAttribute="constraint">
<form:input type="hidden" path="version"/>
<form:input type="hidden" path="optconstid"/>
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
							  			<c:when test="${optconstid > 0}">
	                            			<spring:message code="edit_constraint"/>
							  			</c:when>
							  			<c:otherwise>
		                        			<spring:message code="create_constraint"/>
							  			</c:otherwise>
									</c:choose>  
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
		                                </tr>
		                                <tr>
			                                <td><form:input style="width:400px" type="text" path="name"/></td>
			                                <td></td>
			                            </tr>
			                            <tr height=10></tr>
			                            <tr>
			                                <td class="infosmall"><spring:message code="lower_bound"/>*</td>
		                                </tr>
		                                <tr>
			                                <td><form:input style="width:400px" type="text" path="lowerbound"/></td>
			                                <td></td>
			                            </tr>
			                            <tr height=10></tr>
			                            <tr>
			                                <td class="infosmall"><spring:message code="expression"/>*</td>
		                                </tr>
		                                <tr>
			                                <td><form:input style="width:400px" type="text" path="expression"/></td>
			                                <td></td>
			                            </tr>
			                            <tr height=10></tr>
			                            <tr>
			                                <td class="infosmall"><spring:message code="upper_bound"/>*</td>
		                                </tr>
		                                <tr>
			                                <td><form:input style="width:400px" type="text" path="upperbound"/></td>
			                                <td></td>
			                            </tr>
			                            <tr>
			                                <td align="right">
			                                	<button class="activebutton" type="submit">Ok</button>
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