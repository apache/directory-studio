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
	WorkingSetData data = new WorkingSetData(application, request, response);
	TocData tocData = new TocData(application,request, response);
	WebappPreferences prefs = data.getPrefs();
%>


<html>
<head>
<title><%=ServletResources.getString(data.isEditMode()?"EditWorkingSet":"NewWorkingSet", request)%></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Expires" content="-1">

<style type="text/css">

/* need this one for Mozilla */
HTML, BODY {
	width:100%;
	height:100%;
	margin:0px;
	padding:0px;
	border:0px;
}
 
BODY {
	font: <%=prefs.getViewFont()%>;
	background:<%=prefs.getToolbarBackground()%>;
	color: WindowText;
}

TABLE {
	font:<%=prefs.getViewFont()%>;
	background:<%=prefs.getToolbarBackground()%>;
}

TD, TR {
	margin:0px;
	padding:0px;
	border:0px;
}


#workingSet {
	width:100%;
	font:<%=prefs.getViewFont()%>;
}

#booksContainer {
	background:Window;
	color:WindowText;
	border:	2px inset ThreeDHighlight;
	margin:10px;
	margin-top:2px;
	padding-<%=isRTL?"right":"left"%>:5px;
	overflow:auto;
	height:350px;
<%if (data.isIE()) {%>
	width:100%;
<%}%>
}

.book {
	margin:0xp;
	border:0px;
	padding:0px;
	white-space: nowrap;
}

.topic {
	margin-<%=isRTL?"right":"left"%>:30px;
	border:0px;
	padding:0px;
	white-space: nowrap;
}

BUTTON {
	font:<%=prefs.getViewFont()%>;
}

.expanded {
	display:block;
}

.collapsed {
	display:none;
}

.grayed {
	background-color: <%=prefs.getToolbarBackground()%>;
}

<%
if (data.isMozilla()) {
%>
input[type="checkbox"] {
	border:2px solid WindowText; 
	margin:0xp; 
	padding:0px;	
	height:12px;
	width:12px;
}

.grayed {
	background: <%=prefs.getToolbarBackground()%>;
}
<%
}
%>
</style>

<script language="JavaScript">

// Preload images
var minus = new Image();
minus.src = "<%=prefs.getImagesDirectory()%>"+"/minus.gif";
var plus = new Image();
plus.src = "<%=prefs.getImagesDirectory()%>"+"/plus.gif";

var oldName = '<%=data.isEditMode()?data.getWorkingSetName():""%>';
var altBookClosed = "<%=UrlUtil.JavaScriptEncode(ServletResources.getString("bookClosed", request))%>";
var altBookOpen = "<%=UrlUtil.JavaScriptEncode(ServletResources.getString("bookOpen", request))%>";

function onloadHandler() {
<%if(!data.isMozilla() || "1.3".compareTo(data.getMozillaVersion()) <=0){
// buttons are not resized immediately on mozilla before 1.3
%>
	sizeButtons();
<%}%>
	document.getElementById("workingSet").focus();
	enableOK();
<%-- event handlers that call enableOK() are not invoked properly on Japanese --%>
	setInterval("enableOK()", 250);

}

function sizeButtons() {
	var minWidth=60;

	if(document.getElementById("ok").offsetWidth < minWidth){
		document.getElementById("ok").style.width = minWidth+"px";
	}
	if(document.getElementById("cancel").offsetWidth < minWidth){
		document.getElementById("cancel").style.width = minWidth+"px";
	}
}

function doSubmit()
{
	try
	{
		var workingSet = document.getElementById("workingSet").value;
		if (!workingSet || workingSet == "")
			return false;
	
		var hrefs = getSelectedResources();
		if (!hrefs || hrefs == "")
			return false;

		var query = "operation="+'<%=data.getOperation()%>'+"&workingSet="+encodeURIComponent(workingSet)+ hrefs+"&oldName="+encodeURIComponent(oldName);
		window.opener.location.replace("workingSetManager.jsp?"+query);
		window.opener.focus();
		window.close();
	} catch(ex) {alert("Error..." + ex.message)}
}

