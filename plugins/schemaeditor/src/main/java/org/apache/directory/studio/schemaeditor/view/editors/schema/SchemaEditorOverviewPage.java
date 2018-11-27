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


import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.api.ldap.model.schema.LdapSyntax;
import org.apache.directory.api.ldap.model.schema.MatchingRule;
import org.apache.directory.api.ldap.model.schema.MutableObjectClass;
import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.PluginUtils;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandlerAdapter;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener;
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
import org.eclipse.osgi.util.NLS;
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
 */
public class SchemaEditorOverviewPage extends FormPage
{
    /** The page ID */
    public static final String ID = SchemaEditor.ID + "overviewPage"; //$NON-NLS-1$

    /** The associated schema */
    private Schema originalSchema;

    private SchemaHandlerListener schemaHandlerListener = new SchemaHandlerAdapter()
    {
        public void attributeTypeAdded( AttributeType at )
        {
            refreshUI();
        }


        public void attributeTypeModified( AttributeType at )
        {
            refreshUI();
        }


        public void attributeTypeRemoved( AttributeType at )
        {
            refreshUI();
        }


        public void matchingRuleAdded( MatchingRule mr )
        {
            refreshUI();
        }


        public void matchingRuleModified( MatchingRule mr )
        {
            refreshUI();
        }


        public void matchingRuleRemoved( MatchingRule mr )
        {
            refreshUI();
        }


        public void objectClassAdded( ObjectClass oc )
        {
            refreshUI();
        }


        public void objectClassModified( ObjectClass oc )
        {
            refreshUI();
        }


        public void objectClassRemoved( ObjectClass oc )
        {
            refreshUI();
        }


        public void schemaAdded( Schema schema )
        {
            refreshUI();
        }


        public void schemaRemoved( Schema schema )
        {
            if ( !schema.equals( originalSchema ) )
            {
                refreshUI();
            }
        }


        public void schemaRenamed( Schema schema )
        {
            refreshUI();
        }


        public void syntaxAdded( LdapSyntax syntax )
        {
            refreshUI();
        }


        public void syntaxModified( LdapSyntax syntax )
        {
            refreshUI();
        }


        public void syntaxRemoved( LdapSyntax syntax )
        {
            refreshUI();
        }
    };

    // UI Fields
    private Section attributeTypesSection;
    private TableViewer attributeTypesTableViewer;
    private Section objectClassesSection;
    private TableViewer objectClassesTableViewer;

    // Listeners
    /** The listener of the Attribute Types TableViewer */
    private IDoubleClickListener attributeTypesTableViewerListener = new IDoubleClickListener()
    {
        /**
         * {@inheritDoc}
         */
        public void doubleClick( DoubleClickEvent event )
        {
            StructuredSelection selection = ( StructuredSelection ) event.getSelection();

            if ( !selection.isEmpty() )
            {
                AttributeType at = ( AttributeType ) selection.getFirstElement();

                try
                {
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(
                        new AttributeTypeEditorInput( at ), AttributeTypeEditor.ID );
                }
                catch ( PartInitException exception )
                {
                    PluginUtils.logError( Messages.getString( "SchemaEditorOverviewPage.ErrorOpenEditor" ), exception ); //$NON-NLS-1$
                    ViewUtils.displayErrorMessageDialog(
                        Messages.getString( "SchemaEditorOverviewPage.Error" ), Messages //$NON-NLS-1$
                            .getString( "SchemaEditorOverviewPage.ErrorOpenEditor" ) ); //$NON-NLS-1$
                }
            }
        }
    };

    /** The listener of the Object Classes TableViewer */
    private IDoubleClickListener objectClassesTableViewerListener = new IDoubleClickListener()
    {
        /**
         * {@inheritDoc}
         */
        public void doubleClick( DoubleClickEvent event )
        {
            StructuredSelection selection = ( StructuredSelection ) event.getSelection();

            if ( !selection.isEmpty() )
            {
                MutableObjectClass oc = ( MutableObjectClass ) selection.getFirstElement();

                try
                {
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(
                        new ObjectClassEditorInput( oc ), ObjectClassEditor.ID );
                }
                catch ( PartInitException exception )
                {
                    PluginUtils.logError( Messages.getString( "SchemaEditorOverviewPage.ErrorOpenEditor" ), exception ); //$NON-NLS-1$
                    ViewUtils.displayErrorMessageDialog(
                        Messages.getString( "SchemaEditorOverviewPage.Error" ), Messages //$NON-NLS-1$
                            .getString( "SchemaEditorOverviewPage.ErrorOpenEditor" ) ); //$NON-NLS-1$
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
        super( editor, ID, Messages.getString( "SchemaEditorOverviewPage.Overview" ) ); //$NON-NLS-1$
        Activator.getDefault().getSchemaHandler().addListener( schemaHandlerListener );
    }


    /**
     * {@inheritDoc}
     */
    protected void createFormContent( IManagedForm managedForm )
    {
        // Getting the associated schema
        originalSchema = ( ( SchemaEditor ) getEditor() ).getSchema();

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

        // Help Context for Dynamic Help
        PlatformUI.getWorkbench().getHelpSystem().setHelp( form, PluginConstants.PLUGIN_ID + "." + "schema_editor" ); //$NON-NLS-1$ //$NON-NLS-2$
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
        attributeTypesSection = toolkit.createSection( parent, Section.DESCRIPTION | Section.EXPANDED
            | Section.TITLE_BAR );
        attributeTypesSection.setDescription( "" ); //$NON-NLS-1$
        attributeTypesSection.setText( Messages.getString( "SchemaEditorOverviewPage.AttributeTypes" ) ); //$NON-NLS-1$

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
        objectClassesSection = toolkit.createSection( parent, Section.DESCRIPTION | Section.EXPANDED
            | Section.TITLE_BAR );
        objectClassesSection.setDescription( "" );//$NON-NLS-1$
        objectClassesSection.setText( Messages.getString( "SchemaEditorOverviewPage.ObjectClasses" ) ); //$NON-NLS-1$

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
        attributeTypesSection.setDescription( NLS.bind(
            Messages.getString( "SchemaEditorOverviewPage.SchemaAttribute" ), new String[] //$NON-NLS-1$
            { originalSchema.getSchemaName() } ) );
        objectClassesSection.setDescription( NLS.bind( Messages
            .getString( "SchemaEditorOverviewPage.SchemaObjectClasses" ), new String[] //$NON-NLS-1$
            { originalSchema.getSchemaName() } ) );
        attributeTypesTableViewer.setInput( originalSchema.getAttributeTypes() );
        objectClassesTableViewer.setInput( originalSchema.getObjectClasses() );
    }


    /**
     * Initializes and adds the listeners.
     */
    private void addListeners()
    {
        attributeTypesTableViewer.addDoubleClickListener( attributeTypesTableViewerListener );
        objectClassesTableViewer.addDoubleClickListener( objectClassesTableViewerListener );
    }


    /**
     * Removes the listeners.
     */
    private void removeListeners()
    {
        attributeTypesTableViewer.removeDoubleClickListener( attributeTypesTableViewerListener );
        objectClassesTableViewer.removeDoubleClickListener( objectClassesTableViewerListener );
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        removeListeners();

        Activator.getDefault().getSchemaHandler().removeListener( schemaHandlerListener );

        super.dispose();
    }


    /**
     * Refreshes the UI.
     */
    public void refreshUI()
    {
        removeListeners();
        fillInUiFields();
        addListeners();
    }
}
