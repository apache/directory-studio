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
package org.apache.directory.studio.openldap.config.editor.databases;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.widgets.FormToolkit;

import org.apache.directory.studio.openldap.common.ui.dialogs.AttributeDialog;
import org.apache.directory.studio.openldap.common.ui.widgets.EntryWidget;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPlugin;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPluginConstants;
import org.apache.directory.studio.openldap.config.model.AuxiliaryObjectClass;
import org.apache.directory.studio.openldap.config.model.OlcDatabaseConfig;
import org.apache.directory.studio.openldap.config.model.OlcFrontendConfig;


/**
 * This interface represents a block for Frontend Specific Details.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class FrontendDatabaseSpecificDetailsBlock extends AbstractDatabaseSpecificDetailsBlock<OlcDatabaseConfig>
{
    private static final String SHA = "{SHA}";
    private static final String SSHA = "{SSHA}";
    private static final String CRYPT = "{CRYPT}";
    private static final String MD5 = "{MD5}";
    private static final String SMD5 = "{SMD5}";
    private static final String CLEARTEXT = "{CLEARTEXT}";

    /** The list of sorted attributes values */
    private List<String> sortedValuesAttributesList = new ArrayList<String>();

    // UI Fields
    private EntryWidget defaultSearchBaseEntryWidget;
    private Button shaCheckbox;
    private Button sshaCheckbox;
    private Button cryptCheckbox;
    private Button md5Checkbox;
    private Button smd5Checkbox;
    private Button cleartextCheckbox;
    private Table sortedValuesTable;
    private TableViewer sortedValuesTableViewer;
    private Button addSortedValueButton;
    private Button deleteSortedValueButton;

    // Listeners
    private ISelectionChangedListener sortedValuesTableViewerSelectionChangedListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            deleteSortedValueButton.setEnabled( !sortedValuesTableViewer.getSelection().isEmpty() );
        }
    };
    private SelectionListener addSortedValueButtonSelectionListener = new SelectionAdapter()
    {

        public void widgetSelected( SelectionEvent e )
        {
            AttributeDialog dialog = new AttributeDialog( addSortedValueButton.getShell(), browserConnection );
            if ( dialog.open() == AttributeDialog.OK )
            {
                String attribute = dialog.getAttribute();

                if ( !sortedValuesAttributesList.contains( attribute ) )
                {
                    sortedValuesAttributesList.add( attribute );
                    sortedValuesTableViewer.refresh();
                    sortedValuesTableViewer.setSelection( new StructuredSelection( attribute ) );

                    detailsPage.setEditorDirty();
                }
            }
        }
    };
    private SelectionListener deleteSortedValueButtonSelectionListener = new SelectionAdapter()
    {

        public void widgetSelected( SelectionEvent e )
        {
            StructuredSelection selection = ( StructuredSelection ) sortedValuesTableViewer.getSelection();

            if ( !selection.isEmpty() )
            {
                String selectedAttribute = ( String ) selection.getFirstElement();

                sortedValuesAttributesList.remove( selectedAttribute );
                sortedValuesTableViewer.refresh();

                detailsPage.setEditorDirty();
            }
        }
    };


    /**
     * Creates a new instance of FrontendDatabaseSpecificDetailsBlock.
     *
     * @param detailsPage the details page
     * @param database the database
     * @param browserConnection the connection
     */
    public FrontendDatabaseSpecificDetailsBlock( DatabasesDetailsPage detailsPage, OlcDatabaseConfig database,
        IBrowserConnection browserConnection )
    {
        super( detailsPage, database, browserConnection );
    }


    /**
     * {@inheritDoc}
     */
    public Composite createBlockContent( Composite parent, FormToolkit toolkit )
    {
        // Composite
        Composite composite = toolkit.createComposite( parent );
        composite.setLayout( new GridLayout( 2, false ) );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Default Search Base Text
        toolkit.createLabel( composite, "Default Search Base:" );
        defaultSearchBaseEntryWidget = new EntryWidget( browserConnection, null, true );
        defaultSearchBaseEntryWidget.createWidget( composite, toolkit );
        defaultSearchBaseEntryWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Password Hash
        createPasswordHashContent( composite, toolkit );

        // Sorted Values Attributes
        createSortedValuesAttributesContent( composite, toolkit );

        return composite;
    }


    /**
     * Creates the content for the password hash.
     *
     * @param parent the parent composite
     * @param toolkit the toolkit
     */
    private void createPasswordHashContent( Composite parent, FormToolkit toolkit )
    {
        // Label
        Label passwordHashLabel = toolkit.createLabel( parent, "Password Hash:" );
        passwordHashLabel.setLayoutData( new GridData( SWT.NONE, SWT.TOP, false, false ) );

        // Composite
        Composite passwordHashComposite = toolkit.createComposite( parent );
        GridLayout passwordHashCompositeGridLayout = new GridLayout( 3, true );
        passwordHashCompositeGridLayout.marginHeight = passwordHashCompositeGridLayout.marginWidth = 0;
        passwordHashComposite.setLayout( passwordHashCompositeGridLayout );
        passwordHashComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // SHA Checkbox
        shaCheckbox = toolkit.createButton( passwordHashComposite, "SHA", SWT.CHECK );

        // SSHA Checkbox
        sshaCheckbox = toolkit.createButton( passwordHashComposite, "SSHA", SWT.CHECK );

        // CRYPT Checkbox
        cryptCheckbox = toolkit.createButton( passwordHashComposite, "CRYPT", SWT.CHECK );

        // MD5 Checkbox
        md5Checkbox = toolkit.createButton( passwordHashComposite, "MD5", SWT.CHECK );

        // SMD5 Checkbox
        smd5Checkbox = toolkit.createButton( passwordHashComposite, "SMD5", SWT.CHECK );

        // CLEARTEXT Checkbox
        cleartextCheckbox = toolkit.createButton( passwordHashComposite, "CLEARTEXT", SWT.CHECK );

    }


    /**
     * Creates the content for the sorted values attributes.
     *
     * @param parent the parent composite
     * @param toolkit the toolkit
     */
    private void createSortedValuesAttributesContent( Composite parent, FormToolkit toolkit )
    {
        // Label
        Label sortedValuesLabel = toolkit.createLabel( parent, "Maintain sorted values for these attributes:" );
        sortedValuesLabel.setLayoutData( new GridData( SWT.NONE, SWT.NONE, false, false, 2, 1 ) );

        // Composite
        Composite sortedValuesComposite = toolkit.createComposite( parent );
        GridLayout attributesCompositeGridLayout = new GridLayout( 2, false );
        attributesCompositeGridLayout.marginHeight = attributesCompositeGridLayout.marginWidth = 0;
        //        attributesCompositeGridLayout.verticalSpacing = attributesCompositeGridLayout.horizontalSpacing = 0;
        sortedValuesComposite.setLayout( attributesCompositeGridLayout );
        sortedValuesComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Table and Table Viewer
        sortedValuesTable = toolkit.createTable( sortedValuesComposite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
        GridData gd = new GridData( SWT.FILL, SWT.FILL, true, true, 1, 3 );
        gd.heightHint = 20;
        gd.widthHint = 100;
        sortedValuesTable.setLayoutData( gd );
        sortedValuesTableViewer = new TableViewer( sortedValuesTable );
        sortedValuesTableViewer.setContentProvider( new ArrayContentProvider() );
        sortedValuesTableViewer.setLabelProvider( new LabelProvider()
        {
            public Image getImage( Object element )
            {
                return OpenLdapConfigurationPlugin.getDefault().getImage(
                    OpenLdapConfigurationPluginConstants.IMG_ATTRIBUTE );
            }
        } );
        sortedValuesTableViewer.setInput( sortedValuesAttributesList );

        // Add Button
        addSortedValueButton = toolkit.createButton( sortedValuesComposite, "Add...", SWT.PUSH );
        addSortedValueButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );

        // Delete Button
        deleteSortedValueButton = toolkit.createButton( sortedValuesComposite, "Delete", SWT.PUSH );
        deleteSortedValueButton.setEnabled( false );
        deleteSortedValueButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
    }


    /**
     * {@inheritDoc}
     */
    public void refresh()
    {
        removeListeners();

        if ( database != null )
        {
            OlcFrontendConfig frontendConfig = getFrontendConfig();

            if ( frontendConfig == null )
            {
                // Default Search Base
                defaultSearchBaseEntryWidget.setInput( null );

                // Password Hash
                shaCheckbox.setSelection( false );
                sshaCheckbox.setSelection( false );
                cryptCheckbox.setSelection( false );
                md5Checkbox.setSelection( false );
                smd5Checkbox.setSelection( false );
                cleartextCheckbox.setSelection( false );

                // Sorted Values Attributes
                sortedValuesAttributesList.clear();
                sortedValuesTableViewer.refresh();
            }
            else
            {
                // Default Search Base
                String defaultSearchBase = frontendConfig.getOlcDefaultSearchBase();

                if ( Strings.isEmpty( defaultSearchBase ) )
                {
                    defaultSearchBaseEntryWidget.setInput( null );
                }
                else
                {
                    try
                    {
                        defaultSearchBaseEntryWidget.setInput( new Dn( defaultSearchBase ) );
                    }
                    catch ( LdapInvalidDnException e )
                    {
                        // Nothing to do.
                    }
                }

                // Password Hash
                List<String> passwordHashList = frontendConfig.getOlcPasswordHash();

                if ( ( passwordHashList != null ) && ( passwordHashList.size() > 0 ) )
                {
                    shaCheckbox.setSelection( passwordHashList.contains( SHA ) );
                    sshaCheckbox.setSelection( passwordHashList.contains( SSHA ) );
                    cryptCheckbox.setSelection( passwordHashList.contains( CRYPT ) );
                    md5Checkbox.setSelection( passwordHashList.contains( MD5 ) );
                    smd5Checkbox.setSelection( passwordHashList.contains( SMD5 ) );
                    cleartextCheckbox.setSelection( passwordHashList.contains( CLEARTEXT ) );
                }

                // Sorted Values Attributes
                sortedValuesAttributesList.clear();

                List<String> sortVals = frontendConfig.getOlcSortVals();

                for ( String attribute : sortVals )
                {
                    sortedValuesAttributesList.add( attribute );
                }
                sortedValuesTableViewer.refresh();
            }
        }

        addListeners();
    }


    /**
     * Adds the listeners.
     */
    private void addListeners()
    {
        defaultSearchBaseEntryWidget.addWidgetModifyListener( dirtyWidgetModifyListener );

        sortedValuesTableViewer.addSelectionChangedListener( sortedValuesTableViewerSelectionChangedListener );
        addSortedValueButton.addSelectionListener( addSortedValueButtonSelectionListener );
        deleteSortedValueButton.addSelectionListener( deleteSortedValueButtonSelectionListener );

        shaCheckbox.addSelectionListener( dirtySelectionListener );
        sshaCheckbox.addSelectionListener( dirtySelectionListener );
        cryptCheckbox.addSelectionListener( dirtySelectionListener );
        md5Checkbox.addSelectionListener( dirtySelectionListener );
        smd5Checkbox.addSelectionListener( dirtySelectionListener );
        cleartextCheckbox.addSelectionListener( dirtySelectionListener );
    }


    /**
     * Removes the listeners
     */
    private void removeListeners()
    {
        defaultSearchBaseEntryWidget.removeWidgetModifyListener( dirtyWidgetModifyListener );

        sortedValuesTableViewer.removeSelectionChangedListener( sortedValuesTableViewerSelectionChangedListener );
        addSortedValueButton.removeSelectionListener( addSortedValueButtonSelectionListener );
        deleteSortedValueButton.removeSelectionListener( deleteSortedValueButtonSelectionListener );

        shaCheckbox.removeSelectionListener( dirtySelectionListener );
        sshaCheckbox.removeSelectionListener( dirtySelectionListener );
        cryptCheckbox.removeSelectionListener( dirtySelectionListener );
        md5Checkbox.removeSelectionListener( dirtySelectionListener );
        smd5Checkbox.removeSelectionListener( dirtySelectionListener );
        cleartextCheckbox.removeSelectionListener( dirtySelectionListener );
    }


    /**
     * Gets the frontend config.
     *
     * @return the frontend config
     */
    private OlcFrontendConfig getFrontendConfig()
    {
        if ( database != null )
        {
            List<AuxiliaryObjectClass> auxiliaryObjectClassesList = database.getAuxiliaryObjectClasses();

            if ( ( auxiliaryObjectClassesList != null ) && ( auxiliaryObjectClassesList.size() > 0 ) )
            {
                for ( AuxiliaryObjectClass auxiliaryObjectClass : auxiliaryObjectClassesList )
                {
                    if ( auxiliaryObjectClass instanceof OlcFrontendConfig )
                    {
                        return ( OlcFrontendConfig ) auxiliaryObjectClass;
                    }
                }
            }
        }

        return null;
    }


    /**
     * {@inheritDoc}
     */
    public void commit( boolean onSave )
    {
        OlcFrontendConfig frontendConfig = getOrCreateFrontendConfig();

        if ( frontendConfig != null )
        {
            // Default Search Base
            Dn defaultSearchBase = defaultSearchBaseEntryWidget.getDn();

            if ( defaultSearchBase == null )
            {
                frontendConfig.setOlcDefaultSearchBase( null );
            }
            else
            {
                frontendConfig.setOlcDefaultSearchBase( defaultSearchBase.toString() );
            }

            // Password Hash
            frontendConfig.clearOlcPasswordHash();

            if ( shaCheckbox.getSelection() )
            {
                frontendConfig.addOlcPasswordHash( SHA );
            }
            if ( sshaCheckbox.getSelection() )
            {
                frontendConfig.addOlcPasswordHash( SSHA );
            }
            if ( cryptCheckbox.getSelection() )
            {
                frontendConfig.addOlcPasswordHash( CRYPT );
            }
            if ( md5Checkbox.getSelection() )
            {
                frontendConfig.addOlcPasswordHash( MD5 );
            }
            if ( smd5Checkbox.getSelection() )
            {
                frontendConfig.addOlcPasswordHash( SMD5 );
            }
            if ( cleartextCheckbox.getSelection() )
            {
                frontendConfig.addOlcPasswordHash( CLEARTEXT );
            }

            // Sorted Values Attributes
            frontendConfig.clearOlcSortVals();
            for ( String attribute : sortedValuesAttributesList )
            {
                frontendConfig.addOlcSortVals( attribute );
            }
        }
    }


    /**
     * Gets the frontend config or creates one if it's not available.
     *
     * @return the frontend config
     */
    private OlcFrontendConfig getOrCreateFrontendConfig()
    {
        OlcFrontendConfig frontendConfig = getFrontendConfig();

        if ( ( frontendConfig == null ) && ( database != null ) )
        {
            frontendConfig = new OlcFrontendConfig();
            database.addAuxiliaryObjectClasses( frontendConfig );
        }

        return frontendConfig;
    }
}
