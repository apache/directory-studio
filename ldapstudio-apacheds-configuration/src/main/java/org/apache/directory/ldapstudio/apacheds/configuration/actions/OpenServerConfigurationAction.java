package org.apache.directory.ldapstudio.apacheds.configuration.actions;


import org.apache.directory.ldapstudio.apacheds.configuration.Activator;
import org.apache.directory.ldapstudio.apacheds.configuration.editor.ServerConfigurationEditor;
import org.apache.directory.ldapstudio.apacheds.configuration.editor.ServerConfigurationEditorInput;
import org.apache.directory.ldapstudio.apacheds.configuration.model.ServerConfiguration;
import org.apache.directory.ldapstudio.apacheds.configuration.model.ServerConfigurationParser;
import org.apache.directory.ldapstudio.apacheds.configuration.model.ServerConfigurationParserException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
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
public class OpenServerConfigurationAction extends Action implements IWorkbenchWindowActionDelegate
{
    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run( IAction action )
    {
        FileDialog fd = new FileDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OPEN );
        fd.setText( "Open a Server Configuration file" );
        fd.setFilterExtensions( new String[]
            { "*.xml", "*.*" } );
        fd.setFilterNames( new String[]
            { "XML files", "All files" } );
        String selectedFile = fd.open();
        // selected == null if 'cancel' has been pushed
        if ( selectedFile == null || "".equals( selectedFile ) )
        {
            return;
        }

        ServerConfigurationParser parser = new ServerConfigurationParser();
        ServerConfiguration serverConfiguration = null;

        try
        {
            serverConfiguration = parser.parse( selectedFile );
        }
        catch ( ServerConfigurationParserException e )
        {
            MessageBox messageBox = new MessageBox( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                SWT.OK | SWT.ICON_ERROR );
            messageBox.setText( "Error!" );
            messageBox.setMessage( "An error occurred when reading the file." + "\n" + e.getMessage() );
            messageBox.open();
        }

        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        try
        {
            page.openEditor( new ServerConfigurationEditorInput( serverConfiguration ), ServerConfigurationEditor.ID );
        }
        catch ( PartInitException e )
        {
            Activator.getDefault().getLog().log(
                new Status( Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e.getCause() ) );
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
