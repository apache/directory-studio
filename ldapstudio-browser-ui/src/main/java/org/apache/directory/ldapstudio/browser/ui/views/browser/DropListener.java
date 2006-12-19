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


public class DropListener /* implements DropTargetListener, ModelModifier */
{/*
 * 
 * private Shell shell; private Clipboard systemClipboard; private
 * InternalClipboard internalClipboard;
 * 
 * 
 * public DropListener(Shell shell, Clipboard systemClipboard,
 * InternalClipboard internalClipboard) { this.shell = shell;
 * this.systemClipboard = systemClipboard; this.internalClipboard =
 * internalClipboard;
 *  } public void dispose() { this.shell = null; this.systemClipboard =
 * null; this.internalClipboard = null;
 *  }
 * 
 * private int dragOperation = DND.DROP_NONE;
 * 
 * public void dragEnter(DropTargetEvent event) {
 * //System.out.println("dragEnter: " + event.detail); dragOperation =
 * event.detail; event.currentDataType =
 * BrowserTransfer.getInstance().getSupportedTypes()[0];
 * this.check(event); }
 * 
 * public void dragLeave(DropTargetEvent event) {
 * //System.out.println("dragLeave: " + event.detail); dragOperation =
 * DND.DROP_NONE; }
 * 
 * public void dragOperationChanged(DropTargetEvent event) {
 * //System.out.println("dragOperationChanged: " + event.detail);
 * dragOperation = event.detail; this.check(event); }
 * 
 * public void dragOver(DropTargetEvent event) {
 * //System.out.println("dragOver: " + event.item.getData());
 * 
 * this.check(event);
 * 
 * //System.out.println("dragOver: " + event.detail);
 * 
 * //System.out.println();
 * //System.out.println(event.item.getClass().getName());
 * //System.out.println(event.item.getData().getClass().getName());
 * //System.out.println(event.currentDataType.type); }
 * 
 * private void check(DropTargetEvent event) { if(event.item != null &&
 * event.item.getData() != null) { if
 * (BrowserTransfer.getInstance().isSupportedType(event.currentDataType)) {
 * if(event.item.getData() instanceof ISearch &&
 * this.internalClipboard.getObjectToTransfer() instanceof ISearch[]) {
 * event.detail = dragOperation; return; } else if(event.item.getData()
 * instanceof BrowserCategory &&
 * ((BrowserCategory)event.item.getData()).getType() ==
 * BrowserCategory.TYPE_SEARCHES &&
 * this.internalClipboard.getObjectToTransfer() instanceof ISearch[]) {
 * event.detail = dragOperation; return; } else if(event.item.getData()
 * instanceof IEntry && this.internalClipboard.getObjectToTransfer()
 * instanceof IEntry[]) { event.detail = dragOperation;
 * 
 * IEntry[] entries =
 * (IEntry[])this.internalClipboard.getObjectToTransfer(); for(int i=0;
 * i<entries.length; i++) { if(entries[i].hasChildren()) { event.detail =
 * DND.DROP_COPY; return; } }
 * 
 * return; } else if(event.item.getData() instanceof IEntry &&
 * this.internalClipboard.getObjectToTransfer() instanceof
 * ISearchResult[]) { event.detail = dragOperation;
 * 
 * ISearchResult[] srs =
 * (ISearchResult[])this.internalClipboard.getObjectToTransfer();
 * for(int i=0; i<srs.length; i++) {
 * if(!srs[i].getEntry().hasChildren()) { event.detail = DND.DROP_COPY;
 * return; } }
 * 
 * return; } else { event.detail = DND.DROP_NONE; } } else {
 * event.detail = DND.DROP_NONE; } } else { event.detail =
 * DND.DROP_NONE; } }
 * 
 * public void drop(DropTargetEvent event) {
 * 
 * //System.out.println("drop: " + event);
 * 
 * try {
 * 
 * if
 * (BrowserTransfer.getInstance().isSupportedType(event.currentDataType) &&
 * event.data instanceof String &&
 * this.internalClipboard.getClass().getName().equals(event.data)) {
 * 
 * if(event.detail == DND.DROP_MOVE) {
 * this.internalClipboard.setOperationType(InternalClipboard.TYPE_MOVE_OPERATION); }
 * else if(event.detail == DND.DROP_COPY) {
 * this.internalClipboard.setOperationType(InternalClipboard.TYPE_DUPLICATE_OPERATION); }
 * else {
 * this.internalClipboard.setOperationType(InternalClipboard.TYPE_UNKNOWN); }
 * 
 * 
 * ISearch[] selectedSearches = new ISearch[0]; IEntry[] selectedEntries =
 * new IEntry[0]; ISearchResult[] selectedSearchResults = new
 * ISearchResult[0]; BrowserCategory[] selectedBrowserViewCategories =
 * new BrowserCategory[0];
 * 
 * if (event.item != null && event.item.getData() instanceof ISearch) {
 * selectedSearches = new ISearch[]{(ISearch) event.item.getData()}; }
 * else if (event.item != null && event.item.getData() instanceof
 * IEntry) { selectedEntries = new IEntry[]{(IEntry)
 * event.item.getData()}; } else if (event.item != null &&
 * event.item.getData() instanceof ISearchResult) {
 * selectedSearchResults = new ISearchResult[]{(ISearchResult)
 * event.item.getData()}; } else if (event.item != null &&
 * event.item.getData() instanceof BrowserCategory) {
 * selectedBrowserViewCategories = new
 * BrowserCategory[]{(BrowserCategory) event.item.getData()}; }
 * 
 * this.runPaste(this.internalClipboard, selectedSearches,
 * selectedEntries, selectedSearchResults,
 * selectedBrowserViewCategories);
 * 
 * 
 *  // // get search to handle and its search manager // ISearch[]
 * searches = (ISearch[]) event.data; // for(int i=0; i<searches.length;
 * i++) { // ISearch search = searches[i]; // IConnection connection =
 * search.getConnection(); // SearchManager dragSearchManager =
 * connection.getSearchManager(); // // // get position // //int
 * position = dragSearchManager.indexOf(search); // // // get drop
 * search manager and drop position, // // default is last and the drop
 * search // int dropPosition = -1; // SearchManager dropSearchManager =
 * null; // if (event.item != null && event.item.getData() instanceof
 * ISearch) { // ISearch dropSearch = (ISearch) event.item.getData(); //
 * dropSearchManager = dropSearch.getConnection().getSearchManager(); //
 * dropPosition = dropSearchManager.indexOf(dropSearch); // } else { //
 * dropSearchManager = this.selectedConnection.getSearchManager(); //
 * dropPosition = dropSearchManager.getSearchCount(); // } // if
 * (dropPosition == -1) { // dropSearchManager = dragSearchManager; //
 * dropPosition = dragSearchManager.getSearchCount(); // } // // // if
 * MOVE operation, first remove from old position // if (event.detail ==
 * DND.DROP_MOVE && dragSearchManager == dropSearchManager) { //
 * dragSearchManager.removeSearch(search); //
 * if(dropPosition>dragSearchManager.getSearchCount()) { //
 * dropPosition--; // } // dropSearchManager.addSearch(dropPosition,
 * search); // event.detail = DND.DROP_NONE; // // } else if
 * (event.detail == DND.DROP_COPY || // (event.detail == DND.DROP_MOVE &&
 * dragSearchManager != dropSearchManager)) { // ISearch newSearch =
 * (ISearch) search.clone(); //
 * newSearch.setConnection(dropSearchManager.getConnection()); //
 * dropSearchManager.addSearch(dropPosition, newSearch); // // } // else { //
 * event.detail = DND.DROP_NONE; // } // } } } catch (Exception e) {
 * event.detail = DND.DROP_NONE; e.printStackTrace(); }
 *  }
 * 
 * public void dropAccept(DropTargetEvent event) {
 * //System.out.println("dropAccept: " + event.detail + event.feedback);
 * event.currentDataType =
 * BrowserTransfer.getInstance().getSupportedTypes()[0]; check(event); }
 * 
 * 
 * 
 * 
 * public void runPaste(InternalClipboard internalClipboard, ISearch[]
 * selectedSearches, IEntry[] selectedEntries, ISearchResult[]
 * selectedSearchResults, BrowserCategory[]
 * selectedBrowserViewCategories) { Object objectToTransfer =
 * internalClipboard.getObjectToTransfer();
 * 
 * if (objectToTransfer != null && objectToTransfer instanceof ISearch[] &&
 * selectedSearches.length > 0 && selectedEntries.length == 0 &&
 * selectedSearchResults.length == 0) { ISearch[] searchesToTransfer =
 * (ISearch[]) objectToTransfer; if (searchesToTransfer != null &&
 * searchesToTransfer.length > 0) { ISearch pasteSearch =
 * selectedSearches[selectedSearches.length-1]; IConnection connection =
 * pasteSearch.getConnection(); SearchManager searchManager =
 * connection.getSearchManager();
 * 
 * int index = searchManager.indexOf(pasteSearch);
 * 
 * for(int i=0; i<searchesToTransfer.length; i++) { ISearch newSearch =
 * (ISearch) searchesToTransfer[i].clone();
 * newSearch.setConnection(connection);
 * if(internalClipboard.getOperationType() ==
 * InternalClipboard.TYPE_DUPLICATE_OPERATION) {
 * searchManager.addSearch(index+1+i, newSearch); } else
 * if(internalClipboard.getOperationType() ==
 * InternalClipboard.TYPE_MOVE_OPERATION) {
 * searchesToTransfer[i].getConnection().getSearchManager().removeSearch(searchesToTransfer[i].getName());
 * searchManager.addSearch(index+i, newSearch);
 * if(searchManager.indexOf(pasteSearch) < index) { index =
 * searchManager.indexOf(pasteSearch); } } } } } else if
 * (objectToTransfer != null && objectToTransfer instanceof ISearch[] &&
 * selectedBrowserViewCategories.length == 1 &&
 * selectedBrowserViewCategories[0].getType()==BrowserCategory.TYPE_SEARCHES) {
 * ISearch[] searchesToTransfer = (ISearch[]) objectToTransfer; if
 * (searchesToTransfer != null && searchesToTransfer.length > 0) {
 * IConnection connection =
 * selectedBrowserViewCategories[0].getParent(); SearchManager
 * searchManager = connection.getSearchManager(); for(int i=0; i<searchesToTransfer.length;
 * i++) { ISearch newSearch = (ISearch) searchesToTransfer[i].clone();
 * newSearch.setConnection(connection);
 * if(internalClipboard.getOperationType() ==
 * InternalClipboard.TYPE_DUPLICATE_OPERATION) {
 * searchManager.addSearch(searchManager.getSearchCount(), newSearch); }
 * else if(internalClipboard.getOperationType() ==
 * InternalClipboard.TYPE_MOVE_OPERATION) {
 * searchesToTransfer[i].getConnection().getSearchManager().removeSearch(searchesToTransfer[i].getName());
 * searchManager.addSearch(searchManager.getSearchCount(), newSearch); } } } }
 * else if (objectToTransfer != null && objectToTransfer instanceof
 * IEntry[] && selectedEntries.length == 1 && selectedSearches.length ==
 * 0 && selectedSearchResults.length == 0) { IEntry[] entries =
 * (IEntry[]) objectToTransfer; if (entries != null && entries.length >
 * 0) { if(internalClipboard.getOperationType() ==
 * InternalClipboard.TYPE_DUPLICATE_OPERATION) new
 * CopyEntriesJob(selectedEntries[0], entries).execute(); else
 * if(internalClipboard.getOperationType() ==
 * InternalClipboard.TYPE_MOVE_OPERATION) this.move(selectedEntries[0],
 * entries); } } else if (objectToTransfer != null && objectToTransfer
 * instanceof ISearchResult[] && selectedEntries.length == 1 &&
 * selectedSearches.length == 0 && selectedSearchResults.length == 0) {
 * ISearchResult[] searchResults = (ISearchResult[]) objectToTransfer;
 * IEntry[] entries = new IEntry[searchResults.length]; for(int i=0; i<searchResults.length;
 * i++) { entries[i] = searchResults[i].getEntry(); } if (entries !=
 * null && entries.length > 0) { if(internalClipboard.getOperationType() ==
 * InternalClipboard.TYPE_DUPLICATE_OPERATION) new
 * CopyEntriesJob(selectedEntries[0], entries).execute(); else
 * if(internalClipboard.getOperationType() ==
 * InternalClipboard.TYPE_MOVE_OPERATION) this.move(selectedEntries[0],
 * entries); } }
 * 
 * if(internalClipboard.getOperationType() ==
 * InternalClipboard.TYPE_MOVE_OPERATION) { internalClipboard.clear(); } }
 * 
 * private void move (final IEntry newParent, final IEntry[]
 * entriesToMove) {
 * 
 * for(int i=0; i<entriesToMove.length; i++) { try {
 * entriesToMove[i].moveTo(newParent, this); }
 * catch(ModelModificationException mme) {
 * BrowserUIPlugin.getDefault().getExceptionHandler().handleException(mme.getMessage(),
 * mme); } catch(Exception e) {
 * BrowserUIPlugin.getDefault().getExceptionHandler().handleException(e.getMessage(),
 * e); } }
 * 
 * //EventRegistry.fireEntryUpdated(new
 * ChildrenInitializedEvent(newParent, newParent.getConnection()),
 * this); }
 */
}
