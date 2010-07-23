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


import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.connection.ui.ConnectionUIConstants;
import org.apache.directory.studio.connection.ui.ConnectionUIPlugin;
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


/**
 * The ViewFormWidget is a widget that provides an info text,
 * a tool bar, a menu and a main content composite including
 * a context menu. 
 * It looks like this:
 * <pre>
 * -----------------------------------
 * | info text     | tool bar | menu |
 * -----------------------------------
 * |                                 |
 * |          main content           |
 * |                                 |
 * -----------------------------------
 * </pre>
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class ViewFormWidget
{

    /** The view form control */
    protected ViewForm control;

    /** The info text, positioned at the top left */
    protected Text infoText;

    /** The action tool bar */
    protected ToolBar actionToolBar;

    /** The action tool bar manager */
    protected IToolBarManager actionToolBarManager;

    /** The menu tool bar. */
    protected ToolBar menuToolBar;

    /** The menu manager. */
    protected MenuManager menuManager;

    /** The context menu manager. */
    protected MenuManager contextMenuManager;


    /**
     * Creates the widget.
     *
     * @param parent the parent composite
     */
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
        infoText = BaseWidgetUtils.createLabeledText( infoTextControl, "", 1 ); //$NON-NLS-1$
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
        ti.setImage( ConnectionUIPlugin.getDefault().getImage( ConnectionUIConstants.IMG_PULLDOWN ) );
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


    /**
     * Creates the content.
     * 
     * @param control the control
     * 
     * @return the control
     */
    protected abstract Control createContent( Composite control );


    /**
     * Shows the local view menu.
     */
    private void showViewMenu()
    {
        Menu aMenu = menuManager.createContextMenu( control );
        Point topLeft = new Point( 0, 0 );
        topLeft.y += menuToolBar.getBounds().height;
        topLeft = menuToolBar.toDisplay( topLeft );
        aMenu.setLocation( topLeft.x, topLeft.y );
        aMenu.setVisible( true );
    }


    /**
     * Disposes this widget.
     */
    public void dispose()
    {
        if ( control != null )
        {

            if ( contextMenuManager != null )
            {
                contextMenuManager.removeAll();
                contextMenuManager.dispose();
                contextMenuManager = null;
            }
            if ( menuToolBar != null )
            {
                menuToolBar.dispose();
                menuToolBar = null;
                menuManager.dispose();
                menuManager = null;
            }
            if ( actionToolBar != null )
            {
                actionToolBar.dispose();
                actionToolBar = null;
                actionToolBarManager.removeAll();
                actionToolBarManager = null;
            }

            if ( infoText != null )
            {
                infoText.dispose();
                infoText = null;
            }

            control.dispose();
            control = null;
        }
    }


    /**
     * Gets the info text.
     * 
     * @return the info text
     */
    public Text getInfoText()
    {
        return infoText;
    }


    /**
     * Gets the tool bar manager.
     * 
     * @return the tool bar manager
     */
    public IToolBarManager getToolBarManager()
    {
        return this.actionToolBarManager;
    }


    /**
     * Gets the menu manager.
     * 
     * @return the menu manager
     */
    public IMenuManager getMenuManager()
    {
        return menuManager;
    }


    /**
     * Gets the context menu manager.
     * 
     * @return the context menu manager
     */
    public IMenuManager getContextMenuManager()
    {
        return this.contextMenuManager;
    }

}
