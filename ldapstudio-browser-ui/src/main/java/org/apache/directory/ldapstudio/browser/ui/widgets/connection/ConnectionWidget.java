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

package org.apache.directory.ldapstudio.browser.ui.widgets.connection;


import org.apache.directory.ldapstudio.browser.ui.widgets.ViewFormWidget;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IActionBars;


public class ConnectionWidget extends ViewFormWidget
{

    private ConnectionConfiguration configuration;

    private IActionBars actionBars;

    private Table table;

    private TableViewer viewer;


    public ConnectionWidget( ConnectionConfiguration configuration, IActionBars actionBars )
    {
        this.configuration = configuration;
        this.actionBars = actionBars;
    }


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


    protected Control createContent( Composite parent )
    {

        this.table = new Table( parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
        GridData data = new GridData( GridData.FILL_BOTH );
        data.widthHint = 450;
        data.heightHint = 250;
        this.table.setLayoutData( data );
        this.viewer = new TableViewer( this.table );

        // setup providers
        this.viewer.setContentProvider( configuration.getContentProvider( this.viewer ) );
        this.viewer.setLabelProvider( configuration.getLabelProvider( this.viewer ) );

        return this.table;
    }


    public void setInput( Object input )
    {
        this.viewer.setInput( input );
    }


    public void setFocus()
    {
        this.viewer.getTable().setFocus();
    }


    public void dispose()
    {
        if ( this.viewer != null )
        {
            this.configuration.dispose();
            this.configuration = null;

            this.table.dispose();
            this.table = null;
            this.viewer = null;
        }
    }


    public TableViewer getViewer()
    {
        return viewer;
    }

}
