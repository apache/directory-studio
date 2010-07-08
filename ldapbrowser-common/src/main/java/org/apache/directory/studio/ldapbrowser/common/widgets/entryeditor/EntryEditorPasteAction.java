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

package org.apache.directory.studio.ldapbrowser.common.widgets.entryeditor;


import org.apache.directory.studio.ldapbrowser.common.actions.PasteAction;
import org.apache.directory.studio.ldapbrowser.common.dnd.ValuesTransfer;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.utils.CompoundModification;


/**
 * This class implements the paste action for the tabular entry editor. 
 * It copies attribute-values to another entry.
 * It does not invoke an UpdateEntryRunnable but only updates the model.
 */
public class EntryEditorPasteAction extends PasteAction
{

    /**
     * Creates a new instance of EntryEditorPasteAction.
     */
    public EntryEditorPasteAction()
    {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        IValue[] values = getValuesToPaste();
        if ( values != null )
        {
            return values.length > 1 ? Messages.getString( "EntryEditorPasteAction.PasteValues" ) : Messages.getString( "EntryEditorPasteAction.PasteValue" ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return Messages.getString( "EntryEditorPasteAction.Paste" ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        if ( getValuesToPaste() != null )
        {
            return true;
        }

        return false;
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        IValue[] values = getValuesToPaste();
        if ( values != null )
        {
            IEntry entry = null;
            if ( getInput() instanceof IEntry )
            {
                entry = ( IEntry ) getInput();
            }
            else if ( getInput() instanceof AttributeHierarchy )
            {
                entry = ( ( AttributeHierarchy ) getInput() ).getEntry();
            }

            if ( entry != null )
            {
                // only modify the model
                // the modification at the directory is done by EntryEditorManager
                new CompoundModification().createValues( entry, values );
            }
        }
    }


    /**
     * Conditions: 
     * <li>the input is an entry or attribute hierarchy</li>
     * <li>there are values in clipboard</li>
     * 
     * @return the values to paste
     */
    private IValue[] getValuesToPaste()
    {
        if ( getInput() instanceof IEntry || getInput() instanceof AttributeHierarchy )
        {
            Object content = this.getFromClipboard( ValuesTransfer.getInstance() );
            if ( content != null && content instanceof IValue[] )
            {
                IValue[] values = ( IValue[] ) content;
                return values;
            }
        }

        return null;
    }

}
