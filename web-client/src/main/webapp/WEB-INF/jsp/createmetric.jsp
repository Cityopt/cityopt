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
						<h2>Create metric</h2>
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
									Components
								</td>
								<td></td>
								<td>
									Input variables
								</td>
								<td></td>
								<td>Output variables</td>
							</tr>
							<tr>
								<td></td>
								<td valign="top">
									<table class="tablestyle">
										<col style="width:60px">
										<col style="width:190px">
										<tr>
											<th>Select</th>
											<th>Component</th>
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
											<th>Input parameter</th>
											<th>Id</th>
											<th>Default value</th>
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
											<th>Output variable</th>
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
									Metrics
								</td>
								<td></td>
								<td>External parameters</td>
								<td></td>
							</tr>
							<tr>
								<td></td>
								<td>
									<table class="tablestyle">
										<col style="width:250px">
										
										<tr height="20">
										    <th>Name</th>
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
										    <th>Name</th>
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
								<td>Functions</td>
							</tr>
							<tr>
								<td></td>
								<td>
									<table class="tablestyle">
										<col style="width:150px">
										<col style="width:250px">
										<tr>
											<th>Function</th>
											<th>Description</th>
										</tr>
										<tr>
											<td>integrate</td>
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
								<td>Name</td>
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
								<td>Expression</td>
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
									<input type="submit" style="width:100px" value="Ok"></input>
									<a href="metricdefinition.html"><button style="width:100px" type="button" value="Cancel">Cancel</button></a>
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