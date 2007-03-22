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

package org.apache.directory.ldapstudio.schemas.view.editors.schema;


import org.apache.directory.ldapstudio.schemas.Activator;
import org.apache.directory.ldapstudio.schemas.PluginConstants;
import org.apache.directory.ldapstudio.schemas.model.AttributeType;
import org.apache.directory.ldapstudio.schemas.model.LDAPModelEvent;
import org.apache.directory.ldapstudio.schemas.model.ObjectClass;
import org.apache.directory.ldapstudio.schemas.model.Schema;
import org.apache.directory.ldapstudio.schemas.model.SchemaListener;
import org.apache.directory.ldapstudio.schemas.model.SchemaPool;
import org.apache.directory.ldapstudio.schemas.view.editors.attributeType.AttributeTypeFormEditor;
import org.apache.directory.ldapstudio.schemas.view.editors.attributeType.AttributeTypeFormEditorInput;
import org.apache.directory.ldapstudio.schemas.view.editors.objectClass.ObjectClassFormEditor;
import org.apache.directory.ldapstudio.schemas.view.editors.objectClass.ObjectClassFormEditorInput;
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
public class SchemaFormEditorOverviewPage extends FormPage
{
    /** The page ID */
    public static final String ID = SchemaFormEditor.ID + "overviewPage";

    /** The page title */
    public static final String TITLE = "Overview";

    /** The Schema Pool */
    private SchemaPool schemaPool;

    /** The associated schema */
    private Schema schema;

    private SchemaListener schemaListener = new SchemaListener()
    {
        public void schemaChanged( Schema originatingSchema, LDAPModelEvent e )
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

            AttributeTypeFormEditorInput input = new AttributeTypeFormEditorInput( schemaPool
                .getAttributeType( attributeTypesTable.getSelection()[0].getText() ) );
            String editorId = AttributeTypeFormEditor.ID;
            try
            {
                page.openEditor( input, editorId );
            }
            catch ( PartInitException exception )
            {
                Logger.getLogger( SchemaFormEditorOverviewPage.class ).debug( "error when opening the editor" ); //$NON-NLS-1$
            }
        }
    };
    /** The listener of the Object Classes Table*/
    private MouseAdapter objectClassesTableListener = new MouseAdapter()
    {
        public void mouseDoubleClick( MouseEvent e )
        {
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

            ObjectClassFormEditorInput input = new ObjectClassFormEditorInput( schemaPool
                .getObjectClass( objectClassesTable.getSelection()[0].getText() ) );
            String editorId = ObjectClassFormEditor.ID;
            try
            {
                page.openEditor( input, editorId );
            }
            catch ( PartInitException exception )
            {
                Logger.getLogger( SchemaFormEditorOverviewPage.class ).debug( "error when opening the editor" ); //$NON-NLS-1$
            }
        }
    };


    /**
     * Creates a new instance of SchemaFormEditorOverviewPage.
     *
     * @param editor
     *      the associated editor
     */
    public SchemaFormEditorOverviewPage( FormEditor editor )
    {
        super( editor, ID, TITLE );
        schemaPool = SchemaPool.getInstance();
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
     */
    protected void createFormContent( IManagedForm managedForm )
    {
        // Getting the associated schema
        schema = ( ( SchemaFormEditor ) getEditor() ).getSchema();
        schema.addListener( schemaListener );

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
        attributeTypesSection.setText( "Attribute types" );

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
        objectClassesSection.setText( "Object classes" );

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
        AttributeType[] attributeTypes = schema.getAttributeTypesAsArray();
        for ( AttributeType at : attributeTypes )
        {
            TableItem item = new TableItem( attributeTypesTable, SWT.NONE );
            item.setImage( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                PluginConstants.IMG_ATTRIBUTE_TYPE ).createImage() );
            item.setText( at.getNames()[0] );
        }

        ObjectClass[] objectClasses = schema.getObjectClassesAsArray();
        for ( ObjectClass oc : objectClasses )
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
