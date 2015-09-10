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
    <title>CityOpt edit decision variable</title>
  </c:when>
  <c:otherwise>
    <title>CityOpt create decision variable</title>
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
		<td width="30"></td>
		<td valign="top">
			<table>
				<tr>
					<td>
                        <c:choose>
                          <c:when test="${optconstid > 0}">
                            <h2>Edit decision variable</h2>
                          </c:when>
                          <c:otherwise>
                            <h2>Create decision variable</h2>
                          </c:otherwise>
                        </c:choose>  
					</td>
				</tr>
				<!--
				<tr>
					<td>
						<table>
							<col style="width:250px">
							<col style="width:30px">
							<col style="width:250px">
							<col style="width:30px">
							<col style="width:250px">

							<tr>
								<td><b>Components</b></td>
								<td></td>
								<td><b>Input parameters</b></td>
								<td></td>
								<td><b>Output variables</b></td>
							</tr>
							<tr>
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
												<td><a href="<c:url value='createdecisionvariable.html?selectedcompid=${component.componentid}'/>">Select</a></td>
											</c:if>
												<td>${component.name}</td>
									   	</tr>
										</c:forEach>
									</table>
								</td>
								<td></td>
								<td valign="top">
									<table class="tablestyle">
										<col style="width:250px">
										<tr>
											<th>Input parameter</th>
										</tr>
					
										<c:forEach items="${inputParams}" var="inputParam">
										<tr>
											<td>${inputParam.name}</td>
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
							<tr height="20"></tr>
							<tr>
								<td><b>Metrics</b></td>
							</tr>
							<tr>
								<td>
									<table class="tablestyle">
										<col style="width:250px">
										
										<tr>
											<th>Metric</th>
										</tr>
					
										<c:forEach items="${metrics}" var="metric">
										<tr>
											<td>${metric.name}</td>
									   	</tr>
										</c:forEach>
									</table>										
								</td>
							</tr>
						</table>
					</td>
				</tr>
				-->
				<tr height=20></tr>
				<tr>
					<td>
						<table>						
							<col style="width:30px">
							<col style="width:150px">
							<col style="width:400px">
							<col style="width:240px">
							<tr>
								<td></td>
								<td>Name</td>
								<td><form:input style="width:400px" type="text" path="name"/></td>
								<td></td>
							</tr>
                            <tr height=10></tr>
                            <tr>
                                <td></td>
                                <td colspan="3">Type: 
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
							<tr height=10></tr>
							<tr>
								<td></td>
								<td>Lower bound</td>
								<td><form:input style="width:400px" type="text" path="lowerbound"/></td>
								<td></td>
							</tr>
							<tr height=10></tr>
							<tr>
								<td></td>
								<td>Upper bound</td>
								<td><form:input style="width:400px" type="text" path="upperbound"/></td>
								<td></td>
							</tr>
							<tr>
								<td></td>
								<td></td>
								<td></td>
								<td align=right><input type="submit" value="Ok"/>
								<a href="geneticalgorithm.html"><button type="button">Cancel</button></a></td>
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