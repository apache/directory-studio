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

package org.apache.directory.studio.valueeditors;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.api.ldap.model.schema.LdapSyntax;
import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;
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
 * <code>org.apache.directory.studio.valueeditors</code>. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ValueEditorManager
{
    private static final String ATTRIBUTE_TYPE = "attributeType"; //$NON-NLS-1$

    private static final String ATTRIBUTE = "attribute"; //$NON-NLS-1$

    private static final String SYNTAX_OID = "syntaxOID"; //$NON-NLS-1$

    private static final String SYNTAX = "syntax"; //$NON-NLS-1$

    private static final String ICON = "icon"; //$NON-NLS-1$

    private static final String NAME = "name"; //$NON-NLS-1$

    private static final String CLASS = "class"; //$NON-NLS-1$

    /** The extension point ID for value editors */
    private static final String EXTENSION_POINT = BrowserCommonConstants.EXTENSION_POINT_VALUE_EDITORS;

    /** The composite used to create the value editors **/
    private Composite parent;

    /** 
     * The value editor explicitly selected by the user. If this
     * member is not null it is always returned as current value editor.
     */
    private IValueEditor userSelectedValueEditor;

    /** The special value editor for multi-valued attributes */
    private MultivaluedValueEditor multiValuedValueEditor;

    /** The special value editor to edit the entry in an wizard */
    private EntryValueEditor entryValueEditor;

    /** The special value editor to rename the entry */
    private RenameValueEditor renameValueEditor;

    /** The default string editor for single-line values */
    private IValueEditor defaultStringSingleLineValueEditor;

    /** The default string editor for multi-line values */
    private IValueEditor defaultStringMultiLineValueEditor;

    /** The default binary editor */
    private IValueEditor defaultBinaryValueEditor;

    /** A map containing all available value editors. */
    private Map<String, IValueEditor> class2ValueEditors;


    /**
     * Creates a new instance of ValueEditorManager.
     *
     * @param parent the composite used to create the value editors
     */
    public ValueEditorManager( Composite parent, boolean useEntryValueEditor, boolean useRenameValueEditor )
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
        multiValuedValueEditor.setValueEditorName( Messages.getString( "ValueEditorManager.MulitivaluedEditor" ) ); //$NON-NLS-1$
        multiValuedValueEditor.setValueEditorImageDescriptor( BrowserCommonActivator.getDefault().getImageDescriptor(
            BrowserCommonConstants.IMG_MULTIVALUEDEDITOR ) );

        // special case: entry editor
        if ( useEntryValueEditor )
        {
            entryValueEditor = new EntryValueEditor( this.parent, this );
            entryValueEditor.setValueEditorName( Messages.getString( "ValueEditorManager.EntryEditor" ) ); //$NON-NLS-1$
            entryValueEditor.setValueEditorImageDescriptor( BrowserCommonActivator.getDefault().getImageDescriptor(
                BrowserCommonConstants.IMG_ENTRY_EDITOR ) );
        }

        // special case: rename editor
        if ( useRenameValueEditor )
        {
            renameValueEditor = new RenameValueEditor( this.parent, this );
            renameValueEditor.setValueEditorName( Messages.getString( "ValueEditorManager.RenameEditor" ) ); //$NON-NLS-1$
            renameValueEditor.setValueEditorImageDescriptor( BrowserCommonActivator.getDefault().getImageDescriptor(
                BrowserCommonConstants.IMG_RENAME ) );
        }

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
        if ( parent != null )
        {
            userSelectedValueEditor = null;
            multiValuedValueEditor.dispose();
            
            if ( entryValueEditor != null )
            {
                entryValueEditor.dispose();
            }
            
            if ( renameValueEditor != null )
            {
                renameValueEditor.dispose();
            }
            
            defaultStringSingleLineValueEditor.dispose();
            defaultStringMultiLineValueEditor.dispose();
            defaultBinaryValueEditor.dispose();

            for ( IValueEditor ve : class2ValueEditors.values() )
            {
                ve.dispose();
            }

            parent = null;
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
     * Gets the value editor explicitly selected by the user.
     * 
     * @return the user selected value editor, null if non is set
     */
    public IValueEditor getUserSelectedValueEditor()
    {
        return userSelectedValueEditor;
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
        if ( userSelectedValueEditor != null )
        {
            return userSelectedValueEditor;
        }

        AttributeType atd = schema.getAttributeTypeDescription( attributeType );
        // check attribute preferences
        Map<String, String> attributeValueEditorMap = BrowserCommonActivator.getDefault().getValueEditorsPreferences()
            .getAttributeValueEditorMap();
        
        String oidStr = Strings.toLowerCase( atd.getOid() );
        
        if ( atd.getOid() != null && attributeValueEditorMap.containsKey( oidStr ) )
        {
            return ( IValueEditor ) class2ValueEditors.get( attributeValueEditorMap.get( oidStr ) );
        }
        List<String> names = atd.getNames();
        
        for ( String name : names )
        {
            String nameStr = Strings.toLowerCase( name );
            
            if ( attributeValueEditorMap.containsKey( nameStr ) )
            {
                return ( IValueEditor ) class2ValueEditors.get( attributeValueEditorMap.get( nameStr ) );
            }
        }

        // check syntax preferences
        String syntaxNumericOid = SchemaUtils.getSyntaxNumericOidTransitive( atd, schema );
        Map<String, String> syntaxValueEditorMap = BrowserCommonActivator.getDefault().getValueEditorsPreferences()
            .getSyntaxValueEditorMap();
        
        String syntaxtNumericOidStr = Strings.toLowerCase( syntaxNumericOid );
        
        if ( ( syntaxNumericOid != null ) && syntaxValueEditorMap.containsKey( syntaxtNumericOidStr ) )
        {
            return ( IValueEditor ) class2ValueEditors.get( syntaxValueEditorMap.get( syntaxtNumericOidStr ) );
        }

        // return default
        LdapSyntax lsd = schema.getLdapSyntaxDescription( syntaxNumericOid );
        
        if ( SchemaUtils.isBinary( lsd ) )
        {
            return defaultBinaryValueEditor;
        }
        else
        {
            return defaultStringSingleLineValueEditor;
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
        return getCurrentValueEditor( entry.getBrowserConnection().getSchema(), attributeType );
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
        IAttribute attribute = value.getAttribute();
        IValueEditor ve = getCurrentValueEditor( attribute.getEntry(), attribute.getDescription() );

        // special case objectClass: always return entry editor
        if ( userSelectedValueEditor == null ) 
        {
            if ( attribute.isObjectClassAttribute() && ( entryValueEditor != null ) )
            {
                return entryValueEditor;
            }

            // special case Rdn attribute: always return rename editor
            if ( value.isRdnPart() && ( renameValueEditor != null ) )
            {
                return renameValueEditor;
            }
        }

        // here the value is known, we can check for single-line or multi-line
        if ( ve == defaultStringSingleLineValueEditor )
        {
            String stringValue = value.getStringValue();
            
            if ( ( stringValue.indexOf( '\n' ) == -1 ) && ( stringValue.indexOf( '\r' ) == -1 ) )
            {
                ve = defaultStringSingleLineValueEditor;
            }
            else
            {
                ve = defaultStringMultiLineValueEditor;
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
        else if ( ( userSelectedValueEditor == null ) && attributeHierarchy.getAttribute().isObjectClassAttribute()
            && entryValueEditor != null )
        {
            // special case objectClass: always return entry editor
            return entryValueEditor;
        }
        else if ( ( userSelectedValueEditor == entryValueEditor ) && ( entryValueEditor != null ) )
        {
            // special case objectClass: always return entry editor
            return entryValueEditor;
        }
        else if ( ( attributeHierarchy.size() == 1 ) && ( attributeHierarchy.getAttribute().getValueSize() == 0 ) )
        {
            return getCurrentValueEditor( attributeHierarchy.getAttribute().getEntry(), attributeHierarchy
                .getAttribute().getDescription() );
        }
        else if ( ( attributeHierarchy.size() == 1 ) &&
                  ( attributeHierarchy.getAttribute().getValueSize() == 1 ) &&
                  attributeHierarchy.getAttributeDescription().equalsIgnoreCase(
                      attributeHierarchy.getAttribute().getValues()[0].getAttribute().getDescription() ) )
        {
            // special case Rdn: always return MV-editor
            if ( ( userSelectedValueEditor == null ) && attributeHierarchy.getAttribute().getValues()[0].isRdnPart() )
            {
                if ( renameValueEditor != null )
                {
                    return renameValueEditor;
                }
                else
                {
                    return multiValuedValueEditor;
                }
            }

            return getCurrentValueEditor( attributeHierarchy.getAttribute().getValues()[0] );
        }
        else
        {
            return multiValuedValueEditor;
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
        Schema schema = entry.getBrowserConnection().getSchema();
        
        return getAlternativeValueEditors( schema, attributeName );
    }


    /**
     * Returns alternative value editors for the given attribute. For now these
     * are the three default editors.
     * 
     * @param schema the schema
     * @param attributeName the attribute
     * @return the alternative value editors
     */
    public IValueEditor[] getAlternativeValueEditors( Schema schema, String attributeName )
    {
        List<IValueEditor> alternativeList = new ArrayList<IValueEditor>();

        AttributeType atd = schema.getAttributeTypeDescription( attributeName );

        if ( SchemaUtils.isBinary( atd, schema ) )
        {
            alternativeList.add( defaultBinaryValueEditor );
            alternativeList.add( defaultStringSingleLineValueEditor );
            alternativeList.add( defaultStringMultiLineValueEditor );
        }
        else if ( SchemaUtils.isString( atd, schema ) )
        {
            alternativeList.add( defaultStringSingleLineValueEditor );
            alternativeList.add( defaultStringMultiLineValueEditor );
            alternativeList.add( defaultBinaryValueEditor );
        }

        alternativeList.add( multiValuedValueEditor );

        alternativeList.remove( getCurrentValueEditor( schema, attributeName ) );

        return alternativeList.toArray( new IValueEditor[alternativeList.size()] );
    }


    /**
     * Returns alternative value editors for the given value. For now these
     * are the three default editors.
     *
     * @param value the value
     * @return the alternative value editors
     */
    public IValueEditor[] getAlternativeValueEditors( IValue value )
    {
        List<IValueEditor> alternativeList = new ArrayList<IValueEditor>();

        if ( value.isBinary() )
        {
            alternativeList.add( defaultBinaryValueEditor );
            alternativeList.add( defaultStringSingleLineValueEditor );
            alternativeList.add( defaultStringMultiLineValueEditor );
        }
        else if ( value.isString() )
        {
            alternativeList.add( defaultStringSingleLineValueEditor );
            alternativeList.add( defaultStringMultiLineValueEditor );
            alternativeList.add( defaultBinaryValueEditor );
        }

        alternativeList.add( multiValuedValueEditor );

        alternativeList.remove( getCurrentValueEditor( value ) );

        return alternativeList.toArray( new IValueEditor[alternativeList.size()] );
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

        // special case Rdn: no alternative to the rename editor, except the MV editor
        // perhaps this should be moved somewhere else
        if ( multiValuedValueEditor != null )
        {
            for ( IAttribute attribute : ah )
            {
                for ( IValue value : attribute.getValues() )
                {
                    if ( value.isRdnPart() )
                    {
                        return new IValueEditor[]
                            { multiValuedValueEditor };
                    }
                }
            }
        }

        // special case objectClass: no alternative to the entry editor
        // perhaps this should be moved somewhere else
        for ( IAttribute attribute : ah )
        {
            if ( attribute.isObjectClassAttribute() )
            {
                return new IValueEditor[0];
            }
        }

        if ( ( ah.size() == 1 ) && ( ah.getAttribute().getValueSize() == 0 ) )
        {
            return getAlternativeValueEditors( ah.getAttribute().getEntry(), ah.getAttribute().getDescription() );
        }
        else if ( ( ah.size() == 1 ) &&
                  ( ah.getAttribute().getValueSize() == 1 ) &&
                  ah.getAttributeDescription().equalsIgnoreCase(
                      ah.getAttribute().getValues()[0].getAttribute().getDescription() ) )
        {
            return getAlternativeValueEditors( ah.getAttribute().getValues()[0] );
        }
        else
        {
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

        list.add( defaultStringSingleLineValueEditor );
        list.add( defaultStringMultiLineValueEditor );
        list.add( defaultBinaryValueEditor );

        list.addAll( class2ValueEditors.values() );

        list.add( multiValuedValueEditor );
        
        if ( entryValueEditor != null )
        {
            list.add( entryValueEditor );
        }
        
        if ( renameValueEditor != null )
        {
            list.add( renameValueEditor );
        }

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
     * Returns the entry value editor.
     *
     * @return the entry value editor
     */
    public EntryValueEditor getEntryValueEditor()
    {
        return entryValueEditor;
    }


    /**
     * Returns the rename value editor.
     *
     * @return the rename value editor
     */
    public RenameValueEditor getRenameValueEditor()
    {
        return renameValueEditor;
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

        Collection<ValueEditorExtension> valueEditorExtensions = getValueEditorExtensions();
        
        for ( ValueEditorExtension vee : valueEditorExtensions )
        {
            try
            {
                IValueEditor valueEditor = ( IValueEditor ) vee.member.createExecutableExtension( CLASS );
                valueEditor.create( parent );
                valueEditor.setValueEditorName( vee.name );
                valueEditor.setValueEditorImageDescriptor( vee.icon );
                valueEditors.add( valueEditor );
            }
            catch ( Exception e )
            {
                BrowserCommonActivator.getDefault().getLog().log(
                    new Status( IStatus.ERROR, BrowserCommonConstants.PLUGIN_ID, 1, Messages
                        .getString( "ValueEditorManager.UnableToCreateValueEditor" ) //$NON-NLS-1$
                        + vee.className, e ) );
            }
        }

        return valueEditors;
    }


    /**
     * Returns all value editor extensions specified by value editor extensions.
     *
     * @return the value editor extensions
     */
    public static Collection<ValueEditorExtension> getValueEditorExtensions()
    {
        Collection<ValueEditorExtension> valueEditorExtensions = new ArrayList<ValueEditorExtension>();

        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint extensionPoint = registry.getExtensionPoint( EXTENSION_POINT );
        IConfigurationElement[] members = extensionPoint.getConfigurationElements();

        // For each extension:
        for ( IConfigurationElement member : members )
        {
            ValueEditorExtension proxy = new ValueEditorExtension();
            valueEditorExtensions.add( proxy );

            IExtension extension = member.getDeclaringExtension();
            String extendingPluginId = extension.getNamespaceIdentifier();

            proxy.member = member;
            proxy.name = member.getAttribute( NAME );
            String iconPath = member.getAttribute( ICON );
            proxy.icon = AbstractUIPlugin.imageDescriptorFromPlugin( extendingPluginId, iconPath );
            
            if ( proxy.icon == null )
            {
                proxy.icon = ImageDescriptor.getMissingImageDescriptor();
            }
            
            proxy.className = member.getAttribute( CLASS );

            IConfigurationElement[] children = member.getChildren();
            
            for ( IConfigurationElement child : children )
            {
                String type = child.getName();
                
                if ( SYNTAX.equals( type ) )
                {
                    String syntaxOID = child.getAttribute( SYNTAX_OID );
                    proxy.syntaxOids.add( syntaxOID );
                }
                else if ( ATTRIBUTE.equals( type ) )
                {
                    String attributeType = child.getAttribute( ATTRIBUTE_TYPE );
                    proxy.attributeTypes.add( attributeType );
                }
            }
        }

        return valueEditorExtensions;
    }

    /**
     * This class is a bean to hold the data defined in value editor extension 
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
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
        
        
        /**
         * @see Object#toString()
         */
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            
            sb.append( '<' );
            
            sb.append( name ).append( ", " );
            sb.append(  className );
            
            if ( ( attributeTypes != null ) && ( attributeTypes.size() > 0 ) )
            {
                sb.append( ", {" );
                boolean isFirst = true;
                
                for ( String attributeType : attributeTypes )
                {
                    if ( isFirst )
                    {
                        isFirst = false;
                    }
                    else 
                    {
                        sb.append( ", " );
                    }
                    
                    sb.append( attributeType );
                }
                
                sb.append( '}' );
            }
            
            
            if ( ( syntaxOids != null ) && ( syntaxOids.size() > 0 ) )
            {
                sb.append( ", {" );
                boolean isFirst = true;
                
                for ( String syntaxOid : syntaxOids )
                {
                    if ( isFirst )
                    {
                        isFirst = false;
                    }
                    else 
                    {
                        sb.append( ", " );
                    }
                    
                    sb.append( syntaxOid );
                }
                
                sb.append( '}' );
            }

            sb.append( '>' );
            
            return sb.toString();
        }
    }
}
