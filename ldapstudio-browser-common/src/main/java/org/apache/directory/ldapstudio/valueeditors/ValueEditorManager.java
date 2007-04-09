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

package org.apache.directory.ldapstudio.valueeditors;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.directory.ldapstudio.browser.common.BrowserCommonActivator;
import org.apache.directory.ldapstudio.browser.common.BrowserCommonConstants;
import org.apache.directory.ldapstudio.browser.core.model.AttributeHierarchy;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.core.model.schema.AttributeTypeDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.LdapSyntaxDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.Schema;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * A ValueEditorManager is used to manage value editors. It provides methods to get
 * the best or alternative value editors for a given attribute or value. It takes
 * user preferences into account when determine the best value editor. At least
 * it provides default text and binary value editors. 
 * 
 * The available value editors are specified by the extension point
 * <code>org.apache.directory.ldapstudio.valueeditors</code>. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ValueEditorManager
{
    /** The extension point ID for value editors */
    private static final String EXTENSION_POINT = "org.apache.directory.ldapstudio.valueeditors";

    /** The composite used to create the value editors **/
    private Composite parent;

    /** 
     * The value editor explicitly selected by the user. If this
     * member is not null it is always returned as current value editor.
     */
    private IValueEditor userSelectedValueEditor;

    /** The special value editor for multi-valued attributes */
    private MultivaluedValueEditor multiValuedValueEditor;

    /** The default string editor for single-line values */
    private IValueEditor defaultStringSingleLineValueEditor;

    /** The default string editor for multi-line values */
    private IValueEditor defaultStringMultiLineValueEditor;

    /** The default binary editor */
    private IValueEditor defaultBinaryValueEditor;

    /** A Map wich all available value editors. */
    private Map<String, IValueEditor> class2ValueEditors;


    /**
     * Creates a new instance of ValueEditorManager.
     *
     * @param parent the composite used to create the value editors
     */
    public ValueEditorManager( Composite parent )
    {
        this.parent = parent;
        userSelectedValueEditor = null;

        // init value editor map
        class2ValueEditors = new HashMap<String, IValueEditor>();
        Collection<IValueEditor> valueEditors = createValueEditors( parent );
        for ( IValueEditor valueEditor : valueEditors )
        {
            class2ValueEditors.put( valueEditor.getClass().getName(), valueEditor );
        }

        // special case: multivalued editor
        multiValuedValueEditor = new MultivaluedValueEditor( this.parent, this );
        multiValuedValueEditor.setValueEditorName( "Mulitvalued Editor" );
        multiValuedValueEditor.setValueEditorImageDescriptor( BrowserCommonActivator.getDefault().getImageDescriptor(
            BrowserCommonConstants.IMG_MULTIVALUEDEDITOR ) );

        // get default editors from value editor map
        defaultStringSingleLineValueEditor = class2ValueEditors.get( InPlaceTextValueEditor.class.getName() );
        defaultStringMultiLineValueEditor = class2ValueEditors.get( TextValueEditor.class.getName() );
        defaultBinaryValueEditor = class2ValueEditors.get( HexValueEditor.class.getName() );
    }


    /**
     * Disposes all value editors.
     */
    public void dispose()
    {
        if ( this.parent != null )
        {
            this.userSelectedValueEditor = null;
            this.multiValuedValueEditor.dispose();
            this.defaultStringSingleLineValueEditor.dispose();
            this.defaultStringMultiLineValueEditor.dispose();
            this.defaultBinaryValueEditor.dispose();

            for ( Iterator it = this.class2ValueEditors.values().iterator(); it.hasNext(); )
            {
                IValueEditor vp = ( IValueEditor ) it.next();
                vp.dispose();
            }

            this.parent = null;
        }
    }


    /**
     * Sets the value editor explicitly selected by the user. Set 
     * userSelectedValueEditor to null to remove the selection.
     *
     * @param userSelectedValueEditor the user selected value editor, may be null.
     */
    public void setUserSelectedValueEditor( IValueEditor userSelectedValueEditor )
    {
        this.userSelectedValueEditor = userSelectedValueEditor;
    }


    /**
     * Returns the current (best) value editor for the given attribute.
     * 
     * <ol>
     *  <li>If a user selected value editor is selected, this is returned.
     *  <li>If a specific value editor is defined for the attribute type this 
     *      value editor is returned. See preferences. 
     *  <li>If a specific value editor is defined for the attribute's syntax this 
     *      value editor is returned. See preferences. 
     *  <li>Otherwise a default value editor is returned. If the attribute is 
     *      binary the default Hex Editor is returned. Otherwise the default 
     *      Text Editor is returned.
     * </ol>
     *
     * @param schema the schema
     * @param attributeType the attribute type
     * @return the current value editor
     */
    public IValueEditor getCurrentValueEditor( Schema schema, String attributeType )
    {

        // check user-selected (forced) value editor
        if ( this.userSelectedValueEditor != null )
        {
            return this.userSelectedValueEditor;
        }

        // check attribute preferences
        AttributeTypeDescription atd = schema.getAttributeTypeDescription( attributeType );
        Map attributeValueEditorMap = BrowserCommonActivator.getDefault().getValueEditorsPreferences().getAttributeValueEditorMap();
        if ( atd.getNumericOID() != null && attributeValueEditorMap.containsKey( atd.getNumericOID().toLowerCase() ) )
        {
            return ( IValueEditor ) this.class2ValueEditors.get( attributeValueEditorMap.get( atd.getNumericOID()
                .toLowerCase() ) );
        }
        String[] names = atd.getNames();
        for ( int i = 0; i < names.length; i++ )
        {
            if ( attributeValueEditorMap.containsKey( names[i].toLowerCase() ) )
            {
                return ( IValueEditor ) this.class2ValueEditors.get( attributeValueEditorMap.get( names[i]
                    .toLowerCase() ) );
            }
        }

        // check syntax preferences
        LdapSyntaxDescription lsd = atd.getSyntaxDescription();
        Map syntaxValueEditorMap = BrowserCommonActivator.getDefault().getValueEditorsPreferences().getSyntaxValueEditorMap();
        if ( lsd.getNumericOID() != null && syntaxValueEditorMap.containsKey( lsd.getNumericOID().toLowerCase() ) )
        {
            return ( IValueEditor ) this.class2ValueEditors.get( syntaxValueEditorMap.get( lsd.getNumericOID()
                .toLowerCase() ) );
        }

        // return default
        if ( lsd.isBinary() )
        {
            return this.defaultBinaryValueEditor;
        }
        else
        {
            return this.defaultStringSingleLineValueEditor;
        }

    }


    /**
     * Returns the current (best) value editor for the given attribute.
     * 
     * @param entry the entry
     * @param attributeType the attributge type
     * @return the current value editor
     * @see #getCurrentValueEditor( Schema, String )
     */
    public IValueEditor getCurrentValueEditor( IEntry entry, String attributeType )
    {
        return getCurrentValueEditor( entry.getConnection().getSchema(), attributeType );
    }


    /**
     * Returns the current (best) value editor for the given value.
     * 
     * @param value the value
     * @return the current value editor
     * @see #getCurrentValueEditor( Schema, String )
     */
    public IValueEditor getCurrentValueEditor( IValue value )
    {

        IValueEditor ve = this.getCurrentValueEditor( value.getAttribute().getEntry(), value.getAttribute()
            .getDescription() );

        // here the value is known, we can check for single-line or multi-line
        if ( ve == this.defaultStringSingleLineValueEditor )
        {
            if ( value.getStringValue().indexOf( '\n' ) == -1 && value.getStringValue().indexOf( '\r' ) == -1 )
            {
                ve = this.defaultStringSingleLineValueEditor;
            }
            else
            {
                ve = this.defaultStringMultiLineValueEditor;
            }
        }

        return ve;
    }


    /**
     * Returns the current (best) value editor for the given attribute.
     * 
     * @param attributeHierarchy the attribute hierarchy
     * @return the current value editor
     * @see #getCurrentValueEditor( Schema, String )
     */
    public IValueEditor getCurrentValueEditor( AttributeHierarchy attributeHierarchy )
    {
        if ( attributeHierarchy == null )
        {
            return null;
        }
        else if ( attributeHierarchy.size() == 1 && attributeHierarchy.getAttribute().getValueSize() == 0 )
        {
            return this.getCurrentValueEditor( attributeHierarchy.getAttribute().getEntry(), attributeHierarchy
                .getAttribute().getDescription() );
        }
        else if ( attributeHierarchy.size() == 1 && attributeHierarchy.getAttribute().getValueSize() == 1 )
        {
            // special case objectClass and RDN: always return MV-editor
            // perhaps this should be moved somewhere else
            if ( attributeHierarchy.getAttribute().isObjectClassAttribute() )
            {
                return this.multiValuedValueEditor;
            }
            if ( attributeHierarchy.getAttribute().getValues()[0].isRdnPart() )
            {
                return this.multiValuedValueEditor;
            }

            return this.getCurrentValueEditor( attributeHierarchy.getAttribute().getValues()[0] );
        }
        else
        {
            return this.multiValuedValueEditor;
        }
    }


    /**
     * Returns alternative value editors for the given attribute. For now these
     * are the three default editors.
     *
     * @param entry the entry
     * @param attributeName the attribute
     * @return alternative value editors
     */
    public IValueEditor[] getAlternativeValueEditors( IEntry entry, String attributeName )
    {
        Schema schema = entry.getConnection().getSchema();
        return getAlternativeValueEditors( schema, attributeName );
    }


    /**
     * Returns alternative value editors for the given attribute. For now these
     * are the three default editors.
     * 
     * @param schema the schema
     * @param attributeName the attribute
     * @return alternative value editors
     */
    public IValueEditor[] getAlternativeValueEditors( Schema schema, String attributeName )
    {
        List<IValueEditor> alternativeList = new ArrayList<IValueEditor>();

        AttributeTypeDescription atd = schema.getAttributeTypeDescription( attributeName );

        if ( atd.getSyntaxDescription().isBinary() )
        {
            alternativeList.add( this.defaultBinaryValueEditor );
            alternativeList.add( this.defaultStringSingleLineValueEditor );
            alternativeList.add( this.defaultStringMultiLineValueEditor );
        }
        else if ( atd.getSyntaxDescription().isString() )
        {
            alternativeList.add( this.defaultStringSingleLineValueEditor );
            alternativeList.add( this.defaultStringMultiLineValueEditor );
            alternativeList.add( this.defaultBinaryValueEditor );
        }

        alternativeList.add( this.multiValuedValueEditor );

        alternativeList.remove( getCurrentValueEditor( schema, attributeName ) );

        return (org.apache.directory.ldapstudio.valueeditors.IValueEditor[] ) alternativeList.toArray( new IValueEditor[alternativeList.size()] );
    }


    /**
     * Returns alternative value editors for the given value. For now these
     * are the three default editors.
     *
     * @param value the value
     * @return lternative value editors
     */
    public IValueEditor[] getAlternativeValueEditors( IValue value )
    {
        List<IValueEditor> alternativeList = new ArrayList<IValueEditor>();

        if ( value.isBinary() )
        {
            alternativeList.add( this.defaultBinaryValueEditor );
            alternativeList.add( this.defaultStringSingleLineValueEditor );
            alternativeList.add( this.defaultStringMultiLineValueEditor );
        }
        else if ( value.isString() )
        {
            alternativeList.add( this.defaultStringSingleLineValueEditor );
            alternativeList.add( this.defaultStringMultiLineValueEditor );
            alternativeList.add( this.defaultBinaryValueEditor );
        }

        alternativeList.add( this.multiValuedValueEditor );

        alternativeList.remove( getCurrentValueEditor( value ) );

        return (org.apache.directory.ldapstudio.valueeditors.IValueEditor[] ) alternativeList.toArray( new IValueEditor[alternativeList.size()] );
    }


    /**
     * Returns alternative value editors for the given value. For now these
     * are the three default editors.
     * 
     * @param ah the attribute hierarchy
     * @return alternative value editors
     */
    public IValueEditor[] getAlternativeValueEditors( AttributeHierarchy ah )
    {
        if ( ah == null )
        {
            return new IValueEditor[0];
        }
        else if ( ah.size() == 1 && ah.getAttribute().getValueSize() == 0 )
        {
            return this.getAlternativeValueEditors( ah.getAttribute().getEntry(), ah.getAttribute().getDescription() );
        }
        else if ( ah.size() == 1 && ah.getAttribute().getValueSize() == 1 )
        {

            // special case objectClass and RDN: no alternative to the MV-Editor
            // perhaps this should be moved somewhere else
            if ( ah.getAttribute().isObjectClassAttribute() )
            {
                return new IValueEditor[0];
            }
            if ( ah.getAttribute().getValues()[0].isRdnPart() )
            {
                return new IValueEditor[0];
            }

            return this.getAlternativeValueEditors( ah.getAttribute().getValues()[0] );
        }
        else
        /* if(attribute.getValueSize() > 1) */{
            return new IValueEditor[0];
        }
    }


    /**
     * Returns all available value editors.
     *
     * @return all available value editors
     */
    public IValueEditor[] getAllValueEditors()
    {
        // use a set to avoid double entries
        Set<IValueEditor> list = new LinkedHashSet<IValueEditor>();

        list.add( this.defaultStringSingleLineValueEditor );
        list.add( this.defaultStringMultiLineValueEditor );
        list.add( defaultBinaryValueEditor );

        list.addAll( this.class2ValueEditors.values() );

        list.add( this.multiValuedValueEditor );

        return list.toArray( new IValueEditor[list.size()] );
    }


    /**
     * Returns the default binary editor (a HexEditor).
     *
     * @return the default binary editor
     */
    public IValueEditor getDefaultBinaryValueEditor()
    {
        return defaultBinaryValueEditor;
    }


    /**
     * Returns the default string editor (a TextEditor).
     *
     * @return the default string editor
     */
    public IValueEditor getDefaultStringValueEditor()
    {
        return defaultStringMultiLineValueEditor;
    }


    /**
     * Returns the multi-valued editor.
     *
     * @return the multi-valued editor
     */
    public MultivaluedValueEditor getMultiValuedValueEditor()
    {
        return multiValuedValueEditor;
    }


    /**
     * Creates and returns the value editors specified by value editors extensions.
     *
     * @param parent the parent composite
     * @return the value editors
     */
    private Collection<IValueEditor> createValueEditors( Composite parent )
    {
        Collection<IValueEditor> valueEditors = new ArrayList<IValueEditor>();

        Collection<ValueEditorExtension> valueEditorProxys = getValueEditorProxys();
        for ( ValueEditorExtension proxy : valueEditorProxys )
        {
            try
            {
                IValueEditor valueEditor = ( IValueEditor ) proxy.member.createExecutableExtension( "class" );
                valueEditor.create( parent );
                valueEditor.setValueEditorName( proxy.name );
                valueEditor.setValueEditorImageDescriptor( proxy.icon );
                valueEditors.add( valueEditor );
            }
            catch ( Exception e )
            {
                BrowserCommonActivator.getDefault().getLog().log(
                    new Status( IStatus.ERROR, BrowserCommonActivator.PLUGIN_ID, 1, "Unable to create ValueEditor "
                        + proxy.className, e ) );
            }
        }

        return valueEditors;
    }


    /**
     * Returns all value editor proxies specified by value editor extensions.
     *
     * @return the value editor proxies
     */
    public static Collection<ValueEditorExtension> getValueEditorProxys()
    {
        Collection<ValueEditorExtension> valueEditorProxies = new ArrayList<ValueEditorExtension>();

        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint extensionPoint = registry.getExtensionPoint( EXTENSION_POINT );
        IConfigurationElement[] members = extensionPoint.getConfigurationElements();

        // For each extension:
        for ( int m = 0; m < members.length; m++ )
        {
            ValueEditorExtension proxy = new ValueEditorExtension();
            valueEditorProxies.add( proxy );

            IConfigurationElement member = members[m];
            IExtension extension = member.getDeclaringExtension();
            String extendingPluginId = extension.getNamespaceIdentifier();

            proxy.member = member;
            proxy.name = member.getAttribute( "name" );
            String iconPath = member.getAttribute( "icon" );
            proxy.icon = AbstractUIPlugin.imageDescriptorFromPlugin( extendingPluginId, iconPath );
            if ( proxy.icon == null )
            {
                proxy.icon = ImageDescriptor.getMissingImageDescriptor();
            }
            proxy.className = member.getAttribute( "class" );

            IConfigurationElement[] children = member.getChildren();
            for ( int c = 0; c < children.length; c++ )
            {
                IConfigurationElement element = children[c];
                String type = element.getName();
                if ( "syntax".equals( type ) )
                {
                    String syntaxOID = element.getAttribute( "syntaxOID" );
                    proxy.syntaxOids.add( syntaxOID );
                }
                else if ( "attribute".equals( type ) )
                {
                    String attributeType = element.getAttribute( "attributeType" );
                    proxy.attributeTypes.add( attributeType );
                }
            }
        }

        return valueEditorProxies;
    }

    /**
     * This class is a bean to hold the data defined in value editor extension 
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    public static class ValueEditorExtension
    {
        
        /** The name. */
        public String name = null;
        
        /** The icon. */
        public ImageDescriptor icon = null;
        
        /** The class name. */
        public String className = null;
        
        /** The syntax oids. */
        public Collection<String> syntaxOids = new ArrayList<String>( 3 );
        
        /** The attribute types. */
        public Collection<String> attributeTypes = new ArrayList<String>( 3 );

        /** The configuration element. */
        private IConfigurationElement member = null;
    }

}
