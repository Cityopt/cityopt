<%--@elvariable id="optimizationset" type="eu.cityopt.DTO.OptimizationSetDTO"--%>
<%--@elvariable id="constraint" type="eu.cityopt.DTO.OptConstraintDTO"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt edit database optimization set</title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
<table cellspacing="0" cellpadding="0">
	<tr>	
		<td><%@ include file="mainmenu.inc"%></td>
		<td width="30"></td>
		<td valign="top">
			<form:form method="post" action="editoptimizationset.html" modelAttribute="optimizationset">
			<table>
				<col style="width: 400px;">
				<col style="width: 450px;">
				<tr><td colspan="2"><h2>Edit database optimization set</h2></td></tr>
				<tr>
					<td colspan="2">
						<table>
							<col style="width: 80px;">
							<col style="width: 200px;">
							<col style="width: 80px;">
							<col style="width: 300px;">
							<col style="width: 175px;">
							<tr>
								<td>Name:</td>
								<td><form:input type="text" path="name" style="width:200px"/></td>
								<td>Description:</td>
								<td rowspan="2"><textarea id="description" rows="2" style="width: 300px"></textarea></td>
								<td align="right"><a href="databaseoptimization.html"><button type="button">Run search</button></a></td>
							</tr>
							<tr>						
								<td>User:</td>
								<td><input type="text" id="user" style="width:200px"></td>
								<td></td>
								<td align="right"></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>						
					<td>
						<table>
							<tr>
								<td><b>Objective function</b></td>
							</tr>
							<tr>
								<td>
									<table width="100%">
										<col style="width: 20%;">
										<col style="width: 80%;">
										<tr>
											<c:choose>
												<c:when test="${optimizationset.objectivefunction.ismaximise}">
													<td>Maximize</td>
												</c:when>
												<c:otherwise>
													<td>Minimize</td>
												</c:otherwise>
											</c:choose>
											<td>
												<table class="tablestyle" width="100%">
													<tr>
														<td>
															${optimizationset.objectivefunction.expression}
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
									<a href="createobjfunction.html"><button type="button">Create</button></a>
									<input type="submit" value="Delete">
									<a href="importobjfunction.html"><button type="button">Import</button></a>
								</td>
							</tr>
							<tr height="20"></tr>
							<tr>
								<td><b>Searching constraints</b></td>
							</tr>
							<tr>
								<td>
									<table class="tablestyle" style="width: 390px">
										<tr>
											<th>Name</th>
											<th>Expression</th>
											<th>Lower bound</th>
											<th>Upper bound</th>
										</tr>
										
										<c:forEach items="${constraints}" var="constraint">
										<tr>
											<td>${constraint.name}</td>
											<td>${constraint.expression}</td>
											<td>${constraint.lowerbound}</td>
											<td>${constraint.upperbound}</td>
									   	</tr>
										</c:forEach>
						
									</table>
								</td>
							</tr>
							<tr>
								<td>
									<a href="createconstraint.html"><button type="button">Create</button></a>
									<input type="submit" value="Delete">
									<a href="importsearchconstraint.html"><button type="button">Import</button></a>
								</td>
							</tr>
							<tr height="20"></tr>
							<tr>
								<td><b>External parameter value set</b></td>
							</tr>
							<tr>
								<td>
									<input type="text" style="width: 350px">
								</td>
							</tr>
							<tr>
								<td>
									<a href="extparamsets.html"><button type="button">Add</button></a>
									<input type="submit" value="Delete">
								</td>
							</tr>
						</table>
					</td>
					<td>
						<table style="width: 440px">
							<col style="width: 120px;">
							<col style="width: 180px;">
							<col style="width: 140px;">
							<tr>
								<td><b>Results</b></td>
								<td></td>
								<td></td>
							</tr>
							<tr>
								<td>Scenario name</td>
								<td><input type="text" style="width:190px"></td>
								<td align="right"><a href="showresults.html"><button type="button">Show results</button></a></td>
							</tr>
							<tr>
								<td>Objective function value</td>
								<td><input type="text" style="width:190px"></td>
								<td></td>
							</tr>
							<tr><td><br></td></tr>
							<tr><td><b>Metrics</b></td></tr>
							<tr>
								<td colspan="3">
									<table class="tablestyle">
										<col style="width:400px">
										
										<tr>
											<th>Metric</th>
											<th>Value</th>
										</tr>
					
										<c:forEach items="${metrics}" var="metric">
										<tr>
											<td>${metric.name}</td>
											<td></td>
									   	</tr>
										</c:forEach>
									</table>	
								</td>
							</tr>
							<tr><td><br></td></tr>
						</table>
						<table>
							<col style="width: 220px;">
							<col style="width: 220px;">
							<tr>
								<td><input type="submit" value="Clone database optimization set"></td>
								<td align="right"><input type="submit" value="Create database optimization" style="width: 200px"></td>
							</tr>
							<tr>
								<td></td>
								<td align="right"><input type="submit" value="Cancel" style="width: 200px"></td>
							</tr>
						</table>
					</td>
				</tr>
			
			</table>
			</form:form>
		</td>
	</tr>
</table>
</body>
</html>