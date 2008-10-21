<%--
 Copyright (c) 2005, 2006 Intel Corporation and others.
 All rights reserved. This program and the accompanying materials 
 are made available under the terms of the Eclipse Public License v1.0
 which accompanies this distribution, and is available at
 http://www.eclipse.org/legal/epl-v10.html
 
 Contributors:
     Intel Corporation - initial API and implementation
--%>
<%@ include file="header.jsp"%>

<%
	RequestData data = new RequestData(application, request, response);
	WebappPreferences prefs = data.getPrefs();
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title><%=ServletResources.getString("IndexTypeinTitle", request)%></title>

<style type="text/css">
<%@ include file="indexTypein.css"%>
</style>

<script language="JavaScript" src="indexTypein.js"></script>

</head>

<body dir="<%=direction%>" onload="onloadHandler()">

<table>
<%if (prefs.isIndexInstruction()) {%>
	<tr>
		<td colspan="2"><p id="instruction"><%=ServletResources.getString("IndexTypeinInstructions", request)%></p></td>
	</tr>
<%}%>
	<tr>
		<td width="100%"><input type="text" id="typein"></td>
	<%if (prefs.isIndexButton()) {%>
		<td><input type="button" id="button" value="<%=ServletResources.getString("IndexTypeinButton", request)%>" onclick="this.blur();parent.doDisplay()"></td>
	<%}%>
	</tr>
</table>

</body>
</html>
