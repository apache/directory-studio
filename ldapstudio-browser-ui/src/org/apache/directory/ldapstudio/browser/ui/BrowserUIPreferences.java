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

package org.apache.directory.ldapstudio.browser.ui;


import java.util.HashMap;
import java.util.Map;

import org.apache.directory.ldapstudio.browser.core.model.schema.AttributeValueProviderRelation;
import org.apache.directory.ldapstudio.browser.core.model.schema.SyntaxValueProviderRelation;
import org.apache.directory.ldapstudio.browser.core.utils.Utils;

import org.eclipse.jface.preference.IPreferenceStore;


public class BrowserUIPreferences
{

    private Map attributeValueProviderRelationCache;


    public Map getAttributeValueProviderMap()
    {
        if ( this.attributeValueProviderRelationCache == null )
        {
            this.attributeValueProviderRelationCache = new HashMap();
            AttributeValueProviderRelation[] relations = this.getAttributeValueProviderRelations();
            for ( int i = 0; i < relations.length; i++ )
            {
                if ( relations[i].getAttributeNumericOidOrType() != null )
                {
                    this.attributeValueProviderRelationCache.put( relations[i].getAttributeNumericOidOrType()
                        .toLowerCase(), relations[i].getValueProviderClassname() );
                }
            }
        }
        return this.attributeValueProviderRelationCache;
    }


    public AttributeValueProviderRelation[] getAttributeValueProviderRelations()
    {
        AttributeValueProviderRelation[] avpr = ( AttributeValueProviderRelation[] ) load( BrowserUIConstants.PREFERENCE_ATTRIBUTE_VALUEPROVIDER_RELATIONS );
        return avpr;
    }


    public void setAttributeValueProviderRelations( AttributeValueProviderRelation[] attributeValueProviderRelations )
    {
        store( BrowserUIConstants.PREFERENCE_ATTRIBUTE_VALUEPROVIDER_RELATIONS, attributeValueProviderRelations );
        this.attributeValueProviderRelationCache = null;
    }


    public AttributeValueProviderRelation[] getDefaultAttributeValueProviderRelations()
    {
        AttributeValueProviderRelation[] avpr = ( AttributeValueProviderRelation[] ) loadDefault( BrowserUIConstants.PREFERENCE_ATTRIBUTE_VALUEPROVIDER_RELATIONS );
        return avpr;
    }


    public void setDefaultAttributeValueProviderRelations(
        AttributeValueProviderRelation[] attributeValueProviderRelations )
    {
        storeDefault( BrowserUIConstants.PREFERENCE_ATTRIBUTE_VALUEPROVIDER_RELATIONS, attributeValueProviderRelations );
    }

    private Map syntaxValueProviderCache;


    public Map getSyntaxValueProviderMap()
    {
        if ( this.syntaxValueProviderCache == null )
        {
            this.syntaxValueProviderCache = new HashMap();
            SyntaxValueProviderRelation[] relations = this.getSyntaxValueProviderRelations();
            for ( int i = 0; i < relations.length; i++ )
            {
                if ( relations[i].getSyntaxOID() != null )
                {
                    this.syntaxValueProviderCache.put( relations[i].getSyntaxOID().toLowerCase(), relations[i]
                        .getValueProviderClassname() );
                }
            }
        }
        return this.syntaxValueProviderCache;
    }


    public void setSyntaxValueProviderRelations( SyntaxValueProviderRelation[] syntaxValueProviderRelations )
    {
        store( BrowserUIConstants.PREFERENCE_SYNTAX_VALUEPROVIDER_RELATIONS, syntaxValueProviderRelations );
        this.syntaxValueProviderCache = null;
    }


    public SyntaxValueProviderRelation[] getSyntaxValueProviderRelations()
    {
        SyntaxValueProviderRelation[] svpr = ( SyntaxValueProviderRelation[] ) load( BrowserUIConstants.PREFERENCE_SYNTAX_VALUEPROVIDER_RELATIONS );
        return svpr;
    }


    public SyntaxValueProviderRelation[] getDefaultSyntaxValueProviderRelations()
    {
        SyntaxValueProviderRelation[] svpr = ( SyntaxValueProviderRelation[] ) loadDefault( BrowserUIConstants.PREFERENCE_SYNTAX_VALUEPROVIDER_RELATIONS );
        return svpr;
    }


    public void setDefaultSyntaxValueProviderRelations( SyntaxValueProviderRelation[] syntaxValueProviderRelations )
    {
        storeDefault( BrowserUIConstants.PREFERENCE_SYNTAX_VALUEPROVIDER_RELATIONS, syntaxValueProviderRelations );
    }


    private static Object load( String key )
    {
        IPreferenceStore store = BrowserUIPlugin.getDefault().getPreferenceStore();
        String s = store.getString( key );
        return Utils.deserialize( s );
    }


    private static void store( String key, Object o )
    {
        IPreferenceStore store = BrowserUIPlugin.getDefault().getPreferenceStore();
        String s = Utils.serialize( o );
        store.setValue( key, s );
    }


    private static Object loadDefault( String key )
    {
        IPreferenceStore store = BrowserUIPlugin.getDefault().getPreferenceStore();
        String s = store.getDefaultString( key );
        return Utils.deserialize( s );
    }


    private static void storeDefault( String key, Object o )
    {
        IPreferenceStore store = BrowserUIPlugin.getDefault().getPreferenceStore();
        String s = Utils.serialize( o );
        store.setDefault( key, s );
    }

}
