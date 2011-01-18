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
package org.apache.directory.studio.apacheds.configuration.v2.editor;


import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;


/**
 * This class represents the Error Page of the Server Configuration Editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ErrorPage extends FormPage
{
    /** The Page ID*/
    public static final String ID = ErrorPage.class.getName();

    /** The Page Title */
    private static final String TITLE = "Error opening the editor";

    private static final String DETAILS_CLOSED = NLS.bind( "{0} >>", "Details" );
    private static final String DETAILS_OPEN = NLS.bind( "<< {0}", "Details" );

    /** The exception */
    private Exception exception;

    /** The flag indicating that the details are shown */
    private boolean detailsShown = false;

    // UI Controls
    private FormToolkit toolkit;
    private Composite parent;
    private Button detailsButton;

    private Text detailsText;


    /**
     * Creates a new instance of ErrorPage.
     *
     * @param editor
     *      the associated editor
     */
    public ErrorPage( FormEditor editor, Exception exception )
    {
        super( editor, ID, TITLE );
        this.exception = exception;
    }


    /**
     * {@inheritDoc}
     */
    protected void createFormContent( IManagedForm managedForm )
    {
        ScrolledForm form = managedForm.getForm();
        form.setText( "Error opening the editor" );
        form.setImage( Display.getCurrent().getSystemImage( SWT.ICON_ERROR ) );

        parent = form.getBody();
        GridLayout gl = new GridLayout( 2, false );
        gl.marginHeight = 10;
        gl.marginWidth = 10;
        parent.setLayout( gl );
        parent.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        toolkit = managedForm.getToolkit();
        toolkit.decorateFormHeading( form.getForm() );

        // Error Label
        Label errorLabel = toolkit.createLabel( parent,
            NLS.bind( "Could not open the editor: {0}", exception.getMessage() ) );
        errorLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Details Button
        detailsButton = new Button( parent, SWT.PUSH );
        detailsButton.setText( DETAILS_CLOSED );
        detailsButton.setLayoutData( new GridData( SWT.RIGHT, SWT.NONE, false, false ) );
        detailsButton.addSelectionListener( new SelectionAdapter()
        {
            @Override
            public void widgetSelected( SelectionEvent e )
            {
                showOrHideDetailsView();
            }
        } );
    }


    /**
     * Shows or hides the details view.
     */
    private void showOrHideDetailsView()
    {
        if ( detailsShown )
        {
            detailsButton.setText( DETAILS_CLOSED );

            detailsText.dispose();
        }
        else
        {
            detailsButton.setText( DETAILS_OPEN );

            detailsText = toolkit.createText( parent, getStackTrace( exception ), SWT.H_SCROLL | SWT.V_SCROLL );
            detailsText.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 2, 1 ) );
        }

        parent.layout( true, true );

        detailsShown = !detailsShown;
    }


    /**
     * Gets the stackTrace of the given exception as a string.
     *
     * @param e
     *      the exception
     * @return
     *      the stackTrace of the given exception as a string
     */
    private String getStackTrace( Exception e )
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter( sw, true );
        e.printStackTrace( pw );
        pw.flush();
        sw.flush();
        return sw.toString();
    }
}
