<%--
 Copyright (c) 2000, 2004 IBM Corporation and others.
 All rights reserved. This program and the accompanying materials 
 are made available under the terms of the Eclipse Public License v1.0
 which accompanies this distribution, and is available at
 http://www.eclipse.org/legal/epl-v10.html
 
 Contributors:
     IBM Corporation - initial API and implementation
--%><%@
page import="org.eclipse.help.internal.webapp.data.*"  contentType="text/html; charset=UTF-8"
%><% 
request.setCharacterEncoding("UTF-8");
boolean isRTL = UrlUtil.isRTL(request, response);
String  direction = isRTL?"rtl":"ltr";
if (new RequestData(application,request, response).isMozilla()) {
%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN">
<% 
} else {
%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%
}
%><!------------------------------------------------------------------------------
 ! Copyright (c) 2000, 2004 IBM Corporation and others.
 ! All rights reserved. This program and the accompanying materials 
 ! are made available under the terms of the Eclipse Public License v1.0
 ! which accompanies this distribution, and is available at
 ! http://www.eclipse.org/legal/epl-v10.html
 ! 
 ! Contributors:
 !     IBM Corporation - initial API and implementation
 ------------------------------------------------------------------------------->