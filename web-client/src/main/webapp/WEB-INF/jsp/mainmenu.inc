<table class="menustyle" height=600 align="left">
<tr>
	<td valign="top">
		<b>Main menu</b>
	</td>
</tr>
<tr height=10>
	<td></td>
</tr>
<tr>
	<td><b>Project</b></td>
</tr>
<tr>
	<td>
		<a href="createproject.html">Create project</a>
	</td>
</tr>
<tr>
	<td>
		<a href="openproject.html">Open project</a>
	</td>
</tr>
<% if (request.getSession().getAttribute("project") != null) { %>
<tr>
	<td>
		<a href="editproject.html">Edit project</a>
	</td>
</tr>
<% } else { %>
<tr>
	<td>
		Edit project
	</td>
</tr>
<% } %>
<tr>
	<td>
		<a href="deleteproject.html">Delete project</a>
	</td>
</tr>
<% if (request.getSession().getAttribute("project") != null) { %>
<tr>
	<td>
		<a href="metricdefinition.html">Metrics</a>
	</td></tr>
<% } else { %>
<tr>
	<td>
		Metrics
	</td>
</tr>
<% } %>
<tr height=10>
	<td>
		
	</td>
</tr>
<tr>
	<td><b>Scenario</b></td>
</tr>
<% if (request.getSession().getAttribute("project") != null) { %>
<tr>
	<td>
		<a href="createscenario.html">Create scenario</a>
	</td>
</tr>
<tr>
	<td>
		<a href="openscenario.html">Open scenario</a>
	</td></tr>
<% if (request.getSession().getAttribute("scenario") != null) { %>
<tr>
	<td>
		<a href="editscenario.html">Edit scenario</a>
	</td>
</tr>
<% } else { %>
<tr>
	<td>
		Edit scenario
	</td>
</tr> 
<% } %>
<tr>
	<td>
		<a href="deletescenario.html">Delete scenario</a>
	</td>
</tr>
<tr>
	<td>
		<a href="runmultiscenario.html">Run multi-scenario</a>
	</td>
</tr>
<% } else { %>
<tr>
	<td>
		Create scenario
	</td>
</tr>
<tr>
	<td>
		Open scenario
	</td></tr>
<tr>
	<td>
		Edit scenario
	</td>
</tr>
<tr>
	<td>
		Delete scenario
	</td></tr>
<tr>
	<td>
		Run multi-scenario
	</td></tr>
<% } %>
<tr height=10>
	<td>
		
	</td>
</tr>
<tr height=10>
	<td>
		
	</td>
</tr>
<tr>
	<td><b>Optimization</b></td>
</tr>
<tr>
	<td>
		<a href="databaseoptimization.html">Create optimization set</a>
	</td></tr>
<tr>
	<td>
		<a href="geneticalgorithm.html">Genetic algorithm</a>
	</td></tr>
<tr>
	<td>
		<a href="openoptimizationset.html">Open optimization set</a>
	</td></tr>
<tr>
	<td>
		<a href="deleteoptimizationset.html">Delete optimization set</a>
	</td></tr>
<tr>
	<td>
		<a href="runmultioptimizationset.html">Run multi-optimization set</a>
	</td></tr>
<tr height=10>
	<td>
		
	</td>
</tr>
<tr>
	<td><b>Visualization</b></td>
</tr>
<tr>
	<td>
		<a href="viewchart.html">Chart</a>
	</td></tr>
<tr>
	<td>
		<a href="viewtable.html">Table</a>
	</td>
</tr>
<tr height=10>
	<td>
		
	</td>
</tr>
<tr>
	<td><b>Users</b></td>
</tr>
<tr>
	<td>
		<a href="usermanagement.html">User management</a>
	</td>
</tr>
</table>
