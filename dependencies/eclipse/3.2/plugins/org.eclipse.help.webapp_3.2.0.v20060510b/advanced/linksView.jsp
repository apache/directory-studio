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
	LinksData data = new LinksData(application, request, response);
	WebappPreferences prefs = data.getPrefs();
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Expires" content="-1">

<title><%=ServletResources.getString("Links", request)%></title>

<style type="text/css">
<%@ include file="list.css"%>
</style>

<base target="ContentViewFrame">
<script language="JavaScript" src="list.js"></script>

</head>


<body dir="<%=direction%>">
 
<%
if(!data.isLinksRequest()) {
	out.write(ServletResources.getString("pressF1", request));
} else if (data.getLinksCount() == 0){
	out.write(ServletResources.getString("Nothing_found", null));
} else {
%>

<table id='list'  cellspacing='0' >

<%
	for (int topic = 0; topic < data.getLinksCount(); topic++) 
	{
%>

<tr class='list' id='r<%=topic%>'>
	<td align='<%=isRTL?"right":"left"%>' class='label' nowrap>
		<a id='a<%=topic%>' 
		   href='<%=data.getTopicHref(topic)%>' 
		   onmouseover="showStatus(event);return true;"
		   onmouseout="clearStatus();return true;"
		   onclick='parent.parent.parent.setContentToolbarTitle(this.title)'
		   title="<%=data.getTopicTocLabel(topic)%>">
		   <img src="<%=prefs.getImagesDirectory()%>/topic.gif" alt=""><%=data.getTopicLabel(topic)%></a>
	</td>
</tr>

<%
	}
%>

</table>

<%

}

%>

<script language="JavaScript">
	selectTopicById('<%=data.getSelectedTopicId()%>');
</script>

</body>
</html>
