/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

var isSafari = (navigator.userAgent.indexOf('Safari/') != -1)
			|| (navigator.userAgent.indexOf('AppleWebKit/') != -1);
var highlighted=false;
var startTime;
var MAX_DURATION=3000;
onload=highlight;
document.onreadystatechange=highlight;
function highlight(){
	if(highlighted){
		return;
	}
	highlighted=true;
	if (!document.body) return;
	if(document.body.innerHTML.length < 50000){
		for(i=0; i<keywords.length; i++){
			word=keywords[i].toLowerCase();
			highlightWordInNode(word, document.body);
		}
	}else{
		startTime=new Date().getTime();
		for(i=0; i<keywords.length; i++){
			word=keywords[i].toLowerCase();
			highlightWordInNodeTimed(word, document.body);
			if(new Date().getTime()>startTime+MAX_DURATION) return;
		}
	}
}
function highlightWordInNode(aWord, aNode){
    if (aNode.nodeType == 1){
    	var children = aNode.childNodes;
    	for(var i=0; i < children.length; i++) {
    		highlightWordInNode(aWord, children[i]);
    	}
    }
    else if(aNode.nodeType==3){
    	highlightWordInText(aWord, aNode);
	}

}
function highlightWordInNodeTimed(aWord, aNode){
    if (aNode.nodeType == 1){
    	var children = aNode.childNodes;
    	for(var i=0; i < children.length; i++) {
    		highlightWordInNodeTimed(aWord, children[i]);
			if(new Date().getTime()>startTime+MAX_DURATION) return;
    	}
    }
    else if(aNode.nodeType==3){
    	highlightWordInText(aWord, aNode);
	}

}
function highlightWordInText(aWord, textNode){
	allText=new String(textNode.data);
	allTextLowerCase=allText.toLowerCase();
	index=allTextLowerCase.indexOf(aWord);
	if(index>=0){
		// create a node to replace the textNode so we end up
		// not changing number of children of textNode.parent
		replacementNode=document.createElement("span");
		textNode.parentNode.insertBefore(replacementNode, textNode);
		while(index>=0){
			before=allText.substring(0,index);
			newBefore=document.createTextNode(before);
			replacementNode.appendChild(newBefore);
			spanNode=document.createElement("span");
			if(isSafari){
				spanNode.style.color="#000000";
				spanNode.style.background="#B5D5FF";
			}else{
				spanNode.style.background="Highlight";
				spanNode.style.color="HighlightText";
			}
			replacementNode.appendChild(spanNode);
			boldText=document.createTextNode(allText.substring(index,index+aWord.length));
			spanNode.appendChild(boldText);
			allText=allText.substring(index+aWord.length);
			allTextLowerCase=allText.toLowerCase();
			index=allTextLowerCase.indexOf(aWord);
		}
		newAfter=document.createTextNode(allText);
		replacementNode.appendChild(newAfter);
		textNode.parentNode.removeChild(textNode);
	}
}
