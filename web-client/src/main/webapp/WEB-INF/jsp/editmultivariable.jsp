<%--@elvariable id="multivariable" type="eu.cityopt.DTO.ModelParameterDTO"--%>
<%--@elvariable id="multiscenarioid" type="java.lang.String"--%>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script>
// select all desired input fields and attach tooltips to them
$("#myform :input").tooltip({	 
    // place tooltip on the right edge
    position: "right",
    // a little tweaking of the position
    offset: [-2, 10],
    // use the built-in fadeIn/fadeOut effect
    effect: "fade",
    // custom opacity setting
    opacity: 0.7   
    });
</script>

<title>CityOpt create multi variable</title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
<%@ include file="mainmenu.inc"%>

<form:form method="post" action="editmultivariable.html" modelAttribute="multivariable">
<table>
	<tr>
		<td width=20></td>
		<td>
			<table class="ProjectCreationForm" style="width:900px" >
				<!-- create project -->
				<tr><td><h2>Edit multi variable</h2></td></tr>	
				<tr height="20"></tr>
        		<tr valign="top">
					<td valign="top">
						<table>
							<tr>
								<td>
									Input parameter
								</td>
								<td>
									${multivariable.inputparameter.name}
								</td>
							</tr>
							<tr height="10">
							</tr>
							<tr>
								<td>Expression:</td>
								<c:set var="tooltip_name">Values</c:set>
								<td><form:input type="text" path="expression" title="${tooltip_expression}" style="width: 400px"/></td>
								<td><form:errors path="expression" cssClass="error"/></td>		
								<td align="right">
									<input type="submit" value="<spring:message code="update"/>" style="width:100px">
									<a href="editscenario.html"><button type="button" style="width:100px">Cancel</button></a>
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