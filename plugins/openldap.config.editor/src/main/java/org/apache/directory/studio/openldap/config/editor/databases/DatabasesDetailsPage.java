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


import java.util.List;

import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.util.Strings;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import org.apache.directory.studio.openldap.config.model.AuxiliaryObjectClass;
import org.apache.directory.studio.openldap.config.model.OlcBdbConfig;
import org.apache.directory.studio.openldap.config.model.OlcDatabaseConfig;
import org.apache.directory.studio.openldap.config.model.OlcFrontendConfig;
import org.apache.directory.studio.openldap.config.model.OlcLdifConfig;


/**
 * This class represents the Details Page of the Server Configuration Editor for the Database type
 */
public class DatabasesDetailsPage implements IDetailsPage
{
    /** The associated Master Details Block */
    private DatabasesMasterDetailsBlock masterDetailsBlock;

    /** The Managed Form */
    private IManagedForm mform;

    /** The input Partition */
    private OlcDatabaseConfig database;

    private DatabaseSpecificDetailsBlock databaseSpecificDetailsBlock;

    /** The dirty flag */
    private boolean dirty = false;

    // UI widgets
    private FormToolkit toolkit;
    private Text idText;
    private Text suffixText;
    private Text rootDnText;
    private Text rootPasswordText;
    private Button readOnlyCheckbox;
    private Button hiddenCheckbox;
    private Combo databaseTypeCombo;
    private ComboViewer databaseTypeComboViewer;
    private Composite specificSettingsComposite;
    private Composite innerSpecificSettingsComposite;

    //
    // Listeners
    //

