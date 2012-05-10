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

package org.apache.directory.studio;


import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;


/**
 * This class controls all aspects of the application's execution
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class Application implements IApplication
{
    /** The plugin ID */
    public static final String PLUGIN_ID = "org.apache.directory.studio.rcp"; //$NON-NLS-1$


    /**
     * {@inheritDoc}
     */
    public Object start( IApplicationContext context ) throws Exception
    {
        Display display = PlatformUI.createDisplay();

        try
        {
            int returnCode = PlatformUI.createAndRunWorkbench( display, new ApplicationWorkbenchAdvisor() );
            if ( returnCode == PlatformUI.RETURN_RESTART )
            {
                return IApplication.EXIT_RESTART;
            }
            else
            {
                return IApplication.EXIT_OK;
            }
        }
        finally
        {
            display.dispose();
        }
    }


    /**
     * {@inheritDoc}
     */
    public void stop()
    {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        if ( workbench == null )
        {
            return;
        }
        final Display display = workbench.getDisplay();
        display.syncExec( new Runnable()
        {
            public void run()
            {
                if ( !display.isDisposed() )
                {
                    workbench.close();
                }
            }
        } );
    }
}
