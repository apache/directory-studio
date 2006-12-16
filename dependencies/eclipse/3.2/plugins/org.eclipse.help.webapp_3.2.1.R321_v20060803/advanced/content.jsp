<%--
 Copyright (c) 2000, 2004 IBM Corporation and others.
 All rights reserved. This program and the accompanying materials 
 are made available under the terms of the Eclipse Public License v1.0
 which accompanies this distribution, and is available at
 http://www.eclipse.org/legal/epl-v10.html
 
 Contributors:
     IBM Corporation - initial API and implementation
--%>
<%@ include file="fheader.jsp"%>

<% 
	LayoutData data = new LayoutData(application,request, response);
	WebappPreferences prefs = data.getPrefs();
%>

<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><%=ServletResources.getString("Help", request)%></title>

<style type="text/css">
<% 
if (data.isMozilla()) {
%>
HTML {
	background:<%=prefs.getToolbarBackground()%>;
	border-<%=isRTL?"left":"right"%>:1px solid ThreeDShadow;
}
<% 
} else {
%>
FRAMESET {
	border-top:1px solid ThreeDShadow;
	border-left:1px solid ThreeDShadow;
	border-right:1px solid ThreeDShadow;
	border-bottom:1px solid ThreeDShadow;
}
<%
}
%>
</style>

<script language="JavaScript">

function onloadHandler(e)
{
<% if (data.isIE() || data.isMozilla() && "1.2.1".compareTo(data.getMozillaVersion()) <=0){
%>	var h=window.ContentToolbarFrame.document.getElementById("titleText").offsetHeight; <%-- default 13 --%>
	if(h<=19){
		return; <%-- no need to resize up to 19px --%>
	}
	document.getElementById("contentFrameset").setAttribute("rows", (11+h)+",*"); <%-- default 24 --%>
	window.ContentToolbarFrame.document.getElementById("titleTextTableDiv").style.height=(9+h)+"px"; <%-- default 22 --%>
<%}%>
}
</script>

</head>


<frameset id="contentFrameset" onload="onloadHandler()" rows="24,*" frameborder="0" framespacing="0" border=0 spacing=0>
	<frame name="ContentToolbarFrame" title="<%=ServletResources.getString("topicViewToolbar", request)%>" src='<%="contentToolbar.jsp"+data.getQuery()%>'  marginwidth="0" marginheight="0" scrolling="no" frameborder="0" noresize=0>
	<frame ACCESSKEY="K" name="ContentViewFrame" title="<%=ServletResources.getString("topicView", request)%>" src='<%=data.getContentURL()%>'  marginwidth="10"<%=(data.isIE() && "6.0".compareTo(data.getIEVersion()) <=0)?"scrolling=\"yes\"":""%> marginheight="0" frameborder="0" >
</frameset>

</html>

