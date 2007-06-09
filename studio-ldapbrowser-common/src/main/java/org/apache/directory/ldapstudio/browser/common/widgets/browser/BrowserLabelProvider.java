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

package org.apache.directory.ldapstudio.browser.common.widgets.browser;


import org.apache.directory.ldapstudio.browser.common.BrowserCommonActivator;
import org.apache.directory.ldapstudio.browser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.core.internal.model.AliasBaseEntry;
import org.apache.directory.studio.ldapbrowser.core.internal.model.BaseDNEntry;
import org.apache.directory.studio.ldapbrowser.core.internal.model.DirectoryMetadataEntry;
import org.apache.directory.studio.ldapbrowser.core.internal.model.ReferralBaseEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IRootDSE;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.RDN;
import org.apache.directory.studio.ldapbrowser.core.model.RDNPart;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;


/**
 * The BrowserLabelProvider implements the label provider for
 * the browser widget.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
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
            if ( entry instanceof IRootDSE )
            {
                append.append( "Root DSE" );
            }
            if ( entry.isChildrenInitialized() && ( entry.getChildrenCount() > 0 ) || entry.getChildrenFilter() != null )
            {
                append.append( " (" ).append( entry.getChildrenCount() );
                if ( entry.hasMoreChildren() )
                {
                    append.append( "+" );
                }
                if ( entry.getChildrenFilter() != null )
                {
                    append.append( ", filtered" );
                }
                append.append( ")" );
            }

            if ( entry instanceof ReferralBaseEntry )
            {
                return entry.getUrl().toString() + " " + append.toString();
            }
            else if ( entry instanceof AliasBaseEntry )
            {
                return entry.getDn().toString() + " " + append.toString();
            }
            else if ( entry instanceof BaseDNEntry )
            {
                return entry.getDn().toString() + " " + append.toString();
            }
            else if ( entry.hasParententry() )
            {

                String label = "";
                if ( preferences.getEntryLabel() == BrowserCommonConstants.SHOW_DN )
                {
                    label = entry.getDn().toString();
                }
                else if ( preferences.getEntryLabel() == BrowserCommonConstants.SHOW_RDN )
                {
                    label = entry.getRdn().toString();

                }
                else if ( preferences.getEntryLabel() == BrowserCommonConstants.SHOW_RDN_VALUE )
                {
                    label = entry.getRdn().getValue();
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
                return entry.getDn() + append.toString();
            }
        }
        else if ( obj instanceof BrowserEntryPage )
        {
            BrowserEntryPage container = ( BrowserEntryPage ) obj;
            return "[" + ( container.getFirst() + 1 ) + "..." + ( container.getLast() + 1 ) + "]";
        }
        else if ( obj instanceof BrowserSearchResultPage )
        {
            BrowserSearchResultPage container = ( BrowserSearchResultPage ) obj;
            return "[" + ( container.getFirst() + 1 ) + "..." + ( container.getLast() + 1 ) + "]";
        }
        else if ( obj instanceof ISearch )
        {
            ISearch search = ( ISearch ) obj;
            ISearchResult[] results = search.getSearchResults();
            StringBuffer append = new StringBuffer( search.getName() );
            if ( results != null )
            {
                append.append( " (" ).append( results.length );
                if ( search.isCountLimitExceeded() )
                {
                    append.append( "+" );
                }
                append.append( ")" );
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

            if ( !sr.getSearch().getConnection().equals( sr.getEntry().getConnection() ) )
            {
                return sr.getEntry().getUrl().toString();
            }
            else if ( sr.getEntry().hasParententry() || sr.getEntry() instanceof IRootDSE )
            {
                String label = "";
                if ( sr.getEntry() instanceof IRootDSE )
                {
                    label = "Root DSE";
                }
                else if ( preferences.getSearchResultLabel() == BrowserCommonConstants.SHOW_DN )
                {
                    label = sr.getEntry().getDn().toString();
                }
                else if ( preferences.getSearchResultLabel() == BrowserCommonConstants.SHOW_RDN )
                {
                    label = sr.getEntry().getRdn().toString();
                }
                else if ( preferences.getSearchResultLabel() == BrowserCommonConstants.SHOW_RDN_VALUE )
                {
                    label = sr.getEntry().getRdn().getValue();
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
                return sr.getEntry().getDn().toString();
            }

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
            return "";
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
            return getImageByRdn( entry );
        }
        else if ( obj instanceof BrowserEntryPage )
        {
            return PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJ_FOLDER );
        }
        else if ( obj instanceof BrowserSearchResultPage )
        {
            return PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJ_FOLDER );
        }
        else if ( obj instanceof ISearch )
        {
            ISearch search = ( ISearch ) obj;
            if ( search.getConnection().isOpened() && search.getSearchResults() != null )
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
            return getImageByRdn( entry );
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
     * Gets the image depending on the RDN attribute
     *
     * @param entry the entry
     * @return the image
     */
    private Image getImageByRdn( IEntry entry )
    {
        if ( entry instanceof IRootDSE )
        {
            return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_ENTRY_ROOT );
        }
        else if ( entry instanceof DirectoryMetadataEntry && ( ( DirectoryMetadataEntry ) entry ).isSchemaEntry() )
        {
            return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_BROWSER_SCHEMABROWSEREDITOR );
        }
        else if ( entry.getDn().equals( entry.getConnection().getSchema().getDn() ) )
        {
            return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_BROWSER_SCHEMABROWSEREDITOR );
        }
        else if ( preferences.isDerefAliasesAndReferralsWhileBrowsing() && entry.isAlias() )
        {
            return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_ENTRY_ALIAS );
        }
        else if ( preferences.isDerefAliasesAndReferralsWhileBrowsing() && entry.isReferral() )
        {
            return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_ENTRY_REF );
        }
        else if ( entry.isSubentry() )
        {
            return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_BROWSER_SCHEMABROWSEREDITOR );
        }
        else
        {
            RDN rdn = entry.getRdn();
            RDNPart[] rdnParts = rdn.getParts();
            for ( int i = 0; i < rdnParts.length; i++ )
            {
                RDNPart part = rdnParts[i];
                if ( "cn".equals( part.getType() ) || "sn".equals( part.getType() ) || "uid".equals( part.getType() )
                    || "userid".equals( part.getType() ) )
                {
                    return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_ENTRY_PERSON );
                }
                else if ( "ou".equals( part.getType() ) || "o".equals( part.getType() ) )
                {
                    return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_ENTRY_ORG );
                }
                else if ( "dc".equals( part.getType() ) || "c".equals( part.getType() ) || "l".equals( part.getType() ) )
                {
                    return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_ENTRY_DC );
                }
            }
        }

        return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_ENTRY );
    }


    // private Image getImageByObjectclass(IEntry entry) {
    // IAttribute oc = entry.getAttribute(IAttribute.OBJECTCLASS_ATTRIBUTE);
    // if(oc != null && oc.getStringValues() != null) {
    // String[] ocValues = oc.getStringValues();
    // Set ocSet = new HashSet();
    // for(int i=0; i<ocValues.length; i++) {
    // ocSet.add(ocValues[i].toUpperCase());
    // }
    //			
    // if(entry instanceof IRootDSE) {
    // return
    // Activator.getDefault().getImage(BrowserWidgetsConstants.IMG_ENTRY_ROOT);
    // }
    // else
    // if(entry.getDn().equals(entry.getConnection().getSchema().getDn()))
    // {
    // return
    // Activator.getDefault().getImage(BrowserWidgetsConstants.IMG_BROWSER_SCHEMABROWSEREDITOR);
    // }
    // else if(ocSet.contains(ObjectClassDescription.OC_ALIAS.toUpperCase())
    // || ocSet.contains(ObjectClassDescription.OC_REFERRAL.toUpperCase()))
    // {
    // return
    // Activator.getDefault().getImage(BrowserWidgetsConstants.IMG_ENTRY_REF);
    // }
    // else
    // if(ocSet.contains(ObjectClassDescription.OC_PERSON.toUpperCase())
    // ||
    // ocSet.contains(ObjectClassDescription.OC_ORGANIZATIONALPERSON.toUpperCase())
    // ||
    // ocSet.contains(ObjectClassDescription.OC_INETORGPERSON.toUpperCase())
    // ||
    // ocSet.contains(ObjectClassDescription.OC_RESIDENTIALPERSON.toUpperCase())
    // ||
    // ocSet.contains(ObjectClassDescription.OC_PILOTPERSON.toUpperCase())
    // ||
    // ocSet.contains(ObjectClassDescription.OC_NEWPILOTPERSON.toUpperCase())
    // ||
    // ocSet.contains(ObjectClassDescription.OC_ORGANIZATIONALROLE.toUpperCase())
    // || ocSet.contains(ObjectClassDescription.OC_ACCOUNT.toUpperCase())) {
    // return
    // Activator.getDefault().getImage(BrowserWidgetsConstants.IMG_ENTRY_PERSON);
    // }
    // else
    // if(ocSet.contains(ObjectClassDescription.OC_ORGANIZATION.toUpperCase())
    // ||
    // ocSet.contains(ObjectClassDescription.OC_ORGANIZATIONALUNIT.toUpperCase())
    // ||
    // ocSet.contains(ObjectClassDescription.OC_PILOTORGANIZATION.toUpperCase())
    // || ocSet.contains(ObjectClassDescription.OC_DMD.toUpperCase())
    // ||
    // ocSet.contains(ObjectClassDescription.OC_APPLICATIONPROCESS.toUpperCase())
    // ||
    // ocSet.contains(ObjectClassDescription.OC_APPLICATIONENTITY.toUpperCase()))
    // {
    // return
    // Activator.getDefault().getImage(BrowserWidgetsConstants.IMG_ENTRY_ORG);
    // }
    // else
    // if(ocSet.contains(ObjectClassDescription.OC_COUNTRY.toUpperCase())
    // || ocSet.contains(ObjectClassDescription.OC_LOCALITY.toUpperCase())
    // || ocSet.contains(ObjectClassDescription.OC_DCOBJECT.toUpperCase())
    // || ocSet.contains(ObjectClassDescription.OC_DOMAIN.toUpperCase())) {
    // return
    // Activator.getDefault().getImage(BrowserWidgetsConstants.IMG_ENTRY_DC);
    // }
    // else
    // if(ocSet.contains(ObjectClassDescription.OC_GROUPOFNAMES.toUpperCase())
    // ||
    // ocSet.contains(ObjectClassDescription.OC_GROUPOFUNIQUENAMES.toUpperCase())
    // ||
    // ocSet.contains(ObjectClassDescription.OC_POSIXGROUP.toUpperCase())) {
    // return
    // Activator.getDefault().getImage(BrowserWidgetsConstants.IMG_ENTRY_GROUP);
    // }
    //			
    // }
    //		
    // return
    // Activator.getDefault().getImage(BrowserWidgetsConstants.IMG_ENTRY);
    // }

    /**
     * {@inheritDoc}
     */
    public Font getFont( Object element )
    {

        IEntry entry = null;
        if ( element instanceof IEntry )
        {
            entry = ( IEntry ) element;
        }
        else if ( element instanceof ISearchResult )
        {
            entry = ( ( ISearchResult ) element ).getEntry();
        }

        if ( entry != null )
        {
            if ( !entry.isConsistent() )
            {
                FontData[] fontData = PreferenceConverter.getFontDataArray( BrowserCommonActivator.getDefault()
                    .getPreferenceStore(), BrowserCommonConstants.PREFERENCE_ERROR_FONT );
                return BrowserCommonActivator.getDefault().getFont( fontData );
            }
        }

        return null;
    }


    /**
     * {@inheritDoc}
     */
    public Color getForeground( Object element )
    {

        IEntry entry = null;
        if ( element instanceof IEntry )
        {
            entry = ( IEntry ) element;
        }
        else if ( element instanceof ISearchResult )
        {
            entry = ( ( ISearchResult ) element ).getEntry();
        }

        if ( entry != null )
        {
            if ( !entry.isConsistent() )
            {
                RGB rgb = PreferenceConverter.getColor( BrowserCommonActivator.getDefault().getPreferenceStore(),
                    BrowserCommonConstants.PREFERENCE_ERROR_COLOR );
                return BrowserCommonActivator.getDefault().getColor( rgb );
            }
        }

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
