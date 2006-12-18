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

package org.apache.directory.ldapstudio.browser.ui.widgets.browser;


import org.apache.directory.ldapstudio.browser.core.model.IConnection;


public class BrowserCategory
{

    public static final int TYPE_DIT = 0;

    public static final int TYPE_SEARCHES = 1;

    public static final int TYPE_BOOKMARKS = 2;

    public static final String TITLE_DIT = "DIT";

    public static final String TITLE_SEARCHES = "Searches";

    public static final String TITLE_BOOKMARKS = "Bookmarks";

    private IConnection parent;

    private int type;

    private Object[] children;


    public BrowserCategory( int type, IConnection parent, Object[] children )
    {
        super();
        this.children = children;
        this.parent = parent;
        this.type = type;
    }


    public Object[] getChildren()
    {
        return children;
    }


    public IConnection getParent()
    {
        return parent;
    }


    public int getType()
    {
        return type;
    }


    public String getTitle()
    {
        if ( type == TYPE_DIT )
            return TITLE_DIT;
        if ( type == TYPE_SEARCHES )
            return TITLE_SEARCHES;
        if ( type == TYPE_BOOKMARKS )
            return TITLE_BOOKMARKS;
        return "ERROR";
    }


    public void setChildren( Object[] children )
    {
        this.children = children;
    }

}
