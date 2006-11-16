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
 
// Common scripts for IE and Mozilla.

var isMozilla = navigator.userAgent.indexOf('Mozilla') != -1 && parseInt(navigator.appVersion.substring(0,1)) >= 5;
var isIE = navigator.userAgent.indexOf('MSIE') != -1;
var isSafari = navigator.userAgent.indexOf('Safari') != -1;

// selected node
var active;
var oldActive;

/**
 * Returns the target node of an event
 */
function getTarget(e) {
	var target;
  	if (isMozilla)
  		target = e.target;
  	else if (isIE)
   		target = window.event.srcElement;

	return target;
}


/**
 * Returns the row of this click
 */
function getTRNode(node) {
  if (node.nodeType == 3)  //"Node.TEXT_NODE") 
	return node.parentNode.parentNode.parentNode;
  else if (node.tagName == "A")
  	return node.parentNode.parentNode;
  else if (node.tagName == "TD") 
    return node.parentNode;
  else if (node.tagName == "TR") 
    return node;
  else if (node.tagName == "IMG")
  	return node.parentNode.parentNode.parentNode;
  else
  	return null;
}

/**
 * Returns the anchor node in this row
 */
function getAnchorNode(tr)
{
	var id = tr.id.substring(1);
	return document.getElementById("a"+id);
}


/**
 * Return next item in the list
 */
function getNextDown(node)
{
	var tr = getTRNode(node);
	if (tr == null) return null;
	
	var id = tr.id.substring(1);
	var next = 1 + eval(id);
	return document.getElementById("a"+next);
}

/**
 * Return previous item in the list
 */
function getNextUp(node)
{
	var tr = getTRNode(node);
	if (tr == null) return null;
	
	var id = tr.id.substring(1);
	var next = eval(id) - 1;
	if (next >= 0)
		return document.getElementById("a"+next);
	else
		return null;
}


/**
 * Highlights link. Returns true if highlights.
 */
function highlightTopic(topic)
{
  	if (!topic || (topic.tagName != "A" && topic.parentNode.tagName != "A"))
		return false;
	
  	var tr = getTRNode(topic); 
  	if (tr != null)
  	{
  	   	if (oldActive && oldActive != tr) {
    		oldActive.className="list";
    		var oldA = getAnchorNode(oldActive);
    		if (oldA) oldA.className = "";
  	   	}
    
		oldActive = tr;		
  		tr.className = "active";
  		var a = getAnchorNode(tr);
  		if (a)
  		{
  			a.className = "active";
  			// set toolbar title
  			if (a.onclick)
  				a.onclick();
  			//if (isIE)
  			//	a.hideFocus = "true";
   		}
   		active = a;
   		return true;
  	}
  	return false;
}

/**
 * Selects a topic in the tree: expand tree and highlight it
 */
function selectTopic(topic) 
{
	if (!topic || topic == "") return;
	
	var links = document.getElementsByTagName("a");

	for (var i=0; i<links.length; i++)
	{
		// take into account the extra ?toc=.. or &toc=
		if (links[i].href.indexOf(topic+"?toc=") == 0 ||
			links[i].href.indexOf(topic+"&toc=") == 0 ||
			links[i].href.indexOf(topic+"/?toc=") == 0)
		{
			highlightTopic(links[i]);
			scrollIntoView(links[i]);
			links[i].scrollIntoView(true);
			return true;
		}
	}
	return false;
}

/**
 * Selects a topic in the list
 */
function selectTopicById(id)
{
	var topic = document.getElementById(id);
	if (topic)
	{
		highlightTopic(topic);
		scrollIntoView(topic);
		return true;
	}
	return false;
}


/**
 * Scrolls the page to show the specified element
 */
function scrollIntoView(node)
{
	var scroll = getVerticalScroll(node);
	if (scroll != 0)
		window.scrollBy(0, scroll);
}

/**
 * Scrolls the page to show the specified element
 */
function getVerticalScroll(node)
{
	// Use the parent element for getting the offsetTop, as it appears
	// that tables get their own layout measurements.

	//var nodeTop = node.offsetTop;
	var nodeTop = node.parentNode.offsetTop;
		
	//var nodeBottom = nodeTop + node.offsetHeight;
	var nodeBottom = nodeTop + node.parentNode.offsetHeight;
	
	var pageTop = 0;
	var pageBottom = 0;
		
	if (isIE)
	{
		pageTop = document.body.scrollTop; 
		pageBottom = pageTop + document.body.clientHeight;
	
	} 
	else if (isMozilla)
	{
		pageTop = window.pageYOffset;
		pageBottom = pageTop + window.innerHeight - node.offsetHeight;
	}
	
	var scroll = 0;
	if (nodeTop >= pageTop )
	{
		if (nodeBottom <= pageBottom)
			scroll = 0; // already in view
		else
			scroll = nodeBottom - pageBottom/2;
	}
	else
	{
		scroll = nodeTop - pageTop;
	}
	
	return scroll;
}

