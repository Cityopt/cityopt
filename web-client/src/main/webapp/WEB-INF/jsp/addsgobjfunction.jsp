<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="objectivefunction" type="com.cityopt.DTO.ObjectiveFunctionDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt <spring:message code="add_objective_function"/></title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
<table cellspacing="0" cellpadding="0">
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
								<td><spring:message code="add_objective_function"/></td>
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
			            	<tr>
			                    <td>
			                        <table>
			                            <col style="width:750px">
			                            <tr>
			                                <td><b><spring:message code="objective_functions"/></b></td>
			                            </tr>
			                            <tr>
			                                <td valign="top">
			                                    <table class="tablestyle">
			                                        <col style="width:60px">
			                                        <col style="width:160px">
			                                        <col style="width:100px">
			                                        <col style="width:430px">
			                                        <tr>
			                                            <th><spring:message code="import"/></th>
			                                            <th><spring:message code="name"/></th>
			                                            <th><spring:message code="sense"/></th>
			                                            <th><spring:message code="expression"/></th>
			                                        </tr>
			                            
			                                        <c:forEach items="${objFuncs}" var="objectivefunction">
			                                        <tr>
			                                            <td>
			                                            	<a href="addsgobjfunction.html?obtfunctionid=${objectivefunction.obtfunctionid}">
			                                            		<button type="button">Import</button>
			                                           		</a>
			                                       		</td>
			                                            <td>${objectivefunction.name}</td>
			                                            <td>
			                                                 <c:choose>
			                                                     <c:when test="${objectivefunction.ismaximise}">Maximize</c:when>
			                                                     <c:otherwise>Minimize</c:otherwise>
			                                                 </c:choose>
			                                            </td>
			                                            <td>${objectivefunction.expression}</td>
			                                        </tr>
			                                        </c:forEach>
			                                    </table>
			                                </td>
			                            </tr>
			                            <tr height="10"></tr>
			                            <tr>
			                                <td align="right"><a href="geneticalgorithm.html"><button type="button"><spring:message code="close"/></button></a></td>
			                            </tr>
			                        </table>
			                    </td>
			                </tr>
			                <tr height=20></tr>
			            </table>
			        </td>
		        </tr>
	        </table>
        	</div>
       	</td>
    </tr>
</table>
</body>
</html>