<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="constraint" type="com.cityopt.DTO.OptConstraintDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt add constraint</title>
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
                        <h2>Add constraint</h2>
                    </td>
                </tr>
                <tr>
                    <td>
                        <table>
                            <col style="width:960px">

                            <tr>
                                <td><b>Constraints</b></td>
                            </tr>
                            <tr>
                                <td valign="top">
                                    <table class="tablestyle">
                                        <col style="width:60px">
                                        <col style="width:200px">
                                        <col style="width:460px">
                                        <col style="width:120px">
                                        <col style="width:120px">
                                        <tr>
                                            <th></th>
                                            <th>Constraint</th>
                                            <th>Expression</th>
                                            <th>Lower bound</th>
                                            <th>Upper bound</th>
                                        </tr>
                            
                                        <c:forEach items="${constraints}" var="constraint">
                                        <tr>
                                            <form method="post" action="addsgconstraint.html">
                                            <input type="hidden" name="constrid" id="constrid" value="${constraint.optconstid}">
                                            <td><input type="submit" value="Add"></td>
                                            <td>${constraint.name}</td>
                                            <td>${constraint.expression}</td>
                                            <td>${constraint.lowerbound}</td>
                                            <td>${constraint.upperbound}</td>
                                            </form>
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