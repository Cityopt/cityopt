<%--@elvariable id="scenario" type="eu.cityopt.model.Scenario"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt create scenario</title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
<form:form method="post" action="editscenario.html?action=create" modelAttribute="scenario">
<table cellspacing="0" cellpadding="0">
	<tr>
		<td>
			<%@ include file="mainmenu.inc"%>
		</td>
		<td width="30"></td>
		<td valign="top">
			<table>
				<tr>
					<td>
						<h2>Create scenario</h2>
					</td>
				</tr>
				<tr>
					<td>
						<table>
							<tr>
								<td>Project name:</td>
								<td><form:input type="text" path="name"/></td>
								<td></td>
								<td><input type="submit" value="Create scenario" style="width:120px"></td>
							</tr>
							<tr>						
								<td></td>
								<td></td>
								<td></td>
								<td><input type="submit" value="Run scenario" style="width:120px"></td>
							</tr>
							<tr>						
								<td>Description:</td>
								<td><form:textarea type="text" rows="3" path="description"></form:textarea></td>
								<td></td>
								<td><input type="submit" value="Abort run scenario" style="width:120px"></td>
							</tr>
							<tr>						
								<td></td>
								<td></td>
								<td></td>
								<td><input type="submit" value="Clone scenario" style="width:120px"></td>
							</tr>
							<tr>						
								<td>External parameters:</td>
								<td><input type="submit" value="Ok"></td>
							</tr>
							<tr>						
								<td>Set the input parameters:</td>
								<td><input type="submit" value="Ok"></td>
							</tr>
							<tr>						
								<td>
									<table class="tablestyle" border="1">
										<col style="width:150px">
										<tr>
											<th>Components</th>
										</tr>
										<tr>
											<td>x</td>
										</tr>
									</table>
								</td>
								<td>
									<table class="tablestyle" border="1">
										<col style="width:150px">
										<col style="width:150px">
										<col style="width:150px">
										<tr>
											<th>Parameters</th>
											<th>Value</th>
											<th>Units</th>
										</tr>
										<tr>
											<td>x</td>
											<td>x</td>
											<td>x</td>
										</tr>
									</table>
								</td>
								<td>
									<b>Parameters selection</b><br>
									<input type="radio" >All parameters<br>	
									<input type="radio">Completed parameters<br>	
									<input type="radio">Empty parameters	
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</form:form>
</body>
</html>