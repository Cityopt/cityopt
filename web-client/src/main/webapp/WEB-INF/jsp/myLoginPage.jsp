<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form"%>

<div id="loginPannel">
	<security:authorize access="!isAuthenticated()">
		<h1>This is my custom login page</h1>
		
		<c:if test="${loginFailed}">
			<div style="color: red">Could not sign in, please check your login/password...</div>
		</c:if>		
		<c:url value="/login" var="loginURL"/>
		<form method="post" class="signin" action="${loginURL}" />
			<table>
				<tr>
					<th><label for="username_or_email">Username/email</label></th>
					<td><input id="username_or_email" name="username"	type="text" /></td>
				</tr>
				<tr>
					<th><label for="password">Password</label></th>
					<td><input id="password" name="password" type="password" /></td>
				</tr>
				<tr>
					<th></th>
					<td><input name="commit" type="submit" value="Sign In" /></td>
				</tr>
			</table>	
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>		
		</form>
	</security:authorize>
</div>
