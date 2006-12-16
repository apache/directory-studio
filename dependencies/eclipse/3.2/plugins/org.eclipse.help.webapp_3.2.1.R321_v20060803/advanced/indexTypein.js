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
 
var isMozilla = parent.isMozilla;
var isIE = parent.isIE;

var typein;
var currentId;

function compare(keyword, pattern) {
	var kI = 0, pI = 0;
	var kCh, pCh;

	while (kI < keyword.length && pI < pattern.length) {
		kCh = keyword.charAt(kI).toLowerCase();
		pCh = pattern.charAt(pI).toLowerCase();
		if (kCh > pCh) {
			return 1;
		} else if (kCh < pCh) {
			return -1;
		}
		kI++;
		pI++;
	}
	if (keyword.length >= pattern.length) {
		return 0;
	} else {
		return -1;
	}
}

function searchPattern(pattern) {
    if (!parent.listFrame) return null;

	var from = 0;
	var to = parent.listFrame.ids.length;
	var i;
	var res;

	while (to > from) {
		i = Math.floor((to + from) / 2);
		res = compare(parent.listFrame.ids[i], pattern);
		if (res == 0) {
			while (i > 0) {
				res = compare(parent.listFrame.ids[--i], pattern);
				if (res != 0) {
					i++;
					break;
				}
			}
			return parent.listFrame.ids[i];
		} else if (res < 0) {
			from = i + 1;
		} else {
			to = i;
		}
	}

	return null;
}

function keyDownHandler(e) {
	var key;

	if (isIE) {
		key = window.event.keyCode;
	} else if (isMozilla) {
		key = e.keyCode;
	}

	if (key != 13 && key != 38 && key != 40)
		return true;

	if (isMozilla)
		e.cancelBubble = true;
	else if (isIE)
		window.event.cancelBubble = true;

	if (key == 13) { // enter
		// display topic corresponded to selected list item
		parent.setTypeinValue(parent.getSelection());
		parent.doDisplay();
	} if (key == 38) { // up arrow
		parent.selectNextUp();
	} else if (key == 40) { // down arrow
		parent.selectNextDown();
	}

	return false;
}

/**
  * Select the corresponding item in the index list on typein value change.
  * Check is performed periodically in short interval.
  */
function intervalHandler() {
	if (typein.value != typein.previous) {
		// typein value has been changed
		typein.previous = typein.value;
		var id = searchPattern(typein.value);
		if (id && id != currentId) {
			// the value has became to fit to other item
			if (parent.selectTopicById(id)) {
				currentId = id;
			}
		}
	}
}

/**
 * IndexTypeinFrame onload handler
 */
function onloadHandler() {
	parent.typeinFrame = window;

	typein = document.getElementById("typein");

	typein.value = "";
	typein.previous = "";

	currentId = "";

	if (isMozilla) {
		document.addEventListener("keydown", keyDownHandler, true);
	} else if (isIE) {
		document.onkeydown = keyDownHandler;
	}

	setInterval("intervalHandler()", 200);
}
