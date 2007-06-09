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
package org.apache.directory.ldapstudio.aciitemeditor.model;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;

import org.apache.directory.shared.ldap.aci.ACIItemParser;
import org.apache.directory.shared.ldap.aci.ItemFirstACIItem;
import org.apache.directory.shared.ldap.aci.ProtectedItem;
import org.apache.directory.studio.valueeditors.AbstractDialogStringValueEditor;
import org.eclipse.osgi.util.NLS;


/**
 * The ProtectedItemWrapper is used as input for the table viewer. 
 * The protected item values are always stored as raw string value.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ProtectedItemWrapper
{
    /** This map contains all possible protected item identifiers */
    public static final Map<Class, String> classToIdentifierMap;
    static
    {
        Map<Class, String> map = new HashMap<Class, String>();
        map.put( ProtectedItem.Entry.class, "entry" ); //$NON-NLS-1$
        map.put( ProtectedItem.AllUserAttributeTypes.class, "allUserAttributeTypes" ); //$NON-NLS-1$
        map.put( ProtectedItem.AttributeType.class, "attributeType" ); //$NON-NLS-1$
        map.put( ProtectedItem.AllAttributeValues.class, "allAttributeValues" ); //$NON-NLS-1$
        map.put( ProtectedItem.AllUserAttributeTypesAndValues.class, "allUserAttributeTypesAndValues" ); //$NON-NLS-1$
        map.put( ProtectedItem.AttributeValue.class, "attributeValue" ); //$NON-NLS-1$
        map.put( ProtectedItem.SelfValue.class, "selfValue" ); //$NON-NLS-1$
        map.put( ProtectedItem.RangeOfValues.class, "rangeOfValues" ); //$NON-NLS-1$
        map.put( ProtectedItem.MaxValueCount.class, "maxValueCount" ); //$NON-NLS-1$
        map.put( ProtectedItem.MaxImmSub.class, "maxImmSub" ); //$NON-NLS-1$
        map.put( ProtectedItem.RestrictedBy.class, "restrictedBy" ); //$NON-NLS-1$
        map.put( ProtectedItem.Classes.class, "classes" ); //$NON-NLS-1$
        classToIdentifierMap = Collections.unmodifiableMap( map );
    }

    /** This map contains all protected item display values */
    public static final Map<Class, String> classToDisplayMap;
    static
    {
        Map<Class, String> map = new HashMap<Class, String>();
        map.put( ProtectedItem.Entry.class, Messages.getString( "ProtectedItemWrapper.protectedItem.entry.label" ) ); //$NON-NLS-1$
        map.put( ProtectedItem.AllUserAttributeTypes.class, Messages
            .getString( "ProtectedItemWrapper.protectedItem.allUserAttributeTypes.label" ) ); //$NON-NLS-1$
        map.put( ProtectedItem.AttributeType.class, Messages
            .getString( "ProtectedItemWrapper.protectedItem.attributeType.label" ) ); //$NON-NLS-1$
        map.put( ProtectedItem.AllAttributeValues.class, Messages
            .getString( "ProtectedItemWrapper.protectedItem.allAttributeValues.label" ) ); //$NON-NLS-1$
        map.put( ProtectedItem.AllUserAttributeTypesAndValues.class, Messages
            .getString( "ProtectedItemWrapper.protectedItem.allUserAttributeTypesAndValues.label" ) ); //$NON-NLS-1$
        map.put( ProtectedItem.AttributeValue.class, Messages
            .getString( "ProtectedItemWrapper.protectedItem.attributeValue.label" ) ); //$NON-NLS-1$
        map.put( ProtectedItem.SelfValue.class, Messages
            .getString( "ProtectedItemWrapper.protectedItem.selfValue.label" ) ); //$NON-NLS-1$
        map.put( ProtectedItem.RangeOfValues.class, Messages
            .getString( "ProtectedItemWrapper.protectedItem.rangeOfValues.label" ) ); //$NON-NLS-1$
        map.put( ProtectedItem.MaxValueCount.class, Messages
            .getString( "ProtectedItemWrapper.protectedItem.maxValueCount.label" ) ); //$NON-NLS-1$
        map.put( ProtectedItem.MaxImmSub.class, Messages
            .getString( "ProtectedItemWrapper.protectedItem.maxImmSub.label" ) ); //$NON-NLS-1$
        map.put( ProtectedItem.RestrictedBy.class, Messages
            .getString( "ProtectedItemWrapper.protectedItem.restrictedBy.label" ) ); //$NON-NLS-1$
        map.put( ProtectedItem.Classes.class, Messages.getString( "ProtectedItemWrapper.protectedItem.classes.label" ) ); //$NON-NLS-1$
        classToDisplayMap = Collections.unmodifiableMap( map );
    }

    /** A dummy ACI to check syntax of the protectedItemValue */
    private static final String DUMMY = "{ identificationTag \"id1\", precedence 1, authenticationLevel simple, " //$NON-NLS-1$
        + "itemOrUserFirst itemFirst: { protectedItems  { #identifier# #values# }, " //$NON-NLS-1$
        + "itemPermissions { { userClasses { allUsers }, grantsAndDenials { grantRead } } } } }"; //$NON-NLS-1$ 

    /** The class of the protected item, never null. */
    private final Class clazz;

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
    public ProtectedItemWrapper( Class clazz, boolean isMultivalued, String valuePrefix, String valueSuffix,
        AbstractDialogStringValueEditor valueEditor )
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
        ProtectedItem item = ( ProtectedItem ) aci.getProtectedItems().iterator().next();
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
        if ( item.getClass() == ProtectedItem.AttributeType.class )
        {
            ProtectedItem.AttributeType at = ( ProtectedItem.AttributeType ) item;
            for ( Iterator it = at.iterator(); it.hasNext(); )
            {
                values.add( it.next().toString() );
            }
        }
        else if ( item.getClass() == ProtectedItem.AllAttributeValues.class )
        {
            ProtectedItem.AllAttributeValues aav = ( ProtectedItem.AllAttributeValues ) item;
            for ( Iterator it = aav.iterator(); it.hasNext(); )
            {
                values.add( it.next().toString() );
            }
        }
        else if ( item.getClass() == ProtectedItem.AttributeValue.class )
        {
            ProtectedItem.AttributeValue av = ( ProtectedItem.AttributeValue ) item;
            for ( Iterator it = av.iterator(); it.hasNext(); )
            {
                Attribute attribute = ( Attribute ) it.next();
                try
                {
                    values.add( attribute.getID() + "=" + attribute.get() ); //$NON-NLS-1$
                }
                catch ( NamingException e )
                {
                }
            }
        }
        else if ( item.getClass() == ProtectedItem.SelfValue.class )
        {
            ProtectedItem.SelfValue sv = ( ProtectedItem.SelfValue ) item;
            for ( Iterator it = sv.iterator(); it.hasNext(); )
            {
                values.add( it.next().toString() );
            }
        }
        else if ( item.getClass() == ProtectedItem.RangeOfValues.class )
        {
            ProtectedItem.RangeOfValues rov = ( ProtectedItem.RangeOfValues ) item;
            StringBuffer buffer = new StringBuffer();
            rov.getFilter().printToBuffer( buffer );
            values.add( buffer.toString() );
        }
        else if ( item.getClass() == ProtectedItem.MaxValueCount.class )
        {
            ProtectedItem.MaxValueCount mvc = ( ProtectedItem.MaxValueCount ) item;
            for ( Iterator it = mvc.iterator(); it.hasNext(); )
            {
                ProtectedItem.MaxValueCountItem mvci = ( ProtectedItem.MaxValueCountItem ) it.next();
                StringBuffer buffer = new StringBuffer();
                mvci.printToBuffer( buffer );
                values.add( buffer.toString() );
            }
        }
        else if ( item.getClass() == ProtectedItem.MaxImmSub.class )
        {
            ProtectedItem.MaxImmSub mis = ( ProtectedItem.MaxImmSub ) item;
            values.add( Integer.toString( mis.getValue() ) );
        }
        else if ( item.getClass() == ProtectedItem.RestrictedBy.class )
        {
            ProtectedItem.RestrictedBy rb = ( ProtectedItem.RestrictedBy ) item;
            for ( Iterator it = rb.iterator(); it.hasNext(); )
            {
                ProtectedItem.RestrictedByItem rbi = ( ProtectedItem.RestrictedByItem ) it.next();
                StringBuffer buffer = new StringBuffer();
                rbi.printToBuffer( buffer );
                values.add( buffer.toString() );
            }
        }
        else if ( item.getClass() == ProtectedItem.Classes.class )
        {
            ProtectedItem.Classes classes = ( ProtectedItem.Classes ) item;
            StringBuffer buffer = new StringBuffer();
            classes.getClasses().printRefinementToBuffer( buffer );
            values.add( buffer.toString() );
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
     * @return the modifyable list of values.
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
    public Class getClazz()
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
