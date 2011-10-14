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
package org.apache.directory.studio.ldapservers.actions;


import org.apache.directory.studio.ldapservers.LdapServersPluginConstants;
import org.apache.directory.studio.ldapservers.model.LdapServer;
import org.apache.directory.studio.ldapservers.views.ServersView;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.dialogs.PreferencesUtil;


/**
 * This class implements the properties action for a server.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PropertiesAction extends Action implements IWorkbenchWindowActionDelegate
{
    /** The associated view */
    private ServersView view;


    /**
     * Creates a new instance of PropertiesAction.
     */
    public PropertiesAction()
    {
        super( Messages.getString( "PropertiesAction.Properties" ) ); //$NON-NLS-1$
        init();
    }


    /**
     * Creates a new instance of PropertiesAction.
     * 
     * @param view
     *      the associated view
     */
    public PropertiesAction( ServersView view )
    {
        super( Messages.getString( "PropertiesAction.Properties" ) ); //$NON-NLS-1$
        this.view = view;
        init();
    }


    /**
     * Initializes the action.
     */
    private void init()
    {
        setId( LdapServersPluginConstants.CMD_PROPERTIES );
        setActionDefinitionId( LdapServersPluginConstants.CMD_PROPERTIES );
        setToolTipText( Messages.getString( "PropertiesAction.PropertiesToolTip" ) ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        if ( view != null )
        {
            StructuredSelection selection = ( StructuredSelection ) view.getViewer().getSelection();
            if ( !selection.isEmpty() )
            {
                LdapServer server = ( LdapServer ) selection.getFirstElement();
                PreferenceDialog dialog = PreferencesUtil.createPropertyDialogOn( view.getViewSite().getShell(),
                    server, LdapServersPluginConstants.PROP_SERVER_PROPERTY_PAGE, null, null );
                dialog.getShell().setText( NLS.bind( Messages.getString( "PropertiesAction.PropertiesFor" ), //$NON-NLS-1$
                    shorten( server.getName(), 30 ) ) );
                dialog.open();
            }
        }
    }


    /**
     * Shortens the given label to the given maximum length
     * and filters non-printable characters.
     * 
     * @param label the label
     * @param maxLength the max length
     * 
     * @return the shortened label
     */
    public static String shorten( String label, int maxLength )
    {
        if ( label == null )
        {
            return null;
        }

        // shorten label
        if ( maxLength < 3 )
        {
            return "..."; //$NON-NLS-1$
        }
        if ( label.length() > maxLength )
        {
            label = label.substring( 0, maxLength / 2 ) + "..." //$NON-NLS-1$
                + label.substring( label.length() - maxLength / 2, label.length() );

        }

        // filter non-printable characters
        StringBuffer sb = new StringBuffer( maxLength + 3 );
        for ( int i = 0; i < label.length(); i++ )
        {
            char c = label.charAt( i );
            if ( Character.isISOControl( c ) )
            {
                sb.append( '.' );
            }
            else
            {
                sb.append( c );
            }
        }

        return sb.toString();
    }


    /**
     * {@inheritDoc}
     */
    public void run( IAction action )
    {
        run();
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        // Nothing to do
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbenchWindow window )
    {
        // Nothing to do
    }


    /**
     * {@inheritDoc}
     */
    public void selectionChanged( IAction action, ISelection selection )
    {
        // Nothing to do
    }
}
