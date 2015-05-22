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
package org.apache.directory.studio.openldap.common.ui.widgets;


import org.apache.directory.studio.common.ui.widgets.AbstractWidget;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;


public class BooleanWithDefaultWidget extends AbstractWidget
{
    /** The combo viewer's values */
    private Object[] comboViewerValues = new Object[]
        {
            BooleanValue.DEFAULT,
            BooleanValue.TRUE,
            BooleanValue.FALSE
    };

    // The default value
    private Boolean defaultValue;

    // The value
    private Boolean value;

    // UI widgets
    private ComboViewer comboViewer;


    /**
     * Creates a new instance of BooleanWithDefaultWidget.
     */
    public BooleanWithDefaultWidget()
    {
    }


    /**
     * Creates a new instance of BooleanWithDefaultWidget.
     *
     * @param defaultValue the default value
     */
    public BooleanWithDefaultWidget( boolean defaultValue )
    {
        this.defaultValue = defaultValue;
    }


    /**
     * Creates a new instance of BooleanWithDefaultWidget.
     *
     * @param defaultValue the default value
     */
    public BooleanWithDefaultWidget( Boolean defaultValue )
    {
        this.defaultValue = defaultValue;
    }


    /**
     * Creates the widget.
     *
     * @param parent the parent composite
     */
    public void create( Composite parent )
    {
        create( parent, null );
    }


    /**
     * Creates the widget.
     *
     * @param parent the parent composite
     */
    public void create( Composite parent, FormToolkit toolkit )
    {
        comboViewer = new ComboViewer( parent );
        comboViewer.setContentProvider( new ArrayContentProvider() );
        comboViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                if ( element instanceof BooleanValue )
                {
                    BooleanValue booleanValue = ( BooleanValue ) element;

                    switch ( booleanValue )
                    {
                        case DEFAULT:
                            if ( defaultValue != null )
                            {
                                if ( defaultValue.booleanValue() )
                                {
                                    return NLS.bind( "Default value ({0})", "true" );
                                }
                                else
                                {
                                    return NLS.bind( "Default value ({0})", "false" );
                                }
                            }
                            else
                            {
                                return "Default value";
                            }
                        case TRUE:
                            return "True";
                        case FALSE:
                            return "False";
                    }
                }

                return super.getText( element );
            }
        } );
        comboViewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                value = null;

                StructuredSelection selection = ( StructuredSelection ) comboViewer.getSelection();

                if ( !selection.isEmpty() )
                {
                    BooleanValue booleanValue = ( BooleanValue ) selection.getFirstElement();

                    switch ( booleanValue )
                    {
                        case DEFAULT:
                            value = null;
                            break;
                        case TRUE:
                            value = new Boolean( true );
                            break;
                        case FALSE:
                            value = new Boolean( false );
                            break;
                    }
                }

                notifyListeners();
            }
        } );
        comboViewer.setInput( comboViewerValues );
        comboViewer.setSelection( new StructuredSelection( comboViewerValues[0] ) );
    }


    /**
     * Returns the primary control associated with this widget.
     *
     * @return the primary control associated with this widget.
     */
    public Control getControl()
    {
        return comboViewer.getControl();
    }


    /**
     * Sets the value.
     *
     * @param s the value
     */
    public void setValue( Boolean value )
    {
        this.value = value;

        if ( value != null )
        {
            if ( value.booleanValue() )
            {
                comboViewer.setSelection( new StructuredSelection( comboViewerValues[1] ) );
            }
            else
            {
                comboViewer.setSelection( new StructuredSelection( comboViewerValues[2] ) );
            }
        }
        else
        {
            comboViewer.setSelection( new StructuredSelection( comboViewerValues[0] ) );
        }
    }


    /**
     * Gets the value.
     *
     * @return the value
     */
    public Boolean getValue()
    {
        return value;
    }


    /**
     * Disposes all created SWT widgets.
     */
    public void dispose()
    {
        if ( ( comboViewer != null ) && ( comboViewer.getControl() != null )
            && ( !comboViewer.getControl().isDisposed() ) )
        {
            comboViewer.getControl().dispose();
        }
    }


    /**
     * Sets the enabled state of the widget.
     *
     * @param enabled true to enable the widget, false to disable the widget
     */
    public void setEnabled( boolean enabled )
    {
        if ( ( comboViewer != null ) && ( comboViewer.getControl() != null )
            && ( !comboViewer.getControl().isDisposed() ) )
        {
            comboViewer.getControl().setEnabled( enabled );
        }
    }

    /**
     * This enum represents the various values available.
     */
    enum BooleanValue
    {
        DEFAULT, TRUE, FALSE
    }
}
