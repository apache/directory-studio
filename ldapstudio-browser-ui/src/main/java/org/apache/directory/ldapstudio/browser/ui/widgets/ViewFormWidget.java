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

package org.apache.directory.ldapstudio.browser.ui.widgets;


import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;


public abstract class ViewFormWidget
{

    protected ViewForm control;

    protected Text infoText;

    protected ToolBar actionToolBar;

    protected IToolBarManager actionToolBarManager;

    protected ToolBar menuToolBar;

    protected MenuManager menuManager;

    protected MenuManager contextMenuManager;


    public void createWidget( Composite parent )
    {

        control = new ViewForm( parent, SWT.NONE );
        // control.marginWidth = 0;
        // control.marginHeight = 0;
        // control.horizontalSpacing = 0;
        // control.verticalSpacing = 0;
        control.setLayoutData( new GridData( GridData.FILL_BOTH ) );

        // infoText = BaseWidgetUtils.createLabeledText(control, "", 1);
        Composite infoTextControl = BaseWidgetUtils.createColumnContainer( control, 1, 1 );
        infoTextControl.setLayoutData( new GridData( GridData.FILL_BOTH ) );
        infoText = BaseWidgetUtils.createLabeledText( infoTextControl, "", 1 );
        infoText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, true ) );
        control.setTopLeft( infoTextControl );

        // tool bar
        actionToolBar = new ToolBar( control, SWT.FLAT | SWT.RIGHT );
        actionToolBar.setLayoutData( new GridData( SWT.END, SWT.NONE, true, false ) );
        actionToolBarManager = new ToolBarManager( actionToolBar );
        control.setTopCenter( actionToolBar );

        // local menu
        this.menuManager = new MenuManager();
        menuToolBar = new ToolBar( control, SWT.FLAT | SWT.RIGHT );
        ToolItem ti = new ToolItem( menuToolBar, SWT.PUSH, 0 );
        ti.setImage( BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_PULLDOWN ) );
        ti.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                showViewMenu();
            }
        } );
        control.setTopRight( menuToolBar );

        // content
        Composite composite = BaseWidgetUtils.createColumnContainer( control, 1, 1 );
        GridLayout gl = new GridLayout();
        gl.horizontalSpacing = 0;
        gl.verticalSpacing = 0;
        gl.marginHeight = 0;
        gl.marginWidth = 0;
        composite.setLayout( gl );
        Control childControl = this.createContent( composite );
        control.setContent( composite );

        // context menu
        this.contextMenuManager = new MenuManager();
        Menu menu = this.contextMenuManager.createContextMenu( childControl );
        childControl.setMenu( menu );
    }


    protected abstract Control createContent( Composite control );


    private void showViewMenu()
    {
        Menu aMenu = menuManager.createContextMenu( control );
        Point topLeft = new Point( 0, 0 );
        topLeft.y += menuToolBar.getBounds().height;
        topLeft = menuToolBar.toDisplay( topLeft );
        aMenu.setLocation( topLeft.x, topLeft.y );
        aMenu.setVisible( true );
    }


    public void dispose()
    {
        if ( this.control != null )
        {

            if ( this.contextMenuManager != null )
            {
                this.contextMenuManager.removeAll();
                this.contextMenuManager.dispose();
                this.contextMenuManager = null;
            }
            if ( this.menuToolBar != null )
            {
                this.menuToolBar.dispose();
                this.menuToolBar = null;
                this.menuManager.dispose();
                this.menuManager = null;
            }
            if ( this.actionToolBar != null )
            {
                this.actionToolBar.dispose();
                this.actionToolBar = null;
                this.actionToolBarManager.removeAll();
                this.actionToolBarManager = null;
            }

            if ( this.infoText != null )
            {
                this.infoText.dispose();
                this.infoText = null;
            }

            this.control.dispose();
            this.control = null;
        }
    }


    public Text getInfoText()
    {
        return infoText;
    }


    public IToolBarManager getToolBarManager()
    {
        return this.actionToolBarManager;
    }


    public IMenuManager getMenuManager()
    {
        return menuManager;
    }


    public IMenuManager getContextMenuManager()
    {
        return this.contextMenuManager;
    }

}
