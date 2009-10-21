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

package org.apache.directory.studio.ldapbrowser.ui.views.modificationlogs;


import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldifeditor.widgets.LdifEditorWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;


/**
 * The ModificationLogsView displays all modifications applied 
 * to a connection using LDIF change format.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ModificationLogsView extends ViewPart
{

    /** The action group. */
    private ModificationLogsViewActionGroup actionGroup;

    /** The main widget. */
    private LdifEditorWidget mainWidget;

    /** The universal listener. */
    private ModificationLogsViewUniversalListener universalListener;


    /**
     * Gets the id.
     * 
     * @return the id
     */
    public static String getId()
    {
        return BrowserUIConstants.VIEW_MODIFICATION_LOGS_VIEW;
    }


    /**
     * Creates a new instance of ModificationLogsView.
     */
    public ModificationLogsView()
    {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public void setFocus()
    {
        mainWidget.getSourceViewer().getTextWidget().setFocus();
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        if ( mainWidget != null )
        {
            actionGroup.dispose();
            actionGroup = null;
            universalListener.dispose();
            universalListener = null;
            mainWidget.dispose();
            mainWidget = null;
        }
        super.dispose();
    }


    /**
     * {@inheritDoc}
     */
    public void createPartControl( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        composite.setLayout( layout );

        // create main widget
        mainWidget = new LdifEditorWidget( null, "", false ); //$NON-NLS-1$
        mainWidget.createWidget( composite );
        mainWidget.getSourceViewer().setEditable( false );

        // create actions and context menu (and register global actions)
        actionGroup = new ModificationLogsViewActionGroup( this );
        actionGroup.fillActionBars( getViewSite().getActionBars() );
        // this.actionGroup.fillContextMenu(this.configuration.getContextMenuManager(this.mainWidget.getViewer()));

        // create the listener
        universalListener = new ModificationLogsViewUniversalListener( this );

        // set help context
        PlatformUI.getWorkbench().getHelpSystem().setHelp( mainWidget.getSourceViewer().getTextWidget(),
            BrowserUIConstants.PLUGIN_ID + "." + "tools_modification_logs_view" ); //$NON-NLS-1$ //$NON-NLS-2$
    }


    /**
     * Gets the main widget.
     * 
     * @return the main widget
     */
    public LdifEditorWidget getMainWidget()
    {
        return mainWidget;
    }


    /**
     * Gets the universal listener.
     * 
     * @return the universal listener
     */
    public ModificationLogsViewUniversalListener getUniversalListener()
    {
        return universalListener;
    }


    /**
     * Gets the action group.
     * 
     * @return the action group
     */
    public ModificationLogsViewActionGroup getActionGroup()
    {
        return actionGroup;
    }

}
