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

package org.apache.directory.studio.ldapbrowser.common.widgets.browser;


import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;


/**
 * A BrowserCategory is the top-level node in the browser widget. 
 * There are three types: DIT categories, searches categories
 * and bookmarks categories.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BrowserCategory
{

    /** The Constant TYPE_DIT identifies DIT categories. */
    public static final int TYPE_DIT = 0;

    /** The Constant TYPE_SEARCHES identifies searches categories. */
    public static final int TYPE_SEARCHES = 1;

    /** The Constant TYPE_BOOKMARKS identifies bookmark categories. */
    public static final int TYPE_BOOKMARKS = 2;

    /** The title for the DIT categoy */
    public static final String TITLE_DIT = "DIT";

    /** The title for the searches categoy */
    public static final String TITLE_SEARCHES = "Searches";

    /** The title for the bookmarks categoy */
    public static final String TITLE_BOOKMARKS = "Bookmarks";

    /** The category's connection */
    private IBrowserConnection parent;

    /** The category's type */
    private int type;


    /**
     * Creates a new instance of BrowserCategory.
     *
     * @param type the category's type, one of TYPE_DIT, TYPE_SEARCHES or TYPE_BOOKMARKS
     * @param parent the category's connection
     */
    public BrowserCategory( int type, IBrowserConnection parent )
    {
        this.parent = parent;
        this.type = type;
    }


    /**
     * Gets the category's parent, which is always a connection.
     * 
     * @return the parent connection
     */
    public IBrowserConnection getParent()
    {
        return parent;
    }


    /**
     * Gets the category's type, one of TYPE_DIT, TYPE_SEARCHES or TYPE_BOOKMARKS.
     *
     * @return the category's type.
     */
    public int getType()
    {
        return type;
    }


    /**
     * Gets the category's title.
     *
     * @return the category's title
     */
    public String getTitle()
    {
        switch ( type )
        {
            case TYPE_DIT:
                return TITLE_DIT;

            case TYPE_SEARCHES:
                return TITLE_SEARCHES;

            case TYPE_BOOKMARKS:
                return TITLE_BOOKMARKS;

            default:
                return "ERROR";
        }
    }

}
