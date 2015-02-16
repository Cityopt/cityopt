<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="component" type="com.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="selectedComponent" type="com.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="inputParam" type="com.cityopt.DTO.InputParameterDTO"--%>
<%--@elvariable id="extParam" type="com.cityopt.DTO.ExtParamDTO"--%>
<%--@elvariable id="selectedcompid" type="int"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt Project parameters</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>
		<td width="30"></td>
		<td>
			<div style="overflow:scroll;height:600px;width:900px;overflow:auto">
			<table>
				<col style="width:30px">
				<col style="width:750px">	
				<tr>
					<td colspan="2" height="80">
						<h2>Project parameters</h2>
					</td>
				</tr>
				<tr>
					<td>
					</td>
					<td>
						<table>
							<tr>
								<td>
									<table width="850">
										<col style="width:200px">
										<col style="width:50px">
										<col style="width:500px">
										<tr>
											<td>
												<b>Components</b>
											</td>
											<td></td>
											<td>
												<b>Input parameters</b>
											</td>
										</tr>
										<tr>						
											<td valign="top">
												<table class="tablestyle">
													<col style="width:80px">
													<col style="width:180px">
													<col style="width:80px">
													<col style="width:50px">
													<tr>
														<th>Select</th>
														<th>Components</th>
														<th>Id</th>
														<th>Edit</th>
													</tr>
													
													<c:forEach items="${components}" var="component">
													<c:if test="${selectedcompid == component.componentid}">
														<tr style="background-color: rgb(140, 200, 200)">
													</c:if>
													<c:if test="${selectedcompid != component.componentid}">
														<tr>
													</c:if>
														<td><a href="<c:url value='projectparameters.html?selectedcompid=${component.componentid}'/>">Select</a></td>
														<td>${component.name}</td>
												    	<td>${component.componentid}</td>
														<td>
															<a href="<c:url value='editcomponent.html?componentid=${component.componentid}'/>">
																<button align="right" type="button" value="Edit">Edit</button>
															</a>
														</td>
												   	</tr>
													</c:forEach>
												</table>
											</td>
											<td></td>
											<td valign="top">
												<table class="tablestyle">
													<col style="width:150px">
													<col style="width:60px">
													<col style="width:150px">
													<col style="width:60px">
													<tr>
														<th>Input parameter</th>
														<th>Id</th>
														<th>Default value</th>
														<th>Edit</th>
													</tr>
													
													<c:forEach items="${inputParameters}" var="inputParam">
													<tr>
														<td>${inputParam.name}</td>
												    	<td>${inputParam.inputid}</td>
												    	<td>${inputParam.defaultvalue}</td>
														<td>
															<a href="<c:url value='editinputparameter.html?inputparameterid=${inputParam.inputid}'/>">
																<button align="right" type="button" value="Edit">Edit</button>
															</a>
														</td>
												   	</tr>
													</c:forEach>
													
												</table>
											</td>
										</tr>
										<tr>
											<td align="right">
												<a href="createcomponent.html"><button type="button">Create component</button></a>
											</td>
											<td></td>
											<td align="right">
												<c:if test="${selectedcompid != null}">
													<a href="createinputparameter.html?selectedcompid=${selectedcompid}"><button type="button">Create input parameter</button></a>
												</c:if>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr height="50">
								<td>
								</td>
							</tr>
							<tr>
								<td>
									<b>External parameters</b>
								</td>
							</tr>
							<tr>
								<td>
									<table width="100%">
										<tr>
											<td>
												<table class="tablestyle" width="750">
													<col style="width:200px">
													<col style="width:50px">
													<col style="width:100px">
													<col style="width:50px">
													<tr height="20">
													    <th>Name</th>
													    <th>Id</th>
													    <th>Default value</th>
													    <th>Edit</th>
													</tr>
													
													<c:forEach items="${extParams}" var="extParam">
													<tr>
														<td>${extParam.name}</td>
												    	<td>${extParam.extparamid}</td>
												    	<td>${extParam.defaultvalue}</td>
												    	<td>
															<a href="<c:url value='editextparam.html?extparamid=${extParam.extparamid}'/>">
																<button align="right" type="button" value="Edit">Edit</button>
															</a>
														</td>
												   	</tr>
													</c:forEach>
												</table>
											</td>
										</tr>
										<tr>
											<td width="400" align="right">
												<a href="createextparam.html"><button type="button">Create external parameter</button></a>
											</td>
											<td align="right">
												<a href="editproject.html"><button type="button">Close</button></a>
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