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

package org.apache.directory.studio.ldapbrowser.core.model.impl;


import org.apache.directory.studio.ldapbrowser.core.model.DN;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;


/**
 * An {@link AliasBaseEntry} represents the target 
 * (named by the aliasedObjectName attribute) of an alias entry.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class AliasBaseEntry extends DelegateEntry
{

    private static final long serialVersionUID = -3599038109979581295L;


    protected AliasBaseEntry()
    {
    }


    /**
     * Creates a new instance of AliasBaseEntry.
     * 
     * @param connection the connection of the alias
     * @param dn the DN of the alias target
     */
    public AliasBaseEntry( IBrowserConnection connection, DN dn )
    {
        super( connection, dn );
    }

}
