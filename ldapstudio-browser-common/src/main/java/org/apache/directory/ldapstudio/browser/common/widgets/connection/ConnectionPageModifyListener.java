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

package org.apache.directory.ldapstudio.browser.common.widgets.connection;


import org.apache.directory.ldapstudio.browser.core.model.IConnection;


/**
 * A ConnectionPageModifyListener listens for modifications of the 
 * ConnectionPageWrapper.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface ConnectionPageModifyListener
{

    /**
     * Indicates that the connection page was modified.
     */
    public void connectionPageModified();


    /**
     * Sets a non-error message that should be displayed
     * to the user. Null means no message so an existing
     * message shuld be cleared.
     * 
     * @param message the message
     */
    public void setMessage( String message );


    /**
     * Sets an error message that should be displayed
     * to the user. Null means no error message so an 
     * existing error message shuld be cleared.
     * 
     * @param errorMessage the error message
     */

    public void setErrorMessage( String errorMessage );


    /**
     * Gets the real connection or null if none.
     *
     * @return the real connection
     */
    public IConnection getRealConnection();

}
