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
import org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;


/**
 * This class represents the wizard to create a new AttributeType.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewAttributeTypeWizard extends Wizard implements INewWizard
{
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
        AttributeTypeImpl newAT = new AttributeTypeImpl( generalPage.getOidValue() );
        newAT.setSchema( generalPage.getSchemaValue() );
        newAT.setNames( generalPage.getAliasesValue() );
        newAT.setDescription( generalPage.getDescriptionValue() );
        newAT.setSuperiorName( contentPage.getSuperiorValue() );
        newAT.setUsage( contentPage.getUsageValue() );
        newAT.setSyntaxOid( contentPage.getSyntax() );
        newAT.setLength( contentPage.getSyntaxLengthValue() );
        newAT.setObsolete( contentPage.getObsoleteValue() );
        newAT.setSingleValue( contentPage.getSingleValueValue() );
        newAT.setCollective( contentPage.getCollectiveValue() );
        newAT.setCanUserModify( contentPage.getNoUserModificationValue() );
        newAT.setEqualityName( matchingRulesPage.getEqualityMatchingRuleValue() );
        newAT.setOrderingName( matchingRulesPage.getOrderingMatchingRuleValue() );
        newAT.setSubstrName( matchingRulesPage.getSubstringMatchingRuleValue() );
        
        Activator.getDefault().getSchemaHandler().addAttributeType( newAT );
        
        return true;
    }
    

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        // Nothing to do.
    }
}
