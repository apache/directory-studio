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

package org.apache.directory.ldapstudio.browser.ui.wizards;


import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;


public class AttributeWizard extends Wizard implements INewWizard
{

    private AttributeTypeWizardPage typePage;

    private AttributeOptionsWizardPage optionsPage;

    private boolean initialShowSubschemaAttributesOnly;

    private boolean initialHideExistingAttributes;

    private String initialAttributeDescription;

    private IEntry initialEntry;

    private String finalAttributeDescription = null;


    public AttributeWizard()
    {
        super.setWindowTitle( "New Attribute" );
        super.setNeedsProgressMonitor( false );
        this.initialShowSubschemaAttributesOnly = true;
        this.initialHideExistingAttributes = true;
        this.initialAttributeDescription = "";
        this.initialEntry = null;
    }


    public AttributeWizard( String title, boolean showSubschemaAttributesOnly, boolean hideExistingAttributes,
        String attributeDescription, IEntry entry )
    {
        super.setWindowTitle( title );
        super.setNeedsProgressMonitor( false );
        this.initialShowSubschemaAttributesOnly = showSubschemaAttributesOnly;
        this.initialHideExistingAttributes = hideExistingAttributes;
        this.initialAttributeDescription = attributeDescription;
        this.initialEntry = entry;
    }


    public static String getId()
    {
        return AttributeWizard.class.getName();
    }


    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
    }


    public void addPages()
    {
        if ( this.initialEntry != null )
        {
            typePage = new AttributeTypeWizardPage( AttributeTypeWizardPage.class.getName(), this.initialEntry,
                this.initialAttributeDescription, this.initialShowSubschemaAttributesOnly,
                this.initialHideExistingAttributes, this );
            addPage( typePage );

            optionsPage = new AttributeOptionsWizardPage( AttributeOptionsWizardPage.class.getName(),
                this.initialAttributeDescription, this );
            addPage( optionsPage );
        }
        else
        {
            IWizardPage page = new DummyWizardPage();
            addPage( page );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void createPageControls( Composite pageContainer )
    {
        super.createPageControls( pageContainer );
        
        // set help context ID
        PlatformUI.getWorkbench().getHelpSystem().setHelp( typePage.getControl(),
            BrowserUIPlugin.PLUGIN_ID + "." + "tools_attribute_wizard" );
        PlatformUI.getWorkbench().getHelpSystem().setHelp( optionsPage.getControl(),
            BrowserUIPlugin.PLUGIN_ID + "." + "tools_attribute_wizard" );
    }


    class DummyWizardPage extends WizardPage
    {

        protected DummyWizardPage()
        {
            super( "" );
            super.setTitle( "No entry selected" );
            super.setDescription( "In order to use the attribute creation wizard please select an entry." );
            // super.setImageDescriptor(BrowserUIPlugin.getDefault().getImageDescriptor(BrowserUIConstants.IMG_ATTRIBUTE_WIZARD));
            super.setPageComplete( true );
        }


        public void createControl( Composite parent )
        {
            Composite composite = new Composite( parent, SWT.NONE );
            GridLayout gl = new GridLayout( 1, false );
            composite.setLayout( gl );
            composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );

            setControl( composite );
        }
    }


    public boolean performCancel()
    {
        return true;
    }


    public boolean performFinish()
    {
        finalAttributeDescription = getAttributeDescription();
        return true;
    }


    public String getAttributeDescription()
    {
        if ( finalAttributeDescription != null )
        {
            return finalAttributeDescription;
        }

        return typePage.getAttributeType() + optionsPage.getAttributeOptions();
    }

}
