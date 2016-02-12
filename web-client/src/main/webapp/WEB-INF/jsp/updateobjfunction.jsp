<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="objFuncForm" type="eu.cityopt.web.ObjFuncForm"--%>
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
<form:form method="post" action="updateobjfunction.html?type=${type}" modelAttribute="objFuncForm">
<table cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>
		<td width="30"></td>
		<td valign="top">
			<table>
				<tr>
					<td><font color="red">${error}</font></td>
				</tr>
				<tr>
					<td>
						<h2><spring:message code="create_obj_func"/> step 2</h2>
					</td>
				</tr>
				<tr>
					<td><p><spring:message code="create_obj_func_instructions_2"/></p></td>
				</tr>
				<tr>
					<td>
						<table>
							<col style="width:30px">
							<col style="width:250px">
							<col style="width:20px">
							<col style="width:250px">
							<col style="width:20px">
							<col style="width:250px">

							<tr height="20">
								<td></td>
								<!-- Functions -->
								<td><spring:message code="functions"/></td>
							</tr>
							<tr>
								<td></td>
								<td>
									<table class="tablestyle">
										<col style="width:150px">
										<col style="width:250px">
										<tr>
											<th><spring:message code="function"/></th>
											<th><spring:message code="description"/></th>
										</tr>
										<tr>
											<td>Integrate</td>
											<td></td>
									   	</tr>
										
									</table>										
								</td>
								<td></td>
								<td>
									
								</td>
								<td></td>
							</tr>
							<tr height="20"></tr>
							<tr height="20"></tr>
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
								<td colspan="3"><form:input style="width:100%" type="text" path="name"/></td>
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
								<c:set var="expressiontip"><spring:message code="tooltip_expression"/></c:set>
								<td colspan="3"><form:input style="width:100%" title="${expressiontip}" type="text" path="expression"/></td>
								<td></td>
							</tr>
							<tr>
								<td></td>
								<!-- Optimization sense: Minimize / Maximize -->
								<td colspan="3"><spring:message code="optimization_sense"/>: 
									
									<select name="optsense" id="optsense" size="1">
										<c:choose>
											<c:when test="${function.ismaximise}">
												<option value="1" ><spring:message code="minimize"/></option>
												<option value="2" selected><spring:message code="maximize"/></option>
											</c:when>
											<c:otherwise>
												<option value="1" selected><spring:message code="minimize"/></option>
												<option value="2"><spring:message code="maximize"/></option>
											</c:otherwise>
										</c:choose>
									</select>
								</td>
						
								<td></td>
							</tr>	
							<tr>
								<td></td>
								<td colspan="3"> 
								</td>
								<td></td>
							</tr>					
							<tr>
								<td></td>
								<td></td>
								<td></td>
								<td align="right">
									<!-- Ok submit and Cancel -button -->
									<input type="submit" style="width:100px" value="<spring:message code="ok"/>"></input>
									<input type="submit" value="Cancel" name="cancel">
								</td>
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