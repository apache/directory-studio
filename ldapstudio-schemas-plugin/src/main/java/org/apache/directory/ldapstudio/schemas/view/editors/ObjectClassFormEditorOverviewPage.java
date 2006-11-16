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

package org.apache.directory.ldapstudio.schemas.view.editors;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import org.apache.directory.ldapstudio.schemas.controller.Application;
import org.apache.directory.ldapstudio.schemas.controller.PoolManagerController;
import org.apache.directory.ldapstudio.schemas.model.ObjectClass;
import org.apache.directory.ldapstudio.schemas.model.Schema;
import org.apache.directory.ldapstudio.schemas.model.SchemaPool;
import org.apache.directory.ldapstudio.schemas.view.IImageKeys;
import org.apache.directory.shared.ldap.schema.ObjectClassTypeEnum;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class is the Overview Page of the Object Class Editor
 */
public class ObjectClassFormEditorOverviewPage extends FormPage
{

    private Text name_text;
    private Text oid_text;
    private Text description_text;
    private Hyperlink sup_label;
    private Combo sup_combo;
    private Combo classType_combo;
    private Button obsolete_checkbox;
    private Table mandatoryAttributes_table;
    private Table optionnalAttributes_table;
    private ObjectClass objectClass;
    private String[] aliasesList;
    private Button aliases_button;


    /**
     * Default constructor
     * @param editor
     * @param id
     * @param title
     */
    public ObjectClassFormEditorOverviewPage( FormEditor editor, String id, String title )
    {
        super( editor, id, title );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
     */
    protected void createFormContent( IManagedForm managedForm )
    {
        ScrolledForm form = managedForm.getForm();
        FormToolkit toolkit = managedForm.getToolkit();
        GridLayout layout = new GridLayout( 2, true );
        form.getBody().setLayout( layout );

        // Getting the input and the objectClass
        ObjectClassFormEditorInput input = ( ObjectClassFormEditorInput ) getEditorInput();
        objectClass = ( ObjectClass ) input.getObjectClass();

        // General Information Section
        Section section_general_information = toolkit.createSection( form.getBody(), Section.DESCRIPTION
            | Section.TITLE_BAR );
        section_general_information.setDescription( Messages
            .getString( "ObjectClassFormEditorOverviewPage.General_Information_Section_Description" ) ); //$NON-NLS-1$
        section_general_information.setText( Messages
            .getString( "ObjectClassFormEditorOverviewPage.General_Information_Section_Text" ) ); //$NON-NLS-1$

        // Creating the layout of the section
        Composite client_general_information = toolkit.createComposite( section_general_information );
        GridLayout layout_general_information = new GridLayout();
        layout_general_information.numColumns = 2;
        client_general_information.setLayout( layout_general_information );
        toolkit.paintBordersFor( client_general_information );
        section_general_information.setClient( client_general_information );
        section_general_information.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, true, 2, 1 ) );

