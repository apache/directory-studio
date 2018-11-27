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
package org.apache.directory.studio.openldap.config.editor.pages;


import org.apache.directory.studio.openldap.config.editor.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;


/**
 * This class represents the Loading Page of the Server Configuration Editor. It is
 * just a plain page which expose a progress bar and a message :
 * 
 * <pre>
 * .---------------------------------------------.
 * | Loading Configuration...                    |
 * +---------------------------------------------+
 * |                                             |
 * |                                             |
 * |             [ \\ \\ \\ \\ \\ ]              |
 * |  Loading the configuration, please wait...  |
 * |                                             |
 * |                                             |
 * +---------------------------------------------+
 * </pre>
 * 
 * Once the configuration is loaded, this page is closed.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LoadingPage extends FormPage
{
    /** The Page ID*/
    public static final String ID = LoadingPage.class.getName(); //$NON-NLS-1$

    /** The Page Title */
    private static final String TITLE = Messages.getString( "LoadingPage.LoadingConfiguration" );


    /**
     * Creates a new instance of LoadingPage.
     *
     * @param editor the associated editor
     */
    public LoadingPage( FormEditor editor )
    {
        super( editor, ID, TITLE );
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void createFormContent( IManagedForm managedForm )
    {
        ScrolledForm form = managedForm.getForm();
        form.setText( Messages.getString( "LoadingPage.LoadingConfigurationEllipsis" ) );

        Composite parent = form.getBody();
        parent.setLayout( new GridLayout() );

        FormToolkit toolkit = managedForm.getToolkit();
        toolkit.decorateFormHeading( form.getForm() );

        Composite composite = toolkit.createComposite( parent );
        composite.setLayout( new GridLayout() );
        composite.setLayoutData( new GridData( SWT.CENTER, SWT.CENTER, true, true ) );

        ProgressBar progressBar = new ProgressBar( composite, SWT.INDETERMINATE );
        progressBar.setLayoutData( new GridData( SWT.CENTER, SWT.NONE, false, false ) );

        Label label = toolkit.createLabel( composite, Messages.getString( "LoadingPage.LoadingTheConfigurationPleaseWait" ) );
        label.setLayoutData( new GridData( SWT.CENTER, SWT.NONE, false, false ) );
    }
}
