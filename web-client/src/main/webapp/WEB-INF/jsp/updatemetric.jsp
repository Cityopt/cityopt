<%--@elvariable id="metric" type="eu.cityopt.DTO.MetricDTO"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt create metric</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />

</head>

<body>
<table cellspacing="0px" cellpadding="0px">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>
		<td width=30></td>
		<td valign="top">
			<div style="overflow:scroll;height:100%;width:1000px;overflow:auto">
			<form:form method="post" action="updatemetric.html?action=${action}&metricid=${metricid}" modelAttribute="paramForm">
			<table>
				<tr>
					<td>
						<!-- Create metric -->
						<h2><spring:message code="create_metric"/> step 2</h2>
					</td>
				</tr>
				<tr>
					<td><p>Please finish the metric by editing it by hand and by naming it. You can also add functions if needed.</p>
					<p>Example expression: <i>"integrate(HOUR_HEAT.SP_VALUE, 0, Infinity) / 3600"</i></p></td>
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
								<c:set var="nametooltip"><spring:message code="tooltip_createmetric_name"/></c:set>
								<td colspan="3"><form:input style="width:100%" title="${nametooltip}" type="text" path="name"/></td>
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
								<td colspan="3"><form:input style="width:100%" title="${expressiontip}" type="text" path="value"/></td>
								<td></td>
							</tr>
							<tr>
								<td></td>
								<td>					
									Unit
								</td>
							</tr>
							<tr>
								<td></td>
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
									<a href="metricdefinition.html"><button style="width:100px" type="button" value="Cancel">
									<spring:message code="cancel"/></button></a>
								</td>
								<td></td>
							</tr>					
						</table>
					</td>
				</tr>
			</table>
		</form:form>
		</div>
		</td>
     </tr>
</table>
</body>
</html>