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

package org.apache.directory.ldapstudio.browser.ui.widgets.browser;


import org.apache.directory.ldapstudio.browser.ui.widgets.ViewFormWidget;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;


public class BrowserWidget extends ViewFormWidget
{

    private BrowserConfiguration configuration;

    private IActionBars actionBars;

    private Tree tree;

    private TreeViewer viewer;


    public BrowserWidget( BrowserConfiguration configuration, IActionBars actionBars )
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

        // create tree widget and viewer
        this.tree = new Tree( parent, SWT.VIRTUAL | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
        GridData data = new GridData( GridData.FILL_BOTH );
        data.widthHint = 450;
        data.heightHint = 250;
        this.tree.setLayoutData( data );
        this.viewer = new TreeViewer( this.tree );
        this.viewer.setUseHashlookup( true );

        // setup sorter, filter and layout
        this.configuration.getSorter().connect( this.viewer );
        this.configuration.getPreferences().connect( this.viewer );

        // setup providers
        this.viewer.setContentProvider( configuration.getContentProvider( this.viewer ) );
        this.viewer.setLabelProvider( configuration.getLabelProvider( this.viewer ) );

        return this.tree;
    }


    public void setInput( Object input )
    {
        this.viewer.setInput( input );
    }


    public void setFocus()
    {
        this.viewer.getTree().setFocus();
    }


    public void dispose()
    {
        if ( this.viewer != null )
        {
            this.configuration.dispose();
            this.configuration = null;

            this.tree.dispose();
            this.tree = null;
            this.viewer = null;
        }
    }


    public TreeViewer getViewer()
    {
        return viewer;
    }

}
