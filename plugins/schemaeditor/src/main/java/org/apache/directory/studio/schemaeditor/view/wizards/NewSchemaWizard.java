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
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;


/**
 * This class represents the wizard to create a new Schema.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class NewSchemaWizard extends Wizard implements INewWizard
{
    public static final String ID = PluginConstants.NEW_WIZARD_NEW_SCHEMA_WIZARD;

    // The pages of the wizard
    private NewSchemaWizardPage page;


    /**
     * {@inheritDoc}
     */
    public void addPages()
    {
        // Creating pages
        page = new NewSchemaWizardPage();

        // Adding pages
        addPage( page );
    }


    /**
     * {@inheritDoc}
     */
    public boolean performFinish()
    {
        Schema schema = new Schema( page.getSchemaName() );
        schema.setProject( Activator.getDefault().getProjectsHandler().getOpenProject() );
        Activator.getDefault().getSchemaHandler().addSchema( schema );

        return true;
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        // Nothing to do.
    }
}
