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

package org.apache.directory.ldapstudio.browser.ui.views.modificationlogs;


import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.widgets.ldifeditor.LdifEditorWidget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;


public class ModificationLogsView extends ViewPart
{

    private ModificationLogsViewActionGroup actionGroup;

    private LdifEditorWidget mainWidget;

    private ModificationLogsViewUniversalListener universalListener;


    public static String getId()
    {
        return ModificationLogsView.class.getName();
    }


    public ModificationLogsView()
    {
        super();
    }


    public void setFocus()
    {

    }


    public void dispose()
    {
        if ( this.mainWidget != null )
        {
            this.actionGroup.dispose();
            this.actionGroup = null;
            this.universalListener.dispose();
            this.universalListener = null;
            this.mainWidget.dispose();
            this.mainWidget = null;
        }
        super.dispose();
    }


    public void createPartControl( Composite parent )
    {

        Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        composite.setLayout( layout );

        // create main widget
        mainWidget = new LdifEditorWidget( null, "", false );
        mainWidget.createWidget( composite );
        mainWidget.getSourceViewer().setEditable( false );

        // create actions and context menu (and register global actions)
        this.actionGroup = new ModificationLogsViewActionGroup( this );
        this.actionGroup.fillActionBars( getViewSite().getActionBars() );
        // this.actionGroup.fillContextMenu(this.configuration.getContextMenuManager(this.mainWidget.getViewer()));

        // create the listener
        this.universalListener = new ModificationLogsViewUniversalListener( this );

        // set help context
        PlatformUI.getWorkbench().getHelpSystem().setHelp( mainWidget.getSourceViewer().getTextWidget(),
            BrowserUIPlugin.PLUGIN_ID + "." + "tools_modification_logs_view" );
    }


    public LdifEditorWidget getMainWidget()
    {
        return mainWidget;
    }


    public static void setInput( IConnection connection )
    {
        try
        {
            String targetId = ModificationLogsView.getId();
            IViewPart targetView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
                targetId );

            if ( targetView == null && connection != null )
            {
                try
                {
                    targetView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
                        targetId, null, IWorkbenchPage.VIEW_VISIBLE );
                }
                catch ( PartInitException e )
                {
                }
            }

            try
            {
                targetView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView( targetId,
                    null, IWorkbenchPage.VIEW_VISIBLE );
            }
            catch ( PartInitException e )
            {
            }

            // set input
            if ( targetView != null && targetView instanceof ModificationLogsView )
            {
                ModificationLogsViewInput input = new ModificationLogsViewInput( connection, 0 );
                ( ( ModificationLogsView ) targetView ).universalListener.setInput( input );
                ( ( ModificationLogsView ) targetView ).universalListener.scrollToNewest();
            }
        }
        catch ( NullPointerException npe )
        {
        }
    }


    public ModificationLogsViewUniversalListener getUniversalListener()
    {
        return universalListener;
    }


    public ModificationLogsViewActionGroup getActionGroup()
    {
        return actionGroup;
    }

}
