<head>
	<link rel="shortcut icon" href="assets/img/icon_logo_small.gif"> 
</head>
<table class="menustyle" height="1000" align="left">
<tr style="height: 58px">
	<td>
		<table class="menublock" height="57px">
			<col style="width: 60px">
			<col style="width: 150px">
			<tr>
				<td class="logo">
					<img alt="Logo" src="assets/img/icon_logo_small.gif"/>
				</td>
				<td valign="top" class="logotitle">CITYOPT</td>
			</tr>
		</table>
	</td>
</tr>
<tr>
	<td>
		<c:choose>
			<c:when test="${activeblock == 'project'}">
				<table class="menublockactive">
			</c:when>
			<c:otherwise>
				<table class="menublock">
			</c:otherwise>
		</c:choose>
			<col style="width: 60px">
			<col style="width: 150px">
			<tr>
				<td class="menuicon">
					<img src="assets/img/icon_project_black.gif"/>
				</td>
				<td><b><spring:message code="project"/></b></td>
			</tr>
			<tr>
				<td></td>
				<td>
					<c:choose>
						<c:when test="${page == 'createproject'}">
							<a href="createproject.html"><font color="white"><spring:message code="createproject"/></font></a>
						</c:when>
						<c:otherwise>
							<a href="createproject.html"><spring:message code="createproject"/></a>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<tr>
				<td></td>
				<td>
					<c:choose>
						<c:when test="${page == 'openproject'}">
							<a href="openproject.html"><font color="white"><spring:message code="openproject"/></font></a>
						</c:when>
						<c:otherwise>
							<a href="openproject.html"><spring:message code="openproject"/></a>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<c:choose>	
				<c:when test="${not empty project}">
					<tr>
						<td></td>
						<c:choose>
							<c:when test="${page == 'editproject'}">
								<td>
									<a href="editproject.html"><font color="white"><spring:message code="editproject"/></font></a>
							</c:when>
							<c:otherwise>
								<td>
									<a href="editproject.html"><spring:message code="editproject"/></a>
							</c:otherwise>
						</c:choose>
						</td>
					</tr>
				</c:when>
				<c:otherwise>
					<!-- <tr>
						<td></td>
						<td>
							<spring:message code="editproject"/>
						</td>
					</tr>-->
				</c:otherwise>
			</c:choose>
			<tr>
				<td></td>
				<td>
					<c:choose>
						<c:when test="${page == 'deleteproject'}">
							<a href="deleteproject.html"><font color="white"><spring:message code="deleteproject"/></font></a>
						</c:when>
						<c:otherwise>
							<a href="deleteproject.html"><spring:message code="deleteproject"/></a>	
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<c:choose>	
				<c:when test="${not empty project}">
					<tr>
						<td></td>
						<td>
							<c:choose>
								<c:when test="${page == 'importdata'}">
									<a href="importdata.html"><font color="white"><spring:message code="importdata"/></font></a>
								</c:when>
								<c:otherwise>
									<a href="importdata.html"><spring:message code="importdata"/></a>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
					<tr>
						<td></td>
						<td>
							<c:choose>
								<c:when test="${page == 'exportdata'}">
									<a href="exportdata.html"><font color="white"><spring:message code="exportdata"/></font></a>
								</c:when>
								<c:otherwise>
									<a href="exportdata.html"><spring:message code="exportdata"/></a>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</c:when>
				<c:otherwise>
					<!-- <tr>
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
					</tr>-->
				</c:otherwise>
			</c:choose>
			<c:choose>	
				<c:when test="${not empty project}">
				<tr>
					<td></td>
					<td>
						<c:choose>
							<c:when test="${page == 'metricdefinition'}">
								<a href="metricdefinition.html"><font color="white"><spring:message code="metrics"/></font></a>
							</c:when>
							<c:otherwise>
								<a href="metricdefinition.html"><spring:message code="metrics"/></a>
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
				</c:when>
				<c:otherwise>
					<!-- 
					<tr>
						<td></td>
						<td>
							<spring:message code="metrics"/>
						</td>
					</tr>-->
				</c:otherwise>
			</c:choose>
			
			<tr height="10">
			</tr>
		</table>
	</td>
