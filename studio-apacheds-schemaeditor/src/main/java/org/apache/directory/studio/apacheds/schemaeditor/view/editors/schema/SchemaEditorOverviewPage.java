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

package org.apache.directory.studio.apacheds.schemaeditor.view.editors.schema;


import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.PluginConstants;
import org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaListener;
import org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.MatchingRuleImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.Schema;
import org.apache.directory.studio.apacheds.schemaeditor.model.SyntaxImpl;
import org.apache.directory.studio.apacheds.schemaeditor.view.editors.attributetype.AttributeTypeEditor;
import org.apache.directory.studio.apacheds.schemaeditor.view.editors.attributetype.AttributeTypeEditorInput;
import org.apache.directory.studio.apacheds.schemaeditor.view.editors.objectclass.ObjectClassEditor;
import org.apache.directory.studio.apacheds.schemaeditor.view.editors.objectclass.ObjectClassEditorInput;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class is the Overview Page of the Schema Editore.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SchemaEditorOverviewPage extends FormPage
{
    /** The page ID */
    public static final String ID = SchemaEditor.ID + "overviewPage"; //$NON-NLS-1$

    /** The page title */
    public static final String TITLE = "Overview";

    /** The SchemaHandler */
    private SchemaHandler schemaHandler;

    /** The associated schema */
    private Schema schema;

    private SchemaListener schemaListener = new SchemaListener()
    {
        /* (non-Javadoc)
         * @see org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaListener#attributeTypeAdded(org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl)
         */
        public void attributeTypeAdded( AttributeTypeImpl at )
        {
            fillInUiFields();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaListener#attributeTypeModified(org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl)
         */
        public void attributeTypeModified( AttributeTypeImpl at )
        {
            fillInUiFields();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaListener#attributeTypeRemoved(org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl)
         */
        public void attributeTypeRemoved( AttributeTypeImpl at )
        {
            fillInUiFields();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaListener#matchingRuleAdded(org.apache.directory.studio.apacheds.schemaeditor.model.MatchingRuleImpl)
         */
        public void matchingRuleAdded( MatchingRuleImpl mr )
        {
            fillInUiFields();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaListener#matchingRuleModified(org.apache.directory.studio.apacheds.schemaeditor.model.MatchingRuleImpl)
         */
        public void matchingRuleModified( MatchingRuleImpl mr )
        {
            fillInUiFields();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaListener#matchingRuleRemoved(org.apache.directory.studio.apacheds.schemaeditor.model.MatchingRuleImpl)
         */
        public void matchingRuleRemoved( MatchingRuleImpl mr )
        {
            fillInUiFields();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaListener#objectClassAdded(org.apache.directory.studio.apacheds.schemaeditor.model.ObjectClassImpl)
         */
        public void objectClassAdded( ObjectClassImpl oc )
        {
            fillInUiFields();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaListener#objectClassModified(org.apache.directory.studio.apacheds.schemaeditor.model.ObjectClassImpl)
         */
        public void objectClassModified( ObjectClassImpl oc )
        {
            fillInUiFields();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaListener#objectClassRemoved(org.apache.directory.studio.apacheds.schemaeditor.model.ObjectClassImpl)
         */
        public void objectClassRemoved( ObjectClassImpl oc )
        {
            fillInUiFields();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaListener#syntaxAdded(org.apache.directory.studio.apacheds.schemaeditor.model.SyntaxImpl)
         */
        public void syntaxAdded( SyntaxImpl syntax )
        {
            fillInUiFields();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaListener#syntaxModified(org.apache.directory.studio.apacheds.schemaeditor.model.SyntaxImpl)
         */
        public void syntaxModified( SyntaxImpl syntax )
        {
            fillInUiFields();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaListener#syntaxRemoved(org.apache.directory.studio.apacheds.schemaeditor.model.SyntaxImpl)
         */
        public void syntaxRemoved( SyntaxImpl syntax )
        {
            fillInUiFields();
        }
    };

    // UI Fields
    private Table attributeTypesTable;
    private Table objectClassesTable;

    // Listeners
    /** The listener of the Attribute Types Table*/
    private MouseAdapter attributeTypesTableListener = new MouseAdapter()
    {
        public void mouseDoubleClick( MouseEvent e )
        {
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

            AttributeTypeEditorInput input = new AttributeTypeEditorInput( schemaHandler
                .getAttributeType( attributeTypesTable.getSelection()[0].getText() ) );
            String editorId = AttributeTypeEditor.ID;
            try
            {
                page.openEditor( input, editorId );
            }
            catch ( PartInitException exception )
            {
                Logger.getLogger( SchemaEditorOverviewPage.class ).debug( "error when opening the editor" ); //$NON-NLS-1$
            }
        }
    };
    /** The listener of the Object Classes Table*/
    private MouseAdapter objectClassesTableListener = new MouseAdapter()
    {
        public void mouseDoubleClick( MouseEvent e )
        {
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

            ObjectClassEditorInput input = new ObjectClassEditorInput( schemaHandler.getObjectClass( objectClassesTable
                .getSelection()[0].getText() ) );
            String editorId = ObjectClassEditor.ID;
            try
            {
                page.openEditor( input, editorId );
            }
            catch ( PartInitException exception )
            {
                Logger.getLogger( SchemaEditorOverviewPage.class ).debug( "error when opening the editor" ); //$NON-NLS-1$
            }
        }
    };


    /**
     * Creates a new instance of SchemaFormEditorOverviewPage.
     *
     * @param editor
     *      the associated editor
     */
    public SchemaEditorOverviewPage( FormEditor editor )
    {
        super( editor, ID, TITLE );
        schemaHandler = Activator.getDefault().getSchemaHandler();
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
     */
    protected void createFormContent( IManagedForm managedForm )
    {
        // Getting the associated schema
        schema = ( ( SchemaEditor ) getEditor() ).getSchema();
        schemaHandler.addListener( schema, schemaListener );

        // Creating the base UI
        ScrolledForm form = managedForm.getForm();
        FormToolkit toolkit = managedForm.getToolkit();
        GridLayout layout = new GridLayout( 2, true );
        form.getBody().setLayout( layout );

        createAttributeTypesSection( form.getBody(), toolkit );

        createObjectClassesSection( form.getBody(), toolkit );

        // Initializes the UI from the schema
        fillInUiFields();

        // Listeners initialization
        addListeners();
    }


    /**
     * Create the Attribute Types Section.
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the FormToolKit to use
     */
    private void createAttributeTypesSection( Composite parent, FormToolkit toolkit )
    {
        // Attribute Types Section
        Section attributeTypesSection = toolkit.createSection( parent, Section.DESCRIPTION | Section.EXPANDED
            | Section.TITLE_BAR );
        attributeTypesSection.setDescription( "The schema '" + schema.getName()
            + "' contains the following attribute types." );
        attributeTypesSection.setText( "Attribute Types" );

        // Creating the layout of the section
        Composite attributeTypesSectionClient = toolkit.createComposite( attributeTypesSection );
        attributeTypesSectionClient.setLayout( new GridLayout() );
        toolkit.paintBordersFor( attributeTypesSectionClient );
        attributeTypesSection.setClient( attributeTypesSectionClient );
        attributeTypesSection.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, true ) );

        attributeTypesTable = toolkit.createTable( attributeTypesSectionClient, SWT.NONE );
        GridData gridData = new GridData( GridData.FILL, GridData.FILL, true, true );
        gridData.heightHint = 1;
        attributeTypesTable.setLayoutData( gridData );
    }


    /**
     * Create the Object Classes Section.
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the FormToolKit to use
     */
    private void createObjectClassesSection( Composite parent, FormToolkit toolkit )
    {
        // Attribute Types Section
        Section objectClassesSection = toolkit.createSection( parent, Section.DESCRIPTION | Section.EXPANDED
            | Section.TITLE_BAR );
        objectClassesSection.setDescription( "The schema '" + schema.getName()
            + "' contains the following object classes." );
        objectClassesSection.setText( "Object Classes" );

        // Creating the layout of the section
        Composite objectClassesSectionClient = toolkit.createComposite( objectClassesSection );
        objectClassesSectionClient.setLayout( new GridLayout() );
        toolkit.paintBordersFor( objectClassesSectionClient );
        objectClassesSection.setClient( objectClassesSectionClient );
        objectClassesSection.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, true ) );

        objectClassesTable = toolkit.createTable( objectClassesSectionClient, SWT.NONE );
        GridData gridData = new GridData( GridData.FILL, GridData.FILL, true, true );
        gridData.heightHint = 1;
        objectClassesTable.setLayoutData( gridData );
    }


    /**
     * Fills in the fields of the User Interface.
     */
    private void fillInUiFields()
    {
        for ( AttributeTypeImpl at : schema.getAttributeTypes() )
        {
            TableItem item = new TableItem( attributeTypesTable, SWT.NONE );
            item.setImage( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                PluginConstants.IMG_ATTRIBUTE_TYPE ).createImage() );
            item.setText( at.getNames()[0] );
        }

        for ( ObjectClassImpl oc : schema.getObjectClasses() )
        {
            TableItem item = new TableItem( objectClassesTable, SWT.NONE );
            item.setImage( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                PluginConstants.IMG_OBJECT_CLASS ).createImage() );
            item.setText( oc.getNames()[0] );
        }
    }


    /**
     * Initializes and adds the listners.
     */
    private void addListeners()
    {
        attributeTypesTable.addMouseListener( attributeTypesTableListener );
        objectClassesTable.addMouseListener( objectClassesTableListener );
    }
}
