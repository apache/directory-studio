/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */

package org.apache.directory.ldapstudio.browser.ui.views.browser;

import org.eclipse.swt.dnd.DragSourceListener;


/**
 * This class implements the {@link DragSourceListener} for the browser view.
 * 
 * TODO: The code isn't working yet, just a test implementation.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DragListener /* implements DragSourceListener */
{

    // private Clipboard systemClipboard;
    // private InternalClipboard internalClipboard;
    //    
    // public DragListener(Shell shell, Clipboard systemClipboard,
    // InternalClipboard internalClipboard) {
    // super(shell, "Drag", null, null);
    // this.systemClipboard = systemClipboard;
    // this.internalClipboard = internalClipboard;
    // }
    // public void dispose() {
    // this.systemClipboard = null;
    // this.internalClipboard = null;
    // super.dispose();
    // }
    // protected void updateEnabledState() {
    // }
    //    
    // public void dragStart(DragSourceEvent event) {
    // //System.out.println("dragStart: " + event);
    //
    // if(this.selectedEntries.length > 0 && this.selectedSearches.length ==
    // 0 && this.selectedSearchResults.length == 0) {
    // /*
    // IEntry parent = this.selectedEntries[0].getParententry();
    // for(int i=1; i<this.selectedEntries.length; i++) {
    // if(this.selectedEntries[i].getParententry() != parent) {
    // event.doit = false;
    // return;
    // }
    // }
    // */
    // this.internalClipboard.newTransfer(InternalClipboard.TYPE_UNKNOWN,
    // this.selectedEntries);
    // event.doit = true;
    // }
    // else if(this.selectedSearchResults.length > 0 &&
    // this.selectedEntries.length == 0 && this.selectedSearches.length ==
    // 0) {
    // /*
    // IEntry parent =
    // this.selectedSearchResults[0].getEntry().getParententry();
    // for(int i=1; i<this.selectedSearchResults.length; i++) {
    // if(this.selectedSearchResults[i].getEntry().getParententry() !=
    // parent) {
    // event.doit = false;
    // return;
    // }
    // }
    // */
    // this.internalClipboard.newTransfer(InternalClipboard.TYPE_UNKNOWN,
    // this.selectedSearchResults);
    // event.doit = true;
    // }
    // else if(this.selectedSearches.length > 0 &&
    // this.selectedEntries.length == 0 && this.selectedSearchResults.length
    // == 0) {
    // this.internalClipboard.newTransfer(InternalClipboard.TYPE_UNKNOWN,
    // this.selectedSearches);
    // event.doit = true;
    // }
    // else {
    // event.doit = false;
    // }
    // }
    //
    // public void dragSetData(DragSourceEvent event) {
    // //System.out.println("dragSetDataA: " + event);
    // //System.out.println("dragSetDataT: " + event.dataType);
    // //System.out.println("dragSetDataS: " +
    // BrowserTransfer.getInstance().isSupportedType(event.dataType));
    // //System.out.println("dragSetDataE: " +
    // TextTransfer.getInstance().isSupportedType(event.dataType));
    //        
    // if (BrowserTransfer.getInstance().isSupportedType(event.dataType)) {
    // event.data = this.internalClipboard.getClass().getName();
    // }
    // else if (TextTransfer.getInstance().isSupportedType(event.dataType))
    // {
    // String text = "";
    // Object objectToTransfer =
    // this.internalClipboard.getObjectToTransfer();
    // if(objectToTransfer instanceof IEntry[]) {
    // IEntry[] entries = (IEntry[])objectToTransfer;
    // if(entries.length > 0) {
    // text = entries[0].getDn().toString();
    // for(int i=1; i<entries.length; i++) {
    // text += "\n"+entries[i].getDn().toString();
    // }
    // }
    // }
    // else if(objectToTransfer instanceof ISearchResult[]) {
    // ISearchResult[] searchresults = (ISearchResult[])objectToTransfer;
    // if(searchresults.length > 0) {
    // text = searchresults[0].getDn().toString();
    // for(int i=1; i<searchresults.length; i++) {
    // text += "\n"+searchresults[i].getDn().toString();
    // }
    // }
    // }
    // else if(objectToTransfer instanceof ISearch[]) {
    // ISearch[] searches = (ISearch[])objectToTransfer;
    // if(searches.length > 0) {
    // text = searches[0].getName();
    // for(int i=1; i<searches.length; i++) {
    // text += "\n"+searches[i].getName();
    // }
    // }
    // }
    // event.data = text;
    // }
    // else {
    // event.data = null;
    // }
    //        
    // //System.out.println("dragSetData2: " + event);
    // }
    //
    // public void dragFinished(org.eclipse.swt.dnd.DragSourceEvent event) {
    // //System.out.println("dragFinished: " + event);
    // this.internalClipboard.clear();
    // }

}
