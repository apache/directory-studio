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
package org.apache.directory.studio.templateeditor.actions;


import org.apache.directory.studio.entryeditors.EntryEditorInput;
import org.apache.directory.studio.entryeditors.IEntryEditor;
import org.apache.directory.studio.ldapbrowser.common.actions.PropertiesAction;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;


/**
 * This Action opens the Property Dialog for a given object.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EditorPagePropertiesAction extends PropertiesAction
{
    /** The associated editor */
    private IEntryEditor editor;


    /**
     * Creates a new instance of EntryEditorPropertiesAction.
     *
     * @param editor
     *      the associated Entry Editor
     */
    public EditorPagePropertiesAction( IEntryEditor editor )
    {
        super();
        this.editor = editor;
    }


    /**
     * {@inheritDoc}
     */
    public IEntry[] getSelectedEntries()
    {
        // We're only returning the entry when no value is selected
        if ( getSelectedValues().length == 0 )
        {
            if ( editor != null )
            {
                EntryEditorInput input = editor.getEntryEditorInput();

                IEntry entry = ( ( EntryEditorInput ) input ).getResolvedEntry();
                if ( entry != null )
                {
                    return new IEntry[]
                        { entry };
                }
            }
        }

        return new IEntry[0];
    }
}