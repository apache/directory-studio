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
	IndexData data = new IndexData(application, request, response);
	WebappPreferences prefs = data.getPrefs();
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title><%=ServletResources.getString("IndexListTitle", request)%></title>

<style type="text/css">
<%@ include file="indexList.css"%>
</style>  
    
<base target="ContentViewFrame">

<script language="JavaScript">
var ids = [<%data.generateIds(out);%>];
minus = new Image();
minus.src = "<%=prefs.getImagesDirectory()%>" + "/minus.gif";
plus = new Image();
plus.src = "<%=prefs.getImagesDirectory()%>" + "/plus.gif";
altExpandTopicTitles = "<%=UrlUtil.JavaScriptEncode(ServletResources.getString("expandTopicTitles", request))%>";
altCollapseTopicTitles = "<%=UrlUtil.JavaScriptEncode(ServletResources.getString("collapseTopicTitles", request))%>";
usePlusMinus = <%=prefs.isIndexPlusMinus()%>;
</script>
<script language="JavaScript" src="indexList.js"></script>

</head>
<body dir="<%=direction%>" onload="onloadHandler()">
	<ul dir="<%=direction%>" id="root" class="expanded">
<%
		data.generateIndex(out);
%>
	</ul>
</body>
</html>
