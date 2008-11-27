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

package org.apache.directory.studio.ldapbrowser.ui.editors.searchresult;


import org.apache.directory.studio.ldapbrowser.common.actions.PasteAction;
import org.apache.directory.studio.ldapbrowser.common.dnd.ValuesTransfer;
import org.apache.directory.studio.ldapbrowser.core.jobs.CreateValuesJob;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Value;


/**
 * Special paste action to copy the values of a copied attr-val to another
 * attribute.
 */
public class SearchResultEditorPasteAction extends PasteAction
{

    /**
     * Creates a new instance of SearchResultEditorPasteAction.
     */
    public SearchResultEditorPasteAction()
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
            return values.length > 1 ? Messages.getString( "SearchResultEditorPasteAction.PasteValues" ) : Messages.getString( "SearchResultEditorPasteAction.PasteValue" ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return Messages.getString( "SearchResultEditorPasteAction.Paste" ); //$NON-NLS-1$
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
            IAttribute attribute = getSelectedAttributeHierarchies()[0].getAttribute();
            IEntry entry = attribute.getEntry();

            IValue[] newValues = new IValue[values.length];
            for ( int v = 0; v < values.length; v++ )
            {
                newValues[v] = new Value( attribute, values[v].getRawValue() );
            }

            new CreateValuesJob( entry, newValues ).execute();
        }
    }


    /**
     * Conditions:
     * <li> an search result and a mv-attribute are selected
     * <li> there are IValues in clipboard.
     * 
     * @return the values to paste
     */
    private IValue[] getValuesToPaste()
    {
        if ( getSelectedEntries().length + getSelectedBookmarks().length + getSelectedValues().length
            + getSelectedAttributes().length + getSelectedSearches().length == 0
            && getSelectedSearchResults().length == 1
            && getSelectedAttributeHierarchies().length == 1
            && getSelectedAttributeHierarchies()[0].size() == 1 )
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
