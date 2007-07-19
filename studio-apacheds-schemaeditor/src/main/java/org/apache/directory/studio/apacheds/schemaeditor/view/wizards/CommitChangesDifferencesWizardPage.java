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

import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.PluginConstants;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AliasDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.DescriptionDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.Difference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.DifferenceType;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.PropertyDifference;
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
        PropertyDifference diff = new AliasDifference( null, null, DifferenceType.ADDED );
        diff.setNewValue( "alias1" );
        differences.add( diff );
        
        diff = new AliasDifference( null, null, DifferenceType.REMOVED );
        diff.setOldValue( "alias2" );
        differences.add( diff );

        diff = new DescriptionDifference( null, null, DifferenceType.ADDED );
        diff.setNewValue( "Description" );
        differences.add( diff );
        
        diff = new DescriptionDifference( null, null, DifferenceType.MODIFIED );
        diff.setOldValue( "Old Description" );
        diff.setNewValue( "New Description" );
        differences.add( diff );
        
        diff = new DescriptionDifference( null, null, DifferenceType.REMOVED );
        diff.setOldValue( "Description" );
        differences.add( diff );
        

        //        differences.add( new AddEqualityDifference( null, null, "equality" ) );
        //        differences.add( new ModifyEqualityDifference( null, null, "old equality", "new equality" ) );
        //        differences.add( new RemoveEqualityDifference( null, null, "equality" ) );
        //        differences.add( new AddMandatoryATDifference( null, null, "name" ) );
        //        differences.add( new RemoveMandatoryATDifference( null, null, "name2" ) );
        //        differences.add( new AddOptionalATDifference( null, null, "name" ) );
        //        differences.add( new RemoveOptionalATDifference( null, null, "name2" ) );
        //        differences.add( new AddOrderingDifference( null, null, "ordering" ) );
        //        differences.add( new ModifyOrderingDifference( null, null, "old ordering", "new ordering" ) );
        //        differences.add( new RemoveOrderingDifference( null, null, "ordering" ) );
        //        differences.add( new AddSubstringDifference( null, null, "substring" ) );
        //        differences.add( new ModifySubstringDifference( null, null, "old substring", "new substring" ) );
        //        differences.add( new RemoveSubstringDifference( null, null, "substring" ) );
        //        differences.add( new AddSuperiorATDifference( null, null, "supAT" ) );
        //        differences.add( new ModifySuperiorATDifference( null, null, "oldSupAT", "newSupAT" ) );
        //        differences.add( new RemoveSuperiorATDifference( null, null, "supAT" ) );
        //        differences.add( new AddSuperiorOCDifference( null, null, "supOC" ) );
        //        differences.add( new RemoveSuperiorOCDifference( null, null, "supOC" ) );
        //        differences.add( new AddSyntaxDifference( null, null, "syntax" ) );
        //        differences.add( new ModifySyntaxDifference( null, null, "syntax1", "syntax2" ) );
        //        differences.add( new RemoveSyntaxDifference( null, null, "syntax" ) );
        //        differences.add( new AddSyntaxLengthDifference( null, null, 1234 ) );
        //        differences.add( new ModifySyntaxLengthDifference( null, null, 1234, 12345 ) );
        //        differences.add( new RemoveSyntaxLengthDifference( null, null, 1234 ) );
        //        differences.add( new ModifyClassTypeDifference( null, null, ObjectClassTypeEnum.AUXILIARY,
        //            ObjectClassTypeEnum.ABSTRACT ) );
        //        differences.add( new ModifyCollectiveDifference( null, null, false, true ) );
        //        differences.add( new ModifyNoUserModificationDifference( null, null, true, false ) );
        //        differences.add( new ModifyObsoleteDifference( null, null, true, false ) );
        //        differences.add( new ModifySingleValueDifference( null, null, true, false ) );
        //        differences.add( new ModifyUsageDifference( null, null, UsageEnum.DISTRIBUTED_OPERATION,
        //            UsageEnum.DSA_OPERATION ) );
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
