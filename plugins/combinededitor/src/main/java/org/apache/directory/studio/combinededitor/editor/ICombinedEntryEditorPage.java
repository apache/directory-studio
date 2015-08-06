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
package org.apache.directory.studio.combinededitor.editor;


import org.eclipse.swt.custom.CTabItem;


/**
 * This interface defines a page for the editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface ICombinedEntryEditorPage
{
    /**
     * Disposes any allocated resource.
     */
    void dispose();


    /**
     * This method is called when editor input has changed.
     */
    void editorInputChanged();


    /**
     * Gets the associated editor.
     *
     * @return
     *      the associated editor
     */
    CombinedEntryEditor getEditor();


    /**
     * Gets the {@link CTabItem} associated with the editor page.
     *
     * @return
     *      the {@link CTabItem} associated with the editor page
     */
    CTabItem getTabItem();


    /**
     * Initializes the control of the page.
     */
    void init();


    /**
     * Returns whether or not the editor page has been initialized.
     *
     * @return
     *      <code>true</code> if the editor page has been initialized,
     *      <code>false</code> if not.
     */
    boolean isInitialized();


    /**
     * Asks this part to take focus within the workbench. Parts must
     * assign focus to one of the controls contained in the part's
     * parent composite.
     */
    void setFocus();


    /**
     * This method is called when then editor page needs to be updated. 
     */
    void update();
}
