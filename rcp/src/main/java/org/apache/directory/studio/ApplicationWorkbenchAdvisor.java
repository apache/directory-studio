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


import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;


/**
 * This workbench advisor creates the window advisor, and specifies
 * the perspective id for the initial window.<br />
 * <br />
 * - initialize  		Called first to perform any setup such as parsing the command 
 * line, registering adapters, declaring images, etc..  	IWorkbenchConfigurer<br />
 * - preStartup 		Called after initialization but before the first window is opened. 
 * May be used to set options affecting which editors and views are initially opened. <br />	 
 * - postStartup 		Called after all windows have been opened or restored, but before 
 * the event loop starts. It can be used to start automatic processes and to open tips or 
 * other windows.<br /> 	 
 * - preShutdown 		Called after the event loop has terminated but before any windows 
 * have been closed. 	 <br />
 * - postShutdown 	Called after all windows are closed during Workbench shutdown. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor
{
    /**
     * Performs arbitrary initialization before the workbench starts running.<br />
     * <br />
     * This method is called during workbench initialization prior to any windows 
     * being opened. Clients must not call this method directly (although super calls 
     * are okay). The default implementation does nothing. Subclasses may override. 
     * Typical clients will use the configurer passed in to tweak the workbench. If 
     * further tweaking is required in the future, the configurer may be obtained using 
     * getWorkbenchConfigurer
     */
    public void initialize( IWorkbenchConfigurer configurer )
    {
        //enable the save/restore windows size & position feature
        configurer.setSaveAndRestore( true );

        //enable help button in dialogs 
        TrayDialog.setDialogHelpAvailable( true );
        ImageRegistry reg = JFaceResources.getImageRegistry();
        ImageDescriptor helpImage = PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
            ISharedImages.IMG_LCL_LINKTO_HELP );
        reg.put( Dialog.DLG_IMG_HELP, helpImage );
    }


    /**
     * Creates a new workbench window advisor for configuring a new workbench 
     * window via the given workbench window configurer. Clients should override 
     * to provide their own window configurer. This method replaces all the other 
     * window and action bar lifecycle methods on the workbench advisor.
     */
    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor( IWorkbenchWindowConfigurer configurer )
    {
        return new ApplicationWorkbenchWindowAdvisor( configurer );
    }


    /**
     * Returns the id of the perspective to use for the initial workbench window, 
     * or null if no initial perspective should be shown in the initial workbench 
     * window.<br />
     * <br />
     * This method is called during startup when the workbench is creating the first 
     * new window. Subclasses must implement.<br />
     * <br />
     * If the IWorkbenchPreferenceConstants.DEFAULT_PERSPECTIVE_ID preference is 
     * specified, it supercedes the perspective specified here. 
     */
    public String getInitialWindowPerspectiveId()
    {
        return "org.apache.directory.studio.ldapbrowser.ui.perspective.BrowserPerspective"; //$NON-NLS-1$
    }


    /**
     * Performs arbitrary finalization before the workbench is about to shut down.<br />
     * <br />
     * This method is called immediately prior to workbench shutdown before any 
     * windows have been closed. Clients must not call this method directly (although 
     * super calls are okay). The default implementation returns true. Subclasses may 
     * override. 
     */
    public boolean preShutdown()
    {
        return true;
    }


    @Override
    public void postStartup()
    {
        super.postStartup();
        activateProxyService();
    }


    /**
     * Activate the proxy service, this sets the relevant system properties.
     */
    private void activateProxyService()
    {
        Bundle bundle = Platform.getBundle( "org.eclipse.ui.ide" ); //$NON-NLS-1$
        if ( bundle != null )
        {
            ServiceReference ref = bundle.getBundleContext().getServiceReference( IProxyService.class.getName() );
            if ( ref != null )
            {
                bundle.getBundleContext().getService( ref );
            }
        }
    }
}
