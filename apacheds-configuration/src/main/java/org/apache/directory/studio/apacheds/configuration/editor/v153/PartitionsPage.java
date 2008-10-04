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
package org.apache.directory.studio.apacheds.configuration.editor.v153;


import org.apache.directory.studio.apacheds.configuration.ApacheDSConfigurationPluginConstants;
import org.apache.directory.studio.apacheds.configuration.editor.SaveableFormPage;
import org.apache.directory.studio.apacheds.configuration.editor.ServerConfigurationEditor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;


/**
 * This class represents the Partitions Page of the Server Configuration Editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class PartitionsPage extends FormPage implements SaveableFormPage
{
    /** The Page ID*/
    public static final String ID = ServerConfigurationEditor.ID + ".V153.PartitionsPage";

    /** The Page Title */
    private static final String TITLE = "Partitions";

    /** The Master/Details block */
    private PartitionsMasterDetailsBlock masterDetailsBlock;


    /**
     * Creates a new instance of PartitionsPage.
     *
     * @param editor
     *      the associated editor
     */
    public PartitionsPage( FormEditor editor )
    {
        super( editor, ID, TITLE );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
     */
    protected void createFormContent( IManagedForm managedForm )
    {
        PlatformUI.getWorkbench().getHelpSystem().setHelp( getPartControl(),
            ApacheDSConfigurationPluginConstants.PLUGIN_ID + "." + "configuration_editor_153" );

        ScrolledForm form = managedForm.getForm();
        form.setText( "Partitions" );
        masterDetailsBlock = new PartitionsMasterDetailsBlock( this );
        masterDetailsBlock.createContent( managedForm );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.configuration.editor.SavableWizardPage#save()
     */
    public void save()
    {
        if ( masterDetailsBlock != null )
        {
            masterDetailsBlock.save();
        }
    }
}
