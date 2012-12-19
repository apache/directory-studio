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


import org.apache.directory.api.ldap.model.url.LdapUrl;


/**
 * A tagging interface for search continuations.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface IContinuation
{

    public enum State
    {
        /** The search continuation URL is unresolved.*/
        UNRESOLVED,

        /** The search continuation URL is unresolved. The user didn't select a suitable connection for the URL. */
        CANCELED,

        /** The search continuation URL is resolved. The user selected a suitable connection for the URL. */
        RESOLVED
    }


    /**
     * Gets the resolve state.
     * 
     * @return the resolve state
     */
    State getState();


    /**
     * Resolves the search continuation URL, asks the user which connection to use.
     */
    void resolve();


    /**
     * Gets the search continuation URL.
     * 
     * @return the search continuation URL
     */
    LdapUrl getUrl();
}
