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


import org.apache.directory.api.ldap.model.schema.MutableObjectClass;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.PluginUtils;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;


/**
 * This class represents the wizard to create a new ObjectClass.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class NewObjectClassWizard extends Wizard implements INewWizard
{
    public static final String ID = PluginConstants.NEW_WIZARD_NEW_OBJECT_CLASS_WIZARD;

    /** The selected schema */
    private Schema selectedSchema;

    // The pages of the wizards
    private NewObjectClassGeneralPageWizardPage generalPage;
    private NewObjectClassContentWizardPage contentPage;
    private NewObjectClassMandatoryAttributesPage mandatoryAttributesPage;
    private NewObjectClassOptionalAttributesPage optionalAttributesPage;


    /**
     * {@inheritDoc}
     */
    public void addPages()
    {
        // Creating pages
        generalPage = new NewObjectClassGeneralPageWizardPage();
        generalPage.setSelectedSchema( selectedSchema );
        contentPage = new NewObjectClassContentWizardPage();
        mandatoryAttributesPage = new NewObjectClassMandatoryAttributesPage();
        optionalAttributesPage = new NewObjectClassOptionalAttributesPage();

        // Adding pages
        addPage( generalPage );
        addPage( contentPage );
        addPage( mandatoryAttributesPage );
        addPage( optionalAttributesPage );
    }


    /**
     * {@inheritDoc}
     */
    public boolean performFinish()
    {
        // Creating the new object class
        MutableObjectClass newOC = new MutableObjectClass( generalPage.getOidValue() );
        newOC.setSchemaName( generalPage.getSchemaValue() );
        newOC.setNames( generalPage.getAliasesValue() );
        newOC.setDescription( generalPage.getDescriptionValue() );
        newOC.setSuperiorOids( contentPage.getSuperiorsNameValue() );
        newOC.setType( contentPage.getClassTypeValue() );
        newOC.setObsolete( contentPage.getObsoleteValue() );
        newOC.setMustAttributeTypeOids( mandatoryAttributesPage.getMandatoryAttributeTypesNames() );
        newOC.setMayAttributeTypeOids( optionalAttributesPage.getOptionalAttributeTypesNames() );

        // Adding the new object class
        Activator.getDefault().getSchemaHandler().addObjectClass( newOC );

        // Saving the Dialog Settings OID History
        PluginUtils.saveDialogSettingsHistory( PluginConstants.DIALOG_SETTINGS_OID_HISTORY, newOC.getOid() );

        return true;
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        // Nothing to do.
    }


    /**
     * Sets the selected schema.
     *
     * @param schema
     *      the selected schema
     */
    public void setSelectedSchema( Schema schema )
    {
        selectedSchema = schema;
    }
}
