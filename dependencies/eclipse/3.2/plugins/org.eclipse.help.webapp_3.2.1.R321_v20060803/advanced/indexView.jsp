<%--
 Copyright (c) 2005, 2006 Intel Corporation and others.
 All rights reserved. This program and the accompanying materials 
 are made available under the terms of the Eclipse Public License v1.0
 which accompanies this distribution, and is available at
 http://www.eclipse.org/legal/epl-v10.html
 
 Contributors:
     Intel Corporation - initial API and implementation
--%>
<%@ include file="fheader.jsp"%>

<%
	RequestData data = new RequestData(application, request, response);
	WebappPreferences prefs = data.getPrefs();
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title><%=ServletResources.getString("IndexViewTitle", request)%></title>

<script language="JavaScript" src="indexView.js"></script>
</head>

<frameset id="indexViewFrameset"
<%if (prefs.isIndexInstruction()){%>
		rows="52,*"
<%} else {%>
		rows="32,*"
<%}%>
		frameborder="0" framespacing="0" border="0">
	<frame name="IndexTypeinFrame" src="indexTypein.jsp" title='<%=ServletResources.getString("IndexTypeinTitle", request)%>' frameborder="0" marginheiht="0" marginwidth="0" noresize scrolling="no">
	<frame name="IndexListFrame" src="indexList.jsp" title='<%=ServletResources.getString("IndexListTitle", request)%>' frameborder="0" marginheiht="0" marginwidth="0">
</frameset>

</html>
