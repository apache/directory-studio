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

package org.apache.directory.studio.actions;


import java.net.MalformedURLException;
import java.net.URL;

import org.apache.directory.studio.Messages;
import org.apache.directory.studio.PluginConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;


/**
 * The Action is used to open a browser that displays to page for opening a new Jira
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ReportABugAction extends Action implements IWorkbenchWindowActionDelegate
{

    /** The workbench window */
    private IWorkbenchWindow workbenchWindow;


    /**
     * Creates a new instance of ReportABugAction.
     */
    public ReportABugAction()
    {
        setId( PluginConstants.ACTION_REPORT_A_BUG_ID ); //$NON-NLS-1$
        setText( Messages.getString( "ReportABugAction.Report_a_bug" ) ); //$NON-NLS-1$
        setToolTipText( Messages.getString( "ReportABugAction.Open_a_web_browser" ) ); //$NON-NLS-1$
        setEnabled( true );
    }


    /**
     * Creates a new instance of ReportABugAction.
     *
     * @param window the workbench window
     */
    public ReportABugAction( IWorkbenchWindow window )
    {
        this();
        init( window );
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        workbenchWindow = null;
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbenchWindow window )
    {
        workbenchWindow = window;
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
    public void selectionChanged( IAction action, ISelection selection )
    {
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        try
        {
            workbenchWindow.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(
                new URL( Messages.getString( "ReportABugAction.JIRA_URL" ) ) ); //$NON-NLS-1$
        }
        catch ( PartInitException e )
        {
        }
        catch ( MalformedURLException e )
        {
        }
    }

}