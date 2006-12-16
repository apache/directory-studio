<%--
 Copyright (c) 2000, 2006 IBM Corporation and others.
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
	WebappPreferences prefs = data.getPrefs();
	View[] views = data.getViews();
%>	


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<title><%=ServletResources.getString("Views", request)%></title>

<style type="text/css">

/* need this one for Mozilla */
HTML { 
	width:100%;
	height:100%;
	margin:0px;
	padding:0px;
	border:0px;
 }

BODY {
	margin:0px;
	padding:0px;
	/* Mozilla does not like width:100%, so we set height only */
	height:100%;
}

IFRAME {
	width:100%;
	height:100%;
}

.hidden {
	visibility:hidden;
	width:0;
	height:0;
}

.visible {
	visibility:visible;
	width:100%;
	height:100%;
}

</style>

<script language="Javascript">

var lastView = "";
/**
 * Switches to specified view
 */
function showView(view)
{ 	
	if (view == lastView) 
		return;
		
	lastView = view;
       	
	// show appropriate frame
 	var iframes = parent.ViewsFrame.document.body.getElementsByTagName("IFRAME");
 	for (var i=0; i<iframes.length; i++)
 	{			
  		if (iframes[i].id != view){
   			iframes[i].className = "hidden";
   			iframes[i].style.visibility="hidden";
  		}else{
   			iframes[i].className = "visible";
   			iframes[i].style.visibility="visible";
   		}
 	}
}

var activityFiltering = <%=(new ActivitiesData(application, request, response)).isActivityFiltering()?"true":"false"%>;
var displayShowAllConfirmation = <%=prefs.isDontConfirmShowAll()?"false":"true"%>;
var regExp=/&(showAll|synch)=(on|off|yes|no)/gi;
function toggleShowAll(){
	if(activityFiltering){
		if( displayShowAllConfirmation ){
			confirmShowAll();
		}else{
			showAll();
		}
	} else {
		dontShowAll();
	}
}

function dontAskAgain(){
	displayShowAllConfirmation = false;
}
function showAll(){
	var displayConfirmParam;
	if(displayShowAllConfirmation){
		displayConfirmParam="";
	}else{
		displayConfirmParam="&showconfirm=false";
	}
	activityFiltering=false;
	try{
		window.frames.toc.tocToolbarFrame.setButtonState("show_all", true);
	}catch(ex) {}
	try{
		window.frames.index.indexToolbarFrame.setButtonState("show_all", true);
	}catch(ex) {}
	try{
		window.frames.search.searchToolbarFrame.setButtonState("show_all", true);
	}catch(ex) {}
	try{
		window.frames.toc.tocViewFrame.location.replace(window.frames.toc.tocViewFrame.location.href.replace(regExp, "")+"&showAll=on"+displayConfirmParam);
	}catch(ex) {}
	try{
		window.frames.index.indexViewFrame.location.replace(window.frames.index.indexViewFrame.location.href.replace(regExp, "")+"&showAll=on"+displayConfirmParam);
	}catch(ex) {}
	try{
		window.frames.search.searchViewFrame.location.replace(window.frames.search.searchViewFrame.location.href.replace(regExp, "")+"&showAll=on");
	}catch(ex) {}
}

function dontShowAll(){
	activityFiltering=true;
	try{
		window.frames.toc.tocToolbarFrame.setButtonState("show_all", false);
	}catch(ex) {}
	try{
		window.frames.index.indexToolbarFrame.setButtonState("show_all", false);
	}catch(ex) {}
	try{
		window.frames.search.searchToolbarFrame.setButtonState("show_all", false);
	}catch(ex) {}
	try{
		window.frames.toc.tocViewFrame.location.replace(window.frames.toc.tocViewFrame.location.href.replace(regExp, "")+"&showAll=off");
	}catch(ex) {}
	try{
		window.frames.index.indexViewFrame.location.replace(window.frames.index.indexViewFrame.location.href.replace(regExp, "")+"&showAll=off");
	}catch(ex) {}
	try{
		window.frames.search.searchViewFrame.location.replace(window.frames.search.searchViewFrame.location.href.replace(regExp, "")+"&showAll=off");
	}catch(ex) {}
}

var confirmShowAllDialog;
var w = 470;
var h = 240;

function confirmShowAll()
{
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
	confirmShowAllDialog = window.open("confirmShowAll.jsp", "confirmShowAllDialog", "resizeable=no,height="+h+",width="+w+",left="+l+",top="+t );
	confirmShowAllDialog.focus(); 
}

function closeConfirmShowAllDialog(){
	try {
		if (confirmShowAllDialog){
			confirmShowAllDialog.close();
		}
	}
	catch(e) {}
}

</script>

</head>
   
<body dir="<%=direction%>" tabIndex="-1" onunload="closeConfirmShowAllDialog()">
<%
	for (int i=0; i<views.length; i++) 
	{
		// normally we would hide the views first, but mozilla needs all iframes to be visible to load 
		// other frames
		String className =  data.getVisibleView().equals(views[i].getName()) || data.isMozilla() ? "visible" : "hidden";
%>
 	<iframe frameborder="0" 
 		    class="<%=className%>"  
 		    name="<%=views[i].getName()%>"
 		    title="<%=ServletResources.getString("ignore", views[i].getName(), request)%>"
 		    id="<%=views[i].getName()%>" 
 		    src='<%="view.jsp?view="+views[i].getName()+(request.getQueryString()==null?"":("&"+request.getQueryString()))%>'>
 	</iframe> 
<%
	}
%>	

 <iframe frameborder="0" style="visibility:hidden" tabindex="-1" name="temp" id="temp" title="<%=ServletResources.getString("ignore", "temp", request)%>"></iframe>
 
</body>
</html>

