				<tr align="right" style="height: 60px">
					<td colspan="2">
						<table class="info">
							<col style="width:430px">	
							<col style="width:130px">	
							<col style="width:260px">	
							<tr>
								<td></td>
								<td></td> 
								<td align="right"><%= ((org.springframework.security.core.userdetails.UserDetails)org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername() %>&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;<a href="logout.html"><spring:message code="logout"/></a>&nbsp;&nbsp;&nbsp;&nbsp;</td>
							</tr>
						</table>
					</td>
				</tr>
				
