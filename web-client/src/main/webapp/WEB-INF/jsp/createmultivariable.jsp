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

<form:form method="post" action="createmultivariable.html" modelAttribute="multivariable">
<table>
	<tr>
		<td width=20></td>
		<td>
			<table class="ProjectCreationForm" style="width:900px" >
				<!-- create project -->
				<tr><td><h2>Create multi variable</h2></td></tr>	
				<tr height="20">
					<td><p>Please select component and input parameter:</p></td>
				</tr>
				<tr>
					<td>
						<table>
							<tr>						
								<td valign="top">
									<table class="tablestyle">
										<col style="width:80px">
										<col style="width:180px">
										<tr>
											<!-- Select -->
											<th><spring:message code="select"/></th>
											<!-- Component -->
											<th><spring:message code="components"/></th>
										</tr>
										
										<c:forEach items="${components}" var="component">
											<c:choose>
												<c:when test="${selectedcompid == component.componentid}">
													<tr style="background-color: #D4D4D4">
														<td><spring:message code="selected"/></td>
												</c:when>
												<c:otherwise>
													<tr>
														<td><a href="<c:url value='createmultivariable.html?multiscenarioid=${multiscenarioid}&selectedcompid=${component.componentid}'/>">
														<spring:message code="select"/></a></td>
												</c:otherwise>
											</c:choose>
												<td>${component.name}</td>
										    </tr>
										</c:forEach>
									</table>
								</td>
								<td></td>
								<td valign="top">
									<table class="tablestyle">
										<col style="width:80px">
										<col style="width:150px">
										<tr>
											<!-- Select -->
											<th><spring:message code="select"/></th>
											<!-- Input parameter -->
											<th><spring:message code="input_parameter"/></th>
										</tr>
										
										<c:forEach items="${inputParameters}" var="inputParam">
										<tr>
											<c:choose>
												<c:when test="${selectedinputid == inputParam.inputid}">
													<tr style="background-color: #D4D4D4">
														<td><spring:message code="selected"/></td>
												</c:when>
												<c:otherwise>
													<tr>
														<td><a href="<c:url value='createmultivariable.html?multiscenarioid=${multiscenarioid}&selectedcompid=${selectedcompid}&selectedinputid=${inputParam.inputid}'/>">
														<spring:message code="select"/></a></td>
												</c:otherwise>
											</c:choose>
											
											<td>${inputParam.name}</td>
									   	</tr>
										</c:forEach>
										
									</table>
								</td>
							</tr>				
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
        			</td>
        		</tr>
        		<tr height="20"></tr>
        		<tr valign="top">
					<td valign="top">
						<table>
							<tr>
								<td>Expression:</td>
								<c:set var="tooltip_name">Values</c:set>
								<td><form:input type="text" path="expression" title="${tooltip_expression}" style="width: 400px"/></td>
								<td><form:errors path="expression" cssClass="error"/></td>		
								<td align="right">
									<c:choose>
										<c:when test="${selectedinputid > 0}">
											<input type="submit" value="<spring:message code="create"/>" style="width:100px">
										</c:when>
										<c:otherwise>
											<button type="button" style="width:100px"><spring:message code="create"/></button>
										</c:otherwise>
									</c:choose>
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