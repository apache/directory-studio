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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.jobs.DeleteAttributesValueJob;
import org.apache.directory.studio.ldapbrowser.core.jobs.DeleteEntriesJob;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.ModelModificationException;
import org.apache.directory.studio.ldapbrowser.core.model.RDNPart;
import org.apache.directory.studio.ldapbrowser.core.model.schema.ObjectClassDescription;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;


/**
 * This Action implements the Delete Action. It deletes Connections, Entries, Searches, Bookmarks, Attributes or Values.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DeleteAction extends BrowserAction
{
    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        try
        {
            IConnection[] connections = getConnections();
            IEntry[] entries = getEntries();
            ISearch[] searches = getSearches();
            IBookmark[] bookmarks = getBookmarks();
            IAttribute[] attributes = getAttributes();
            IValue[] values = getValues();

            if ( connections.length > 0 && entries.length == 0 && searches.length == 0 && bookmarks.length == 0
                && attributes.length == 0 && values.length == 0 )
            {
                return connections.length > 1 ? "Delete Connections" : "Delete Connection";
            }
            if ( entries.length > 0 && connections.length == 0 && searches.length == 0 && bookmarks.length == 0
                && attributes.length == 0 && values.length == 0 )
            {
                return entries.length > 1 ? "Delete Entries" : "Delete Entry";
            }
            if ( searches.length > 0 && connections.length == 0 && entries.length == 0 && bookmarks.length == 0
                && attributes.length == 0 && values.length == 0 )
            {
                return searches.length > 1 ? "Delete Searches" : "Delete Search";
            }
            if ( bookmarks.length > 0 && connections.length == 0 && entries.length == 0 && searches.length == 0
                && attributes.length == 0 && values.length == 0 )
            {
                return bookmarks.length > 1 ? "Delete Bookmarks" : "Delete Bookmark";
            }
            if ( attributes.length > 0 && connections.length == 0 && entries.length == 0 && searches.length == 0
                && bookmarks.length == 0 && values.length == 0 )
            {
                return attributes.length > 1 ? "Delete Attributes" : "Delete Attribute";
            }
            if ( values.length > 0 && connections.length == 0 && entries.length == 0 && searches.length == 0
                && bookmarks.length == 0 && attributes.length == 0 )
            {
                return values.length > 1 ? "Delete Values" : "Delete Value";
            }
        }
        catch ( Exception e )
        {
        }

        return "Delete";
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor( ISharedImages.IMG_TOOL_DELETE );
    }


    /**
     * {@inheritDoc}
     */
    public String getCommandId()
    {
        return IWorkbenchActionDefinitionIds.DELETE;
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        try
        {
            IConnection[] connections = getConnections();
            IEntry[] entries = getEntries();
            ISearch[] searches = getSearches();
            IBookmark[] bookmarks = getBookmarks();
            IAttribute[] attributes = getAttributes();
            IValue[] values = getValues();

            StringBuffer message = new StringBuffer();

            if ( connections.length > 0 )
            {
                if ( connections.length <= 5 )
                {
                    message.append( connections.length == 1 ? "Are your sure to delete the following connection?"
                        : "Are your sure to delete the following connections?" );
                    for ( int i = 0; i < connections.length; i++ )
                    {
                        message.append( BrowserCoreConstants.LINE_SEPARATOR );
                        message.append( "  - " );
                        message.append( connections[i].getName() );
                    }
                }
                else
                {
                    message.append( "Are your sure to delete the selected connections?" );
                }
                message.append( BrowserCoreConstants.LINE_SEPARATOR );
                message.append( BrowserCoreConstants.LINE_SEPARATOR );
            }

            if ( entries.length > 0 )
            {
                if ( entries.length <= 5 )
                {
                    message
                        .append( entries.length == 1 ? "Are your sure to delete the following entry, including all children?"
                            : "Are your sure to delete the following entries, including all children?" );
                    for ( int i = 0; i < entries.length; i++ )
                    {
                        message.append( BrowserCoreConstants.LINE_SEPARATOR );
                        message.append( "  - " );
                        message.append( entries[i].getDn() );
                    }
                }
                else
                {
                    message.append( "Are your sure to delete the selected entries, including all children?" );
                }
                message.append( BrowserCoreConstants.LINE_SEPARATOR );
                message.append( BrowserCoreConstants.LINE_SEPARATOR );
            }

            if ( searches.length > 0 )
            {
                if ( searches.length <= 5 )
                {
                    message.append( searches.length == 1 ? "Are your sure to delete the following search?"
                        : "Are your sure to delete the following searches?" );
                    for ( int i = 0; i < searches.length; i++ )
                    {
                        message.append( BrowserCoreConstants.LINE_SEPARATOR );
                        message.append( "  - " );
                        message.append( searches[i].getName() );
                    }
                }
                else
                {
                    message.append( "Are your sure to delete the selected searches?" );
                }
                message.append( BrowserCoreConstants.LINE_SEPARATOR );
                message.append( BrowserCoreConstants.LINE_SEPARATOR );
            }

            if ( bookmarks.length > 0 )
            {
                if ( bookmarks.length <= 5 )
                {
                    message.append( bookmarks.length == 1 ? "Are your sure to delete the following bookmark?"
                        : "Are your sure to delete the following bookmarks?" );
                    for ( int i = 0; i < bookmarks.length; i++ )
                    {
                        message.append( BrowserCoreConstants.LINE_SEPARATOR );
                        message.append( "  - " );
                        message.append( bookmarks[i].getName() );
                    }
                }
                else
                {
                    message.append( "Are your sure to delete the selected bookmarks?" );
                }
                message.append( BrowserCoreConstants.LINE_SEPARATOR );
                message.append( BrowserCoreConstants.LINE_SEPARATOR );
            }

            if ( attributes.length > 0 )
            {
                if ( attributes.length <= 5 )
                {
                    message.append( attributes.length == 1 ? "Are your sure to delete the following attribute?"
                        : "Are your sure to delete the following attribute?" );
                    for ( int i = 0; i < attributes.length; i++ )
                    {
                        message.append( BrowserCoreConstants.LINE_SEPARATOR );
                        message.append( "  - " );
                        message.append( attributes[i].getDescription() );
                    }
                }
                else
                {
                    message.append( "Are your sure to delete the selected attributes?" );
                }
                message.append( BrowserCoreConstants.LINE_SEPARATOR );
                message.append( BrowserCoreConstants.LINE_SEPARATOR );
            }

            if ( values.length > 0 )
            {
                boolean emptyValuesOnly = true;
                for ( int i = 0; i < values.length; i++ )
                {
                    if ( !values[i].isEmpty() )
                    {
                        emptyValuesOnly = false;
                    }
                }
                if ( !emptyValuesOnly )
                {
                    if ( values.length <= 5 )
                    {
                        message.append( values.length == 1 ? "Are your sure to delete the following value?"
                            : "Are your sure to delete the following values?" );
                        for ( int i = 0; i < values.length; i++ )
                        {
                            message.append( BrowserCoreConstants.LINE_SEPARATOR );
                            message.append( "  - " );
                            message.append( values[i].toString() );
                        }
                    }
                    else
                    {
                        message.append( "Are your sure to delete the selected values?" );
                    }
                    message.append( BrowserCoreConstants.LINE_SEPARATOR );
                    message.append( BrowserCoreConstants.LINE_SEPARATOR );
                }
            }

            if ( message.length() == 0 || MessageDialog.openConfirm( getShell(), getText(), message.toString() ) )
            {

                if ( connections.length > 0 )
                {
                    deleteConnections( connections );
                }
                if ( entries.length > 0 )
                {
                    deleteEntries( entries );
                }
                if ( searches.length > 0 )
                {
                    deleteSearches( searches );
                }
                if ( bookmarks.length > 0 )
                {
                    deleteBookmarks( bookmarks );
                }
                if ( attributes.length + values.length > 0 )
                {

                    List attributeList = new ArrayList( Arrays.asList( attributes ) );
                    List valueList = new ArrayList( Arrays.asList( values ) );

                    // filter empty attributes and values
                    for ( Iterator it = attributeList.iterator(); it.hasNext(); )
                    {
                        IAttribute att = ( IAttribute ) it.next();
                        IValue[] vals = att.getValues();
                        for ( int i = 0; i < vals.length; i++ )
                        {
                            if ( vals[i].isEmpty() )
                            {
                                att.deleteEmptyValue();
                            }
                        }
                        if ( att.getValueSize() == 0 )
                        {
                            try
                            {
                                att.getEntry().deleteAttribute( att );
                            }
                            catch ( ModelModificationException e )
                            {
                            }
                            it.remove();
                        }
                    }
                    for ( Iterator it = valueList.iterator(); it.hasNext(); )
                    {
                        IValue value = ( IValue ) it.next();
                        if ( value.isEmpty() )
                        {
                            value.getAttribute().deleteEmptyValue();
                            it.remove();
                        }
                    }

                    if ( !attributeList.isEmpty() || !valueList.isEmpty() )
                    {
                        deleteAttributesAndValues( ( IAttribute[] ) attributeList.toArray( new IAttribute[attributeList
                            .size()] ), ( IValue[] ) valueList.toArray( new IValue[valueList.size()] ) );
                    }
                }
            }
        }
        catch ( Exception e )
        {
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        try
        {
            IConnection[] connections = getConnections();
            IEntry[] entries = getEntries();
            ISearch[] searches = getSearches();
            IBookmark[] bookmarks = getBookmarks();
            IAttribute[] attributes = getAttributes();
            IValue[] values = getValues();

            return connections.length + entries.length + searches.length + bookmarks.length + attributes.length
                + values.length > 0;

        }
        catch ( Exception e )
        {
            // e.printStackTrace();
            return false;
        }
    }


    /**
     * Gets the Connections 
     *
     * @return
     *      the Connections
     * @throws Exception
     *      when a is opened
     */
    protected IConnection[] getConnections() throws Exception
    {
        for ( int i = 0; i < getSelectedConnections().length; i++ )
        {
            if ( getSelectedConnections()[i].isOpened() )
            {
                throw new Exception();
            }
        }

        return getSelectedConnections();
    }


    /**
     * Deletes Connections
     *
     * @param connections
     *      the Connections to delete
     */
    protected void deleteConnections( IConnection[] connections )
    {
        for ( int i = 0; i < connections.length; i++ )
        {
            BrowserCorePlugin.getDefault().getConnectionManager().removeConnection( connections[i] );
        }
    }


    /**
     * Gets the Entries.
     *
     * @return
     *      the Entries
     * @throws Exception
     *      when an Entry has parent Entries
     */
    protected IEntry[] getEntries() throws Exception
    {
        LinkedHashSet entriesSet = new LinkedHashSet();
        for ( int i = 0; i < getSelectedEntries().length; i++ )
        {
            if ( !getSelectedEntries()[i].hasParententry() )
            {
                throw new Exception();
            }
            entriesSet.add( getSelectedEntries()[i] );
        }
        for ( int i = 0; i < this.getSelectedSearchResults().length; i++ )
        {
            if ( !getSelectedSearchResults()[i].getEntry().hasParententry() )
            {
                throw new Exception();
            }
            entriesSet.add( this.getSelectedSearchResults()[i].getEntry() );
        }

        IEntry[] allEntries = ( IEntry[] ) entriesSet.toArray( new IEntry[entriesSet.size()] );
        for ( int i = 0; i < allEntries.length; i++ )
        {
            IEntry entry = allEntries[i];
            if ( entriesSet.contains( entry.getParententry() ) )
            {
                entriesSet.remove( entry );
            }
        }

        return ( IEntry[] ) entriesSet.toArray( new IEntry[entriesSet.size()] );
    }


    /**
     * Deletes Entries
     *
     * @param entries
     *      the Entries to delete
     */
    protected void deleteEntries( IEntry[] entries )
    {
        new DeleteEntriesJob( entries ).execute();
    }


    /**
     * Gets the Searches
     *
     * @return
     *      the Searches
     * @throws Exception
     */
    protected ISearch[] getSearches() throws Exception
    {
        return getSelectedSearches();
    }


    /**
     * Delete Searches
     *
     * @param searches
     *      the Searches to delete
     */
    protected void deleteSearches( ISearch[] searches )
    {
        for ( int i = 0; i < searches.length; i++ )
        {
            ISearch search = searches[i];
            search.getConnection().getSearchManager().removeSearch( search );
        }
    }


    /**
     * Get the Bookmarks
     *
     * @return
     * @throws Exception
     */
    protected IBookmark[] getBookmarks() throws Exception
    {
        return getSelectedBookmarks();
    }


    /**
     * Delete Bookmarks
     *
     * @param bookmarks
     *      the Bookmarks to delete
     */
    protected void deleteBookmarks( IBookmark[] bookmarks )
    {
        for ( int i = 0; i < bookmarks.length; i++ )
        {
            IBookmark bookmark = bookmarks[i];
            bookmark.getConnection().getBookmarkManager().removeBookmark( bookmark );
        }
    }


    /**
     * Gets the Attributes
     *
     * @return
     *      the Attributes
     * @throws Exception
     */
    protected IAttribute[] getAttributes() throws Exception
    {

        for ( int i = 0; i < getSelectedAttributes().length; i++ )
        {
            // check if a non-modifyable, must or objectClass attribute is
            // selected
            IAttribute att = getSelectedAttributes()[i];
            if ( !SchemaUtils.isModifyable( att.getAttributeTypeDescription() ) || att.isMustAttribute()
                || att.isObjectClassAttribute() )
            {
                throw new Exception();
            }
        }

        for ( int i = 0; i < getSelectedAttributeHierarchies().length; i++ )
        {
            // check if a non-modifyable, must or objectClass attribute is
            // selected
            AttributeHierarchy ah = getSelectedAttributeHierarchies()[i];
            for ( Iterator it = ah.iterator(); it.hasNext(); )
            {
                IAttribute attribute = ( IAttribute ) it.next();
                if ( !SchemaUtils.isModifyable( attribute.getAttributeTypeDescription() )
                    || attribute.isMustAttribute() || attribute.isObjectClassAttribute() )
                {
                    throw new Exception();
                }
            }
        }

        List attributeList = new ArrayList();

        // add selected attributes
        for ( int i = 0; i < getSelectedAttributes().length; i++ )
        {
            IAttribute attribute = getSelectedAttributes()[i];
            if ( attribute != null && attribute.getValueSize() > 0 )
            {
                attributeList.add( attribute );
            }
        }

        // add selected hierarchies
        for ( int i = 0; i < getSelectedAttributeHierarchies().length; i++ )
        {
            // check if a operational, must or objectClass attribute is
            // selected
            AttributeHierarchy ah = getSelectedAttributeHierarchies()[i];
            for ( Iterator it = ah.iterator(); it.hasNext(); )
            {
                IAttribute attribute = ( IAttribute ) it.next();
                if ( attribute != null && attribute.getValueSize() > 0 )
                {
                    attributeList.add( attribute );
                }
            }
        }

        // check if ALL values of an attribute are selected -> delete whole
        // attribute
        Map attributeNameToSelectedValuesCountMap = new HashMap();
        for ( int i = 0; i < getSelectedValues().length; i++ )
        {
            if ( !attributeNameToSelectedValuesCountMap.containsKey( getSelectedValues()[i].getAttribute()
                .getDescription() ) )
            {
                attributeNameToSelectedValuesCountMap.put( getSelectedValues()[i].getAttribute().getDescription(),
                    new Integer( 0 ) );
            }
            int count = ( ( Integer ) attributeNameToSelectedValuesCountMap.get( getSelectedValues()[i].getAttribute()
                .getDescription() ) ).intValue() + 1;
            attributeNameToSelectedValuesCountMap.put( getSelectedValues()[i].getAttribute().getDescription(),
                new Integer( count ) );
            if ( count >= getSelectedValues()[i].getAttribute().getValueSize() )
            {
                IAttribute attribute = getSelectedValues()[i].getAttribute();
                if ( attribute != null && !attributeList.contains( attribute ) )
                {
                    attributeList.add( attribute );
                }
            }
        }

        return ( IAttribute[] ) attributeList.toArray( new IAttribute[attributeList.size()] );
    }


    /**
     * Gets the Values
     *
     * @return
     *      the Values
     * @throws Exception
     */
    protected IValue[] getValues() throws Exception
    {

        Map attributeNameToSelectedValuesCountMap = new HashMap();
        Set selectedObjectClasses = new HashSet();
        for ( int i = 0; i < getSelectedValues().length; i++ )
        {
            // check if a value of an operational attribute is selected
            if ( !SchemaUtils.isModifyable( getSelectedValues()[i].getAttribute().getAttributeTypeDescription() ) )
            {
                throw new Exception();
            }

            // check if (part of) RDN is selected
            RDNPart[] parts = this.getSelectedValues()[i].getAttribute().getEntry().getRdn().getParts();
            for ( int p = 0; p < parts.length; p++ )
            {
                if ( getSelectedValues()[i].getAttribute().getDescription().equals( parts[p].getType() )
                    && getSelectedValues()[i].getStringValue().equals( parts[p].getValue() ) )
                {
                    throw new Exception();
                }
            }

            // check if a required objectClass is selected
            if ( getSelectedValues()[i].getAttribute().isObjectClassAttribute() )
            {
                selectedObjectClasses.add( getSelectedValues()[i].getStringValue() );
            }

            // check if ALL values of objectClass or a MUST attribute are
            // selected
            if ( !attributeNameToSelectedValuesCountMap.containsKey( getSelectedValues()[i].getAttribute()
                .getDescription() ) )
            {
                attributeNameToSelectedValuesCountMap.put( getSelectedValues()[i].getAttribute().getDescription(),
                    new Integer( 0 ) );
            }
            int count = ( ( Integer ) attributeNameToSelectedValuesCountMap.get( getSelectedValues()[i].getAttribute()
                .getDescription() ) ).intValue() + 1;
            attributeNameToSelectedValuesCountMap.put( getSelectedValues()[i].getAttribute().getDescription(),
                new Integer( count ) );
            if ( ( getSelectedValues()[i].getAttribute().isObjectClassAttribute() || getSelectedValues()[i]
                .getAttribute().isMustAttribute() /*
             * || this.selectedEntry ==
             * null
             */)
                && count >= getSelectedValues()[i].getAttribute().getValueSize() )
            {
                throw new Exception();
            }
        }
        // check if a required objectClass is selected
        if ( getSelectedValues().length > 0 && !selectedObjectClasses.isEmpty() )
        {
            IEntry entry = getSelectedValues()[0].getAttribute().getEntry();
            // get remaining attributes
            String[] ocValues = entry.getSubschema().getObjectClassNames();
            Set remainingObjectClassesSet = new HashSet( Arrays.asList( ocValues ) );
            remainingObjectClassesSet.removeAll( selectedObjectClasses );
            Set remainingAttributeSet = new HashSet();
            for ( Iterator it = remainingObjectClassesSet.iterator(); it.hasNext(); )
            {
                String oc = ( String ) it.next();
                ObjectClassDescription ocd = entry.getConnection().getSchema().getObjectClassDescription( oc );
                if ( ocd != null )
                {
                    remainingAttributeSet
                        .addAll( Arrays.asList( ocd.getMustAttributeTypeDescriptionNamesTransitive() ) );
                    remainingAttributeSet.addAll( Arrays.asList( ocd.getMayAttributeTypeDescriptionNamesTransitive() ) );
                }
            }
            // check against attributes
            IAttribute[] attributes = entry.getAttributes();
            for ( int i = 0; i < attributes.length; i++ )
            {
                IAttribute attribute = attributes[i];
                if ( attribute.isMayAttribute() || attribute.isMustAttribute() )
                {
                    if ( !remainingAttributeSet.contains( attribute.getType() ) )
                    {
                        throw new Exception();
                    }
                }
            }
        }

        List valueList = new ArrayList();

        // add selected values
        Set attributeSet = new HashSet( Arrays.asList( getAttributes() ) );
        for ( int i = 0; i < getSelectedValues().length; i++ )
        {
            if ( !attributeSet.contains( getSelectedValues()[i].getAttribute() ) )
            {
                valueList.add( getSelectedValues()[i] );
            }
        }

        return ( IValue[] ) valueList.toArray( new IValue[valueList.size()] );
    }


    /**
     * Deletes Attributes and Values
     *
     * @param attributes
     *      the Attributes to delete
     * @param values
     *      the Values to delete
     */
    protected void deleteAttributesAndValues( IAttribute[] attributes, IValue[] values )
    {
        new DeleteAttributesValueJob( attributes, values ).execute();
    }
}
