<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title><spring:message code="simulation_info" /></title>
	<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>	
	<table class="maintablehelppagebig">
		<tr class="spacerowbig"></tr>
		<tr>
			<td class="spacecolumnsmall"></td>
			<td>
				<table width="100%">
					<tr class="titlerow">
						<td colspan="1">
							<table width="100%">
								<tr>
									<td class="spacecolumn"></td>
									<td><font class="activeproject">${title}</font></td>
									<td align="left" width="40">
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
			<td class="spacecolumnsmall"></td>
		</tr>
		<tr>
			<td class="spacecolumnsmall"></td>
			<td>
				<table>
					<tr>
						<td class="spacecolumn"></td>
						<td><pre><c:out value="${infotext}"/></pre></td>
						<td class="spacecolumn"></td>
					</tr>
				</table>
			</td>
			<td class="spacecolumnsmall"></td>
		</tr>
	</table>
</body>
</html>
