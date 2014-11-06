<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt Database optimization</title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
<%@ include file="mainmenu.inc"%>
<form>
<table border="1">
	<col style="width: 400px;">
	<col style="width: 450px;">
	<tr><td colspan="2"><h2>Create - database optimization set</h2></td></tr>
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
					<td><input type="text" id="name" style="width:200px"></td>
					<td>Description:</td>
					<td rowspan="2"><textarea id="description" rows="2" style="width: 300px"></textarea></td>
					<td align="right"><input type="submit" value="Abort search" style="width: 100px"></td>
				</tr>
				<tr>						
					<td>User:</td>
					<td><input type="text" id="user" style="width:200px"></td>
					<td></td>
					<td align="right"><input type="submit" value="Run search" style="width: 100px"></td>
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
					<td><input type="text" id="function"></td>
				</tr>
				<tr>
					<td>Optimization sense: <input type="radio">Maximize <input type="radio">Minimize</td>
				</tr>
				<tr>
					<td>
						<input type="submit" value="Create">
						<input type="submit" value="Delete">
						<input type="submit" value="Import">
					</td>
				</tr>
				<tr height="20"></tr>
				<tr>
					<td><b>Searching constraint</b></td>
				</tr>
				<tr>
					<td>
						<table class="tablestyle" style="width: 390px">
							<tr>
								<th>Name</th>
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
						<input type="submit" value="Add">
						<input type="submit" value="Delete">
						<input type="submit" value="Import">
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
					<td align="right"><input type="submit" value="Show scenario"></td>
				</tr>
				<tr>
					<td>Objective function value</td>
					<td><input type="text" style="width:190px"></td>
					<td></td>
				</tr>
				<tr><td><br></td></tr>
				<tr>
					<td colspan="3">
						<table class="tablestyle" style="width: 100%">
							<tr>
								<th>Metrics</th>
								<th>Value</th>
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
</form>
</body>
</html>