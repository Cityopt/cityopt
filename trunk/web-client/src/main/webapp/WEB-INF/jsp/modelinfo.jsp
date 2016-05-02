<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>${title}</title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>

<body>	
	<table class="infopage">
		<tr>
			<td class="spacecolumn"></td>
			<td><h1>${title}</h1></td>
		</tr>
		<tr>
			<td class="spacecolumn"></td>
			<td><p>${infotext}</p></td>
		</tr>
	</table>
</body>
</html>