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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.directory.shared.ldap.name.AttributeTypeAndValue;
import org.apache.directory.shared.ldap.schema.syntax.AttributeTypeDescription;
import org.apache.directory.shared.ldap.schema.syntax.ObjectClassDescription;
import org.apache.directory.studio.ldapbrowser.common.dialogs.DeleteDialog;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.jobs.DeleteAttributesValueJob;
import org.apache.directory.studio.ldapbrowser.core.jobs.DeleteEntriesJob;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.StudioControl;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;
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
            IEntry[] entries = getEntries();
            ISearch[] searches = getSearches();
            IBookmark[] bookmarks = getBookmarks();
            IAttribute[] attributes = getAttributes();
            IValue[] values = getValues();

            if ( entries.length > 0 && searches.length == 0 && bookmarks.length == 0 && attributes.length == 0
                && values.length == 0 )
            {
                return entries.length > 1 ? "Delete Entries" : "Delete Entry";
            }
            if ( searches.length > 0 && entries.length == 0 && bookmarks.length == 0 && attributes.length == 0
                && values.length == 0 )
            {
                return searches.length > 1 ? "Delete Searches" : "Delete Search";
            }
            if ( bookmarks.length > 0 && entries.length == 0 && searches.length == 0 && attributes.length == 0
                && values.length == 0 )
            {
                return bookmarks.length > 1 ? "Delete Bookmarks" : "Delete Bookmark";
            }
            if ( attributes.length > 0 && entries.length == 0 && searches.length == 0 && bookmarks.length == 0
                && values.length == 0 )
            {
                return attributes.length > 1 ? "Delete Attributes" : "Delete Attribute";
            }
            if ( values.length > 0 && entries.length == 0 && searches.length == 0 && bookmarks.length == 0
                && attributes.length == 0 )
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
            IEntry[] entries = getEntries();
            ISearch[] searches = getSearches();
            IBookmark[] bookmarks = getBookmarks();
            IAttribute[] attributes = getAttributes();
            IValue[] values = getValues();

            StringBuffer message = new StringBuffer();
            boolean askForTreeDeleteControl = false;

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
                        message.append( entries[i].getDn().getUpName() );
                    }
                }
                else
                {
                    message.append( "Are your sure to delete the selected entries, including all children?" );
                }
                message.append( BrowserCoreConstants.LINE_SEPARATOR );
                message.append( BrowserCoreConstants.LINE_SEPARATOR );

                if ( entries[0].getBrowserConnection().getRootDSE().isControlSupported(
                    StudioControl.TREEDELETE_CONTROL.getOid() ) )
                {
                    askForTreeDeleteControl = true;
                }
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

            DeleteDialog dialog = new DeleteDialog( getShell(), getText(), message.toString(), askForTreeDeleteControl );
            if ( message.length() == 0 || dialog.open() == DeleteDialog.OK )
            {
                if ( entries.length > 0 )
                {
                    deleteEntries( entries, dialog.isUseTreeDeleteControl() );
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
                    List<IAttribute> attributeList = new ArrayList<IAttribute>( Arrays.asList( attributes ) );
                    List<IValue> valueList = new ArrayList<IValue>( Arrays.asList( values ) );

                    // filter empty attributes and values
                    for ( Iterator<IAttribute> it = attributeList.iterator(); it.hasNext(); )
                    {
                        IAttribute att = it.next();
                        IValue[] vals = att.getValues();
                        for ( IValue value : vals )
                        {
                            if ( value.isEmpty() )
                            {
                                att.deleteEmptyValue();
                            }
                        }
                        if ( att.getValueSize() == 0 )
                        {
                            att.getEntry().deleteAttribute( att );
                            it.remove();
                        }
                    }
                    for ( Iterator<IValue> it = valueList.iterator(); it.hasNext(); )
                    {
                        IValue value = it.next();
                        if ( value.isEmpty() )
                        {
                            value.getAttribute().deleteEmptyValue();
                            it.remove();
                        }
                    }

                    if ( !attributeList.isEmpty() || !valueList.isEmpty() )
                    {
                        deleteAttributesAndValues( attributeList.toArray( new IAttribute[attributeList.size()] ),
                            valueList.toArray( new IValue[valueList.size()] ) );
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
            IEntry[] entries = getEntries();
            ISearch[] searches = getSearches();
            IBookmark[] bookmarks = getBookmarks();
            IAttribute[] attributes = getAttributes();
            IValue[] values = getValues();

            return entries.length + searches.length + bookmarks.length + attributes.length + values.length > 0;

        }
        catch ( Exception e )
        {
            //e.printStackTrace();
            return false;
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
        LinkedHashSet<IEntry> entriesSet = new LinkedHashSet<IEntry>();
        for ( IEntry entry : getSelectedEntries() )
        {
            if ( !entry.hasParententry() )
            {
                throw new Exception();
            }
            entriesSet.add( entry );
        }
        for ( ISearchResult sr : getSelectedSearchResults() )
        {
            if ( !sr.getEntry().hasParententry() )
            {
                throw new Exception();
            }
            entriesSet.add( sr.getEntry() );
        }

        IEntry[] allEntries = entriesSet.toArray( new IEntry[entriesSet.size()] );
        for ( IEntry entry : allEntries )
        {
            if ( entriesSet.contains( entry.getParententry() ) )
            {
                entriesSet.remove( entry );
            }
        }

        return entriesSet.toArray( new IEntry[entriesSet.size()] );
    }


    /**
     * Deletes Entries.
     * 
     * @param entries the Entries to delete
     * @param useTreeDeleteControl true to use the tree delete control
     */
    protected void deleteEntries( IEntry[] entries, boolean useTreeDeleteControl )
    {
        new DeleteEntriesJob( entries, useTreeDeleteControl ).execute();
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
        for ( ISearch search : searches )
        {
            search.getBrowserConnection().getSearchManager().removeSearch( search );
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
        for ( IBookmark bookmark : bookmarks )
        {
            bookmark.getBrowserConnection().getBookmarkManager().removeBookmark( bookmark );
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
        // check if a non-modifyable, must or objectClass attribute is selected
        for ( IAttribute att : getSelectedAttributes() )
        {
            if ( !SchemaUtils.isModifyable( att.getAttributeTypeDescription() ) || att.isMustAttribute()
                || att.isObjectClassAttribute() )
            {
                throw new Exception();
            }
        }

        // check if a non-modifyable, must or objectClass attribute is selected
        for ( AttributeHierarchy ah : getSelectedAttributeHierarchies() )
        {
            for ( IAttribute attribute : ah )
            {
                if ( !SchemaUtils.isModifyable( attribute.getAttributeTypeDescription() )
                    || attribute.isMustAttribute() || attribute.isObjectClassAttribute() )
                {
                    throw new Exception();
                }
            }
        }

        List<IAttribute> attributeList = new ArrayList<IAttribute>();

        // add selected attributes
        for ( IAttribute attribute : getSelectedAttributes() )
        {
            if ( attribute != null && attribute.getValueSize() > 0 )
            {
                attributeList.add( attribute );
            }
        }

        // add selected hierarchies
        for ( AttributeHierarchy ah : getSelectedAttributeHierarchies() )
        {
            for ( IAttribute attribute : ah )
            {
                if ( attribute != null && attribute.getValueSize() > 0 )
                {
                    attributeList.add( attribute );
                }
            }
        }

        // check if ALL values of an attribute are selected -> delete whole
        // attribute
        Map<String, Integer> attributeNameToSelectedValuesCountMap = new HashMap<String, Integer>();
        for ( IValue value : getSelectedValues() )
        {
            if ( !attributeNameToSelectedValuesCountMap.containsKey( value.getAttribute().getDescription() ) )
            {
                attributeNameToSelectedValuesCountMap.put( value.getAttribute().getDescription(), new Integer( 0 ) );
            }
            int count = ( ( Integer ) attributeNameToSelectedValuesCountMap.get( value.getAttribute().getDescription() ) )
                .intValue() + 1;
            attributeNameToSelectedValuesCountMap.put( value.getAttribute().getDescription(), new Integer( count ) );
            if ( count >= value.getAttribute().getValueSize() )
            {
                IAttribute attribute = value.getAttribute();
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
        Map<String, Integer> attributeNameToSelectedValuesCountMap = new HashMap<String, Integer>();
        Set<String> selectedObjectClasses = new HashSet<String>();
        for ( IValue value : getSelectedValues() )
        {
            // check if a value of an operational attribute is selected
            if ( !SchemaUtils.isModifyable( value.getAttribute().getAttributeTypeDescription() ) )
            {
                throw new Exception();
            }

            // check if (part of) RDN is selected
            Iterator<AttributeTypeAndValue> atavIterator = value.getAttribute().getEntry().getRdn().iterator();
            while ( atavIterator.hasNext() )
            {
                AttributeTypeAndValue atav = atavIterator.next();
                if ( value.getAttribute().getDescription().equals( atav.getUpType() )
                    && value.getStringValue().equals( atav.getUpValue() ) )
                {
                    throw new Exception();
                }
            }

            // check if a required objectClass is selected
            if ( value.getAttribute().isObjectClassAttribute() )
            {
                selectedObjectClasses.add( value.getStringValue() );
            }

            // check if ALL values of objectClass or a MUST attribute are
            // selected
            if ( !attributeNameToSelectedValuesCountMap.containsKey( value.getAttribute().getDescription() ) )
            {
                attributeNameToSelectedValuesCountMap.put( value.getAttribute().getDescription(), new Integer( 0 ) );
            }
            int count = ( ( Integer ) attributeNameToSelectedValuesCountMap.get( value.getAttribute().getDescription() ) )
                .intValue() + 1;
            attributeNameToSelectedValuesCountMap.put( value.getAttribute().getDescription(), new Integer( count ) );
            if ( ( value.getAttribute().isObjectClassAttribute() || value.getAttribute().isMustAttribute() )
                && count >= value.getAttribute().getValueSize() )
            {
                throw new Exception();
            }
        }

        // check if a required objectClass is selected
        if ( getSelectedValues().length > 0 && !selectedObjectClasses.isEmpty() )
        {
            IEntry entry = getSelectedValues()[0].getAttribute().getEntry();
            Schema schema = entry.getBrowserConnection().getSchema();
            // get remaining attributes
            String[] ocValues = entry.getSubschema().getObjectClassNames();
            Set<String> remainingObjectClassesSet = new HashSet<String>( Arrays.asList( ocValues ) );
            remainingObjectClassesSet.removeAll( selectedObjectClasses );
            Set<AttributeTypeDescription> remainingAttributeSet = new HashSet<AttributeTypeDescription>();
            for ( String oc : remainingObjectClassesSet )
            {
                ObjectClassDescription ocd = schema.getObjectClassDescription( oc );
                if ( ocd != null )
                {
                    Collection<String> mustAttrs = SchemaUtils.getMustAttributeTypeDescriptionNamesTransitive( ocd,
                        schema );
                    for ( String mustAttr : mustAttrs )
                    {
                        AttributeTypeDescription atd = entry.getBrowserConnection().getSchema()
                            .getAttributeTypeDescription( mustAttr );
                        remainingAttributeSet.add( atd );
                    }
                    Collection<String> mayAttrs = SchemaUtils.getMayAttributeTypeDescriptionNamesTransitive( ocd,
                        schema );
                    for ( String mayAttr : mayAttrs )
                    {
                        AttributeTypeDescription atd = entry.getBrowserConnection().getSchema()
                            .getAttributeTypeDescription( mayAttr );
                        remainingAttributeSet.add( atd );
                    }
                }
            }
            // check against attributes
            IAttribute[] attributes = entry.getAttributes();
            for ( IAttribute attribute : attributes )
            {
                if ( attribute.isMayAttribute() || attribute.isMustAttribute() )
                {
                    if ( !remainingAttributeSet.contains( attribute.getAttributeTypeDescription() ) )
                    {
                        throw new Exception();
                    }
                }
            }
        }

        List<IValue> valueList = new ArrayList<IValue>();

        // add selected values
        Set<IAttribute> attributeSet = new HashSet<IAttribute>( Arrays.asList( getAttributes() ) );
        for ( IValue value : getSelectedValues() )
        {
            if ( !attributeSet.contains( value.getAttribute() ) )
            {
                valueList.add( value );
            }
        }

        return valueList.toArray( new IValue[valueList.size()] );
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
