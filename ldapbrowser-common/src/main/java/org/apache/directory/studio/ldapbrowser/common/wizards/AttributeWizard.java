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

package org.apache.directory.studio.ldapbrowser.common.wizards;


import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
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


/**
 * The AttributeWizard is used to create a new attribute or
 * to modify an existing attribute description. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class AttributeWizard extends Wizard implements INewWizard
{

    /** The type page. */
    private AttributeTypeWizardPage typePage;

    /** The options page. */
    private AttributeOptionsWizardPage optionsPage;

    /** The initial show subschema attributes only. */
    private boolean initialShowSubschemaAttributesOnly;

    /** The initial hide existing attributes. */
    private boolean initialHideExistingAttributes;

    /** The initial attribute description. */
    private String initialAttributeDescription;

    /** The initial entry. */
    private IEntry initialEntry;

    /** The final attribute description. */
    private String finalAttributeDescription = null;


    /**
     * Creates a new instance of AttributeWizard with an empty
     * attribute description.
     */
    public AttributeWizard()
    {
        super.setWindowTitle( Messages.getString("AttributeWizard.NewAttribute") ); //$NON-NLS-1$
        super.setNeedsProgressMonitor( false );
        this.initialShowSubschemaAttributesOnly = true;
        this.initialHideExistingAttributes = true;
        this.initialAttributeDescription = ""; //$NON-NLS-1$
        this.initialEntry = null;
    }


    /**
     * Creates a new instance of AttributeWizard with the given initial attribute description.
     * 
     * @param title the title
     * @param entry the entry
     * @param showSubschemaAttributesOnly the show subschema attributes only
     * @param hideExistingAttributes the hide existing attributes
     * @param attributeDescription the attribute description
     */
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


    /**
     * Gets the id.
     * 
     * @return the id
     */
    public static String getId()
    {
        return BrowserCommonConstants.WIZARD_ATTRIBUTE_WIZARD;
    }


    /**
     * {@inheritDoc}}
     */
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
    }


    /**
     * {@inheritDoc}}
     */
    public void addPages()
    {
        if ( initialEntry != null )
        {
            typePage = new AttributeTypeWizardPage( AttributeTypeWizardPage.class.getName(), initialEntry,
                initialAttributeDescription, initialShowSubschemaAttributesOnly, initialHideExistingAttributes, this );
            addPage( typePage );

            optionsPage = new AttributeOptionsWizardPage( AttributeOptionsWizardPage.class.getName(),
                initialAttributeDescription, this );
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
            BrowserCommonConstants.PLUGIN_ID + "." + "tools_attribute_wizard" ); //$NON-NLS-1$ //$NON-NLS-2$
        PlatformUI.getWorkbench().getHelpSystem().setHelp( optionsPage.getControl(),
            BrowserCommonConstants.PLUGIN_ID + "." + "tools_attribute_wizard" ); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /** 
     * A dummy wizard page to show the user that no entry is selected.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    class DummyWizardPage extends WizardPage
    {

        /**
         * Creates a new instance of DummyWizardPage.
         */
        protected DummyWizardPage()
        {
            super( "" ); //$NON-NLS-1$
            super.setTitle( Messages.getString("AttributeWizard.NoEntrySelected") ); //$NON-NLS-1$
            super.setDescription( Messages.getString("AttributeWizard.NoeEntrySelectedDescription") ); //$NON-NLS-1$
            // super.setImageDescriptor(BrowserUIPlugin.getDefault().getImageDescriptor(BrowserUIConstants.IMG_ATTRIBUTE_WIZARD));
            super.setPageComplete( true );
        }


        /**
         * {@inheritDoc}
         */
        public void createControl( Composite parent )
        {
            Composite composite = new Composite( parent, SWT.NONE );
            GridLayout gl = new GridLayout( 1, false );
            composite.setLayout( gl );
            composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );

            setControl( composite );
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean performFinish()
    {
        finalAttributeDescription = getAttributeDescription();
        return true;
    }


    /**
     * Gets the attribute description.
     * 
     * @return the attribute description
     */
    public String getAttributeDescription()
    {
        if ( finalAttributeDescription != null )
        {
            return finalAttributeDescription;
        }

        return typePage.getAttributeType() + optionsPage.getAttributeOptions();
    }

}
