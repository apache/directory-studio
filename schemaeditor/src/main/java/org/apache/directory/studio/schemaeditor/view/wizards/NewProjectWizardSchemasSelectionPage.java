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
import org.apache.directory.studio.schemaeditor.view.widget.CoreSchemasSelectionWidget;
import org.apache.directory.studio.schemaeditor.view.widget.CoreSchemasSelectionWidget.ServerTypeEnum;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;


/**
 * This class represents the Information Page of the NewProjectWizard.
 * <p>
 * It is used to let the user create a new Project
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class NewProjectWizardSchemasSelectionPage extends WizardPage
{
    // UI Fields    
    private CoreSchemasSelectionWidget coreSchemaSelectionWidget;


    /**
     * Creates a new instance of NewProjectWizardSchemasSelectionPage.
     */
    protected NewProjectWizardSchemasSelectionPage()
    {
        super( "NewProjectWizardSchemasSelectionPage" ); //$NON-NLS-1$
        setTitle( Messages.getString( "NewProjectWizardSchemasSelectionPage.CreateSchemaProject" ) ); //$NON-NLS-1$
        setDescription( Messages.getString( "NewProjectWizardSchemasSelectionPage.PleaseSelectCoreSchemaForInclude" ) ); //$NON-NLS-1$
        setImageDescriptor( Activator.getDefault().getImageDescriptor( PluginConstants.IMG_PROJECT_NEW_WIZARD ) );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent )
    {
        coreSchemaSelectionWidget = new CoreSchemasSelectionWidget();
        Composite composite = coreSchemaSelectionWidget.createWidget( parent );
        coreSchemaSelectionWidget.init( ServerTypeEnum.APACHE_DS );

        setControl( composite );
    }


    /**
     * Gets the schemas selected by the User.
     *
     * @return
     *      the selected schemas
     */
    public String[] getSelectedSchemas()
    {
        return coreSchemaSelectionWidget.getCheckedCoreSchemas();
    }


    /**
     * Gets the Server Type
     *
     * @return
     *      the Server Type
     */
    public ServerTypeEnum getServerType()
    {
        return coreSchemaSelectionWidget.getServerType();
    }
}
