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

package org.apache.directory.studio.ldapbrowser.common.widgets;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Base class that provides support for {@link WidgetModifyListener} 
 * registration and notification.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class BrowserWidget
{

    /** The listener list */
    protected List<WidgetModifyListener> modifyListenerList;


    /**
     * Creates a new instance of BrowserWidget.
     */
    protected BrowserWidget()
    {
        modifyListenerList = new ArrayList<WidgetModifyListener>( 3 );
    }


    /**
     * Adds the widget modify listener.
     * 
     * @param listener the listener
     */
    public void addWidgetModifyListener( WidgetModifyListener listener )
    {
        if ( !modifyListenerList.contains( listener ) )
        {
            modifyListenerList.add( listener );
        }
    }


    /**
     * Removes the widget modify listener.
     * 
     * @param listener the listener
     */
    public void removeWidgetModifyListener( WidgetModifyListener listener )
    {
        if ( modifyListenerList.contains( listener ) )
            modifyListenerList.remove( listener );
    }


    /**
     * Notifies the listeners.
     */
    protected void notifyListeners()
    {
        WidgetModifyEvent event = new WidgetModifyEvent( this );
        for ( Iterator<WidgetModifyListener> it = modifyListenerList.iterator(); it.hasNext(); )
        {
            WidgetModifyListener listener = it.next();
            listener.widgetModified( event );
        }
    }

}