    private ModifyListener idTextModifylistener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            String id = idText.getText();
            if ( Strings.isNotEmpty( id ) )
            {
                database.setOlcDatabase( id );
            }
        }
    };

    private ModifyListener suffixTextModifyListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            String suffixes = suffixText.getText();
            if ( Strings.isNotEmpty( suffixes ) )
            {
                database.clearOlcSuffix();

                String[] suffixesArray = suffixes.split( ", " );
                for ( String suffix : suffixesArray )
                {
                    try
                    {
                        database.addOlcSuffix( new Dn( suffix ) );
                    }
                    catch ( LdapInvalidDnException e1 )
                    {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            }
        }
    };

    private ModifyListener rootDnTextModifyListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            String rootDn = rootDnText.getText();
            if ( Strings.isNotEmpty( rootDn ) )
            {
                try
                {
                    database.setOlcRootDN( new Dn( rootDn ) );
                }
                catch ( LdapInvalidDnException e1 )
                {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        }
    };

    private ModifyListener rootPasswordModifyListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            String rootPassword = rootPasswordText.getText();
            if ( Strings.isNotEmpty( rootPassword ) )
            {
                database.setOlcRootPW( rootPassword );
            }
        }
    };

    private SelectionListener readOnlyCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            database.setOlcReadOnly( new Boolean( readOnlyCheckbox.getSelection() ) );
        }
    };

    private SelectionListener hiddenCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            database.setOlcHidden( new Boolean( hiddenCheckbox.getSelection() ) );
        }
    };

    /** The modify listener which set the editor dirty */
    private ModifyListener dirtyModifyListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            setEditorDirty();
        }
    };

    /** The selection listener which set the editor dirty */
    private SelectionListener dirtySelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            setEditorDirty();
        }
    };


    /**
     * Creates a new instance of PartitionDetailsPage.
     *
     * @param pmdb
     *      the associated Master Details Block
     */
    public DatabasesDetailsPage( DatabasesMasterDetailsBlock pmdb )
    {
        masterDetailsBlock = pmdb;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IDetailsPage#createContents(org.eclipse.swt.widgets.Composite)
     */
    public void createContents( Composite parent )
    {
        toolkit = mform.getToolkit();
        TableWrapLayout layout = new TableWrapLayout();
        layout.topMargin = 5;
        layout.leftMargin = 5;
        layout.rightMargin = 2;
        layout.bottomMargin = 2;
        parent.setLayout( layout );

        createGeneralSettingsSection( parent, toolkit );
        createDatabaseSpecificSettingsSection( parent, toolkit );
    }


    /**
     * Creates the General Settings Section
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the toolkit to use
     */
    private void createGeneralSettingsSection( Composite parent, FormToolkit toolkit )
    {
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.marginWidth = 10;
        section.setText( "Database General Settings" );
        TableWrapData td = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        td.grabHorizontal = true;
        section.setLayoutData( td );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout glayout = new GridLayout( 2, false );
        composite.setLayout( glayout );
        section.setClient( composite );

        // ID
        toolkit.createLabel( composite, "ID:" );
        idText = toolkit.createText( composite, "" ); //$NON-NLS-1$
        idText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Suffixes
        toolkit.createLabel( composite, "Suffixes:" ); //$NON-NLS-1$
        suffixText = toolkit.createText( composite, "" ); //$NON-NLS-1$
        suffixText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Root DN
        toolkit.createLabel( composite, "Root DN:" );
        rootDnText = toolkit.createText( composite, "" ); //$NON-NLS-1$
        rootDnText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Root Password
        toolkit.createLabel( composite, "Root Password:" );
        rootPasswordText = toolkit.createText( composite, "" ); //$NON-NLS-1$
        rootPasswordText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Read Only
        readOnlyCheckbox = toolkit.createButton( composite, "Read Only", SWT.CHECK );
        readOnlyCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Hidden
        hiddenCheckbox = toolkit.createButton( composite, "Hidden", SWT.CHECK );
        hiddenCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
    }


    /**
     * Creates the Database Specific Settings Section
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the toolkit to use
     */
    private void createDatabaseSpecificSettingsSection( Composite parent, FormToolkit toolkit )
    {
        Section section = toolkit.createSection( parent, Section.TWISTIE | Section.EXPANDED | Section.TITLE_BAR );
        section.marginWidth = 10;
        section.setText( "Database Specific Settings" );
        section.setLayoutData( new TableWrapData( TableWrapData.FILL ) );
        specificSettingsComposite = toolkit.createComposite( section );
        toolkit.paintBordersFor( specificSettingsComposite );
        specificSettingsComposite.setLayout( new GridLayout( 2, false ) );
        section.setClient( specificSettingsComposite );

        // Type
        toolkit.createLabel( specificSettingsComposite, "Database Type:" );
        databaseTypeCombo = new Combo( specificSettingsComposite, SWT.READ_ONLY | SWT.SINGLE );
        databaseTypeCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        databaseTypeComboViewer = new ComboViewer( databaseTypeCombo );
        databaseTypeComboViewer.setContentProvider( new ArrayContentProvider() );
        databaseTypeComboViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                if ( element instanceof DatabaseType )
                {
                    DatabaseType databaseType = ( DatabaseType ) element;

                    switch ( databaseType )
                    {
                        case NONE:
                            return "None";
                        case FRONTEND:
                            return "Frontend DB";
                        case BDB:
                            return "Oracle Berkerly DB";
                        case HDB:
                            return "Hybrid DB";
                        case LDAP:
                            return "LDAP DB";
                        case LDIF:
                            return "LDIF DB";
                    }
                }

                return super.getText( element );
            }
        } );
        DatabaseType[] databaseTypes = new DatabaseType[]
            {
                DatabaseType.NONE,
                DatabaseType.FRONTEND,
                DatabaseType.BDB,
                DatabaseType.HDB,
                DatabaseType.LDAP,
                DatabaseType.LDIF
            };
        databaseTypeComboViewer.setInput( databaseTypes );

        createInnerSpecificSettingsComposite();
    }


    /**
     * {@inheritDoc}
     */
    public void selectionChanged( IFormPart part, ISelection selection )
    {
        IStructuredSelection ssel = ( IStructuredSelection ) selection;
        if ( ssel.size() == 1 )
        {
            database = ( OlcDatabaseConfig ) ssel.getFirstElement();
        }
        else
        {
            database = null;
        }
        refresh();
    }


    /**
     * {@inheritDoc}
     */
    public void commit( boolean onSave )
    {
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
    }


    /**
     * {@inheritDoc}
     */
    public void initialize( IManagedForm form )
    {
        this.mform = form;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isDirty()
    {
        return dirty;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isStale()
    {
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public void setFocus()
    {
        idText.setFocus();
    }


    /**
     * {@inheritDoc}
     */
    public boolean setFormInput( Object input )
    {
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public void refresh()
    {
        removeListeners();

        if ( database == null )
        {
            // Blank out all fields

            // ID
            idText.setEnabled( false );
            idText.setText( "" ); //$NON-NLS-1$

            // Suffixes
            suffixText.setEnabled( false );
            suffixText.setText( "" ); //$NON-NLS-1$

            // Root DN
            rootDnText.setEnabled( false );
            rootDnText.setText( "" ); //$NON-NLS-1$

            // Root PW
            rootPasswordText.setEnabled( false );
            rootPasswordText.setText( "" ); //$NON-NLS-1$

            // Read Only
            readOnlyCheckbox.setEnabled( false );
            readOnlyCheckbox.setSelection( false );

            // Hidden
            hiddenCheckbox.setEnabled( false );
            hiddenCheckbox.setSelection( false );
        }
        else
        {
            // ID
            String id = database.getOlcDatabase();
            idText.setEnabled( true );
            idText.setText( ( id == null ) ? "" : id ); //$NON-NLS-1$

            // Suffixes
            List<Dn> suffixesDnList = database.getOlcSuffix();
            StringBuilder sb = new StringBuilder();
            for ( Dn suffixDn : suffixesDnList )
            {
                sb.append( suffixDn.toString() );
                sb.append( ", " );
            }
            if ( sb.length() > 1 )
            {
                sb.deleteCharAt( sb.length() - 1 );
                sb.deleteCharAt( sb.length() - 1 );
            }
            suffixText.setEnabled( true );
            suffixText.setText( sb.toString() );

            // Root DN
            Dn rootDn = database.getOlcRootDN();
            rootDnText.setEnabled( true );
            rootDnText.setText( ( rootDn == null ) ? "" : rootDn.toString() ); //$NON-NLS-1$

            // Root PW
            String rootPassword = database.getOlcRootPW();
            rootPasswordText.setEnabled( true );
            rootPasswordText.setText( ( rootPassword == null ) ? "" : rootPassword ); //$NON-NLS-1$

            // Read Only
            readOnlyCheckbox.setEnabled( true );
            Boolean readOnly = database.getOlcReadOnly();
            if ( readOnly != null )
            {
                readOnlyCheckbox.setSelection( readOnly.booleanValue() );
            }
            else
            {
                readOnlyCheckbox.setSelection( false );
            }

            // Hidden
            hiddenCheckbox.setEnabled( true );
            Boolean hidden = database.getOlcHidden();
            if ( hidden != null )
            {
                hiddenCheckbox.setSelection( hidden.booleanValue() );
            }
            else
            {
                hiddenCheckbox.setSelection( false );
            }

            //
            // Specific Settings
            //

            // OlcBdbConfig Type
            if ( database instanceof OlcBdbConfig )
            {
                databaseTypeComboViewer.setSelection( new StructuredSelection( DatabaseType.BDB ) );
                databaseSpecificDetailsBlock = new BdbDatabaseSpecificDetailsBlock( ( OlcBdbConfig ) database );
            }
            else if ( database instanceof OlcLdifConfig )
            {
                databaseTypeComboViewer.setSelection( new StructuredSelection( DatabaseType.LDIF ) );
                databaseSpecificDetailsBlock = new LdifDatabaseSpecificDetailsBlock( ( OlcLdifConfig ) database );
            }
            // None of these types
            else
            {
                // Looking for a frontend configuration
                OlcFrontendConfig frontendConfiguration = getFrontendConfig( database );
                if ( frontendConfiguration != null )
                {
                    databaseTypeComboViewer.setSelection( new StructuredSelection( DatabaseType.FRONTEND ) );
                    databaseSpecificDetailsBlock = new FrontendDatabaseSpecificDetailsBlock( frontendConfiguration );
                }
                else
                {
                    databaseSpecificDetailsBlock = new NoneDatabaseSpecificDetailsBlock();
                    databaseTypeComboViewer.setSelection( new StructuredSelection( DatabaseType.NONE ) );
                }
            }

            // Disposing existing specific settings composite and creating a new one
            disposeInnerSpecificSettingsComposite();
            createInnerSpecificSettingsComposite();

            // Displaying the specific settings
            databaseSpecificDetailsBlock.createFormContent( innerSpecificSettingsComposite, toolkit );
            databaseSpecificDetailsBlock.refresh();
            specificSettingsComposite.layout();
        }

        addListeners();
    }


    /**
     * Adds listeners to UI widgets.
     */
    private void addListeners()
    {
        // ID
        addModifyListener( idText, dirtyModifyListener );
        addModifyListener( idText, idTextModifylistener );

        // Suffixes
        addModifyListener( suffixText, dirtyModifyListener );
        addModifyListener( suffixText, suffixTextModifyListener );

        // Root DN
        addModifyListener( rootDnText, dirtyModifyListener );
        addModifyListener( rootDnText, rootDnTextModifyListener );

        // Root PW
        addModifyListener( rootPasswordText, dirtyModifyListener );
        addModifyListener( rootPasswordText, rootPasswordModifyListener );

        // Read Only
        addSelectionListener( readOnlyCheckbox, dirtySelectionListener );
        addSelectionListener( readOnlyCheckbox, readOnlyCheckboxSelectionListener );

        // Hidden
        addSelectionListener( hiddenCheckbox, dirtySelectionListener );
        addSelectionListener( hiddenCheckbox, hiddenCheckboxSelectionListener );
    }


    /**
     * Removes listeners from UI widgets. 
     */
    private void removeListeners()
    {
        // ID
        removeModifyListener( idText, dirtyModifyListener );
        removeModifyListener( idText, idTextModifylistener );

        // Suffixes
        removeModifyListener( suffixText, dirtyModifyListener );
        removeModifyListener( suffixText, suffixTextModifyListener );

        // Root DN
        removeModifyListener( rootDnText, dirtyModifyListener );
        removeModifyListener( rootDnText, rootDnTextModifyListener );

        // Root PW
        removeModifyListener( rootPasswordText, dirtyModifyListener );
        removeModifyListener( rootPasswordText, rootPasswordModifyListener );

        // Read Only
        removeSelectionListener( readOnlyCheckbox, dirtySelectionListener );
        removeSelectionListener( readOnlyCheckbox, readOnlyCheckboxSelectionListener );

        // Hidden
        removeSelectionListener( hiddenCheckbox, dirtySelectionListener );
        removeSelectionListener( hiddenCheckbox, hiddenCheckboxSelectionListener );
    }


    /**
     * Creates the inner specific settings composite.
     */
    private void createInnerSpecificSettingsComposite()
    {
        innerSpecificSettingsComposite = toolkit.createComposite( specificSettingsComposite );
        GridLayout gl = new GridLayout( 2, false );
        gl.marginWidth = gl.marginHeight = 0;
        innerSpecificSettingsComposite.setLayout( gl );
        innerSpecificSettingsComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
    }


    /**
     * Disposes the inner specific settings composite.
     */
    private void disposeInnerSpecificSettingsComposite()
    {
        if ( innerSpecificSettingsComposite != null )
        {
            innerSpecificSettingsComposite.dispose();
            innerSpecificSettingsComposite = null;
        }
    }


    /**
     * Gets the front-end configuration (if any).
     *
     * @param database the database
     * @return the front-end configuration or <code>null</code>
     */
    private OlcFrontendConfig getFrontendConfig( OlcDatabaseConfig database )
    {
        if ( database != null )
        {
            List<AuxiliaryObjectClass> auxiliaryObjectClasses = database.getAuxiliaryObjectClasses();
            if ( auxiliaryObjectClasses != null )
            {
                for ( AuxiliaryObjectClass auxiliaryObjectClass : auxiliaryObjectClasses )
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
     * Adds a modify listener to the given Text.
     *
     * @param text
     *      the Text control
     * @param listener
     *      the listener
     */
    protected void addModifyListener( Text text, ModifyListener listener )
    {
        if ( ( text != null ) && ( !text.isDisposed() ) && ( listener != null ) )
        {
            text.addModifyListener( listener );
        }
    }


    /**
     * Adds a selection listener to the given Button.
     *
     * @param button
     *      the Button control
     * @param listener
     *      the listener
     */
    protected void addSelectionListener( Button button, SelectionListener listener )
    {
        if ( ( button != null ) && ( !button.isDisposed() ) && ( listener != null ) )
        {
            button.addSelectionListener( listener );
        }
    }


    /**
     * Removes a modify listener to the given Text.
     *
     * @param text
     *      the Text control
     * @param listener
     *      the listener
     */
    protected void removeModifyListener( Text text, ModifyListener listener )
    {
        if ( ( text != null ) && ( !text.isDisposed() ) && ( listener != null ) )
        {
            text.removeModifyListener( listener );
        }
    }


    /**
     * Removes a selection listener to the given Button.
     *
     * @param button
     *      the Button control
     * @param listener
     *      the listener
     */
    protected void removeSelectionListener( Button button, SelectionListener listener )
    {
        if ( ( button != null ) && ( !button.isDisposed() ) && ( listener != null ) )
        {
            button.removeSelectionListener( listener );
        }
    }


    /**
     * Sets the associated editor dirty.
     */
    private void setEditorDirty()
    {
        masterDetailsBlock.getPage().getServerConfigurationEditor().setDirty( true );
    }
}
