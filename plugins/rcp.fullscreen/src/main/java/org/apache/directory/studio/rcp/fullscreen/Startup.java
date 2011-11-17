package org.apache.directory.studio.rcp.fullscreen;


import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;


public class Startup implements Runnable, IStartup
{

    private IWorkbenchWindow[] windows;


    @Override
    public void earlyStartup()
    {
        IWorkbench workbench = PlatformUI.getWorkbench();
        windows = workbench.getWorkbenchWindows();
        workbench.getDisplay().asyncExec( this );
    }


    @Override
    public void run()
    {
        for ( int i = 0; i < windows.length; i++ )
        {
            IWorkbenchWindow iWorkbenchWindow = windows[i];
            Activator.setWindowFullscreen( iWorkbenchWindow.getShell() );
        }
    }
}