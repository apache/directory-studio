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

package org.apache.directory.studio.ldapbrowser.core.model.ldif;


import org.apache.directory.studio.ldapbrowser.core.internal.model.ConnectionException;
import org.apache.directory.studio.ldapbrowser.core.jobs.ExtendedProgressMonitor;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.container.LdifContainer;


public interface LdifEnumeration
{

    /**
     * 
     * @param monitor
     *                The monitor to check for cancellation. Don't report
     *                errors to the monitor, throw an ConnectionException
     *                instead.
     * @throws ConnectionException
     */
    public boolean hasNext( ExtendedProgressMonitor monitor ) throws ConnectionException;


    /**
     * 
     * @param monitor
     *                The monitor to check for cancellation. Don't report
     *                errors to the monitor, throw an ConnectionException
     *                instead.
     * @return the next LDIF container or null if hasNext() returns false;
     * @throws ConnectionException
     */
    public LdifContainer next( ExtendedProgressMonitor monitor ) throws ConnectionException;

}
