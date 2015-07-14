<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="function" type="eu.cityopt.DTO.ObjectiveFunctionDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<c:choose>
  <c:when test="${obtfunctionid > 0}">
    <title>CityOpt edit GA objective function</title>
  </c:when>
  <c:otherwise>
    <title>CityOpt create GA objective function</title>
  </c:otherwise>
</c:choose>  
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
<form:form method="post" action="editsgobjfunction.html" modelAttribute="function">
<form:input type="hidden" path="version"/>
<form:input type="hidden" path="obtfunctionid"/>
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
                          <c:when test="${obtfunctionid > 0}">
                            <h2>Edit GA objective function</h2>
                          </c:when>
                          <c:otherwise>
                            <h2>Create GA objective function</h2>
                          </c:otherwise>
                        </c:choose>  
					</td>
				</tr>
				<tr>
					<td>
						<table>
							<col style="width:30px">
							<col style="width:250px">
							<col style="width:20px">
							<col style="width:250px">
							<col style="width:80px">

							<!--
							<tr>
								<td></td>
								<td>
									Components
								</td>
								<td></td>
								<td>
									Output variables
								</td>
								<td></td>
							</tr>
							<tr>
								<td></td>
								<td>
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
												<td><a href="<c:url value='editsgobjfunction.html?objid=${function.obtfunctionid}&selectedcompid=${component.componentid}'/>">Select</a></td>
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
											<th>Output variable</th>
										</tr>
					
										<c:forEach items="${outputVars}" var="outputVar">
										<tr>
											<td>${outputVar.name}</td>
										</tr>
										</c:forEach>
									</table>
								</td>
								<td></td>
							</tr>
							<tr height="20">
							</tr>
							<tr>
								<td></td>
								<td>
									Metrics
								</td>
								<td></td>
								<td>Decision variables</td>
								<td></td>
							</tr>
							<tr>
								<td></td>
								<td>
									<table class="tablestyle">
										<col style="width:250px">
										
										<c:forEach items="${metrics}" var="metric">
										<tr>
											<td>${metric.name}</td>
									   	</tr>
										</c:forEach>
									</table>										
								</td>
								<td></td>
								<td>
									<table class="tablestyle">
										<col style="width:100px">
										<col style="width:250px">
										
										<c:forEach items="${decisionVars}" var="decisionVar">
										<tr>
											<td>${decisionVar.name}</td>
											<td>${decisionVar.expression}</td>
									   	</tr>
										</c:forEach>
									</table>										
								</td>
								<td></td>
							</tr>
							 -->
							<tr height="20">
							</tr>
							<tr>
								<td></td>
								<td>Name</td>
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
								<td>Expression</td>
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
                                <td colspan="3">Optimization sense: 
                                    <select name="optsense" id="optsense" size="1">
                                    <c:choose>
                                        <c:when test="${function.ismaximise}">
	                                       <option value="min">Minimize</option>
	                                       <option value="max" selected>Maximize</option>
                                        </c:when>
                                        <c:otherwise>
                                           <option value="min" selected>Minimize</option>
                                           <option value="max">Maximize</option>
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
								<td align="right"><input type="submit" value="Ok"></input>
								<a href="geneticalgorithm.html"><button type="button">Cancel</button></a></td>
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