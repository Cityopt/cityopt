<%--@elvariable id="apv" type="eu.cityopt.DTO.AlgoParamValDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt edit algorithm parameter values</title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
<form:form modelAttribute="algoparamvalform" method="post" action="editsgalgoparamval.html">
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
	                  <h2>Edit algorithm parameter values</h2>
	               </td>
               </tr>
              <tr>
	              <td>
	                Algorithm: ${scengenerator.algorithm.description}
	              </td>
              </tr>
              <tr height="10"></tr>
              <tr>
                <td>
	              <table class="tablestyle" style="width: 450px">
                      <col>
                      <col align="right">
                      <col align="right">
	                  <tr>
	                      <th>Parameter</th>
	                      <th>Value</th>
	                      <th>Default value</th>
	                  </tr>
	
	                  <c:forEach items="${algoparamvals}" var="apv">
	                  <tr>
	                      <td>${apv.algoparam.name}</td>
	                      <td>
                             <form:input align="right" style="width:200px" type="text" path="valueByParamId[${apv.algoparam.aparamsid}]"/>
	                      </td>
	                      <td align="right">${apv.algoparam.defaultvalue}</td>
	                  </tr>
	                  </c:forEach>
	              </table>
                </td>
              </tr>
              <tr height="10"></tr>
              <tr>
                  <td align="right">
                    <input type="submit" value="Ok"></input>
                    <a href="geneticalgorithm.html"><button type="button">Cancel</button></a>
                  </td>
              </tr>   
			</table>
		</td>
	</tr>
</table>
</form:form>
</body>
</html>