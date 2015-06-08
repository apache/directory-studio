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
import org.apache.directory.studio.openldap.common.ui.dialogs.SsfDialog;
import org.apache.directory.studio.openldap.common.ui.model.SsfEnum;


/**
 * A widget used to configure the OpenLDAP SSF. SSF (Security Strength Factors) associates
 * a strength to a feature. Here is the list of feature that support a SSF :
 * <ul>
 * <li>ssf : global</li>
 * <li>transport</li>
 * <li>tls</li>
 * <li>sasl</li>
 * <li>simple_bind</li>
 * <li>update_ssf</li>
 * <li>update_transport</li>
 * <li>update_tls</li>
 * <li>update_sasl</li>
 * </ul>
 * The ssf valus will generally depend on the number of bits used by the cipher to use,
 * with two special values : 0 and 1. Here is a set of possible values :
 * <ul>
 * <li>0 : no protection</li>
 * <li>1 : integrity check only</li>
 * <li>56 : DES (key length is 56 bits)</li>
 * <li>112 : 3DES (key length is 112 bits)</li>
 * <li>128 : RC4, Blowish, AES-128</li>
 * <li>256 : AES-256</li>
 * </ul>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SsfWidget extends AbstractWidget
{
    /** The SSF-features */
    private SsfEnum ssf;

    // UI widgets
    private Composite composite;
    private Text label;
    private Button editButton;

    // Listeners
    private SelectionListener editButtonSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            // Creating and opening a SSF dialog
            SsfDialog dialog = new SsfDialog( editButton.getShell(), null );

            if ( LogLevelDialog.OK == dialog.open() )
            {
                //setValue( dialog.getLogLevelValue() );
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
        //this.value = value;

        label.setText( LogLevel.getLogLevelText( value ) );
    }


    /**
     * Gets the value.
     *
     * @return the value
     */
    public int getValue()
    {
        return 0;
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
