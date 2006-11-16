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

var isIE = navigator.userAgent.indexOf('MSIE') != -1;

function resynchNav(button)
{
	try {
		parent.parent.parent.parent.ContentFrame.ContentToolbarFrame.resynch(button);
	} catch(e){
	}
	if (isIE && button && document.getElementById(button)){
		document.getElementById(button).blur();
	}
}

function toggleShowAll(button){
	window.parent.parent.toggleShowAll();
	if (isIE && button && document.getElementById(button)){
		document.getElementById(button).blur();
	}
}

function removeBookmark(button){
	try {
		parent.bookmarksViewFrame.removeBookmark();
	} catch(e){
	}
	if (isIE && button && document.getElementById(button)){
		document.getElementById(button).blur();
	}
}

function removeAllBookmarks(button){
	try {
		parent.bookmarksViewFrame.removeAllBookmarks();
	} catch(e){
	}
	if (isIE && button && document.getElementById(button)){
		document.getElementById(button).blur();
	}
}