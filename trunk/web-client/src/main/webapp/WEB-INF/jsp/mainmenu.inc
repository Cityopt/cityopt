<table class="menustyle" height=800 align="left">
<col style="width: 60px">
<col style="width: 144px">
<tr>
	<td>
		<img src="assets/img/icon_logo_small.jpg"/>
	</td>
	<td valign="top">
		<h1>CITYOPT</h1>
	</td>
</tr>
<tr>
	<td></td>
	<td><p>Version: <%= request.getSession().getAttribute("version") %><br>
	<spring:message code="language"/>:
   		<a href="openproject.html?lang=en">en</a> 
	    <a href="openproject.html?lang=es">es</a>
	    <a href="openproject.html?lang=fi">fi</a></p></td>
</tr>
<tr>
	<td>
		<img src="assets/img/icon_project_black.jpg"/>
	</td>
<% if (request.getSession().getAttribute("project") != null) { %>
	<td><b><spring:message code="project"/></b> (${project.name})</td>
<% } else { %>
	<td><b><spring:message code="project"/></b></td>
<% } %>
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
<% if (request.getSession().getAttribute("project") != null) { %>
<tr>
	<td></td>
	<td>
		<a href="editproject.html"><spring:message code="editproject"/></a>
	</td>
</tr>
<% } else { %>
<tr>
	<td></td>
	<td>
		<spring:message code="editproject"/>
	</td>
</tr>
<% } %>
<tr>
	<td></td>
	<td>
		<a href="deleteproject.html"><spring:message code="deleteproject"/></a>
	</td>
</tr>
<tr>
	<td></td>
	<td>
		<a href="importdata.html"><spring:message code="importdata"/></a>
	</td>
</tr>
<% if (request.getSession().getAttribute("project") != null) { %>
<tr>
	<td></td>
	<td>
		<a href="metricdefinition.html"><spring:message code="metrics"/></a>
	</td></tr>
<% } else { %>
<tr>
	<td></td>
	<td>
		<spring:message code="metrics"/>
	</td>
</tr>
<% } %>
<tr height=10>
	<td></td>
	<td>
		
	</td>
</tr>
<tr>
	<td>
		<img src="assets/img/icon_scenario_black.jpg"/>
	</td>
<% if (request.getSession().getAttribute("scenario") != null) { %>
	<td><b>Scenario</b> (${scenario.name})</td>
<% } else { %>
	<td><b>Scenario</b></td>
<% } %>
</tr>
<% if (request.getSession().getAttribute("project") != null) { %>
<tr>
	<td></td>
	<td>
		<a href="createscenario.html">Create scenario</a>
	</td>
</tr>
<tr>
	<td></td>
	<td>
		<a href="openscenario.html">Open scenario</a>
	</td></tr>
<% if (request.getSession().getAttribute("scenario") != null) { %>
<tr>
	<td></td>
	<td>
		<a href="editscenario.html">Edit scenario</a>
	</td>
</tr>
<% } else { %>
<tr>
	<td></td>
	<td>
		Edit scenario
	</td>
</tr> 
<% } %>
<tr>
	<td></td>
	<td>
		<a href="deletescenario.html">Delete scenario</a>
	</td>
</tr>
<tr>
	<td></td>
	<td>
		<a href="runmultiscenario.html">Run multi-scenario</a>
	</td>
</tr>
<% } else { %>
<tr>
	<td></td>
	<td>
		Create scenario
	</td>
</tr>
<tr>
	<td></td>
	<td>
		Open scenario
	</td></tr>
<tr>
	<td></td>
	<td>
		Edit scenario
	</td>
</tr>
<tr>
	<td></td>
	<td>
		Delete scenario
	</td></tr>
<tr>
	<td></td>
	<td>
		Run multi-scenario
	</td></tr>
<% } %>
<tr height=10>
	<td></td>
	<td>
		
	</td>
</tr>
<tr>
	<td>
		<img src="assets/img/icon_optimization_black.jpg"/>
	</td>
	<td><b>Optimization</b></td>
</tr>
<% if (request.getSession().getAttribute("project") != null) { %>
<tr>
	<td></td>
	<td>
		<a href="createoptimizationset.html">Create optimization set</a>
	</td></tr>
<tr>
	<td></td>
	<td>
		<a href="openoptimizationset.html">Open optimization set</a>
	</td></tr>
<% } else { %>
<tr>
	<td></td>
	<td>
		Create optimization set
	</td>
</tr>
<tr>
	<td></td>
	<td>
		Open optimization set
	</td>
</tr>
<% } %>
<% if (request.getSession().getAttribute("optimizationset") != null) { %>
<tr>
	<td></td>
	<td>
		<a href="editoptimizationset.html">Edit DB optimization set</a>
	</td>
</tr>
<% } else { %>
<tr>
	<td></td>
	<td>
		Edit DB optimization set
	</td>
</tr>
<% } %>
<% if (request.getSession().getAttribute("scengenerator") != null) { %>
<tr>
	<td></td>
	<td>
		<a href="geneticalgorithm.html">Edit GA optimization set</a>
	</td>
</tr>
<% } else { %>
<tr>
	<td></td>
	<td>
		Edit GA optimization set
	</td>
</tr>
<% } %>
<tr>
	<td></td>
	<td>
		<a href="deleteoptimizationset.html">Delete optimization set</a>
	</td></tr>
<tr>
	<td></td>
	<td>
		<a href="runmultioptimizationset.html">Run multi-optimization set</a>
	</td></tr>
<tr height=10>
	<td>
		
	</td>
</tr>
<tr>
	<td>
		<img src="assets/img/icon_Visualization_black.jpg"/>
	</td>
	<td><b>Visualization</b></td>
</tr>
<tr>
	<td></td>
	<td>
		<a href="viewchart.html">Chart</a>
	</td></tr>
<tr>
	<td></td>
	<td>
		<a href="viewtable.html">Table</a>
	</td>
</tr>
<tr height=10>
	<td>
		
	</td>
</tr>
<tr>
	<td>
		<img src="assets/img/icon_users_black.jpg"/>
	</td>
	<td><b>Users</b></td>
</tr>
<tr>
	<td></td>
	<td>
		<a href="usermanagement.html">User management</a>
	</td>
</tr>
<tr>
	<td></td>
	<td>
		<a href="units.html">Units</a>
	</td>
</tr>
<tr>
	<td></td>
	<td>
		<a href="paramreliability.html">Parameter reliability</a>
	</td>
</tr>
</table>
