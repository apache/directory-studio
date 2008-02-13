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

import org.apache.directory.studio.ldapbrowser.core.model.schema.AttributeValueEditorRelation;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SyntaxValueEditorRelation;
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
    public Map<String, String> getAttributeValueEditorMap()
    {
        if ( attributeValueEditorCache == null )
        {
            attributeValueEditorCache = new HashMap<String, String>();
            AttributeValueEditorRelation[] relations = getAttributeValueEditorRelations();
            for ( int i = 0; i < relations.length; i++ )
            {
                if ( relations[i].getAttributeNumericOidOrType() != null )
                {
                    attributeValueEditorCache.put( relations[i].getAttributeNumericOidOrType()
                        .toLowerCase(), relations[i].getValueEditorClassName() );
                }
            }
        }
        return attributeValueEditorCache;
    }


    /**
     * Gets an array containing all the Attribute Value Editor Relations.
     *
     * @return
     *      an array containing all the Attribute Value Editor Relations
     */
    public AttributeValueEditorRelation[] getAttributeValueEditorRelations()
    {
        AttributeValueEditorRelation[] aver = ( AttributeValueEditorRelation[] ) load( BrowserCommonConstants.PREFERENCE_ATTRIBUTE_VALUEEDITOR_RELATIONS );
        return aver;
    }


    /**
     * Sets the Attribute Value Editor Relations.
     *
     * @param attributeValueEditorRelations
     *      an array containing all the Attribute Value Editor Relations
     */
    public void setAttributeValueEditorRelations( AttributeValueEditorRelation[] attributeValueEditorRelations )
    {
        store( BrowserCommonConstants.PREFERENCE_ATTRIBUTE_VALUEEDITOR_RELATIONS, attributeValueEditorRelations );
        attributeValueEditorCache = null;
    }


    /**
     * Gets the default Attribute Value Editor Relations.
     *
     * @return
     *      an array containing all the default Attribute Value Editor Relations
     */
    public AttributeValueEditorRelation[] getDefaultAttributeValueEditorRelations()
    {
        AttributeValueEditorRelation[] aver = ( AttributeValueEditorRelation[] ) loadDefault( BrowserCommonConstants.PREFERENCE_ATTRIBUTE_VALUEEDITOR_RELATIONS );
        return aver;
    }


    /**
     * Sets the default Attribute Value Editor Relations.
     *
     * @param attributeValueEditorRelations
     *      an array containing all the default Attribute Value Editor Relations
     */
    public void setDefaultAttributeValueEditorRelations(
        AttributeValueEditorRelation[] attributeValueEditorRelations )
    {
        storeDefault( BrowserCommonConstants.PREFERENCE_ATTRIBUTE_VALUEEDITOR_RELATIONS, attributeValueEditorRelations );
    }



    /**
     * Gets a Map containing all the Syntax Value Editors.
     *
     * @return
     *      a Map containing all the Syntax Value Editors
     */
    public Map<String, String> getSyntaxValueEditorMap()
    {
        if ( syntaxValueEditorCache == null )
        {
            syntaxValueEditorCache = new HashMap<String, String>();
            SyntaxValueEditorRelation[] relations = getSyntaxValueEditorRelations();
            for ( int i = 0; i < relations.length; i++ )
            {
                if ( relations[i].getSyntaxOID() != null )
                {
                    syntaxValueEditorCache.put( relations[i].getSyntaxOID().toLowerCase(), relations[i]
                        .getValueEditorClassName() );
                }
            }
        }
        return syntaxValueEditorCache;
    }


    /**
     * Sets the Syntax Value Editor Relations.
     *
     * @param syntaxValueEditorRelations
     *      an array containing the Syntax Value Editor Relations to set
     */
    public void setSyntaxValueEditorRelations( SyntaxValueEditorRelation[] syntaxValueEditorRelations )
    {
        store( BrowserCommonConstants.PREFERENCE_SYNTAX_VALUEPEDITOR_RELATIONS, syntaxValueEditorRelations );
        syntaxValueEditorCache = null;
    }


    /**
     * Gets an array containing all the Syntax Value Editor Relations
     *
     * @return
     *      an array containing all the Syntax Value Editor Relations
     */
    public SyntaxValueEditorRelation[] getSyntaxValueEditorRelations()
    {
        SyntaxValueEditorRelation[] sver = ( SyntaxValueEditorRelation[] ) load( BrowserCommonConstants.PREFERENCE_SYNTAX_VALUEPEDITOR_RELATIONS );
        return sver;
    }


    /**
     * Gets an array containing all the default Syntax Value Editor Relations
     *
     * @return
     *      an array containing all the default Syntax Value Editor Relations
     */
    public SyntaxValueEditorRelation[] getDefaultSyntaxValueEditorRelations()
    {
        SyntaxValueEditorRelation[] sver = ( SyntaxValueEditorRelation[] ) loadDefault( BrowserCommonConstants.PREFERENCE_SYNTAX_VALUEPEDITOR_RELATIONS );
        return sver;
    }


    /**
     * Sets the default Syntax Value Editor Relations.
     *
     * @param syntaxValueEditorRelations
     *      an array containing the default Syntax Value Editor Relations to set
     */
    public void setDefaultSyntaxValueEditorRelations( SyntaxValueEditorRelation[] syntaxValueEditorRelations )
    {
        storeDefault( BrowserCommonConstants.PREFERENCE_SYNTAX_VALUEPEDITOR_RELATIONS, syntaxValueEditorRelations );
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
