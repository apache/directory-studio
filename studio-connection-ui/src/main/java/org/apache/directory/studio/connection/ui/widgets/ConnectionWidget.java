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

package org.apache.directory.studio.connection.ui.widgets;


import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IActionBars;


/**
 * The ConnectionWidget is a reusable widget that displays all connections
 * in a table viewer. It is used by 
 * org.apache.directory.studio.ldapbrowser.ui.views.connection.ConnectionView, 
 * org.apache.directory.studio.ldapbrowser.common.dialogs.SelectConnectionDialog and 
 * org.apache.directory.studio.ldapbrowser.common.dialogs.SelectReferralConnectionDialog. 
 * 
 * It includes a content and label provider to display connections with a nice icon.
 * 
 * Further is provides a context menu and a local toolbar with actions to
 * add, modify, delete, open and close connections.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ConnectionWidget extends ViewFormWidget
{

    /** The widget's configuration with the content provider, label provider and menu manager */
    private ConnectionConfiguration configuration;

    /** The action bars */
    private IActionBars actionBars;

    /** The table widget used by the table viewer */
    private Table table;

    /** The table viewer */
    private TableViewer viewer;


    /**
     * Creates a new instance of ConnectionWidget.
     *
     * @param configuration the configuration
     * @param actionBars the action bars
     */
    public ConnectionWidget( ConnectionConfiguration configuration, IActionBars actionBars )
    {
        this.configuration = configuration;
        this.actionBars = actionBars;
    }


    /**
     * {@inheritDoc}
     */
    public void createWidget( Composite parent )
    {
        if ( actionBars == null )
        {
            super.createWidget( parent );
        }
        else
        {
            createContent( parent );
        }
    }


    /**
     * {@inheritDoc}
     */
    public IToolBarManager getToolBarManager()
    {
        if ( actionBars == null )
        {
            return super.getToolBarManager();
        }
        else
        {
            return actionBars.getToolBarManager();
        }
    }


    /**
     * {@inheritDoc}
     */
    public IMenuManager getMenuManager()
    {
        if ( actionBars == null )
        {
            return super.getMenuManager();

        }
        else
        {
            return actionBars.getMenuManager();
        }
    }


    /**
     * {@inheritDoc}
     */
    public IMenuManager getContextMenuManager()
    {
        if ( actionBars == null )
        {
            return super.getContextMenuManager();
        }
        else
        {
            return configuration.getContextMenuManager( viewer );
        }
    }


    /**
     * {@inheritDoc}
     */
    protected Control createContent( Composite parent )
    {

        table = new Table( parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
        GridData data = new GridData( GridData.FILL_BOTH );
        data.widthHint = 450;
        data.heightHint = 250;
        table.setLayoutData( data );
        viewer = new TableViewer( table );

        // setup providers
        viewer.setContentProvider( configuration.getContentProvider( viewer ) );
        viewer.setLabelProvider( configuration.getLabelProvider( viewer ) );

        return table;
    }


    /**
     * Sets the input to the table viewer.
     *
     * @param input the input
     */
    public void setInput( Object input )
    {
        viewer.setInput( input );
    }


    /**
     * Sets focus to the table viewer.
     */
    public void setFocus()
    {
        viewer.getTable().setFocus();
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        if ( viewer != null )
        {
            configuration.dispose();
            configuration = null;

            table.dispose();
            table = null;
            viewer = null;
        }
    }


    /**
     * Gets the table viewer.
     * 
     * @return the table viewer
     */
    public TableViewer getViewer()
    {
        return viewer;
    }

}
