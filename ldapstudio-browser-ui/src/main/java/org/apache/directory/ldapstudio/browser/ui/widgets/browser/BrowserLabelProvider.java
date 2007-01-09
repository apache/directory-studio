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

package org.apache.directory.ldapstudio.browser.ui.widgets.browser;


import org.apache.directory.ldapstudio.browser.core.BrowserCoreConstants;
import org.apache.directory.ldapstudio.browser.core.BrowserCorePlugin;
import org.apache.directory.ldapstudio.browser.core.internal.model.AliasBaseEntry;
import org.apache.directory.ldapstudio.browser.core.internal.model.DirectoryMetadataEntry;
import org.apache.directory.ldapstudio.browser.core.internal.model.ReferralBaseEntry;
import org.apache.directory.ldapstudio.browser.core.model.IBookmark;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.IRootDSE;
import org.apache.directory.ldapstudio.browser.core.model.ISearch;
import org.apache.directory.ldapstudio.browser.core.model.ISearchResult;
import org.apache.directory.ldapstudio.browser.core.model.RDN;
import org.apache.directory.ldapstudio.browser.core.model.RDNPart;
import org.apache.directory.ldapstudio.browser.core.utils.Utils;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;

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


public class BrowserLabelProvider extends LabelProvider implements IFontProvider, IColorProvider
{

    private BrowserPreferences preferences;


