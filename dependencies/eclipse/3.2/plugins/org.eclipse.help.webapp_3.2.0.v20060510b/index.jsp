<%--
 Copyright (c) 2000, 2004 IBM Corporation and others.
 All rights reserved. This program and the accompanying materials 
 are made available under the terms of the Eclipse Public License v1.0
 which accompanies this distribution, and is available at
 http://www.eclipse.org/legal/epl-v10.html
 
 Contributors:
     IBM Corporation - initial API and implementation
--%>
<%@ page import="org.eclipse.help.internal.webapp.data.*" errorPage="/advanced/err.jsp" contentType="text/html; charset=UTF-8"%>
<%
	request.setCharacterEncoding("UTF-8");

	if (request.getParameter("noscript") != null) {
		request.getRequestDispatcher("/basic/index.jsp").forward(request, response);
		return;
	}

	RequestData data = new RequestData(application,request, response);
	if(data.isBot()){
		TocData tData = new TocData(application,request, response);
		LayoutData lData = new LayoutData(application,request, response);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><%=lData.getWindowTitle()%></title>
</head>
<body>
<% tData.generateLinks(out); %>
</body>
</html>	
<%
	}else if(data.isAdvancedUI()){
		request.getRequestDispatcher("/advanced/index.jsp").forward(request, response);
	}else{
		request.getRequestDispatcher("/basic/index.jsp").forward(request, response);
	}
%>
