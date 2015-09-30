<%--@elvariable id="epv" type="eu.cityopt.DTO.ExtParamValDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<script language="javascript"><%@ include file="cityopt.js"%></script>
<head>
<title>CityOpt edit GA model input parameters</title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body onLoad="showComponent('ModelParameter',${usersession.componentId})">
<form:form modelAttribute="modelparamform" method="post" action="editsgmodelparams.html">
<table cellspacing="0" cellpadding="0">
	<tr>
		<td>
			<%@ include file="mainmenu.inc"%>
		</td>
		<td width="30"></td>
		<td valign="top">
          <table style="width: 450px">
               <tr>
	               <td>
	                  <h2>Edit GA model input parameters</h2>
	               </td>
               </tr>
              <tr>
	              <td>
	                GA optimization set: ${scengenerator.name}
	              </td>
              </tr>
              <tr height="10"></tr>
              <tr><td colspan="2">Component:
                <select name="selectedcompid" onChange="selectComponent('ModelParameter',this.value)">
                 <c:forEach items="${inputcomponents}" var="component">
                   <c:choose>
                       <c:when test="${component.componentid == usersession.componentId}">
                           <option value="${component.componentid}" selected>${component.name}</option>
                       </c:when>
                       <c:otherwise>
                           <option value="${component.componentid}">${component.name}</option>
                       </c:otherwise>
                   </c:choose>
                 </c:forEach>
                </select>
                </td>
              </tr>
              <tr>
                <td><div class="error">${errorMessage}</div></td>
              </tr>
              <tr>
                <td>
	              <table class="tablestyle" style="width: 450px">
                     <tr>
                         <th>Parameter</th>
                         <th>Value(s)</th>
                         <th>Unit</th>
                         <th>Group</th>
                     </tr>
                     <c:forEach items="${modelparams}" var="mp">
                     <tr class="Component${mp.inputparameter.componentComponentid} ModelParameter">
                         <td>${mp.inputparameter.name}</td>
                         <td>
                             <form:input align="right" style="width:200px" type="text" path="valueByInputId[${mp.inputparameter.inputid}]"/>
                         </td>
                         <td>${empty mp.inputparameter.unit ? '' : mp.inputparameter.unit.name}</td>
                         <td>
                            <c:choose>
                            <c:when test="${empty groups}">
                                <form:hidden path="groupByInputId[${mp.inputparameter.inputid}]"/>
                                --
                            </c:when>
                            <c:otherwise>
	                         <form:select path="groupByInputId[${mp.inputparameter.inputid}]">
		                         <form:option value="">--</form:option>
		                         <form:options items="${groups}"></form:options>
	                         </form:select>
	                         </c:otherwise>
                            </c:choose>
                         </td>
                     </tr>
                     </c:forEach>
	              </table>
                </td>
              </tr>
              <tr height="10"></tr>
              <tr>
                  <td align="right">
                    <input type="submit" value="Ok">
                    <a href="geneticalgorithm.html"><button type="button" style="width: 100px">Cancel</button></a>
                  </td>
              </tr>   
              <tr>
                  <td>
                    <input type="submit" name="newgroup" value="New Group">
                    <input type="submit" name="cleangroups" value="Delete Empty Groups">
                  </td>
              </tr>   
			</table>
		</td>
	</tr>
</table>
</form:form>
</body>
</html>