<%--@elvariable id="scengenerator" type="eu.cityopt.DTO.ScenarioGeneratorDTO"--%>
<%--@elvariable id="constraint" type="eu.cityopt.DTO.OptConstraintDTO"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Cityopt create genetic algorithm optimization set</title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
<table cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>
		<td style="width: 30px"></td>
		<td valign="top">
			<form:form method="post" action="geneticalgorithm.html" modelAttribute="scengenerator">
			<table style="width: 950px">
				<col style="width: 400px;">
				<col style="width: 450px;">
				<tr><td colspan="2"><h2>Edit genetic algorithm optimization set</h2></td></tr>
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
								<td align="right"><input type="submit" value="Run GA generation" style="width: 150px"></td>
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
									<table class="tablestyle" style="width: 390px">
										<tr>
											<th>Optimization sense</th>
											<th>Expression</th>
										</tr>
										
										<c:forEach items="${objFuncs}" var="function">
										<tr>
                                            <td>
                                                 <c:choose>
                                                     <c:when test="${function.ismaximise}">Maximize</c:when>
                                                     <c:otherwise>Minimize</c:otherwise>
                                                 </c:choose>
                                            </td>
											<td>${function.expression}</td>
									   	</tr>
										</c:forEach>
									</table>
								</td>
							</tr>
							<tr>
								<td>
									<a href="creategaobjfunction.html"><button type="button" style="width: 100px">Create</button></a>
									<input type="submit" value="Delete"  style="width: 100px">
									<a href="importgaobjfunction.html"><button type="button" style="width: 100px">Import</button></a>
								</td>
							</tr>
							<tr height="10"></tr>
							<tr>
								<td><b>Decision variables</b></td>
							</tr>
							<tr>
								<td>
									<table class="tablestyle" style="width: 390px">
										<tr>
											<th>Expression</th>
											<th>Unit</th>
											<th>Type</th>
										</tr>
										<tr>
											<td>x</td>
											<td>x</td>
											<td>x</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td>
									<input type="submit" value="Add" style="width: 100px">
									<input type="submit" value="Delete" style="width: 100px">
									<input type="submit" value="Import" style="width: 100px">
								</td>
							</tr>
							<tr height="10"></tr>
							<tr>
								<td><b>Constraints</b></td>
							</tr>
							<tr>
								<td>
									<table class="tablestyle" style="width: 390px">
										<tr>
											<th>Name</th>
											<th>Expression</th>
										</tr>
										
										<c:forEach items="${constraints}" var="constraint">
										<tr>
											<td>${constraint.name}</td>
											<td>${constraint.expression}</td>
									   	</tr>
										</c:forEach>
									</table>
								</td>
							</tr>
							<tr>
								<td>
									<input type="submit" value="Add" style="width: 100px">
									<input type="submit" value="Delete" style="width: 100px">
									<input type="submit" value="Import" style="width: 100px">
								</td>
							</tr>
							<tr height="10"></tr>
							<tr>
								<td><b>External parameter value set</b></td>
							</tr>
							<tr>
								<td>
									<table class="tablestyle" style="width: 390px">
										<tr>
											<th>Expression</th>
											<th>Type</th>
											<th>Unit</th>
										</tr>
										<tr>
											<td>x</td>
											<td>x</td>
											<td>x</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td>
									<input type="submit" value="Add" style="width: 100px">
									<input type="submit" value="Delete" style="width: 100px">
								</td>
							</tr>
						</table>
					</td>
					<td valign="top">
						<table style="width: 450px">
							<col style="width: 180px;">
							<col style="width: 270px;">
							<tr>
								<td>Set the model parameters</td>
								<td><a href=""><button type="button" style="width:100px">Set</button></a></td>
							</tr>
							<tr height="10"></tr>
							<tr>
								<td>
									<table class="tablestyle" style="width: 180px; height: 150px">
										<tr>
											<th>Component</th>
										</tr>
										<tr>
											<td>x</td>
										</tr>
										<tr>
											<td>x</td>
										</tr>
										<tr>
											<td>x</td>
										</tr>
										<tr>
											<td>x</td>
										</tr>
									</table>
								</td>
								<td>
									<table class="tablestyle" style="width: 270px; height: 150px">
										<tr>
											<th>Parameter</th>
											<th>Value</th>
											<th>Unit</th>
										</tr>
										<tr>
											<td>x</td>
											<td>x</td>
											<td>x</td>
										</tr>
										<tr>
											<td>x</td>
											<td>x</td>
											<td>x</td>
										</tr>
										<tr>
											<td>x</td>
											<td>x</td>
											<td>x</td>
										</tr>
										<tr>
											<td>x</td>
											<td>x</td>
											<td>x</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr height="10"></tr>
							<tr>
								<td colspan="2"><b>Algorithm parameters</b></td>
							</tr>
							<tr>
								<td colspan="2">
									<table class="tablestyle" style="width: 450px">
										<tr>
											<th>Parameter</th>
											<th>Value</th>
											<th>Default value</th>
											<th>Type</th>
										</tr>
										<tr>
											<td>x</td>
											<td>x</td>
											<td>x</td>
											<td>x</td>
										</tr>
									</table>
								</td>
								<td></td>
							</tr>
							<tr height="2"></tr>
						</table>
						<table>
							<col style="width: 225px;">
							<col style="width: 225px;">
							<tr>
								<td></td>
								<td align="right">
									<a href=""><button type="button"  style="width: 50px">Create</button></a>
									<a href=""><button type="button"  style="width: 150px">Select existing one</button></a>
								</td>
							</tr>
							<tr height="10"></tr>
							<tr>
								<td><input type="submit" value="Visualize pareto diagram"></td>
								<td align="right"><input type="submit" value="Create GA optimization set" style="width: 200px"></td>
							</tr>
							<tr>
								<td><input type="submit" value="Search optimal solution"></td>
								<td align="right"><input type="submit" value="Cancel GA optimization set" style="width: 200px"></td>
							</tr>
							<tr>
								<td></td>
								<td align="right"><input type="submit" value="Clone GA optimization set" style="width: 200px"></td>
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