</tr>
<tr>
	<td>
		<c:choose>
			<c:when test="${activeblock == 'scenario'}">
				<table class="menublockactive">
			</c:when>
			<c:otherwise>
				<table class="menublock">
			</c:otherwise>
		</c:choose>
			<col style="width: 60px">
			<col style="width: 150px">
			<tr>
				<td class="menuicon">	
					<img src="assets/img/icon_scenario_black.gif"/>
				</td>
				<td><b><spring:message code="scenario"/></b></td>
			</tr>
			<c:choose>	
				<c:when test="${not empty project}">
					<tr>
						<td></td>
						<td>
							<c:choose>
								<c:when test="${page == 'createscenario'}">
									<a href="createscenario.html"><font color="white"><spring:message code="create_scenario"/></font></a>
								</c:when>
								<c:otherwise>
									<a href="createscenario.html"><spring:message code="create_scenario"/></a>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
					<tr>
						<td></td>
						<td>
							<c:choose>
								<c:when test="${page == 'openscenario'}">
									<a href="openscenario.html"><font color="white"><spring:message code="open_scenario"/></font></a>
								</c:when>
								<c:otherwise>
									<a href="openscenario.html"><spring:message code="open_scenario"/></a>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
					<c:choose>	
						<c:when test="${usersession.getActiveScenario() != null}">
							<tr>
								<td></td>
								<td>
									<c:choose>
										<c:when test="${page == 'editscenario'}">
											<a href="editscenario.html"><font color="white"><spring:message code="edit_scenario"/></font></a>
										</c:when>
										<c:otherwise>
											<a href="editscenario.html"><spring:message code="edit_scenario"/></a>
										</c:otherwise>
									</c:choose>
								</td>
							</tr>
						</c:when>
						<c:otherwise>
							<!-- <tr>
								<td></td>
								<td>
									<spring:message code="edit_scenario"/>
								</td>
							</tr>--> 
						</c:otherwise>
					</c:choose>
					<tr>
						<td></td>
						<td>
							<c:choose>
								<c:when test="${page == 'deletescenario'}">
									<a href="deletescenario.html"><font color="white"><spring:message code="delete_scenario"/></font></a>
								</c:when>
								<c:otherwise>
									<a href="deletescenario.html"><spring:message code="delete_scenario"/></a>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</c:when>	
				<c:otherwise>
					<!-- <tr>
						<td></td>
						<td>
							<spring:message code="create_scenario"/>
						</td>
					</tr>
					<tr>
						<td></td>
						<td>
							<spring:message code="open_scenario"/>
						</td></tr>
					<tr>
						<td></td>
						<td>
							<spring:message code="edit_scenario"/>
						</td>
					</tr>
					<tr>
						<td></td>
						<td>
							<spring:message code="delete_scenario"/>		
						</td></tr>-->
				</c:otherwise>
			</c:choose>
			<tr height="10">
			</tr>
		</table>
	</td>
