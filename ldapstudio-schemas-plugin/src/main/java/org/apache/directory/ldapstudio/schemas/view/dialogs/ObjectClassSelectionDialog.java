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

package org.apache.directory.ldapstudio.schemas.view.dialogs;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.ldapstudio.schemas.Activator;
import org.apache.directory.ldapstudio.schemas.model.ObjectClass;
import org.apache.directory.ldapstudio.schemas.model.SchemaPool;
import org.apache.directory.ldapstudio.schemas.view.views.TableDecoratingLabelProvider;
import org.apache.directory.ldapstudio.schemas.view.views.wrappers.ObjectClassWrapper;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;


/**
 * This class is the Object Class Selection Dialog, that allows user to select an object class.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ObjectClassSelectionDialog extends Dialog
{
    /** The selected Object Class*/
    private ObjectClass selectedObjectClass;

    /** The Schema Pool */
    private SchemaPool schemaPool;

    /** The hidden Object Classes */
    private List<ObjectClass> hiddenObjectClasses;

    // UI Fields
    private Table objectClassesTable;
    private TableViewer tableViewer;
    private Text searchText;


    /**
     * Creates a new instance of ObjectClassSelectionDialog.
     */
    public ObjectClassSelectionDialog()
    {
        super( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() );
        schemaPool = SchemaPool.getInstance();
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell newShell )
    {
        super.configureShell( newShell );
        newShell.setText( Messages.getString( "ObjectClassSelectionDialog.Object_Class_Selection" ) ); //$NON-NLS-1$
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout layout = new GridLayout( 1, false );
        composite.setLayout( layout );

        Label chooseLabel = new Label( composite, SWT.NONE );
        chooseLabel.setText( Messages.getString( "ObjectClassSelectionDialog.Choose_an_object_class" ) ); //$NON-NLS-1$
        chooseLabel.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false ) );

        searchText = new Text( composite, SWT.BORDER );
        searchText.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false ) );
        searchText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                tableViewer.setInput( searchText.getText() );
                objectClassesTable.select( 0 );
            }
        } );
        searchText.addKeyListener( new KeyAdapter()
        {
            public void keyPressed( KeyEvent e )
            {
                if ( e.keyCode == SWT.ARROW_DOWN )
                {
                    objectClassesTable.setFocus();
                }
            }
        } );

        Label matchingLabel = new Label( composite, SWT.NONE );
        matchingLabel.setText( Messages.getString( "ObjectClassSelectionDialog.Matching_object_classes" ) ); //$NON-NLS-1$
        matchingLabel.setLayoutData( new GridData( GridData.FILL, SWT.None, true, false ) );

        objectClassesTable = new Table( composite, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
            | SWT.FULL_SELECTION | SWT.HIDE_SELECTION );
        GridData gridData = new GridData( GridData.FILL, GridData.FILL, true, true );
        gridData.heightHint = 148;
        gridData.minimumHeight = 148;
        gridData.widthHint = 350;
        gridData.minimumWidth = 350;
        objectClassesTable.setLayoutData( gridData );
        objectClassesTable.addMouseListener( new MouseAdapter()
        {
            public void mouseDoubleClick( MouseEvent e )
            {
                if ( objectClassesTable.getSelectionIndex() != -1 )
                {
                    okPressed();
                }
            }
        } );

        tableViewer = new TableViewer( objectClassesTable );
        tableViewer.setUseHashlookup( true );

        tableViewer.setContentProvider( new ObjectClassSelectionDialogContentProvider( hiddenObjectClasses ) );
        tableViewer.setLabelProvider( new TableDecoratingLabelProvider( new ObjectClassSelectionDialogLabelProvider(),
            Activator.getDefault().getWorkbench().getDecoratorManager().getLabelDecorator() ) );

        // We need to force the input to load the complete list of attribute types
        tableViewer.setInput( "" ); //$NON-NLS-1$
        // We also need to force the selection of the first row
        objectClassesTable.select( 0 );

        return composite;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        createButton( parent, IDialogConstants.OK_ID, Messages.getString( "ObjectClassSelectionDialog.Add" ), true ); //$NON-NLS-1$
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    protected void okPressed()
    {
        StructuredSelection selection = ( StructuredSelection ) tableViewer.getSelection();

        if ( selection.isEmpty() )
        {
            MessageDialog
                .openError(
                    getShell(),
                    Messages.getString( "ObjectClassSelectionDialog.Invalid_Selection" ), Messages.getString( "ObjectClassSelectionDialog.You_have_to_choose_an_object_class" ) ); //$NON-NLS-1$ //$NON-NLS-2$
            return;
        }

        ObjectClassWrapper ocw = ( ObjectClassWrapper ) selection.getFirstElement();
        if ( ocw != null )
        {
            selectedObjectClass = ocw.getMyObjectClass();
        }

        super.okPressed();
    }


    /**
     * Returns the selected object class.
     * 
     * @return 
     *      the selected object class
     */
    public ObjectClass getSelectedObjectClass()
    {
        return selectedObjectClass;
    }


    /**
     * Set the hidden Object Classes.
     *
     * @param list
     *      a list of Object Classes to hide
     */
    public void setHiddenObjectClasses( List<ObjectClass> list )
    {
        hiddenObjectClasses = list;
    }


    /**
     * Sets the hidden Object Classes.
     *
     * @param objectClasses
     *      an array of Object Classes to hide
     */
    public void setHiddenObjectClasses( ObjectClass[] objectClasses )
    {
        hiddenObjectClasses = new ArrayList<ObjectClass>();

        for ( ObjectClass objectClass : objectClasses )
        {
            hiddenObjectClasses.add( objectClass );
        }
    }


    /**
     * Sets the hidden Object Classes.
     *
     * @param names
     *      an array of names of Object Classes to hide
     */
    public void setHiddenObjectClasses( String[] names )
    {
        hiddenObjectClasses = new ArrayList<ObjectClass>();

        for ( String name : names )
        {
            ObjectClass oc = schemaPool.getObjectClass( name );
            if ( oc != null )
            {
                hiddenObjectClasses.add( oc );
            }
        }
    }
}
