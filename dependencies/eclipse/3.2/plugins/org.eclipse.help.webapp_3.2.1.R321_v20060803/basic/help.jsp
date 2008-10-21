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
%>

<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><%=ServletResources.getString("Help", request)%></title>

</head>

<frameset cols="<%=isRTL?"*,300":"300,*"%>">
<%
if (isRTL) {
%>
	<frame name="ContentViewFrame" title="<%=ServletResources.getString("aView", ServletResources.getString("topic", request), request)%>" src='<%=data.getContentURL()%>' marginwidth="5" marginheight="5">
	<frame name="ViewsFrame" title="<%=ServletResources.getString("ignore", "ViewsFrame", request)%>" src='<%="view.jsp?view="+data.getVisibleView()+"&"+request.getQueryString()%>' marginwidth="0" marginheight="0" scrolling="no">
<%
} else {
%>
	<frame name="ViewsFrame" title="<%=ServletResources.getString("ignore", "ViewsFrame", request)%>" src='<%="view.jsp?view="+data.getVisibleView()+"&"+request.getQueryString()%>' marginwidth="0" marginheight="0" scrolling="no">
	<frame name="ContentViewFrame" title="<%=ServletResources.getString("aView", ServletResources.getString("topic", request), request)%>" src='<%=data.getContentURL()%>' marginwidth="5" marginheight="5">
<%
}
%>
</frameset>

</html>

