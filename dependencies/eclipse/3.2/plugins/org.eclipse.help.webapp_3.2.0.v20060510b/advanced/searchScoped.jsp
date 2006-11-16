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
	SearchData data = new SearchData(application, request, response);
	WebappPreferences prefs = data.getPrefs();
%>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title><%=ServletResources.getString("Search", request)%></title>
     
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
	background:<%=prefs.getToolbarBackground()%>;
	border:0px;
	text:white;
	height:100%;
}

TABLE {
	font: <%=prefs.getToolbarFont()%>;
	background:<%=prefs.getToolbarBackground()%>;
	margin: 0px;
	padding: 0px;
	height:100%;
}

FORM {
	background:<%=prefs.getToolbarBackground()%>;
	height:100%;
	margin:0px;
}

INPUT {
	font: <%=prefs.getToolbarFont()%>;
	margin:0px;
	padding:0px;
}

A {
	color:WindowText;
	text-decoration:none;
}

#searchTD {
	padding-<%=isRTL?"right":"left"%>:7px;
	padding-<%=isRTL?"left":"right"%>:4px;
}

#searchWord {
	padding-left:4px;
	padding-right:4px;
	border:1px solid ThreeDShadow;
}

#searchLabel {
	color:WindowText;
}

#go {
	background:ThreeDShadow;
	color:Window;
	font-weight:bold;
	border:1px solid ThreeDShadow;
	margin-left:1px;
}

#scopeLabel {
	text-decoration:underline; 
	color:#0066FF; 
	cursor:hand;
	padding-left:15px;   /* This should be the same for both RTL and LTR. */
}

#scope { 
	text-align:<%=isRTL?"left":"right"%>;
	margin-<%=isRTL?"right":"left"%>:5px;
	border:0px;
	color:WindowText;
	text-decoration:none;
}

<%
	if (data.isIE()) {
%>
#go {
	padding-<%=isRTL?"right":"left"%>:1px;
}
<%
	}
%>
</style>

<script language="JavaScript">
var isIE = navigator.userAgent.indexOf('MSIE') != -1;
var isMozilla = navigator.userAgent.toLowerCase().indexOf('mozilla') != -1 && parseInt(navigator.appVersion.substring(0,1)) >= 5;

var advancedDialog;
var w = 300;
var h = 300;

function openAdvanced()
{
	var scope = document.getElementById("scope").firstChild;
	var workingSet = "";
	if (scope != null)
	 	workingSet = document.getElementById("scope").firstChild.nodeValue;
	 	
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
	advancedDialog = window.open("workingSetManager.jsp?workingSet="+encodeURIComponent(workingSet), "advancedDialog", "resizeable=no,height="+h+",width="+w+",left="+l+",top="+t );
	advancedDialog.focus(); 
}

function closeAdvanced()
{
	try {
		if (advancedDialog)
			advancedDialog.close();
	}
	catch(e) {}
}

/**
 * This function can be called from this page or from
 * the advanced search page. When called from the advanced
 * search page, a query is passed.
 */
function doSearch(query)
{
	var workingSet = document.getElementById("scope").firstChild.nodeValue;

	if (!query || query == "")
	{
		var form = document.forms["searchForm"];
		var searchWord = form.searchWord.value;
		var maxHits = form.maxHits.value;
		if (!searchWord || searchWord == "")
			return;
		query ="searchWord="+encodeURIComponent(searchWord)+"&maxHits="+maxHits;
		if (workingSet != '<%=ServletResources.getString("All", request)%>')
			query = query +"&scope="+encodeURIComponent(workingSet);
	}
		
	/******** HARD CODED VIEW NAME *********/
	// do some tests to ensure the results are available
	if (parent.HelpFrame && 
		parent.HelpFrame.NavFrame && 
		parent.HelpFrame.NavFrame.showView &&
		parent.HelpFrame.NavFrame.ViewsFrame && 
		parent.HelpFrame.NavFrame.ViewsFrame.search && 
		parent.HelpFrame.NavFrame.ViewsFrame.search.searchViewFrame) 
	{
		parent.HelpFrame.NavFrame.showView("search");
		var searchView = parent.HelpFrame.NavFrame.ViewsFrame.search.searchViewFrame;
		searchView.location.replace("searchView.jsp?"+query);
	}
}

function fixHeights()
{
	if (!isIE) return;
	
	var h = document.getElementById("searchWord").offsetHeight;
	document.getElementById("go").style.height = h;
}

function onloadHandler(e)
{
	var form = document.forms["searchForm"];
	form.searchWord.value = '<%=UrlUtil.JavaScriptEncode(data.getSearchWord())%>';
	fixHeights();
}

</script>

</head>

<body dir="<%=direction%>" onload="onloadHandler()"  onunload="closeAdvanced()">

	<form  name="searchForm"   onsubmit="doSearch()">
		<table id="searchTable" align="<%=isRTL?"right":"left"%>" valign="middle" cellspacing="0" cellpadding="0" border="0">
			<tr nowrap  valign="middle">
				<td <%=isRTL?"nowrap":""%> id="searchTD">
					<label id="searchLabel" for="searchWord" accesskey="<%=ServletResources.getAccessKey("Search", request)%>">
					&nbsp;<%=ServletResources.getLabel("Search", request)%>:
					</label>
				</td>
				<td>
					<input type="text" id="searchWord" name="searchWord" value='' size="24" maxlength="256" alt='<%=ServletResources.getString("SearchExpression", request)%>' title='<%=ServletResources.getString("SearchExpression", request)%>'>
				</td>
				<td >
					&nbsp;<input type="button" onclick="this.blur();doSearch()" value='<%=ServletResources.getString("GO", request)%>' id="go" alt='<%=ServletResources.getString("GO", request)%>' title='<%=ServletResources.getString("GO", request)%>'>
					<input type="hidden" name="maxHits" value="500" >
				</td>
				<td nowrap>
					<a id="scopeLabel" href="javascript:openAdvanced();" title='<%=ServletResources.getString("ScopeTooltip", request)%>' alt='<%=ServletResources.getString("ScopeTooltip", request)%>' onmouseover="window.status='<%=ServletResources.getString("ScopeTooltip", request)%>'; return true;" onmouseout="window.status='';"><%=ServletResources.getLabel("Scope", request)%>:</a>
				</td>
				<td nowrap>
					<input type="hidden" name="workingSet" value='<%=data.getScope()%>'>
					<div id="scope" ><%=data.getScope()%></div>
				</td>
			</tr>

		</table>
	</form>		

</body>
</html>

