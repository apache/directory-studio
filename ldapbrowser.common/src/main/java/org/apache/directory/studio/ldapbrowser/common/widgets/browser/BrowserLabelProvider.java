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

package org.apache.directory.studio.ldapbrowser.common.widgets.browser;


import java.util.Collection;

import org.apache.directory.shared.ldap.schema.ObjectClassTypeEnum;
import org.apache.directory.shared.ldap.schema.parsers.ObjectClassDescription;
import org.apache.directory.studio.connection.core.Utils;
import org.apache.directory.studio.connection.core.jobs.StudioConnectionRunnableWithProgress;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IContinuation;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IQuickSearch;
import org.apache.directory.studio.ldapbrowser.core.model.IRootDSE;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.IContinuation.State;
import org.apache.directory.studio.ldapbrowser.core.model.impl.BaseDNEntry;
import org.apache.directory.studio.ldapbrowser.core.model.impl.DirectoryMetadataEntry;
import org.apache.directory.studio.ldapbrowser.core.model.impl.SearchContinuation;
import org.apache.directory.studio.ldapbrowser.core.model.schema.ObjectClassIconPair;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;


/**
 * The BrowserLabelProvider implements the label provider for
 * the browser widget.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class BrowserLabelProvider extends LabelProvider implements IFontProvider, IColorProvider
{

    /** The preferences. */
    private BrowserPreferences preferences;


    /**
     * Creates a new instance of BrowserLabelProvider.
     *
     * @param preferences the preferences
     */
    public BrowserLabelProvider( BrowserPreferences preferences )
    {
        this.preferences = preferences;
    }


    /**
     * {@inheritDoc}
     */
    public String getText( Object obj )
    {
        if ( obj instanceof IEntry )
        {
            IEntry entry = ( IEntry ) obj;

            StringBuffer append = new StringBuffer();

            if ( entry.isChildrenInitialized() && ( entry.getChildrenCount() > 0 ) || entry.getChildrenFilter() != null )
            {
                append.append( " (" ).append( entry.getChildrenCount() ); //$NON-NLS-1$
                if ( entry.hasMoreChildren() )
                {
                    append.append( "+" ); //$NON-NLS-1$
                }
                if ( entry.getChildrenFilter() != null )
                {
                    append.append( ", filtered" ); //$NON-NLS-1$
                }
                append.append( ")" ); //$NON-NLS-1$
            }

            if ( entry instanceof IRootDSE )
            {
                return "Root DSE" + append.toString(); //$NON-NLS-1$
            }
            else if ( entry instanceof IContinuation )
            {
                return entry.getUrl().toString() + append.toString();
            }
            else if ( entry instanceof BaseDNEntry )
            {
                return entry.getDn().getUpName() + append.toString();
            }
            else if ( entry.hasParententry() )
            {
                String label = ""; //$NON-NLS-1$
                if ( preferences.getEntryLabel() == BrowserCommonConstants.SHOW_DN )
                {
                    label = entry.getDn().getUpName();
                }
                else if ( preferences.getEntryLabel() == BrowserCommonConstants.SHOW_RDN )
                {
                    label = entry.getRdn().getUpName();
                }
                else if ( preferences.getEntryLabel() == BrowserCommonConstants.SHOW_RDN_VALUE )
                {
                    label = ( String ) entry.getRdn().getUpValue();
                }

                label += append.toString();

                if ( preferences.isEntryAbbreviate() && label.length() > preferences.getEntryAbbreviateMaxLength() )
                {
                    label = Utils.shorten( label, preferences.getEntryAbbreviateMaxLength() );
                }

                return label;
            }
            else
            {
                return entry.getDn().getUpName() + append.toString();
            }
        }
        else if ( obj instanceof SearchContinuation )
        {
            SearchContinuation sc = ( SearchContinuation ) obj;
            return sc.getUrl().toString();
        }
        else if ( obj instanceof BrowserEntryPage )
        {
            BrowserEntryPage container = ( BrowserEntryPage ) obj;
            return "[" + ( container.getFirst() + 1 ) + "..." + ( container.getLast() + 1 ) + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        else if ( obj instanceof BrowserSearchResultPage )
        {
            BrowserSearchResultPage container = ( BrowserSearchResultPage ) obj;
            return "[" + ( container.getFirst() + 1 ) + "..." + ( container.getLast() + 1 ) + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        else if ( obj instanceof ISearch )
        {
            ISearch search = ( ISearch ) obj;
            ISearchResult[] results = search.getSearchResults();
            SearchContinuation[] scs = search.getSearchContinuations();
            StringBuffer append = new StringBuffer( search.getName() );
            if ( results != null && scs != null )
            {
                append.append( " (" ).append( results.length + scs.length ); //$NON-NLS-1$
                if ( search.isCountLimitExceeded() )
                {
                    append.append( "+" ); //$NON-NLS-1$
                }
                append.append( ")" ); //$NON-NLS-1$
            }
            return append.toString();
        }
        else if ( obj instanceof IBookmark )
        {
            IBookmark bookmark = ( IBookmark ) obj;
            return bookmark.getName();
        }
        else if ( obj instanceof ISearchResult )
        {
            ISearchResult sr = ( ISearchResult ) obj;

            if ( sr.getEntry() instanceof IContinuation )
            {
                return sr.getEntry().getUrl().toString();
            }
            else if ( sr.getEntry().hasParententry() || sr.getEntry() instanceof IRootDSE )
            {
                String label = ""; //$NON-NLS-1$
                if ( sr.getEntry() instanceof IRootDSE )
                {
                    label = "Root DSE"; //$NON-NLS-1$
                }
                else if ( preferences.getSearchResultLabel() == BrowserCommonConstants.SHOW_DN )
                {
                    label = sr.getEntry().getDn().getUpName();
                }
                else if ( preferences.getSearchResultLabel() == BrowserCommonConstants.SHOW_RDN )
                {
                    label = sr.getEntry().getRdn().getUpName();
                }
                else if ( preferences.getSearchResultLabel() == BrowserCommonConstants.SHOW_RDN_VALUE )
                {
                    label = ( String ) sr.getEntry().getRdn().getUpValue();
                }

                if ( preferences.isSearchResultAbbreviate()
                    && label.length() > preferences.getSearchResultAbbreviateMaxLength() )
                {
                    label = Utils.shorten( label, preferences.getSearchResultAbbreviateMaxLength() );
                }

                return label;
            }
            else
            {
                return sr.getEntry().getDn().getUpName();
            }

        }
        else if ( obj instanceof StudioConnectionRunnableWithProgress )
        {
            StudioConnectionRunnableWithProgress runnable = ( StudioConnectionRunnableWithProgress ) obj;
            for ( Object lockedObject : runnable.getLockedObjects() )
            {
                if ( lockedObject instanceof ISearch )
                {
                    ISearch search = ( ISearch ) lockedObject;
                    if ( obj == search.getTopSearchRunnable() )
                    {
                        return Messages.getString( "BrowserLabelProvider.TopPage" ); //$NON-NLS-1$
                    }
                    else if ( obj == search.getNextSearchRunnable() )
                    {
                        return Messages.getString( "BrowserLabelProvider.NextPage" ); //$NON-NLS-1$
                    }
                }
                else if ( lockedObject instanceof IEntry )
                {
                    IEntry entry = ( IEntry ) lockedObject;
                    if ( obj == entry.getTopPageChildrenRunnable() )
                    {
                        return Messages.getString( "BrowserLabelProvider.TopPage" ); //$NON-NLS-1$
                    }
                    else if ( obj == entry.getNextPageChildrenRunnable() )
                    {
                        return Messages.getString( "BrowserLabelProvider.NextPage" ); //$NON-NLS-1$
                    }
                }
            }
            return obj.toString();
        }
        else if ( obj instanceof BrowserCategory )
        {
            BrowserCategory category = ( BrowserCategory ) obj;
            return category.getTitle();
        }
        else if ( obj != null )
        {
            return obj.toString();
        }
        else
        {
            return ""; //$NON-NLS-1$
        }
    }


    /**
     * {@inheritDoc}
     */
    public Image getImage( Object obj )
    {
        if ( obj instanceof IEntry )
        {
            IEntry entry = ( IEntry ) obj;
            if ( entry instanceof IRootDSE )
            {
                return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_ENTRY_ROOT );
            }
            else if ( entry instanceof DirectoryMetadataEntry && ( ( DirectoryMetadataEntry ) entry ).isSchemaEntry() )
            {
                return BrowserCommonActivator.getDefault().getImage(
                    BrowserCommonConstants.IMG_BROWSER_SCHEMABROWSEREDITOR );
            }
            else if ( entry.getDn().equals( entry.getBrowserConnection().getSchema().getDn() ) )
            {
                return BrowserCommonActivator.getDefault().getImage(
                    BrowserCommonConstants.IMG_BROWSER_SCHEMABROWSEREDITOR );
            }
            else
            {
                return BrowserLabelProvider.getImageByObjectClass( entry );
            }
        }
        else if ( obj instanceof BrowserEntryPage )
        {
            return PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJ_FOLDER );
        }
        else if ( obj instanceof BrowserSearchResultPage )
        {
            return PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJ_FOLDER );
        }
        else if ( obj instanceof IQuickSearch )
        {
            return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_QUICKSEARCH );
        }
        else if ( obj instanceof ISearch )
        {
            ISearch search = ( ISearch ) obj;
            if ( search instanceof IContinuation && ( ( IContinuation ) search ).getState() != State.RESOLVED )
            {
                return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_SEARCH_UNPERFORMED );
            }
            else if ( search.getSearchResults() != null )
            {
                return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_SEARCH );
            }
            else
            {
                return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_SEARCH_UNPERFORMED );
            }
        }
        else if ( obj instanceof IBookmark )
        {
            return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_BOOKMARK );
        }
        else if ( obj instanceof ISearchResult )
        {
            ISearchResult sr = ( ISearchResult ) obj;
            IEntry entry = sr.getEntry();
            return BrowserLabelProvider.getImageByObjectClass( entry );
        }
        else if ( obj instanceof StudioConnectionRunnableWithProgress )
        {
            StudioConnectionRunnableWithProgress runnable = ( StudioConnectionRunnableWithProgress ) obj;
            for ( Object lockedObject : runnable.getLockedObjects() )
            {
                if ( lockedObject instanceof ISearch )
                {
                    ISearch search = ( ISearch ) lockedObject;
                    if ( obj == search.getTopSearchRunnable() )
                    {
                        return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_TOP );
                    }
                    else if ( obj == search.getNextSearchRunnable() )
                    {
                        return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_NEXT );
                    }
                }
                else if ( lockedObject instanceof IEntry )
                {
                    IEntry entry = ( IEntry ) lockedObject;
                    if ( obj == entry.getTopPageChildrenRunnable() )
                    {
                        return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_TOP );
                    }
                    else if ( obj == entry.getNextPageChildrenRunnable() )
                    {
                        return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_NEXT );
                    }
                }
            }
            return null;
        }
        else if ( obj instanceof BrowserCategory )
        {
            BrowserCategory category = ( BrowserCategory ) obj;
            if ( category.getType() == BrowserCategory.TYPE_DIT )
            {
                return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_DIT );
            }
            else if ( category.getType() == BrowserCategory.TYPE_SEARCHES )
            {
                return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_SEARCHES );
            }
            else if ( category.getType() == BrowserCategory.TYPE_BOOKMARKS )
            {
                return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_BOOKMARKS );
            }
            else
            {
                return null;
            }
        }
        else
        {
            // return
            // Activator.getDefault().getImage("icons/sandglass.gif");
            return null;
        }
    }


    /**
     * Gets the image associated with the entry based 
     * on the value of its 'objectClass' attribute.
     *
     * @param entry
     *      the entry
     * @return
     *      the image associated with then entry
     */
    public static Image getImageByObjectClass( IEntry entry )
    {
        Schema schema = entry.getBrowserConnection().getSchema();
        Collection<ObjectClassDescription> ocds = entry.getObjectClassDescriptions();
        if ( ocds != null )
        {
            Collection<String> numericOids = SchemaUtils.getNumericOids( ocds );
            ObjectClassIconPair[] objectClassIcons = BrowserCorePlugin.getDefault().getCorePreferences()
                .getObjectClassIcons();
            int maxWeight = 0;
            ObjectClassIconPair maxObjectClassIconPair = null;
            for ( ObjectClassIconPair objectClassIconPair : objectClassIcons )
            {
                int weight = 0;
                String[] ocNumericOids = objectClassIconPair.getOcNumericOids();
                for ( String ocNumericOid : ocNumericOids )
                {
                    if ( numericOids.contains( ocNumericOid ) )
                    {
                        ObjectClassDescription ocd = schema.getObjectClassDescription( ocNumericOid );
                        if ( ocd.getKind() == ObjectClassTypeEnum.STRUCTURAL )
                        {
                            weight += 3;
                        }
                        else if ( ocd.getKind() == ObjectClassTypeEnum.AUXILIARY )
                        {
                            weight += 2;
                        }
                    }
                }
                if ( weight > maxWeight )
                {
                    maxObjectClassIconPair = objectClassIconPair;
                }
            }

            if ( maxObjectClassIconPair != null )
            {
                return BrowserCommonActivator.getDefault().getImage( maxObjectClassIconPair.getIconPath() );
            }
        }

        return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_ENTRY );
    }


    /**
     * {@inheritDoc}
     */
    public Font getFont( Object element )
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public Color getForeground( Object element )
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public Color getBackground( Object element )
    {
        return null;
    }

}
