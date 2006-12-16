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

<title><%=ServletResources.getString("Content", request)%></title>

<style type="text/css">
<%@ include file="tree.css"%>
</style>  
    
<base target="ContentViewFrame">
<script language="JavaScript">

// Preload images
minus = new Image();
minus.src = "<%=prefs.getImagesDirectory()%>"+"/minus.gif";
plus = new Image();
plus.src = "<%=prefs.getImagesDirectory()%>"+"/plus.gif";
toc_open_img = new Image();
toc_open_img.src = "<%=prefs.getImagesDirectory()%>"+"/toc_open.gif";
toc_closed_img = new Image();
toc_closed_img.src = "<%=prefs.getImagesDirectory()%>"+"/toc_closed.gif";
folder_img = new Image();
folder_img.src = "<%=prefs.getImagesDirectory()%>"+"/container_obj.gif";
topic_img = new Image();
topic_img.src = "<%=prefs.getImagesDirectory()%>"+"/topic.gif";
altTopicClosed = "<%=UrlUtil.JavaScriptEncode(ServletResources.getString("topicClosed", request))%>";
altTopicOpen = "<%=UrlUtil.JavaScriptEncode(ServletResources.getString("topicOpen", request))%>";
</script>

<script language="JavaScript" src="toc.js"></script>
<script language="JavaScript"> 
 
/**
 * Loads the specified table of contents
 */		
function loadTOC(tocHref)
{
	// navigate to this toc, if not already loaded
	if (window.location.href.indexOf("tocView.jsp?toc="+tocHref) != -1)
		return;
	window.location.replace("tocView.jsp?toc="+tocHref);
}

var tocTitle = "";
var tocId = "";
	
function onloadHandler()
{
<%
	if (data.getSelectedToc() != -1)
	{
%>
	tocTitle = '<%=UrlUtil.JavaScriptEncode(data.getTocLabel(data.getSelectedToc()))%>';
	var tocTopic = "<%=data.getTocDescriptionTopic(data.getSelectedToc())%>";
	
	// set title on the content toolbar
	parent.parent.parent.setContentToolbarTitle(tocTitle);
		
	var topicSelected=false;
	// select specified topic, or else the book
	var topic = "<%=data.getSelectedTopic()%>";
	if (topic != "about:blank" && topic != tocTopic) {
		if (topic.indexOf(window.location.protocol) != 0 && topic.length > 2) {
			// remove the .. from topic
			topic = topic.substring(2);
			// remove advanced/tocView.jsp from path to obtain contextPath
			var contextPath = window.location.pathname;
			var slash = contextPath.lastIndexOf('/');
			if(slash > 0) {
				slash = contextPath.lastIndexOf('/', slash-1);
				if(slash >= 0) {
					contextPath = contextPath.substr(0, slash);
					topic = window.location.protocol + "//" +window.location.host + contextPath + topic;
				}
			}			
		}
		topicSelected = selectTopic(topic);
	} else {
		topicSelected = selectTopicById(tocId);
	}
<%
	// if topic failed to be selected, but we know it exist in some book,
	// offer to turn on "show all"
	
	// do not offer to show all just after it was manually turned off
	if (null==request.getParameter("showAll")) {
%>
	if(!topicSelected){
		if(parent.parent.activityFiltering){
			askShowAll();
		}
	}
<%
	}
%>
<%
	} else if ("yes".equals(request.getParameter("synch"))) {
%>
	var message='<%=UrlUtil.JavaScriptEncode(ServletResources.getString("CannotSync", request))%>';
	// when we don't find the specified toc, we just restore navigation
	parent.parent.parent.restoreNavigation(message);
<%
	}
%>
	focusHandler("e");
}

var askShowAllDialog;
var w = 470;
var h = 270;

function askShowAll(){
<%
if (data.isIE()){
%>
	var l = top.screenLeft + (top.document.body.clientWidth - w) / 2;
	var t = top.screenTop + (top.document.body.clientHeight - h) / 2;
<%
} else {
%>
	var l = top.screenX + (top.innerWidth - w) / 2;
	var t = top.screenY + (top.innerHeight - h) / 2;
<%
}
%>
	// move the dialog just a bit higher than the middle
	if (t-50 > 0) t = t-50;
	
	window.location="javascript://needModal";
	askShowAllDialog = window.open("askShowAll.jsp", "askShowAllDialog", "resizeable=no,height="+h+",width="+w+",left="+l+",top="+t );
	askShowAllDialog.focus(); 
}

function yesShowAll(){
	window.parent.parent.showAll();
}

function closeAskShowAllDialog(){
	try {
		if (askShowAllDialog){
			askShowAllDialog.close();
		}
	}
	catch(e) {}
}

function onunloadHandler() {
	closeAskShowAllDialog();
<%
// for large books, we want to avoid a long unload time
if (data.isIE()){
%>
	document.body.innerHTML = "";
<%
}
%>
}

</script>
</head>


<body dir="<%=direction%>" onload="onloadHandler()" onunload="onunloadHandler()">
	<ul dir="<%=direction%>" class='expanded' id='root'>
<%
	for (int toc=0; toc<data.getTocCount(); toc++) {
		boolean isSelected =data.getSelectedToc() != -1 &&
					   data.getTocHref(data.getSelectedToc()).equals(data.getTocHref(toc));
		if(!data.isEnabled(toc)){
			// do not show
			continue;
		}
		if(isSelected) {
%>
		<li>
		<img src="<%=prefs.getImagesDirectory()%>/toc_open.gif" alt="<%=ServletResources.getString("bookOpen", request)%>"><a id="b<%=toc%>" name="opened" style="font-weight: bold;" href="<%=data.getTocDescriptionTopic(toc)%>" onclick=''><%=data.getTocLabel(toc)%></a>
<%
			// Only generate the selected toc
			data.generateToc(toc, out);
			// keep track of the selected toc id
%>
			<script language="JavaScript">tocId="b"+<%=toc%></script>
<%
		} else {
%>
		<li>
		<img src="<%=prefs.getImagesDirectory()%>/toc_closed.gif" alt="<%=ServletResources.getString("bookClosed", request)%>"><a id="b<%=toc%>" name="<%=data.getTocHref(toc)%>" style="font-weight: bold;" href="<%=data.getTocDescriptionTopic(toc)%>" onclick='loadTOC("<%=data.getTocHref(toc)%>")'><%=data.getTocLabel(toc)%></a>
<%
		}
%>
		</li>	
<%
	}
%>		
	</ul>
   <iframe name="dynLoadFrame" title="<%=ServletResources.getString("ignore", "dynLoadFrame", request)%>" style="visibility:hidden" tabindex="-1" frameborder="no" width="0" height="0" scrolling="no">
    </iframe>
</body>
</html>

