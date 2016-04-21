<%--@elvariable id="inputParamForm" type="eu.cityopt.web.ParamForm"--%>
<%--@elvariable id="inputParam" type="eu.cityopt.DTO.InputParameterDTO"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt <spring:message code="edit_input_parameter"/></title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0px" cellpadding="0px">
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
								<td><spring:message code="edit_input_parameter"/></td>
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
					<td valign="top">
						<div style="overflow:scroll;height:800px;width:600px;overflow:auto">
						
						<table align="center">
							<col style="width:150px">
							<col style="width:80px">
							<col style="width:300px">
							<form:form method="post" action="editinputparameter.html?inputparamid=${inputParam.inputid}" modelAttribute="inputParamForm">
							<tr>
								<td>
									<spring:message code="name"/>
								</td>
								<td>
									${inputParam.name}
								</td>
							</tr>
							<c:choose>
								<c:when test="${inputParam.getType().getTypeid() >= 5}">
									<tr>
										<td>
											<spring:message code="type"/>
										</td>
										<td><spring:message code="time_series"/></td>																		
									</tr>
									<tr>
										<td colspan="2">
											<table border="1">
												<tr>
													<td>Times</td>
													<c:forEach items="${times}" var="time">
														<td>${time}</td>
													</c:forEach>
												</tr>
												<tr>
													<td>Values</td>
													<c:forEach items="${values}" var="value">
														<td>${value}</td>
													</c:forEach>
												</tr>
											</table>
										</td>
									</tr>			
								</c:when>
								<c:otherwise>
								<tr>
									<td>
										<spring:message code="type"/>
									</td>
										<td><spring:message code="value"/></td>																		
								</tr>
								<tr>
									<td>
										<spring:message code="default_value"/>
									</td>
									<td>
										${inputParam.defaultvalue}
									</td>
								</tr>			
								</c:otherwise>
							</c:choose>
							<tr>
								<td>					
									<spring:message code="unit"/>
								</td>
								<td>					
									${inputParam.unit.name}
								</td>
							</tr>
							
							<tr height="20">
								<td>
								</td>
							</tr>
							<tr>
								<td>
									<h2><spring:message code="save_as_value"/></h2>
								</td>
							</tr>
							<tr>
								<td>
									<spring:message code="name"/>*
								</td>
								<td>
									<form:input style="width:300px" type="text" path="name"/>
								</td>
							</tr>
							<tr>
								<td>
									<spring:message code="default_value"/>
								</td>
								<td>
									<form:input style="width:300px" type="text" path="value"/>
								</td>
							</tr>			
							<tr>
								<td>					
									<spring:message code="unit"/>
								</td>
								<td>					
									<form:select path="unit">
										<option value="${inputParam.unit.name}" selected>${inputParam.unit.name}</option>
										<c:forEach items="${units}" var="unit">																																
											<option value="${unit.name}">${unit.name}</option>
										</c:forEach>
									</form:select>				
								</td>
							</tr>
							
							<tr height="10">
								<td>
								</td>
							</tr>
							<tr>
								<td></td>
								<td align="right">
									<input style="width:120px" type="submit" value="<spring:message code="save_as_value"/>"/>
									<input style="width:120px" type="submit" id="cancel" name="cancel" value="<spring:message code="cancel"/>">
								</td>
							</tr>
							<tr height="10">
								<td>
								</td>
							</tr>
							<tr>
								<td colspan="2">
									<h1><spring:message code="import_and_save_as_time_series"/></h1>
								</td>
							</tr>
							</form:form>
							<form:form method="POST" action="importinputtimeseries.html?inputid=${inputParam.inputid}" enctype="multipart/form-data">
							<tr>
								<td><spring:message code="import_time_series"/></td>
								<td><input id="file" name="file" type="file"/></td>
							</tr>
							<tr>	
			  					<td></td>
			   					<td align="right">
			   						<input type="submit" value="<spring:message code="import_and_save_as_time_series"/>">
									<input style="width:100px" type="submit" id ="cancel" name="cancel" value="<spring:message code="cancel"/>">
			  					</td>
							</tr>	
							</form:form>
						</table>
						</div>
					</td>
			     </tr>
		     </table>
	     	</div>
     	</td>
   	</tr>
</table>
</body>
</html>