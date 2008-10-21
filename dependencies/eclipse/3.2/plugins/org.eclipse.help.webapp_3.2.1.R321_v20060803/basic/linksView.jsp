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

<base target="ContentViewFrame">
</head>


<body dir="<%=direction%>" bgcolor="<%=prefs.getBasicViewBackground()%>">
 
<%
if(!data.isLinksRequest()) {
	out.write(ServletResources.getString("pressF1", request));
} else if (data.getLinksCount() == 0){
	out.write(ServletResources.getString("Nothing_found", null));
} else {
%>

<table border="0" cellpadding="0" cellspacing="0">

<%
	for (int link = 0; link < data.getLinksCount(); link++) 
	{
%>

<tr>
	<td align='<%=isRTL?"right":"left"%>' nowrap>
		<a href='<%=data.getTopicHref(link)%>'>
		   <img src="<%=prefs.getImagesDirectory()%>/topic.gif"  alt="" border=0>
		   <%=data.getTopicLabel(link)%>
		 </a>
	</td>
</tr>

<%
	}
%>

</table>

<%

}

%>
</body>
</html>
