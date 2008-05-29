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
package org.apache.directory.studio.apacheds.actions;


import org.apache.directory.studio.apacheds.ApacheDsPluginConstants;
import org.apache.directory.studio.apacheds.model.Server;
import org.apache.directory.studio.apacheds.properties.ServerPropertyPage;
import org.apache.directory.studio.apacheds.views.ServersView;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.dialogs.PreferencesUtil;


/**
 * This class implements the properties action for a server.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class PropertiesAction extends Action implements IWorkbenchWindowActionDelegate
{
    /** The associated view */
    private ServersView view;


    /**
     * Creates a new instance of PropertiesAction.
     * 
     * @param view
     *      the associated view
     */
    public PropertiesAction( ServersView view )
    {
        super( "&Properties" );
        this.view = view;
        setId( ApacheDsPluginConstants.CMD_PROPERTIES );
        setActionDefinitionId( ApacheDsPluginConstants.CMD_PROPERTIES );
        setToolTipText( "Properties" );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run()
    {
        StructuredSelection selection = ( StructuredSelection ) view.getViewer().getSelection();
        if ( !selection.isEmpty() )
        {
            Server server = ( Server ) selection.getFirstElement();
            PreferenceDialog dialog = PreferencesUtil.createPropertyDialogOn( view.getViewSite().getShell(), server,
                ServerPropertyPage.ID, null, null );
            dialog.getShell().setText( "Properties for '" + shorten( server.getName(), 30 ) + "'" );
            dialog.open();
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
            return "...";
        }
        if ( label.length() > maxLength )
        {
            label = label.substring( 0, maxLength / 2 ) + "..."
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


    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run( IAction action )
    {
        run();
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
     */
    public void dispose()
    {
        // Nothing to do
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init( IWorkbenchWindow window )
    {
        // Nothing to do
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged( IAction action, ISelection selection )
    {
        // Nothing to do
    }
}