<tr>
<tr>
	<td>
		<c:choose>
			<c:when test="${activeblock == 'optimization'}">
				<table class="menublockactive">
			</c:when>
			<c:otherwise>
				<table class="menublock">
			</c:otherwise>
		</c:choose>
			<col style="width: 60px">
			<col style="width: 150px">
			<tr>
				<td class="menuicon">
					<img src="assets/img/icon_optimization_black.gif"/>
				</td>
				<td><b><spring:message code="optimization"/></b></td>
			</tr>
			<c:choose>
				<c:when test="${not empty project}">
				<tr>
					<td></td>
					<td>
						<c:choose>
							<c:when test="${page == 'createoptimizationset'}">
								<a href="createoptimizationset.html"><font color="white"><spring:message code="create_optimization_set"/></font></a>
							</c:when>
							<c:otherwise>
								<a href="createoptimizationset.html"><spring:message code="create_optimization_set"/></a>
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
				<tr>
					<td></td>
					<td>
						<c:choose>
							<c:when test="${page == 'openoptimizationset'}">
								<a href="openoptimizationset.html"><font color="white"><spring:message code="open_optimization_set"/></font></a>
							</c:when>
							<c:otherwise>
								<a href="openoptimizationset.html"><spring:message code="open_optimization_set"/></a>
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
				</c:when>
				<c:otherwise>	
					<!-- <tr>
						<td></td>
						<td>
							<spring:message code="create_optimization_set"/>
						</td>
					</tr>
					<tr>
						<td></td>
						<td>
							<spring:message code="open_optimization_set"/>		
						</td>
					</tr>-->
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${usersession.getActiveOptSet() != null}">
					<tr>
						<td></td>
						<td>
							<c:choose>
								<c:when test="${page == 'editoptimizationset'}">
									<a href="editoptimizationset.html"><font color="white"><spring:message code="edit"/> ${usersession.getActiveOptSet()}</font></a>
								</c:when>
								<c:otherwise>
									<a href="editoptimizationset.html"><spring:message code="edit"/> ${usersession.getActiveOptSet()}</a>
								</c:otherwise>
							</c:choose>
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
							<c:choose>
								<c:when test="${page == 'geneticalgorithm'}">
									<a href=geneticalgorithm.html><font color="white"><spring:message code="edit"/> ${usersession.getActiveScenGen()}</font></a>
								</c:when>
								<c:otherwise>
									<a href="geneticalgorithm.html"><spring:message code="edit"/> ${usersession.getActiveScenGen()}</a>
								</c:otherwise>
							</c:choose>
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
							<c:choose>
								<c:when test="${page == 'deleteoptimizationset'}">
									<a href="deleteoptimizationset.html"><font color="white"><spring:message code="delete_optimization_set"/></font></a>
								</c:when>
								<c:otherwise>
									<a href="deleteoptimizationset.html"><spring:message code="delete_optimization_set"/></a>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
					<tr>
						<td></td>
						<td>
							<c:choose>
								<c:when test="${page == 'garuns'}">
									<a href="garuns.html"><font color="white"><spring:message code="running_genetic_optimizations"/></font></a>
								</c:when>
								<c:otherwise>
									<a href="garuns.html"><spring:message code="running_genetic_optimizations"/></a>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</c:when>
				<c:otherwise>
					<!-- <tr>
						<td></td>
						<td>
							<spring:message code="delete_optimization_set"/>
						</td>
					</tr>
					<tr>
						<td></td>
						<td>
							<spring:message code="running_genetic_optimizations"/>
						</td>
					</tr>-->
				</c:otherwise>
			</c:choose>
			<tr height=10>
			</tr>
		</table>
	</td>
