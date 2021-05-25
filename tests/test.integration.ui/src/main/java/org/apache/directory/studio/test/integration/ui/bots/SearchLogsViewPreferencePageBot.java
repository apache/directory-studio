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
package org.apache.directory.studio.test.integration.ui.bots;


public class SearchLogsViewPreferencePageBot extends PreferencePageBot
{

    public void setEnableSearchRequestLogs( boolean b )
    {
        activate();
        if ( b )
        {
            bot.checkBox( 0 ).select();
        }
        else
        {
            bot.checkBox( 0 ).deselect();
        }
    }


    public void setEnableSearchResultEntryLogs( boolean b )
    {
        activate();
        if ( b )
        {
            bot.checkBox( 1 ).select();
        }
        else
        {
            bot.checkBox( 1 ).deselect();
        }
    }


    public void setLogFileCount( int i )
    {
        bot.text( 1 ).setText( "" + i );
    }


    public void setLogFileSize( int i )
    {
        bot.text( 2 ).setText( "" + i );
    }

}
