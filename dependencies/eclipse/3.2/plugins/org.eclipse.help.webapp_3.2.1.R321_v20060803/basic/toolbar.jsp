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
	ToolbarData data = new ToolbarData(application,request, response);
	WebappPreferences prefs = data.getPrefs();
%>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title><%=ServletResources.getString("Toolbar", request)%></title>
</head>
 
<body dir="<%=direction%>" bgcolor="<%=prefs.getBasicToolbarBackground()%>">
<%
	String title=data.getTitle();
	// search view is not called "advanced view"
	if("search".equals(request.getParameter("view"))){
		title=ServletResources.getString("Search", request);
	}
%>
	<b>
	<%=title%>
	</b>

</body>     
</html>

