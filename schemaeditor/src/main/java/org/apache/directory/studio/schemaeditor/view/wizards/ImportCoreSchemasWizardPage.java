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


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.model.Project;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.apache.directory.studio.schemaeditor.view.widget.CoreSchemasSelectionWidget;
import org.apache.directory.studio.schemaeditor.view.widget.CoreSchemasSelectionWidget.ServerTypeEnum;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;


/**
 * This class represents the {@link WizardPage} of the {@link ImportCoreSchemasWizard}.
 * <p>
 * It is used to let the user choose the 'core' schemas to import.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ImportCoreSchemasWizardPage extends AbstractWizardPage
{
    // UI Fields    
    private CoreSchemasSelectionWidget coreSchemaSelectionWidget;


    /**
     * Creates a new instance of ImportCoreSchemasWizardPage.
     */
    protected ImportCoreSchemasWizardPage()
    {
        super( "ImportCoreSchemasWizardPage" );
        setTitle( "Import core schemas" );
        setDescription( "Please select the 'core' schemas to import." );
        setImageDescriptor( Activator.getDefault().getImageDescriptor( PluginConstants.IMG_SCHEMAS_IMPORT_WIZARD ) );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent )
    {
        coreSchemaSelectionWidget = new CoreSchemasSelectionWidget();
        Composite composite = coreSchemaSelectionWidget.createWidget( parent );
        coreSchemaSelectionWidget.init( ServerTypeEnum.APACHE_DS );

        Project project = Activator.getDefault().getProjectsHandler().getOpenProject();
        if ( project != null )
        {
            List<Schema> schemas = project.getSchemaHandler().getSchemas();
            List<String> schemaNames = new ArrayList<String>();
            for ( Schema schema : schemas )
            {
                schemaNames.add( schema.getName() );
            }

            coreSchemaSelectionWidget.setGrayedCoreSchemas( schemaNames.toArray( new String[0] ) );
        }

        dialogChanged();

        setControl( composite );
    }


    /**
     * This method is called when the user modifies something in the UI.
     */
    private void dialogChanged()
    {
        // Checking if a Schema Project is open
        if ( Activator.getDefault().getSchemaHandler() == null )
        {
            displayErrorMessage( "A Schema Project must be open to import core schemas files." );
            return;
        }

        displayErrorMessage( null );
    }


    /**
     * Gets the schemas selected by the User.
     *
     * @return
     *      the selected schemas
     */
    public String[] getSelectedSchemas()
    {
        return coreSchemaSelectionWidget.getSelectedCoreSchemas();
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
