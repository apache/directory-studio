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

package org.apache.directory.ldapstudio.browser.common.actions;


import java.util.Arrays;
import java.util.LinkedHashSet;

import org.apache.directory.ldapstudio.browser.common.actions.proxy.BrowserActionProxy;
import org.apache.directory.ldapstudio.browser.common.dnd.ConnectionTransfer;
import org.apache.directory.ldapstudio.browser.common.dnd.EntryTransfer;
import org.apache.directory.ldapstudio.browser.common.dnd.ValuesTransfer;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.utils.LdifUtils;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;


/**
 * This class implements the Copy Action
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class CopyAction extends BrowserAction
{
    protected BrowserActionProxy pasteActionProxy;


    /**
     * Creates a new instance of CopyAction.
     *
     * @param pasteActionProxy
     *      the associated Paste Action
     */
    public CopyAction( BrowserActionProxy pasteActionProxy )
    {
        super();
        this.pasteActionProxy = pasteActionProxy;
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {

        // connection
        IConnection[] connections = getConnections();
        if ( connections != null )
        {
            return connections.length > 1 ? "Copy Connections" : "Copy Connection";
        }

        // entry/searchresult/bookmark
        IEntry[] entries = getEntries();
        if ( entries != null )
        {
            return entries.length > 1 ? "Copy Entries / DNs" : "Copy Entry / DN";
        }

        // values
        IValue[] values = getValues();
        if ( values != null )
        {
            return values.length > 1 ? "Copy Values" : "Copy Value";
        }

        return "Copy";
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor( ISharedImages.IMG_TOOL_COPY );
    }


    /**
     * {@inheritDoc}
     */
    public String getCommandId()
    {
        return IWorkbenchActionDefinitionIds.COPY;
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        IConnection[] connections = getConnections();
        IEntry[] entries = getEntries();
        IValue[] values = getValues();

        // connection
        if ( connections != null )
        {
            copyToClipboard( new Object[]
                { connections }, new Transfer[]
                { ConnectionTransfer.getInstance() } );
        }

        // entry/searchresult/bookmark
        else if ( entries != null )
        {
            StringBuffer text = new StringBuffer();
            for ( int i = 0; i < entries.length; i++ )
            {
                text.append( entries[i].getDn().toString() );
                if ( i + 1 < entries.length )
                {
                    text.append( BrowserCoreConstants.LINE_SEPARATOR );
                }
            }
            copyToClipboard( new Object[]
                { entries, text.toString() }, new Transfer[]
                { EntryTransfer.getInstance(), TextTransfer.getInstance() } );
        }

        // values
        else if ( values != null )
        {

            // LdifAttrValLine[] lines = new LdifAttrValLine[values.length];
            StringBuffer text = new StringBuffer();

            for ( int i = 0; i < values.length; i++ )
            {

                // lines[i] = ModelConverter.valueToLdifAttrValLine(values[i]);

                if ( values[i].isString() )
                {
                    text.append( values[i].getStringValue() );
                }
                else if ( values[i].isBinary() )
                {
                    text.append( LdifUtils.base64encode( values[i].getBinaryValue() ) );
                }
                if ( i + 1 < values.length )
                {
                    text.append( BrowserCoreConstants.LINE_SEPARATOR );
                }
            }

            copyToClipboard( new Object[]
                { values, text.toString() }, new Transfer[]
                { ValuesTransfer.getInstance(), TextTransfer.getInstance() } );
        }

        // update paste action
        if ( this.pasteActionProxy != null )
        {
            this.pasteActionProxy.updateAction();
        }
    }


    /**
     * Copies data to Clipboard
     *
     * @param data
     *      the data to be set in the clipboard
     * @param dataTypes
     *      the transfer agents that will convert the data to its platform specific format; 
     *      each entry in the data array must have a corresponding dataType
     */
    public static void copyToClipboard( Object[] data, Transfer[] dataTypes )
    {
        Clipboard clipboard = null;
        try
        {
            clipboard = new Clipboard( Display.getCurrent() );
            clipboard.setContents( data, dataTypes );
        }
        finally
        {
            if ( clipboard != null )
                clipboard.dispose();
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {

        // connection
        if ( getConnections() != null )
        {
            return true;
        }

        // entry/searchresult/bookmark
        else if ( getEntries() != null )
        {
            return true;
        }

        // values
        else if ( getValues() != null )
        {
            return true;
        }

        else
        {
            return false;
        }
    }


    /**
     * Get the Connections
     *
     * @return
     *      the Connections
     */
    private IConnection[] getConnections()
    {

        if ( getSelectedEntries().length + getSelectedSearchResults().length + getSelectedBookmarks().length
            + getSelectedSearches().length + getSelectedAttributeHierarchies().length + getSelectedAttributes().length
            + getSelectedValues().length == 0
            && getSelectedConnections().length > 0 )
        {
            return getSelectedConnections();
        }
        else
        {
            return null;
        }
    }


    /**
     * Get the Entries
     *
     * @return
     *      the Entries
     */
    private IEntry[] getEntries()
    {
        if ( getSelectedConnections().length + getSelectedSearches().length + getSelectedAttributeHierarchies().length
            + getSelectedAttributes().length + getSelectedValues().length == 0
            && getSelectedEntries().length + getSelectedSearchResults().length + getSelectedBookmarks().length > 0 )
        {

            LinkedHashSet entriesSet = new LinkedHashSet();
            for ( int i = 0; i < getSelectedEntries().length; i++ )
            {
                entriesSet.add( getSelectedEntries()[i] );
            }
            for ( int i = 0; i < this.getSelectedSearchResults().length; i++ )
            {
                entriesSet.add( this.getSelectedSearchResults()[i].getEntry() );
            }
            for ( int i = 0; i < this.getSelectedBookmarks().length; i++ )
            {
                entriesSet.add( this.getSelectedBookmarks()[i].getEntry() );
            }
            return ( IEntry[] ) entriesSet.toArray( new IEntry[entriesSet.size()] );
        }
        else
        {
            return null;
        }
    }


    /**
     * Get the Values
     *
     * @return
     *      the Values
     */
    private IValue[] getValues()
    {
        if ( getSelectedConnections().length + getSelectedBookmarks().length + getSelectedEntries().length
            + getSelectedSearches().length == 0
            && getSelectedAttributeHierarchies().length + getSelectedAttributes().length + getSelectedValues().length > 0 )
        {

            LinkedHashSet valuesSet = new LinkedHashSet();
            for ( int i = 0; i < this.getSelectedAttributeHierarchies().length; i++ )
            {
                IAttribute[] attributes = getSelectedAttributeHierarchies()[i].getAttributes();
                for ( int k = 0; k < attributes.length; k++ )
                {
                    valuesSet.addAll( Arrays.asList( attributes[k].getValues() ) );
                }
            }
            for ( int i = 0; i < this.getSelectedAttributes().length; i++ )
            {
                valuesSet.addAll( Arrays.asList( this.getSelectedAttributes()[i].getValues() ) );
            }
            for ( int i = 0; i < this.getSelectedValues().length; i++ )
            {
                valuesSet.add( this.getSelectedValues()[i] );
            }
            return ( IValue[] ) valuesSet.toArray( new IValue[valuesSet.size()] );
        }
        else
        {
            return null;
        }
    }
}
