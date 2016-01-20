<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="objectivefunction" type="com.cityopt.DTO.ObjectiveFunctionDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt add objective function</title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
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
                        <h2>Add objective function</h2>
                    </td>
                </tr>
                <tr>
                    <td>
                        <table>
                            <col style="width:960px">

                            <tr>
                                <td><b>Objective functions</b></td>
                            </tr>
                            <tr>
                                <td valign="top">
                                    <table class="tablestyle">
                                        <col style="width:60px">
                                        <col style="width:160px">
                                        <col style="width:200px">
                                        <col style="width:540px">
                                        <tr>
                                            <th>Import</th>
                                            <th>Name</th>
                                            <th>Sense</th>
                                            <th>Expression</th>
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
                            <tr height="20"></tr>
                            <tr>
                                <td align="right"><a href="geneticalgorithm.html"><button type="button">Close</button></a></td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <tr height=20></tr>
            </table>
        </td>
    </tr>
</table>
</body>
</html>