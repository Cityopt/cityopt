<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
 
<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Virtual City Login</title>
</head>
<body>
<form action="../../j_spring_security_check" method="post" >
<table height="600px" align="center">
	<tr height="200px">
	</tr>
	<tr>
		<td>
			<div class="login">
			<table>
				<tr>
					<td align="center">
						<img src="assets/img/icon_logo_big.jpg"/>
					</td>
				</tr>
				<tr>
					<td><input class="login" id="username" name="username" type="text" value="Username" /></td>
				</tr>
				<tr>
					<td><input class="login" id="password" name="password" type="password" value="Password" /></td>
				</tr>

				<tr height="10"></tr>
				<tr>
					<td align="center">
					<a href="start.html">Log in</a>
					<!--<input type="submit" value="Login"/>-->       
					</td>
				</tr>
			</table>
			</div> 
		</td>
	</tr>
</table>	
<div id="login-error">${error}</div>
</form>
</body>
</html>

