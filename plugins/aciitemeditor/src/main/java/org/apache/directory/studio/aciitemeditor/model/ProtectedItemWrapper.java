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
package org.apache.directory.studio.aciitemeditor.model;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.directory.shared.ldap.aci.ACIItemParser;
import org.apache.directory.shared.ldap.aci.ItemFirstACIItem;
import org.apache.directory.shared.ldap.aci.ProtectedItem;
import org.apache.directory.shared.ldap.aci.protectedItem.AllAttributeValuesItem;
import org.apache.directory.shared.ldap.aci.protectedItem.AllUserAttributeTypesAndValuesItem;
import org.apache.directory.shared.ldap.aci.protectedItem.AllUserAttributeTypesItem;
import org.apache.directory.shared.ldap.aci.protectedItem.AttributeTypeItem;
import org.apache.directory.shared.ldap.aci.protectedItem.AttributeValueItem;
import org.apache.directory.shared.ldap.aci.protectedItem.ClassesItem;
import org.apache.directory.shared.ldap.aci.protectedItem.EntryItem;
import org.apache.directory.shared.ldap.aci.protectedItem.MaxImmSubItem;
import org.apache.directory.shared.ldap.aci.protectedItem.MaxValueCountElem;
import org.apache.directory.shared.ldap.aci.protectedItem.MaxValueCountItem;
import org.apache.directory.shared.ldap.aci.protectedItem.RangeOfValuesItem;
import org.apache.directory.shared.ldap.aci.protectedItem.RestrictedByElem;
import org.apache.directory.shared.ldap.aci.protectedItem.RestrictedByItem;
import org.apache.directory.shared.ldap.aci.protectedItem.SelfValueItem;
import org.apache.directory.shared.ldap.entry.EntryAttribute;
import org.apache.directory.shared.ldap.schema.AttributeType;
import org.apache.directory.studio.valueeditors.AbstractDialogStringValueEditor;
import org.eclipse.osgi.util.NLS;


