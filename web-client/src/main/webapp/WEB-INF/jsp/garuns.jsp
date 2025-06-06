<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<script language="javascript"><%@ include file="cityopt.js"%></script>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>CityOpt <spring:message code="running_genetic_optimizations"/></title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body onLoad="localizeUTCTimes('utctime')">
<table cellspacing="0" cellpadding="0">
	<tr>	
		<td><%@ include file="mainmenu.inc"%></td>
		<td valign="top">
			<div style="overflow: auto; height: 100%; width: 1100px; overflow: auto;">
			<table class="maintable">			
				<%@ include file="toprow.inc"%>
				<tr class="titlerow">
					<td class="spacecolumn"></td>
					<td>
						<table width="100%">
							<tr>
								<td>
                           			<font class="activeproject">${project.name}</font>&nbsp;&nbsp;<spring:message code="running_genetic_optimizations_small"/>
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
						<table>
							<tr>
								<td class="active">
                           			<spring:message code="running_genetic_optimizations"/>
								</td>
							</tr>
							<tr>
								<td>
									<table class="tablestyle" width="750">
										<col style="width: 100px">
										<col style="width: 150px">
										<col style="width: 150px">
										<col style="width: 150px">
										<col style="width: 100px">
										<col style="width: 100px">
									
										<tr height="20">
										    <th><spring:message code="id"/></th>
											<th><spring:message code="started"/></th>
                                            <th><spring:message code="completion_estimate"/></th>
										    <th><spring:message code="deadline"/></th>
										    <th><spring:message code="status"/></th>
										    <th><spring:message code="abort"/></th>
										</tr>
									
										<c:forEach items="${optRuns}" var="optRun">
										<tr>
											<td>${optRun.id}</td>
											<td class="utctime">${optRun.started}</td>
                                            <td class="utctime">${optRun.estimated}</td>
											<td class="utctime">${optRun.deadline}</td>
											<td>${optRun.status}</td>
											<td>
												<a onclick="return confirm('<spring:message code="confirm_delete"/>')" 
													href="abortgarun.html?id=${optRun.id}">
													<button align="right"  type="button" value="Abort">
													Abort</button>
												</a>
											</td>
									   	</tr>
										</c:forEach>
									</table>
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
</body>
</html>