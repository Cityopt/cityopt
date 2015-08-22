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

<form:form method="post" action="createmultivariable.html" modelAttribute="multivariable">
<table>
	<tr>
		<td width=20></td>
		<td>
			<table class="ProjectCreationForm" style="width:900px" >
				<!-- create project -->
				<tr><td><h2>Create project</h2></td></tr>	
				<tr valign="top">
					<td valign="top">
						<table>
							<tr>
								<td>Name:</td>
								<c:set var="tooltip_name">Multi variable name</c:set>
								<td><form:input type="text" path="name" title="${tooltip_name}"/></td>
								<td><form:errors path="name" cssClass="error"/></td>															
							</tr>
							<tr height=10px></tr>		
							<tr>
								<td>Name:</td>
								<c:set var="tooltip_name">Values</c:set>
								<td><form:input type="text" path="name" title="${tooltip_name}"/></td>
								<td><form:errors path="name" cssClass="error"/></td>															
							</tr>
							<tr height=10px></tr>						
						</table>						
							<!-- Success // failure message -->
				 <c:choose>
          			  <c:when test="${success!=null && success==true}">
            			   <h2 class="successful">Multi variable created</h2>
            			   <c:set var="tooltip_next"><spring:message code="tooltip_next"/></c:set>
            			   <a href="editproject.html"><button style="width:100px" type="button" value="Next" title="${tooltip_next}">
            			   <spring:message code="next"/></button></a>
            		</c:when>
            		<c:when test="${success!=null && success==false}">
            			   <h2 class="error">Multi variable already exists</h2>
            		</c:when>            	
        		</c:choose>						
				
					</td><td align="right"><img src="assets/img/test_map.jpg"/></td>				
			</table>
		</td>
	</tr>
</table>
</form:form>
</body>
</html>