/**
 * The ProtectedItemWrapper is used as input for the table viewer. 
 * The protected item values are always stored as raw string value.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ProtectedItemWrapper
{
    /** This map contains all possible protected item identifiers */
    public static final Map<Class<? extends ProtectedItem>, String> classToIdentifierMap;
    static
    {
        Map<Class<? extends ProtectedItem>, String> map = new HashMap<Class<? extends ProtectedItem>, String>();
        map.put( EntryItem.class, "entry" ); //$NON-NLS-1$
        map.put( AllUserAttributeTypesItem.class, "allUserAttributeTypes" ); //$NON-NLS-1$
        map.put( AttributeTypeItem.class, "attributeType" ); //$NON-NLS-1$
        map.put( AllAttributeValuesItem.class, "allAttributeValues" ); //$NON-NLS-1$
        map.put( AllUserAttributeTypesAndValuesItem.class, "allUserAttributeTypesAndValues" ); //$NON-NLS-1$
        map.put( AttributeValueItem.class, "attributeValue" ); //$NON-NLS-1$
        map.put( SelfValueItem.class, "selfValue" ); //$NON-NLS-1$
        map.put( RangeOfValuesItem.class, "rangeOfValues" ); //$NON-NLS-1$
        map.put( MaxValueCountItem.class, "maxValueCount" ); //$NON-NLS-1$
        map.put( MaxImmSubItem.class, "maxImmSub" ); //$NON-NLS-1$
        map.put( RestrictedByItem.class, "restrictedBy" ); //$NON-NLS-1$
        map.put( ClassesItem.class, "classes" ); //$NON-NLS-1$
        classToIdentifierMap = Collections.unmodifiableMap( map );
    }

    /** This map contains all protected item display values */
    public static final Map<Class<? extends ProtectedItem>, String> classToDisplayMap;
    static
    {
        Map<Class<? extends ProtectedItem>, String> map = new HashMap<Class<? extends ProtectedItem>, String>();
        map.put( EntryItem.class, Messages.getString( "ProtectedItemWrapper.protectedItem.entry.label" ) ); //$NON-NLS-1$
        map.put( AllUserAttributeTypesItem.class, Messages
            .getString( "ProtectedItemWrapper.protectedItem.allUserAttributeTypes.label" ) ); //$NON-NLS-1$
        map.put( AttributeTypeItem.class, Messages
            .getString( "ProtectedItemWrapper.protectedItem.attributeType.label" ) ); //$NON-NLS-1$
        map.put( AllAttributeValuesItem.class, Messages
            .getString( "ProtectedItemWrapper.protectedItem.allAttributeValues.label" ) ); //$NON-NLS-1$
        map.put( AllUserAttributeTypesAndValuesItem.class, Messages
            .getString( "ProtectedItemWrapper.protectedItem.allUserAttributeTypesAndValues.label" ) ); //$NON-NLS-1$
        map.put( AttributeValueItem.class, Messages
            .getString( "ProtectedItemWrapper.protectedItem.attributeValue.label" ) ); //$NON-NLS-1$
        map.put( SelfValueItem.class, Messages
            .getString( "ProtectedItemWrapper.protectedItem.selfValue.label" ) ); //$NON-NLS-1$
        map.put( RangeOfValuesItem.class, Messages
            .getString( "ProtectedItemWrapper.protectedItem.rangeOfValues.label" ) ); //$NON-NLS-1$
        map.put( MaxValueCountItem.class, Messages
            .getString( "ProtectedItemWrapper.protectedItem.maxValueCount.label" ) ); //$NON-NLS-1$
        map.put( MaxImmSubItem.class, Messages
            .getString( "ProtectedItemWrapper.protectedItem.maxImmSub.label" ) ); //$NON-NLS-1$
        map.put( RestrictedByItem.class, Messages
            .getString( "ProtectedItemWrapper.protectedItem.restrictedBy.label" ) ); //$NON-NLS-1$
        map.put( ClassesItem.class, Messages.getString( "ProtectedItemWrapper.protectedItem.classes.label" ) ); //$NON-NLS-1$
        classToDisplayMap = Collections.unmodifiableMap( map );
    }

    /** A dummy ACI to check syntax of the protectedItemValue */
    private static final String DUMMY = "{ identificationTag \"id1\", precedence 1, authenticationLevel simple, " //$NON-NLS-1$
        + "itemOrUserFirst itemFirst: { protectedItems  { #identifier# #values# }, " //$NON-NLS-1$
        + "itemPermissions { { userClasses { allUsers }, grantsAndDenials { grantRead } } } } }"; //$NON-NLS-1$ 

    /** The class of the protected item, never null. */
    private final Class<? extends ProtectedItem> clazz;

    /** The protected item values, may be empty. */
    private List<String> values;

    /** The value prefix, prepended to the value. */
    private String valuePrefix;

    /** The value suffix, appended to the value. */
    private String valueSuffix;

    /** The value editor, null means no value. */
    private AbstractDialogStringValueEditor valueEditor;

    /** The multivalued. */
    private boolean isMultivalued;


    /**
     * Creates a new instance of ProtectedItemWrapper.
     * 
     * @param clazz the java class of the UserClass
     * @param isMultivalued the is multivalued
     * @param valuePrefix the identifier
     * @param valueSuffix the dislpay name
     * @param valueEditor the value editor
     */
    public ProtectedItemWrapper( Class<? extends ProtectedItem> clazz, boolean isMultivalued, String valuePrefix,
        String valueSuffix, AbstractDialogStringValueEditor valueEditor )
    {
        this.clazz = clazz;
        this.isMultivalued = isMultivalued;
        this.valuePrefix = valuePrefix;
        this.valueSuffix = valueSuffix;
        this.valueEditor = valueEditor;

        this.values = new ArrayList<String>();
    }


    /**
     * Creates a new protected item object. Therefore it uses the 
     * dummy ACI, injects the protected item and its value, parses
     * the ACI and extracts the protected item from the parsed bean.
     * 
     * @return the parsed protected item
     * 
     * @throws ParseException if parsing fails
     */
    public ProtectedItem getProtectedItem() throws ParseException
    {
        String flatValue = getFlatValue();
        String spec = DUMMY;
        spec = spec.replaceAll( "#identifier#", getIdentifier() ); //$NON-NLS-1$
        spec = spec.replaceAll( "#values#", flatValue ); //$NON-NLS-1$
        ACIItemParser parser = new ACIItemParser( null );
        ItemFirstACIItem aci = null;
        try
        {
            aci = ( ItemFirstACIItem ) parser.parse( spec );
        }
        catch ( ParseException e )
        {

            String msg = NLS
                .bind(
                    Messages.getString( "ProtectedItemWrapper.error.message" ), new String[] { getIdentifier(), flatValue } ); //$NON-NLS-1$
            throw new ParseException( msg, 0 );
        }
        ProtectedItem item = aci.getProtectedItems().iterator().next();
        return item;
    }


    /**
     * Sets the protected item.
     * 
     * @param item the protected item
     */
    public void setProtectedItem( ProtectedItem item )
    {
        assert item.getClass() == getClazz();

        // first clear values
        values.clear();

        // switch on userClass type
        // no value in ProtectedItem.Entry, ProtectedItem.AllUserAttributeTypes and ProtectedItem.AllUserAttributeTypesAndValues
        if ( item.getClass() == AttributeTypeItem.class )
        {
            AttributeTypeItem at = ( AttributeTypeItem ) item;
            for ( Iterator<AttributeType> it = at.iterator(); it.hasNext(); )
            {
                AttributeType attributeType = it.next();
                values.add( attributeType.toString() );
            }
        }
        else if ( item.getClass() == AllAttributeValuesItem.class )
        {
            AllAttributeValuesItem aav = ( AllAttributeValuesItem ) item;
            for ( Iterator<AttributeType> it = aav.iterator(); it.hasNext(); )
            {
                AttributeType attributeType = it.next();
                values.add( attributeType.toString() );
            }
        }
        else if ( item.getClass() == AttributeValueItem.class )
        {
            AttributeValueItem av = ( AttributeValueItem ) item;
            for ( Iterator<EntryAttribute> it = av.iterator(); it.hasNext(); )
            {
                EntryAttribute entryAttribute = it.next();
                values.add( entryAttribute.getId() + "=" + entryAttribute.get() ); //$NON-NLS-1$
            }
        }
        else if ( item.getClass() == SelfValueItem.class )
        {
            SelfValueItem sv = ( SelfValueItem ) item;
            for ( Iterator<AttributeType> it = sv.iterator(); it.hasNext(); )
            {
                AttributeType attributeType = it.next();
                values.add( attributeType.toString() );
            }
        }
        else if ( item.getClass() == RangeOfValuesItem.class )
        {
            RangeOfValuesItem rov = ( RangeOfValuesItem ) item;
            values.add( rov.getRefinement().toString() );
        }
        else if ( item.getClass() == MaxValueCountItem.class )
        {
            MaxValueCountItem mvc = ( MaxValueCountItem ) item;
            for ( Iterator<MaxValueCountElem> it = mvc.iterator(); it.hasNext(); )
            {
                MaxValueCountElem mvci = it.next();
                values.add( mvci.toString() );
            }
        }
        else if ( item.getClass() == MaxImmSubItem.class )
        {
            MaxImmSubItem mis = ( MaxImmSubItem ) item;
            values.add( Integer.toString( mis.getValue() ) );
        }
        else if ( item.getClass() == RestrictedByItem.class )
        {
            RestrictedByItem rb = ( RestrictedByItem ) item;
            for ( Iterator<RestrictedByElem> it = rb.iterator(); it.hasNext(); )
            {
                RestrictedByElem rbe = it.next();
                values.add( rbe.toString() );
            }
        }
        else if ( item.getClass() == ClassesItem.class )
        {
            ClassesItem classes = ( ClassesItem ) item;
            StringBuilder sb = new StringBuilder();
            classes.getClasses().printRefinementToBuffer( sb );
            values.add( sb.toString() );
        }

    }


    /**
     * Returns a user-friedly string, displayed in the table.
     * 
     * @return the string
     */
    public String toString()
    {
        String flatValue = getFlatValue();
        if ( flatValue.length() > 0 )
        {
            flatValue = flatValue.replace( '\r', ' ' );
            flatValue = flatValue.replace( '\n', ' ' );
            flatValue = ": " + flatValue; //$NON-NLS-1$
            if ( flatValue.length() > 40 )
            {
                String temp = flatValue;
                flatValue = temp.substring( 0, 20 );
                flatValue = flatValue + "..."; //$NON-NLS-1$
                flatValue = flatValue + temp.substring( temp.length() - 20, temp.length() );
            }
        }

        return getDisplayName() + " " + flatValue; //$NON-NLS-1$
    }


    /**
     * Returns the flat value.
     * 
     * @return the flat value
     */
    private String getFlatValue()
    {
        if ( valueEditor == null || values.isEmpty() )
        {
            return ""; //$NON-NLS-1$
        }

        StringBuffer sb = new StringBuffer();
        if ( isMultivalued() )
        {
            sb.append( "{ " ); //$NON-NLS-1$
        }
        for ( Iterator<String> it = values.iterator(); it.hasNext(); )
        {
            sb.append( valuePrefix );
            String value = it.next();
            sb.append( value );
            sb.append( valueSuffix );
            if ( it.hasNext() )
            {
                sb.append( ", " ); //$NON-NLS-1$
            }
        }
        if ( isMultivalued() )
        {
            sb.append( " }" ); //$NON-NLS-1$
        }
        return sb.toString();
    }


    /**
     * Returns the list of values, may be modified.
     * 
     * @return the modifiable list of values.
     */
    public List<String> getValues()
    {
        return values;
    }


    /**
     * Gets the display name.
     * 
     * @return the display name
     */
    public String getDisplayName()
    {
        return classToDisplayMap.get( clazz );
    }


    /**
     * Gets the identifier.
     * 
     * @return the identifier
     */
    public String getIdentifier()
    {
        return classToIdentifierMap.get( clazz );
    }


    /**
     * Returns the class of the user class.
     * 
     * @return the class of the user class.
     */
    public Class<? extends ProtectedItem> getClazz()
    {
        return clazz;
    }


    /**
     * Checks if is editable.
     * 
     * @return true, if is editable
     */
    public boolean isEditable()
    {
        return valueEditor != null;
    }


    /**
     * Gets the value editor.
     * 
     * @return the value editor, may be null.
     */
    public AbstractDialogStringValueEditor getValueEditor()
    {
        return valueEditor;
    }


    /**
     * Checks if is multivalued.
     * 
     * @return true, if is multivalued
     */
    public boolean isMultivalued()
    {
        return isMultivalued;
    }
}
