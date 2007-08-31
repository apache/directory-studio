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

package org.apache.directory.studio.schemaeditor.view.editors.schema;


import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginUtils;
import org.apache.directory.studio.schemaeditor.controller.SchemaAdapter;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.schemaeditor.controller.SchemaListener;
import org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.apache.directory.studio.schemaeditor.view.ViewUtils;
import org.apache.directory.studio.schemaeditor.view.editors.attributetype.AttributeTypeEditor;
import org.apache.directory.studio.schemaeditor.view.editors.attributetype.AttributeTypeEditorInput;
import org.apache.directory.studio.schemaeditor.view.editors.objectclass.ObjectClassEditor;
import org.apache.directory.studio.schemaeditor.view.editors.objectclass.ObjectClassEditorInput;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;


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

    private SchemaListener schemaListener = new SchemaAdapter()
    {
        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaListener#attributeTypeAdded(org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl)
         */
        public void attributeTypeAdded( AttributeTypeImpl at )
        {
            fillInUiFields();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaListener#attributeTypeModified(org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl)
         */
        public void attributeTypeModified( AttributeTypeImpl at )
        {
            fillInUiFields();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaListener#attributeTypeRemoved(org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl)
         */
        public void attributeTypeRemoved( AttributeTypeImpl at )
        {
            fillInUiFields();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaListener#objectClassAdded(org.apache.directory.studio.schemaeditor.model.ObjectClassImpl)
         */
        public void objectClassAdded( ObjectClassImpl oc )
        {
            fillInUiFields();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaListener#objectClassModified(org.apache.directory.studio.schemaeditor.model.ObjectClassImpl)
         */
        public void objectClassModified( ObjectClassImpl oc )
        {
            fillInUiFields();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaListener#objectClassRemoved(org.apache.directory.studio.schemaeditor.model.ObjectClassImpl)
         */
        public void objectClassRemoved( ObjectClassImpl oc )
        {
            fillInUiFields();
        }
    };

    // UI Fields
    private TableViewer attributeTypesTableViewer;
    private TableViewer objectClassesTableViewer;

    // Listeners
    /** The listener of the Attribute Types TableViewer */
    private IDoubleClickListener attributeTypesTableViewerListener = new IDoubleClickListener()
    {
        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
         */
        public void doubleClick( DoubleClickEvent event )
        {
            StructuredSelection selection = ( StructuredSelection ) event.getSelection();

            if ( !selection.isEmpty() )
            {
                AttributeTypeImpl at = ( AttributeTypeImpl ) selection.getFirstElement();

                try
                {
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(
                        new AttributeTypeEditorInput( at ), AttributeTypeEditor.ID );
                }
                catch ( PartInitException exception )
                {
                    PluginUtils.logError( "An error occured when opening the editor.", exception );
                    ViewUtils.displayErrorMessageBox( "Error", "An error occured when opening the editor." );
                }
            }
        }
    };

    /** The listener of the Object Classes TableViewer */
    private IDoubleClickListener objectClassesTableViewerListener = new IDoubleClickListener()
    {
        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
         */
        public void doubleClick( DoubleClickEvent event )
        {
            StructuredSelection selection = ( StructuredSelection ) event.getSelection();

            if ( !selection.isEmpty() )
            {
                ObjectClassImpl oc = ( ObjectClassImpl ) selection.getFirstElement();

                try
                {
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(
                        new ObjectClassEditorInput( oc ), ObjectClassEditor.ID );
                }
                catch ( PartInitException exception )
                {
                    PluginUtils.logError( "An error occured when opening the editor.", exception );
                    ViewUtils.displayErrorMessageBox( "Error", "An error occured when opening the editor." );
                }
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
        attributeTypesSection.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        attributeTypesTableViewer = new TableViewer( attributeTypesSectionClient, SWT.SINGLE | SWT.H_SCROLL
            | SWT.V_SCROLL | SWT.BORDER );
        attributeTypesTableViewer.setContentProvider( new SchemaEditorTableViewerContentProvider() );
        attributeTypesTableViewer.setLabelProvider( new SchemaEditorTableViewerLabelProvider() );
        attributeTypesTableViewer.getTable().setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
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
        objectClassesSection.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        objectClassesTableViewer = new TableViewer( objectClassesSectionClient, SWT.SINGLE | SWT.H_SCROLL
            | SWT.V_SCROLL | SWT.BORDER );
        objectClassesTableViewer.setContentProvider( new SchemaEditorTableViewerContentProvider() );
        objectClassesTableViewer.setLabelProvider( new SchemaEditorTableViewerLabelProvider() );
        objectClassesTableViewer.getTable().setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    }


    /**
     * Fills in the fields of the User Interface.
     */
    private void fillInUiFields()
    {
        attributeTypesTableViewer.setInput( schema.getAttributeTypes() );
        objectClassesTableViewer.setInput( schema.getObjectClasses() );
    }


    /**
     * Initializes and adds the listeners.
     */
    private void addListeners()
    {
        schemaHandler.addListener( schema, schemaListener );
        attributeTypesTableViewer.addDoubleClickListener( attributeTypesTableViewerListener );
        objectClassesTableViewer.addDoubleClickListener( objectClassesTableViewerListener );
    }


    /**
     * Removes the listeners.
     */
    private void removeListeners()
    {
        schemaHandler.removeListener( schema, schemaListener );
        attributeTypesTableViewer.removeDoubleClickListener( attributeTypesTableViewerListener );
        objectClassesTableViewer.removeDoubleClickListener( objectClassesTableViewerListener );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormPage#dispose()
     */
    public void dispose()
    {
        removeListeners();

        super.dispose();
    }
}
