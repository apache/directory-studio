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

package org.apache.directory.studio.schemaeditor.view.editors.attributetype;


import java.util.List;

import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.api.ldap.model.schema.MutableObjectClass;
import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.PluginUtils;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandlerAdapter;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.apache.directory.studio.schemaeditor.view.ViewUtils;
import org.apache.directory.studio.schemaeditor.view.editors.objectclass.ObjectClassEditor;
import org.apache.directory.studio.schemaeditor.view.editors.objectclass.ObjectClassEditorInput;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;


/**
 * This class is the Used By Page of the Attribute Type Editor
 */
public class AttributeTypeEditorUsedByPage extends AbstractAttributeTypeEditorPage
{
    /** The page ID */
    public static final String ID = AttributeTypeEditor.ID + "usedByPage"; //$NON-NLS-1$

    /** The Schema listener */
    private SchemaHandlerListener schemaHandlerListener = new SchemaHandlerAdapter()
    {
        /**
         * {@inheritDoc}
         */
        public void attributeTypeAdded( AttributeType at )
        {
            refreshTableViewers();
        }


        /**
         * {@inheritDoc}
         */
        public void attributeTypeModified( AttributeType at )
        {
            refreshTableViewers();
        }


        /**
         * {@inheritDoc}
         */
        public void attributeTypeRemoved( AttributeType at )
        {
            refreshTableViewers();
        }


        /**
         * {@inheritDoc}
         */
        public void objectClassAdded( ObjectClass oc )
        {
            refreshTableViewers();
        }


        /**
         * {@inheritDoc}
         */
        public void objectClassModified( ObjectClass oc )
        {
            refreshTableViewers();
        }


        /**
         * {@inheritDoc}
         */
        public void objectClassRemoved( ObjectClass oc )
        {
            refreshTableViewers();
        }


        /**
         * {@inheritDoc}
         */
        public void schemaAdded( Schema schema )
        {
            refreshTableViewers();
        }


        /**
         * {@inheritDoc}
         */
        public void schemaRemoved( Schema schema )
        {
            refreshTableViewers();
        }
    };

    // UI Widgets
    private Table mandatoryAttributeTable;
    private TableViewer mandatoryAttributeTableViewer;
    private Table optionalAttibuteTable;
    private TableViewer optionalAttibuteTableViewer;

    // Listeners
    /** The listener of the Mandatory Attribute Type Table*/
    private MouseAdapter mandatoryAttributeTableListener = new MouseAdapter()
    {
        public void mouseDoubleClick( MouseEvent e )
        {
            Object selectedItem = ( ( StructuredSelection ) mandatoryAttributeTableViewer.getSelection() )
                .getFirstElement();

            if ( selectedItem instanceof ObjectClass )
            {
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                try
                {
                    page.openEditor( new ObjectClassEditorInput( ( MutableObjectClass ) selectedItem ),
                        ObjectClassEditor.ID );
                }
                catch ( PartInitException exception )
                {
                    PluginUtils.logError( "error when opening the editor", exception ); //$NON-NLS-1$
                }
            }
        }
    };

    /** The listener of the Optional Attribute Type Table*/
    private MouseAdapter optionalAttibuteTableListener = new MouseAdapter()
    {
        public void mouseDoubleClick( MouseEvent e )
        {
            Object selectedItem = ( ( StructuredSelection ) optionalAttibuteTableViewer.getSelection() )
                .getFirstElement();

            if ( selectedItem instanceof ObjectClass )
            {
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                try
                {
                    page.openEditor( new ObjectClassEditorInput( ( MutableObjectClass ) selectedItem ),
                        ObjectClassEditor.ID );
                }
                catch ( PartInitException exception )
                {
                    PluginUtils.logError( "error when opening the editor", exception ); //$NON-NLS-1$
                }
            }
        }
    };


    /**
     * Default constructor.
     * 
     * @param editor
     *      the associated editor
     */
    public AttributeTypeEditorUsedByPage( AttributeTypeEditor editor )
    {
        super( editor, ID, Messages.getString( "AttributeTypeEditorUsedByPage.UsedBy" ) ); //$NON-NLS-1$
        Activator.getDefault().getSchemaHandler().addListener( schemaHandlerListener );
    }


    /**
     * {@inheritDoc}
     */
    protected void createFormContent( IManagedForm managedForm )
    {
        // Creating the base UI
        ScrolledForm form = managedForm.getForm();
        FormToolkit toolkit = managedForm.getToolkit();
        GridLayout layout = new GridLayout( 2, true );
        form.getBody().setLayout( layout );

        // As Mandatory Attribute Section
        createAsMandatoryAttributeSection( form.getBody(), toolkit );

        // As Optional Attribute Section
        createAsOptionalAttributeSection( form.getBody(), toolkit );

        // Filling the UI with values from the attribute type
        fillInUiFields();

        // Listeners initialization
        addListeners();

        // Help Context for Dynamic Help
        PlatformUI.getWorkbench().getHelpSystem().setHelp( form,
            PluginConstants.PLUGIN_ID + "." + "attribute_type_editor" ); //$NON-NLS-1$ //$NON-NLS-2$

        initialized = true;
    }


