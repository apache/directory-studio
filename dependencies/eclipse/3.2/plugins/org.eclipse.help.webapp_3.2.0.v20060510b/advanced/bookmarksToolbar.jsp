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


<jsp:include page="toolbar.jsp">
	<jsp:param name="script" value="navActions.js"/>
	<jsp:param name="view" value="bookmarks"/>

	<jsp:param name="name"     value="deleteBookmark"/>
	<jsp:param name="tooltip"  value='deleteBookmark'/>
	<jsp:param name="image"    value="bookmark_rem.gif"/>
	<jsp:param name="action"   value="removeBookmark"/>
	<jsp:param name="state"    value='off'/>

	<jsp:param name="name"     value="deleteAllBookmarks"/>
	<jsp:param name="tooltip"  value='deleteAllBookmarks'/>
	<jsp:param name="image"    value="bookmark_remall.gif"/>
	<jsp:param name="action"   value="removeAllBookmarks"/>
	<jsp:param name="state"    value='off'/>

	<jsp:param name="name"     value="synchnav"/>
	<jsp:param name="tooltip"  value='SynchNav'/>
	<jsp:param name="image"    value="synch_nav.gif"/>
	<jsp:param name="action"   value="resynchNav"/>
	<jsp:param name="state"    value='off'/>
</jsp:include>
