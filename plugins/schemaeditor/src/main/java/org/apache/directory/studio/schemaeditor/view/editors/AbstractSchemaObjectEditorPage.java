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

package org.apache.directory.studio.schemaeditor.view.editors;


import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;


/**
 * This abstract class defines a schema object editor page.
 */
public abstract class AbstractSchemaObjectEditorPage<E extends FormEditor> extends FormPage
{
    /** The flag to indicate if the page has been initialized */
    protected boolean initialized = false;


    /**
     * Default constructor
     * 
     * @param editor the parent editor
     * @param id the unique identifier
     * @param title the page title
     */
    public AbstractSchemaObjectEditorPage( E editor, String id, String title )
    {
        super( editor, id, title );
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public E getEditor()
    {
        return ( E ) super.getEditor();
    }


    /**
     * {@inheritDoc}
     */
    protected void createFormContent( IManagedForm managedForm )
    {
        initialized = true;
    }


    /**
     * Adds listeners to UI fields
     */
    protected void addListeners()
    {
    }


    /**
     * Removes listeners from UI fields
     */
    protected void removeListeners()
    {
    }


    /**
     * Initializes the UI fields from the input.
     */
    protected void fillInUiFields()
    {
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        removeListeners();

        super.dispose();
    }


    /**
     * Refreshes the UI.
     */
    public void refreshUI()
    {
        if ( initialized )
        {
            removeListeners();
            fillInUiFields();
            addListeners();
        }
    }


    /**
     * Adds a modify listener to the given Text.
     *
     * @param text the Text control
     * @param listener the listener
     */
    protected void addModifyListener( Text text, ModifyListener listener )
    {
        if ( ( text != null ) && ( !text.isDisposed() ) && ( listener != null ) )
        {
            text.addModifyListener( listener );
        }
    }


    /**
     * Adds a modify listener to the given Text.
     *
     * @param text the StyledText control
     * @param listener the listener
     */
    protected void addModifyListener( StyledText text, ModifyListener listener )
    {
        if ( ( text != null ) && ( !text.isDisposed() ) && ( listener != null ) )
        {
            text.addModifyListener( listener );
        }
    }


    /**
     * Adds a modify listener to the given Text.
     *
     * @param combo the Combo control
     * @param listener the listener
     */
    protected void addModifyListener( Combo combo, ModifyListener listener )
    {
        if ( ( combo != null ) && ( !combo.isDisposed() ) && ( listener != null ) )
        {
            combo.addModifyListener( listener );
        }
    }


    /**
     * Adds a verify listener to the given Text.
     *
     * @param text the Text control
     * @param listener the listener
     */
    protected void addVerifyListener( Text text, VerifyListener listener )
    {
        if ( ( text != null ) && ( !text.isDisposed() ) && ( listener != null ) )
        {
            text.addVerifyListener( listener );
        }
    }


    /**
     * Adds a selection changed listener to the given Viewer.
     *
     * @param viewer the viewer control
     * @param listener the listener
     */
    protected void addSelectionChangedListener( Viewer viewer, ISelectionChangedListener listener )
    {
        if ( ( viewer != null ) && ( viewer.getControl() != null ) && ( !viewer.getControl().isDisposed() )
            && ( listener != null ) )
        {
            viewer.addSelectionChangedListener( listener );
        }
    }


    /**
     * Adds an hyperlink listener to the given HyperLink.
     *
     * @param hyperLink the HyperLink
     * @param listener the listener
     */
    protected void addHyperlinkListener( Hyperlink hyperLink, IHyperlinkListener listener )
    {
        if ( ( hyperLink != null ) && ( !hyperLink.isDisposed() ) && ( listener != null ) )
        {
            hyperLink.addHyperlinkListener( listener );
        }
    }


    /**
     * Adds a selection listener to the given Button.
     *
     * @param button the Button control
     * @param listener the listener
     */
    protected void addSelectionListener( Button button, SelectionListener listener )
    {
        if ( ( button != null ) && ( !button.isDisposed() ) && ( listener != null ) )
        {
            button.addSelectionListener( listener );
        }
    }


    /**
     * Adds a mouse listener to the given Table.
     *
     * @param table the Table control
     * @param listener the listener
     */
    protected void addMouseListener( Table table, MouseListener listener )
    {
        if ( ( table != null ) && ( !table.isDisposed() ) && ( listener != null ) )
        {
            table.addMouseListener( listener );
        }
    }


    /**
     * Removes a modify listener from the given Text.
     *
     * @param text the Text control
     * @param listener the listener
     */
    protected void removeModifyListener( Text text, ModifyListener listener )
    {
        if ( ( text != null ) && ( !text.isDisposed() ) && ( listener != null ) )
        {
            text.removeModifyListener( listener );
        }
    }


    /**
     * Removes a modify listener from the given Text.
     *
     * @param text the StyledText control
     * @param listener the listener
     */
    protected void removeModifyListener( StyledText text, ModifyListener listener )
    {
        if ( ( text != null ) && ( !text.isDisposed() ) && ( listener != null ) )
        {
            text.removeModifyListener( listener );
        }
    }


    /**
     * Removes a modify listener from the given Text.
     *
     * @param combo the Combo control
     * @param listener the listener
     */
    protected void removeModifyListener( Combo combo, ModifyListener listener )
    {
        if ( ( combo != null ) && ( !combo.isDisposed() ) && ( listener != null ) )
        {
            combo.removeModifyListener( listener );
        }
    }


    /**
     * Removes a verify listener from the given Text.
     *
     * @param text the Text control
     * @param listener the listener
     */
    protected void removeVerifyListener( Text text, VerifyListener listener )
    {
        if ( ( text != null ) && ( !text.isDisposed() ) && ( listener != null ) )
        {
            text.removeVerifyListener( listener );
        }
    }


    /**
     * Removes a selection changed listener from the given Viewer.
     *
     * @param viewer
     *      the viewer control
     * @param listener
     *      the listener
     */
    protected void removeSelectionChangedListener( Viewer viewer, ISelectionChangedListener listener )
    {
        if ( ( viewer != null ) && ( viewer.getControl() != null ) && ( !viewer.getControl().isDisposed() )
            && ( listener != null ) )
        {
            viewer.removeSelectionChangedListener( listener );
        }
    }


    /**
     * Removes an hyperlink listener from the given HyperLink.
     *
     * @param hyperLink the HyperLink
     * @param listener the listener
     */
    protected void removeHyperlinkListener( Hyperlink hyperLink, IHyperlinkListener listener )
    {
        if ( ( hyperLink != null ) && ( !hyperLink.isDisposed() ) && ( listener != null ) )
        {
            hyperLink.removeHyperlinkListener( listener );
        }
    }


    /**
     * Removes a selection listener from the given Button.
     *
     * @param button
     *      the Button control
     * @param listener
     *      the listener
     */
    protected void removeSelectionListener( Button button, SelectionListener listener )
    {
        if ( ( button != null ) && ( !button.isDisposed() ) && ( listener != null ) )
        {
            button.removeSelectionListener( listener );
        }
    }


    /**
     * Removes a mouse listener from the given Table.
     *
     * @param table the Table control
     * @param listener the listener
     */
    protected void removeMouseListener( Table table, MouseListener listener )
    {
        if ( ( table != null ) && ( !table.isDisposed() ) && ( listener != null ) )
        {
            table.removeMouseListener( listener );
        }
    }
}
