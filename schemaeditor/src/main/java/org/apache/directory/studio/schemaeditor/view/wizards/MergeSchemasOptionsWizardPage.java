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


import org.apache.directory.studio.connection.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;


/**
 * This class represents the page to select options of the MergeSchemasWizard.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class MergeSchemasOptionsWizardPage extends WizardPage
{
    // UI Fields
    private Button replaceUnknowNSyntaxButton;
    private Button mergeDependenciesButton;
    private Button pullUpAttributesButton;


    /**
     * Creates a new instance of MergeSchemasOptionsWizardPage.
     */
    protected MergeSchemasOptionsWizardPage()
    {
        super( "MergeSchemasOptionsWizardPage" ); //$NON-NLS-1$
        setTitle( Messages.getString( "MergeSchemasSelectionWizardPage.ImportSchemasFromProjects" ) ); //$NON-NLS-1$
        setDescription( Messages.getString( "MergeSchemasSelectionWizardPage.SelectOptions" ) ); //$NON-NLS-1$
        setImageDescriptor( Activator.getDefault().getImageDescriptor( PluginConstants.IMG_SCHEMAS_IMPORT_WIZARD ) );
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

        replaceUnknowNSyntaxButton = BaseWidgetUtils.createCheckbox( composite, Messages
            .getString( "MergeSchemasOptionsWizardPage.ReplaceUnknownSyntax" ), 1 );
        replaceUnknowNSyntaxButton.setToolTipText( Messages
            .getString( "MergeSchemasOptionsWizardPage.ReplaceUnknownSyntaxTooltip" ) );
        replaceUnknowNSyntaxButton.setSelection( true );

        mergeDependenciesButton = BaseWidgetUtils.createCheckbox( composite, Messages
            .getString( "MergeSchemasOptionsWizardPage.MergeDependencies" ), 1 );
        mergeDependenciesButton.setToolTipText( Messages
            .getString( "MergeSchemasOptionsWizardPage.MergeDependenciesTooltip" ) );
        mergeDependenciesButton.setSelection( true );

        pullUpAttributesButton = BaseWidgetUtils.createCheckbox( composite, Messages
            .getString( "MergeSchemasOptionsWizardPage.PullUpAttributes" ), 1 );
        pullUpAttributesButton.setToolTipText( Messages
            .getString( "MergeSchemasOptionsWizardPage.PullUpAttributesTooltip" ) );
        pullUpAttributesButton.setSelection( true );

        setControl( composite );
    }


    public boolean isReplaceUnknownSyntax()
    {
        return replaceUnknowNSyntaxButton.getSelection();
    }


    public boolean isMergeDependencies()
    {
        return mergeDependenciesButton.getSelection();
    }


    public boolean isPullUpAttributes()
    {
        return pullUpAttributesButton.getSelection();
    }

}
