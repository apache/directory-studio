<%--
 Copyright (c) 2000, 2005 IBM Corporation and others.
 All rights reserved. This program and the accompanying materials 
 are made available under the terms of the Eclipse Public License v1.0
 which accompanies this distribution, and is available at
 http://www.eclipse.org/legal/epl-v10.html
 
 Contributors:
     IBM Corporation - initial API and implementation
--%>
<%@ include file="header.jsp"%>

<% 
	RequestData data = new RequestData(application,request, response);
	WebappPreferences prefs = data.getPrefs();
	String forwardImage, backImage;
	if(isRTL) {
		forwardImage = "back.gif";
		backImage = "forward.gif";
	} else {
		forwardImage = "forward.gif";
		backImage = "back.gif";
	}
	boolean isBookmarkAction = prefs.isBookmarksView() 
		|| prefs.isBookmarksAction() && data.isIE() && !data.isOpera(); // for infocenter, add to favorites supported on IE
	String bookmarkButtonState = isBookmarkAction?"off":"hidden";
	String bookmarkAction = RequestData.MODE_INFOCENTER==data.getMode()?"bookmarkInfocenterPage":"bookmarkPage";
%>
<jsp:include page="toolbar.jsp">
	<jsp:param name="script" value="contentActions.js"/>
	<jsp:param name="toolbar" value="content"/>
	
	<jsp:param name="name"     value="back"/>
	<jsp:param name="tooltip"  value='back_tip'/>
	<jsp:param name="image"    value='<%=backImage%>'/>
	<jsp:param name="action"   value="goBack"/>
	<jsp:param name="state"    value='off'/>
	
	<jsp:param name="name"     value="forward"/>
	<jsp:param name="tooltip"  value='forward_tip'/>
	<jsp:param name="image"    value='<%=forwardImage%>'/>
	<jsp:param name="action"   value="goForward"/>
	<jsp:param name="state"    value='off'/>
	
	<jsp:param name="name"     value=""/>
	<jsp:param name="tooltip"  value=""/>
	<jsp:param name="image"    value=""/>
	<jsp:param name="action"   value=""/>
	<jsp:param name="state"    value='off'/>
	
	<jsp:param name="name"     value="synch"/>
	<jsp:param name="tooltip"  value='Synch'/>
	<jsp:param name="image"    value="synch_toc_nav.gif"/>
	<jsp:param name="action"   value="resynch"/>
	<jsp:param name="state"    value='off'/>
	
	<jsp:param name="name"     value="add_bkmrk"/>
	<jsp:param name="tooltip"  value='BookmarkPage'/>
	<jsp:param name="image"    value="add_bkmrk.gif"/>
	<jsp:param name="action"   value="<%=bookmarkAction%>"/>
	<jsp:param name="state"    value='<%=bookmarkButtonState%>'/>

	<jsp:param name="name"     value="print"/>
	<jsp:param name="tooltip"  value='Print'/>
	<jsp:param name="image"    value="print_edit.gif"/>
	<jsp:param name="action"   value="printContent"/>
	<jsp:param name="state"    value='off'/>

</jsp:include>
