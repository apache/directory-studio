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
<title><%=data.getWindowTitle()%></title>
<jsp:include page="livehelp_js.jsp"/>
</head>

<frameset rows="<%="0".equals(data.getBannerHeight())?"":data.getBannerHeight()+","%>45,*">
<%
	if(!("0".equals(data.getBannerHeight()))){
%>
	<frame name="BannerFrame" title="<%=ServletResources.getString("Banner", request)%>" src='<%=data.getBannerURL()%>'  marginwidth="0" marginheight="0" scrolling="no" frameborder="no" noresize>
<%
	}
%>
	<frame name="TabsFrame" title="<%=ServletResources.getString("helpToolbarFrame", request)%>" src='<%="basic/tabs.jsp"+data.getQuery()%>' marginwidth="5" marginheight="5" scrolling="no">
	<frame name="HelpFrame" title="<%=ServletResources.getString("ignore", "HelpFrame", request)%>" src='<%="basic/help.jsp"+data.getQuery()%>' frameborder="no" marginwidth="0" marginheight="0" scrolling="no">
</frameset>

</html>

