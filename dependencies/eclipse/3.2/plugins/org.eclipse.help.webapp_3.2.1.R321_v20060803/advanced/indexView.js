/*******************************************************************************
 * Copyright (c) 2005, 2006 Intel Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Intel Corporation - initial API and implementation
 *******************************************************************************/
 
var isMozilla = navigator.userAgent.indexOf("Mozilla") != -1 && parseInt(navigator.appVersion.substring(0,1)) >= 5;
var isIE = navigator.userAgent.indexOf("MSIE") != -1;

/**
 * Global references to IndexTypein and IndexList child frames.
 * Are intialized by the child frames on theirs load.
 */
var typeinFrame;
var listFrame;

/**
 * Selects a topic in the index list
 */
function selectTopicById(id) {
	if (!listFrame) return false;

	return listFrame.selectTopicById(id);
}

/**
 * Selects the next index list node "up" from current one
 */
function selectNextUp() {
	if (!listFrame) return false;

	var next = listFrame.getNextUp(listFrame.oldActive);
	if (next) {
		listFrame.highlightTopic(next);
		listFrame.scrollIntoView(next);
		setTypeinValue(next);
	}
}

/**
 * Selects the next index list node "down" from current one
 */
function selectNextDown() {
	if (!listFrame) return false;

	var next = listFrame.getNextDown(listFrame.oldActive);
	if (next) {
		listFrame.highlightTopic(next);
		listFrame.scrollIntoView(next);
		setTypeinValue(next);
	}
}

/**
 * Returns selected list item
 */
function getSelection() {
	if (!listFrame) return null;

	return listFrame.oldActive;
}

/**
 * Set value of the typein input field.
 * The value can be anchor's id or anchor's text.
 */
function setTypeinValue(anchor) {
	if (!typeinFrame) return;

	if (!anchor) return;

	var value = anchor.getAttribute("id");
	if (value) {
		typeinFrame.currentId = value;
	} else {
		typeinFrame.currentId = "";
		if (isIE)
			value = anchor.innerText;
		else if (isMozilla)
			value = anchor.lastChild.nodeValue;
		if (!value)
			value = "";
	}
	typeinFrame.typein.value = value;
	typeinFrame.typein.previous = value;
}

/**
 * Open current selected item in the content frame
 */
function doDisplay() {
	if (!listFrame) return;
	if (!listFrame.oldActive) return;

	parent.parent.parent.parent.ContentFrame.ContentViewFrame.location.replace(listFrame.oldActive);
}
