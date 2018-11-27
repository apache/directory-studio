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
import java.util.List;
import java.util.Map;

import org.apache.directory.api.ldap.aci.ACIItemParser;
import org.apache.directory.api.ldap.aci.UserClass;
import org.apache.directory.api.ldap.aci.UserFirstACIItem;
import org.apache.directory.api.ldap.model.subtree.SubtreeSpecification;
import org.apache.directory.studio.valueeditors.AbstractDialogStringValueEditor;
import org.eclipse.osgi.util.NLS;


/**
 * The UserClassWrapper is used as input for the table viewer.
 * The user class values are always stored as raw string values.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class UserClassWrapper
{
    /** This map contains all possible user class identifiers */
    public static final Map<Class<? extends UserClass>, String> CLASS_TO_IDENTIFIER_MAP;
    
    static
    {
        Map<Class<? extends UserClass>, String> map = new HashMap<>();
        map.put( UserClass.AllUsers.class, "allUsers" ); //$NON-NLS-1$
        map.put( UserClass.ThisEntry.class, "thisEntry" ); //$NON-NLS-1$
        map.put( UserClass.ParentOfEntry.class, "parentOfEntry" ); //$NON-NLS-1$
        map.put( UserClass.Name.class, "name" ); //$NON-NLS-1$
        map.put( UserClass.UserGroup.class, "userGroup" ); //$NON-NLS-1$
        map.put( UserClass.Subtree.class, "subtree" ); //$NON-NLS-1$
        CLASS_TO_IDENTIFIER_MAP = Collections.unmodifiableMap( map );
    }

    /** This map contains all user class display values */
    public static final Map<Class<? extends UserClass>, String> CLASS_TO_DISPLAY_MAP;
    
    static
    {
        Map<Class<? extends UserClass>, String> map = new HashMap<>();
        map.put( UserClass.AllUsers.class, Messages.getString( "UserClassWrapper.userClass.allUsers.label" ) ); //$NON-NLS-1$
        map.put( UserClass.ThisEntry.class, Messages.getString( "UserClassWrapper.userClass.thisEntry.label" ) ); //$NON-NLS-1$
        map.put( UserClass.ParentOfEntry.class, Messages.getString( "UserClassWrapper.userClass.parentOfEntry.label" ) ); //$NON-NLS-1$
        map.put( UserClass.Name.class, Messages.getString( "UserClassWrapper.userClass.name.label" ) ); //$NON-NLS-1$
        map.put( UserClass.UserGroup.class, Messages.getString( "UserClassWrapper.userClass.userGroup.label" ) ); //$NON-NLS-1$
        map.put( UserClass.Subtree.class, Messages.getString( "UserClassWrapper.userClass.subtree.label" ) ); //$NON-NLS-1$
        CLASS_TO_DISPLAY_MAP = Collections.unmodifiableMap( map );
    }

    /** A dummy ACI to check syntax of the userClassValue. */
    private static final String DUMMY = "{ identificationTag \"id1\", precedence 1, authenticationLevel simple, " //$NON-NLS-1$
        + "itemOrUserFirst userFirst: { userClasses  { #identifier# #values# }, " //$NON-NLS-1$
        + "userPermissions { { protectedItems { entry }, grantsAndDenials { grantRead } } } } }"; //$NON-NLS-1$

    /** The class of the user class, never null. */
    private final Class<? extends UserClass> clazz;

    /** The user class values, may be empty. */
    private final List<String> values;

    /** The value prefix, prepended to the value. */
    private final String valuePrefix;

    /** The value suffix, appended to the value. */
    private final String valueSuffix;

    /** The value editor, null means no value. */
    private final AbstractDialogStringValueEditor valueEditor;


    /**
     * Creates a new instance of UserClassWrapper.
     * 
     * @param clazz the java class of the UserClass
     * @param valuePrefix the identifier
     * @param valueSuffix the dislpay name
     * @param valueEditor the value editor
     */
    public UserClassWrapper( Class<? extends UserClass> clazz, String valuePrefix, String valueSuffix,
        AbstractDialogStringValueEditor valueEditor )
    {
        this.clazz = clazz;
        this.valuePrefix = valuePrefix;
        this.valueSuffix = valueSuffix;
        this.valueEditor = valueEditor;

        this.values = new ArrayList<>();
    }


    /**
     * Creates a new user class object. Therefore it uses the 
     * dummy ACI, injects the user class and its value, parses
     * the ACI and extracts the user class from the parsed bean.
     * 
     * @return the parsed user class
     * 
     * @throws ParseException if parsing fails
     */
    public UserClass getUserClass() throws ParseException
    {
        String flatValue = getFlatValue();
        String spec = DUMMY;
        spec = spec.replaceAll( "#identifier#", getIdentifier() ); //$NON-NLS-1$
        spec = spec.replaceAll( "#values#", flatValue ); //$NON-NLS-1$
        ACIItemParser parser = new ACIItemParser( null );
        UserFirstACIItem aci = null;
        
        try
        {
            aci = ( UserFirstACIItem ) parser.parse( spec );
        }
        catch ( ParseException e )
        {
            String msg = NLS.bind(
                Messages.getString( "UserClassWrapper.error.message" ), new String[] { getIdentifier(), flatValue } ); //$NON-NLS-1$
            throw new ParseException( msg, 0 );
        }
        
        return aci.getUserClasses().iterator().next();
    }


    /**
     * Sets the user class.
     * 
     * @param userClass the user class
     */
    public void setUserClass( UserClass userClass )
    {
        assert userClass.getClass() == getClazz();

        // first clear values
        values.clear();

        // switch on userClass type
        // no value in UserClass.AllUsers and UserClass.ThisEntry
        if ( userClass.getClass() == UserClass.Name.class )
        {
            UserClass.Name name = ( UserClass.Name ) userClass;
            
            for ( String jndiName : name.getNames() )
            {
                values.add( jndiName );
            }
        }
        else if ( userClass.getClass() == UserClass.UserGroup.class )
        {
            UserClass.UserGroup userGroups = ( UserClass.UserGroup ) userClass;
            
            for ( String jndiName : userGroups.getNames() )
            {
                values.add( jndiName );
            }
        }
        else if ( userClass.getClass() == UserClass.Subtree.class )
        {
            UserClass.Subtree subtree = ( UserClass.Subtree ) userClass;

            for ( SubtreeSpecification subtreeSpecification : subtree.getSubtreeSpecifications() )
            {
                StringBuilder buffer = new StringBuilder();
                subtreeSpecification.toString( buffer );
                values.add( buffer.toString() );
            }
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
        StringBuilder sb = new StringBuilder();
        
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

        return sb.append( getDisplayName() ).append( ' ' ).append( flatValue ).toString(); //$NON-NLS-1$
    }


    /**
     * Returns the flat value.
     * 
     * @return the flat value
     */
    private String getFlatValue()
    {
        if ( ( valueEditor == null ) || values.isEmpty() )
        {
            return ""; //$NON-NLS-1$
        }

        StringBuilder buffer = new StringBuilder();
        buffer.append( "{ " ); //$NON-NLS-1$
        
        boolean isFirst = true;
        
        for ( String value : values )
        {
            if ( isFirst )
            {
                isFirst = false;
            }
            else
            {
                buffer.append( ", " ); //$NON-NLS-1$
            }
            
            buffer.append( valuePrefix );
            buffer.append( value );
            buffer.append( valueSuffix );
        }
        
        buffer.append( " }" ); //$NON-NLS-1$
        
        return buffer.toString();
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
        return CLASS_TO_DISPLAY_MAP.get( clazz );
    }


    /**
     * Gets the identifier.
     * 
     * @return the identifier
     */
    public String getIdentifier()
    {
        return CLASS_TO_IDENTIFIER_MAP.get( clazz );
    }


    /**
     * Returns the class of the user class.
     * 
     * @return the class of the user class.
     */
    public Class<? extends UserClass> getClazz()
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
}
