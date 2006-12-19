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

package org.apache.directory.ldapstudio.browser.ui.jobs;


import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;


public class TimeTriggeredProgressMonitorDialog extends ProgressMonitorDialog
{

    /**
     * The time considered to be the long operation time.
     */
    private int longOperationTime;

    /**
     * The time at which the dialog should be opened.
     */
    private long triggerTime = -1;

    /**
     * Whether or not we've already opened a dialog.
     */
    private boolean dialogOpened = false;

    /**
     * Wrappered monitor so we can check ticks and open the dialog when
     * appropriate
     */
    private IProgressMonitor wrapperedMonitor;


    /**
     * Create a new instance of the receiver.
     * 
     * @param parent
     *                the parent of the dialog
     * @param longOperationTime
     *                the time (in milliseconds) considered to be a long
     *                enough execution time to warrant opening a dialog.
     */
    public TimeTriggeredProgressMonitorDialog( Shell parent, int longOperationTime )
    {
        super( parent );
        setOpenOnRun( false );
        this.longOperationTime = longOperationTime;
    }


    /**
     * Create a monitor for the receiver that wrappers the superclasses
     * monitor.
     * 
     */
    public void createWrapperedMonitor()
    {
        wrapperedMonitor = new IProgressMonitor()
        {

            IProgressMonitor superMonitor = TimeTriggeredProgressMonitorDialog.super.getProgressMonitor();


            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.core.runtime.IProgressMonitor#beginTask(java.lang.String,
             *      int)
             */
            public void beginTask( String name, int totalWork )
            {
                superMonitor.beginTask( name, totalWork );
                checkTicking();
            }


            /**
             * Check if we have ticked in the last 800ms.
             */
            private void checkTicking()
            {
                if ( triggerTime < 0 )
                    triggerTime = System.currentTimeMillis() + longOperationTime;
                if ( !dialogOpened && System.currentTimeMillis() > triggerTime )
                {

                    // workaround: check that not another modal windows
                    // (e.g. password dialog)
                    // was opend while showing the busy cursor.
                    if ( PlatformUI.getWorkbench().getDisplay().getActiveShell() == getParentShell() )
                    {
                        open();
                        dialogOpened = true;
                    }
                }
            }


            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.core.runtime.IProgressMonitor#done()
             */
            public void done()
            {
                superMonitor.done();
                checkTicking();
            }


            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.core.runtime.IProgressMonitor#internalWorked(double)
             */
            public void internalWorked( double work )
            {
                superMonitor.internalWorked( work );
                checkTicking();
            }


            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.core.runtime.IProgressMonitor#isCanceled()
             */
            public boolean isCanceled()
            {
                return superMonitor.isCanceled();
            }


            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.core.runtime.IProgressMonitor#setCanceled(boolean)
             */
            public void setCanceled( boolean value )
            {
                superMonitor.setCanceled( value );

            }


            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.core.runtime.IProgressMonitor#setTaskName(java.lang.String)
             */
            public void setTaskName( String name )
            {
                superMonitor.setTaskName( name );
                checkTicking();

            }


            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.core.runtime.IProgressMonitor#subTask(java.lang.String)
             */
            public void subTask( String name )
            {
                superMonitor.subTask( name );
                checkTicking();
            }


            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.core.runtime.IProgressMonitor#worked(int)
             */
            public void worked( int work )
            {
                superMonitor.worked( work );
                checkTicking();

            }
        };
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.ProgressMonitorDialog#getProgressMonitor()
     */
    public IProgressMonitor getProgressMonitor()
    {
        if ( wrapperedMonitor == null )
            createWrapperedMonitor();
        return wrapperedMonitor;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.operations.IRunnableContext#run(boolean,
     *      boolean, IRunnableWithProgress)
     */
    public void run( final boolean fork, final boolean cancelable, final IRunnableWithProgress runnable )
        throws InvocationTargetException, InterruptedException
    {
        final InvocationTargetException[] invokes = new InvocationTargetException[1];
        final InterruptedException[] interrupt = new InterruptedException[1];
        Runnable dialogWaitRunnable = new Runnable()
        {
            public void run()
            {
                try
                {
                    TimeTriggeredProgressMonitorDialog.super.run( fork, cancelable, runnable );
                }
                catch ( InvocationTargetException e )
                {
                    invokes[0] = e;
                }
                catch ( InterruptedException e )
                {
                    interrupt[0] = e;
                }
            }
        };
        final Display display = PlatformUI.getWorkbench().getDisplay();
        if ( display == null )
            return;
        // show a busy cursor until the dialog opens
        BusyIndicator.showWhile( display, dialogWaitRunnable );
        if ( invokes[0] != null )
        {
            throw invokes[0];
        }
        if ( interrupt[0] != null )
        {
            throw interrupt[0];
        }
    }

}