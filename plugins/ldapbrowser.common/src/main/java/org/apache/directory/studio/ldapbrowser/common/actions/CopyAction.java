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

package org.apache.directory.studio.ldapbrowser.common.actions;


import java.awt.datatransfer.Clipboard;
import java.util.Arrays;
import java.util.LinkedHashSet;

import org.apache.directory.studio.ldapbrowser.common.actions.proxy.BrowserActionProxy;
import org.apache.directory.studio.ldapbrowser.common.dnd.EntryTransfer;
import org.apache.directory.studio.ldapbrowser.common.dnd.SearchTransfer;
import org.apache.directory.studio.ldapbrowser.common.dnd.ValuesTransfer;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldifparser.LdifUtils;
import org.apache.directory.studio.valueeditors.IValueEditor;
import org.apache.directory.studio.valueeditors.ValueEditorManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;


/**
 * This class implements the Copy Action
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CopyAction extends BrowserAction
{
    private BrowserActionProxy pasteActionProxy;

    private ValueEditorManager valueEditorManager;


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
     * Creates a new instance of CopyAction.
     *
     * @param pasteActionProxy
     *      the associated Paste Action
     */
    public CopyAction( BrowserActionProxy pasteActionProxy, ValueEditorManager valueEditorManager )
    {
        super();
        this.pasteActionProxy = pasteActionProxy;
        this.valueEditorManager = valueEditorManager;
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        // entry/searchresult/bookmark
        IEntry[] entries = getEntries();
        if ( entries != null )
        {
            return entries.length > 1 ? Messages.getString( "CopyAction.CopyEntriesDNs" ) : Messages.getString( "CopyAction.CopyEntryDN" ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // searches
        ISearch[] searches = getSearches();
        if ( searches != null )
        {
            return searches.length > 1 ? Messages.getString( "CopyAction.CopySearches" ) : Messages.getString( "CopyAction.CopySearch" ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // values
        IValue[] values = getValues();
        if ( values != null )
        {
            return values.length > 1 ? Messages.getString( "CopyAction.CopyValues" ) : Messages.getString( "CopyAction.CopyValue" ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return Messages.getString( "CopyAction.Copy" ); //$NON-NLS-1$
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
        return "copy";//IWorkbenchActionDefinitionIds.COPY;
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        IEntry[] entries = getEntries();
        ISearch[] searches = getSearches();
        IValue[] values = getValues();
        String[] stringProperties = getSelectedProperties();

        // entry/searchresult/bookmark
        if ( entries != null )
        {
            StringBuffer text = new StringBuffer();
            for ( int i = 0; i < entries.length; i++ )
            {
                text.append( entries[i].getDn().getUpName() );
                if ( i + 1 < entries.length )
                {
                    text.append( BrowserCoreConstants.LINE_SEPARATOR );
                }
            }
            copyToClipboard( new Object[]
                { entries, text.toString() }, new Transfer[]
                { EntryTransfer.getInstance(), TextTransfer.getInstance() } );
        }

        // searches
        if ( searches != null )
        {
            copyToClipboard( new Object[]
                { searches }, new Transfer[]
                { SearchTransfer.getInstance() } );
        }

        // values
        else if ( values != null )
        {
            StringBuffer text = new StringBuffer();

            for ( int i = 0; i < values.length; i++ )
            {
                IValue value = values[i];

                if ( valueEditorManager != null )
                {
                    IValueEditor ve = valueEditorManager.getCurrentValueEditor( value );
                    String displayValue = ve.getDisplayValue( value );
                    text.append( displayValue );
                }
                else if ( values[i].isString() )
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

        // string properties
        else if ( stringProperties != null && stringProperties.length > 0 )
        {
            StringBuffer text = new StringBuffer();

            for ( int i = 0; i < stringProperties.length; i++ )
            {
                text.append( stringProperties[i] );
                if ( i + 1 < stringProperties.length )
                {
                    text.append( BrowserCoreConstants.LINE_SEPARATOR );
                }
            }

            copyToClipboard( new Object[]
                { text.toString() }, new Transfer[]
                { TextTransfer.getInstance() } );
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
//        Clipboard clipboard = null;
//        try
//        {
//            clipboard = new Clipboard( Display.getCurrent() );
//            clipboard.setContents( data, dataTypes );
//        }
//        finally
//        {
//            if ( clipboard != null )
//                clipboard.dispose();
//        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        // entry/searchresult/bookmark
        if ( getEntries() != null )
        {
            return true;
        }

        // searches
        if ( getSearches() != null )
        {
            return true;
        }

        // values
        else if ( getValues() != null )
        {
            return true;
        }

        // string properties
        else if ( getSelectedProperties() != null && getSelectedProperties().length > 0 )
        {
            return true;
        }

        else
        {
            return false;
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
            LinkedHashSet<IEntry> entriesSet = new LinkedHashSet<IEntry>();
            for ( IEntry entry : getSelectedEntries() )
            {
                entriesSet.add( entry );
            }
            for ( ISearchResult searchResult : getSelectedSearchResults() )
            {
                entriesSet.add( searchResult.getEntry() );
            }
            for ( IBookmark bookmark : getSelectedBookmarks() )
            {
                entriesSet.add( bookmark.getEntry() );
            }
            return entriesSet.toArray( new IEntry[entriesSet.size()] );
        }
        else
        {
            return null;
        }
    }


    /**
     * Get the Searches
     *
     * @return
     *      the Searches
     */
    private ISearch[] getSearches()
    {
        if ( getSelectedConnections().length + getSelectedEntries().length + getSelectedSearchResults().length
            + getSelectedBookmarks().length + getSelectedAttributeHierarchies().length + getSelectedAttributes().length
            + getSelectedValues().length == 0
            && getSelectedSearches().length > 0 )
        {
            LinkedHashSet<ISearch> searchesSet = new LinkedHashSet<ISearch>();
            for ( ISearch search : getSelectedSearches() )
            {
                searchesSet.add( search );
            }
            return searchesSet.toArray( new ISearch[searchesSet.size()] );
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
            LinkedHashSet<IValue> valuesSet = new LinkedHashSet<IValue>();
            for ( AttributeHierarchy ah : getSelectedAttributeHierarchies() )
            {
                for ( IAttribute attribute : ah.getAttributes() )
                {
                    valuesSet.addAll( Arrays.asList( attribute.getValues() ) );
                }
            }
            for ( IAttribute attribute : getSelectedAttributes() )
            {
                valuesSet.addAll( Arrays.asList( attribute.getValues() ) );
            }
            for ( IValue value : getSelectedValues() )
            {
                valuesSet.add( value );
            }
            return valuesSet.toArray( new IValue[valuesSet.size()] );
        }
        else
        {
            return null;
        }
    }
}
