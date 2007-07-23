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
import org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.Schema;
import org.apache.directory.studio.apacheds.schemaeditor.model.SchemaImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AliasDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.DescriptionDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.Difference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.DifferenceEngine;
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
    // UI Fields
    private DifferencesWidget differencesWidget;


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

        differencesWidget = new DifferencesWidget();
        differencesWidget.createWidget( composite );

        SchemaImpl schema1Old = new SchemaImpl( "Schema1" );
        SchemaImpl schema1New = new SchemaImpl( "Schema1" );
        SchemaImpl schema2 = new SchemaImpl( "Schema2" );
        SchemaImpl schema3 = new SchemaImpl( "Schema3" );
        SchemaImpl schema4 = new SchemaImpl( "Schema4" );

        List<Schema> schemasListOld = new ArrayList<Schema>();
        schemasListOld.add( schema1Old );
        schemasListOld.add( schema2 );
        schemasListOld.add( schema4 );

        List<Schema> schemasListNew = new ArrayList<Schema>();
        schemasListNew.add( schema4 );
        schemasListNew.add( schema1New );
        schemasListNew.add( schema3 );

        AttributeTypeImpl at1 = new AttributeTypeImpl( "1.2.1" );
        at1.setNames( new String[]
            { "AT1", "AttributeType1" } );
        AttributeTypeImpl at2 = new AttributeTypeImpl( "1.2.2" );
        at2.setNames( new String[]
            { "AT2", "AttributeType2" } );
        AttributeTypeImpl at2Bis = new AttributeTypeImpl( "1.2.2" );
        at2Bis.setNames( new String[]
            { "AT2" } );
        AttributeTypeImpl at3 = new AttributeTypeImpl( "1.2.3" );
        at3.setNames( new String[]
            { "AT3" } );
        schema1Old.addAttributeType( at1 );
        schema1Old.addAttributeType( at2 );
        schema1New.addAttributeType( at2Bis );
        schema1New.addAttributeType( at3 );

        ObjectClassImpl oc1 = new ObjectClassImpl( "1.2.10" );
        oc1.setNames( new String[]
            { "OC1", "ObjectClass1" } );
        ObjectClassImpl oc2 = new ObjectClassImpl( "1.2.11" );
        oc2.setNames( new String[]
            { "OC2", "ObjectClass2" } );
        ObjectClassImpl oc2Bis = new ObjectClassImpl( "1.2.11" );
        oc2Bis.setNames( new String[]
            { "OC2" } );
        ObjectClassImpl oc3 = new ObjectClassImpl( "1.2.12" );
        oc3.setNames( new String[]
            { "OC3" } );
        schema1Old.addObjectClass( oc1 );
        schema1Old.addObjectClass( oc2 );
        schema1New.addObjectClass( oc2Bis );
        schema1New.addObjectClass( oc3 );

        differencesWidget.setInput( DifferenceEngine.getDifferences( schemasListOld, schemasListNew ) );

        initFields();

        setControl( composite );
    }


    /**
     * Initializes the UI Fields.
     */
    private void initFields()
    {

    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.DialogPage#dispose()
     */
    public void dispose()
    {
        differencesWidget.dispose();

        super.dispose();
    }
}
