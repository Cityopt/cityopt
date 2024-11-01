<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="function" type="eu.cityopt.DTO.ObjectiveFunctionDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>CityOpt <spring:message code="edit_obj_func"/></title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
<form:form method="post" action="editsgobjfunction.html" modelAttribute="function">
<form:input type="hidden" path="version"/>
<form:input type="hidden" path="obtfunctionid"/>
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
								<td align="left">
			                        <font class="activeproject">${project.name}</font>&nbsp;&nbsp;<spring:message code="edit_obj_func_small"/>
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
					<td valign="top">
						<table>
							<col style="width:30px">
							<col style="width:250px">
							<col style="width:20px">
							<col style="width:250px">
							<col style="width:80px">

							<tr height="20">
							</tr>
							<tr>
								<td></td>
								<td><spring:message code="name"/>*</td>
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
								<td><spring:message code="expression"/>*</td>
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
                                <td colspan="3"><spring:message code="optimization_sense"/>: 
                                    <select name="optsense" id="optsense" size="1">
                                    <c:choose>
                                        <c:when test="${function.ismaximise}">
	                                       <option value="min"><spring:message code="minimize"/></option>
	                                       <option value="max" selected><spring:message code="maximize"/></option>
                                        </c:when>
                                        <c:otherwise>
                                           <option value="min" selected><spring:message code="minimize"/></option>
                                           <option value="max"><spring:message code="maximize"/></option>
                                        </c:otherwise>
                                    </c:choose>
                                    </select>
                                </td>
                                <td></td>
                            </tr>                   
							<tr>
								<td></td>
								<td></td>
								<td></td>
								<td align="right">
									<button class="activebutton" type="submit">Ok</button>
									<a href="geneticalgorithm.html"><button type="button"><spring:message code="cancel"/></button></a></td>
								<td></td>
							</tr>					
						</table>
					</td>
				</tr>
				<tr><td></td></tr>
			</table>
			</div>
		</td>
	</tr>
</table>
</form:form>
</body>
</html>