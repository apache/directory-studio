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
	<jsp:param name="view" value="toc"/>

	<jsp:param name="name"     value="show_all"/>
	<jsp:param name="tooltip"  value='show_all'/>
	<jsp:param name="image"    value="show_all.gif"/>
	<jsp:param name="action"   value="toggleShowAll"/>
	<jsp:param name="state"    value="<%=(new ActivitiesData(application, request, response)).getButtonState()%>"/>

	<jsp:param name="name"     value="synchnav"/>
	<jsp:param name="tooltip"  value='SynchNav'/>
	<jsp:param name="image"    value="synch_nav.gif"/>
	<jsp:param name="action"   value="resynchNav"/>
	<jsp:param name="state"    value="off"/>
</jsp:include>