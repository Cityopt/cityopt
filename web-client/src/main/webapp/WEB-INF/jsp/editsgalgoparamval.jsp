<%--@elvariable id="apv" type="eu.cityopt.DTO.AlgoParamValDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt <spring:message code="edit_algorithm_parameter_values"/></title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<script>
    function openInfoWindow() {
    	   window.open("editsgalgoparamval_info.html", '<spring:message code="edit_algorithm_parameter_values" /> info', 'width=510, height=600');
    }
</script>

<body>
<form:form modelAttribute="algoparamvalform" method="post" action="editsgalgoparamval.html">
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
								<td>
									<font class="activeproject">${project.name}</font>&nbsp;&nbsp;
                           			<spring:message code="edit_algorithm_parameter_values_small"/>
								</td>
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
					<td class="info">${info}</td>
				</tr>
				<tr>
					<td class="spacecolumn"></td>
					<td valign="top">
				    	<table style="width: 700px">
			            	<tr>
			               		<td class="active"><spring:message code="algorithm_parameters"/></td>
			               	</tr>
				            <tr>
			               		<td>
				              		<table class="tablestyle" style="width: 700px">
                   						<col style="width: 400px">
										<col style="width: 200px">
										<col style="width: 100px">
						          		<tr>
				                      		<th><spring:message code="parameter"/></th>
				                      		<th><spring:message code="value"/></th>
				                      		<th><spring:message code="default_value"/></th>
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
			                    <button class="activebutton" type="submit">Ok</button>
			                    <a href="geneticalgorithm.html"><button type="button"><spring:message code="cancel"/></button></a>
			                  </td>
			              </tr>   
						</table>
					</td>
				</tr>
			</table>
			</div>
		</td>
	</tr>
</table>
</form:form>
</body>
</html>