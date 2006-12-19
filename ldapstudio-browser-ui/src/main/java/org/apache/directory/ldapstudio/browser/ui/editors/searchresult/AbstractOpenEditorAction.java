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

package org.apache.directory.ldapstudio.browser.ui.editors.searchresult;


import org.apache.directory.ldapstudio.browser.core.events.ModelModifier;
import org.apache.directory.ldapstudio.browser.ui.valueproviders.ValueProviderManager;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.TableItem;


public abstract class AbstractOpenEditorAction extends AbstractSearchResultListenerAction implements FocusListener,
    KeyListener, ModelModifier
{

    protected SearchResultEditorActionGroup actionGroup;

    protected ValueProviderManager valueProviderManager;

    protected TableViewer viewer;

    protected SearchResultEditorCursor cursor;

    protected CellEditor cellEditor;

    private boolean isActive;


    protected AbstractOpenEditorAction( TableViewer viewer, SearchResultEditorCursor cursor,
        SearchResultEditorActionGroup actionGroup, ValueProviderManager valueProviderManager )
    {
        super( cursor, "Editor", null, null );
        this.actionGroup = actionGroup;
        this.viewer = viewer;
        this.cursor = cursor;
        this.valueProviderManager = valueProviderManager;
        this.isActive = false;
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

        Object element = cursor.getRow().getData();
        String property = ( String ) this.viewer.getColumnProperties()[cursor.getColumn()];

        if ( !this.viewer.isCellEditorActive() && viewer.getCellModifier().canModify( element, property ) )
        {

            // check if attribute exists
            /*
             * if(element instanceof ISearchResult) { ISearchResult result =
             * (ISearchResult)element; IAttribute attribute =
             * result.getAttribute(property); if(attribute == null) {
             * EventRegistry.suspendEventFireingInCurrentThread(); try {
             * attribute = result.getEntry().createAttribute(property,
             * this); System.out.println("activateEditor(): created
             * attribute " + attribute); } catch (ModelModificationException
             * e) { } EventRegistry.resumeEventFireingInCurrentThread(); } }
             */

            // set cell editor to viewer
            for ( int i = 0; i < this.viewer.getCellEditors().length; i++ )
            {
                this.viewer.getCellEditors()[i] = this.cellEditor;
            }

            // add listener for end of editing
            if ( this.cellEditor.getControl() != null )
            {
                this.cellEditor.getControl().addFocusListener( this );
                this.cellEditor.getControl().addKeyListener( this );
            }

            // deactivate cursor
            this.cursor.setVisible( false );

            // deactivate global actions
            this.actionGroup.deactivateGlobalActionHandlers();

            // start editing
            this.isActive = true;
            this.viewer.editElement( element, cursor.getColumn() );

            viewer.setSelection( null, true );
            viewer.getTable().setSelection( new TableItem[0] );

            if ( !this.viewer.isCellEditorActive() )
            {
                this.editorClosed();
            }
        }
        else
        {
            this.valueProviderManager.setUserSelectedValueProvider( null );
        }
    }


    private void editorClosed()
    {

        // check empty attribute
        /*
         * Object element = cursor.getRow().getData(); String property =
         * (String)this.viewer.getColumnProperties()[cursor.getColumn()];
         * if(element instanceof ISearchResult) { ISearchResult result =
         * (ISearchResult)element; IAttribute attribute =
         * result.getAttribute(property); if(attribute != null &&
         * attribute.getValueSize() == 0) {
         * EventRegistry.suspendEventFireingInCurrentThread(); try {
         * result.getEntry().deleteAttribute(attribute, this);
         * System.out.println("activateEditor(): deleted attribute " +
         * attribute); } catch (ModelModificationException e) { }
         * EventRegistry.resumeEventFireingInCurrentThread(); } }
         */

        // clear active flag
        this.isActive = false;

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
        this.actionGroup.activateGlobalActionHandlers();

        this.valueProviderManager.setUserSelectedValueProvider( null );

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


    public boolean isActive()
    {
        return isActive;
    }

}