        // Adding elements to the section
        // NAME Field
        toolkit
            .createLabel( client_general_information, Messages.getString( "ObjectClassFormEditorOverviewPage.Name" ) ); //$NON-NLS-1$
        name_text = toolkit.createText( client_general_information, "" ); //$NON-NLS-1$
        name_text.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false ) );

        // ALIASES Button
        toolkit.createLabel( client_general_information, Messages
            .getString( "ObjectClassFormEditorOverviewPage.Aliases" ) ); //$NON-NLS-1$
        aliases_button = toolkit.createButton( client_general_information, Messages
            .getString( "ObjectClassFormEditorOverviewPage.Manage_Aliases" ), SWT.PUSH ); //$NON-NLS-1$
        aliases_button.setLayoutData( new GridData( SWT.NONE, SWT.BEGINNING, false, false ) );

        // OID Field
        toolkit.createLabel( client_general_information, Messages.getString( "ObjectClassFormEditorOverviewPage.OID" ) ); //$NON-NLS-1$
        oid_text = toolkit.createText( client_general_information, "" ); //$NON-NLS-1$
        oid_text.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // DESCRIPTION Field
        toolkit.createLabel( client_general_information, Messages
            .getString( "ObjectClassFormEditorOverviewPage.Description" ) ); //$NON-NLS-1$
        description_text = toolkit.createText( client_general_information, "", SWT.MULTI | SWT.V_SCROLL ); //$NON-NLS-1$
        GridData descriptionGridData = new GridData( SWT.FILL, SWT.NONE, true, false );
        descriptionGridData.heightHint = 37;
        description_text.setLayoutData( descriptionGridData );

        // SUP Combo
        sup_label = toolkit.createHyperlink( client_general_information, Messages
            .getString( "ObjectClassFormEditorOverviewPage.Superior_class" ), SWT.WRAP ); //$NON-NLS-1$
        sup_combo = new Combo( client_general_information, SWT.READ_ONLY | SWT.SINGLE );
        sup_combo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // CLASS TYPE Combo
        toolkit.createLabel( client_general_information, Messages
            .getString( "ObjectClassFormEditorOverviewPage.Class_type" ) ); //$NON-NLS-1$
        classType_combo = new Combo( client_general_information, SWT.READ_ONLY | SWT.SINGLE );
        classType_combo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // OBSOLETE Checkbox
        toolkit.createLabel( client_general_information, "" ); //$NON-NLS-1$
        obsolete_checkbox = toolkit.createButton( client_general_information, Messages
            .getString( "ObjectClassFormEditorOverviewPage.Obsolete" ), SWT.CHECK ); //$NON-NLS-1$
        obsolete_checkbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Mandatory Attributes Section
        createMandatoryAttributesSection( form.getBody(), toolkit );

        // Optionnal Attributes Section
        createOptionnalAttributesSection( form.getBody(), toolkit );

        // Initialization from the "input" object class
        initFieldsContentFromInput();

        // Listeners initialization
        initListeners();
    }


    private void createMandatoryAttributesSection( Composite parent, FormToolkit toolkit )
    {
        // MANDATORY ATTRIBUTES Section
        Section section = toolkit.createSection( parent, Section.DESCRIPTION | Section.TITLE_BAR );
        section.setText( Messages.getString( "ObjectClassFormEditorOverviewPage.Mandatory_Attribute_Section_Text" ) ); //$NON-NLS-1$
        section.setDescription( Messages
            .getString( "ObjectClassFormEditorOverviewPage.Mandatory_Attribute_Section_Description" ) ); //$NON-NLS-1$
        section.setExpanded( true );
        Composite client = toolkit.createComposite( section );
        section.setClient( client );
        GridData gd = new GridData( GridData.FILL, GridData.FILL, true, true );
        section.setLayoutData( gd );
        toolkit.paintBordersFor( client );

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        client.setLayout( layout );

        mandatoryAttributes_table = toolkit.createTable( client, SWT.NULL );
        gd = new GridData( SWT.FILL, SWT.FILL, true, true );
        gd.verticalSpan = 2;
        gd.heightHint = 100;
        mandatoryAttributes_table.setLayoutData( gd );

        final Button add_button = toolkit.createButton( client, Messages
            .getString( "ObjectClassFormEditorOverviewPage.Add..." ), SWT.PUSH ); //$NON-NLS-1$
        final Button remove_button = toolkit.createButton( client, Messages
            .getString( "ObjectClassFormEditorOverviewPage.Remove" ), SWT.PUSH ); //$NON-NLS-1$
        gd = new GridData( GridData.VERTICAL_ALIGN_BEGINNING );
        add_button.setLayoutData( gd );
        remove_button.setLayoutData( gd );
        remove_button.setEnabled( false );

        if ( objectClass.getOriginatingSchema().type == Schema.SchemaType.coreSchema )
        {
            // If the object class is in a core-schema file, we disable editing
            add_button.setEnabled( false );
            remove_button.setEnabled( false );
        }
        else
        {
            // else we set the listeners
            add_button.addSelectionListener( new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent e )
                {
                    AttributeTypeSelectionDialog selectionDialog = new AttributeTypeSelectionDialog( null );
                    if ( selectionDialog.open() != Window.OK )
                    {
                        return;
                    }
                    if ( isAttributeTypeAlreadySpecified( selectionDialog.getSelectedAttributeType(),
                        optionnalAttributes_table ) )
                    {
                        // The selected attribute type is already in the Optionnal Attributes Table
                        MessageDialog
                            .openError(
                                null,
                                Messages.getString( "ObjectClassFormEditorOverviewPage.Invalid_Selection" ), Messages.getString( "ObjectClassFormEditorOverviewPage.The_selected_attribute_type_is_already_in_the_Optionnal_Attributes_section" ) ); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    else
                    {
                        if ( isAttributeTypeAlreadySpecified( selectionDialog.getSelectedAttributeType(),
                            mandatoryAttributes_table ) )
                        {
                            // The selected attribute type is already in the Mandatory Attributes Table
                            MessageDialog
                                .openError(
                                    null,
                                    Messages.getString( "ObjectClassFormEditorOverviewPage.Invalid_Selection" ), Messages.getString( "ObjectClassFormEditorOverviewPage.The_selected_attribute_type_is_already_in_the_this_section" ) ); //$NON-NLS-1$ //$NON-NLS-2$
                        }
                        else
                        {
                            // The selected attribute is not in any table, so it can be added
                            TableItem item = new TableItem( mandatoryAttributes_table, SWT.NONE );
                            item.setImage( AbstractUIPlugin.imageDescriptorFromPlugin( Application.PLUGIN_ID,
                                IImageKeys.ATTRIBUTE_TYPE ).createImage() );
                            item.setText( selectionDialog.getSelectedAttributeType() );
                            setEditorDirty();
                        }

                    }
                }
            } );
            remove_button.addSelectionListener( new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent e )
                {
                    mandatoryAttributes_table.remove( mandatoryAttributes_table.getSelectionIndex() );
                    setEditorDirty();
                    remove_button.setEnabled( mandatoryAttributes_table.getSelection().length != 0 );
                }
            } );
        }
        // This listener needs to be outside of the 'if' so that attribute type editor can be opened from any object class (in a core or a user schema)
        mandatoryAttributes_table.addMouseListener( new MouseListener()
        {
            public void mouseDoubleClick( MouseEvent e )
            {
                SchemaPool pool = SchemaPool.getInstance();
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

                AttributeTypeFormEditorInput input = new AttributeTypeFormEditorInput( pool
                    .getAttributeType( mandatoryAttributes_table.getSelection()[0].getText() ) );
                String editorId = AttributeTypeFormEditor.ID;
                try
                {
                    page.openEditor( input, editorId );
                }
                catch ( PartInitException exception )
                {
                    Logger.getLogger( PoolManagerController.class ).debug( "error when opening the editor" ); //$NON-NLS-1$
                }
            }


            public void mouseDown( MouseEvent e )
            {
            }


            public void mouseUp( MouseEvent e )
            {
                if ( objectClass.getOriginatingSchema().type != Schema.SchemaType.coreSchema )
                {
                    remove_button.setEnabled( mandatoryAttributes_table.getSelection().length != 0 );
                }
            }
        } );
    }


    private void createOptionnalAttributesSection( Composite parent, FormToolkit toolkit )
    {
        // OPTIONNAL ATTRIBUTES Section
        Section section = toolkit.createSection( parent, Section.DESCRIPTION | Section.TITLE_BAR );
        section.setText( Messages.getString( "ObjectClassFormEditorOverviewPage.Optionnal_Attributes_Section_Text" ) ); //$NON-NLS-1$
        section.setDescription( Messages
            .getString( "ObjectClassFormEditorOverviewPage.Optionnal_Attributes_Section_Description" ) ); //$NON-NLS-1$
        section.setExpanded( true );
        Composite client = toolkit.createComposite( section );
        section.setClient( client );
        GridData gd = new GridData( GridData.FILL, GridData.FILL, true, true );
        section.setLayoutData( gd );
        toolkit.paintBordersFor( client );

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        client.setLayout( layout );

        optionnalAttributes_table = toolkit.createTable( client, SWT.SINGLE | SWT.V_SCROLL );
        gd = new GridData( SWT.FILL, SWT.FILL, true, true );
        gd.verticalSpan = 2;
        gd.heightHint = 100;
        optionnalAttributes_table.setLayoutData( gd );

        final Button add_button = toolkit.createButton( client, Messages
            .getString( "ObjectClassFormEditorOverviewPage.Add..." ), SWT.PUSH ); //$NON-NLS-1$
        final Button remove_button = toolkit.createButton( client, Messages
            .getString( "ObjectClassFormEditorOverviewPage.Remove" ), SWT.PUSH ); //$NON-NLS-1$
        gd = new GridData( GridData.VERTICAL_ALIGN_BEGINNING );
        add_button.setLayoutData( gd );
        remove_button.setLayoutData( gd );
        remove_button.setEnabled( false );

        if ( objectClass.getOriginatingSchema().type == Schema.SchemaType.coreSchema )
        {
            // If the object class is in a core-schema file, we disable editing
            add_button.setEnabled( false );
            remove_button.setEnabled( false );
        }
        else
        {
            // else we set the listeners
            add_button.addSelectionListener( new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent e )
                {
                    AttributeTypeSelectionDialog selectionDialog = new AttributeTypeSelectionDialog( null );
                    if ( selectionDialog.open() != Window.OK )
                    {
                        return;
                    }
                    if ( isAttributeTypeAlreadySpecified( selectionDialog.getSelectedAttributeType(),
                        mandatoryAttributes_table ) )
                    {
                        // The selected attribute type is already in the Mandatory Attributes Table
                        MessageDialog
                            .openError(
                                null,
                                Messages.getString( "ObjectClassFormEditorOverviewPage.Invalid_Selection" ), Messages.getString( "ObjectClassFormEditorOverviewPage.The_selected_attribute_type_is_already_in_the_Mandatory_Attributes_section" ) ); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    else
                    {
                        if ( isAttributeTypeAlreadySpecified( selectionDialog.getSelectedAttributeType(),
                            optionnalAttributes_table ) )
                        {
                            // The selected attribute type is already in the Optionnal Attributes Table
                            MessageDialog
                                .openError(
                                    null,
                                    Messages.getString( "ObjectClassFormEditorOverviewPage.Invalid_Selection" ), Messages.getString( "ObjectClassFormEditorOverviewPage.The_selected_attribute_type_is_already_in_the_this_section" ) ); //$NON-NLS-1$ //$NON-NLS-2$
                        }
                        else
                        {
                            // The selected attribute is not in any table, so it can be added
                            TableItem item = new TableItem( optionnalAttributes_table, SWT.NONE );
                            item.setImage( AbstractUIPlugin.imageDescriptorFromPlugin( Application.PLUGIN_ID,
                                IImageKeys.ATTRIBUTE_TYPE ).createImage() );
                            item.setText( selectionDialog.getSelectedAttributeType() );
                            setEditorDirty();
                        }

                    }
                }
            } );
            remove_button.addSelectionListener( new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent e )
                {
                    optionnalAttributes_table.remove( optionnalAttributes_table.getSelectionIndex() );
                    setEditorDirty();
                    remove_button.setEnabled( optionnalAttributes_table.getSelection().length != 0 );
                }
            } );
        }
        // This listener needs to be outside of the 'if' so that attribute type editor can be opened from any object class (in a core or a user schema)
        optionnalAttributes_table.addMouseListener( new MouseListener()
        {
            public void mouseDoubleClick( MouseEvent e )
            {
                SchemaPool pool = SchemaPool.getInstance();
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

                AttributeTypeFormEditorInput input = new AttributeTypeFormEditorInput( pool
                    .getAttributeType( optionnalAttributes_table.getSelection()[0].getText() ) );
                String editorId = AttributeTypeFormEditor.ID;
                try
                {
                    page.openEditor( input, editorId );
                }
                catch ( PartInitException exception )
                {
                    Logger.getLogger( PoolManagerController.class ).debug( "error when opening the editor" ); //$NON-NLS-1$
                }
            }


            public void mouseDown( MouseEvent e )
            {
            }


            public void mouseUp( MouseEvent e )
            {
                if ( objectClass.getOriginatingSchema().type != Schema.SchemaType.coreSchema )
                {
                    remove_button.setEnabled( optionnalAttributes_table.getSelection().length != 0 );
                }
            }
        } );
    }


    private boolean isAttributeTypeAlreadySpecified( String name, Table table )
    {
        for ( int i = 0; i < table.getItemCount(); i++ )
        {
            if ( table.getItem( i ).getText().equals( name ) )
            {
                return true;
            }
        }
        return false;
    }


    private void initFieldsContentFromInput()
    {
        // NAME Field
        if ( objectClass.getNames()[0] != null )
        {
            this.name_text.setText( objectClass.getNames()[0] );
        }

        // ALIASES
        String[] names = objectClass.getNames();
        ArrayList<String> aliases = new ArrayList<String>();
        for ( int i = 1; i < names.length; i++ )
        {
            String name = names[i];
            aliases.add( name );
        }
        this.aliasesList = aliases.toArray( new String[0] );

        // OID Field
        if ( objectClass.getOid() != null )
        {
            this.oid_text.setText( objectClass.getOid() );
        }

        // DESCRIPTION Field
        if ( objectClass.getDescription() != null )
        {
            this.description_text.setText( objectClass.getDescription() );
        }

        // SUP Combo
        initSup_combo();

        // CLASSTYPE Combo
        initClassType_combo();

        // OBSOLETE Checkbox
        if ( objectClass.isObsolete() )
        {
            this.obsolete_checkbox.setSelection( true );
        }

        // MANDATORY ATTRIBUTES Table
        initMandatoryAttributes_table();

        // OPTIONNAL ATTRIBUTES Table
        initOptionnalAttributes_table();
    }


    private void initSup_combo()
    {
        SchemaPool pool = SchemaPool.getInstance();
        ArrayList<ObjectClass> ocList = new ArrayList<ObjectClass>( pool.getObjectClassesAsHashTableByName().values() );

        //remove duplicate entries
        HashSet<ObjectClass> set = new HashSet<ObjectClass>( ocList );
        ocList = new ArrayList<ObjectClass>( set );

        // Sorting the list
        Collections.sort( ocList, new Comparator<ObjectClass>()
        {
            public int compare( ObjectClass arg0, ObjectClass arg1 )
            {
                String oneName = arg0.getNames()[0];
                String twoName = arg1.getNames()[0];
                return oneName.compareTo( twoName );
            }
        } );

        // Creating the UI
        sup_combo.add( Messages.getString( "ObjectClassFormEditorOverviewPage.(None)" ) ); //$NON-NLS-1$
        sup_combo.select( 0 );
        int counter = 1;
        for ( ObjectClass oc : ocList )
        {
            // TODO : Ajouter une verification pour qu'on ne puisse pas ajouter en sup l'objectclass lui meme et ses alias
            sup_combo.add( oc.getNames()[0], counter );
            if ( ( objectClass.getSuperiors().length != 0 )
                && ( oc.getNames()[0].equals( objectClass.getSuperiors()[0] ) ) )
            {
                // We select the right superior
                sup_combo.select( counter );
            }
            counter++;
        }
    }


    private void initClassType_combo()
    {
        classType_combo.add( Messages.getString( "ObjectClassFormEditorOverviewPage.Abstract" ), 0 ); //$NON-NLS-1$
        classType_combo.add( Messages.getString( "ObjectClassFormEditorOverviewPage.Auxiliary" ), 1 ); //$NON-NLS-1$
        classType_combo.add( Messages.getString( "ObjectClassFormEditorOverviewPage.Structural" ), 2 ); //$NON-NLS-1$
        if ( objectClass.getClassType() == ObjectClassTypeEnum.ABSTRACT )
        {
            classType_combo.select( 0 );
        }
        else if ( objectClass.getClassType() == ObjectClassTypeEnum.AUXILIARY )
        {
            classType_combo.select( 1 );
        }
        else if ( objectClass.getClassType() == ObjectClassTypeEnum.STRUCTURAL )
        {
            classType_combo.select( 2 );
        }
    }


    private void initMandatoryAttributes_table()
    {
        String[] mustArray = objectClass.getMust();
        for ( int i = 0; i < mustArray.length; i++ )
        {
            TableItem item = new TableItem( mandatoryAttributes_table, SWT.NONE );
            item.setImage( AbstractUIPlugin
                .imageDescriptorFromPlugin( Application.PLUGIN_ID, IImageKeys.ATTRIBUTE_TYPE ).createImage() );
            item.setText( mustArray[i] );
        }
    }


    private void initOptionnalAttributes_table()
    {
        String[] mayArray = objectClass.getMay();
        for ( int i = 0; i < mayArray.length; i++ )
        {
            TableItem item = new TableItem( optionnalAttributes_table, SWT.NONE );
            item.setImage( AbstractUIPlugin
                .imageDescriptorFromPlugin( Application.PLUGIN_ID, IImageKeys.ATTRIBUTE_TYPE ).createImage() );
            item.setText( mayArray[i] );
        }
    }


    private void initListeners()
    {
        // NAME Field
        if ( objectClass.getOriginatingSchema().type == Schema.SchemaType.coreSchema )
        {
            // If the object class is in a core-schema file, we disable editing
            name_text.setEditable( false );
        }
        else
        {
            // else we set the listener
            name_text.addModifyListener( new ModifyListener()
            {
                public void modifyText( ModifyEvent e )
                {
                    setEditorDirty();
                }
            } );
        }

        // ALIASES Button
        // The user can always access to the Manage Aliases Window, but if the object class is in a core-schema file editing will be disabled
        aliases_button.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                ManageAliasesDialog manageDialog = new ManageAliasesDialog( null, aliasesList, ( objectClass
                    .getOriginatingSchema().type == Schema.SchemaType.coreSchema ) );
                if ( manageDialog.open() != Window.OK )
                {
                    return;
                }
                if ( manageDialog.isDirty() )
                {
                    aliasesList = manageDialog.getAliasesList();
                    setEditorDirty();
                }
            }
        } );

        // OID Field
        if ( objectClass.getOriginatingSchema().type == Schema.SchemaType.coreSchema )
        {
            // If the object class is in a core-schema file, we disable editing
            oid_text.setEditable( false );
        }
        else
        {
            // else we set the listener
            oid_text.addModifyListener( new ModifyListener()
            {
                public void modifyText( ModifyEvent e )
                {
                    setEditorDirty();
                }
            } );
        }

        // DESCRIPTION Field
        if ( objectClass.getOriginatingSchema().type == Schema.SchemaType.coreSchema )
        {
            // If the object class is in a core-schema file, we disable editing
            description_text.setEditable( false );
        }
        else
        {
            // else we set the listener
            description_text.addModifyListener( new ModifyListener()
            {
                public void modifyText( ModifyEvent e )
                {
                    setEditorDirty();
                }
            } );
        }

        // SUP Combo
        sup_label.addHyperlinkListener( new HyperlinkAdapter()
        {
            public void linkActivated( HyperlinkEvent e )
            {
                if ( !sup_combo.getItem( sup_combo.getSelectionIndex() ).equals(
                    Messages.getString( "ObjectClassFormEditorOverviewPage.(None)" ) ) ) { //$NON-NLS-1$
                    SchemaPool pool = SchemaPool.getInstance();
                    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

                    ObjectClassFormEditorInput input = new ObjectClassFormEditorInput( pool.getObjectClass( sup_combo
                        .getItem( sup_combo.getSelectionIndex() ) ) );
                    String editorId = ObjectClassFormEditor.ID;
                    try
                    {
                        page.openEditor( input, editorId );
                    }
                    catch ( PartInitException exception )
                    {
                        Logger.getLogger( ObjectClassFormEditorOverviewPage.class ).debug(
                            "error when opening the editor" ); //$NON-NLS-1$
                    }
                }
            }
        } );
        if ( objectClass.getOriginatingSchema().type == Schema.SchemaType.coreSchema )
        {
            // If the object class is in a core-schema file, we disable editing
            sup_combo.setEnabled( false );
        }
        else
        {
            // else we set the listener
            sup_combo.addModifyListener( new ModifyListener()
            {
                public void modifyText( ModifyEvent e )
                {
                    setEditorDirty();
                }
            } );
        }

        // CLASS TYPE Combo
        if ( objectClass.getOriginatingSchema().type == Schema.SchemaType.coreSchema )
        {
            // If the object class is in a core-schema file, we disable editing
            classType_combo.setEnabled( false );
        }
        else
        {
            // else we set the listener
            classType_combo.addModifyListener( new ModifyListener()
            {
                public void modifyText( ModifyEvent e )
                {
                    setEditorDirty();
                }
            } );
        }

        // OBSOLETE Checkbox
        if ( objectClass.getOriginatingSchema().type == Schema.SchemaType.coreSchema )
        {
            // If the object class is in a core-schema file, we disable editing
            obsolete_checkbox.setEnabled( false );
        }
        else
        {
            // else we set the listener
            obsolete_checkbox.addSelectionListener( new SelectionListener()
            {
                public void widgetDefaultSelected( SelectionEvent e )
                {
                }


                public void widgetSelected( SelectionEvent e )
                {
                    setEditorDirty();
                }
            } );
        }
    }


    private void setEditorDirty()
    {
        ( ( ObjectClassFormEditor ) getEditor() ).setDirty( true );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormPage#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void doSave( IProgressMonitor monitor )
    {
        // NAME
        //		if ( name_text.getText().equals("") ){
        //			MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OK|SWT.ICON_ERROR);
        //			messageBox.setMessage("You must to provide a name to the object class to be able to save it.");
        //			messageBox.open();
        //			monitor.setCanceled(true);
        //			monitor.done();
        //			return;
        //		}
        ArrayList<String> names = new ArrayList<String>();
        names.add( name_text.getText() );
        for ( int i = 0; i < this.aliasesList.length; i++ )
        {
            names.add( this.aliasesList[i] );
        }
        objectClass.setNames( names.toArray( new String[0] ) );

        // OID
        objectClass.setOid( oid_text.getText() );

        // DESCRIPTION
        objectClass.setDescription( description_text.getText() );

        // SUP
        if ( sup_combo.getItem( sup_combo.getSelectionIndex() ).equals(
            Messages.getString( "ObjectClassFormEditorOverviewPage.(None)" ) ) ) { //$NON-NLS-1$
            objectClass.setSuperiors( new String[0] );
        }
        else
        {
            objectClass.setSuperiors( new String[]
                { sup_combo.getItem( sup_combo.getSelectionIndex() ) } );
        }

        // CLASS TYPE
        if ( classType_combo.getSelectionIndex() == 0 )
        {
            objectClass.setClassType( ObjectClassTypeEnum.ABSTRACT );
        }
        else if ( classType_combo.getSelectionIndex() == 1 )
        {
            objectClass.setClassType( ObjectClassTypeEnum.AUXILIARY );
        }
        else if ( classType_combo.getSelectionIndex() == 2 )
        {
            objectClass.setClassType( ObjectClassTypeEnum.STRUCTURAL );
        }

        // OBSOLETE
        objectClass.setObsolete( obsolete_checkbox.getSelection() );

        // MANDATORY ATTRIBUTES
        ArrayList<String> mustList = new ArrayList<String>();
        for ( int i = 0; i < mandatoryAttributes_table.getItemCount(); i++ )
        {
            mustList.add( mandatoryAttributes_table.getItem( i ).getText() );
        }
        objectClass.setMust( mustList.toArray( new String[0] ) );

        // OPTIONNAL ATTRIBUTES
        ArrayList<String> mayList = new ArrayList<String>();
        for ( int i = 0; i < optionnalAttributes_table.getItemCount(); i++ )
        {
            mayList.add( optionnalAttributes_table.getItem( i ).getText() );
        }
        objectClass.setMay( mayList.toArray( new String[0] ) );
    }
}
