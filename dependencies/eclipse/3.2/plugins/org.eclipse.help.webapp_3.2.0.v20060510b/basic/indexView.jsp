<%--
 Copyright (c) 2006 Intel Corporation and others.
 All rights reserved. This program and the accompanying materials 
 are made available under the terms of the Eclipse Public License v1.0
 which accompanies this distribution, and is available at
 http://www.eclipse.org/legal/epl-v10.html
 
 Contributors:
     Intel Corporation - initial API and implementation
--%>
<%@ include file="header.jsp"%>
<% 
	IndexData data = new IndexData(application, request, response);
	WebappPreferences prefs = data.getPrefs();
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title><%=ServletResources.getString("Content", request)%></title>

<base target="ContentViewFrame">
</head>


<body dir="<%=direction%>" bgcolor="<%=prefs.getBasicViewBackground()%>">

<table border="0" cellpadding="0" cellspacing="0">
<%
		data.generateBasicIndex(out);
%>
</table>

</body>
</html>

