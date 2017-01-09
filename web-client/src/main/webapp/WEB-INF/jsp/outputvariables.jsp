<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="component" type="com.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="selectedComponent" type="com.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="outputVar" type="com.cityopt.DTO.OutputVariableDTO"--%>
<%--@elvariable id="selectedcompid" type="int"--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>CityOpt <spring:message code="output_variables"/></title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top">
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
								<td><font class="activeproject">${project.name}</font>&nbsp;&nbsp;<spring:message code="output_variables_small"/></td>
								<td align="left" width="40">
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
					<td valign="top">
						<table>
							<tr>
								<td>
									<table class="tablegroup">
										<tr>
											<td>
												<table width="550">
													<col style="width:250px">
													<col style="width:50px">
													<col style="width:350px">
													<tr>
														<!-- Components -->
														<td><b><spring:message code="components"/></b></td>
														<td></td>
														<!-- Output variables -->
														<td><b><spring:message code="output_variables"/></b></td>
													</tr>
													<tr>						
														<td>
															<table class="tablestyle">
																<col style="width:60px">
																<col style="width:190px">
																<tr>
																	<!-- Select -->
																	<th><spring:message code="select"/></th>
																	<!-- Component -->
																	<th><spring:message code="component"/></th>
																</tr>
													
																<c:forEach items="${components}" var="component">
																<tr>
																	<c:if test="${selectedcompid == component.componentid}">
																		<tr style="background-color: #D4D4D4"><td><b><spring:message code="selected"/></b></td>
																			<td><b>${component.name}</b></td>
																	</c:if>
																	<c:if test="${selectedcompid != component.componentid}">
																		<tr>
																			<td><a href="<c:url value='outputvariables.html?selectedcompid=${component.componentid}&comppagenum=${comppagenum}'/>">
																					<button type="button"><spring:message code="select"/></button>
																				</a>
																			</td>
																			<td>${component.name}</td>
																	</c:if>
																</tr>
																</c:forEach>
															</table>
														</td>
														<td>
														</td>
														<td valign="top">
															<table class="tablestyle">
																<col style="width:250px">
																<col style="width:100px">
																<col style="width:50px">
																<tr>
																	<!-- Output variable -->
																	<th><spring:message code="output_variable"/></th>
																	<!-- Unit -->
																	<th><spring:message code="unit"/></th>
																	<th><spring:message code="edit"/></th>
																</tr>
											
																<c:forEach items="${outputVariables}" var="outputVar">
																<tr>
																	<td>${outputVar.name}</td>
															    	<td>${outputVar.unit.name}</td>
															    	<td><a href="editoutputvariable.html?outputvarid=${outputVar.outvarid}">
															    		<button type="button"><spring:message code="edit"/></button>
														    			</a>
													    			</td>
																</tr>
																</c:forEach>
															</table>
														</td>
													</tr>
													<tr>
														<td colspan="3">
															<table>
																<tr>
																	<td class="info">
																		<table width="250">
																			<col style="width:33%">	
																			<col style="width:34%">	
																			<col style="width:33%">	
																			<tr>
																				<td align="left">
																					<c:choose>
																						<c:when test="${comppagenum > 1}">
																							<a href="outputvariables.html?selectedcompid=${selectedcompid}&comppagenum=${(comppagenum - 1)}&inputpagenum=${inputpagenum}">
																								< <spring:message code="previous"/>
																							</a>
																						</c:when>
																						<c:otherwise>
																							 &nbsp;
																						</c:otherwise>	
																					</c:choose>
																				</td>
																				<td align="center">
																					<spring:message code="page" /> ${comppagenum}/${comppages}
																				</td>
																				<td align="right">
																					<c:if test="${comppagenum < comppages}">
																						<a href="outputvariables.html?selectedcompid=${selectedcompid}&comppagenum=${(comppagenum + 1)}&inputpagenum=${inputpagenum}">
																							<spring:message code="next"/> >
																						</a>
																					</c:if>
																				</td>
																			</tr>
																		</table>
																	</td>
																	<td></td>
																	<td class="info">
																		<table width="400">
																			<col style="width:33%">	
																			<col style="width:34%">	
																			<col style="width:33%">	
																			<tr>
																				<td align="left">
																					<c:choose>
																						<c:when test="${outputpagenum > 1}">
																							<a href="outputvariables.html?selectedcompid=${selectedcompid}&comppagenum=${(comppagenum)}&outputpagenum=${outputpagenum - 1}">
																								< <spring:message code="previous"/>
																							</a>
																						</c:when>
																						<c:otherwise>
																							 &nbsp;
																						</c:otherwise>	
																					</c:choose>
																				</td>
																				<td align="center">
																					<c:if test="${not empty outputpagenum && not empty outputpages && outputpages > 0}">
																						<spring:message code="page" /> ${outputpagenum}/${outputpages}
																					</c:if>
																				</td>
																				<td align="right">
																					<c:if test="${outputpagenum < outputpages}">
																						<a href="outputvariables.html?selectedcompid=${selectedcompid}&comppagenum=${comppagenum}&outputpagenum=${(outputpagenum + 1)}">
																							<spring:message code="next"/> >
																						</a>
																					</c:if>
																				</td>
																			</tr>
																		</table>
																	</td>
																</tr>
															</table>
														</td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td>		
									<table width="100%">
									
										<tr>
											<td align="right">
												<a href="editproject.html"><button type="button"><spring:message code="back"/></button></a>
										    </td>
										</tr>							      
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