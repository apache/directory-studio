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


/**
 * This class implements the Open Editor Action.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class OpenEditorAction extends Action implements IWorkbenchWindowActionDelegate
{
    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
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


    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged( IAction action, ISelection selection )
    {
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
     */
    public void dispose()
    {
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init( IWorkbenchWindow window )
    {
    }
}
