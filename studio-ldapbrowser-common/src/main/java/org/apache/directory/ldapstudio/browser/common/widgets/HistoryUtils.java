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

package org.apache.directory.ldapstudio.browser.common.widgets;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.directory.ldapstudio.browser.common.BrowserCommonActivator;
import org.apache.directory.ldapstudio.browser.common.BrowserCommonConstants;


/**
 * The HistoryUtils are used to save and load the history of input fields.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class HistoryUtils
{

    /**
     * Saves the the given value under the given key in the dialog settings.
     *
     * @param key the key
     * @param value the value
     */
    public static void save( String key, String value )
    {
        // get current history
        String[] history = load( key );
        List<String> list = new ArrayList<String>( Arrays.asList( history ) );

        // add new value or move to first position
        if ( list.contains( value ) )
        {
            list.remove( value );
        }
        list.add( 0, value );

        // check history size
        while ( list.size() > BrowserCommonConstants.HISTORYSIZE )
        {
            list.remove( list.size() - 1 );
        }

        // save
        history = list.toArray( new String[list.size()] );
        BrowserCommonActivator.getDefault().getDialogSettings().put( key, history );

    }


    /**
     * Loads the value of the given key from the dialog settings
     *
     * @param key the key
     * @return the value
     */
    public static String[] load( String key )
    {
        String[] history = BrowserCommonActivator.getDefault().getDialogSettings().getArray( key );
        if ( history == null )
        {
            history = new String[0];
        }
        return history;
    }

}
