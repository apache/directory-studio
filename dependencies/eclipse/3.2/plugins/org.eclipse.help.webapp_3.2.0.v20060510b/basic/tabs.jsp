<%--
 Copyright (c) 2000, 2004 IBM Corporation and others.
 All rights reserved. This program and the accompanying materials 
 are made available under the terms of the Eclipse Public License v1.0
 which accompanies this distribution, and is available at
 http://www.eclipse.org/legal/epl-v10.html
 
 Contributors:
     IBM Corporation - initial API and implementation
--%>
<%@ include file="header.jsp"%>
<% 
	LayoutData data = new LayoutData(application,request, response);
	WebappPreferences prefs = data.getPrefs();
	View[] views = data.getViews();
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title><%=ServletResources.getString("Tabs", request)%></title>
    
<base target="ViewsFrame">
<SCRIPT TYPE="text/javascript">
<!--
function resynch()
{
		var topic = parent.HelpFrame.ContentViewFrame.window.location.href;
		// remove the query, if any
		var i = topic.indexOf('?');
		if (i != -1)
			topic = topic.substring(0, i);
		// remove the fragment, if any
		var i = topic.indexOf('#');
		if (i != -1)
			topic = topic.substring(0, i);
		parent.HelpFrame.ViewsFrame.location="view.jsp?view=toc&topic="+topic;
}
//-->
</SCRIPT>
</head>
   
<body dir="<%=direction%>" bgcolor="<%=prefs.getBasicToolbarBackground()%>" link="#0000FF" vlink="#0000FF" alink="#0000FF">
	<table align="<%=isRTL?"right":"left"%>" border="0" cellpadding="0" cellspacing="0">
	<tr>

<%
	for (int i=0; i<views.length; i++) 
	{
		// do not show booksmarks view
		if("bookmarks".equals(views[i].getName())){
			continue;
		}
		
		// search view is not called "advanced view"
		String title = ServletResources.getString(views[i].getName(), request);
		if("search".equals(views[i].getName())){
			title=ServletResources.getString("Search", request);
		}
		
		String viewHref="view.jsp?view="+views[i].getName();
		// always pass query string to "links view"
		if("links".equals(views[i].getName())){
			viewHref=viewHref+(request.getQueryString()!=null?"&"+request.getQueryString():"");
		}
		
%>
		<td nowrap>
		<b>
		<a  href='<%=viewHref%>' > 
	         <img alt="<%=title%>" 
	              title="<%=title%>" 
	              src="<%=views[i].getOnImage()%>" border=0>
	         
	     <%=title%>
	     </a>
	     &nbsp;
		</b>
	     </td>
<%
	}
%>
	</tr>
	</table>

<SCRIPT TYPE="text/javascript">
<!--
document.write("<table align=\"<%=isRTL?"left":"right"%>\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr><td nowrap><b><a  href='javascript:parent.parent.TabsFrame.resynch();' >"); 
document.write("<img alt=\"\" title=\"<%=ServletResources.getString("Synch", request)%>\" src=\"images/e_synch_toc_nav.gif\" border=0> ");
document.write("<%=ServletResources.getString("shortSynch", request)%></a>&nbsp;</b></td></tr></table>");
//-->
</SCRIPT>

	<iframe name="liveHelpFrame" title="<%=ServletResources.getString("ignore", "liveHelpFrame", request)%>" frameborder="no" width="0" height="0" scrolling="no">
	<layer name="liveHelpFrame" frameborder="no" width="0" height="0" scrolling="no"></layer>
	</iframe>
</body>
</html>

