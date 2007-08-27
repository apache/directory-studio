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
package org.apache.directory.studio.apacheds.schemaeditor.view.wizards;


import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.PluginConstants;
import org.apache.directory.studio.apacheds.schemaeditor.model.Project;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.DifferenceEngine;
import org.apache.directory.studio.apacheds.schemaeditor.view.widget.DifferencesWidget;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class represents the WizardPage of the ExportProjectsWizard.
 * <p>
 * It is used to let the user enter the informations about the
 * schemas projects he wants to export and where to export.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class CommitChangesDifferencesWizardPage extends WizardPage
{
    // UI Fields
    private DifferencesWidget differencesWidget;


    /**
     * Creates a new instance of ExportSchemasAsXmlWizardPage.
     */
    protected CommitChangesDifferencesWizardPage()
    {
        super( "CommitChangesDifferencesWizardPage" );
        setTitle( "Commit Changes" );
        setDescription( "Displays the modifications made on the schema." );
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
            PluginConstants.IMG_COMMIT_CHANGES_WIZARD ) );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NULL );
        GridLayout layout = new GridLayout();
        composite.setLayout( layout );

        differencesWidget = new DifferencesWidget();
        differencesWidget.createWidget( composite );

        Project project = Activator.getDefault().getProjectsHandler().getOpenProject();

        differencesWidget.setInput( DifferenceEngine.getDifferences( project.getSchemaBackup(), project
            .getSchemaHandler().getSchemas() ) );

        initFields();

        setControl( composite );
    }


    /**
     * Initializes the UI Fields.
     */
    private void initFields()
    {

    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.DialogPage#dispose()
     */
    public void dispose()
    {
        differencesWidget.dispose();

        super.dispose();
    }
}
