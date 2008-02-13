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


import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.model.DependenciesComputer;
import org.apache.directory.studio.schemaeditor.model.Project;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.apache.directory.studio.schemaeditor.model.DependenciesComputer.DependencyComputerException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;


/**
 * This class represents the wizard to commit changes to the Apache Directory Server.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class CommitChangesWizard extends Wizard implements IExportWizard
{
    /** The wizard's ID */
    public static final String ID = Activator.PLUGIN_ID + ".wizards.CommitChangesWizard";

    /** The flag to know if the Schema contains errors */
    private boolean schemaContainsErrors = false;

    /** The project */
    private Project project;

    /** The DependenciesComputer */
    private DependenciesComputer dependenciesComputer;

    // The pages of the wizard
    private CommitChangesInformationWizardPage commitChangesInformation;
    private CommitChangesDifferencesWizardPage commitChangesDifferences;


    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    public void addPages()
    {
        // Creating pages
        commitChangesInformation = new CommitChangesInformationWizardPage();
        commitChangesDifferences = new CommitChangesDifferencesWizardPage();

        // Adding pages
        addPage( commitChangesInformation );
        addPage( commitChangesDifferences );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    public boolean performFinish()
    {
        final List<Schema> orderedSchemas = dependenciesComputer.getDependencyOrderedSchemasList();

        try
        {
            getContainer().run( false, false, new IRunnableWithProgress()
            {
                public void run( IProgressMonitor monitor )
                {
                    //TODO implement
                }
            } );
        }
        catch ( InvocationTargetException e )
        {
            // Nothing to do (it will never occur)
        }
        catch ( InterruptedException e )
        {
            // Nothing to do.
        }

        return true;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#canFinish()
     */
    public boolean canFinish()
    {
        if ( schemaContainsErrors )
        {
            return false;
        }
        else
        {
            return ( getContainer().getCurrentPage() instanceof CommitChangesDifferencesWizardPage );
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        setNeedsProgressMonitor( true );

        project = Activator.getDefault().getProjectsHandler().getOpenProject();

        try
        {
            dependenciesComputer = new DependenciesComputer( project.getSchemaHandler().getSchemas() );
        }
        catch ( DependencyComputerException e )
        {
            schemaContainsErrors = true;
        }
    }


    /**
     * Gets the SchemaContainsErrors flag.
     *
     * @return
     *      the SchemaContainsErrors flag
     */
    public boolean isSchemaContainsErrors()
    {
        return schemaContainsErrors;
    }


    /**
     * Gets the DependenciesComputer.
     *
     * @return
     *      the DependenciesComputer
     */
    public DependenciesComputer getDependenciesComputer()
    {
        return dependenciesComputer;
    }
}
