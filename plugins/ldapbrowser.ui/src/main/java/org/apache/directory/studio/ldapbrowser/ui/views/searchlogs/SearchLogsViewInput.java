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

package org.apache.directory.studio.ldapbrowser.ui.views.searchlogs;


import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;


/**
 * A SearchLogsViewInput represents the input of the search logs view.
 * It consists of a connection and the index of the displayed log file.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchLogsViewInput
{

    /** The browser connection. */
    private IBrowserConnection browserConnection;

    /** The index of the displayed log file */
    private int index;


    /**
     * Creates a new instance of ModificationLogsViewInput.
     * 
     * @param browserConnection the browser connection
     * @param index the index of the displayed log file
     */
    public SearchLogsViewInput( IBrowserConnection browserConnection, int index )
    {
        this.browserConnection = browserConnection;
        this.index = index;
    }


    /**
     * Gets the browser connection.
     * 
     * @return the browser connection
     */
    public IBrowserConnection getBrowserConnection()
    {
        return browserConnection;
    }


    /**
     * Gets the index of the displayed log file.
     * 
     * @return the index
     */
    public int getIndex()
    {
        return index;
    }

}
