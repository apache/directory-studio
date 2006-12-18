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

package org.apache.directory.ldapstudio.browser.ui.editors.searchresult;


import org.apache.directory.ldapstudio.browser.core.jobs.CreateValuesJob;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.ui.actions.PasteAction;
import org.apache.directory.ldapstudio.browser.ui.dnd.ValuesTransfer;


/**
 * Special paste action to copy the values of a copied attr-val to another
 * attribute.
 */
public class SearchResultEditorPasteAction extends PasteAction
{

    public SearchResultEditorPasteAction()
    {
        super();
    }


    public String getText()
    {
        IValue[] values = getValuesToPaste();
        if ( values != null )
        {
            return values.length > 1 ? "Paste Values" : "Paste Value";
        }

        return "Paste";
    }


    public boolean isEnabled()
    {
        if ( this.getValuesToPaste() != null )
        {
            return true;
        }

        return false;
    }


    public void run()
    {
        IValue[] values = getValuesToPaste();
        if ( values != null )
        {

            String attributeDescription = getSelectedAttributeHierarchies()[0].getAttribute().getDescription();

            String[] attributeDescriptions = new String[values.length];
            Object[] rawValues = new Object[values.length];
            for ( int v = 0; v < values.length; v++ )
            {
                attributeDescriptions[v] = attributeDescription;
                rawValues[v] = values[v].getRawValue();
            }
            IEntry entry = getSelectedAttributeHierarchies()[0].getAttribute().getEntry();

            new CreateValuesJob( entry, attributeDescriptions, rawValues ).execute();
        }
    }


    /**
     * Conditions: - an search result and a multiattribute are selected -
     * there are IValues in clipboard
     * 
     * @return
     */
    private IValue[] getValuesToPaste()
    {
        if ( getSelectedEntries().length + getSelectedBookmarks().length + getSelectedValues().length
            + getSelectedAttributes().length + getSelectedSearches().length + getSelectedConnections().length == 0
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

            // Object content =
            // this.getFromClipboard(LdifAttrValLinesTransfer.getInstance());
            // if (content != null && content instanceof LdifAttrValLine[])
            // {
            // LdifAttrValLine[] lines = (LdifAttrValLine[]) content;
            // return lines;
            // }
        }

        return null;
    }

}
