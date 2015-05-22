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
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.apache.directory.studio.openldap.common.ui.LogLevel;
import org.apache.directory.studio.openldap.common.ui.dialogs.LogLevelDialog;


/**
 * A widget used to configure the OpenLDAP LogLevel. We can have any of the following
 * values :
 * 
 * <ul>
 * <li>none        0</li>
 * <li>trace       1</li>
 * <li>packets     2</li>
 * <li>args        4</li>
 * <li>conns       8</li>
 * <li>BER        16</li>
 * <li>filter     32</li>
 * <li>config     64</li>
 * <li>ACL       128</li>
 * <li>stats     256</li>
 * <li>stats2    512</li>
 * <li>shell    1024</li>
 * <li>parse    2048</li>
 * <li>sync    16384</li>
 * <li>any       -1</li>
 * </ul>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LogLevelWidget extends AbstractWidget
{
    /** The log level */
    private int value;

    // UI widgets
    private Composite composite;
    private Text label;
    private Button editButton;

    // Listeners
    private SelectionListener editButtonSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            // Creating and opening a LogLevel dialog
            LogLevelDialog dialog = new LogLevelDialog( editButton.getShell(), value );

            if ( LogLevelDialog.OK == dialog.open() )
            {
                setValue( dialog.getLogLevelValue() );
                notifyListeners();
            }
        }
    };


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
        // Creating the widget base composite
        if ( toolkit != null )
        {
            composite = toolkit.createComposite( parent );
        }
        else
        {
            composite = new Composite( parent, SWT.NONE );
        }
        
        GridLayout compositeGridLayout = new GridLayout( 2, false );
        compositeGridLayout.marginHeight = compositeGridLayout.marginWidth = 0;
        compositeGridLayout.verticalSpacing = 0;
        composite.setLayout( compositeGridLayout );

        // Label
        if ( toolkit != null )
        {
            label = toolkit.createText( composite, "" );
        }
        else
        {
            label = BaseWidgetUtils.createText( composite, "", 1 );
        }
        
        label.setEditable( false );
        label.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

        // Edit Button
        if ( toolkit != null )
        {
            editButton = toolkit.createButton( composite, "Edit LogLevels...", SWT.PUSH );
        }
        else
        {
            editButton = BaseWidgetUtils.createButton( composite, "Edit LogLevels...", 1 );
        }
        
        editButton.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false ) );

        // Adding the listeners to the UI widgets
        addListeners();
    }


    /**
     * Returns the primary control associated with this widget.
     *
     * @return the primary control associated with this widget.
     */
    public Control getControl()
    {
        return composite;
    }


    /**
     * Adds the listeners to the UI widgets.
     */
    private void addListeners()
    {
        editButton.addSelectionListener( editButtonSelectionListener );
    }


    /**
     * Sets the value.
     *
     * @param ints the value
     */
    public void setValue( int value )
    {
        this.value = value;

        label.setText( LogLevel.getLogLevelText( value ) );
    }


    /**
     * Gets the value.
     *
     * @return the value
     */
    public int getValue()
    {
        return value;
    }


    /**
     * Disposes all created SWT widgets.
     */
    public void dispose()
    {
        // Composite
        if ( ( composite != null ) && ( !composite.isDisposed() ) )
        {
            composite.dispose();
        }
    }
}
