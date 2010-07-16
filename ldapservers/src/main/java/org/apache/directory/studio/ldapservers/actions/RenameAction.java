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
/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.apache.directory.studio.ldapservers.actions;


import org.apache.directory.studio.ldapservers.LdapServersManager;
import org.apache.directory.studio.ldapservers.model.LdapServer;
import org.apache.directory.studio.ldapservers.views.ServersView;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.TextActionHandler;


/**
 * This class implements the open action for a server.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class RenameAction extends Action implements IWorkbenchWindowActionDelegate
{
    /** The associated view */
    private ServersView view;
    private Tree tree;
    private TreeEditor treeEditor;
    protected Composite textEditorParent;
    protected Text textEditor;
    private TextActionHandler textActionHandler;
    // The server being edited if this is being done inline
    protected LdapServer editedServer;

    protected boolean saving = false;


    /**
     * Creates a new instance of RenameAction.
     */
    public RenameAction()
    {
        super( Messages.getString( "RenameAction.Rename" ) ); //$NON-NLS-1$
        init();
    }


    /**
     * Creates a new instance of RenameAction.
     * 
     * @param view
     *      the associated view
     */
    public RenameAction( ServersView view )
    {
        super( Messages.getString( "RenameAction.Rename" ) ); //$NON-NLS-1$
        this.view = view;
        this.tree = view.getViewer().getTree();
        this.treeEditor = new TreeEditor( tree );
        init();
    }


    /**
     * Initializes the action.
     */
    private void init()
    {
        //        setId( ApacheDsPluginConstants.CMD_RENAME ); // TODO
        //        setActionDefinitionId( ApacheDsPluginConstants.CMD_RENAME ); // TODO
        setToolTipText( Messages.getString( "RenameAction.RenameToolTip" ) ); //$NON-NLS-1$
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run()
    {
        if ( view != null )
        {
            // What we get from the TableViewer is a StructuredSelection
            StructuredSelection selection = ( StructuredSelection ) view.getViewer().getSelection();

            // Here's the real object
            LdapServer server = ( LdapServer ) selection.getFirstElement();
            if ( server != null )
            {
                queryNewServerNameInline( server );
            }
        }
    }


    /**
     * Return the new name to be given to the target resource or
     * <code>null<code>
     * if the query was canceled. Rename the currently selected server using the table editor. 
     * Continue the action when the user is done.
     *
     * @param server the server to rename
     */
    private void queryNewServerNameInline( final LdapServer server )
    {
        // Make sure text editor is created only once. Simply reset text
        // editor when action is executed more than once. Fixes bug 22269
        if ( textEditorParent == null )
        {
            createTextEditor( server );
        }
        textEditor.setText( server.getName() );

        // Open text editor with initial size
        textEditorParent.setVisible( true );
        Point textSize = textEditor.computeSize( SWT.DEFAULT, SWT.DEFAULT );
        textSize.x += textSize.y; // Add extra space for new characters
        Point parentSize = textEditorParent.getSize();
        int inset = getCellEditorInset( textEditorParent );
        textEditor.setBounds( 2, inset, Math.min( textSize.x, parentSize.x - 4 ), parentSize.y - 2 * inset );
        textEditorParent.redraw();
        textEditor.selectAll();
        textEditor.setFocus();
    }


    /**
     * Create the text editor widget.
     * 
     * @param server the server to rename
     */
    private void createTextEditor( final LdapServer server )
    {
        // Create text editor parent. This draws a nice bounding rect
        textEditorParent = createParent();
        textEditorParent.setVisible( false );
        final int inset = getCellEditorInset( textEditorParent );
        if ( inset > 0 )
        {
            textEditorParent.addListener( SWT.Paint, new Listener()
            {
                public void handleEvent( Event e )
                {
                    Point textSize = textEditor.getSize();
                    Point parentSize = textEditorParent.getSize();
                    e.gc.drawRectangle( 0, 0, Math.min( textSize.x + 4, parentSize.x - 1 ), parentSize.y - 1 );
                }
            } );
        }
        // Create inner text editor
        textEditor = new Text( textEditorParent, SWT.NONE );
        textEditor.setFont( tree.getFont() );
        textEditorParent.setBackground( textEditor.getBackground() );
        textEditor.addListener( SWT.Modify, new Listener()
        {
            public void handleEvent( Event e )
            {
                Point textSize = textEditor.computeSize( SWT.DEFAULT, SWT.DEFAULT );
                textSize.x += textSize.y; // Add extra space for new
                // characters.
                Point parentSize = textEditorParent.getSize();
                textEditor.setBounds( 2, inset, Math.min( textSize.x, parentSize.x - 4 ), parentSize.y - 2 * inset );
                textEditorParent.redraw();
            }
        } );
        textEditor.addListener( SWT.Traverse, new Listener()
        {
            public void handleEvent( Event event )
            {

                // Workaround for Bug 20214 due to extra
                // traverse events
                switch ( event.detail )
                {
                    case SWT.TRAVERSE_ESCAPE:
                        // Do nothing in this case
                        disposeTextWidget();
                        event.doit = true;
                        event.detail = SWT.TRAVERSE_NONE;
                        break;
                    case SWT.TRAVERSE_RETURN:
                        saveChangesAndDispose( server );
                        event.doit = true;
                        event.detail = SWT.TRAVERSE_NONE;
                        break;
                }
            }
        } );
        textEditor.addFocusListener( new FocusAdapter()
        {
            public void focusLost( FocusEvent fe )
            {
                saveChangesAndDispose( server );
            }
        } );

        if ( textActionHandler != null )
        {
            textActionHandler.addText( textEditor );
        }
    }


    private Composite createParent()
    {
        Tree tree2 = tree;
        Composite result = new Composite( tree2, SWT.NONE );
        TreeItem[] selectedItems = tree2.getSelection();
        treeEditor.horizontalAlignment = SWT.LEFT;
        treeEditor.grabHorizontal = true;
        treeEditor.setEditor( result, selectedItems[0] );
        return result;
    }


    /**
     * Close the text widget and reset the editorText field.
     */
    protected void disposeTextWidget()
    {
        if ( textActionHandler != null )
            textActionHandler.removeText( textEditor );

        if ( textEditorParent != null )
        {
            textEditorParent.dispose();
            textEditorParent = null;
            textEditor = null;
            treeEditor.setEditor( null, null );
        }
    }


    /**
     * Save the changes and dispose of the text widget.
     * 
     * @param server the server to rename
     */
    protected void saveChangesAndDispose( LdapServer server )
    {
        if ( saving == true )
            return;

        saving = true;
        // Cache the resource to avoid selection loss since a selection of
        // another item can trigger this method
        editedServer = server;
        final String newName = textEditor.getText();
        // Run this in an async to make sure that the operation that triggered
        // this action is completed. Otherwise this leads to problems when the
        // icon of the item being renamed is clicked (i.e., which causes the
        // rename text widget to lose focus and trigger this method)
        tree.getShell().getDisplay().asyncExec( new Runnable()
        {
            public void run()
            {
                try
                {
                    if ( !newName.equals( editedServer.getName() ) )
                    {
                        if ( !LdapServersManager.getDefault().isNameAvailable( newName ) )
                        {
                            MessageDialog.openError( tree.getShell(), Messages.getString( "RenameAction.Server" ), //$NON-NLS-1$
                                Messages.getString( "RenameAction.ErrorNameInUse" ) ); //$NON-NLS-1$
                        }
                        else
                        {
                            editedServer.setName( newName );
                        }
                    }
                    editedServer = null;
                    // Dispose the text widget regardless
                    disposeTextWidget();
                    // Ensure the Navigator tree has focus, which it may not if
                    // the text widget previously had focus
                    if ( tree != null && !tree.isDisposed() )
                    {
                        tree.setFocus();
                    }
                }
                finally
                {
                    saving = false;
                }
            }
        } );
    }


    /**
     * On Mac the text widget already provides a border when it has focus, so
     * there is no need to draw another one. The value of returned by this
     * method is usd to control the inset we apply to the text field bound's in
     * order to get space for drawing a border. A value of 1 means a one-pixel
     * wide border around the text field. A negative value supresses the border.
     * However, in M9 the system property
     * "org.eclipse.swt.internal.carbon.noFocusRing" has been introduced as a
     * temporary workaround for bug #28842. The existence of the property turns
     * the native focus ring off if the widget is contained in a main window
     * (not dialog). The check for the property should be removed after a final
     * fix for #28842 has been provided.
     */
    private static int getCellEditorInset( Control c )
    {
        // special cases for MacOS X
        if ( "carbon".equals( SWT.getPlatform() ) ) { //$NON-NLS-1$
            if ( System.getProperty( "org.eclipse.swt.internal.carbon.noFocusRing" ) == null || c.getShell().getParent() != null ) { //$NON-NLS-1$
                return -2; // native border
            }
        }
        else if ( "cocoa".equals( SWT.getPlatform() ) )
        {
            return 0; // native border
        }
        return 1; // one pixel wide black border
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
