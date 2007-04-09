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

package org.apache.directory.ldapstudio.browser.common;


import java.util.HashSet;
import java.util.Set;


import org.apache.directory.ldapstudio.browser.core.events.EventDispatcher;
import org.apache.directory.ldapstudio.browser.core.events.EventListener;
import org.apache.directory.ldapstudio.browser.core.events.EventRunnable;
import org.eclipse.swt.widgets.Display;


public class EventDispatcherSync implements EventDispatcher
{

    private Set pauseEventFireringThreadList;

    private Object lock;


    public EventDispatcherSync()
    {
        this.pauseEventFireringThreadList = new HashSet();;
        this.lock = new Object();
    }


    public void resumeEventFireingInCurrentThread()
    {
        this.pauseEventFireringThreadList.remove( Thread.currentThread() );
    }


    public void suspendEventFireingInCurrentThread()
    {
        this.pauseEventFireringThreadList.add( Thread.currentThread() );
    }


    public boolean isEventFireingSuspendInCurrentThread()
    {
        return this.pauseEventFireringThreadList.contains( Thread.currentThread() );
    }


    public void dispatchEvent( Object source, EventListener target, EventRunnable runnable )
    {

        SourceTargetRunnableWrapper strw = new SourceTargetRunnableWrapper( source, target, runnable );

        if ( isEventFireingSuspendInCurrentThread() )
        {
            return;
        }

        synchronized ( lock )
        {
            this.runSourceTargetRunnableWrapper( strw );
        }
    }


    public synchronized void startEventDispatcher()
    {
    }


    public synchronized void stopEventDispatcher()
    {
    }


    private void runSourceTargetRunnableWrapper( SourceTargetRunnableWrapper strw )
    {
        // System.out.println(strw.toString());
        Display.getDefault().asyncExec( strw.runnable );
        // Display.getDefault().syncExec(strw.runnable);
    }

    class SourceTargetRunnableWrapper
    {
        Object source;

        EventListener target;

        EventRunnable runnable;


        SourceTargetRunnableWrapper( Object source, EventListener target, EventRunnable runnable )
        {
            this.source = source;
            this.target = target;
            this.runnable = runnable;
        }


        public boolean equals( Object o )
        {
            if ( o instanceof SourceTargetRunnableWrapper )
            {
                SourceTargetRunnableWrapper strw = ( SourceTargetRunnableWrapper ) o;
                return strw.source == this.source
                    && strw.target == this.target
                    && strw.runnable.getClass() == this.runnable.getClass()
                    && ( strw.runnable.getEventObject() == null || this.runnable.getEventObject() == null || strw.runnable
                        .getEventObject().getClass() == this.runnable.getEventObject().getClass() );
            }
            else
            {
                return false;
            }
        }


        public int hashCode()
        {
            return this.source.hashCode() + this.target.hashCode() + this.runnable.getClass().hashCode();
        }


        public String toString()
        {
            return "" + source.getClass().getName() + " - " + target.getClass().getName();
        }
    }

}
