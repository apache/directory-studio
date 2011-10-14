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
package org.apache.directory.studio.apacheds.configuration.editor.v154;


import org.apache.directory.studio.apacheds.configuration.model.v154.InterceptorEnum;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
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
 */
public class InterceptorDetailsPage implements IDetailsPage
{
    /** The associated Master Details Block */
    private InterceptorsMasterDetailsBlock masterDetailsBlock;

    /** The Managed Form */
    private IManagedForm mform;

    /** The input Interceptor */
    private InterceptorEnum input;

    /** The dirty flag */
    private boolean dirty = false;

    // UI fields
    private Text nameText;
    private Text descriptionText;


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


    /**
     * {@inheritDoc}
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
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.marginWidth = 10;
        section.setText( "Interceptor Details" ); //$NON-NLS-1$
        TableWrapData td = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        td.grabHorizontal = true;
        section.setLayoutData( td );
        Composite client = toolkit.createComposite( section );
        toolkit.paintBordersFor( client );
        GridLayout glayout = new GridLayout( 2, false );
        client.setLayout( glayout );
        section.setClient( client );

        // Name
        toolkit.createLabel( client, Messages.getString( "InterceptorDetailsPage.Name" ) ); //$NON-NLS-1$
        nameText = toolkit.createText( client, "" ); //$NON-NLS-1$
        nameText.setEditable( false );
        nameText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Description
        toolkit.createLabel( client, Messages.getString( "InterceptorDetailsPage.Description" ) ); //$NON-NLS-1$
        descriptionText = toolkit.createText( client, "", SWT.MULTI | SWT.WRAP | SWT.V_SCROLL ); //$NON-NLS-1$
        descriptionText.setEditable( false );
        GridData gridData = new GridData( SWT.FILL, SWT.NONE, true, false );
        gridData.widthHint = 100;
        gridData.heightHint = 75;
        descriptionText.setLayoutData( gridData );
    }


    /**
     * {@inheritDoc}
     */
    public void selectionChanged( IFormPart part, ISelection selection )
    {
        IStructuredSelection ssel = ( IStructuredSelection ) selection;
        if ( ssel.size() == 1 )
        {
            input = ( InterceptorEnum ) ssel.getFirstElement();
        }
        else
        {
            input = null;
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
    public void refresh()
    {
        // Name
        String name = input.getName();
        nameText.setText( ( name == null ) ? "" : name ); //$NON-NLS-1$

        // Description
        String description = input.getDescription();
        descriptionText.setText( ( description == null ) ? "" : description ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public void setFocus()
    {
        nameText.setFocus();
    }


    /**
     * {@inheritDoc}
     */
    public boolean setFormInput( Object input )
    {
        return false;
    }
}
