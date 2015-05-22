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


import java.text.ParseException;

import org.apache.directory.studio.common.ui.widgets.AbstractWidget;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.eclipse.osgi.util.NLS;
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
import org.apache.directory.studio.openldap.common.ui.dialogs.UnixPermissions;
import org.apache.directory.studio.openldap.common.ui.dialogs.UnixPermissionsDialog;


/**
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class UnixPermissionsWidget extends AbstractWidget
{
    // The value
    private String value;

    // UI widgets
    private Composite composite;
    private Text label;
    private Button editButton;

    // Listeners
    private SelectionListener editButtonSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            // Creating and opening a UNIX permission dialog
            UnixPermissionsDialog dialog = new UnixPermissionsDialog( editButton.getShell(), value );

            if ( UnixPermissionsDialog.OK == dialog.open() )
            {
                setValue( dialog.getDecimalValue() );
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
            editButton = toolkit.createButton( composite, "Edit Permissions...", SWT.PUSH );
        }
        else
        {
            editButton = BaseWidgetUtils.createButton( composite, "Edit Permissions...", 1 );
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
     * @param s the value
     */
    public void setValue( String s )
    {
        value = s;

        UnixPermissions perm = null;

        try
        {
            perm = new UnixPermissions( s );
        }
        catch ( ParseException e )
        {
            perm = new UnixPermissions();
        }

        label.setText( NLS.bind( "{0} ({1})", perm.getSymbolicValue(), perm.getOctalValue() ) );
    }


    /**
     * Gets the value.
     *
     * @return the value
     */
    public String getValue()
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
