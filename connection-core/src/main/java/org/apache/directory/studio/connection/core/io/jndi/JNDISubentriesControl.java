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

package org.apache.directory.studio.connection.core.io.jndi;


import javax.naming.ldap.BasicControl;


/**
 * The Subentries control.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class JNDISubentriesControl extends BasicControl
{

    private static final long serialVersionUID = -6614360496036854589L;

    /**
     * The Subentries control's OID is 1.3.6.1.4.1.4203.1.10.1.
     */
    public static final String OID = "1.3.6.1.4.1.4203.1.10.1"; //$NON-NLS-1$

    /**
     * The Subentries control's value.
     */
    public static final byte[] VALUE = new byte[]
        { 0x01, 0x01, ( byte ) 0xFF };


    /**
     * Creates a new instance of JNDISubentriesControl.
     */
    public JNDISubentriesControl()
    {
        super( OID, false, VALUE );
    }

}
