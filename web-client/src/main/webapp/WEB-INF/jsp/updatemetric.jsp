<%--@elvariable id="metric" type="eu.cityopt.DTO.MetricDTO"--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>CityOpt <spring:message code="update_metric"/></title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />

</head>

<body>
<table cellspacing="0px" cellpadding="0px">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>
		<td valign="top">
			<table class="maintable">			
				<%@ include file="toprow.inc"%>
				<tr class="titlerow">
					<td class="spacecolumn"></td>
					<td>
						<table width="100%">
							<tr>
								<td><spring:message code="create_metric"/> step 2</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td class="spacecolumn"></td>
					<td valign="top">
						<form:form method="post" action="updatemetric.html?action=${action}&metricid=${metricid}" modelAttribute="paramForm">
						<table>
							<tr>
								<td class="error">${error}
								</td>
							</tr>
							<tr>
								<td class="regular"><spring:message code="create_metric_instructions_2"/></td>
							</tr>
							<tr height="10"></tr>
							<tr>
								<td>
									<table>
										<col style="width:250px">
										<col style="width:100px">
										<col style="width:100px">
			
										<tr height="20">
											<!-- Functions -->
											<td><b><spring:message code="functions"/></b></td>
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
										<tr>
											<!-- Name -->
											<td class="infosmall"><spring:message code="name"/>*</td>
											<td></td>
											<td></td>
											<td></td>
										</tr>					
										<tr>
											<c:set var="nametooltip"><spring:message code="tooltip_createmetric_name"/></c:set>
											<td colspan="3"><form:input style="width:100%" title="${nametooltip}" type="text" path="name"/></td>
											<td></td>
										</tr>
										<tr height="10"></tr>
										<tr>
											<!-- Expression -->
											<td class="infosmall"><spring:message code="expression"/>*</td>
											<td></td>
											<td></td>
											<td></td>
										</tr>					
										<tr>
											<c:set var="expressiontip"><spring:message code="tooltip_expression"/></c:set>
											<td colspan="3"><form:input style="width:100%" title="${expressiontip}" type="text" path="value"/></td>
											<td></td>
										</tr>
										<tr height="10"></tr>
										<tr>
											<td class="infosmall">					
												<spring:message code="unit"/>
											</td>
										</tr>
										<tr>
											<td>					
												<form:select path="unit">
													<option value="${paramForm.unit}" selected>${paramForm.unit}</option>
													<c:forEach items="${units}" var="unit">																																
														<option value="${unit.name}">${unit.name}</option>
													</c:forEach>
												</form:select>				
											</td>
										</tr>
										<tr>
											<td></td>
											<td align="right">
												<button type="submit" style="width:100px" class="activebutton"><spring:message code="ok"/></button>
											</td>
											<td>
												<input type="submit" value="Cancel" name="cancel" style="width: 100px">
											</td>
											<td></td>
										</tr>					
									</table>
								</td>
							</tr>
						</table>
						</form:form>
					</td>
				</tr>
			</table>
		</td>
     </tr>
</table>
</body>
</html>