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

package org.apache.directory.ldapstudio.schemas.view.wizards;


import org.apache.directory.ldapstudio.schemas.model.ObjectClass;
import org.apache.directory.ldapstudio.schemas.model.Schema;
import org.apache.directory.ldapstudio.schemas.model.SchemaPool;
import org.apache.directory.ldapstudio.schemas.view.editors.ObjectClassFormEditor;
import org.apache.directory.ldapstudio.schemas.view.editors.ObjectClassFormEditorInput;
import org.apache.directory.server.core.tools.schema.ObjectClassLiteral;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


/**
 * Wizard for creation of a new object class
 */
public class CreateANewObjectClassWizard extends Wizard implements INewWizard
{

    private ISelection selection;

    private CreateANewObjectClassWizardPage page;

    private String schemaName;


    /**
     * Default constructor
     * 
     * @param schemaName
     *            the schema name in which should be added the new object class
     */
    public CreateANewObjectClassWizard( String schemaName )
    {
        super();
        this.schemaName = schemaName;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish()
    {
        // Getting the SchemaPool
        SchemaPool pool = SchemaPool.getInstance();

        // Getting the right schema
        Schema schema = pool.getSchema( schemaName );

        // Creating the new object class and adding it to the schema
        ObjectClassLiteral objectClassLiteral = new ObjectClassLiteral( this.page.getOidField() );
        objectClassLiteral.setNames( new String[] { this.page.getNameField() } );
        objectClassLiteral.setSuperiors( new String[]{ "top" } ); //$NON-NLS-1$
        ObjectClass objectClass = new ObjectClass( objectClassLiteral, schema );
        schema.addObjectClass( objectClass );

        // Opening the associated editor
        ObjectClassFormEditorInput input = new ObjectClassFormEditorInput( objectClass );
        String editorId = ObjectClassFormEditor.ID;
        try
        {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor( input, editorId );
        }
        catch ( PartInitException e )
        {
        }
        
        return true;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
     *      org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        this.selection = selection;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    public void addPages()
    {
        this.page = new CreateANewObjectClassWizardPage( selection );
        addPage( page );
    }
}
