<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt import data</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0px" cellpadding="0px">
	<tr>
		<td valign="top">
			<%@ include file="mainmenu.inc"%>
		</td>

		<td width=30></td>
		<td valign="top">
			<div style="overflow:scroll;height:800px;width:1100px;overflow:auto">
			<form:form method="post" action="editscenario.html?action=update" modelAttribute="project">
			<table>
				<col style="width:400px">	
				<col style="width:400px">	
			
				<tr>
					<td>
						<h2>Import data</h2>
					</td>
					<td align="right">
						<p>Download project templates<a href=""><button>Download</button></a></p>
						<p>Download scenario templates<a href=""><button>Download</button></a></p>
					</td>
				</tr>
				<tr>
					<td>
						<table>
							<tr>
								<td>Project name:</td>
								<td><form:input type="text" path="name" style="width:200px"/></td>
							</tr>
							<tr>						
								<td>Location:</td>
								<td><form:input type="text" path="location" style="width:200px"/></td>
							</tr>
							<tr>						
								<td>Design target:</td>
								<td><form:input type="text" path="designtarget" style="width:200px"/></td>
							</tr>
							<tr>						
								<td>Description:</td>
								<td><form:textarea type="text" rows="3" path="description" style="width:200px"></form:textarea></td>
							</tr>
							<tr>						
								<td>Energy model:</td>
								<td><input id="uploadFile" name="uploadFile" type="file"/></td>
							</tr>
							<tr>
								<td>
									Parameter level:
								</td>
								<td>
							 		<select name="parameterLevel">
									  	<option value="1">1</option>
									  	<option value="2">2</option>
									  	<option value="3">3</option>
									  	<option value="4">4</option>
									</select>
								</td> 
							</tr>
							<tr>						
								<td></td>
								<td><a href="uploaddiagram.html"><button type="button">Upload</button></a>
								<!--<form:input type="submit" path="" value="Upload" style="width:120px"/>--></td>
							</tr>
							<tr>
								<td>
									<br>
									<b>Import project data</b>
								</td>
							</tr>
							<tr>						
								<td>Components</td>
								<td><input id="uploadFile" name="uploadFile" type="file"/></td>
							</tr>
							<tr>						
								<td>Input parameters</td>
								<td><input id="uploadFile" name="uploadFile" type="file"/></td>
							</tr>
							<tr>						
								<td>Output variables</td>
								<td><input id="uploadFile" name="uploadFile" type="file"/></td>
							</tr>
							<tr>						
								<td>External parameter sets</td>
								<td><input id="uploadFile" name="uploadFile" type="file"/></td>
							</tr>
							<tr>		
								<br/>				
								<td><a href=""><button>Visualize project data</button></a></td>
								<td align="right"><a href=""><button>Import</button></a></td>
							</tr>
						</table>
					</td>
					<td valign="top">
						<table>
							<tr>
								<td>
									<img src="assets/img/test_map.jpg"/>
								</td>
							</tr>
							<tr align="right">
								<td><a href="uploaddiagram.html"><button type="button">Upload diagram</button></a></td>
							</tr>
							<tr>
								<td>					
									<b>Import scenarios</b>
								</td>
							</tr>
							<tr>						
								<td>Import scenarios <a href=""><button>Import</button></a></td>
							</tr>
							<tr>						
								<td><a href=""><button>Check scenario</button></a></td>
							</tr>
							<tr align="right">
								<td>
									<a href=""><button>Accept</button></a>
									<a href=""><button>Cancel</button></a>
								</td>
							</tr>
						</table>
					</td>
					
				</tr>
			</table>
			</form:form>
			</div>
		</td>
		
     </tr>
</table>
</body>
</html>