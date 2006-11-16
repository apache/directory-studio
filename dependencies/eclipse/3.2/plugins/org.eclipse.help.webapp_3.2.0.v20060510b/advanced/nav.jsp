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
	border-<%=isRTL?"right":"left"%>:1px solid ThreeDShadow;
	background:<%=prefs.getToolbarBackground()%>;
}
<% 
} else {
%>
FRAMESET {
	border-top:1px solid ThreeDShadow;
	border-left:1px solid ThreeDShadow;
	border-right:1px solid ThreeDShadow;
}
<%
}
%>
</style>

<script language="JavaScript">
var isMozilla = navigator.userAgent.indexOf('Mozilla') != -1 && parseInt(navigator.appVersion.substring(0,1)) >= 5;
var isMozilla10 = isMozilla && navigator.userAgent.indexOf('rv:1') != -1;
var isIE = navigator.userAgent.indexOf('MSIE') != -1;

/**
 * Views can call this to set the title on the content toolbar
 */
function setContentToolbarTitle(title)
{
	if(parent.ContentFrame.ContentToolbarFrame && parent.ContentFrame.ContentToolbarFrame.setTitle ){
		parent.ContentFrame.ContentToolbarFrame.setTitle(title);
	}
}

/**
 * Shows specified view. Called from actions that switch the view
 */
function showView(view)
{
	// Note: assumes the same id shared by tabs and iframes
	ViewsFrame.showView(view);
	TabsFrame.showTab(view);
}

var temp;
var tempActiveId;
var tempView = "";

/**
 * Shows the TOC frame, loads appropriate TOC, and selects the topic
 */
function displayTocFor(topic)
{
	tempView = ViewsFrame.lastView;
	
	/******** HARD CODED VIEW NAME *********/
	showView("toc");
	
	var tocView = ViewsFrame.toc.tocViewFrame;

	if (tocView.selectTopic && tocView.selectTopic(topic))
		return;
	else {
		// save the current navigation, so we can retrieve it when synch does not work
		saveNavigation();

		var advIndex=window.location.href.indexOf("/advanced/nav.jsp");
		if(advIndex < 0)
			return;
		var tocURL = window.location.href.substr(0, advIndex) + "/advanced/tocView.jsp";
		tocView.location.replace(tocURL + "?topic="+topic+"&synch=yes");			
	}
}

function saveNavigation()
{
	/**** HARD CODED VIEW NAME *********/
	var tocView = ViewsFrame.toc.tocViewFrame;
	
	if (tocView.oldActive) {
		tempActiveId = tocView.oldActive.id;
		tocView.oldActive.className = tocView.oldActiveClass;
		tocView.oldActive = null;
	}
		
	if (isIE)
		temp = tocView.document.body.innerHTML;
	else if (isMozilla)
		temp = tocView.document.documentElement.innerHTML;
}

function restoreNavigation(errorMessage)
{	
	// switch to saved view
	showView(tempView);

	/**** HARD CODED VIEW NAME *********/	
	var tocView = ViewsFrame.toc.tocViewFrame;

	if (temp && (isIE || isMozilla10)) {
		// Restore old navigation
		if (isIE) {
			tocView.document.body.innerHTML = temp;
		} else if (isMozilla10) {
			tocView.document.documentElement.innerHTML = temp;
		}
		if (tempActiveId){
			tocView.selectTopicById(tempActiveId);
		}
	} else {
		// fail back case
		tocView.location.replace("tocView.jsp");
	}
	window.status=errorMessage;	
}
</script>
</head>

<frameset onload="showView('<%=data.getVisibleView()%>')" id="navFrameset" rows="*,21"  framespacing="0" border="0"  frameborder="0" scrolling="no">
   <frame name="ViewsFrame" title="<%=ServletResources.getString("ignore", "ViewsFrame", request)%>" src='<%="views.jsp"+data.getQuery()%>' marginwidth="0" marginheight="0" scrolling="no" frameborder="0" resize=yes>
   <frame name="TabsFrame" title="<%=ServletResources.getString("TabsFrame", request)%>" src='<%="tabs.jsp"+data.getQuery()%>' marginwidth="0" marginheight="0" scrolling="no" frameborder="0" noresize>
</frameset>

</html>