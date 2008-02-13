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


import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;


/**
 * This class controls all aspects of the application's execution
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class Application implements IPlatformRunnable
{

    public static final String PLUGIN_ID = "org.apache.directory.studio.rcp"; //$NON-NLS-1$
    private static Logger logger = Logger.getLogger( Application.class );


    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IPlatformRunnable#run(java.lang.Object)
     */
    public Object run( Object args ) throws Exception
    {
        //Set up a simple configuration that logs on the console.
        PropertyConfigurator.configure( Platform.getBundle( Application.PLUGIN_ID ).getResource( "log4j.conf" ) ); //$NON-NLS-1$
        logger.info( "Entering Apache Directory Studio." ); //$NON-NLS-1$
        Display display = PlatformUI.createDisplay();
        
        try
        {
            int returnCode = PlatformUI.createAndRunWorkbench( display, new ApplicationWorkbenchAdvisor() );
        
            if ( returnCode == PlatformUI.RETURN_RESTART )
            {
                return IPlatformRunnable.EXIT_RESTART;
            }
            
            return IPlatformRunnable.EXIT_OK;
        }
        finally
        {
            display.dispose();
            logger.info( "Exiting Apache Directory Studio." ); //$NON-NLS-1$
        }
    }
}
