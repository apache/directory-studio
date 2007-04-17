package org.apache.directory.ldapstudio.apacheds.configuration.actions;


import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

import org.apache.directory.ldapstudio.apacheds.configuration.Activator;
import org.apache.directory.ldapstudio.apacheds.configuration.editor.ServerConfigurationEditor;
import org.apache.directory.ldapstudio.apacheds.configuration.editor.ServerConfigurationEditorInput;
import org.apache.directory.ldapstudio.apacheds.configuration.model.ExtendedOperation;
import org.apache.directory.ldapstudio.apacheds.configuration.model.IndexedAttribute;
import org.apache.directory.ldapstudio.apacheds.configuration.model.Interceptor;
import org.apache.directory.ldapstudio.apacheds.configuration.model.Partition;
import org.apache.directory.ldapstudio.apacheds.configuration.model.ServerConfiguration;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


/**
 * This class implements the New Server Configuration Action.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewServerConfigurationAction extends Action implements IWorkbenchWindowActionDelegate
{
    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run( IAction action )
    {
        // Creating the Default ServerConfiguration
        ServerConfiguration serverConfiguration = new ServerConfiguration();

        // General Settings
        serverConfiguration.setAllowAnonymousAccess( false );
        serverConfiguration.setEnableAccessControl( false );
        serverConfiguration.setEnableChangePassword( false );
        serverConfiguration.setEnableKerberos( false );
        serverConfiguration.setEnableNTP( false );
        serverConfiguration.setMaxSizeLimit( 1000 );
        serverConfiguration.setMaxThreads( 8 );
        serverConfiguration.setMaxTimeLimit( 15000 );
        serverConfiguration.setPassword( "secret" );
        serverConfiguration.setPort( 10389 );
        serverConfiguration.setPrincipal( "uid=admin,ou=system" );
        serverConfiguration.setSynchronizationPeriod( 15000 );

        // System Partition
        Partition partition = new Partition( "system" );
        partition.setSuffix( "ou=system" );
        Attributes attributes = new BasicAttributes( true );
        Attribute attribute = new BasicAttribute( "objectClass" );
        attribute.add( "top" );
        attribute.add( "organizationalUnit" );
        attribute.add( "extensibleObject" );
        attributes.put( attribute );
        attributes.put( new BasicAttribute( "ou", "system" ) );
        partition.setContextEntry( attributes );
        partition.setEnableOptimizer( true );
        partition.setCacheSize( 100 );
        partition.setSynchronizationOnWrite( true );
        partition.addIndexedAttribute( new IndexedAttribute( "1.3.6.1.4.1.18060.0.4.1.2.1", 100 ) );
        partition.addIndexedAttribute( new IndexedAttribute( "1.3.6.1.4.1.18060.0.4.1.2.2", 100 ) );
        partition.addIndexedAttribute( new IndexedAttribute( "1.3.6.1.4.1.18060.0.4.1.2.3", 100 ) );
        partition.addIndexedAttribute( new IndexedAttribute( "1.3.6.1.4.1.18060.0.4.1.2.4", 100 ) );
        partition.addIndexedAttribute( new IndexedAttribute( "1.3.6.1.4.1.18060.0.4.1.2.5", 100 ) );
        partition.addIndexedAttribute( new IndexedAttribute( "1.3.6.1.4.1.18060.0.4.1.2.6", 100 ) );
        partition.addIndexedAttribute( new IndexedAttribute( "1.3.6.1.4.1.18060.0.4.1.2.7", 100 ) );
        partition.addIndexedAttribute( new IndexedAttribute( "ou", 100 ) );
        partition.addIndexedAttribute( new IndexedAttribute( "uid", 100 ) );
        partition.addIndexedAttribute( new IndexedAttribute( "objectClass", 100 ) );
        partition.setSystemPartition( true );
        serverConfiguration.addPartition( partition );

        // Interceptors
        Interceptor interceptor = new Interceptor( "normalizationService" );
        interceptor.setClassType( "org.apache.directory.server.core.normalization.NormalizationService" );
        serverConfiguration.addInterceptor( interceptor );
        interceptor = new Interceptor( "authenticationService" );
        interceptor.setClassType( "org.apache.directory.server.core.authn.AuthenticationService" );
        serverConfiguration.addInterceptor( interceptor );
        interceptor = new Interceptor( "referralService" );
        interceptor.setClassType( "org.apache.directory.server.core.referral.ReferralService" );
        serverConfiguration.addInterceptor( interceptor );
        interceptor = new Interceptor( "authorizationService" );
        interceptor.setClassType( "org.apache.directory.server.core.authz.AuthorizationService" );
        serverConfiguration.addInterceptor( interceptor );
        interceptor = new Interceptor( "defaultAuthorizationService" );
        interceptor.setClassType( "org.apache.directory.server.core.authz.DefaultAuthorizationService" );
        serverConfiguration.addInterceptor( interceptor );
        interceptor = new Interceptor( "exceptionService" );
        interceptor.setClassType( "org.apache.directory.server.core.exception.ExceptionService" );
        serverConfiguration.addInterceptor( interceptor );
        interceptor = new Interceptor( "operationalAttributeService" );
        interceptor.setClassType( "org.apache.directory.server.core.operational.OperationalAttributeService" );
        serverConfiguration.addInterceptor( interceptor );
        interceptor = new Interceptor( "schemaService" );
        interceptor.setClassType( "org.apache.directory.server.core.schema.SchemaService" );
        serverConfiguration.addInterceptor( interceptor );
        interceptor = new Interceptor( "subentryService" );
        interceptor.setClassType( "org.apache.directory.server.core.subtree.SubentryService" );
        serverConfiguration.addInterceptor( interceptor );
        interceptor = new Interceptor( "collectiveAttributeService" );
        interceptor.setClassType( "org.apache.directory.server.core.collective.CollectiveAttributeService" );
        serverConfiguration.addInterceptor( interceptor );
        interceptor = new Interceptor( "eventService" );
        interceptor.setClassType( "org.apache.directory.server.core.event.EventService" );
        serverConfiguration.addInterceptor( interceptor );
        interceptor = new Interceptor( "triggerService" );
        interceptor.setClassType( "org.apache.directory.server.core.trigger.TriggerService" );
        serverConfiguration.addInterceptor( interceptor );

        // Extended Operations
        serverConfiguration.addExtendedOperation( new ExtendedOperation(
            "org.apache.directory.server.ldap.support.extended.GracefulShutdownHandler" ) );
        serverConfiguration.addExtendedOperation( new ExtendedOperation(
            "org.apache.directory.server.ldap.support.extended.LaunchDiagnosticUiHandler" ) );

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
