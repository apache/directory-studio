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

package org.apache.directory.studio.ldapbrowser.ui.editors.entry;


import org.apache.directory.studio.entryeditors.EntryEditorInput;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;


/**
 * An entry editor the opens all entries in one single editor tab.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SingleTabEntryEditor extends EntryEditor
{

    /**
     * Gets the ID of the SingleTabEntryEditor.
     * 
     * @return the id of the SingleTabEntryEditor
     */
    public static String getId()
    {
        return BrowserUIConstants.EDITOR_SINGLE_TAB_ENTRY_EDITOR;
    }


    public boolean isAutoSave()
    {
        return true;
    }


    @Override
    protected void setEditorName( EntryEditorInput eei )
    {
        setPartName( eei.getName() );
    }

}
