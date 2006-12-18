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

package org.apache.directory.ldapstudio.browser.ui.widgets;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;


public class HistoryUtils
{

    public static void save( String key, String value )
    {
        // get current history
        String[] history = load( key );
        List list = new ArrayList( Arrays.asList( history ) );

        // add new value or move to first position
        if ( list.contains( value ) )
            list.remove( value );
        list.add( 0, value );

        // check history size
        while ( list.size() > BrowserUIConstants.HISTORYSIZE )
        {
            list.remove( list.size() - 1 );
        }

        // save
        history = ( String[] ) list.toArray( new String[list.size()] );
        BrowserUIPlugin.getDefault().getDialogSettings().put( key, history );

    }


    public static String[] load( String key )
    {
        String[] history = BrowserUIPlugin.getDefault().getDialogSettings().getArray( key );
        if ( history == null )
        {
            history = new String[0];
        }
        return history;
    }

}
