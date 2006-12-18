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


import javax.naming.ldap.Control;


public class JNDISubentriesControl implements Control
{

    private static final long serialVersionUID = -6614360496036854589L;


    // Note that TRUE visibility has the three octet encoding { 01 01 FF }
    // and FALSE visibility has the three octet encoding { 01 01 00 }.

    public JNDISubentriesControl()
    {
        // super(OID, false, null);
        // super.value = setEncodedValue(FIRST, SECOND, sub?TRUE:FALSE);
    }


    public byte[] getEncodedValue()
    {

        byte[] value = new byte[]
            {
            // 0x30, 0x00,
                // ( byte ) 0xa0, 0x23, // controls
                // 0x30, 0x21,
                // 0x04, 0x17,
                // '1', '.', '3', '.', '6', '.', '1', '.', '4', '.', '1', '.',
                // '4', '2', '0', '3',
                // '.', '1', '.', '1', '0', '.', '1', // SubEntry OID
                // 0x01, 0x01, ( byte ) 0xFF, // criticality: true
                // 0x04, 0x03,
                0x01, 0x01, ( byte ) 0xFF // SubEntry visibility
            };
        return value;
    }


    public String getID()
    {
        return "1.3.6.1.4.1.4203.1.10.1";
    }


    public boolean isCritical()
    {
        return false;
    }

}
