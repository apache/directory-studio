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

package org.apache.directory.ldapstudio.browser.core.events;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.ldapstudio.browser.core.model.schema.AttributeTypeDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.LdapSyntaxDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.MatchingRuleDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.MatchingRuleUseDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.ObjectClassDescription;


public class EventRegistry
{

    private static EventDispatcher eventDispatcher;


    public static void init( EventDispatcher eventDispatcher )
    {
        EventRegistry.eventDispatcher = eventDispatcher;
    }


    public static void resumeEventFireingInCurrentThread()
    {
        eventDispatcher.resumeEventFireingInCurrentThread();
    }


    public static void suspendEventFireingInCurrentThread()
    {
        eventDispatcher.suspendEventFireingInCurrentThread();
    }

    private static List searchUpdateListenerList = new ArrayList();


    public static void addSearchUpdateListener( SearchUpdateListener listener )
    {
        if ( !searchUpdateListenerList.contains( listener ) )
            searchUpdateListenerList.add( listener );
    }


    public static void removeSearchUpdateListener( SearchUpdateListener listener )
    {
        if ( searchUpdateListenerList.contains( listener ) )
            searchUpdateListenerList.remove( listener );
    }


    public static void fireSearchUpdated( final SearchUpdateEvent searchUpdateEvent, final Object source )
    {
        for ( int i = 0; i < searchUpdateListenerList.size(); i++ )
        {
            final SearchUpdateListener listener = ( SearchUpdateListener ) searchUpdateListenerList.get( i );
            EventRunnable runnable = new EventRunnable()
            {
                public Object getEventObject()
                {
                    return searchUpdateEvent.getSearch();
                }


                public void run()
                {
                    listener.searchUpdated( searchUpdateEvent );
                }
            };
            eventDispatcher.dispatchEvent( source, listener, runnable );
        }
    }

    private static List bookmarkUpdateListenerList = new ArrayList();


    public static void addBookmarkUpdateListener( BookmarkUpdateListener listener )
    {
        if ( !bookmarkUpdateListenerList.contains( listener ) )
            bookmarkUpdateListenerList.add( listener );
    }


    public static void removeBookmarkUpdateListener( BookmarkUpdateListener listener )
    {
        if ( bookmarkUpdateListenerList.contains( listener ) )
            bookmarkUpdateListenerList.remove( listener );
    }


    public static void fireBookmarkUpdated( final BookmarkUpdateEvent bookmarkUpdateEvent, final Object source )
    {
        for ( int i = 0; i < bookmarkUpdateListenerList.size(); i++ )
        {
            final BookmarkUpdateListener listener = ( BookmarkUpdateListener ) bookmarkUpdateListenerList.get( i );
            EventRunnable runnable = new EventRunnable()
            {
                public Object getEventObject()
                {
                    return bookmarkUpdateEvent.getBookmark();
                }


                public void run()
                {
                    listener.bookmarkUpdated( bookmarkUpdateEvent );
                }
            };
            eventDispatcher.dispatchEvent( source, listener, runnable );
        }
    }

    private static List connectionUpdateListenerList = new ArrayList();


    public static void addConnectionUpdateListener( ConnectionUpdateListener listener )
    {
        if ( !connectionUpdateListenerList.contains( listener ) )
            connectionUpdateListenerList.add( listener );
    }


    public static void removeConnectionUpdateListener( ConnectionUpdateListener listener )
    {
        if ( connectionUpdateListenerList.contains( listener ) )
            connectionUpdateListenerList.remove( listener );
    }


    public static void fireConnectionUpdated( final ConnectionUpdateEvent connectionUpdateEvent, final Object source )
    {
        for ( int i = 0; i < connectionUpdateListenerList.size(); i++ )
        {
            final ConnectionUpdateListener listener = ( ConnectionUpdateListener ) connectionUpdateListenerList.get( i );
            EventRunnable runnable = new EventRunnable()
            {
                public Object getEventObject()
                {
                    return connectionUpdateEvent.getConnection();
                }


                public void run()
                {
                    listener.connectionUpdated( connectionUpdateEvent );
                }
            };
            eventDispatcher.dispatchEvent( source, listener, runnable );
        }
    }

    private static List entryUpdateListenerList = new ArrayList();


    public static void addEntryUpdateListener( EntryUpdateListener listener )
    {
        if ( !entryUpdateListenerList.contains( listener ) )
            entryUpdateListenerList.add( listener );
    }


