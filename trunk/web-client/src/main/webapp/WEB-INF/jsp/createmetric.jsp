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
			<div style="overflow:scroll;height:1000px;width:1000px;overflow:auto">
			<form:form method="post" action="createmetric.html" modelAttribute="metric">
			<table>
				<tr>
					<td>
						<!-- Create metric -->
						<h2><spring:message code="create_metric"/></h2>
					</td>
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

							<tr>
								<td></td>
								<td>
									<!-- Components -->
									<spring:message code="components"/>
									
								</td>
								<td></td>
								<td>
									<!-- Input variables -->
									<spring:message code="input_variables"/>
								</td>
								<td></td>
								<!--Output variables  -->
								<spring:message code="output_variables"/>
								<td></td>
							</tr>
							<tr>
								<td></td>
								<td valign="top">
									<table class="tablestyle">
										<col style="width:60px">
										<col style="width:190px">
										<tr>
											<!-- Select -->
											<th><spring:message code="select"/></th>
											<!-- Component -->
											<th><spring:message code="component"/></th>
										</tr>
							
										<c:forEach items="${components}" var="component">
										<tr>
											<c:if test="${selectedcompid == component.componentid}">
												<tr style="background-color: #D4D4D4"><td>Selected</td>
											</c:if>
											<c:if test="${selectedcompid != component.componentid}">
												<tr>
												<td><a href="<c:url value='createmetric.html?selectedcompid=${component.componentid}'/>">Select</a></td>
											</c:if>
												<td>${component.name}</td>
									   	</tr>
										</c:forEach>
									</table>
								</td>
								<td></td>
								<td valign="top">
									<table class="tablestyle">
										<col style="width:150px">
										<col style="width:60px">
										<col style="width:150px">
										<tr>
											<!-- Input parameter, Id, Default value -->
											<th><spring:message code="input_parameter"/></th>
											<th><spring:message code="id"/></th>
											<th><spring:message code="default_value"/></th>
										</tr>
										
										<c:forEach items="${inputParameters}" var="inputParam">
										<tr>
											<td>${inputParam.name}</td>
									    	<td>${inputParam.inputid}</td>
									    	<td>${inputParam.defaultvalue}</td>
									   	</tr>
										</c:forEach>
									</table>
								</td>
								<td></td>
								<td valign="top">
									<table class="tablestyle">
										<col style="width:250px">
										<tr>
											<!--Output variable-->
											<th><spring:message code="output_variable"/></th>
										</tr>
					
										<c:forEach items="${outputVars}" var="outputVar">
										<tr>
											<td>${outputVar.name}</td>
										</tr>
										</c:forEach>
									</table>
								</td>	
							</tr>
							<tr height="20">
							</tr>
							<tr>
								<td></td>
								<td>
									<!--Metrics-->
									<spring:message code="metrics"/>
								</td>
								<td></td>
									<!-- External parameters -->
								<td><spring:message code="external_parameters"/></td>
								<td></td>
							</tr>
							<tr>
								<td></td>
								<td>
									<table class="tablestyle">
										<col style="width:250px">
										
										<tr height="20">
											<!-- Name -->
										    <th><spring:message code="name"/></th>
										</tr>
									
										<c:forEach items="${metrics}" var="metric">
										<tr>
											<td>${metric.name}</td>
									   	</tr>
										</c:forEach>
									</table>										
								</td>
								<td></td>
								<td valign="top">
									<table class="tablestyle" width="450">
										<col style="width:200px">
												
										<tr height="20">
											<!-- Name -->
										    <th><spring:message code="name"/></th>
										</tr>
										
										<c:forEach items="${extParamVals}" var="extParamVal">
										<tr>
											<td>${extParamVal.extparam.name}</td>
									    </tr>
										</c:forEach>
									</table>
								</td>
								<td></td>
							</tr>
							<tr height="20"></tr>
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
											<!-- Why hard coding?... Integrate -->
											<td><spring:message code="integrate"/></td>
											<td></td>
									   	</tr>
										
									</table>										
								</td>
								<td></td>
								<td></td>
								<td></td>
							</tr>
							<tr height="20"></tr>
							<tr height="20">
								<td></td>
								<td colspan="3">
									Example: <i>"integrate(HOUR_HEAT.SP_VALUE, 0, Infinity) / 3600"</i>
								</td>
							</tr>
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
								<td colspan="3"><form:input style="width:100%" type="text" path="expression"/></td>
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