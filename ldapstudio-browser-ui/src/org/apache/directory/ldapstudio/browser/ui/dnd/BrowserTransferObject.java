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

package org.apache.directory.ldapstudio.browser.ui.dnd;


import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.ISearch;


// TODO: Browser Drag 'n' Drop
public class BrowserTransferObject
{

    public static final int TYPE_UNKNOWN = 0;

    public static final int TYPE_ENTRY_TRANSFER = 1;

    public static final int TYPE_SEARCH_TRANSFER = 2;

    private int transferType;

    private IEntry[] entriesToTransfer;

    private ISearch[] searchesToTransfer;


    public BrowserTransferObject()
    {
        this.transferType = TYPE_UNKNOWN;
        this.entriesToTransfer = new IEntry[0];
        this.searchesToTransfer = new ISearch[0];
    }


    public BrowserTransferObject( IEntry[] entriesToTransfer )
    {
        this();
        this.initEntriesToTransfer( entriesToTransfer );
    }


    public BrowserTransferObject( ISearch[] searchesToTransfer )
    {
        this();
        this.initSearchesToTransfer( searchesToTransfer );
    }


    public BrowserTransferObject( IEntry[] entriesToTransfer, ISearch[] searchesToTransfer )
    {
        this();
        this.initEntriesToTransfer( entriesToTransfer );
        this.initSearchesToTransfer( searchesToTransfer );
    }


    private void initEntriesToTransfer( IEntry[] entriesToTransfer )
    {
        if ( entriesToTransfer != null || entriesToTransfer.length > 0 )
        {
            this.transferType |= TYPE_ENTRY_TRANSFER;
            this.entriesToTransfer = entriesToTransfer;
        }
    }


    private void initSearchesToTransfer( ISearch[] searchesToTransfer )
    {
        if ( searchesToTransfer != null || searchesToTransfer.length > 0 )
        {
            this.transferType |= TYPE_SEARCH_TRANSFER;
            this.searchesToTransfer = searchesToTransfer;
        }
    }


    public IEntry[] getEntriesToTransfer()
    {
        return entriesToTransfer;
    }


    public ISearch[] getSearchesToTransfer()
    {
        return searchesToTransfer;
    }


    public int getTransferType()
    {
        return transferType;
    }

}