    public static void removeEntryUpdateListener( EntryUpdateListener listener )
    {
        if ( entryUpdateListenerList.contains( listener ) )
            entryUpdateListenerList.remove( listener );
    }


    public static void fireEntryUpdated( final EntryModificationEvent event, final Object source )
    {
        for ( int i = 0; i < entryUpdateListenerList.size(); i++ )
        {
            final EntryUpdateListener listener = ( EntryUpdateListener ) entryUpdateListenerList.get( i );
            EventRunnable runnable = new EventRunnable()
            {
                public Object getEventObject()
                {
                    return event;
                }


                public void run()
                {
                    listener.entryUpdated( event );
                }
            };
            eventDispatcher.dispatchEvent( source, listener, runnable );
        }
    }

    private static List schemaElementSelectionListenerList = new ArrayList();


    public static void addSchemaElementSelectionListener( SchemaElementSelectionListener listener )
    {
        if ( !schemaElementSelectionListenerList.contains( listener ) )
            schemaElementSelectionListenerList.add( listener );
    }


    public static void removeSchemaElementSelectionListener( SchemaElementSelectionListener listener )
    {
        if ( schemaElementSelectionListenerList.contains( listener ) )
            schemaElementSelectionListenerList.remove( listener );
    }


    public static void fireObjectClassDescriptionSelected( final ObjectClassDescription ocd, final Object source )
    {
        for ( int i = 0; i < schemaElementSelectionListenerList.size(); i++ )
        {
            final SchemaElementSelectionListener listener = ( SchemaElementSelectionListener ) schemaElementSelectionListenerList
                .get( i );
            EventRunnable runnable = new EventRunnable()
            {
                public Object getEventObject()
                {
                    return ocd;
                }


                public void run()
                {
                    listener.objectClassDescriptionSelected( ocd );
                }
            };
            eventDispatcher.dispatchEvent( source, listener, runnable );
        }
    }


    public static void fireAttributeTypeDescriptionSelected( final AttributeTypeDescription atd, final Object source )
    {
        for ( int i = 0; i < schemaElementSelectionListenerList.size(); i++ )
        {
            final SchemaElementSelectionListener listener = ( SchemaElementSelectionListener ) schemaElementSelectionListenerList
                .get( i );
            EventRunnable runnable = new EventRunnable()
            {
                public Object getEventObject()
                {
                    return atd;
                }


                public void run()
                {
                    listener.attributeTypeDescriptionSelected( atd );
                }
            };
            eventDispatcher.dispatchEvent( source, listener, runnable );
        }
    }


    public static void fireMatchingRuleDescriptionSelected( final MatchingRuleDescription mrd, final Object source )
    {
        for ( int i = 0; i < schemaElementSelectionListenerList.size(); i++ )
        {
            final SchemaElementSelectionListener listener = ( SchemaElementSelectionListener ) schemaElementSelectionListenerList
                .get( i );
            EventRunnable runnable = new EventRunnable()
            {
                public Object getEventObject()
                {
                    return mrd;
                }


                public void run()
                {
                    listener.matchingRuleDescriptionSelected( mrd );
                }
            };
            eventDispatcher.dispatchEvent( source, listener, runnable );
        }
    }


    public static void fireLdapSyntaxDescriptionSelected( final LdapSyntaxDescription lsd, final Object source )
    {
        for ( int i = 0; i < schemaElementSelectionListenerList.size(); i++ )
        {
            final SchemaElementSelectionListener listener = ( SchemaElementSelectionListener ) schemaElementSelectionListenerList
                .get( i );
            EventRunnable runnable = new EventRunnable()
            {
                public Object getEventObject()
                {
                    return lsd;
                }


                public void run()
                {
                    listener.ldapSyntacDescriptionSelected( lsd );
                }
            };
            eventDispatcher.dispatchEvent( source, listener, runnable );
        }
    }


    public static void fireMatchingRuleUseDescriptionSelected( final MatchingRuleUseDescription mrud,
        final Object source )
    {
        for ( int i = 0; i < schemaElementSelectionListenerList.size(); i++ )
        {
            final SchemaElementSelectionListener listener = ( SchemaElementSelectionListener ) schemaElementSelectionListenerList
                .get( i );
            EventRunnable runnable = new EventRunnable()
            {
                public Object getEventObject()
                {
                    return mrud;
                }


                public void run()
                {
                    listener.matchingRuleUseDescriptionSelected( mrud );
                }
            };
            eventDispatcher.dispatchEvent( source, listener, runnable );
        }
    }

}
