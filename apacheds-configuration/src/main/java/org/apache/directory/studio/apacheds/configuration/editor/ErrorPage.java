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
package org.apache.directory.studio.apacheds.configuration.editor;


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
 * This class represents the General Page of the Server Configuration Editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ErrorPage extends FormPage
{
    /** The Page ID*/
    public static final String ID = ServerConfigurationEditor.ID + ".ErrorPage"; //$NON-NLS-1$

    /** The Page Title */
    private static final String TITLE = Messages.getString("ErrorPage.Error"); //$NON-NLS-1$


    /**
     * Creates a new instance of GeneralPage.
     *
     * @param editor
     *      the associated editor
     */
    public ErrorPage( FormEditor editor )
    {
        super( editor, ID, TITLE );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
     */
    protected void createFormContent( IManagedForm managedForm )
    {
        ScrolledForm form = managedForm.getForm();
        FormToolkit toolkit = managedForm.getToolkit();
        Composite parent = form.getBody();
        parent.setLayout( new GridLayout( 2, false ) );

        // Error Image
        Label errorImageLabel = toolkit.createLabel( parent, null );
        errorImageLabel.setImage( Display.getCurrent().getSystemImage( SWT.ICON_ERROR ) );

        // Error Label
        toolkit.createLabel( parent, Messages.getString("ErrorPage.ErrorOpeningTheEditor") ); //$NON-NLS-1$

        // Details Label
        Label detailsLabel = toolkit.createLabel( parent, Messages.getString("ErrorPage.Details") ); //$NON-NLS-1$
        detailsLabel.setLayoutData( new GridData( SWT.NONE, SWT.NONE, false, false, 2, 1 ) );

        // Details Text
        Text detailsText = toolkit.createText( parent, "", SWT.MULTI ); //$NON-NLS-1$
        detailsText.setEditable( false );
        detailsText.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 2, 1 ) );
        detailsText.setText( ( ( ServerConfigurationEditor ) getEditor() ).getErrorMessage() );
    }
}
