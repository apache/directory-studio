/*******************************************************************************
 * Copyright (c) 2011, Alex Blewitt.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alex Blewitt - initial API and implementation
 *******************************************************************************/
package org.apache.directory.studio.rcp.fullscreen;


import org.eclipse.swt.internal.cocoa.NSWindow;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * Set all existing windows to have full-screen behaviour, and also permit new
 * windows to be registered with same as well.
 * 
 * @author Alex Blewitt <alex.blewit@gmail.com>
 */
@SuppressWarnings("restriction")
public class Activator extends AbstractUIPlugin implements IWindowListener
{

    public static final String PLUGIN_ID = "org.apache.directory.studio.fullscreen.ui"; //$NON-NLS-1$


    public void start( BundleContext context ) throws Exception
    {
        super.start( context );
        PlatformUI.getWorkbench().addWindowListener( this );
    }


    public void stop( BundleContext context ) throws Exception
    {
        super.stop( context );
    }


    @Override
    public void windowActivated( IWorkbenchWindow window )
    {
    }


    @Override
    public void windowDeactivated( IWorkbenchWindow window )
    {
    }


    @Override
    public void windowClosed( IWorkbenchWindow window )
    {
    }


    @Override
    public void windowOpened( IWorkbenchWindow window )
    {
        Shell shell = window.getShell();
        setWindowFullscreen( shell );
    }


    static void setWindowFullscreen( final Shell shell )
    {
        NSWindow nswindow = shell.view.window();
        nswindow.setToolbar( null );
        SO.Reflect.executeLong( nswindow, "setCollectionBehavior", //$NON-NLS-1$
            new Class[]
                { SO.NSUInteger }, ( int ) ( 1 << 7 ) );
    }
}
