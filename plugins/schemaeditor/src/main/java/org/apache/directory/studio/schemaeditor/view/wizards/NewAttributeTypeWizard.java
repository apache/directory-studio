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


import org.apache.directory.shared.ldap.model.schema.MutableAttributeTypeImpl;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.PluginUtils;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;


/**
 * This class represents the wizard to create a new AttributeType.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class NewAttributeTypeWizard extends Wizard implements INewWizard
{
    public static final String ID = PluginConstants.NEW_WIZARD_NEW_ATTRIBUTE_TYPE_WIZARD;

    /** The selected schema */
    private Schema selectedSchema;

    // The pages of the wizards
    private NewAttributeTypeGeneralWizardPage generalPage;
    private NewAttributeTypeContentWizardPage contentPage;
    private NewAttributeTypeMatchingRulesWizardPage matchingRulesPage;


    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    public void addPages()
    {
        // Creating pages
        generalPage = new NewAttributeTypeGeneralWizardPage();
        generalPage.setSelectedSchema( selectedSchema );
        contentPage = new NewAttributeTypeContentWizardPage();
        matchingRulesPage = new NewAttributeTypeMatchingRulesWizardPage();

        // Adding pages
        addPage( generalPage );
        addPage( contentPage );
        addPage( matchingRulesPage );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    public boolean performFinish()
    {
        // Creating the new attribute type
        MutableAttributeTypeImpl newAT = new MutableAttributeTypeImpl( generalPage.getOidValue() );
        newAT.setSchemaName( generalPage.getSchemaValue() );
        newAT.setNames( generalPage.getAliasesValue() );
        newAT.setDescription( generalPage.getDescriptionValue() );
        newAT.setSuperiorOid( contentPage.getSuperiorValue() );
        newAT.setUsage( contentPage.getUsageValue() );
        newAT.setSyntaxOid( contentPage.getSyntax() );
        newAT.setSyntaxLength( contentPage.getSyntaxLengthValue() );
        newAT.setObsolete( contentPage.getObsoleteValue() );
        newAT.setSingleValued( contentPage.getSingleValueValue() );
        newAT.setCollective( contentPage.getCollectiveValue() );
        newAT.setUserModifiable( !contentPage.getNoUserModificationValue() );
        newAT.setEqualityOid( matchingRulesPage.getEqualityMatchingRuleValue() );
        newAT.setOrderingOid( matchingRulesPage.getOrderingMatchingRuleValue() );
        newAT.setSubstringOid( matchingRulesPage.getSubstringMatchingRuleValue() );

        // Adding the new attribute type
        Activator.getDefault().getSchemaHandler().addAttributeType( newAT );

        // Saving the Dialog Settings OID History
        PluginUtils.saveDialogSettingsHistory( PluginConstants.DIALOG_SETTINGS_OID_HISTORY, newAT.getOid() );

        return true;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
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
