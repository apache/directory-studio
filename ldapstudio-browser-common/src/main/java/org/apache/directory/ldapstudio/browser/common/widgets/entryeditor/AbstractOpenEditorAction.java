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

package org.apache.directory.ldapstudio.browser.common.widgets.entryeditor;


import org.apache.directory.ldapstudio.browser.common.actions.BrowserAction;
import org.apache.directory.ldapstudio.valueeditors.ValueEditorManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;


/**
 * The base class for all value editor actions of the entry editor widget.
 * It manages activation and closing of value editors. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public abstract class AbstractOpenEditorAction extends BrowserAction implements FocusListener, KeyListener
{

    /** The action group. */
    protected EntryEditorWidgetActionGroup actionGroup;

    /** The value editor manager. */
    protected ValueEditorManager valueEditorManager;

    /** The viewer. */
    protected TreeViewer viewer;

    /** The cell editor. */
    protected CellEditor cellEditor;


    /**
     * Creates a new instance of AbstractOpenEditorAction.
     * 
     * @param viewer the viewer
     * @param actionGroup the action group
     * @param valueEditorManager the value editor manager
     */
    protected AbstractOpenEditorAction( TreeViewer viewer, EntryEditorWidgetActionGroup actionGroup,
        ValueEditorManager valueEditorManager )
    {
        this.viewer = viewer;
        this.actionGroup = actionGroup;
        this.valueEditorManager = valueEditorManager;
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        valueEditorManager = null;
        actionGroup = null;
        viewer = null;
        cellEditor = null;
        super.dispose();
    }


    /**
     * Gets the cell editor.
     * 
     * @return the cell editor
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

        if ( !viewer.isCellEditorActive()
            && getSelectedValues().length == 1
            && getSelectedAttributes().length == 0
            && viewer.getCellModifier().canModify( getSelectedValues()[0],
                EntryEditorWidgetTableMetadata.VALUE_COLUMN_NAME ) )
        {

            // set cell editor to viewer
            viewer.getCellEditors()[EntryEditorWidgetTableMetadata.VALUE_COLUMN_INDEX] = cellEditor;

            // add listener for end of editing
            if ( cellEditor.getControl() != null )
            {
                cellEditor.getControl().addFocusListener( this );
                cellEditor.getControl().addKeyListener( this );
            }

            // deactivate global actions
            if ( actionGroup != null )
            {
                actionGroup.deactivateGlobalActionHandlers();
            }

            // start editing
            viewer.editElement( getSelectedValues()[0], EntryEditorWidgetTableMetadata.VALUE_COLUMN_INDEX );

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


    /**
     * Editor closed.
     */
    private void editorClosed()
    {

        // remove cell editors from viewer to prevend auto-editing
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

        // activate global actions
        if ( actionGroup != null )
        {
            actionGroup.activateGlobalActionHandlers();
        }

        // reset custom value editor and set selection to notify all
        // openeditoractions to update their enabled state.
        valueEditorManager.setUserSelectedValueEditor( null );
        viewer.setSelection( viewer.getSelection() );
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

}
