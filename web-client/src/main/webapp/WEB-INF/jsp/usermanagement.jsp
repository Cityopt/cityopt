<%@ page language="java" contentType="text/html"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
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
			      				<!-- Users -->
			      				<td><h2><spring:message code="usermanagement"/></h2></td>
			   				</tr>
			      			<tr>
			      			   	<td>
				      			   	<table class="tablestyle" border="1">
							      		<col style="width:50px">							      			
										<col style="width:50px">	
										<col style="width:50px">
										<col style="width:50px">
										<col style="width:50px">	
																	      			
							      			<!-- Username -->
							      			<th><spring:message code="username"/></th>							      			
							      			<!-- Password -->
							      			<th><spring:message code="password"/></th>							      			
							      			<!-- UserRole -->
							      			<th><spring:message code="userole"/></th>  			
							      			<!-- Project -->
							      			<th><spring:message code="project"/></th>
							      			<!-- Active -->
							      			<th><spring:message code="enabled"/></th>							      			
							      			<!-- Edit -->
							      			<th><spring:message code="edit"/></th>
							      			<!-- Delete -->
							      			<th><spring:message code="delete"/></th>
							      										   				
							   				</tr>							   				
							   			<form:form modelAttribute="UserManagementForm" method="post" action="usermanagement.html">	
						   					<c:forEach items="${users}" var="user">
											<tr>											
												<td><form:input path="user[${user.userid}]"	value="${user.name}"/></td>																						
												<td><form:input path="password[${user.userid}]"	value="${user.password}"/></td>											
												<td>																						
													<form:select path="userRole[${user.userid}]" >
											 			<c:forEach items="${userGroups}" var="userGroup">
											 			 	<option  value="${userGroup.usergroupid}"> ${userGroup.name}</option>
								              			</c:forEach>		                 
													</form:select>  														
												</td>
												<td>
												<form:select path="project[${user.userid}]" >
											 		<c:forEach items="${projects}" var="project">											 		      								                   
								                           <option value="${project.name}">${project.name}</option>
								              		</c:forEach>		                 
												</form:select>  
												</td>
											<!-- Enabled -->
											
											<td align="center">	
												<c:if test="${user.enabled eq true}">
													<form:checkbox path="enabled[${user.userid}]" value="${user.enabled}" checked="true" />	
												</c:if>				
												<c:if test="${user.enabled eq false}">
													<form:checkbox path="enabled[${user.userid}]" value="${user.enabled}"/>	
												</c:if>
											</td>	
												
											<!-- Edit -button -->
											<td>	
												<a href="<c:url value='edituser.html?userid=${user.userid}'/>">
													<button align="right" type="button" value="Edit">
													<spring:message code="edit"/></button>
												</a>
											</td>
											
											<!-- Delete -button -->
			   								<td>
			   									<a href="<c:url value='deleteuser.html?userid=${user.userid}'/>"
			   									onclick="return confirm('<spring:message code="confirm_delete_user"/>')">
													<button align="right" type="button" value="Delete">
													<spring:message code="delete"/></button>
												</a>
											</td>
									   	</tr>
										</c:forEach>
											<input type="submit" value="update"></input>
										</form:form>							
							      	</table>
					      		</td>
					      	</tr>
					      	<tr height="0">
					      		<td></td>
					      	</tr>
							<tr>
								<td align="right">
									
								
									<!-- Create user -button -->
				      				<a href="createuser.html"><button type="button">
				      				<spring:message code="create_user"/></button></a>
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