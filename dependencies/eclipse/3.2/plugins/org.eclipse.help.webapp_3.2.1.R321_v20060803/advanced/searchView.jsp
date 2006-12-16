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
	SearchData data = new SearchData(application, request, response);
	// After each search we preserve the scope (working set), if any
	// this need to be at the beginning, otherwise cookie is not written
	if (data.isSearchRequest())
		data.saveScope();

	WebappPreferences prefs = data.getPrefs();
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Expires" content="-1">

<title><%=ServletResources.getString("SearchResults", request)%></title>

<style type="text/css">
<%@ include file="searchList.css"%>
</style>


<base target="ContentViewFrame">
<script language="JavaScript" src="list.js"></script>
<script language="JavaScript">		

function refresh() 
{ 
	window.location.replace("searchView.jsp?<%=request.getQueryString()%>");
}
</script>


</head>

<body dir="<%=direction%>">

<%
if (!data.isSearchRequest()) {
	out.write(ServletResources.getString("doSearch", request));
} else if (data.getQueryExceptionMessage()!=null) {
	out.write(data.getQueryExceptionMessage());
} else if (data.isProgressRequest()) {
%>

<CENTER>
<TABLE BORDER='0'>
	<TR><TD><%=ServletResources.getString("Indexing", request)%></TD></TR>
	<TR><TD ALIGN='<%=isRTL?"RIGHT":"LEFT"%>'>
		<DIV STYLE='width:100px;height:16px;border:1px solid ThreeDShadow;'>
			<DIV ID='divProgress' STYLE='width:<%=data.getIndexedPercentage()%>px;height:100%;background-color:Highlight'></DIV>
		</DIV>
	</TD></TR>
	<TR><TD><%=data.getIndexedPercentage()%>% <%=ServletResources.getString("complete", request)%></TD></TR>
	<TR><TD><br><%=ServletResources.getString("IndexingPleaseWait", request)%></TD></TR>
</TABLE>
</CENTER>
<script language='JavaScript'>
setTimeout('refresh()', 2000);
</script>
</body>
</html>

<%
	return;
} else if (data.getResultsCount() == 0){
	out.write(ServletResources.getString("Nothing_found", request));
} else {
%>

<table id='list'  cellspacing='0' >

<%
	for (int topic = 0; topic < data.getResultsCount(); topic++)
	{
		if(data.isActivityFiltering() && !data.isEnabled(topic)){
			continue;
		}
%>

<tr class='list' id='r<%=topic%>'>
	<td class='score' align='<%=isRTL?"left":"right"%>'>

<%
		boolean isPotentialHit = data.isPotentialHit(topic);
		if (isPotentialHit) {
%>


	<img src="<%=prefs.getImagesDirectory()%>/d_topic.gif" alt=""/>

<%
		}
		else {
%>

	<img src="<%=prefs.getImagesDirectory()%>/topic.gif" alt=""/>

<%
		}
%>

	</td>
	<td align='<%=isRTL?"right":"left"%>'>
		<a class='link' id='a<%=topic%>' 
		   href="<%=data.getTopicHref(topic)%>" 
		   onmouseover="showStatus(event);return true;"
		   onmouseout="clearStatus();return true;"
		   onclick='parent.parent.parent.setContentToolbarTitle(this.title)' 
		   title="<%=data.getTopicTocLabel(topic)%>">

<%
		String label = null;
		if (isPotentialHit) {
            label = ServletResources.getString("PotentialHit", data.getTopicLabel(topic), request);
        }
        else {
            label = data.getTopicLabel(topic);
        }
%>

        <%=label%></a>
	</td>
</tr>

<%
		String desc = data.getTopicDescription(topic);
		if (desc!=null) {
%>
<tr class='description' id='d<%=topic%>'>
	<td class='score'>
	</td>
	<td align='<%=isRTL?"right":"left"%>' class='label'>
		<%=desc%>
	</td>
<%
		}
	}
%>
</table>

<%
}
%>

<script language="JavaScript">
	selectTopicById('<%=data.getSelectedTopicId()%>');
</script>

</body>
</html>
