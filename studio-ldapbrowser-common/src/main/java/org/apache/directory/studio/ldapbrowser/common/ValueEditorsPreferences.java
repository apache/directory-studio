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

package org.apache.directory.studio.ldapbrowser.common;


import java.util.HashMap;
import java.util.Map;

import org.apache.directory.studio.ldapbrowser.core.model.schema.AttributeValueProviderRelation;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SyntaxValueProviderRelation;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;

import org.eclipse.jface.preference.IPreferenceStore;


/**
 * This class is used to manage and access the preferences of the Value Editors Plugin.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ValueEditorsPreferences
{

    /** The attribute value editor cache. */
    private Map<String, String> attributeValueEditorCache;

    /** The syntax value editor cache. */
    private Map<String, String> syntaxValueEditorCache;

    
    /**
     * Gets a Map containing all the Attribute Value Editors.
     *
     * @return
     *      a Map containing all the Attribute Value Editors
     */
    public Map getAttributeValueEditorMap()
    {
        if ( attributeValueEditorCache == null )
        {
            attributeValueEditorCache = new HashMap<String, String>();
            AttributeValueProviderRelation[] relations = getAttributeValueProviderRelations();
            for ( int i = 0; i < relations.length; i++ )
            {
                if ( relations[i].getAttributeNumericOidOrType() != null )
                {
                    attributeValueEditorCache.put( relations[i].getAttributeNumericOidOrType()
                        .toLowerCase(), relations[i].getValueProviderClassname() );
                }
            }
        }
        return attributeValueEditorCache;
    }


    /**
     * Gets an array containing all the Attribute Value Provider Relations.
     *
     * @return
     *      an array containing all the Attribute Value Provider Relations
     */
    public AttributeValueProviderRelation[] getAttributeValueProviderRelations()
    {
        AttributeValueProviderRelation[] avpr = ( AttributeValueProviderRelation[] ) load( BrowserCommonConstants.PREFERENCE_ATTRIBUTE_VALUEPROVIDER_RELATIONS );
        return avpr;
    }


    /**
     * Sets the Attribute Value Provider Relations.
     *
     * @param attributeValueProviderRelations
     *      an array containing all the Attribute Value Provider Relations
     */
    public void setAttributeValueProviderRelations( AttributeValueProviderRelation[] attributeValueProviderRelations )
    {
        store( BrowserCommonConstants.PREFERENCE_ATTRIBUTE_VALUEPROVIDER_RELATIONS, attributeValueProviderRelations );
        attributeValueEditorCache = null;
    }


    /**
     * Gets the default Attribute Value Provider Relations.
     *
     * @return
     *      an array containing all the default Attribute Value Provider Relations
     */
    public AttributeValueProviderRelation[] getDefaultAttributeValueProviderRelations()
    {
        AttributeValueProviderRelation[] avpr = ( AttributeValueProviderRelation[] ) loadDefault( BrowserCommonConstants.PREFERENCE_ATTRIBUTE_VALUEPROVIDER_RELATIONS );
        return avpr;
    }


    /**
     * Sets the default Attribute Value Provider Relations.
     *
     * @param attributeValueProviderRelations
     *      an array containing all the default Attribute Value Provider Relations
     */
    public void setDefaultAttributeValueProviderRelations(
        AttributeValueProviderRelation[] attributeValueProviderRelations )
    {
        storeDefault( BrowserCommonConstants.PREFERENCE_ATTRIBUTE_VALUEPROVIDER_RELATIONS, attributeValueProviderRelations );
    }



    /**
     * Gets a Map containing all the Syntax Value Editors.
     *
     * @return
     *      a Map containing all the Syntax Value Editors
     */
    public Map getSyntaxValueEditorMap()
    {
        if ( syntaxValueEditorCache == null )
        {
            syntaxValueEditorCache = new HashMap<String, String>();
            SyntaxValueProviderRelation[] relations = getSyntaxValueProviderRelations();
            for ( int i = 0; i < relations.length; i++ )
            {
                if ( relations[i].getSyntaxOID() != null )
                {
                    syntaxValueEditorCache.put( relations[i].getSyntaxOID().toLowerCase(), relations[i]
                        .getValueProviderClassname() );
                }
            }
        }
        return syntaxValueEditorCache;
    }


    /**
     * Sets the Syntax Value Provider Relations.
     *
     * @param syntaxValueProviderRelations
     *      an array containing the Syntax Value Provider Relations to set
     */
    public void setSyntaxValueProviderRelations( SyntaxValueProviderRelation[] syntaxValueProviderRelations )
    {
        store( BrowserCommonConstants.PREFERENCE_SYNTAX_VALUEPROVIDER_RELATIONS, syntaxValueProviderRelations );
        syntaxValueEditorCache = null;
    }


    /**
     * Gets an array containing all the Syntax Value Provider Relations
     *
     * @return
     *      an array containing all the Syntax Value Provider Relations
     */
    public SyntaxValueProviderRelation[] getSyntaxValueProviderRelations()
    {
        SyntaxValueProviderRelation[] svpr = ( SyntaxValueProviderRelation[] ) load( BrowserCommonConstants.PREFERENCE_SYNTAX_VALUEPROVIDER_RELATIONS );
        return svpr;
    }


    /**
     * Gets an array containing all the default Syntax Value Provider Relations
     *
     * @return
     *      an array containing all the default Syntax Value Provider Relations
     */
    public SyntaxValueProviderRelation[] getDefaultSyntaxValueProviderRelations()
    {
        SyntaxValueProviderRelation[] svpr = ( SyntaxValueProviderRelation[] ) loadDefault( BrowserCommonConstants.PREFERENCE_SYNTAX_VALUEPROVIDER_RELATIONS );
        return svpr;
    }


    /**
     * Sets the default Syntax Value Provider Relations.
     *
     * @param syntaxValueProviderRelations
     *      an array containing the default Syntax Value Provider Relations to set
     */
    public void setDefaultSyntaxValueProviderRelations( SyntaxValueProviderRelation[] syntaxValueProviderRelations )
    {
        storeDefault( BrowserCommonConstants.PREFERENCE_SYNTAX_VALUEPROVIDER_RELATIONS, syntaxValueProviderRelations );
    }


    /**
     * Loads the current value of the string-valued property with the given name.
     *
     * @param key
     *      the name of the property
     * @return
     *      the corresponding object
     */
    private static Object load( String key )
    {
        IPreferenceStore store = BrowserCommonActivator.getDefault().getPreferenceStore();
        String s = store.getString( key );
        return Utils.deserialize( s );
    }


    /**
     * Stores the current value of the string-valued property with the given name.
     *
     * @param key
     *      the name of the property
     * @param o
     *      the new current value of the property
     */
    private static void store( String key, Object o )
    {
        IPreferenceStore store = BrowserCommonActivator.getDefault().getPreferenceStore();
        String s = Utils.serialize( o );
        store.setValue( key, s );
    }


    /**
     * Loads the default value for the string-valued property with the given name.
     *
     * @param key
     *      the name of the property
     * @return
     *      the default value of the named property
     */
    private static Object loadDefault( String key )
    {
        IPreferenceStore store = BrowserCommonActivator.getDefault().getPreferenceStore();
        String s = store.getDefaultString( key );
        return Utils.deserialize( s );
    }


    /**
     * Stores the default value for the string-valued property with the given name.
     *
     * @param key
     *      the name of the property
     * @param o
     *      the new default value for the property
     */
    private static void storeDefault( String key, Object o )
    {
        IPreferenceStore store = BrowserCommonActivator.getDefault().getPreferenceStore();
        String s = Utils.serialize( o );
        store.setDefault( key, s );
    }
}
