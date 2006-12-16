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
	boolean cookiesEnabled = false;
	Cookie[] cookies = request.getCookies();
	if (cookies != null) {
		for (int i = 0; i < cookies.length; i++) {
			if ("cookiesEnabled".equals(cookies[i].getName()) && "yes".equals(cookies[i].getValue())) {
				cookiesEnabled = true;
				break;
			}
		}
	}
	if(cookiesEnabled || (new RequestData(application, request, response)).getMode() != RequestData.MODE_INFOCENTER){
		request.getRequestDispatcher("/advanced/searchScoped.jsp").forward(request, response);
	}else{
		request.getRequestDispatcher("/advanced/searchSimple.jsp").forward(request, response);
	}
%>