function getSelectedResources() {
	var hrefs = "";
	var inputs = document.getElementsByTagName("INPUT");
	for (var i=0; i<inputs.length; i++)
	{
		if (inputs[i].type != "checkbox") continue;
		if (inputs[i].checked == false) continue;
		if (getGrayed(inputs[i])) continue;
		if (isToc(inputs[i].name)) {
			hrefs += "&hrefs="+encodeURIComponent(inputs[i].name);
		} else if (!isParentTocSelected(inputs[i].name)) {
			hrefs += "&hrefs="+encodeURIComponent(inputs[i].name);
		}
	}
	return hrefs;
}

// Assumption: last character of a toc reference cannot be underscore _
function isToc(name) {
	return name.charAt(name.length-1) != "_";
}

function isParentTocSelected(name) {
	var parentCheckbox = getParentCheckbox(name);
	return (parentCheckbox.checked && !getGrayed(parentCheckbox));
}

function getParentCheckbox(name) {
	var parentId = name.substring(0, name.lastIndexOf("_", name.length-2));
	return document.getElementById(parentId);
}

function collapseOrExpand(nodeId) {
	var node = document.getElementById("div"+nodeId);
	var img = document.getElementById("img"+nodeId);
	if (!node || !img) return;
	if (node.className == "expanded") {
		node.className = "collapsed";
		img.src = plus.src;
		img.alt = altBookClosed;
	} else {
		node.className = "expanded";
		img.src = minus.src;
		img.alt = altBookOpen;
	}
}

function collapse(nodeId) {
	var node = document.getElementById("div"+nodeId);
	var img = document.getElementById("img"+nodeId);
	if (!node || !img) return;
	node.className = "collapsed";
	img.src = plus.src;
	img.alt = altBookClosed;
}

function expand(nodeId) {
	var node = document.getElementById("div"+nodeId);
	var img = document.getElementById("img"+nodeId);
	if (!node || !img) return;
	node.className = "expanded";
	img.src = minus.src;
	img.alt = altBookOpen;
}

function getParent(child) {
	var id = child.name;
	var parentId = id.substring(0, id.lastIndexOf("_", id.length-2));
	return document.getElementById(parentId);
}

function updateParentState(checkbox,parentDiv) {

	if (checkbox == null)
		return;

	var baseChildState = checkbox.checked;
	var parent = getParent(checkbox);
	if (parent == null)
		return;

	var allSameState = true;
	var children = document.getElementById(parentDiv).getElementsByTagName("INPUT");
	for (var i = children.length - 1; i >= 0; i--) {
		if (children[i].checked != baseChildState ) {
			allSameState = false;
			break;
		}
	}

	setGrayed(parent, !allSameState);
	parent.checked = !allSameState || baseChildState;
}

function setSubtreeChecked(checkbox, parentDiv) {
	var state = checkbox.checked;
	var children = document.getElementById(parentDiv).getElementsByTagName("INPUT");
	for (var i = children.length - 1; i >= 0; i--) {
		var element = children[i];
		if (state) {
			element.checked = true;
		} else {
			element.checked = false;
		}
	}
	setGrayed(checkbox, false);
}

function setGrayed(node, enableGray) {
	if (enableGray)
		node.className = "grayed";
	else
		node.className = "checkbox";
}

function getGrayed(node) {
	return node.className == "grayed";
}

function isExpanded(nodeId) {
	var node = document.getElementById("div"+nodeId);
	if (node == null) return false;
	return node.className == "expanded";
}

function isCollapsed(nodeId) {
	var node = document.getElementById("div"+nodeId);
	if (node == null) return false;
	return node.className == "collapsed";
}

/**
 * Handler for key down (arrows)
 */
function keyDownHandler(folderId, key, target)
{
	if (key != 37 && key != 39) 
		return true;

 	if (key == 39) { // Right arrow, expand
		if (isCollapsed(folderId))
			expand(folderId);
		target.focus();
  	} else if (key == 37) { // Left arrow,collapse
		if (isExpanded(folderId))
			collapse(folderId);
		var parentCheckbox = getParentCheckbox(target.name);
		if (parentCheckbox != null)
			parentCheckbox.focus();	
		else
			target.focus();
  	} 
  			
  	return false;
}

function enableOK() {
	var value = document.getElementById("workingSet").value;
	if (!value || value.length == 0 || value.charAt(0) == " ")
		document.getElementById("ok").disabled = true;
	else
		document.getElementById("ok").disabled = false;
}

