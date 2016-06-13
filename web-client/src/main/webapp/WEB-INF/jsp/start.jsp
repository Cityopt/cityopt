<%--@elvariable id="project" type="com.cityopt.DTO.ProjectDTO"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>CityOpt <spring:message code="create_project" /></title>
<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
</head>
<body>
<table cellpadding="0" cellspacing="0">
	<tr>
		<td>
			<%@ include file="mainmenu.inc"%>
		</td>
		<td valign="top">
			<div style="overflow: auto; height: 100%; width: 100%; overflow: auto;">
			<table class="maintable">			
				<%@ include file="toprow.inc"%>
				<tr class="titlerow">
					<td class="spacecolumn"></td>
					<td>
						<table width="100%">
							<tr>
								<td><spring:message code="welcome"/></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td class="spacecolumn"></td>
					<td valign="top">
						<h2>WHAT IS CITYOPT?</h2>
						<p>Holistic simulation and optimization of energy systems in Smart Cities
							CITYOPT mission is to optimize energy systems in smart cities. The project will create a set of applications and related guidelines that support efficient planning, detailed design and operation of energy systems in urban districts.
			 			</p>
						<h2>Our approach</h2>
						<p>The project addresses energy system optimization in different life cycle phases. This will be supported by user-centred design approach. All stakeholders including cities' decision makers, facility managers citizens will be involved throughout all phases of the project</p>
						<h2>Our team</h2>
						<p>Our group gathers 7 project partners from 4 European countries, including research institutes, cities, energy utilities and a design studio. CITYOPT applications will be demonstrated in Helsinki (Finland), Vienna (Austria) and Nice Côte d'Azur (France).</p>
						<i>CITYOPT is supported by the European Commission through the Seventh Framework Programme (FP7)</i>
					</td>
				</tr>
			</table>
			</div>
		</td>
	</tr>
</table>
</body>
</html>