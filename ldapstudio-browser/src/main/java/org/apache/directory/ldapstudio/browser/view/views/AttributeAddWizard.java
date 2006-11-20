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

package org.apache.directory.ldapstudio.browser.view.views;

import org.eclipse.jface.wizard.Wizard;


/**
 * This class implements the Attribute Add Wizard
 */
public class AttributeAddWizard extends Wizard {

    private AttributeAddWizardPage attributeAddWizardPage;
    
    /**
     * Default constructor
     */
    public AttributeAddWizard() 
    {
    	attributeAddWizardPage = new AttributeAddWizardPage();
        addPage( attributeAddWizardPage );
        setWindowTitle( "Add a new attribute" );
	}
	
    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#needsPreviousAndNextButtons()
     */
    @Override
    public boolean needsPreviousAndNextButtons()
    {
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#canFinish()
     */
    @Override
    public boolean canFinish()
    {
        return attributeAddWizardPage.canFinish();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish()
    {
        return attributeAddWizardPage.performFinish();
    }
}