</script>

</head>

<body dir="<%=direction%>" onload="onloadHandler()">
<form onsubmit="doSubmit();return false;">
	<table id="wsTable" width="100%" cellspacing=0 cellpading=0 border=0 align=center >
		<tr><td style="padding:5px 10px 0px 10px;"><label for="workingSet" accesskey="<%=ServletResources.getAccessKey("WorkingSetName", request)%>"><%=ServletResources.getLabel("WorkingSetName", request)%>:</label>
		</td></tr>
		<tr><td style="padding:0px 10px;"><input type="text" id="workingSet" name="workingSet" value='<%=data.isEditMode()?data.getWorkingSetName():""%>' maxlength=256 alt='<%=ServletResources.getString("WorkingSetName", request)%>' title='<%=ServletResources.getString("WorkingSetName", request)%>' onkeyup="enableOK();return true;">
        </td></tr>
         <tr><td><div id="selectBook" style="padding-top:5px; margin-<%=isRTL?"right":"left"%>:10px;"><%=ServletResources.getString("WorkingSetContent", request)%>:</div>
		</td></tr>
    </table>
    
<div id="booksContainer" style="background:<%=prefs.getViewBackground()%>;">

<% 
for (int i=0; i<data.getTocCount(); i++)
{
	if(!tocData.isEnabled(i)){
		// do not show
		continue;
	}
	String label = data.getTocLabel(i);
	short state = data.getTocState(i);
	String checked = state == WorkingSetData.STATE_CHECKED || state == WorkingSetData.STATE_GRAYED ? "checked" : "";
	String className = state == WorkingSetData.STATE_GRAYED ? "grayed" : "checkbox";
%>
				<div class="book" id='<%="id"+i%>' >
					<img id='<%="img"+i%>' alt="<%=ServletResources.getString("bookClosed", request)%>" src="<%=prefs.getImagesDirectory()%>/plus.gif" onclick="collapseOrExpand('<%=i%>')">
					<input 	class='<%=className%>' 
							type="checkbox" 
							id='<%=data.getTocHref(i)%>' 
							name='<%=data.getTocHref(i)%>' 
							alt="<%=label%>" <%=checked%> 
						  	onkeydown="keyDownHandler(<%=i%>, event.keyCode, this)"
							onclick="setSubtreeChecked(this, '<%="div"+i%>')">
							<label for="<%=data.getTocHref(i)%>"><%=label%></label>
					<div id='<%="div"+i%>' class="collapsed">
<%
	for (int topic=0; topic<data.getTopicCount(i); topic++)
	{
		String topicLabel = data.getTopicLabel(i, topic);
		String topicChecked = (state == WorkingSetData.STATE_CHECKED) || 
							  (state == WorkingSetData.STATE_GRAYED && data.getTopicState(i,topic) == WorkingSetData.STATE_CHECKED) 
							  ? "checked" : "";
%>
						<div class="topic" id='<%="id"+i+"_"+topic%>'>
							<input 	class="checkbox" 
									type="checkbox" 
									id='<%=data.getTocHref(i)+"_"+topic+"_"%>' 
									name='<%=data.getTocHref(i)+"_"+topic+"_"%>' 
									alt="<%=topicLabel%>" <%=topicChecked%> 
									onkeydown="keyDownHandler(<%=i%>, event.keyCode, this)"
									onclick="updateParentState(this, '<%="div"+i%>')">
									<label for="<%=data.getTocHref(i)+"_"+topic+"_"%>"><%=topicLabel%></label>
						</div>
<%
	}
%>
					</div>
				</div>
<%
}		
%>

</div>
<div style="height:50px;">
	<table valign="bottom" align="<%=isRTL?"left":"right"%>">
		<tr id="buttonsTable" valign="bottom"><td valign="bottom" align="<%=isRTL?"left":"right"%>">
  			<table cellspacing=10 cellpading=0 border=0 style="background:transparent;">
				<tr>
					<td>
						<button type="submit" id="ok"><%=ServletResources.getString("OK", request)%></button>
					</td>
					<td>
					  	<button type="reset" onclick="window.close()" id="cancel"><%=ServletResources.getString("Cancel", request)%></button>
					</td>
				</tr>
  			</table>
		</td></tr>
	</table>
</div>
</form>
</body>
</html>

