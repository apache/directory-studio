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
package org.apache.directory.studio.templateeditor.editor.widgets;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.directory.api.util.GeneralizedTime;
import org.apache.directory.studio.entryeditors.IEntryEditor;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.valueeditors.time.GeneralizedTimeValueDialog;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

import org.apache.directory.studio.templateeditor.EntryTemplatePlugin;
import org.apache.directory.studio.templateeditor.EntryTemplatePluginConstants;
import org.apache.directory.studio.templateeditor.model.widgets.TemplateDate;


/**
 * This class implements an editor date.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EditorDate extends EditorWidget<TemplateDate>
{
    /** The main composite */
    private Composite composite;

    /** The date text widget */
    private Text dateText;

    /** The 'Browse...' toolbar item */
    private ToolItem editToolItem;


    /**
     * Creates a new instance of EditorLabel.
     * 
     * @param editor
     *      the associated editor
     * @param templateDate
     *      the associated template label
     * @param toolkit
     *      the associated toolkit
     */
    public EditorDate( IEntryEditor editor, TemplateDate templateDate, FormToolkit toolkit )
    {
        super( templateDate, editor, toolkit );
    }


    /**
     * {@inheritDoc}
     */
    public Composite createWidget( Composite parent )
    {
        // Creating and initializing the widget UI
        Composite composite = initWidget( parent );

        // Updating the widget's content
        updateWidget();

        // Adding the listeners
        addListeners();

        return composite;
    }


    /**
     * Creates and initializes the widget UI.
     *
     * @param parent
     *      the parent composite
     * @return
     *      the associated composite
     */
    private Composite initWidget( Composite parent )
    {
        // Creating the widget composite
        composite = getToolkit().createComposite( parent );
        composite.setLayoutData( getGridata() );

        // Calculating the number of columns needed
        int numberOfColumns = 1;
        if ( getWidget().isShowEditButton() )
        {
            numberOfColumns++;
        }

        // Creating the layout
        GridLayout gl = new GridLayout( numberOfColumns, false );
        gl.marginHeight = gl.marginWidth = 0;
        gl.horizontalSpacing = gl.verticalSpacing = 0;
        composite.setLayout( gl );

        // Creating the label
        dateText = new Text( composite, SWT.NONE );
        dateText.setEditable( false );
        dateText.setBackground( composite.getBackground() );
        dateText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, false, false ) );

        // Creating the edit password button
        if ( getWidget().isShowEditButton() )
        {
            ToolBar toolbar = new ToolBar( composite, SWT.HORIZONTAL | SWT.FLAT );

            editToolItem = new ToolItem( toolbar, SWT.PUSH );
            editToolItem.setToolTipText( Messages.getString( "EditorDate.EditDate" ) ); //$NON-NLS-1$
            editToolItem.setImage( EntryTemplatePlugin.getDefault().getImage(
                EntryTemplatePluginConstants.IMG_TOOLBAR_EDIT_DATE ) );
        }

        return parent;
    }


    /**
     * Converts the given GeneralizedTime string representation of the date into 
     * the desired string format.
     *
     * @param dateString
     *      the GeneralizedTime string representation of the date
     * @return
     *      the given date in the desired string format
     */
    private String convertDate( String dateString )
    {
        try
        {
            // Creating a date
            Date date = ( new GeneralizedTime( dateString ) ).getCalendar().getTime();

            // Setting a default formatter
            SimpleDateFormat formatter = new SimpleDateFormat();

            // Getting the format defined in the template
            String format = getWidget().getFormat();
            if ( ( format != null ) && ( !format.equalsIgnoreCase( "" ) ) ) //$NON-NLS-1$
            {
                // Setting a custom formatter
                formatter = new SimpleDateFormat( format );
            }

            // Returning the formatted date
            return formatter.format( date );
        }
        catch ( ParseException pe )
        {
            // Returning the original value in that case
            return dateString;
        }
    }


    /**
     * Adds the listeners.
     */
    private void addListeners()
    {
        // Edit toolbar item
        if ( ( editToolItem != null ) && ( !editToolItem.isDisposed() ) )
        {
            editToolItem.addSelectionListener( new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent e )
                {
                    editToolItemAction();
                }
            } );
        }
    }


    /**
     * This method is called when the 'Edit...' toolbar item is clicked.
     */
    private void editToolItemAction()
    {
        // Creating and opening a GeneralizedTimeValueDialog
        GeneralizedTimeValueDialog dialog = new GeneralizedTimeValueDialog( PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), getGeneralizedTimeValueFromAttribute() );
        if ( dialog.open() == Dialog.OK )
        {
            // Updating the attribute with the new value
            updateAttributeValue( dialog.getGeneralizedTime().toGeneralizedTime() );
        }
    }


    /**
     * Get the Generalized Time associated from the attribute.
     *
     * @return
     *      the Generalized Time associated from the attribute or <code>null</code>.
     */
    private GeneralizedTime getGeneralizedTimeValueFromAttribute()
    {
        IAttribute attribute = getAttribute();
        if ( ( attribute != null ) && ( attribute.isString() ) && ( attribute.getValueSize() > 0 ) )
        {
            try
            {
                return new GeneralizedTime( attribute.getStringValue() );
            }
            catch ( ParseException e )
            {
                // Nothing to do, will return null
            }
        }

        return null;
    }


    /**
     * Updates the widget's content.
     */
    private void updateWidget()
    {
        IAttribute attribute = getAttribute();
        if ( ( attribute != null ) && ( attribute.isString() ) && ( attribute.getValueSize() > 0 ) )
        {
            // Setting the date value
            dateText.setText( convertDate( attribute.getStringValue() ) );
        }
        else
        {
            // No value
            dateText.setText( Messages.getString( "EditorDate.NoValue" ) ); //$NON-NLS-1$
        }

        // Updating the layout of the composite
        composite.layout();
    }


    /**
     * {@inheritDoc}
     */
    public void update()
    {
        updateWidget();
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        // Nothing to do
    }
}