    public BrowserLabelProvider( BrowserPreferences preferences )
    {
        this.preferences = preferences;
    }


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
            if ( entry.isChildrenInitialized() && entry.getChildrenCount() > 0 )
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
            else if ( entry.hasParententry() )
            {

                String label = "";
                if ( this.preferences.getEntryLabel() == BrowserUIConstants.SHOW_DN )
                {
                    label = entry.getDn().toString();
                }
                else if ( this.preferences.getEntryLabel() == BrowserUIConstants.SHOW_RDN )
                {
                    label = entry.getRdn().toString();

                }
                else if ( this.preferences.getEntryLabel() == BrowserUIConstants.SHOW_RDN_VALUE )
                {
                    label = entry.getRdn().getValue();
                }

                label += append.toString();

                if ( this.preferences.isEntryAbbreviate()
                    && label.length() > this.preferences.getEntryAbbreviateMaxLength() )
                {
                    label = Utils.shorten( label, this.preferences.getEntryAbbreviateMaxLength() );
                    // label =
                    // label.substring(0,this.preferences.getEntryAbbreviateMaxLength()/2)
                    // + "..." +
                    // label.substring(label.length()-this.preferences.getEntryAbbreviateMaxLength()/2,
                    // label.length());
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
            else if ( sr.getEntry().hasParententry() )
            {
                String label = "";
                if ( this.preferences.getSearchResultLabel() == BrowserUIConstants.SHOW_DN )
                {
                    label = sr.getEntry().getDn().toString();
                }
                else if ( this.preferences.getSearchResultLabel() == BrowserUIConstants.SHOW_RDN )
                {
                    label = sr.getEntry().getRdn().toString();
                }
                else if ( this.preferences.getSearchResultLabel() == BrowserUIConstants.SHOW_RDN_VALUE )
                {
                    label = sr.getEntry().getRdn().getValue();
                }

                if ( this.preferences.isSearchResultAbbreviate()
                    && label.length() > this.preferences.getSearchResultAbbreviateMaxLength() )
                {
                    label = Utils.shorten( label, this.preferences.getSearchResultAbbreviateMaxLength() );
                    // label =
                    // label.substring(0,this.preferences.getSearchResultAbbreviateMaxLength()/2)
                    // + "..." +
                    // label.substring(label.length()-this.preferences.getSearchResultAbbreviateMaxLength()/2,
                    // label.length());
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
                return BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_SEARCH );
            }
            else
            {
                return BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_SEARCH_UNPERFORMED );
            }
        }
        else if ( obj instanceof IBookmark )
        {
            // IBookmark bookmark = (IBookmark) obj;
            return BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_BOOKMARK );
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
                return BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_DIT );
            }
            else if ( category.getType() == BrowserCategory.TYPE_SEARCHES )
            {
                return BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_SEARCHES );
            }
            else if ( category.getType() == BrowserCategory.TYPE_BOOKMARKS )
            {
                return BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_BOOKMARKS );
            }
            else
            {
                return null;
            }
        }
        else
        {
            // return
            // BrowserUIPlugin.getDefault().getImage("icons/sandglass.gif");
            return null;
        }
    }


    private Image getImageByRdn( IEntry entry )
    {

        if ( entry instanceof IRootDSE )
        {
            return BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_ENTRY_ROOT );
        }
        else if ( entry instanceof DirectoryMetadataEntry && ( ( DirectoryMetadataEntry ) entry ).isSchemaEntry() )
        {
            return BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_BROWSER_SCHEMABROWSEREDITOR );
        }
        else if ( entry.getDn().equals( entry.getConnection().getSchema().getDn() ) )
        {
            return BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_BROWSER_SCHEMABROWSEREDITOR );
        }
        else if ( BrowserCorePlugin.getDefault().getPluginPreferences().getBoolean(
            BrowserCoreConstants.PREFERENCE_SHOW_ALIAS_AND_REFERRAL_OBJECTS )
            && entry.isAlias() )
        {
            return BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_ENTRY_ALIAS );
        }
        else if ( BrowserCorePlugin.getDefault().getPluginPreferences().getBoolean(
            BrowserCoreConstants.PREFERENCE_SHOW_ALIAS_AND_REFERRAL_OBJECTS )
            && entry.isReferral() )
        {
            return BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_ENTRY_REF );
        }
        else if ( entry.isSubentry() )
        {
            return BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_BROWSER_SCHEMABROWSEREDITOR );
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
                    return BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_ENTRY_PERSON );
                }
                else if ( "ou".equals( part.getType() ) || "o".equals( part.getType() ) )
                {
                    return BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_ENTRY_ORG );
                }
                else if ( "dc".equals( part.getType() ) || "c".equals( part.getType() ) || "l".equals( part.getType() ) )
                {
                    return BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_ENTRY_DC );
                }
            }
        }

        return BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_ENTRY );
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
    // BrowserUIPlugin.getDefault().getImage(BrowserUIConstants.IMG_ENTRY_ROOT);
    // }
    // else
    // if(entry.getDn().equals(entry.getConnection().getSchema().getDn()))
    // {
    // return
    // BrowserUIPlugin.getDefault().getImage(BrowserUIConstants.IMG_BROWSER_SCHEMABROWSEREDITOR);
    // }
    // else if(ocSet.contains(ObjectClassDescription.OC_ALIAS.toUpperCase())
    // || ocSet.contains(ObjectClassDescription.OC_REFERRAL.toUpperCase()))
    // {
    // return
    // BrowserUIPlugin.getDefault().getImage(BrowserUIConstants.IMG_ENTRY_REF);
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
    // BrowserUIPlugin.getDefault().getImage(BrowserUIConstants.IMG_ENTRY_PERSON);
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
    // BrowserUIPlugin.getDefault().getImage(BrowserUIConstants.IMG_ENTRY_ORG);
    // }
    // else
    // if(ocSet.contains(ObjectClassDescription.OC_COUNTRY.toUpperCase())
    // || ocSet.contains(ObjectClassDescription.OC_LOCALITY.toUpperCase())
    // || ocSet.contains(ObjectClassDescription.OC_DCOBJECT.toUpperCase())
    // || ocSet.contains(ObjectClassDescription.OC_DOMAIN.toUpperCase())) {
    // return
    // BrowserUIPlugin.getDefault().getImage(BrowserUIConstants.IMG_ENTRY_DC);
    // }
    // else
    // if(ocSet.contains(ObjectClassDescription.OC_GROUPOFNAMES.toUpperCase())
    // ||
    // ocSet.contains(ObjectClassDescription.OC_GROUPOFUNIQUENAMES.toUpperCase())
    // ||
    // ocSet.contains(ObjectClassDescription.OC_POSIXGROUP.toUpperCase())) {
    // return
    // BrowserUIPlugin.getDefault().getImage(BrowserUIConstants.IMG_ENTRY_GROUP);
    // }
    //			
    // }
    //		
    // return
    // BrowserUIPlugin.getDefault().getImage(BrowserUIConstants.IMG_ENTRY);
    // }

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
                FontData[] fontData = PreferenceConverter.getFontDataArray( BrowserUIPlugin.getDefault()
                    .getPreferenceStore(), BrowserUIConstants.PREFERENCE_ERROR_FONT );
                return BrowserUIPlugin.getDefault().getFont( fontData );
            }
        }

        return null;
    }


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
                RGB rgb = PreferenceConverter.getColor( BrowserUIPlugin.getDefault().getPreferenceStore(),
                    BrowserUIConstants.PREFERENCE_ERROR_COLOR );
                return BrowserUIPlugin.getDefault().getColor( rgb );
            }
        }

        return null;
    }


    public Color getBackground( Object element )
    {
        return null;
    }

}
