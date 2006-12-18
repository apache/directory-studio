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

package org.apache.directory.ldapstudio.browser.core.internal.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.schema.AttributeTypeDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.Schema;


public class AttributeDescription implements Serializable
{

    private static final long serialVersionUID = 1L;

    private String description;

    private String parsedAttributeType;

    private List parsedLangList;

    private List parsedOptionList;


    public AttributeDescription( String description )
    {

        this.description = description;

        String[] attributeDescriptionComponents = description.split( IAttribute.OPTION_DELIMITER );
        this.parsedAttributeType = attributeDescriptionComponents[0];
        this.parsedLangList = new ArrayList();
        this.parsedOptionList = new ArrayList();
        for ( int i = 1; i < attributeDescriptionComponents.length; i++ )
        {
            if ( attributeDescriptionComponents[i].startsWith( IAttribute.OPTION_LANG_PREFIX ) )
            {
                this.parsedLangList.add( attributeDescriptionComponents[i] );
            }
            else
            {
                this.parsedOptionList.add( attributeDescriptionComponents[i] );
            }
        }
    }


    public String getDescription()
    {
        return description;
    }


    public String getParsedAttributeType()
    {
        return parsedAttributeType;
    }


    public List getParsedLangList()
    {
        return parsedLangList;
    }


    public List getParsedOptionList()
    {
        return parsedOptionList;
    }


    public String toOidString( Schema schema )
    {

        if ( schema == null )
        {
            return description;
        }

        AttributeTypeDescription atd = schema.getAttributeTypeDescription( parsedAttributeType );
        String oidString = atd.getNumericOID();

        if ( !parsedLangList.isEmpty() )
        {
            for ( Iterator it = parsedLangList.iterator(); it.hasNext(); )
            {
                String element = ( String ) it.next();
                oidString += element;

                if ( it.hasNext() || !parsedOptionList.isEmpty() )
                {
                    oidString += IAttribute.OPTION_DELIMITER;
                }
            }
        }
        if ( !parsedOptionList.isEmpty() )
        {
            for ( Iterator it = parsedOptionList.iterator(); it.hasNext(); )
            {
                String element = ( String ) it.next();
                oidString += element;

                if ( it.hasNext() )
                {
                    oidString += IAttribute.OPTION_DELIMITER;
                }
            }
        }

        return oidString;
    }


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

        // check type
        AttributeTypeDescription myAtd = schema.getAttributeTypeDescription( this.getParsedAttributeType() );
        AttributeTypeDescription otherAtd = schema.getAttributeTypeDescription( other.getParsedAttributeType() );
        if ( myAtd != otherAtd )
        {
            AttributeTypeDescription superiorAtd = null;
            String superiorName = myAtd.getSuperiorAttributeTypeDescriptionName();
            while ( superiorName != null )
            {
                superiorAtd = schema.getAttributeTypeDescription( superiorName );
                if ( superiorAtd == otherAtd )
                {
                    break;
                }
                superiorName = superiorAtd.getSuperiorAttributeTypeDescriptionName();
            }
            if ( superiorAtd != otherAtd )
            {
                return false;
            }
        }

        // check options
        List myOptionsList = new ArrayList( this.getParsedOptionList() );
        List otherOptionsList = new ArrayList( other.getParsedOptionList() );
        otherOptionsList.removeAll( myOptionsList );
        if ( !otherOptionsList.isEmpty() )
        {
            return false;
        }

        // check language tags
        List myLangList = new ArrayList( this.getParsedLangList() );
        List otherLangList = new ArrayList( other.getParsedLangList() );
        for ( Iterator myIt = myLangList.iterator(); myIt.hasNext(); )
        {
            String myLang = ( String ) myIt.next();
            for ( Iterator otherIt = otherLangList.iterator(); otherIt.hasNext(); )
            {
                String otherLang = ( String ) otherIt.next();
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
