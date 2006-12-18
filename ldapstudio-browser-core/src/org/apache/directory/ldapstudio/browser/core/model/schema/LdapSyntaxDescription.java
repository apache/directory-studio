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

package org.apache.directory.ldapstudio.browser.core.model.schema;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.directory.ldapstudio.browser.core.BrowserCorePlugin;


/*
 * Value being represented H-R OBJECT IDENTIFIER
 * ================================================================= ACI Item N
 * 1.3.6.1.4.1.1466.115.121.1.1 Access Point Y 1.3.6.1.4.1.1466.115.121.1.2
 * Attribute Type Description Y 1.3.6.1.4.1.1466.115.121.1.3 Audio N
 * 1.3.6.1.4.1.1466.115.121.1.4 Binary N 1.3.6.1.4.1.1466.115.121.1.5 Bit String
 * Y 1.3.6.1.4.1.1466.115.121.1.6 Boolean Y 1.3.6.1.4.1.1466.115.121.1.7
 * Certificate N 1.3.6.1.4.1.1466.115.121.1.8 Certificate List N
 * 1.3.6.1.4.1.1466.115.121.1.9 Certificate Pair N 1.3.6.1.4.1.1466.115.121.1.10
 * Country String Y 1.3.6.1.4.1.1466.115.121.1.11 DN Y
 * 1.3.6.1.4.1.1466.115.121.1.12 Data Quality Syntax Y
 * 1.3.6.1.4.1.1466.115.121.1.13 Delivery Method Y 1.3.6.1.4.1.1466.115.121.1.14
 * Directory String Y 1.3.6.1.4.1.1466.115.121.1.15 DIT Content Rule Description
 * Y 1.3.6.1.4.1.1466.115.121.1.16 DIT Structure Rule Description Y
 * 1.3.6.1.4.1.1466.115.121.1.17 DL Submit Permission Y
 * 1.3.6.1.4.1.1466.115.121.1.18 DSA Quality Syntax Y
 * 1.3.6.1.4.1.1466.115.121.1.19 DSE Type Y 1.3.6.1.4.1.1466.115.121.1.20
 * Enhanced Guide Y 1.3.6.1.4.1.1466.115.121.1.21 Facsimile Telephone Number Y
 * 1.3.6.1.4.1.1466.115.121.1.22 Fax N 1.3.6.1.4.1.1466.115.121.1.23 Generalized
 * Time Y 1.3.6.1.4.1.1466.115.121.1.24 Guide Y 1.3.6.1.4.1.1466.115.121.1.25
 * IA5 String Y 1.3.6.1.4.1.1466.115.121.1.26 INTEGER Y
 * 1.3.6.1.4.1.1466.115.121.1.27 JPEG N 1.3.6.1.4.1.1466.115.121.1.28 LDAP
 * Syntax Description Y 1.3.6.1.4.1.1466.115.121.1.54 LDAP Schema Definition Y
 * 1.3.6.1.4.1.1466.115.121.1.56 LDAP Schema Description Y
 * 1.3.6.1.4.1.1466.115.121.1.57 Master And Shadow Access Points Y
 * 1.3.6.1.4.1.1466.115.121.1.29 Matching Rule Description Y
 * 1.3.6.1.4.1.1466.115.121.1.30 Matching Rule Use Description Y
 * 1.3.6.1.4.1.1466.115.121.1.31 Mail Preference Y 1.3.6.1.4.1.1466.115.121.1.32
 * MHS OR Address Y 1.3.6.1.4.1.1466.115.121.1.33 Modify Rights Y
 * 1.3.6.1.4.1.1466.115.121.1.55 Name And Optional UID Y
 * 1.3.6.1.4.1.1466.115.121.1.34 Name Form Description Y
 * 1.3.6.1.4.1.1466.115.121.1.35 Numeric String Y 1.3.6.1.4.1.1466.115.121.1.36
 * Object Class Description Y 1.3.6.1.4.1.1466.115.121.1.37 Octet String Y
 * 1.3.6.1.4.1.1466.115.121.1.40 OID Y 1.3.6.1.4.1.1466.115.121.1.38 Other
 * Mailbox Y 1.3.6.1.4.1.1466.115.121.1.39 Postal Address Y
 * 1.3.6.1.4.1.1466.115.121.1.41 Protocol Information Y
 * 1.3.6.1.4.1.1466.115.121.1.42 Presentation Address Y
 * 1.3.6.1.4.1.1466.115.121.1.43 Printable String Y
 * 1.3.6.1.4.1.1466.115.121.1.44 Substring Assertion Y
 * 1.3.6.1.4.1.1466.115.121.1.58 Subtree Specification Y
 * 1.3.6.1.4.1.1466.115.121.1.45 Supplier Information Y
 * 1.3.6.1.4.1.1466.115.121.1.46 Supplier Or Consumer Y
 * 1.3.6.1.4.1.1466.115.121.1.47 Supplier And Consumer Y
 * 1.3.6.1.4.1.1466.115.121.1.48 Supported Algorithm N
 * 1.3.6.1.4.1.1466.115.121.1.49 Telephone Number Y
 * 1.3.6.1.4.1.1466.115.121.1.50 Teletex Terminal Identifier Y
 * 1.3.6.1.4.1.1466.115.121.1.51 Telex Number Y 1.3.6.1.4.1.1466.115.121.1.52
 * UTC Time Y 1.3.6.1.4.1.1466.115.121.1.53
 */