    /**
     * Creates the As Mandatory Attribute Section.
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the FormToolKit to use
     */
    private void createAsMandatoryAttributeSection( Composite parent, FormToolkit toolkit )
    {
        AttributeType modifiedAttributeType = getModifiedAttributeType();

        // As Mandatory Attribute Section
        Section mandatoryAttributeSection = toolkit.createSection( parent, Section.DESCRIPTION | Section.EXPANDED
            | Section.TITLE_BAR );
        List<String> names = modifiedAttributeType.getNames();
        if ( ( names != null ) && ( names.size() > 0 ) )
        {
            mandatoryAttributeSection
                .setDescription( NLS
                    .bind(
                        Messages.getString( "AttributeTypeEditorUsedByPage.AttributeTypeMandatory" ), new String[] { ViewUtils.concateAliases( names ) } ) ); //$NON-NLS-1$
        }
        else
        {
            mandatoryAttributeSection
                .setDescription( NLS
                    .bind(
                        Messages.getString( "AttributeTypeEditorUsedByPage.AttributeTypeMandatory" ), new String[] { modifiedAttributeType.getOid() } ) ); //$NON-NLS-1$
        }
        mandatoryAttributeSection.setText( Messages.getString( "AttributeTypeEditorUsedByPage.AsMandatoryAttribute" ) ); //$NON-NLS-1$

        // Creating the layout of the section
        Composite mandatoryAttributeSectionClient = toolkit.createComposite( mandatoryAttributeSection );
        mandatoryAttributeSectionClient.setLayout( new GridLayout() );
        toolkit.paintBordersFor( mandatoryAttributeSectionClient );
        mandatoryAttributeSection.setClient( mandatoryAttributeSectionClient );
        mandatoryAttributeSection.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        mandatoryAttributeTable = toolkit.createTable( mandatoryAttributeSectionClient, SWT.NONE );
        GridData gridData = new GridData( SWT.FILL, SWT.FILL, true, true );
        gridData.heightHint = 1;
        mandatoryAttributeTable.setLayoutData( gridData );
        mandatoryAttributeTableViewer = new TableViewer( mandatoryAttributeTable );
        mandatoryAttributeTableViewer.setContentProvider( new ATEUsedByMandatoryTableContentProvider() );
        mandatoryAttributeTableViewer.setLabelProvider( new ATEUsedByTablesLabelProvider() );
    }


    /**
     * Creates the As Optional Attribute Section.
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the FormToolKit to use
     */
    private void createAsOptionalAttributeSection( Composite parent, FormToolkit toolkit )
    {
        AttributeType modifiedAttributeType = getModifiedAttributeType();

        // Matching Rules Section
        Section optionalAttributeSection = toolkit.createSection( parent, Section.DESCRIPTION | Section.EXPANDED
            | Section.TITLE_BAR );
        List<String> names = modifiedAttributeType.getNames();
        if ( ( names != null ) && ( names.size() > 0 ) )
        {
            optionalAttributeSection
                .setDescription( NLS
                    .bind(
                        Messages.getString( "AttributeTypeEditorUsedByPage.AttributeTypeOptional" ), new String[] { ViewUtils.concateAliases( names ) } ) ); //$NON-NLS-1$
        }
        else
        {
            optionalAttributeSection
                .setDescription( NLS
                    .bind(
                        Messages.getString( "AttributeTypeEditorUsedByPage.AttributeTypeOptional" ), new String[] { modifiedAttributeType.getOid() } ) ); //$NON-NLS-1$
        }
        optionalAttributeSection.setText( Messages.getString( "AttributeTypeEditorUsedByPage.AsOptionalAttribute" ) ); //$NON-NLS-1$

        // Creating the layout of the section
        Composite optionalAttributeSectionClient = toolkit.createComposite( optionalAttributeSection );
        optionalAttributeSectionClient.setLayout( new GridLayout() );
        toolkit.paintBordersFor( optionalAttributeSectionClient );
        optionalAttributeSection.setClient( optionalAttributeSectionClient );
        optionalAttributeSection.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        optionalAttibuteTable = toolkit.createTable( optionalAttributeSectionClient, SWT.NONE );
        GridData gridData = new GridData( SWT.FILL, SWT.FILL, true, true );
        gridData.heightHint = 1;
        optionalAttibuteTable.setLayoutData( gridData );
        optionalAttibuteTableViewer = new TableViewer( optionalAttibuteTable );
        optionalAttibuteTableViewer.setContentProvider( new ATEUsedByOptionalTableContentProvider() );
        optionalAttibuteTableViewer.setLabelProvider( new ATEUsedByTablesLabelProvider() );
    }


    /**
     * {@inheritDoc}
     */
    protected void fillInUiFields()
    {
        AttributeType modifiedAttributeType = getModifiedAttributeType();

        mandatoryAttributeTableViewer.setInput( modifiedAttributeType );
        optionalAttibuteTableViewer.setInput( modifiedAttributeType );
    }


    /**
     * {@inheritDoc}
     */
    protected void addListeners()
    {
        addMouseListener( mandatoryAttributeTable, mandatoryAttributeTableListener );
        addMouseListener( optionalAttibuteTable, optionalAttibuteTableListener );
    }


    /**
     * {@inheritDoc}
     */
    protected void removeListeners()
    {
        removeMouseListener( mandatoryAttributeTable, mandatoryAttributeTableListener );
        removeMouseListener( optionalAttibuteTable, optionalAttibuteTableListener );
    }


    /**
     * Refreshes the Table Viewers
     */
    public void refreshTableViewers()
    {
        if ( mandatoryAttributeTableViewer != null )
        {
            mandatoryAttributeTableViewer.refresh();
        }
        if ( optionalAttibuteTableViewer != null )
        {
            optionalAttibuteTableViewer.refresh();
        }
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        Activator.getDefault().getSchemaHandler().removeListener( schemaHandlerListener );

        super.dispose();
    }
}
