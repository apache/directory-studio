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
package org.apache.directory.ldapstudio.apacheds.configuration.editor;


import org.apache.directory.ldapstudio.apacheds.configuration.model.ExtendedOperation;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;


/**
 * This class represents the Details Page of the Server Configuration Editor for the Extended Operation type
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ExtendedOperationDetailsPage implements IDetailsPage
{
    /** The associated Master Details Block */
    private ExtendedOperationsMasterDetailsBlock masterDetailsBlock;

    /** The Managed Form */
    private IManagedForm mform;

    /** The input Interceptor */
    private ExtendedOperation input;

    /** The dirty flag */
    private boolean dirty = false;

    // UI fields
    private Text classTypeText;

    // Listeners
    /** The Modify Listener for Text Widgets */
    private ModifyListener textModifyListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            masterDetailsBlock.setEditorDirty();
            dirty = true;
        }
    };


    /**
     * Creates a new instance of ExtendedOperationDetailsPage.
     *
     * @param emdb
     *      the associated Master Details Block
     */
    public ExtendedOperationDetailsPage( ExtendedOperationsMasterDetailsBlock emdb )
    {
        masterDetailsBlock = emdb;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IDetailsPage#createContents(org.eclipse.swt.widgets.Composite)
     */
    public void createContents( Composite parent )
    {
        FormToolkit toolkit = mform.getToolkit();
        TableWrapLayout layout = new TableWrapLayout();
        layout.topMargin = 5;
        layout.leftMargin = 5;
        layout.rightMargin = 2;
        layout.bottomMargin = 2;
        parent.setLayout( layout );

        createDetailsSection( parent, toolkit );
    }


    /**
     * Creates the Details Section
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the toolkit to use
     */
    private void createDetailsSection( Composite parent, FormToolkit toolkit )
    {
        Section section = toolkit.createSection( parent, Section.DESCRIPTION | Section.TITLE_BAR );
        section.marginWidth = 10;
        section.setText( "Extended Operation Details" ); //$NON-NLS-1$
        section.setDescription( "Set the properties of the extended operation." ); //$NON-NLS-1$
        TableWrapData td = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        td.grabHorizontal = true;
        section.setLayoutData( td );
        Composite client = toolkit.createComposite( section );
        toolkit.paintBordersFor( client );
        GridLayout glayout = new GridLayout( 3, false );
        client.setLayout( glayout );
        section.setClient( client );

        // Class
        toolkit.createLabel( client, "Class:" );
        classTypeText = toolkit.createText( client, "" );
        classTypeText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        addListeners();
    }


    /**
     * Adds listeners to UI fields.
     */
    private void addListeners()
    {
        classTypeText.addModifyListener( textModifyListener );
    }


    /**
     * Removes listeners to UI fields.
     */
    private void removeListeners()
    {
        classTypeText.removeModifyListener( textModifyListener );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IPartSelectionListener#selectionChanged(org.eclipse.ui.forms.IFormPart, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged( IFormPart part, ISelection selection )
    {
        IStructuredSelection ssel = ( IStructuredSelection ) selection;
        if ( ssel.size() == 1 )
        {
            input = ( ExtendedOperation ) ssel.getFirstElement();
        }
        else
        {
            input = null;
        }
        refresh();
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#commit(boolean)
     */
    public void commit( boolean onSave )
    {
        if ( input != null )
        {
            input.setClassType( classTypeText.getText() );
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#dispose()
     */
    public void dispose()
    {
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#initialize(org.eclipse.ui.forms.IManagedForm)
     */
    public void initialize( IManagedForm form )
    {
        this.mform = form;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#isDirty()
     */
    public boolean isDirty()
    {
        return dirty;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#isStale()
     */
    public boolean isStale()
    {
        return false;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#refresh()
     */
    public void refresh()
    {
        removeListeners();

        classTypeText.setText( input.getClassType() );

        addListeners();
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#setFocus()
     */
    public void setFocus()
    {
        classTypeText.setFocus();
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#setFormInput(java.lang.Object)
     */
    public boolean setFormInput( Object input )
    {
        return false;
    }
}
