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

<jsp:include page="confirm.jsp">
	<jsp:param name="title"		value="confirmShowAllTitle"/>
	<jsp:param name="header"	value="askShowAllQuestion"/>
	<jsp:param name="message"	value="askShowAllExplanation"/>
	<jsp:param name="confirmCallback"	value="<%=\"yesShowAll()\"%>"/>
	<jsp:param name="initialFocus"	value="cancel"/>
	<jsp:param name="buttons"	value="yesno"/>
</jsp:include>