<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CityOpt Output variables</title>

<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>
<table cellspacing="0" cellpadding="0">
	<tr>
		<td>
			<%@ include file="mainmenu.inc"%>
		</td>
		<td width="30"></td>
		<td>
			<div style="overflow:scroll;height:600px;width:1100px;overflow:auto">
			<table>
				<col style="width:40px">
				<col style="width:30px">
				<col style="width:850px">	
				<tr>
					<td></td>
					<td colspan="2" height="80">
						<h2>Output variables</h2>
					</td>
					<td></td>
				</tr>
				<tr>
					<td></td>
					<td></td>
					<td>
						<table>
							<tr>
								<td>
									<table width="850">
										<col style="width:150px">
										<col style="width:50px">
										<col style="width:600px">
										<col style="width:50px">
										<tr>						
											<td>
												<table class="tablestyle" height="400">
													<col style="width:150px">
													<tr>
														<th>Components</th>
													</tr>
													
													<c:forEach items="${components}" var="component">
													<tr>
														<td>			
															<a href="<c:url value='outputvariables.html?componentid=${component.componentid}'/>">
																<button align="right" type="button" value="Edit">${component.name}</button>
															</a>
														</td>
												   	</tr>
													</c:forEach>
																
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
													<tr>
														<td>x</td>
													</tr>
													<tr>
														<td>x</td>
													</tr>
												</table>
											</td>
											<td>
											</td>
											<td>
												<table height="400" class="tablestyle">
													<col style="width:120px">
													<col style="width:120px">
													<col style="width:120px">
													<col style="width:120px">
													<tr>
														<th>Project variable</th>
														<th>Unit</th>
														<th>Type</th>
														<th>Select</th>
													</tr>
													<tr>
														<td>x</td>
														<td>x</td>
														<td>x</td>
														<td>x</td>
													</tr>
													<tr>
														<td>x</td>
														<td>x</td>
														<td>x</td>
														<td>x</td>
													</tr>
													<tr>
														<td>x</td>
														<td>x</td>
														<td>x</td>
														<td>x</td>
													</tr>
													<tr>
														<td>x</td>
														<td>x</td>
														<td>x</td>
														<td>x</td>
													</tr>
													<tr>
														<td>x</td>
														<td>x</td>
														<td>x</td>
														<td>x</td>
													</tr>
													<tr>
														<td>x</td>
														<td>x</td>
														<td>x</td>
														<td>x</td>
													</tr>
													<tr>
														<td>x</td>
														<td>x</td>
														<td>x</td>
														<td>x</td>
													</tr>
													<tr>
														<td>x</td>
														<td>x</td>
														<td>x</td>
														<td>x</td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td>		
									<table width="100%">
									
										<tr height="30">
											<td></td>
										</tr>
										<tr>
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
		</td>
	</tr>
</table>
</div>
</body>
</html>