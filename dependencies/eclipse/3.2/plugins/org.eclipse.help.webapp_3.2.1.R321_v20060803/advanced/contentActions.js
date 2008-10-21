/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
 
var isMozilla = navigator.userAgent.indexOf('Mozilla') != -1 && parseInt(navigator.appVersion.substring(0,1)) >= 5;
var isIE = navigator.userAgent.indexOf('MSIE') != -1;

var navVisible = true;
	
function goBack(button, param) {
	parent.history.back();
	if (isIE && button && document.getElementById(button)){
		document.getElementById(button).blur();
	}
}

function goForward(button, param) {
	parent.history.forward();
	if (isIE && button && document.getElementById(button)){
		document.getElementById(button).blur();
	}
}

function goHome(button, param) {
	var isHome = false;

	try {
		// first check if we're already at home
		var str = param;
		var index = str.indexOf("/");
		if (index > 0) {
			str = str.substring(index);
		}
		var locationStr = parent.ContentViewFrame.location.href;
		isHome = (locationStr.substring(locationStr.length - str.length) == str)
	}
	catch (e) {
		// insufficient permission, not home
	}
	
	if (!isHome) {
		parent.ContentViewFrame.location = param;
	}
	parent.parent.NavFrame.collapseToc();
	if (isIE && button && document.getElementById(button)){
		document.getElementById(button).blur();
	}
}

function bookmarkPage(button, param)
{
	// Currently we pick up the url from the content page.
	// If the page is from outside the help domain, a script
	// exception is thrown. We need to catch it and ignore it.
	try
	{
		// use the url from plugin id only
		var url = parent.ContentViewFrame.location.href;
		var i = url.indexOf("/topic/");
		if (i >=0 )
			url = url.substring(i+6);
		// remove any query string
		i = url.indexOf("?");
		if (i >= 0)
			url = url.substring(0, i);
			
		var title = parent.ContentViewFrame.document.title;
		if (title == null || title == "")
			title = url;

		/********** HARD CODED VIEW NAME *************/
		parent.parent.NavFrame.ViewsFrame.bookmarks.bookmarksViewFrame.location.replace("bookmarksView.jsp?operation=add&bookmark="+encodeURIComponent(url)+"&title="+encodeURIComponent(title));
	}catch (e) {}
	if (isIE && button && document.getElementById(button)){
		document.getElementById(button).blur();
	}
}

function bookmarkInfocenterPage(button, param)
{
	// Currently we pick up the url from the content page.
	// If the page is from outside the help domain, a script
	// exception is thrown. We need to catch it and ignore it.
	try
	{
		// use the url from plugin id only
		var url = parent.ContentViewFrame.location.href;
		var i = url.indexOf("/topic/");
		if (i >=0 )
			url = url.substring(i+6);
		// remove any query string
		i = url.indexOf("?");
		if (i >= 0)
			url = url.substring(0, i);
			
		var title = parent.ContentViewFrame.document.title;
		if (title == null || title == "")
			title = url;

		/********** HARD CODED VIEW NAME *************/
		window.external.AddFavorite(parent.ContentViewFrame.location.href,title);
	}catch (e) {}
	if (isIE && button && document.getElementById(button)){
		document.getElementById(button).blur();
	}
}

function resynch(button, param)
{
	try {
		var topic = parent.ContentViewFrame.window.location.href;
		// remove the query, if any
		var i = topic.indexOf('?');
		if (i != -1)
			topic = topic.substring(0, i);
		parent.parent.NavFrame.displayTocFor(topic);
	} catch(e) {}
	if (isIE && button && document.getElementById(button)){
		document.getElementById(button).blur();
	}
}

function printContent(button, param)
{
	try {
		parent.ContentViewFrame.focus();
		parent.ContentViewFrame.print();
	} catch(e) {}
	if (isIE && button && document.getElementById(button)){
		document.getElementById(button).blur();
	}
}

function setTitle(label)
{
	if( label == null) label = "";
	var title = document.getElementById("titleText");
	if(title !=null){
		var text = title.lastChild;
		text.nodeValue = " "+label;
	}
}

