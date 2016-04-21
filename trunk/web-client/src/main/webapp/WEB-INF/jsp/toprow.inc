				<tr align="right" style="height: 60px">
					<td colspan="2">
						<table class="info">
							<col style="width:450px">	
							<col style="width:160px">	
							<col style="width:210px">	
							<tr>
								<td></td>
								<td>Language:&nbsp;${usersession.getLanguage()}&nbsp;(<a href="settings.html">Change</a>)</td> 
								<td align="right"><spring:message code="user"/>: <%= ((org.springframework.security.core.userdetails.UserDetails)org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername() %>&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;<a href="logout.html"><spring:message code="logout"/></a>&nbsp;&nbsp;&nbsp;&nbsp;</td>
							</tr>
						</table>
					</td>
				</tr>
				
