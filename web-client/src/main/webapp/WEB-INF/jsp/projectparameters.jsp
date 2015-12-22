<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="component" type="com.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="selectedComponent" type="com.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="inputParam" type="com.cityopt.DTO.InputParameterDTO"--%>
<%--@elvariable id="extParam" type="com.cityopt.DTO.ExtParamDTO"--%>
<%--@elvariable id="extParamVal" type="com.cityopt.DTO.ExtParamValDTO"--%>
<%--@elvariable id="extParamValSet" type="com.cityopt.DTO.ExtParamValSetDTO"--%>
<%--@elvariable id="selectedcompid" type="int"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt Project parameters</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />

<script>
    function openInfoWindow() {
    	   window.open("createproject_info.html",'Info: Create Project','width=600,height=800');
    }
</script>

<style type="text/css">

table.external_parameter_sets{
margin-top: 20%;
padding-top: 20%;
}

table.extern{
margin-top: 5%;
padding-top: 5%;
padding-right: 5%;
}

table.close_button{
right: 0px;
width:100%;
}

.big{
float:right;
margin-right: 2%;
margin-top: 5%;
margin-bottom: 10%;
}	

</style>

</head>
<body>
	<table cellspacing="0" cellpadding="0">
		<tr>
			<td valign="top">
				<%@ include file="mainmenu.inc"%>
			</td>
			<td width="5%"></td>
			<td valign="top">
				<div style="overflow: auto; height: 100%; width: 100%; overflow: auto;">
					<table>
						<col style="width: 5%">
						<col style="width: 90%">
						<col style="width: 5%">
						<tr class="project_parameters">
							<td colspan="2" height="80">
								<h2>
									<spring:message code="project_parameters" />
								</h2>
							</td>
							<td>
								<div class="round-button">
									<div class="round-button-circle" onclick="openInfoWindow()">
										<a>?</a>
									</div> 
								</div>
							</td>
						</tr>
						<tr class="content_tables">
							<td></td>
							<td class="external_parameters">
								<table class="tablegroup">
									<tr>
										<td>
											<table width="100%" class="input_parameter_table">
												<col style="width: 200px">
												<col style="width: 50px">
												<col style="width: 400px">
												<tr>
													<td>
														<!-- Components --> <b><spring:message
																code="components" /></b>
													</td>
													<td></td>
													<td>
														<!-- Input parameters --> <b><spring:message
																code="input_parameters" /></b>
													</td>
												</tr>
												<tr>
													<td valign="top">
														<table class="tablestyle">
															<col style="width: 80px">
															<col style="width: 180px">
															<col style="width: 50px">
															<tr>
																<!-- Select -->
																<th><spring:message code="select" /></th>
																<!-- Component -->
																<th><spring:message code="components" /></th>
																<!-- Edit -->
																<th><spring:message code="edit" /></th>
															</tr>
															<!-- Tooltips -->
															<c:set var="tooltip_select">
																<spring:message code="tooltip_select" />
															</c:set>
															<c:set var="tooltip_selected">
																<spring:message code="tooltip_selected" />
															</c:set>
															<c:set var="tooltip_projectparameters">
																<spring:message code="tooltip_projectparameters" />
															</c:set>

															<c:forEach items="${components}" var="component">
																<c:choose>
																	<c:when
																		test="${selectedcompid == component.componentid}">
																		<tr style="background-color: #D4D4D4"
																			title="${tooltip_selected}">
																			<td><spring:message code="selected" /></td>
																	</c:when>
																	<c:otherwise>
																		<tr>
																			<td><a href="<c:url value='projectparameters.html?selectedcompid=${component.componentid}'/>"
																				title="${tooltip_select}"> <spring:message
																						code="select" /></a></td>
																	</c:otherwise>
																</c:choose>
																<td>${component.name}</td>
																<td><a href="<c:url value='editcomponent.html?componentid=${component.componentid}'/>">
																		<button align="right"
																			title="${tooltip_projectparameters}" type="button"
																			value="Edit">
																			<spring:message code="edit" />
																		</button>
																</a></td>
																</tr>
															</c:forEach>
														</table>
													</td>
													<td></td>
													<td valign="top">
														<table class="tablestyle">
															<col style="width: 150px">
															<col style="width: 150px">
															<col style="width: 60px">
															<col style="width: 60px">
															<tr>
																<!-- Input parameter -->
																<th><spring:message code="input_parameter" /></th>
																<!-- Default value -->
																<th><spring:message code="default_value" /></th>
																<!-- Units -->
																<th><spring:message code="units" /></th>
																<!-- Edit -->
																<th><spring:message code="edit" /></th>
																<!-- Delete -->
																<th><spring:message code="remove" /></th>
															</tr>

															<c:forEach items="${inputParameters}" var="inputParam">
																<tr>
																	<td>${inputParam.name}</td>
																	<td>${inputParam.defaultvalue}</td>
																	<td>${inputParam.unit.name}</td>
																	<td><a href="<c:url value='editinputparameter.html?inputparamid=${inputParam.inputid}'/>">
																			<button align="right" type="button" value="Edit">
																				<spring:message code="edit" />
																			</button>
																	</a></td>
																	<td><a  onclick="return confirm('Are you sure you want to delete')" href="<c:url value='deleteinputparameter.html?inputparamid=${inputParam.inputid}'/>">
																			<button align="right" type="button" value="Remove">
																				<spring:message code="remove" />
																			</button>
																	</a></td>
																</tr>
															</c:forEach>

														</table>
													</td>
												</tr>
												<tr>
													<c:set var="tooltip_createcomponent">
														<spring:message code="tooltip_create_component" />
													</c:set>
													<td align="right"><a href="createcomponent.html"><button
																title="${tooltip_createcomponent}" type="button">
																<spring:message code="create_component" />
															</button></a></td>
													<td></td>
													<td align="right"><c:if
															test="${selectedcompid != null}">
															<a
																href="createinputparameter.html?selectedcompid=${selectedcompid}"><button
																	type="button">
																	<spring:message code="create_input_parameter" />
																</button></a>
														</c:if>
													</td>
												</tr>
											</table>
										</td>
									</tr>
								</table>
								<table>
									<tr>
										<td>			
											<table width="800" class="extern">										
												<tr class="external_parameter_sets">
													<td><b><spring:message code="selected_external_parameter_set" />:</b>
														${extParamValSet.name} <br>
														<table class="tablestyle">
		
													</table></td>
												</tr>
												<tr class="external_parameter_tables">
													<td>
														<table width="750" class="external_parameter_tables">
															<tr>
																<td>
																	<table class="tablestyle" width="750">
																		<col style="width: 200px">
																		<col style="width: 250px">
																		<col style="width: 100px">
																		<col style="width: 100px">
																		<col style="width: 50px">
																		<col style="width: 50px">
																		<col style="width: 50px">
																		<tr height="20">
																			<!-- Name -->
																			<th><spring:message code="name" /></th>
																			<!-- Comment -->
																			<th><spring:message code="comment" /></th>
																			<!-- Type -->
																			<th><spring:message code="type" /></th>
																			<!-- Value -->
																			<th><spring:message code="value" /></th>
																			<!-- Unit -->
																			<th><spring:message code="unit" /></th>
																			<!-- Edit -->
																			<th><spring:message code="edit" /></th>
																			<th>Export</th>
																		</tr>
		
																		<c:forEach items="${extParamVals}" var="extParamVal">
																			<tr>
																				<td>${extParamVal.extparam.name}</td>
																				<td>${extParamVal.comment}</td>
																				<td>${extParamVal.extparam.getType().getName()}</td>
																				<td>${extParamVal.value}</td>
																				<td>${extParamVal.extparam.unit.name}</td>
																				<td>
																					<a href="<c:url value='editextparamvalue.html?extparamvalid=${extParamVal.extparamvalid}'/>">
																						<button align="right" type="button" value="Edit">
																							<spring:message code="edit" />
																						</button>
																					</a>
																				</td>
																				<td>
																					<c:choose>
																						<c:when test="${extParamVal.extparam.getType().getTypeid() >= 5}">
																							<a href="<c:url value='exportextparam.html?extparamvalid=${extParamVal.extparamvalid}&extparamvalsetid=${extParamValSet.extparamvalsetid}'/>">
																								<button align="right" type="button" value="Export">
																									Export
																								</button>
																							</a>
																						</c:when>
																						<c:otherwise>
																						
																						</c:otherwise>
																					</c:choose>
																				</td>
																			</tr>
																		</c:forEach>
																	</table>
																</td>
															</tr>
															<tr>
																<td align="right">
																	<a href="selectextparamset.html">
																		<button	type="button">
																			<spring:message code="select_external_parameter_set" />
																		</button>
																	</a>
																</td>
															</tr>
															<tr height="20"></tr>
															<tr>
																<td><b>Project external parameters</b></td>
															</tr>
															<tr>
																<td>
																	<table class="tablestyle" width="750">
																		<col style="width: 500px">
																		<col style="width: 200px">
																		<col style="width: 50px">
																		<col style="width: 50px">
																		<tr height="20">
																			<!-- Name -->
																			<th><spring:message code="name" /></th>
																			<!-- Type -->
																			<th><spring:message code="type" /></th>
																			<!-- Unit -->
																			<th><spring:message code="unit" /></th>
																			<!-- Remove -->
																			<th><spring:message code="remove" /></th>
																		</tr>
		
																		<c:forEach items="${extParams}" var="extParam">
																			<tr>
																				<td>${extParam.name}</td>
																				<td>${extParam.type.name}</td>
																				<td>${extParam.unit.name}</td>
		
																				<td><a onclick="return confirm('Are you sure you want to delete')"
																						href="<c:url value='deleteextparam.html?extparamid=${extParam.extparamid}'/>">
																						<button align="right" type="button" value="Remove">
																					 
																							<spring:message code="remove" />
																						</button>
																				</a></td>
																			</tr>
																		</c:forEach>
																	</table>
																</td>
															</tr>
															<tr>
																<td width="750" align="right">
																	<a href="createextparam.html"><button type="button"	style="width: 150px">
																			<spring:message code="create_external_parameter" />
																		</button>
																	</a>
																</td>
															</tr>
															<tr height="20"></tr>
															<tr>
																<td>
																	<table style="width: 100%">
																		<tr>
																			<td>
																				<!-- Import external parameter -->					
																				<b>Import external parameter</b>
																			</td>
																			<td></td>
																			<td></td>
																		</tr>
																		<form:form method="POST" action="importextparam.html" enctype="multipart/form-data">
												        					<tr>
												        						<td>Import external time series file (CSV)</td>
																				<td><input id="file" name="file" type="file"/></td>
																				<td align="right"><input type="submit" value="Import file"></td>	
																			</tr>
											    						</form:form>
											    						<tr height="20"></tr>
											    						<tr>
											    							<td></td>
											    							<td></td>
											    							<td>
											    								<table class="close_button">
																					<tr>														
																						<td align="right">
																							<a href='editproject.html?prjid=${project.prjid}'>
																								<button class="big" type="button">
																									<spring:message code="close" />
																								</button>
																							</a>
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