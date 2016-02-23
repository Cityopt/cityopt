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
	// CheckboxValues
	
	
    $(document).ready(function() {
      $('#myCheckbox').attr('checked', true);
    });    
    </script>
</head>
<body>
	<table cellspacing="0" cellpadding="0">
		<tr>
			<td><%@include file='mainmenu.inc'%></td>
			<td width="30"></td>
			<td valign="top">
				<table>
					<tr>
						<td valign="top">
							<table>
								<tr>
									<!-- Users -->
									<td><h2>
											<spring:message code="usermanagement" />
										</h2>
									</td>
								</tr>
								<tr>
									<td>
										<table class="tablestyle">
											<col style="width: 50px">
											<col style="width: 50px">
											<col style="width: 50px">
											<col style="width: 50px">
											<col style="width: 90px">
											<col style="width: 50px">
												
											<tr>
												<!-- Username -->
												<th><spring:message code="username" /></th>
												<!-- Password -->
												<th><spring:message code="password" /></th>										
												<!-- Enabled -->
												<th><spring:message code="enabled" /></th>
												
												<!-- Edit -->
												<th><spring:message code="edit" /></th>
												<!-- Edit roles -->
												<th>Edit roles</th>
												<!-- Delete -->
												<th><spring:message code="delete" /></th>
											</tr>
											 				
											<c:forEach items="${users}" var="user">													
												<tr>
													<td>${user.name}</td>
													<td>${user.password}</td>
													<td align="center">
														<c:choose>
															<c:when test="${user.enabled eq true}">
																TRUE
															</c:when>
															<c:otherwise>
																FALSE
															</c:otherwise>
														</c:choose>
													</td>
																	
													<td>
														<a href="<c:url value='edituser.html?userid=${user.userid}'/>">
															<button align="right" type="button" value="Edit">
																<spring:message code="edit" />
															</button>
														</a>
													</td>

													<td>
														<a href="<c:url value='editroles.html?userid=${user.userid}'/>">
															<button align="right" type="button" value="Edit">
																Edit roles
															</button>
														</a>
													</td>

													<!-- Delete -button -->
													<td>
														<a href="<c:url value='deleteuser.html?userid=${user.userid}'/>"
														onclick="return confirm('<spring:message code="confirm_delete_user"/>')">
															<button align="right" type="button" value="Delete">
																<spring:message code="delete" />
															</button>
														</a>
													</td>
												</tr>
											</c:forEach>
											
											<c:if test="${bindingError eq true}">
												<tr><spring:message code="error" /></tr>
											</c:if>
										</table>							
											
										<tr height="0">
											<td></td>
										</tr>
										<tr>
											<td align="right">
												<!-- Create user -button --> <c:set var="update">
													<spring:message code="update" />
												</c:set>
												<a href="createuser.html"><button type="button">
												<spring:message code="create_user" />
												</button></a>
											</td>
										</tr>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</body>
</html>