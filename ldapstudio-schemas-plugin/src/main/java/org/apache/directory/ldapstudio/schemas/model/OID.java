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

package org.apache.directory.ldapstudio.schemas.model;


/**
 * This class is the placeholder fot the model for OID strings.
 * 
 * Currently we only use string validation, but the ultimate goal would be to fully
 * represent OIDs as model object in LDAP Studio.
 * 
 * An oid is a string of numbers separated by dots, like 1.2.3.4.567
 *
 */
public class OID
{

    /**
     * Validate an OID string
     * @param str the OID
     * @return true if it's a correcly formed OID
     */
    public static boolean validate( String str )
    {
        return java.util.regex.Pattern.matches( "^\\d+(\\.\\d+)*", str ); //$NON-NLS-1$
    }
}
