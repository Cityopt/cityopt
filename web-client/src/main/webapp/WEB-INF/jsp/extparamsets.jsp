<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="constraint" type="eu.cityopt.DTO.OptConstraintDTO"--%>
<%--@elvariable id="component" type="eu.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="inputParam" type="eu.cityopt.DTO.InputParameterDTO"--%>
<%--@elvariable id="outputVar" type="eu.cityopt.DTO.OutputVariableDTO"--%>
<%--@elvariable id="metric" type="eu.cityopt.DTO.MetricDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>

<!-- JQuery script: For Radio button for in-Page events -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script>
var getUrlParameter = function getUrlParameter(sParam) {
    var sPageURL = decodeURIComponent(window.location.search.substring(1)),
        sURLVariables = sPageURL.split('&'),
        sParameterName,
        i;

    for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split('=');

        if (sParameterName[0] === sParam) {
            return sParameterName[1] === undefined ? true : sParameterName[1];
        }
    }
};

</script>
<title>CityOpt external parameter sets</title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
	<table cellspacing="0" cellpadding="0">
		<tr>
			<td><%@ include file="mainmenu.inc"%></td>
			<td width="30"></td>
			<td valign="top">
				<table>
					<tr>
						<td>
							<!--External parameter sets  -->
							<h2>
								<spring:message code="external_parameter_sets" />
							</h2>
							<p id="JQTest"></p>
						</td>
					</tr>
					<tr>
						<td>
							<form action="extparamsets.html" method="post">
								<table>
									<col style="width: 255px">
									<col style="width: 30px">
									<col style="width: 350px">
									<tr>
										<!--External parameter sets  -->
										<td><b><spring:message code="external_parameter_sets" /></b></td>
										<td></td>
										<!-- External parameters -->
										<td><b><spring:message code="external_parameters" /></b></td>
									</tr>
									<tr>
										<td valign="top">
											<table class="tablestyle">
												<col style="width: 70px">
												<col style="width: 200px">
												<tr>
													<!-- Select -->
													<th><spring:message code="select" /></th>

													<!--External parameter set  -->
													<th><spring:message code="external_parameter_set" /></th>
												</tr>
											</table>
										</td>
										<td></td>
										<td valign="top">
											<table class="tablestyle">
												<tr>
													<!--External parameter-->
													<th width="125"><spring:message code="name" /></th>
													<th width="135"><spring:message code="value" /></th>
												</tr>

											</table>
										</td>
									</tr>
									<!-- Create Data Entries -->									
										<tr class="tablestyle">
											<td style="border-style: hidden" valign="top" align="left" border="none" cellspacing="0" cellpadding="0">
												<table style="width: 250px" cellspacing="0" cellpadding="0"
													border="1">
													<col style="width: 60px">
													<col style="width: 200px">

													<!-- ForEachElement: Select element // External parameterset -->
													<!-- Example:<tr><td>Select</td><td>Dataentry</td></tr> -->
													<c:forEach items="${extParamValSets}" var="extParamValSet">
														<tr class="tablestyle" align="left" valign="top">
															<c:choose>
																<c:when test="${extParamValSet.extparamvalsetid == id}">
																	<input type="hidden" name=id
																		value="${extParamValSet.extparamvalsetid}" />
																	<tr style="background-color: #D4D4D4">
																		<td><spring:message code="selected" /></td>
																</c:when>
																<c:otherwise>
																	<tr>
																		<td><a
																			href="<c:url value='extparamsets.html?id=${extParamValSet.extparamvalsetid}'/>">
																				<!--<input type="button" name="id" th:field="*{id}" value="${extParamValSet.extparamvalsetid}">-->
																				<spring:message code="select" />
																		</a></td>
																</c:otherwise>
															</c:choose>
															<td>${extParamValSet.name}</td>
														</tr>
													</c:forEach>
												</table>
											<td style="border-style: hidden"></td>
											<td style="border-style: hidden" valign="top">
												<table id="externalParameters" cellspacing="0"
													style="width: 255px" border="1" align="left">
													<!-- For each element get the External parameter Parameter -->

													<c:forEach items="${extParamVals}" var="extParamVal">
														<tr data-extparam-id="${extParamVal.extparam.extparamid}">
															<td>${extParamVal.extparam.name}</td>
															<td>${extParamVal.value}</td>
														</tr>
													</c:forEach>

													</tr>
												</table>
											</td>
									<tr>
										</div>
										<td></td>
										<td></td>
										<td></td>
										<!-- Submit button -->

										<!-- Close button -->
										<td><input name="index" type="submit"></a></td>
										<td align="right"><a href="editoptimizationset.html"><button
													type="button">
													<spring:message code="close" />
												</button></a></td>
									</tr>
								</table>
							</form>
						</td>
						</td>
					</tr>
					<tr height=20></tr>
				</table>
			</td>
		</tr>
	</table>
</body>
</html>