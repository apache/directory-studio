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
	TocData data = new TocData(application,request, response);
	WebappPreferences prefs = data.getPrefs();
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><%=ServletResources.getString("partialContent", request)%></title>
<script language="JavaScript">
plus = new Image();
plus.src = "<%=prefs.getImagesDirectory()%>"+"/plus.gif";
folder_img = new Image();
folder_img.src = "<%=prefs.getImagesDirectory()%>"+"/container_obj.gif";
topic_img = new Image();
topic_img.src = "<%=prefs.getImagesDirectory()%>"+"/topic.gif";

function onloadHandler()
{
	var fragment = null;
	for (var i=0; i < document.body.childNodes.length; i++)
		if (document.body.childNodes[i].nodeName == "UL"){
			fragment = document.body.childNodes[i];
			break;
		}
	if(fragment == null) return;
	var path = fragment.id;
	var oldFragment = window.parent.document.getElementById(path);
	oldFragment.innerHTML = fragment.innerHTML;
	oldFragment.id = "";
}
</script>
</head>
<body dir="<%=direction%>" onload="onloadHandler()">
<%
		int toc=data.getSelectedToc();
		// Only generate the selected toc
		if (toc != -1 && data.getTocHref(data.getSelectedToc()).equals(data.getTocHref(toc)))
		{
			data.generateToc(toc, out);
		}
%>
</body>
</html>
