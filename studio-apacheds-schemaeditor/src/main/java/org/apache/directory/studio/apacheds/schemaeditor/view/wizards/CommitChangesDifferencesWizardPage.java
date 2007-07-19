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


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.shared.ldap.schema.ObjectClassTypeEnum;
import org.apache.directory.shared.ldap.schema.UsageEnum;
import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.PluginConstants;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AddAliasDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AddDescriptionDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AddEqualityDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AddMandatoryATDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AddOptionalATDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AddOrderingDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AddSubstringDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AddSuperiorATDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AddSuperiorOCDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AddSyntaxDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AddSyntaxLengthDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.Difference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifyClassTypeDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifyCollectiveDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifyDescriptionDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifyEqualityDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifyNoUserModificationDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifyObsoleteDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifyOrderingDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifySingleValueDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifySubstringDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifySuperiorATDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifySyntaxDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifySyntaxLengthDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifyUsageDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.RemoveAliasDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.RemoveDescriptionDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.RemoveEqualityDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.RemoveMandatoryATDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.RemoveOptionalATDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.RemoveOrderingDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.RemoveSubstringDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.RemoveSuperiorATDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.RemoveSuperiorOCDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.RemoveSyntaxDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.RemoveSyntaxLengthDifference;
import org.apache.directory.studio.apacheds.schemaeditor.view.widget.DifferencesWidget;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class represents the WizardPage of the ExportProjectsWizard.
 * <p>
 * It is used to let the user enter the informations about the
 * schemas projects he wants to export and where to export.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class CommitChangesDifferencesWizardPage extends WizardPage
{
    /**
     * Creates a new instance of ExportSchemasAsXmlWizardPage.
     */
    protected CommitChangesDifferencesWizardPage()
    {
        super( "CommitChangesDifferencesWizardPage" );
        setTitle( "Commit Changes" );
        setDescription( "Displays the modifications made on the schema." );
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
            PluginConstants.IMG_COMMIT_CHANGES_WIZARD ) );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NULL );
        GridLayout layout = new GridLayout();
        composite.setLayout( layout );

        DifferencesWidget differencesWidget = new DifferencesWidget();
        differencesWidget.createWidget( composite );

        List<Difference> differences = new ArrayList<Difference>();
        differences.add( new AddAliasDifference( null, null, "alias1" ) );
        differences.add( new RemoveAliasDifference( null, null, "alias2" ) );
        differences.add( new AddDescriptionDifference( null, null, "Description" ) );
        differences.add( new ModifyDescriptionDifference( null, null, "Old description", "New Description" ) );
        differences.add( new RemoveDescriptionDifference( null, null, "Description" ) );
        differences.add( new AddEqualityDifference( null, null, "equality" ) );
        differences.add( new ModifyEqualityDifference( null, null, "old equality", "new equality" ) );
        differences.add( new RemoveEqualityDifference( null, null, "equality" ) );
        differences.add( new AddMandatoryATDifference( null, null, "name" ) );
        differences.add( new RemoveMandatoryATDifference( null, null, "name2" ) );
        differences.add( new AddOptionalATDifference( null, null, "name" ) );
        differences.add( new RemoveOptionalATDifference( null, null, "name2" ) );
        differences.add( new AddOrderingDifference( null, null, "ordering" ) );
        differences.add( new ModifyOrderingDifference( null, null, "old ordering", "new ordering" ) );
        differences.add( new RemoveOrderingDifference( null, null, "ordering" ) );
        differences.add( new AddSubstringDifference( null, null, "substring" ) );
        differences.add( new ModifySubstringDifference( null, null, "old substring", "new substring" ) );
        differences.add( new RemoveSubstringDifference( null, null, "substring" ) );
        differences.add( new AddSuperiorATDifference( null, null, "supAT" ) );
        differences.add( new ModifySuperiorATDifference( null, null, "oldSupAT", "newSupAT" ) );
        differences.add( new RemoveSuperiorATDifference( null, null, "supAT" ) );
        differences.add( new AddSuperiorOCDifference( null, null, "supOC" ) );
        differences.add( new RemoveSuperiorOCDifference( null, null, "supOC" ) );
        differences.add( new AddSyntaxDifference( null, null, "syntax" ) );
        differences.add( new ModifySyntaxDifference( null, null, "syntax1", "syntax2" ) );
        differences.add( new RemoveSyntaxDifference( null, null, "syntax" ) );
        differences.add( new AddSyntaxLengthDifference( null, null, 1234 ) );
        differences.add( new ModifySyntaxLengthDifference( null, null, 1234, 12345 ) );
        differences.add( new RemoveSyntaxLengthDifference( null, null, 1234 ) );
        differences.add( new ModifyClassTypeDifference( null, null, ObjectClassTypeEnum.AUXILIARY,
            ObjectClassTypeEnum.ABSTRACT ) );
        differences.add( new ModifyCollectiveDifference( null, null, false, true ) );
        differences.add( new ModifyNoUserModificationDifference( null, null, true, false ) );
        differences.add( new ModifyObsoleteDifference( null, null, true, false ) );
        differences.add( new ModifySingleValueDifference( null, null, true, false ) );
        differences.add( new ModifyUsageDifference( null, null, UsageEnum.DISTRIBUTED_OPERATION,
            UsageEnum.DSA_OPERATION ) );
        differencesWidget.setInput( differences );

        initFields();

        setControl( composite );
    }


    /**
     * Initializes the UI Fields.
     */
    private void initFields()
    {

    }
}
