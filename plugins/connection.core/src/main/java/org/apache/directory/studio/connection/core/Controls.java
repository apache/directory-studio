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

package org.apache.directory.studio.connection.core;


import org.apache.directory.api.ldap.model.message.Control;
import org.apache.directory.api.ldap.model.message.controls.ManageDsaIT;
import org.apache.directory.api.ldap.model.message.controls.ManageDsaITImpl;
import org.apache.directory.api.ldap.model.message.controls.OpaqueControl;
import org.apache.directory.api.ldap.model.message.controls.PagedResults;
import org.apache.directory.api.ldap.model.message.controls.PagedResultsImpl;
import org.apache.directory.api.ldap.model.message.controls.Subentries;
import org.apache.directory.api.ldap.model.message.controls.SubentriesImpl;


/**
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class Controls
{

    public static final Subentries SUBENTRIES_CONTROL = new SubentriesImpl();
    static
    {
        SUBENTRIES_CONTROL.setVisibility( true );
    }

    public static final ManageDsaIT MANAGEDSAIT_CONTROL = new ManageDsaITImpl();

    public static final Control TREEDELETE_CONTROL = new OpaqueControl( "1.2.840.113556.1.4.805", false );


    public static final PagedResults newPagedResultsControl( int size )
    {
        PagedResults control = new PagedResultsImpl();
        control.setSize( size );
        return control;
    }


    public static final PagedResults newPagedResultsControl( int size, byte[] cookie )
    {
        PagedResults control = new PagedResultsImpl();
        control.setSize( size );
        control.setCookie( cookie );
        return control;
    }
}
