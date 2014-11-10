<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt Project parameters</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<%@ include file="mainmenu.inc"%>

<%
	//ArrayList<Book> b = new ArrayList<Book>();
	//b = SqlSentencesList.showCatalog(); // this method returns an arrayList with all books
%>

<div style="overflow:scroll;height:600px;width:1100px;overflow:auto">
<form method="get" action="ProjectController">
<table>
	<col style="width:40px">
	<col style="width:1000px">	
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
						<b>Components parameters</b>
					</td>
				</tr>
				<tr>
					<td>
						<table width="1000">
							<col style="width:150px">
							<col style="width:600px">
							<col style="width:250px">
							<tr>						
								<td>
									<table class="tablestyle">
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
									<table class="tablestyle">
										<col style="width:120px">
										<col style="width:120px">
										<col style="width:120px">
										<col style="width:120px">
										<col style="width:120px">
										<tr>
											<th>Input parameter</th>
											<th>Default value</th>
											<th>Units</th>
											<th>Date</th>
											<th>Reliability</th>
										</tr>
										<tr>
											<td>x</td>
											<td>x</td>
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
									<input type="radio">Empty parameters<br><br>
									<input type="button" value="Upload default values">	
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
						<table class="tablestyle" width="800">
						
							<tr height="20">
							    <th>Variable</th>
							    <th>Units</th>
							    <th>Upload default profile</th>
							    <th>Date</th>
							    <th>Reliability</th>
							    <th>Visualize</th>
							    <th>Select</th>
							</tr>
							
							<tr height="20">
							    <td>variable 1</td>
							    <td>x</td>
							    <td>x</td>
							    <td>x</td>
							    <td>x</td>
							    <td>x</td>
							    <td> <input type="radio"/> </td>
							</tr>
							
							<tr height="20">
							    <td>variable 2</td>
							    <td>x</td>
							    <td>x</td>
							    <td>x</td>
							    <td>x</td>
							    <td>x</td>
							    <td> <input type="radio"/> </td>
							</tr>
							
							<% //for(int i=0; i<l.size();i++) {%>
							<!--    <tr>
							        <td> <%//out.print(b.get(i).getIsbn());%> </td>
							        <td> <%//out.print(b.get(i).getTitle());%> </td>
							        <td> <%//out.print(b.get(i).getAuthor());%> </td>
							        <td> <%//out.print(b.get(i).getPrice());%> </td>
							        <td> <!-- <input type="radio" name="project" value="<%//Integer.toString(i);%>"/>--> </td>
							    <!-- </tr>-->
							<% //} %>
						</table>
						
						<table width="100%">
						
							<tr height="30">
								<td></td>
							</tr>
							<tr>
								<td align="right">
									<input align="right" type="submit" value="Accept"/>
									<input align="right" type="submit" value="Cancel"/>
							    </td>
							</tr>
							      
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>	
</table>
</form>
</div>
</body>
</html>