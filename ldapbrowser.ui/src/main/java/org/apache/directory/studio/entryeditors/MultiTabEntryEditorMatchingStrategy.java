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

package org.apache.directory.studio.entryeditors;


import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorMatchingStrategy;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;


/**
 * Matching strategy for a multi tab entry editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class MultiTabEntryEditorMatchingStrategy implements IEditorMatchingStrategy
{

    /**
     * Returns true if the given editor is multi-tab and the input resolved entries
     * are equal.
     */
    public boolean matches( IEditorReference editorRef, IEditorInput input )
    {
        if ( !( input instanceof EntryEditorInput ) )
        {
            return false;
        }
        EntryEditorInput entryEditorInput = ( EntryEditorInput ) input;

        if ( entryEditorInput.getExtension() == null )
        {
            return false;
        }
        if ( !entryEditorInput.getExtension().isMultiWindow() )
        {
            return false;
        }
        if ( !editorRef.getId().equals( entryEditorInput.getExtension().getEditorId() ) )
        {
            return false;
        }

        try
        {
            IEditorInput otherInput = editorRef.getEditorInput();
            if ( !( otherInput instanceof EntryEditorInput ) )
            {
                return false;
            }
            EntryEditorInput otherEntryEditorInput = ( EntryEditorInput ) otherInput;
            if ( entryEditorInput.getResolvedEntry() == null && otherEntryEditorInput.getResolvedEntry() == null )
            {
                return true;
            }
            return entryEditorInput.getResolvedEntry() != null && otherEntryEditorInput.getResolvedEntry() != null
                && entryEditorInput.getResolvedEntry().equals( otherEntryEditorInput.getResolvedEntry() );
        }
        catch ( PartInitException e )
        {
            return false;
        }
    }

}
