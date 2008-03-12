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
package org.apache.directory.studio.schemaeditor.view.wizards;


import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;


/**
 * This class represents the InformationWizardPage of the CommitChangesWizard.
 * <p>
 * It is used to let the user enter the informations about the
 * schemas projects he wants to export and where to export.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class CommitChangesInformationWizardPage extends WizardPage
{
    // UI Fields

    /**
     * Creates a new instance of ExportSchemasAsXmlWizardPage.
     */
    protected CommitChangesInformationWizardPage()
    {
        super( "CommitChangesInformationWizardPage" );
        setTitle( "Commit Changes" );
        setDescription( "Please read the following information before committing the changes made on the schema." );
        setImageDescriptor( Activator.getDefault().getImageDescriptor( PluginConstants.IMG_COMMIT_CHANGES_WIZARD ) );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NULL );
        GridLayout layout = new GridLayout();
        composite.setLayout( layout );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Information Label
        String informationString = "You are about to commit changes to Apache Directory Server." + "\n\n"
            + "Please carefully review the changes made on the schema on the next page." + "\n\n"
            + "Commiting changes with an inconsistent schema may corrupt you server.";
        Label informationLabel = new Label( composite, SWT.WRAP );
        informationLabel.setText( informationString );
        informationLabel.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, true ) );

        // Warning Label
        Label warningLabel = new Label( composite, SWT.NONE );
        warningLabel.setImage( Activator.getDefault().getImage( PluginConstants.IMG_WARNING_32X32 ) );
        warningLabel.setLayoutData( new GridData( SWT.CENTER, SWT.BOTTOM, true, true ) );

        setControl( composite );
    }

}
