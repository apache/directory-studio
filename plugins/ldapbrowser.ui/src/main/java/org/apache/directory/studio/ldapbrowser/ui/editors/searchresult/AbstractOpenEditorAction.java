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

package org.apache.directory.studio.ldapbrowser.ui.editors.searchresult;


import org.apache.directory.studio.ldapbrowser.common.actions.BrowserAction;
import org.apache.directory.studio.valueeditors.ValueEditorManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.TableItem;


/**
 * Base class for all value editor actions of the search result editor.
 * It manages activation and closing of value editors. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class AbstractOpenEditorAction extends BrowserAction implements FocusListener, KeyListener
{

    /** The value editor manager. */
    protected ValueEditorManager valueEditorManager;

    /** The viewer. */
    protected TableViewer viewer;

    /** The cursor. */
    protected SearchResultEditorCursor cursor;

    /** The cell editor. */
    protected CellEditor cellEditor;

    /** The is active flag. */
    private boolean isActive;

    /** The actionGroup. */
    protected SearchResultEditorActionGroup actionGroup;


    /**
     * Creates a new instance of AbstractOpenEditorAction.
     * 
     * @param viewer the viewer
     * @param cursor the cursor
     * @param valueEditorManager the value editor manager
     * @param actionGroup the action group
     */
    protected AbstractOpenEditorAction( TableViewer viewer, SearchResultEditorCursor cursor,
        ValueEditorManager valueEditorManager, SearchResultEditorActionGroup actionGroup )
    {
        this.viewer = viewer;
        this.cursor = cursor;
        this.valueEditorManager = valueEditorManager;
        this.actionGroup = actionGroup;
        this.isActive = false;
    }


    /**
     * {@inheritDoc}
     */
    public CellEditor getCellEditor()
    {
        return cellEditor;
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        activateEditor();
    }


    /**
     * Activates the editor.
     */
    private void activateEditor()
    {
        Object element = cursor.getRow().getData();
        String property = ( String ) viewer.getColumnProperties()[cursor.getColumn()];

        if ( !viewer.isCellEditorActive() && viewer.getCellModifier().canModify( element, property ) )
        {
            // disable action handlers
            actionGroup.deactivateGlobalActionHandlers();

            // set cell editor to viewer
            for ( int i = 0; i < viewer.getCellEditors().length; i++ )
            {
                viewer.getCellEditors()[i] = cellEditor;
            }

            // add listener for end of editing
            if ( cellEditor.getControl() != null )
            {
                cellEditor.getControl().addFocusListener( this );
                cellEditor.getControl().addKeyListener( this );
            }

            // deactivate cursor
            cursor.setVisible( false );

            // start editing
            isActive = true;
            viewer.editElement( element, cursor.getColumn() );

            viewer.setSelection( null, true );
            viewer.getTable().setSelection( new TableItem[0] );

            if ( !viewer.isCellEditorActive() )
            {
                editorClosed();
            }
        }
        else
        {
            valueEditorManager.setUserSelectedValueEditor( null );
        }
    }


    private void editorClosed()
    {
        // clear active flag
        isActive = false;

        // remove cell editors from viewer to prevent auto-editing
        for ( int i = 0; i < viewer.getCellEditors().length; i++ )
        {
            viewer.getCellEditors()[i] = null;
        }

        // remove listener
        if ( cellEditor.getControl() != null )
        {
            cellEditor.getControl().removeFocusListener( this );
            cellEditor.getControl().removeKeyListener( this );
        }

        valueEditorManager.setUserSelectedValueEditor( null );

        // activate cursor
        cursor.setVisible( true );
        viewer.refresh();
        cursor.redraw();
        cursor.getDisplay().asyncExec( new Runnable()
        {
            public void run()
            {
                cursor.setFocus();
            }
        } );

        // enable action handlers
        actionGroup.activateGlobalActionHandlers();
    }


    /**
     * {@inheritDoc}
     */
    public void focusGained( FocusEvent e )
    {
    }


    /**
     * {@inheritDoc}
     */
    public void focusLost( FocusEvent e )
    {
        editorClosed();
    }


    /**
     * {@inheritDoc}
     */
    public void keyPressed( KeyEvent e )
    {
        if ( e.character == SWT.ESC && e.stateMask == SWT.NONE )
        {
            e.doit = false;
        }
    }


    /**
     * {@inheritDoc}
     */
    public void keyReleased( KeyEvent e )
    {
    }


    /**
     * {@inheritDoc}
     */
    public boolean isActive()
    {
        return isActive;
    }

}
