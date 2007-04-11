package org.apache.directory.ldapstudio.apacheds.configuration.actions;


import org.apache.directory.ldapstudio.apacheds.configuration.editor.ServerConfigurationEditor;
import org.apache.directory.ldapstudio.apacheds.configuration.editor.ServerConfigurationEditorInput;
import org.apache.directory.ldapstudio.apacheds.configuration.model.ExtendedOperation;
import org.apache.directory.ldapstudio.apacheds.configuration.model.IndexedAttribute;
import org.apache.directory.ldapstudio.apacheds.configuration.model.Interceptor;
import org.apache.directory.ldapstudio.apacheds.configuration.model.Partition;
import org.apache.directory.ldapstudio.apacheds.configuration.model.ServerConfiguration;
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
        ServerConfiguration serverConfiguration = new ServerConfiguration();
        serverConfiguration.setAllowAnonymousAccess( true );
        serverConfiguration.setEnableAccessControl( true );
        serverConfiguration.setEnableChangePassword( true );
        serverConfiguration.setEnableKerberos( true );
        serverConfiguration.setEnableNTP( true );
        serverConfiguration.setMaxSizeLimit( 10 );
        serverConfiguration.setMaxThreads( 20 );
        serverConfiguration.setMaxTimeLimit( 30 );
        serverConfiguration.setPassword( "secret" );
        serverConfiguration.setPath( "/usr/local/apacheds-1.5.0/conf/server.xml" );
        serverConfiguration.setPort( 10389 );
        serverConfiguration.setPrincipal( "uid=admin,ou=system" );
        serverConfiguration.setSynchronizationPeriod( 40 );

        serverConfiguration.addExtendedOperation( new ExtendedOperation(
            "org.apache.directory.server.ldap.support.extended.GracefulShutdownHandler" ) );
        serverConfiguration.addExtendedOperation( new ExtendedOperation(
            "org.apache.directory.server.ldap.support.extended.LaunchDiagnosticUiHandler" ) );

        serverConfiguration.addInterceptor( new Interceptor( "NormalizationService" ) );
        serverConfiguration.addInterceptor( new Interceptor( "AuthenticationService" ) );
        serverConfiguration.addInterceptor( new Interceptor( "ReferalService" ) );
        serverConfiguration.addInterceptor( new Interceptor( "AuthorizationService" ) );

        Partition partition = new Partition( "System Partition" );
        partition.setSuffix( "ou=system" );
        // TODO Add Context Entry
        partition.setEnableOptimizer( true );
        partition.setCacheSize( 1000 );
        partition.setSynchronizationOnWrite( true );
        partition.addIndexedAttribute( new IndexedAttribute( "cn", 100 ) );
        partition.addIndexedAttribute( new IndexedAttribute( "ou", 30 ) );
        serverConfiguration.addPartition( partition );

        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        try
        {
            page.openEditor( new ServerConfigurationEditorInput( serverConfiguration ), ServerConfigurationEditor.ID );
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
