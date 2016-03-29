<head>
	<link rel="shortcut icon" href="assets/img/icon_logo_small.gif"> 
</head>
<table class="menustyle" height=1000 align="left">
<col style="width: 60px">
<col style="width: 150px">
<tr>
	<td>
		<img src="assets/img/icon_logo_small.gif"/>
	</td>
	<td valign="top">
		<h1>CITYOPT</h1>
	</td>
</tr>
<tr>
	<td></td>
	<td>
		<table cellpadding="0" cellspacing="0">
			<tr>
				<td><spring:message code="version"/>:&nbsp;</td><td>${version}</td>
			</tr>
			<tr>
				<td><spring:message code="build_date"/>:&nbsp;</td><td>1.4.2016</td>
			</tr>
			<tr>
				<td>User:&nbsp;
				
				</td><td><%= ((org.springframework.security.core.userdetails.UserDetails)org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername() %>
				<!--${user.name}-->
				(<a href="logout.html">Logout</a>)</td>
			</tr>
			<tr>
				<td>Language:&nbsp;</td>
		   		<td>
			   		${usersession.getLanguage()}&nbsp;(<a href="settings.html">Change</a>) 
			    </td>
			</tr>
		</table>
	</td>	
</tr>
<tr>
	<td>
		<img src="assets/img/icon_project_black.gif"/>
	</td>
<c:choose>	
	<c:when test="${usersession.getActiveProject() != null}">
		<td><b><spring:message code="project"/></b> (${usersession.getActiveProject()})</td>
	</c:when>
	<c:otherwise>
		<td><b><spring:message code="project"/></b></td>
	</c:otherwise>
</c:choose>
</tr>
<tr>
	<td></td>
	<td>
		<a href="createproject.html"><spring:message code="createproject"/></a>
	</td>
</tr>
<tr>
	<td></td>
	<td>
		<a href="openproject.html"><spring:message code="openproject"/></a>
	</td>
</tr>
<c:choose>	
	<c:when test="${not empty project}">
		<tr>
			<td></td>
			<td>
				<a href="editproject.html"><spring:message code="editproject"/></a>
			</td>
		</tr>
	</c:when>
	<c:otherwise>
		<tr>
			<td></td>
			<td>
				<spring:message code="editproject"/>
			</td>
		</tr>
	</c:otherwise>
</c:choose>
<tr>
	<td></td>
	<td>
		<a href="deleteproject.html"><spring:message code="deleteproject"/></a>
	</td>
</tr>
<c:choose>	
	<c:when test="${not empty project}">
		<tr>
			<td></td>
			<td>
				<a href="importdata.html"><spring:message code="importdata"/></a>
			</td>
		</tr>
		<tr>
			<td></td>
			<td>
				<a href="exportdata.html"><spring:message code="exportdata"/></a>
			</td>
		</tr>
	</c:when>
	<c:otherwise>
		<tr>
			<td></td>
			<td>
				<spring:message code="importdata"/>
			</td>
		</tr>
		<tr>
			<td></td>
			<td>
				<spring:message code="exportdata"/>
			</td>
		</tr>
	</c:otherwise>
</c:choose>
<c:choose>	
	<c:when test="${not empty project}">
	<tr>
		<td></td>
		<td>
			<a href="metricdefinition.html"><spring:message code="metrics"/></a>
		</td>
	</tr>
	</c:when>
	<c:otherwise>
		<tr>
			<td></td>
			<td>
				<spring:message code="metrics"/>
			</td>
		</tr>
	</c:otherwise>
</c:choose>

<tr height=10>
	<td></td>
	<td>
		
	</td>
</tr>
<tr>
	<td>
		<img src="assets/img/icon_scenario_black.gif"/>
	</td>
<c:choose>	
	<c:when test="${usersession.getActiveScenario() != null}">
		<td><b>Scenario</b> (${usersession.getActiveScenario()})</td>
	</c:when>
	<c:otherwise>
		<td><b>Scenario</b></td>
	</c:otherwise>
</c:choose>
</tr>

<c:choose>	
	<c:when test="${not empty project}">
		<tr>
			<td></td>
			<td>
				<!-- Create scenario link -->
				<a href="createscenario.html">
				<spring:message code="create_scenario"/></a>
			</td>
		</tr>
		<tr>
			<td></td>
			<td>
				<!-- Open scenario link -->
				<a href="openscenario.html">
				<spring:message code="open_scenario"/></a>
			</td></tr>

			<c:choose>	
				<c:when test="${usersession.getActiveScenario() != null}">
					<tr>
						<td></td>
						<td>
							<!-- Edit scenario with a link -->
							<a href="editscenario.html">
							<spring:message code="edit_scenario"/></a>
						</td>
					</tr>
				</c:when>
				<c:otherwise>
					<tr>
						<td></td>
						<td>
							<!-- Edit scenario without link -->
							<spring:message code="edit_scenario"/>
						</td>
					</tr> 
				</c:otherwise>
			</c:choose>
			<tr>
				<td></td>
				<td>
					<!-- Delete scenario link -->
					<a href="deletescenario.html">
					<spring:message code="delete_scenario"/></a>
				</td>
			</tr>
		</c:when>	
		<c:otherwise>
			<tr>
				<td></td>
				<td>
					<!--Create scenario  -->
					<spring:message code="create_scenario"/>
					
				</td>
			</tr>
			<tr>
				<td></td>
				<td>
					<!-- Open scenario -->
					<spring:message code="open_scenario"/>
					
				</td></tr>
			<tr>
				<td></td>
				<td>
					<!-- Edit scenario -->
					<spring:message code="edit_scenario"/>
				</td>
			</tr>
			<tr>
				<td></td>
				<td>
					<!-- Delete scenario -->
					<spring:message code="delete_scenario"/>		
				</td></tr>
	</c:otherwise>
</c:choose>
<tr height=10>
	<td></td>
	<td>
		
	</td>
</tr>
<tr>
	<td>
		<img src="assets/img/icon_optimization_black.gif"/>
	</td>
	<!-- Optimization title -->
	<td><b><spring:message code="optimization"/></b></td>
</tr>
<c:choose>
	<c:when test="${not empty project}">
		<tr>
			<td></td>
			<td>
				<!-- Create optimization with a link -->
				<a href="createoptimizationset.html">
				<spring:message code="create_optimization_set"/></a>
			</td>
		</tr>
		<tr>
			<td></td>
			<td>
				<!-- Open optimization with a link -->
				<a href="openoptimizationset.html">
				<spring:message code="open_optimization_set"/></a>
			</td>
		</tr>
	</c:when>
	<c:otherwise>	
		<tr>
			<td></td>
			<td>
				<!-- Create Optimization set with out link -->
				<spring:message code="create_optimization_set"/>
			</td>
		</tr>
		<tr>
			<td></td>
			<td>
				<!-- Open Optimization set with out link -->
				<spring:message code="open_optimization_set"/>		
			</td>
		</tr>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${usersession.getActiveOptSet() != null}">
		<tr>
			<td></td>
			<td>
				<a href="editoptimizationset.html">
				<spring:message code="edit"/> ${usersession.getActiveOptSet()}</a>
			</td>
		</tr>
	</c:when>
	<c:otherwise>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${usersession.getActiveScenGen() != null}">
		<tr>
			<td></td>
			<td>
				<a href="geneticalgorithm.html">
				<spring:message code="edit"/> ${usersession.getActiveScenGen()}</a>
			</td>
		</tr>
	</c:when>
	<c:otherwise>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${not empty project}">
		<tr>
			<td></td>
			<td>
				<!-- Delete optimization set -->
				<a href="deleteoptimizationset.html">
				<spring:message code="delete_optimization_set"/></a>
			</td>
		</tr>
		<tr>
			<td></td>
			<td>
				<a href="garuns.html"><spring:message code="running_genetic_optimizations"/></a>
			</td>
		</tr>
	</c:when>
	<c:otherwise>
		<tr>
			<td></td>
			<td>
				<!-- Delete optimization set -->
				<spring:message code="delete_optimization_set"/>
			</td>
		</tr>
		<tr>
			<td></td>
			<td>
				<spring:message code="running_genetic_optimizations"/>
			</td>
		</tr>
	</c:otherwise>
</c:choose>

<tr height=10>
	<td>
		
	</td>
</tr>
<tr>
	<td>
		<img src="assets/img/icon_Visualization_black.gif"/>
	</td>
	<!--  -->
	<td><b><spring:message code="visualization"/></b></td>
</tr>

<c:choose>
	<c:when test="${not empty scenario}">
		<tr>
			<td></td>
			<td>
				<a href="timeserieschart.html"><spring:message code="time_series_chart"/></a>
			</td>
		</tr>
	</c:when>
	<c:otherwise>
		<tr>
			<td></td>
			<td>
				<spring:message code="time_series_chart"/>
			</td>
		</tr>
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test="${not empty project}">
		<tr>
			<td></td>
			<td>
				<a href="summarychart.html"><spring:message code="summary_chart"/></a>
			</td>
		</tr>
	</c:when>
	<c:otherwise>
		<tr>
			<td></td>
			<td>
				<spring:message code="summary_chart"/>
			</td>
		</tr>
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test="${not empty scengenerator}">
		<tr>
			<td></td>
			<td>
				<a href="gachart.html"><spring:message code="genetic_optimization_chart"/></a>
			</td>
		</tr>
	</c:when>
	<c:otherwise>
		<tr>
			<td></td>
			<td>
				<spring:message code="genetic_optimization_chart"/>
			</td>
		</tr>
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test="${not empty scenario}">
		<tr>
			<td></td>
			<td>
				<a href="viewtable.html">
				<spring:message code="time_series_table"/></a>
			</td>
		</tr>
	</c:when>
	<c:otherwise>
		<tr>
			<td></td>
			<td>
				<spring:message code="time_series_table"/>
			</td>
		</tr>
	</c:otherwise>
</c:choose>

<tr height=10>
	<td>
		
	</td>
</tr>
<tr>
	<td>
		<img src="assets/img/icon_users_black.gif"/>
	</td>
	<td><b><spring:message code="users_and_settings"/></b></td>
</tr>
<tr>
	<td></td>
	<td>
		<!-- User management-->
		<a href="usermanagement.html">
		<spring:message code="user_management"/></a>
	</td>
</tr>
<tr>
	<td></td>
	<td>
		<!-- Units -->
		<a href="units.html">
		<spring:message code="units"/></a>
	</td>
</tr>
<tr>
	<td></td>
	<td>
		<!--Parameter reliability-->
		<a href="paramreliability.html">
		<spring:message code="parameter_reliability"/></a>
	</td>
</tr>
<tr>
	<td></td>
	<td>
		<a href="settings.html">
		<spring:message code="settings"/></a>
	</td>
</tr>
</table>
