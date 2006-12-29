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

package org.apache.directory.ldapstudio;


import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;


/**
 * The workbench window advisor object is created in response to a workbench window 
 * being created (one per window), and is used to configure the window.<br />
 * <br />
 * The following advisor methods are called at strategic points in the workbench window's 
 * lifecycle (as with the workbench advisor, all occur within the dynamic scope of the call 
 * to PlatformUI.createAndRunWorkbench):<br />
 * <br />
 *  - preWindowOpen - called as the window is being opened; use to configure aspects of the 
 *  window other than actions bars<br />
 *  - postWindowRestore - called after the window has been recreated from a previously saved 
 *  state; use to adjust the restored window<br />
 *  - postWindowCreate - called after the window has been created, either from an initial 
 *  state or from a restored state; used to adjust the window<br />
 *  - openIntro - called immediately before the window is opened in order to create the 
 *  introduction component, if any.<br />
 *  - postWindowOpen - called after the window has been opened; use to hook window listeners, 
 *  etc.<br />
 *  - preWindowShellClose - called when the window's shell is closed by the user; use to 
 *  pre-screen window closings
 *  
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor
{

    /**
     * Default constructor
     * @param configurer 
     *          an object for configuring the workbench window
     */
    public ApplicationWorkbenchWindowAdvisor( IWorkbenchWindowConfigurer configurer )
    {
        super( configurer );
    }


    /**
     * Creates a new action bar advisor to configure the action bars of the window via 
     * the given action bar configurer. The default implementation returns a new instance 
     * of ActionBarAdvisor.
     */
    public ActionBarAdvisor createActionBarAdvisor( IActionBarConfigurer configurer )
    {
        return new ApplicationActionBarAdvisor( configurer );
    }


    /**
     * Performs arbitrary actions before the window is opened.<br />
     * <br />
     * This method is called before the window's controls have been created. Clients must 
     * not call this method directly (although super calls are okay). The default 
     * implementation does nothing. Subclasses may override. Typical clients will use the 
     * window configurer to tweak the workbench window in an application-specific way; 
     * however, filling the window's menu bar, tool bar, and status line must be done in 
     * ActionBarAdvisor.fillActionBars, which is called immediately after this method is 
     * called. 
     */
    public void preWindowOpen()
    {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setInitialSize( new Point( 900, 600 ) );
        configurer.setShowCoolBar( true );
        configurer.setShowStatusLine( false );
        configurer.setShowPerspectiveBar( true );
        configurer.setShowProgressIndicator( true );
    }

}
