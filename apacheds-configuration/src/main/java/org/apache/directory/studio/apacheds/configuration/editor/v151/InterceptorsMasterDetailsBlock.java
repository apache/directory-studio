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
package org.apache.directory.studio.apacheds.configuration.editor.v151;


import java.util.List;

import org.apache.directory.studio.apacheds.configuration.ApacheDSConfigurationPlugin;
import org.apache.directory.studio.apacheds.configuration.ApacheDSConfigurationPluginConstants;
import org.apache.directory.studio.apacheds.configuration.editor.ServerConfigurationEditor;
import org.apache.directory.studio.apacheds.configuration.model.v151.Interceptor;
import org.apache.directory.studio.apacheds.configuration.model.v151.ServerConfigurationV151;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;


/**
 * This class represents the Interceptors Master/Details Block used in the Interceptors Page.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class InterceptorsMasterDetailsBlock extends MasterDetailsBlock
{
    /** The associated page */
    private FormPage page;

    /** The input Server Configuration */
    private ServerConfigurationV151 serverConfiguration;

    /** The Interceptors List */
    private List<Interceptor> interceptors;

    /** The Details Page */
    private InterceptorDetailsPage detailsPage;

    private static final String NEW_NAME = "New Interceptor ";

    // UI Fields
    private CheckboxTableViewer viewer;
    private Button addButton;
    private Button deleteButton;
    private Button upButton;
    private Button downButton;


    /**
     * Creates a new instance of InterceptorsMasterDetailsBlock.
     *
     * @param page
     */
    public InterceptorsMasterDetailsBlock( FormPage page )
    {
        this.page = page;
        serverConfiguration = ( ServerConfigurationV151 ) ( ( ServerConfigurationEditor ) page.getEditor() ).getServerConfiguration();
        interceptors = serverConfiguration.getInterceptors();
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.MasterDetailsBlock#createMasterPart(org.eclipse.ui.forms.IManagedForm, org.eclipse.swt.widgets.Composite)
     */
    protected void createMasterPart( final IManagedForm managedForm, Composite parent )
    {
        FormToolkit toolkit = managedForm.getToolkit();

        // Creating the Section
        Section section = toolkit.createSection( parent, Section.TITLE_BAR | Section.DESCRIPTION );
        section.setText( "All Interceptors" );
        section
            .setDescription( " Set the Interceptors used in the server. Use the \"Up\" and \"Down\" buttons to change the order." );
        section.marginWidth = 10;
        section.marginHeight = 5;
        Composite client = toolkit.createComposite( section, SWT.WRAP );
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.makeColumnsEqualWidth = false;
        layout.marginWidth = 2;
        layout.marginHeight = 2;
        client.setLayout( layout );
        toolkit.paintBordersFor( client );
        section.setClient( client );

        // Creatig the Table and Table Viewer
        Table table = toolkit.createTable( client, SWT.CHECK );
        GridData gd = new GridData( SWT.FILL, SWT.FILL, true, true, 1, 4 );
        gd.heightHint = 20;
        gd.widthHint = 100;
        table.setLayoutData( gd );
        final SectionPart spart = new SectionPart( section );
        managedForm.addPart( spart );
        viewer = new CheckboxTableViewer( table );
        viewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                managedForm.fireSelectionChanged( spart, event.getSelection() );
            }
        } );
        viewer.setContentProvider( new ArrayContentProvider() );
        viewer.setLabelProvider( new LabelProvider()
        {
            public Image getImage( Object element )
            {
                return ApacheDSConfigurationPlugin.getDefault().getImage( ApacheDSConfigurationPluginConstants.IMG_INTERCEPTOR );
            }
        } );

        // Creating the button(s)
        addButton = toolkit.createButton( client, "Add...", SWT.PUSH ); //$NON-NLS-1$
        addButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );

        deleteButton = toolkit.createButton( client, "Delete", SWT.PUSH );
        deleteButton.setEnabled( false );
        deleteButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );

        upButton = toolkit.createButton( client, "Up", SWT.PUSH );
        upButton.setEnabled( false );
        upButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );

        downButton = toolkit.createButton( client, "Down", SWT.PUSH );
        downButton.setEnabled( false );
        downButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );

        initFromInput();
        addListeners();
    }


    /**
     * Initializes the page with the Editor input.
     */
    private void initFromInput()
    {
        viewer.setInput( interceptors );
    }


    /**
     * Add listeners to UI fields.
     */
    private void addListeners()
    {
        viewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                viewer.refresh();

                deleteButton.setEnabled( !event.getSelection().isEmpty() );

                enableDisableUpDownButtons();
            }
        } );

        addButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                Interceptor newInterceptor = new Interceptor( getNewName() );
                interceptors.add( newInterceptor );
                viewer.refresh();
                viewer.setSelection( new StructuredSelection( newInterceptor ) );
                setEditorDirty();
            }
        } );

        deleteButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                StructuredSelection selection = ( StructuredSelection ) viewer.getSelection();
                if ( !selection.isEmpty() )
                {
                    Interceptor interceptor = ( Interceptor ) selection.getFirstElement();

                    interceptors.remove( interceptor );
                    viewer.refresh();
                    setEditorDirty();
                }
            }
        } );

        upButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                StructuredSelection selection = ( StructuredSelection ) viewer.getSelection();
                if ( !selection.isEmpty() )
                {
                    Interceptor interceptor = ( Interceptor ) selection.getFirstElement();

                    int index = interceptors.indexOf( interceptor );
                    if ( index > 0 )
                    {
                        Interceptor interceptorBefore = interceptors.get( index - 1 );
                        if ( interceptorBefore != null )
                        {
                            interceptors.set( index - 1, interceptor );
                            interceptors.set( index, interceptorBefore );

                            viewer.refresh();
                            setEditorDirty();
                            enableDisableUpDownButtons();
                        }
                    }
                }
            }
        } );

        downButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                StructuredSelection selection = ( StructuredSelection ) viewer.getSelection();
                if ( !selection.isEmpty() )
                {
                    Interceptor interceptor = ( Interceptor ) selection.getFirstElement();

                    int index = interceptors.indexOf( interceptor );
                    if ( index < ( interceptors.size() - 1 ) )
                    {
                        Interceptor interceptorAfter = interceptors.get( index + 1 );
                        if ( interceptorAfter != null )
                        {
                            interceptors.set( index + 1, interceptor );
                            interceptors.set( index, interceptorAfter );

                            viewer.refresh();
                            setEditorDirty();
                            enableDisableUpDownButtons();
                        }
                    }
                }
            }
        } );
    }


    /**
     * Gets a new Name for a new Extended Operation.
     *
     * @return 
     *      a new Name for a new Extended Operation
     */
    private String getNewName()
    {
        int counter = 1;
        String name = NEW_NAME;
        boolean ok = false;

        while ( !ok )
        {
            ok = true;
            name = NEW_NAME + counter;

            for ( Interceptor interceptor : interceptors )
            {
                if ( interceptor.getName().equalsIgnoreCase( name ) )
                {
                    ok = false;
                }
            }

            counter++;
        }

        return name;
    }


    /**
     * Enables or Disables the Up and Down Buttons.
     */
    private void enableDisableUpDownButtons()
    {
        StructuredSelection selection = ( StructuredSelection ) viewer.getSelection();

        upButton.setEnabled( !selection.isEmpty() );
        downButton.setEnabled( !selection.isEmpty() );
        if ( !selection.isEmpty() )
        {
            Interceptor interceptor = ( Interceptor ) selection.getFirstElement();
            upButton.setEnabled( interceptors.indexOf( interceptor ) != 0 );
            downButton.setEnabled( interceptors.indexOf( interceptor ) != ( interceptors.size() - 1 ) );
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.MasterDetailsBlock#createToolBarActions(org.eclipse.ui.forms.IManagedForm)
     */
    protected void createToolBarActions( IManagedForm managedForm )
    {
        final ScrolledForm form = managedForm.getForm();

        // Horizontal layout Action
        Action horizontalAction = new Action( "Horizontal layout", Action.AS_RADIO_BUTTON ) { //$NON-NLS-1$
            public void run()
            {
                sashForm.setOrientation( SWT.HORIZONTAL );
                form.reflow( true );
            }
        };
        horizontalAction.setChecked( true );
        horizontalAction.setToolTipText( "Horizontal Orientation" ); //$NON-NLS-1$
        horizontalAction.setImageDescriptor( ApacheDSConfigurationPlugin.getDefault().getImageDescriptor(
            ApacheDSConfigurationPluginConstants.IMG_HORIZONTAL_ORIENTATION ) );

        // Vertical layout Action
        Action verticalAction = new Action( "Vertical Orientation", Action.AS_RADIO_BUTTON ) { //$NON-NLS-1$
            public void run()
            {
                sashForm.setOrientation( SWT.VERTICAL );
                form.reflow( true );
            }
        };
        verticalAction.setChecked( false );
        verticalAction.setToolTipText( "Vertical Orientation" ); //$NON-NLS-1$
        verticalAction.setImageDescriptor( ApacheDSConfigurationPlugin.getDefault().getImageDescriptor(
            ApacheDSConfigurationPluginConstants.IMG_VERTICAL_ORIENTATION ) );

        form.getToolBarManager().add( horizontalAction );
        form.getToolBarManager().add( verticalAction );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.MasterDetailsBlock#registerPages(org.eclipse.ui.forms.DetailsPart)
     */
    protected void registerPages( DetailsPart detailsPart )
    {
        detailsPage = new InterceptorDetailsPage( this );
        detailsPart.registerPage( Interceptor.class, detailsPage );
    }


    /**
     * Sets the Editor as dirty.
     */
    public void setEditorDirty()
    {
        ( ( ServerConfigurationEditor ) page.getEditor() ).setDirty( true );
    }


    /**
     * Saves the necessary elements to the input model.
     */
    public void save()
    {
        detailsPage.commit( true );
        viewer.setInput( interceptors );
    }
}
