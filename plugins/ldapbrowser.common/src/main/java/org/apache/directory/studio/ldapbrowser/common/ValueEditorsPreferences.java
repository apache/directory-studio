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

import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.ldapbrowser.core.model.schema.AttributeValueEditorRelation;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SyntaxValueEditorRelation;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;
import org.eclipse.jface.preference.IPreferenceStore;


/**
 * This class is used to manage and access the preferences of the Value Editors Plugin.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
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
                    attributeValueEditorCache.put( Strings.toLowerCase( relations[i].getAttributeNumericOidOrType() ),
                        relations[i].getValueEditorClassName() );
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
        IPreferenceStore store = BrowserCommonActivator.getDefault().getPreferenceStore();
        String s = store.getString( BrowserCommonConstants.PREFERENCE_ATTRIBUTE_VALUEEDITOR_RELATIONS );
        // Migration issue from 1.0.1 to 1.1.0 (DIRSTUDIO-287): class AttributeValueProviderRelation 
        // was renamed to AttributeValueEditorRelation, to be able to load the old configuration it
        // is necessary to replace the old class name with the new class name.
        s = s.replaceAll( "AttributeValueProviderRelation", "AttributeValueEditorRelation" ); //$NON-NLS-1$ //$NON-NLS-2$
        s = s.replaceAll( "valueProviderClassname", "valueEditorClassName" ); //$NON-NLS-1$ //$NON-NLS-2$
        AttributeValueEditorRelation[] aver = ( AttributeValueEditorRelation[] ) Utils.deserialize( s );
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
        IPreferenceStore store = BrowserCommonActivator.getDefault().getPreferenceStore();
        String s = store.getDefaultString( BrowserCommonConstants.PREFERENCE_ATTRIBUTE_VALUEEDITOR_RELATIONS );
        // Migration issue from 1.0.1 to 1.1.0 (DIRSTUDIO-287): class AttributeValueProviderRelation 
        // was renamed to AttributeValueEditorRelation, to be able to load the old configuration it
        // is necessary to replace the old class name with the new class name.
        s = s.replaceAll( "AttributeValueProviderRelation", "AttributeValueEditorRelation" ); //$NON-NLS-1$ //$NON-NLS-2$
        s = s.replaceAll( "valueProviderClassname", "valueEditorClassName" ); //$NON-NLS-1$ //$NON-NLS-2$
        AttributeValueEditorRelation[] aver = ( AttributeValueEditorRelation[] ) Utils.deserialize( s );
        return aver;
    }


    /**
     * Sets the default Attribute Value Editor Relations.
     *
     * @param attributeValueEditorRelations
     *      an array containing all the default Attribute Value Editor Relations
     */
    public void setDefaultAttributeValueEditorRelations( AttributeValueEditorRelation[] attributeValueEditorRelations )
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
                    syntaxValueEditorCache.put( Strings.toLowerCase( relations[i].getSyntaxOID() ), relations[i]
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
        IPreferenceStore store = BrowserCommonActivator.getDefault().getPreferenceStore();
        String s = store.getString( BrowserCommonConstants.PREFERENCE_SYNTAX_VALUEPEDITOR_RELATIONS );
        // Migration issue from 1.0.1 to 1.1.0 (DIRSTUDIO-287): class SyntaxValueProviderRelation 
        // was renamed to SyntaxValueEditorRelation, to be able to load the old configuration it
        // is necessary to replace the old class name with the new class name.
        s = s.replaceAll( "SyntaxValueProviderRelation", "SyntaxValueEditorRelation" ); //$NON-NLS-1$ //$NON-NLS-2$
        s = s.replaceAll( "valueProviderClassname", "valueEditorClassName" ); //$NON-NLS-1$ //$NON-NLS-2$
        SyntaxValueEditorRelation[] sver = ( SyntaxValueEditorRelation[] ) Utils.deserialize( s );
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
        IPreferenceStore store = BrowserCommonActivator.getDefault().getPreferenceStore();
        String s = store.getDefaultString( BrowserCommonConstants.PREFERENCE_SYNTAX_VALUEPEDITOR_RELATIONS );
        // Migration issue from 1.0.1 to 1.1.0 (DIRSTUDIO-287): class SyntaxValueProviderRelation 
        // was renamed to SyntaxValueEditorRelation, to be able to load the old configuration it
        // is necessary to replace the old class name with the new class name.
        s = s.replaceAll( "SyntaxValueProviderRelation", "SyntaxValueEditorRelation" ); //$NON-NLS-1$ //$NON-NLS-2$
        s = s.replaceAll( "valueProviderClassname", "valueEditorClassName" ); //$NON-NLS-1$ //$NON-NLS-2$
        SyntaxValueEditorRelation[] sver = ( SyntaxValueEditorRelation[] ) Utils.deserialize( s );
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
