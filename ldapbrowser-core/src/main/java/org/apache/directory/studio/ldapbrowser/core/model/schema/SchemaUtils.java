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

package org.apache.directory.studio.ldapbrowser.core.model.schema;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;


public class SchemaUtils
{

    public static String[] getAttributeTypeDescriptionNames( AttributeTypeDescription[] atds )
    {

        Set attributeSet = new HashSet();
        for ( int i = 0; i < atds.length; i++ )
        {
            AttributeTypeDescription atd = atds[i];
            attributeSet.addAll( Arrays.asList( atd.getNames() ) );
        }

        String[] attributes = ( String[] ) attributeSet.toArray( new String[0] );
        Arrays.sort( attributes );
        return attributes;

    }


    /**
     * 
     * @return all operational attributes types
     */
    public static AttributeTypeDescription[] getOperationalAttributeDescriptions( Schema schema )
    {
        Set operationalAttributeSet = new HashSet();
        for ( Iterator it = schema.getAtdMapByName().values().iterator(); it.hasNext(); )
        {
            AttributeTypeDescription atd = ( AttributeTypeDescription ) it.next();
            if ( isOperational( atd ) )
            {
                operationalAttributeSet.add( atd );
            }
        }

        AttributeTypeDescription[] operationalAttributes = ( AttributeTypeDescription[] ) operationalAttributeSet
            .toArray( new AttributeTypeDescription[0] );
        return operationalAttributes;
    }


    /**
     * 
     * @return all user attributes types
     */
    public static AttributeTypeDescription[] getUserAttributeDescriptions( Schema schema )
    {
        Set userAttributeSet = new HashSet();
        for ( Iterator it = schema.getAtdMapByName().values().iterator(); it.hasNext(); )
        {
            AttributeTypeDescription atd = ( AttributeTypeDescription ) it.next();
            if ( !isOperational( atd ) )
            {
                userAttributeSet.add( atd );
            }
        }

        AttributeTypeDescription[] userAttributes = ( AttributeTypeDescription[] ) userAttributeSet
            .toArray( new AttributeTypeDescription[0] );
        return userAttributes;
    }


    public static boolean isOperational( AttributeTypeDescription atd )
    {
        return atd.isNoUserModification()
            || !AttributeTypeDescription.ATTRIBUTE_USAGE_USER_APPLICATIONS.equalsIgnoreCase( atd.getUsage() );

        // atd.isNoUserModification()
        // ||
        // AttributeTypeDescription.ATTRIBUTE_USAGE_DIRECTORY_OPERATION.equalsIgnoreCase(atd.getUsage())
        // ||
        // AttributeTypeDescription.ATTRIBUTE_USAGE_DSA_OPERATION.equalsIgnoreCase(atd.getUsage());
    }


    public static boolean isModifyable( AttributeTypeDescription atd )
    {

        if ( atd == null )
        {
            return false;
        }

        if ( atd.isNoUserModification() )
        {
            return false;
        }

        // Check some default no-user-modification attributes
        // e.g. Siemens DirX doesn't provide a good schema.
        // TODO: make default no-user-modification attributes configurable
        String[] nonModifyableAttributes = new String[]
            { IAttribute.OPERATIONAL_ATTRIBUTE_CREATORS_NAME, IAttribute.OPERATIONAL_ATTRIBUTE_CREATE_TIMESTAMP,
                IAttribute.OPERATIONAL_ATTRIBUTE_MODIFIERS_NAME, IAttribute.OPERATIONAL_ATTRIBUTE_MODIFY_TIMESTAMP,
                IAttribute.OPERATIONAL_ATTRIBUTE_STRUCTURAL_OBJECT_CLASS,
                IAttribute.OPERATIONAL_ATTRIBUTE_GOVERNING_STRUCTURE_RULE,

                IAttribute.OPERATIONAL_ATTRIBUTE_SUBSCHEMA_SUBENTRY, IAttribute.OPERATIONAL_ATTRIBUTE_VENDOR_NAME,
                IAttribute.OPERATIONAL_ATTRIBUTE_VENDOR_VERSION,

                IAttribute.OPERATIONAL_ATTRIBUTE_ENTRY_UUID, IAttribute.OPERATIONAL_ATTRIBUTE_HAS_SUBORDINATES,
                IAttribute.OPERATIONAL_ATTRIBUTE_SUBORDINATE_COUNT, IAttribute.OPERATIONAL_ATTRIBUTE_NUM_SUBORDINATES

            };
        for ( int i = 0; i < nonModifyableAttributes.length; i++ )
        {
            String att = nonModifyableAttributes[i];
            if ( att.equalsIgnoreCase( atd.getNumericOID() ) )
            {
                return false;
            }
            for ( int n = 0; n < atd.getNames().length; n++ )
            {
                if ( att.equalsIgnoreCase( atd.getNames()[n] ) )
                {
                    return false;
                }
            }
        }

        return true;
    }

}
