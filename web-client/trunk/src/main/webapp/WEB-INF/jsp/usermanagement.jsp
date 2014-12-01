<%@ page language="java" contentType="text/html"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<title>CityOpt User management</title>
		<link rel="stylesheet" type="text/css" href="assets/css/style.css" />


    <script type="text/javascript">
    //<![CDATA[

    var tabLinks = new Array();
    var contentDivs = new Array();

    function init() {

      // Grab the tab links and content divs from the page
      var tabListItems = document.getElementById('tabs').childNodes;
      for ( var i = 0; i < tabListItems.length; i++ ) {
        if ( tabListItems[i].nodeName == "LI" ) {
          var tabLink = getFirstChildWithTagName( tabListItems[i], 'A' );
          var id = getHash( tabLink.getAttribute('href') );
          tabLinks[id] = tabLink;
          contentDivs[id] = document.getElementById( id );
        }
      }

      // Assign onclick events to the tab links, and
      // highlight the first tab
      var i = 0;

      for ( var id in tabLinks ) {
        tabLinks[id].onclick = showTab;
        tabLinks[id].onfocus = function() { this.blur() };
        if ( i == 0 ) tabLinks[id].className = 'selected';
        i++;
      }

      // Hide all content divs except the first
      var i = 0;

      for ( var id in contentDivs ) {
        if ( i != 0 ) contentDivs[id].className = 'tabContent hide';
        i++;
      }
    }

    function showTab() {
      var selectedId = getHash( this.getAttribute('href') );

      // Highlight the selected tab, and dim all others.
      // Also show the selected content div, and hide all others.
      for ( var id in contentDivs ) {
        if ( id == selectedId ) {
          tabLinks[id].className = 'selected';
          contentDivs[id].className = 'tabContent';
        } else {
          tabLinks[id].className = '';
          contentDivs[id].className = 'tabContent hide';
        }
      }

      // Stop the browser following the link
      return false;
    }

    function getFirstChildWithTagName( element, tagName ) {
      for ( var i = 0; i < element.childNodes.length; i++ ) {
        if ( element.childNodes[i].nodeName == tagName ) return element.childNodes[i];
      }
    }

    function getHash( url ) {
      var hashPos = url.lastIndexOf ( '#' );
      return url.substring( hashPos + 1 );
    }

    //]]>
    </script>
</head>
<body>
<table cellspacing="0" cellpadding="0">
	<tr>
		<td>
			<%@include file='mainmenu.inc'%>
		</td>
		<td width="30"></td>
		<td valign="top">
			<table>
			   	<tr>
			      	<td valign="top">
			      		<table>
			      			<tr>
			      				<td><h2>User definition</h2></td>
			   				</tr>
			      			<tr>
			      			   	<td>
				      			   	<table class="tablestyle" border="1">
							      		<tr>
							      			<th>Name</th>
							      			<th>Company</th>
							      			<th>User name</th>
							      			<th>Password</th>
							      			<th>Email</th>
							      			<th>User role</th>
							      			<th>Start rights</th>
							      			<th>Finish projects</th>
							      			<th>Project</th>
							      			<th>Edit</th>
							      			<th>Delete</th>
							   			</tr>
							      		<tr>
							      			<td>x</td>
							      			<td>x</td>
							      			<td>x</td>
							      			<td>x</td>
							      			<td>x</td>
							      			<td>x</td>
							      			<td>x</td>
							      			<td>x</td>
							      			<td>x</td>
			   								<td>
												<a href="<c:url value='edit.html'/>">
													<button align="right" type="button" value="Edit">Edit</button>
												</a>
											</td>
			   								<td>
												<a href="<c:url value='deleteuser.html'/>">
													<button align="right" type="button" value="Delete">Delete</button>
												</a>
											</td>
							      			
							   			</tr>
							   			
							   				<c:forEach items="${userGroups}" var="userGroups">
												<tr>
													<td>${userGroups.name}</td>
											    	<td>${userGroups.usergroupid}</td>
													<td></td>			
													<td></td>
													<td>
													</td>
											   	</tr>
											</c:forEach>
						
							      	</table>
					      		</td>
					      	</tr>
					      	<tr height="0">
					      		<td></td>
					      	</tr>
							<tr>
								<td align="right">
				      				<a href="createuser.html"><button type="button">Create user</button></a>
			      				</td>
			   				</tr>
			      			      	<!-- <tr width="600" valign="bottom">
					      		<table valign="top" width="400" align="right">
							    <ul id="tabs">
							      <li><a href="#about">About JavaScript tabs</a></li>
							      <li><a href="#advantages">Advantages of tabs</a></li>
							      <li><a href="#usage">Using tabs</a></li>
							    </ul>
							
							    <div width="400" class="tabContent" id="about">
							      <h2>About JavaScript tabs</h2>
							      <div width="400">
							        <p>JavaScript tabs partition your Web page content into tabbed sections. Only one section at a time is visible.</p>
							        <p>The code is written in such a way that the page degrades gracefully in browsers that don't support JavaScript or CSS.</p>
							      </div>
							    </div>
							
							    <div width="400" class="tabContent" id="advantages">
							      <h2>Advantages of tabs</h2>
							      <div width="400">
							        <p>JavaScript tabs are great if your Web page contains a large amount of content.</p>
							        <p>They're also good for things like multi-step Web forms.</p>
							      </div>
							    </div>
							
							    <div width="400" class="tabContent" id="usage">
							      <h2>Using tabs</h2>
							      <div width="400">
							        <p>Click a tab to view the tab's content. Using tabs couldn't be easier!</p>
							      </div>
							    </div>
							    </table>
				      		</tr>-->
				      	</table>
			      	</td>
			   	</tr>
			</table>
		</td>
	</tr>
</table>
</body>
</html>