</tr>
<tr>
	<td>
		<c:choose>
			<c:when test="${activeblock == 'visualization'}">
				<table class="menublockactive">
			</c:when>
			<c:otherwise>
				<table class="menublock">
			</c:otherwise>
		</c:choose>
			<col style="width: 60px">
			<col style="width: 150px">
			<tr>
				<td class="menuicon">
					<img src="assets/img/icon_Visualization_black.gif"/>
				</td>
				<td><b><spring:message code="visualization"/></b></td>
			</tr>
			<c:choose>
				<c:when test="${not empty scenario}">
					<tr>
						<td></td>
						<td>
							<c:choose>
								<c:when test="${page == 'timeserieschart'}">
									<a href="timeserieschart.html"><font color="white"><spring:message code="time_series_chart"/></font></a>
								</c:when>
								<c:otherwise>
									<a href="timeserieschart.html"><spring:message code="time_series_chart"/></a>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</c:when>
			<c:otherwise>
				<!-- <tr>
					<td></td>
					<td>
						<spring:message code="time_series_chart"/>
					</td>
				</tr>-->
			</c:otherwise>
		</c:choose>
		<c:choose>
			<c:when test="${not empty project}">
				<tr>
					<td></td>
					<td>
						<c:choose>
							<c:when test="${page == 'summarychart'}">
								<a href="summarychart.html"><font color="white"><spring:message code="summary_chart"/></font></a>
							</c:when>
							<c:otherwise>
								<a href="summarychart.html"><spring:message code="summary_chart"/></a>
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
			</c:when>
			<c:otherwise>
				<!-- <tr>
					<td></td>
					<td>
						<spring:message code="summary_chart"/>
					</td>
				</tr>-->
			</c:otherwise>
		</c:choose>
		<c:choose>
			<c:when test="${not empty scengenerator}">
				<tr>
					<td></td>
					<td>
						<c:choose>
							<c:when test="${page == 'gachart'}">
								<a href="gachart.html?resetselections=true"><font color="white"><spring:message code="genetic_optimization_chart"/></font></a>
							</c:when>
							<c:otherwise>
								<a href="gachart.html?resetselections=true"><spring:message code="genetic_optimization_chart"/></a>
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
			</c:when>
			<c:otherwise>
				<!-- <tr>
					<td></td>
					<td>
						<spring:message code="genetic_optimization_chart"/>
					</td>
				</tr>-->
			</c:otherwise>
		</c:choose>
		<c:choose>
			<c:when test="${not empty scenario}">
				<tr>
					<td></td>
					<td>
						<c:choose>
							<c:when test="${page == 'viewtable'}">
								<a href="viewtable.html"><font color="white"><spring:message code="time_series_table"/></font></a>
							</c:when>
							<c:otherwise>
								<a href="viewtable.html"><spring:message code="time_series_table"/></a>
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
			</c:when>
		<c:otherwise>
			<!-- <tr>
				<td></td>
				<td>
					<spring:message code="time_series_table"/>
				</td>
			</tr>-->
		</c:otherwise>
	</c:choose>
	<tr height=10>
	</tr>
</table>
</td>
</tr>	
<tr>
	<td>
		<c:choose>
			<c:when test="${activeblock == 'settings'}">
				<table class="menublockactive">
			</c:when>
			<c:otherwise>
				<table class="menublock">
			</c:otherwise>
		</c:choose>
			<col style="width: 60px">
			<col style="width: 150px">
			<tr>
				<td class="menuicon">
					<img src="assets/img/icon_users_black.gif"/>
				</td>
				<td><b><spring:message code="users_and_settings"/></b></td>
			</tr>
			<tr>
				<td></td>
				<td>
					<c:choose>
						<c:when test="${page == 'userinfo'}">
							<a href="userinfo.html"><font color="white"><spring:message code="user_info"/></font></a>
						</c:when>
						<c:otherwise>
							<a href="userinfo.html"><spring:message code="user_info"/></a>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<tr>
				<td></td>
				<td>
					<c:choose>
						<c:when test="${page == 'usermanagement'}">
							<a href="usermanagement.html"><font color="white"><spring:message code="user_management"/></font></a>
						</c:when>
						<c:otherwise>
							<a href="usermanagement.html"><spring:message code="user_management"/></a>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<tr>
				<td></td>
				<td>
					<c:choose>
						<c:when test="${page == 'units'}">
							<a href="units.html"><font color="white"><spring:message code="units"/></font></a>
						</c:when>
						<c:otherwise>
							<a href="units.html"><spring:message code="units"/></a>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<tr>
				<td></td>
				<td>
					<c:choose>
						<c:when test="${page == 'settings'}">
							<a href="settings.html"><font color="white"><spring:message code="settings"/></font></a>
						</c:when>
						<c:otherwise>
							<a href="settings.html"><spring:message code="settings"/></a>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<tr height="10"></tr>
		</table>
	</td>
</tr>
</table>
	