function hidePopupMenu() {
	// hide popup if open
	var menu = document.getElementById("menu");
	if (!menu)
		return;
	if (menu.style.display == "block")
		menu.style.display = "none";
}

var popupMenuTarget;

function showPopupMenu(e) {
	// show the menu
	var x = e.clientX;
	var y = e.clientY;

	e.cancelBubble = true;

	var menu = document.getElementById("menu");
	if (!menu) 
		return;	
	menu.style.left = (x+1)+"px";
	menu.style.top = (y+1)+"px";
	menu.style.display = "block";
	if (isMozilla)
		popupMenuTarget = e.target;
}

/**
 * display topic label in the status line on mouse over topic
 */
function showStatus(e) {
	try {
		var overNode;
		if (isMozilla)
			overNode = e.target;
		else if (isIE)
			overNode = window.event.srcElement;
		else
			return true;

		overNode = getTRNode(overNode);
		if (overNode == null) {
			window.status = "";
			return true;
		}

		if (isMozilla)
			e.cancelBubble = false;

		var a = getAnchorNode(overNode);
		var statusText = "";
		if (isIE)
			statusText = a.innerText;
		else if (isMozilla)
			statusText = a.lastChild.nodeValue;
			
		if (statusText != a.title)
			statusText += " - " + a.title;
			
		window.status = statusText;
	} catch (e) {
	}

	return true;
}

function clearStatus() {
	window.status="";
}

/**
 * Popup a menu on right click over a bookmark.
 * This handler assumes the list.js script has been loaded.
 */
function contextMenuHandler(e)
{
	// hide popup if open
	hidePopupMenu();

	if (isIE)
		e = window.event;
		
  	var clickedNode;
  	if (isMozilla)
  		clickedNode = e.target;
  	else if (isIE)
   		clickedNode = e.srcElement;

  	if (!clickedNode)
  		return true;
  	
  	// call the click handler to select node
  	mouseClickHandler(e);
  	
  	if(clickedNode.tagName == "A")
  		active = clickedNode;
  	else if (clickedNode.parentNode.tagName == "A")
  		active = clickedNode.parentNode;
  	else
  		return true;
	
	showPopupMenu(e);
	
	return false;
}

/**
 * handler for clicking on a node
 */
function mouseClickHandler(e) {

	if (!isMozilla || e && e.target && e.target != popupMenuTarget)
		hidePopupMenu();
		
  	var clickedNode;
 	if (isMozilla)
  		clickedNode = e.target;
  	else if (isIE)
   		clickedNode = window.event.srcElement;
  	else 
  		return true;
  	
  	highlightTopic(clickedNode);
}


function focusHandler(e)
{
	if (oldActive){
		try{
			oldActive.focus();
		} catch (e) {
		}
	}
}

/**
 * Handler for key down (arrows)
 */
function keyDownHandler(e)
{
	var key;

	if (isIE) {
		key = window.event.keyCode;
	} else if (isMozilla) {
		key = e.keyCode;
	}

	if (key <37 || key > 40)
		return true;
		
	if (isMozilla)
  		e.cancelBubble = true;
  	else if (isIE)
  		window.event.cancelBubble = true;
  		
  	if (key == 40 ) { // down arrow
  		var clickedNode = getTarget(e);
  		if (!clickedNode) return;

		var next = getNextDown(clickedNode);
		highlightTopic(next);
		if (next)
			next.focus();
		else
			return true;

  	} else if (key == 38 ) { // up arrow
  		var clickedNode = getTarget(e);
  		if (!clickedNode) return;

		var next = getNextUp(clickedNode);
		highlightTopic(next);
		if (next)
			next.focus();
		else
			return true;
  	} else
  		return true;
  				
  	return false;
}


// listen for events
if (isMozilla) {
  document.addEventListener('click', mouseClickHandler, true);
  document.addEventListener('keydown', keyDownHandler, true);
  //document.addEventListener("focus", focusHandler, true);
	if (isSafari) {
		// workaround for lack of good system colors in Safari
		document.write('<style type="text/css">');
		document.write('.active {background:#B5D5FF;color:#000000;}');
		document.write('</style>');
	}
}
else if (isIE){
  document.onclick = mouseClickHandler;
  document.onkeydown = keyDownHandler;
  window.onfocus = focusHandler;
}
