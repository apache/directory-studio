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

package org.apache.directory.ldapstudio.browser.ui.widgets.entryeditor;


import org.apache.directory.ldapstudio.browser.ui.valueeditors.internal.ValueEditorManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;


public abstract class AbstractOpenEditorAction extends AbstractEntryEditorListenerAction implements FocusListener,
    KeyListener
{

    protected EntryEditorWidgetActionGroup actionGroup;

    protected ValueEditorManager valueEditorManager;

    protected TreeViewer viewer;

    protected CellEditor cellEditor;


    protected AbstractOpenEditorAction( TreeViewer viewer, EntryEditorWidgetActionGroup actionGroup,
        ValueEditorManager valueEditorManager )
    {
        super( viewer, "Editor", null, null );
        this.viewer = viewer;
        this.actionGroup = actionGroup;
        this.valueEditorManager = valueEditorManager;
    }


    public void dispose()
    {
        this.valueEditorManager = null;
        this.actionGroup = null;
        this.viewer = null;
        this.cellEditor = null;
        super.dispose();
    }


    public CellEditor getCellEditor()
    {
        return this.cellEditor;
    }


    public void run()
    {
        this.activateEditor();
    }


    private void activateEditor()
    {

        if ( !this.viewer.isCellEditorActive()
            && this.selectedValues.length == 1
            && this.selectedAttributes.length == 0
            && viewer.getCellModifier().canModify( this.selectedValues[0],
                EntryEditorWidgetTableMetadata.VALUE_COLUMN_NAME ) )
        {

            // set cell editor to viewer
            this.viewer.getCellEditors()[EntryEditorWidgetTableMetadata.VALUE_COLUMN_INDEX] = this.cellEditor;

            // add listener for end of editing
            if ( this.cellEditor.getControl() != null )
            {
                this.cellEditor.getControl().addFocusListener( this );
                this.cellEditor.getControl().addKeyListener( this );
            }

            // deactivate global actions
            if ( this.actionGroup != null )
                this.actionGroup.deactivateGlobalActionHandlers();

            // start editing
            this.viewer.editElement( this.selectedValues[0], EntryEditorWidgetTableMetadata.VALUE_COLUMN_INDEX );

            if ( !this.viewer.isCellEditorActive() )
            {
                this.editorClosed();
            }
        }
        else
        {
            this.valueEditorManager.setUserSelectedValueEditor( null );
        }
    }


    private void editorClosed()
    {

        // remove cell editors from viewer to prevend auto-editing
        for ( int i = 0; i < this.viewer.getCellEditors().length; i++ )
        {
            this.viewer.getCellEditors()[i] = null;
        }

        // remove listener
        if ( this.cellEditor.getControl() != null )
        {
            this.cellEditor.getControl().removeFocusListener( this );
            this.cellEditor.getControl().removeKeyListener( this );
        }

        // activate global actions
        if ( this.actionGroup != null )
            this.actionGroup.activateGlobalActionHandlers();

        // reset custom value editor and set selection to notify all
        // openeditoractions to update their
        // enabled state.
        this.valueEditorManager.setUserSelectedValueEditor( null );
        this.viewer.setSelection( this.viewer.getSelection() );
    }


    public void focusGained( FocusEvent e )
    {
    }


    public void focusLost( FocusEvent e )
    {
        this.editorClosed();
    }


    public void keyPressed( KeyEvent e )
    {
        if ( e.character == SWT.ESC && e.stateMask == SWT.NONE )
        {
            e.doit = false;
        }
    }


    public void keyReleased( KeyEvent e )
    {
    }

}
