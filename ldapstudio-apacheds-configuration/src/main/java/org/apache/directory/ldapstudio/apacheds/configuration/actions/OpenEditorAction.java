package org.apache.directory.ldapstudio.apacheds.configuration.actions;


import org.apache.directory.ldapstudio.apacheds.configuration.editor.ServerConfigurationEditor;
import org.apache.directory.ldapstudio.apacheds.configuration.editor.ServerConfigurationEditorInput;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


public class OpenEditorAction extends Action implements IWorkbenchWindowActionDelegate
{

    public void dispose()
    {
    }


    public void init( IWorkbenchWindow window )
    {
    }


    public void run( IAction action )
    {
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        try
        {
            page.openEditor( new ServerConfigurationEditorInput(), ServerConfigurationEditor.ID );
        }
        catch ( PartInitException e )
        {
            // TODO ADD A LOGGER
            e.printStackTrace();
        }
    }


    public void selectionChanged( IAction action, ISelection selection )
    {
    }
}