public class LdapSyntaxDescription extends SchemaPart
{

    private static final long serialVersionUID = 2740623603305997234L;

    public static final String DN_OID = "1.3.6.1.4.1.1466.115.121.1.12";

    public static final LdapSyntaxDescription DUMMY;
    static
    {
        DUMMY = new LdapSyntaxDescription();
        DUMMY.setSchema( Schema.DEFAULT_SCHEMA );
        DUMMY.setNumericOID( "" );
        DUMMY.setDesc( "" );
    }


    public LdapSyntaxDescription()
    {
        super();
    }


    public int compareTo( Object o )
    {
        if ( o instanceof LdapSyntaxDescription )
        {
            return this.toString().compareTo( o.toString() );
        }
        else
        {
            throw new ClassCastException( "Object of type " + this.getClass().getName() + " required." );
        }
    }


    /**
     * 
     * @return the string representation of this syntax description, either
     *         desc or numericOID
     */
    public String toString()
    {
        if ( this.desc != null && this.desc.length() > 0 )
        {
            return this.desc;
        }
        else if ( numericOID != null )
        {
            return this.numericOID;
        }
        else
        {
            return "";
        }
    }


    /**
     * Convenience method to !isBinary().
     * 
     * @return true if the syntax is defined as string
     */
    public boolean isString()
    {
        return !isBinary();
    }


    /**
     * Checks the pre-defined and user-defined binary syntax oids. If this
     * syntax OID is defned as binary true is returned, false otherwise.
     * 
     * @return true if the syntax is defined as binary
     */
    public boolean isBinary()
    {
        // check user-defined binary syntaxes
        Set binarySyntaxOids = BrowserCorePlugin.getDefault().getCorePreferences().getBinarySyntaxOids();
        if ( binarySyntaxOids.contains( this.numericOID ) )
        {
            return true;
        }

        // default: not binary
        return false;
    }


    /**
     * 
     * @return all attribute type description using this syntax description
     */
    public AttributeTypeDescription[] getUsedFromAttributeTypeDescription()
    {
        Set usedFromSet = new HashSet();
        for ( Iterator it = this.getSchema().getAtdMapByName().values().iterator(); it.hasNext(); )
        {
            AttributeTypeDescription atd = ( AttributeTypeDescription ) it.next();
            if ( atd.getSyntaxDescriptionNumericOIDTransitive() != null && this.numericOID != null
                && atd.getSyntaxDescriptionNumericOIDTransitive().toLowerCase().equals( this.numericOID.toLowerCase() ) )
            {
                usedFromSet.add( atd );
            }
        }
        AttributeTypeDescription[] atds = ( AttributeTypeDescription[] ) usedFromSet
            .toArray( new AttributeTypeDescription[0] );
        Arrays.sort( atds );
        return atds;
    }

}
