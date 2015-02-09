<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%--@elvariable id="component" type="com.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="selectedComponent" type="com.cityopt.DTO.ComponentDTO"--%>
<%--@elvariable id="selectedcompid" type="int"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt view chart</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0" cellpadding="0">
	<tr>
		<td width="100" valign="top">
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
						<h2>View chart</h2>
					</td>
				</tr>
				<tr>
					<td>
						<table>
							<tr>
								<td>
									<table width="850">
										<col style="width:300px">
										<col style="width:50px">
										<col style="width:400px">
										<tr>
											<td>
												<b>Components</b>
											</td>
											<td></td>
											<td>
												<b>Chart</b>
											</td>
										</tr>
										<tr>						
											<td valign="top">
												<table class="tablestyle">
													<col style="width:50px">
													<col style="width:200px">
													<col style="width:50px">
													<tr>
														<th>Show</th>
														<th>Component</th>
														<th>Id</th>
													</tr>
													<c:forEach items="${components}" var="component">
													<c:if test="${selectedcompid == component.componentid}">
														<tr style="background-color: rgb(140, 200, 200)">
													</c:if>
													<c:if test="${selectedcompid != component.componentid}">
														<tr>
													</c:if>
														<td><p>Show</p></td>
														<td>${component.name}</td>
												    	<td>${component.componentid}</td>
												   	</tr>
													</c:forEach>
												</table>
											</td>
											<td></td>
											<td valign="top">
												<img src="assets/img/graph_total_energy.png">
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr height="50">
								<td>
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