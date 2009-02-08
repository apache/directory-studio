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
import org.apache.directory.shared.ldap.schema.parsers.AttributeTypeDescription;
import org.apache.directory.shared.ldap.schema.parsers.ObjectClassDescription;
import org.apache.directory.studio.ldapbrowser.common.dialogs.DeleteDialog;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.jobs.DeleteAttributesValueJob;
import org.apache.directory.studio.ldapbrowser.core.jobs.DeleteEntriesJob;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IRootDSE;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.StudioControl;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
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
            Collection<IEntry> entries = getEntries();
            ISearch[] searches = getSearches();
            IBookmark[] bookmarks = getBookmarks();
            Collection<IAttribute> attributes = getAttributes();
            Collection<IValue> values = getValues();

            if ( entries.size() > 0 && searches.length == 0 && bookmarks.length == 0 && attributes.size() == 0
                && values.size() == 0 )
            {
                return entries.size() > 1 ? Messages.getString( "DeleteAction.DeleteEntries" ) : Messages.getString( "DeleteAction.DeleteEntry" ); //$NON-NLS-1$ //$NON-NLS-2$
            }
            if ( searches.length > 0 && entries.size() == 0 && bookmarks.length == 0 && attributes.size() == 0
                && values.size() == 0 )
            {
                return searches.length > 1 ? Messages.getString( "DeleteAction.DeleteSearches" ) : Messages.getString( "DeleteAction.DeleteSearch" ); //$NON-NLS-1$ //$NON-NLS-2$
            }
            if ( bookmarks.length > 0 && entries.size() == 0 && searches.length == 0 && attributes.size() == 0
                && values.size() == 0 )
            {
                return bookmarks.length > 1 ? Messages.getString( "DeleteAction.DeleteBookmarks" ) : Messages.getString( "DeleteAction.DeleteBookmark" ); //$NON-NLS-1$ //$NON-NLS-2$
            }
            if ( attributes.size() > 0 && entries.size() == 0 && searches.length == 0 && bookmarks.length == 0
                && values.size() == 0 )
            {
                return attributes.size() > 1 ? Messages.getString( "DeleteAction.DeleteAttributes" ) : Messages.getString( "DeleteAction.DeleteAttribute" ); //$NON-NLS-1$ //$NON-NLS-2$
            }
            if ( values.size() > 0 && entries.size() == 0 && searches.length == 0 && bookmarks.length == 0
                && attributes.size() == 0 )
            {
                return values.size() > 1 ? Messages.getString( "DeleteAction.DeleteValues" ) : Messages.getString( "DeleteAction.DeleteValue" ); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        catch ( Exception e )
        {
        }

        return Messages.getString( "DeleteAction.Delete" ); //$NON-NLS-1$
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
            Collection<IEntry> entries = getEntries();
            ISearch[] searches = getSearches();
            IBookmark[] bookmarks = getBookmarks();
            Collection<IAttribute> attributes = getAttributes();
            Collection<IValue> values = getValues();

            StringBuffer message = new StringBuffer();
            boolean askForTreeDeleteControl = false;

            if ( entries.size() > 0 )
            {
                appendEntriesWarnMessage( message, entries );

                if ( entries.iterator().next().getBrowserConnection().getRootDSE().isControlSupported(
                    StudioControl.TREEDELETE_CONTROL.getOid() ) )
                {
                    askForTreeDeleteControl = true;
                }
            }

            if ( searches.length > 0 )
            {
                appendSearchesWarnMessage( message, searches );
            }

            if ( bookmarks.length > 0 )
            {
                appendBookmarsWarnMessage( message, bookmarks );
            }

            if ( attributes.size() > 0 )
            {
                appendAttributesWarnMessage( message, attributes );
            }

            if ( values.size() > 0 )
            {
                boolean emptyValuesOnly = true;
                for ( IValue value : values )
                {
                    if ( !value.isEmpty() )
                    {
                        emptyValuesOnly = false;
                    }
                }
                if ( !emptyValuesOnly )
                {
                    appendValuesWarnMessage( message, values );
                }
            }

            DeleteDialog dialog = new DeleteDialog( getShell(), getText(), message.toString(), askForTreeDeleteControl );
            if ( message.length() == 0 || dialog.open() == DeleteDialog.OK )
            {
                if ( entries.size() > 0 )
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
                if ( attributes.size() + values.size() > 0 )
                {
                    List<IAttribute> attributeList = new ArrayList<IAttribute>( attributes );
                    List<IValue> valueList = new ArrayList<IValue>( values );

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
            Collection<IEntry> entries = getEntries();
            ISearch[] searches = getSearches();
            IBookmark[] bookmarks = getBookmarks();
            Collection<IAttribute> attributes = getAttributes();
            Collection<IValue> values = getValues();

            return entries.size() + searches.length + bookmarks.length + attributes.size() + values.size() > 0;

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
    protected Collection<IEntry> getEntries()
    {
        LinkedHashSet<IEntry> entriesSet = new LinkedHashSet<IEntry>();
        for ( IEntry entry : getSelectedEntries() )
        {
            entriesSet.add( entry );
        }
        for ( ISearchResult sr : getSelectedSearchResults() )
        {
            entriesSet.add( sr.getEntry() );
        }

        Iterator<IEntry> iterator = entriesSet.iterator();
        while ( iterator.hasNext() )
        {
            IEntry entry = iterator.next();
            if ( entriesSet.contains( entry.getParententry() ) )
            {
                iterator.remove();
            }
        }

        return entriesSet;
    }


    /**
     * Appends the entries warn message.
     * 
     * @param message the message
     * @param entries the entries
     */
    protected void appendEntriesWarnMessage( StringBuffer message, Collection<IEntry> entries )
    {
        for ( IEntry entry : entries )
        {
            if ( entry instanceof IRootDSE )
            {
                message.append( Messages.getString( "DeleteAction.DeleteRootDSE" ) ); //$NON-NLS-1$
                message.append( BrowserCoreConstants.LINE_SEPARATOR );
                message.append( BrowserCoreConstants.LINE_SEPARATOR );
            }
        }

        if ( entries.size() <= 5 )
        {
            message.append( entries.size() == 1 ? Messages.getString( "DeleteAction.DeleteEntryQuestion" ) //$NON-NLS-1$
                : Messages.getString( "DeleteAction.DeleteEntriesQuestion" ) ); //$NON-NLS-1$
            for ( IEntry entry : entries )
            {
                message.append( BrowserCoreConstants.LINE_SEPARATOR );
                message.append( "  - " ); //$NON-NLS-1$
                message.append( entry.getDn().getUpName() );
            }
        }
        else
        {
            message.append( Messages.getString( "DeleteAction.DeleteSelectedEntriesQuestion" ) ); //$NON-NLS-1$
        }
        message.append( BrowserCoreConstants.LINE_SEPARATOR );
        message.append( BrowserCoreConstants.LINE_SEPARATOR );
    }


    /**
     * Deletes Entries.
     * 
     * @param entries the Entries to delete
     * @param useTreeDeleteControl true to use the tree delete control
     */
    protected void deleteEntries( Collection<IEntry> entries, boolean useTreeDeleteControl )
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
    protected ISearch[] getSearches()
    {
        return getSelectedSearches();
    }


    protected void appendSearchesWarnMessage( StringBuffer message, ISearch[] searches )
    {
        if ( searches.length <= 5 )
        {
            message.append( searches.length == 1 ? Messages.getString( "DeleteAction.DeleteSearchQuestion" ) //$NON-NLS-1$
                : Messages.getString( "DeleteAction.DeleteSearchesQuestion" ) ); //$NON-NLS-1$
            for ( int i = 0; i < searches.length; i++ )
            {
                message.append( BrowserCoreConstants.LINE_SEPARATOR );
                message.append( "  - " ); //$NON-NLS-1$
                message.append( searches[i].getName() );
            }
        }
        else
        {
            message.append( Messages.getString( "DeleteAction.DeleteSelectedSearchesQuestion" ) ); //$NON-NLS-1$
        }
        message.append( BrowserCoreConstants.LINE_SEPARATOR );
        message.append( BrowserCoreConstants.LINE_SEPARATOR );
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
    protected IBookmark[] getBookmarks()
    {
        return getSelectedBookmarks();
    }


    protected void appendBookmarsWarnMessage( StringBuffer message, IBookmark[] bookmarks )
    {
        if ( bookmarks.length <= 5 )
        {
            message.append( bookmarks.length == 1 ? Messages.getString( "DeleteAction.DeleteBookmarkQuestion" ) //$NON-NLS-1$
                : Messages.getString( "DeleteAction.DeleteBookmarksQuestion" ) ); //$NON-NLS-1$
            for ( int i = 0; i < bookmarks.length; i++ )
            {
                message.append( BrowserCoreConstants.LINE_SEPARATOR );
                message.append( "  - " ); //$NON-NLS-1$
                message.append( bookmarks[i].getName() );
            }
        }
        else
        {
            message.append( Messages.getString( "DeleteAction.DeleteSelectedBookmarksQuestion" ) ); //$NON-NLS-1$
        }
        message.append( BrowserCoreConstants.LINE_SEPARATOR );
        message.append( BrowserCoreConstants.LINE_SEPARATOR );
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
    protected Collection<IAttribute> getAttributes()
    {
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

        // check if ALL values of an attribute are selected -> delete whole attribute
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

        return attributeList;
    }


    protected void appendAttributesWarnMessage( StringBuffer message, Collection<IAttribute> attributes )
    {
        // check if a non-modifyable, must or objectClass attribute is selected
        for ( IAttribute att : attributes )
        {
            Iterator<AttributeTypeAndValue> atavIterator = att.getEntry().getRdn().iterator();
            while ( atavIterator.hasNext() )
            {
                AttributeTypeAndValue atav = atavIterator.next();
                for ( IValue value : att.getValues() )
                {
                    if ( att.getDescription().equals( atav.getUpType() )
                        && value.getStringValue().equals( atav.getUpValue() ) )
                    {
                        message.append( NLS.bind(
                            Messages.getString( "DeleteAction.DeletePartOfRDN" ), value.toString() ) ); //$NON-NLS-1$
                        message.append( BrowserCoreConstants.LINE_SEPARATOR );
                        message.append( BrowserCoreConstants.LINE_SEPARATOR );
                    }
                }
            }

            if ( att.isObjectClassAttribute() )
            {
                message.append( Messages.getString( "DeleteAction.DeleteObjectClass" ) ); //$NON-NLS-1$
                message.append( BrowserCoreConstants.LINE_SEPARATOR );
                message.append( BrowserCoreConstants.LINE_SEPARATOR );
            }
            else if ( att.isMustAttribute() )
            {
                message.append( NLS.bind( Messages.getString( "DeleteAction.DeleteMust" ), att.getDescription() ) ); //$NON-NLS-1$
                message.append( BrowserCoreConstants.LINE_SEPARATOR );
                message.append( BrowserCoreConstants.LINE_SEPARATOR );
            }
            else if ( !SchemaUtils.isModifyable( att.getAttributeTypeDescription() ) )
            {
                message.append( NLS.bind(
                    Messages.getString( "DeleteAction.DeleteNonModifyable" ), att.getDescription() ) ); //$NON-NLS-1$
                message.append( BrowserCoreConstants.LINE_SEPARATOR );
                message.append( BrowserCoreConstants.LINE_SEPARATOR );
            }
        }

        if ( attributes.size() <= 5 )
        {
            message.append( attributes.size() == 1 ? Messages.getString( "DeleteAction.DeleteAttributeQuestion" ) //$NON-NLS-1$
                : Messages.getString( "DeleteAction.DeleteAttributesQuestion" ) ); //$NON-NLS-1$
            for ( IAttribute attribute : attributes )
            {
                message.append( BrowserCoreConstants.LINE_SEPARATOR );
                message.append( "  - " ); //$NON-NLS-1$
                message.append( attribute.getDescription() );
            }
        }
        else
        {
            message.append( Messages.getString( "DeleteAction.DeleteSelectedAttributesQuestion" ) ); //$NON-NLS-1$
        }
        message.append( BrowserCoreConstants.LINE_SEPARATOR );
        message.append( BrowserCoreConstants.LINE_SEPARATOR );
    }


    /**
     * Gets the Values
     *
     * @return
     *      the Values
     * @throws Exception
     */
    protected Collection<IValue> getValues() throws Exception
    {
        List<IValue> valueList = new ArrayList<IValue>();

        // add selected values, but not if there attributes are also selected
        Set<IAttribute> attributeSet = new HashSet<IAttribute>( getAttributes() );
        for ( IValue value : getSelectedValues() )
        {
            if ( !attributeSet.contains( value.getAttribute() ) )
            {
                valueList.add( value );
            }
        }

        return valueList;
    }


    protected void appendValuesWarnMessage( StringBuffer message, Collection<IValue> values )
    {
        Map<String, Integer> attributeNameToSelectedValuesCountMap = new HashMap<String, Integer>();
        Set<String> selectedObjectClasses = new HashSet<String>();
        for ( IValue value : values )
        {
            // check if (part of) RDN is selected
            Iterator<AttributeTypeAndValue> atavIterator = value.getAttribute().getEntry().getRdn().iterator();
            while ( atavIterator.hasNext() )
            {
                AttributeTypeAndValue atav = atavIterator.next();
                if ( value.getAttribute().getDescription().equals( atav.getUpType() )
                    && value.getStringValue().equals( atav.getUpValue() ) )
                {
                    message.append( NLS.bind( Messages.getString( "DeleteAction.DeletePartOfRDN" ), value.toString() ) ); //$NON-NLS-1$
                    message.append( BrowserCoreConstants.LINE_SEPARATOR );
                    message.append( BrowserCoreConstants.LINE_SEPARATOR );
                }
            }

            // check if a required objectClass is selected
            if ( value.getAttribute().isObjectClassAttribute() )
            {
                selectedObjectClasses.add( value.getStringValue() );
            }

            // check if ALL values of objectClass or a MUST attribute are selected
            if ( !attributeNameToSelectedValuesCountMap.containsKey( value.getAttribute().getDescription() ) )
            {
                attributeNameToSelectedValuesCountMap.put( value.getAttribute().getDescription(), new Integer( 0 ) );
            }
            int count = ( ( Integer ) attributeNameToSelectedValuesCountMap.get( value.getAttribute().getDescription() ) )
                .intValue() + 1;
            attributeNameToSelectedValuesCountMap.put( value.getAttribute().getDescription(), new Integer( count ) );
            if ( value.getAttribute().isObjectClassAttribute() && count >= value.getAttribute().getValueSize() )
            {
                message.append( Messages.getString( "DeleteAction.DeleteObjectClass" ) ); //$NON-NLS-1$
                message.append( BrowserCoreConstants.LINE_SEPARATOR );
                message.append( BrowserCoreConstants.LINE_SEPARATOR );
                continue;
            }
            else if ( value.getAttribute().isMustAttribute() && count >= value.getAttribute().getValueSize() )
            {
                message.append( NLS.bind(
                    Messages.getString( "DeleteAction.DeleteMust" ), value.getAttribute().getDescription() ) ); //$NON-NLS-1$
                message.append( BrowserCoreConstants.LINE_SEPARATOR );
                message.append( BrowserCoreConstants.LINE_SEPARATOR );
            }

            // check if a value of an operational attribute is selected
            if ( !SchemaUtils.isModifyable( value.getAttribute().getAttributeTypeDescription() ) )
            {
                message.append( NLS.bind(
                    Messages.getString( "DeleteAction.DeleteNonModifyable" ), value.getAttribute().getDescription() ) ); //$NON-NLS-1$
                message.append( BrowserCoreConstants.LINE_SEPARATOR );
                message.append( BrowserCoreConstants.LINE_SEPARATOR );
                continue;
            }
        }

        // check if a required objectClass is selected
        if ( values.size() > 0 && !selectedObjectClasses.isEmpty() )
        {
            IEntry entry = values.iterator().next().getAttribute().getEntry();
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
                        message.append( NLS.bind(
                            Messages.getString( "DeleteAction.DeleteNeededObjectClass" ), attribute.getDescription() ) ); //$NON-NLS-1$
                        message.append( BrowserCoreConstants.LINE_SEPARATOR );
                        message.append( BrowserCoreConstants.LINE_SEPARATOR );
                    }
                }
            }
        }

        if ( values.size() <= 5 )
        {
            message.append( values.size() == 1 ? Messages.getString( "DeleteAction.DeleteValueQuestion" ) //$NON-NLS-1$
                : Messages.getString( "DeleteAction.DeleteValuesQuestion" ) ); //$NON-NLS-1$
            for ( IValue value : values )
            {
                message.append( BrowserCoreConstants.LINE_SEPARATOR );
                message.append( "  - " ); //$NON-NLS-1$
                message.append( value.toString() );
            }
        }
        else
        {
            message.append( Messages.getString( "DeleteAction.DeleteSelectedValuesQuestion" ) ); //$NON-NLS-1$
        }
        message.append( BrowserCoreConstants.LINE_SEPARATOR );
        message.append( BrowserCoreConstants.LINE_SEPARATOR );
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
