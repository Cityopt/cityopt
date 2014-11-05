<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt create scenario</title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
<%@ include file="mainmenu.inc"%>
<form>
<table>
	<h2>Create scenario</h2>
	<tr>
		<td>
			<table>
				<tr>
					<td>Project name:</td>
					<td><input type="text" id="projectname"></td>
					<td></td>
					<td><input type="submit" value="Create scenario"></td>
				</tr>
				<tr>						
					<td>User:</td>
					<td><input type="text" id="location"></td>
					<td></td>
					<td><input type="submit" value="Run scenario"></td>
				</tr>
				<tr>						
					<td>Description:</td>
					<td><textarea type="text" rows="3" id="description"></textarea></td>
					<td></td>
					<td><input type="submit" value="Abort run scenario"></td>
				</tr>
				<tr>						
					<td></td>
					<td></td>
					<td></td>
					<td><input type="submit" value="Clone scenario"></td>
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
						<p>Parameters selection</p>
						<input type="radio" >All parameters<br>	
						<input type="radio">Completed parameters<br>	
						<input type="radio">Empty parameters	
					</td>
				</tr>
			</table>
		</td>
	</tr>

</table>
</form>
</body>
</html>