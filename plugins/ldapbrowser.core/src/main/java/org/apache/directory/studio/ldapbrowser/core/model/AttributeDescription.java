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

package org.apache.directory.studio.ldapbrowser.core.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.shared.ldap.constants.SchemaConstants;
import org.apache.directory.shared.ldap.schema.AttributeType;
import org.apache.directory.shared.ldap.schema.ObjectClass;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;


/**
 * This class implements an attribute description as 
 * specified in RFC4512, section 2.5:
 * 
 *    An attribute description is represented by the ABNF:
 *
 *      attributedescription = attributetype options
 *      attributetype = oid
 *      options = *( SEMI option )
 *      option = 1*keychar
 *
 *   where &lt;attributetype> identifies the attribute type and each <option>
 *   identifies an attribute option.  Both &lt;attributetype> and <option>
 *   productions are case insensitive.  The order in which <option>s
 *   appear is irrelevant.  That is, any two &lt;attributedescription>s that
 *   consist of the same &lt;attributetype> and same set of <option>s are
 *   equivalent.
 *
 *   Examples of valid attribute descriptions:
 *
 *      2.5.4.0
 *      cn;lang-de;lang-en
 *      owner
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AttributeDescription implements Serializable
{

    private static final long serialVersionUID = 1L;

    /** The user provided description. */
    private String description;

    /** The parsed attribute type. */
    private String parsedAttributeType;

    /** The parsed language tag option list. */
    private List<String> parsedLangList;

    /** The parsed option list, except the language tags. */
    private List<String> parsedOptionList;


    /**
     * Creates a new instance of AttributeDescription.
     * 
     * @param description the user provided description
     */
    public AttributeDescription( String description )
    {
        this.description = description;

        String[] attributeDescriptionComponents = description.split( IAttribute.OPTION_DELIMITER );
        this.parsedAttributeType = attributeDescriptionComponents[0];
        this.parsedLangList = new ArrayList<String>();
        this.parsedOptionList = new ArrayList<String>();
        for ( int i = 1; i < attributeDescriptionComponents.length; i++ )
        {
            String component = attributeDescriptionComponents[i];
            if ( component.startsWith( IAttribute.OPTION_LANG_PREFIX ) )
            {
                this.parsedLangList.add( component );
            }
            else
            {
                this.parsedOptionList.add( component );
            }
        }
    }


    /**
     * Gets the user provided description.
     * 
     * @return the user provided description
     */
    public String getDescription()
    {
        return description;
    }


    /**
     * Gets the parsed attribute type.
     * 
     * @return the parsed attribute type
     */
    public String getParsedAttributeType()
    {
        return parsedAttributeType;
    }


    /**
     * Gets the list of parsed language tags.
     * 
     * @return the list of parsed language tags
     */
    public List<String> getParsedLangList()
    {
        return parsedLangList;
    }


    /**
     * Gets the list of parsed options, except the language tags.
     * 
     * @return the list of parsed options, except the language tags
     */
    public List<String> getParsedOptionList()
    {
        return parsedOptionList;
    }


    /**
     * Returns the attribute description with the numeric OID
     * instead of the descriptive attribute type.
     * 
     * @param schema the schema
     * 
     * @return the attribute description with the numeric OID
     */
    public String toOidString( Schema schema )
    {
        if ( schema == null )
        {
            return description;
        }

        AttributeType atd = schema.getAttributeTypeDescription( parsedAttributeType );
        String oidString = atd.getOid();

        if ( !parsedLangList.isEmpty() )
        {
            for ( Iterator<String> it = parsedLangList.iterator(); it.hasNext(); )
            {
                String element = it.next();
                oidString += element;

                if ( it.hasNext() || !parsedOptionList.isEmpty() )
                {
                    oidString += IAttribute.OPTION_DELIMITER;
                }
            }
        }
        if ( !parsedOptionList.isEmpty() )
        {
            for ( Iterator<String> it = parsedOptionList.iterator(); it.hasNext(); )
            {
                String element = it.next();
                oidString += element;

                if ( it.hasNext() )
                {
                    oidString += IAttribute.OPTION_DELIMITER;
                }
            }
        }

        return oidString;
    }


    /**
     * Checks if the given attribute description is subtype of 
     * this attribute description.
     * 
     * @param other the other attribute description
     * @param schema the schema
     * 
     * @return true, if the other attribute description is a 
     *         subtype of this attribute description.
     */
    public boolean isSubtypeOf( AttributeDescription other, Schema schema )
    {
        // this=name, other=givenName;lang-de -> false
        // this=name;lang-en, other=givenName;lang-de -> false
        // this=givenName, other=name -> true
        // this=givenName;lang-de, other=givenName -> true
        // this=givenName;lang-de, other=name -> true
        // this=givenName;lang-en, other=name;lang-de -> false
        // this=givenName, other=givenName;lang-de -> false

        // check equal descriptions
        if ( this.toOidString( schema ).equals( other.toOidString( schema ) ) )
        {
            return false;
        }

        AttributeType myAtd = schema.getAttributeTypeDescription( this.getParsedAttributeType() );
        AttributeType otherAtd = schema.getAttributeTypeDescription( other.getParsedAttributeType() );

        // special case *: all user attributes (RFC4511)
        if ( SchemaConstants.ALL_USER_ATTRIBUTES.equals( other.description ) && !SchemaUtils.isOperational( myAtd ) )
        {
            return true;
        }

        // special case +: all operational attributes (RFC3673)
        if ( SchemaConstants.ALL_OPERATIONAL_ATTRIBUTES.equals( other.description )
            && SchemaUtils.isOperational( myAtd ) )
        {
            return true;
        }

        // special case @: attributes by object class (RFC4529)
        if ( other.description.length() > 1 && other.description.startsWith( "@" ) )
        {
            String objectClass = other.description.substring( 1 );
            ObjectClass ocd = schema.getObjectClassDescription( objectClass );
            ocd.getMayAttributeTypes();
            ocd.getMustAttributeTypes();

            Collection<String> names = new HashSet<String>();
            names.addAll( SchemaUtils.getMayAttributeTypeDescriptionNamesTransitive( ocd, schema ) );
            names.addAll( SchemaUtils.getMustAttributeTypeDescriptionNamesTransitive( ocd, schema ) );
            for ( String name : names )
            {
                AttributeType atd = schema.getAttributeTypeDescription( name );
                if ( myAtd == atd )
                {
                    return true;
                }
            }
        }

        // check type
        if ( myAtd != otherAtd )
        {
            AttributeType superiorAtd = null;
            String superiorName = myAtd.getSuperiorOid();
            while ( superiorName != null )
            {
                superiorAtd = schema.getAttributeTypeDescription( superiorName );
                if ( superiorAtd == otherAtd )
                {
                    break;
                }
                superiorName = superiorAtd.getSuperiorOid();
            }
            if ( superiorAtd != otherAtd )
            {
                return false;
            }
        }

        // check options
        List<String> myOptionsList = new ArrayList<String>( this.getParsedOptionList() );
        List<String> otherOptionsList = new ArrayList<String>( other.getParsedOptionList() );
        otherOptionsList.removeAll( myOptionsList );
        if ( !otherOptionsList.isEmpty() )
        {
            return false;
        }

        // check language tags
        List<String> myLangList = new ArrayList<String>( this.getParsedLangList() );
        List<String> otherLangList = new ArrayList<String>( other.getParsedLangList() );
        for ( String myLang : myLangList )
        {
            for ( Iterator<String> otherIt = otherLangList.iterator(); otherIt.hasNext(); )
            {
                String otherLang = otherIt.next();
                if ( otherLang.endsWith( "-" ) )
                {
                    if ( myLang.toLowerCase().startsWith( otherLang.toLowerCase() ) )
                    {
                        otherIt.remove();
                    }
                }
                else
                {
                    if ( myLang.equalsIgnoreCase( otherLang ) )
                    {
                        otherIt.remove();
                    }
                }
            }
        }
        if ( !otherLangList.isEmpty() )
        {
            return false;
        }

        return true;
    }

}
