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

package org.apache.directory.studio.common.ui;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogSettings;


/**
 * The HistoryUtils are used to save and load the history of input fields.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class HistoryUtils
{
    /**
     * Saves the the given value under the given key in the dialog settings.
     *
     * @param dialogSettings the dialog settings
     * @param key the key
     * @param value the value
     */
    public static void save( IDialogSettings dialogSettings, String key, String value )
    {
        if ( dialogSettings != null )
        {

            // get current history
            String[] history = load( dialogSettings, key );
            List<String> list = new ArrayList<String>( Arrays.asList( history ) );

            // add new value or move to first position
            if ( list.contains( value ) )
            {
                list.remove( value );
            }
            list.add( 0, value );

            // check history size
            while ( list.size() > 20 )
            {
                list.remove( list.size() - 1 );
            }

            // save
            history = list.toArray( new String[list.size()] );
            dialogSettings.put( key, history );
        }
    }


    /**
     * Loads the value of the given key from the dialog settings
     *
     * @param dialogSettings the dialog settings
     * @param key the key
     * @return the value
     */
    public static String[] load( IDialogSettings dialogSettings, String key )
    {
        if ( dialogSettings != null )
        {
            String[] history = dialogSettings.getArray( key );

            if ( history == null )
            {
                history = new String[0];
            }

            return history;
        }

        return new String[0];
    }
}
