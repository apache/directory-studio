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
package org.apache.directory.studio.openldap.config.model.widgets;


import org.apache.directory.studio.ldapbrowser.common.widgets.BrowserWidget;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;

import org.apache.directory.studio.openldap.config.model.OlcBdbConfigLockDetectEnum;


/**
 * The LockDetectWidget provides a combo to select the Lock Detect value.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LockDetectWidget extends BrowserWidget
{
    /** The combo viewer's values */
    private Object[] comboViewerValues = new Object[]
        {
            new NoneObject(),
            OlcBdbConfigLockDetectEnum.DEFAULT,
            OlcBdbConfigLockDetectEnum.RANDOM,
            OlcBdbConfigLockDetectEnum.OLDEST,
            OlcBdbConfigLockDetectEnum.YOUNGEST,
            OlcBdbConfigLockDetectEnum.FEWEST
    };

    /** The selected value */
    private OlcBdbConfigLockDetectEnum value;

    // UI widgets
    private ComboViewer comboViewer;


    /**
     * Creates a new instance of LockDetectWidget.
     */
    public LockDetectWidget()
    {
    }


    /**
     * Creates the widget.
     * 
     * @param parent the parent
     */
    public void createWidget( Composite parent )
    {
        createWidget( parent, null );
    }


    /**
     * Creates the widget.
     * 
     * @param parent the parent
     * @param toolkit the toolkit
     */
    public void createWidget( Composite parent, FormToolkit toolkit )
    {
        // Combo
        comboViewer = new ComboViewer( parent );
        comboViewer.setContentProvider( new ArrayContentProvider() );
        comboViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                if ( element instanceof NoneObject )
                {
                    return "(No value)";
                }
                else if ( element instanceof OlcBdbConfigLockDetectEnum )
                {
                    OlcBdbConfigLockDetectEnum lockDetect = ( OlcBdbConfigLockDetectEnum ) element;

                    switch ( lockDetect )
                    {
                        case OLDEST:
                            return "Oldest";
                        case YOUNGEST:
                            return "Youngest";
                        case FEWEST:
                            return "Fewest";
                        case RANDOM:
                            return "Random";
                        case DEFAULT:
                            return "Default";
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
                    Object selectedObject = selection.getFirstElement();

                    if ( selectedObject instanceof OlcBdbConfigLockDetectEnum )
                    {
                        value = ( OlcBdbConfigLockDetectEnum ) selectedObject;
                    }
                }

                notifyListeners();
            }
        } );
        comboViewer.setInput( comboViewerValues );
        comboViewer.setSelection( new StructuredSelection( comboViewerValues[0] ) );
    }


    /**
     * Sets the value.
     *
     * @param value the value
     */
    public void setValue( OlcBdbConfigLockDetectEnum value )
    {
        this.value = value;

        if ( value == null )
        {
            comboViewer.setSelection( new StructuredSelection( comboViewerValues[0] ) );
        }
        else
        {
            comboViewer.setSelection( new StructuredSelection( value ) );
        }
    }


    /**
     * Gets the value.
     *
     * @return the value
     */
    public OlcBdbConfigLockDetectEnum getValue()
    {
        return value;
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

    class NoneObject
    {
    }
}
