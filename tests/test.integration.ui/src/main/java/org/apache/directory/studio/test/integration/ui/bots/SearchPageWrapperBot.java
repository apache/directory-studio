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


import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.eclipse.swtbot.swt.finder.SWTBot;


class SearchPageWrapperBot
{
    private SWTBot bot;


    SearchPageWrapperBot( SWTBot bot )
    {
        this.bot = bot;
    }


    boolean isVisible()
    {
        return bot.textWithLabel( "Search Name:" ).isVisible();
    }


    void setSearchName( String string )
    {
        bot.textWithLabel( "Search Name:" ).setText( string );
    }


    void setFilter( String string )
    {
        bot.comboBoxWithLabel( "Filter:" ).setText( string );
    }


    void setReturningAttributes( String string )
    {
        bot.comboBoxWithLabel( "Returning Attributes:" ).setText( string );
    }


    void setControlManageDsaIT( boolean enabled )
    {
        if ( enabled )
        {
            bot.checkBox( "ManageDsaIT" ).select();
        }
        else
        {
            bot.checkBox( "ManageDsaIT" ).deselect();
        }
    }


    void setControlSubentries( boolean enabled )
    {
        if ( enabled )
        {
            bot.checkBox( "Subentries" ).select();
        }
        else
        {
            bot.checkBox( "Subentries" ).deselect();
        }
    }


    void setControlPagedSearch( boolean enabled, int pageSize, boolean scrollMode )
    {
        if ( enabled )
        {
            bot.checkBox( "Paged Search" ).select();
            bot.textInGroup( "Controls" ).setText( "" + pageSize );
            if ( scrollMode )
            {
                bot.checkBox( "Scroll Mode" ).select();
            }
            else
            {
                bot.checkBox( "Scroll Mode" ).deselect();
            }
        }
        else
        {
            bot.checkBox( "Paged Search" ).deselect();
        }
    }


    void setAliasDereferencingMode( AliasDereferencingMethod mode )
    {
        switch ( mode )
        {
            case ALWAYS:
                bot.checkBox( "Finding Base DN" ).select();
                bot.checkBox( "Search" ).select();
                break;
            case FINDING:
                bot.checkBox( "Finding Base DN" ).select();
                bot.checkBox( "Search" ).deselect();
                break;
            case SEARCH:
                bot.checkBox( "Finding Base DN" ).deselect();
                bot.checkBox( "Search" ).select();
                break;
            case NEVER:
                bot.checkBox( "Finding Base DN" ).deselect();
                bot.checkBox( "Search" ).deselect();
                break;
        }
    }


    void setScope( SearchScope scope )
    {
        switch ( scope )
        {
            case OBJECT:
                bot.radio( "Object" ).click();
                break;
            case ONELEVEL:
                bot.radio( "One Level" ).click();
                break;
            case SUBTREE:
                bot.radio( "Subtree" ).click();
                break;
        }
    }


    void setCountLimit( int countLimit )
    {
        bot.textWithLabel( "Count Limit:" ).setText( "" + countLimit );
    }

}
