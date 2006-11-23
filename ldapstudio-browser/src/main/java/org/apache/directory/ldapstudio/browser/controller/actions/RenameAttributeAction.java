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

package org.apache.directory.ldapstudio.browser.controller.actions;


import org.apache.directory.ldapstudio.browser.view.views.AttributesView;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;


/**
 * This class implements the Rename Attribute Action.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class RenameAttributeAction extends Action
{
    private static final int COLUMN_TO_EDIT = 1;
    private final AttributesView view;
    private final Table table;
    private final TableEditor tableEditor;
    //	private final Text textActionHandler;
    private Composite editorParent;
    private Text editor;
    private String originalText;


    public RenameAttributeAction( AttributesView view, Table table, String text )
    {
        super( text );
        this.view = view;
        this.table = table;
        tableEditor = new TableEditor( table );
    }


    public void run()
    {
        originalText = getTextToEdit();
        if ( originalText == null )
        {
            return;
        }
        if ( editor == null )
        {
            createEditor();
        }
        showEditor( originalText );
    }


    private void createEditor()
    {
        // Create the parent so that a simple border
        // can be painted around the text editor
        editorParent = new Composite( table, SWT.NONE );
        TableItem[] tableItems = table.getSelection();
        tableEditor.horizontalAlignment = SWT.LEFT;
        tableEditor.grabHorizontal = true;
        tableEditor.setEditor( editorParent, tableItems[0], COLUMN_TO_EDIT );
        editorParent.setVisible( false );
        editorParent.addListener( SWT.Paint, new Listener()
        {
            public void handleEvent( Event e )
            {
                // Paint a simple border around the text editor
                Point textSize = editor.getSize();
                Point parentSize = editorParent.getSize();
                int w = Math.min( textSize.x + 4, parentSize.x - 1 );
                int h = parentSize.y - 1;
                e.gc.drawRectangle( 0, 0, w, h );
            }
        } );

        // Create the editor itself
        editor = new Text( editorParent, SWT.NONE );
        editorParent.setBackground( editor.getBackground() );
        editor.addListener( SWT.Modify, new Listener()
        {
            public void handleEvent( Event e )
            {
                Point textSize = editor.computeSize( SWT.DEFAULT, SWT.DEFAULT );
                textSize.x += textSize.y;

                // Add extra space for new characters
                Point parentSize = editorParent.getSize();
                int w = Math.min( textSize.x, textSize.y );
                int h = parentSize.y - 2;
                editor.setBounds( 2, 1, w, h );
                editorParent.redraw();
            }
        } );
        editor.addListener( SWT.Traverse, new Listener()
        {
            public void handleEvent( Event event )
            {
                // Workaround for bug 20214 due to extra traverse events
                switch ( event.detail )
                {
                    case SWT.TRAVERSE_ESCAPE:
                        // Do nothing in this case
                        disposeEditor();
                        event.doit = true;
                        event.detail = SWT.TRAVERSE_NONE;
                        break;
                    case SWT.TRAVERSE_RETURN:
                        saveChangesAndDisposeEditor();
                        event.doit = true;
                        event.detail = SWT.TRAVERSE_NONE;
                        break;
                }
            }
        } );
        editor.addFocusListener( new FocusAdapter()
        {
            public void focusLost( FocusEvent fe )
            {
                saveChangesAndDisposeEditor();
            }
        } );

        // Add a handler to redirect global cut, copy, etc.
        // textActionHandler.......
    }


    private void showEditor( String name )
    {
        editor.setText( name );
        editorParent.setVisible( true );
        Point textSize = editor.computeSize( SWT.DEFAULT, SWT.DEFAULT );
        textSize.x += textSize.y;
        // Add extra space for new characters
        Point parentSize = editorParent.getSize();
        int w = Math.min( textSize.x, parentSize.x - 4 );
        int h = parentSize.y - 2;
        editor.setBounds( 2, 1, w, h );
        editorParent.redraw();
        editor.selectAll();
        editor.setFocus();
    }


    protected void saveChangesAndDisposeEditor()
    {
        String newText = editor.getText();
        if ( !originalText.equals( newText ) )
        {
            saveChanges( newText );
        }
        disposeEditor();
    }


    protected void disposeEditor()
    {
        if ( editorParent != null )
        {
            editorParent.dispose();
            editorParent = null;
            editor = null;
            tableEditor.setEditor( null, null, COLUMN_TO_EDIT );
        }
    }


    protected String getTextToEdit()
    {
        TableItem item = view.getSelectedAttributeTableItem();
        return item.getText( 1 );
    }


    protected void saveChanges( String newText )
    {
        TableItem item = view.getSelectedAttributeTableItem();
        item.setText( 1, newText );
        view.getViewer().refresh( item );
    }
}
