<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="objFuncForm" type="eu.cityopt.web.ObjFuncForm"--%>
<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt <spring:message code="update_obj_func"/></title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
<form:form method="post" action="updateobjfunction.html?type=${type}" modelAttribute="objFuncForm">
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
								<td><spring:message code="update_obj_func"/> step 2</td>
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
								<td class="regular"><spring:message code="create_obj_func_instructions_2"/></td>
							</tr>
							<tr height="20"></tr>
							<tr>
								<td>
									<table>
										<col style="width:500px">
										<col style="width:100px">
										<col style="width:100px">
			
										<tr height="20">
											<!-- Functions -->
											<td class="active"><spring:message code="functions"/></td>
										</tr>
										<tr>
											<td>
												<div style="overflow:scroll;height:250px;width:500px;overflow:auto">
												<table class="tablestyle">
													<col style="width:150px">
													<col style="width:350px">
													<tr>
														<th><spring:message code="function"/></th>
														<th><spring:message code="description"/></th>
													</tr>
													
													<c:forEach items="${functions}" var="function">
														<tr>
															<td>${function.first}</td>
															<td>${function.second}</td>
													   	</tr>
													</c:forEach>
													
												</table>								
												</div>												
											</td>
											<td></td>
											<td>
												
											</td>
											<td></td>
										</tr>
										<tr height="20"></tr>
										<tr height="20"></tr>
										<tr>
											<!-- Name -->
											<td class="infosmall"><spring:message code="name"/>*</td>
											<td></td>
											<td></td>
											<td></td>
										</tr>					
										<tr>
											<td colspan="3"><form:input style="width:100%" type="text" path="name"/></td>
											<td></td>
										</tr>
										<tr>
											<!-- Expression -->
											<td class="infosmall"><spring:message code="expression"/>*</td>
											<td></td>
											<td></td>
											<td></td>
										</tr>					
										<tr>
											<c:set var="expressiontip"><spring:message code="tooltip_expression"/></c:set>
											<td colspan="3"><form:input style="width:100%" title="${expressiontip}" type="text" path="expression"/></td>
											<td></td>
										</tr>
										<tr>
											<!-- Optimization sense: Minimize / Maximize -->
											<td colspan="3" class="infosmall"><spring:message code="optimization_sense"/>: 
												
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
											<td colspan="3"> 
											</td>
											<td></td>
										</tr>					
										<tr>
											<td></td>
											<td align="right">
												<input type="submit" style="width: 100px" value="<spring:message code="ok"/>"></input>
											</td>
											<td>
												<input type="submit" style="width: 100px" value="Cancel" name="cancel">
											</td>
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