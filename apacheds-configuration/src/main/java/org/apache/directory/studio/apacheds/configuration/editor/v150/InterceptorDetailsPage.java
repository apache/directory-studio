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
package org.apache.directory.studio.apacheds.configuration.editor.v150;


import org.apache.directory.studio.apacheds.configuration.model.v150.Interceptor;
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
 * This class represents the Details Page of the Server Configuration Editor for the Interceptor type
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class InterceptorDetailsPage implements IDetailsPage
{
    /** The associated Master Details Block */
    private InterceptorsMasterDetailsBlock masterDetailsBlock;

    /** The Managed Form */
    private IManagedForm mform;

    /** The input Interceptor */
    private Interceptor input;

    /** The dirty flag */
    private boolean dirty = false;

    // UI fields
    private Text nameText;
    private Text classText;

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
     * Creates a new instance of InterceptorDetailsPage.
     *
     * @param imdb
     *      The associated Master Details Block
     */
    public InterceptorDetailsPage( InterceptorsMasterDetailsBlock imdb )
    {
        masterDetailsBlock = imdb;
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
        section.setText( "Interceptor Details" ); //$NON-NLS-1$
        section.setDescription( "Set the properties of the interceptor." ); //$NON-NLS-1$
        TableWrapData td = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        td.grabHorizontal = true;
        section.setLayoutData( td );
        Composite client = toolkit.createComposite( section );
        toolkit.paintBordersFor( client );
        GridLayout glayout = new GridLayout( 3, false );
        client.setLayout( glayout );
        section.setClient( client );

        // Name
        toolkit.createLabel( client, "Name:" );
        nameText = toolkit.createText( client, "" );
        nameText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Class
        toolkit.createLabel( client, "Class:" );
        classText = toolkit.createText( client, "" );
        classText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
    }


    /**
     * Adds listeners to UI fields.
     */
    private void addListeners()
    {
        nameText.addModifyListener( textModifyListener );
        classText.addModifyListener( textModifyListener );
    }


    /**
     * Removes listeners to UI fields.
     */
    private void removeListeners()
    {
        nameText.removeModifyListener( textModifyListener );
        classText.removeModifyListener( textModifyListener );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IPartSelectionListener#selectionChanged(org.eclipse.ui.forms.IFormPart, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged( IFormPart part, ISelection selection )
    {
        IStructuredSelection ssel = ( IStructuredSelection ) selection;
        if ( ssel.size() == 1 )
        {
            input = ( Interceptor ) ssel.getFirstElement();
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
            input.setName( nameText.getText() );
            input.setClassType( classText.getText() );
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

        // Name
        String name = input.getName();
        nameText.setText( ( name == null ) ? "" : name );

        // Class
        String classType = input.getClassType();
        classText.setText( ( classType == null ) ? "" : classType );

        addListeners();
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#setFocus()
     */
    public void setFocus()
    {
        nameText.setFocus();
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#setFormInput(java.lang.Object)
     */
    public boolean setFormInput( Object input )
    {
        return false;
    }
}
