<%--
 Copyright (c) 2000, 2004 IBM Corporation and others.
 All rights reserved. This program and the accompanying materials 
 are made available under the terms of the Eclipse Public License v1.0
 which accompanies this distribution, and is available at
 http://www.eclipse.org/legal/epl-v10.html
 
 Contributors:
     IBM Corporation - initial API and implementation
--%>
<form action="searchView.jsp" method="get" accept-charset="UTF-8" target="_self">

	<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td>
				<label for="searchWord"><%=ServletResources.getString("SearchExpression", request)%></label>
			</td>
		</tr>
		<tr>
			<td nowrap>
				<input type="text" name="searchWord" id="searchWord" value='<%=data.getSearchWord()%>' maxlength=256 alt='<%=ServletResources.getString("SearchExpression", request)%>' title='<%=ServletResources.getString("SearchExpression", request)%>'>
          	  	<input type="hidden" name="maxHits" value="500" >
          	  	<input type="hidden" name="scopedSearch" value="true" >
				<input type="submit" value='<%=ServletResources.getString("GO", request)%>' alt='<%=ServletResources.getString("GO", request)%>' title='<%=ServletResources.getString("GO", request)%>'>
        	</td>
        </tr>
        <tr>
        	<td>
        		<%=ServletResources.getString("expression_label", request)%>
        	</td>
        </tr>
		<tr>
			<td>
				<hr>
			</td>
		</tr>
    	<tr>
  			<td>
  				<b>
				<%=ServletResources.getString("Select", request)%>
				</b>
			</td>
		</tr>
  				
<% 
TocData tocData = new TocData(application, request, response);
for (int toc=0; toc<tocData.getTocCount(); toc++)
{
		//if(!tocData.isEnabled(toc)){
			// do not show
		//	continue;
		//}
	String label = tocData.getTocLabel(toc);
	String checked="checked=\"yes\" ";
	if( data.isSearchRequest() && !data.isTocSelected(toc) ){
		checked="";
	}
%>
  		<tr>
  			<td nowrap>
				<input type="checkbox" name='scope' id="checkbox<%=toc%>" value='<%=tocData.getTocHref(toc)%>' <%=checked%> alt="<%=label%>"><label for="checkbox<%=toc%>"><%=label%></label>
			</td>
		</tr>
<%
}		
%>
	</table>
 </form>
