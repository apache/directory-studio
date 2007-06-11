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

package org.apache.directory.studio.schemas.view.wizards;


import org.apache.directory.server.core.tools.schema.AttributeTypeLiteral;
import org.apache.directory.studio.schemas.model.AttributeType;
import org.apache.directory.studio.schemas.model.Schema;
import org.apache.directory.studio.schemas.model.SchemaPool;
import org.apache.directory.studio.schemas.view.editors.attributeType.AttributeTypeEditor;
import org.apache.directory.studio.schemas.view.editors.attributeType.AttributeTypeEditorInput;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


/**
 * Wizard for creation of a new attribute type
 */
public class CreateANewAttributeTypeWizard extends Wizard implements INewWizard
{
    /** The default page */
    private CreateANewAttributeTypeWizardPage page;

    /** The schema name */
    private String schemaName;


    /**
     * Creates a new instance of CreateANewAttributeTypeWizard.
     * 
     * @param schemaName
     *            the schema name in which should be added the new attribute
     *            type
     */
    public CreateANewAttributeTypeWizard( String schemaName )
    {
        super();
        this.schemaName = schemaName;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    public boolean performFinish()
    {
        // Getting the SchemaPool
        SchemaPool pool = SchemaPool.getInstance();

        // Getting the right schema
        Schema schema = pool.getSchema( schemaName );

        // Creating the new attribute type and adding it to the schema
        AttributeTypeLiteral attributeTypeLiteral = new AttributeTypeLiteral( this.page.getOidField() );
        attributeTypeLiteral.setNames( new String[]
            { this.page.getNameField() } );
        AttributeType attributeType = new AttributeType( attributeTypeLiteral, schema );
        schema.addAttributeType( attributeType );

        // Opening the associated editor
        AttributeTypeEditorInput input = new AttributeTypeEditorInput( attributeType );
        String editorId = AttributeTypeEditor.ID;
        try
        {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor( input, editorId );
        }
        catch ( PartInitException e )
        {
            // TODO Log exception.
        }

        return true;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    public void addPages()
    {
        this.page = new CreateANewAttributeTypeWizardPage();
        addPage( page );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
    }
}
