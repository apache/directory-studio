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

package org.apache.directory.studio.ldapbrowser.ui.editors.schemabrowser;


import org.apache.directory.shared.ldap.schema.syntax.AbstractSchemaDescription;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;


/**
 * A base implementation used from all schema master pages.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public abstract class SchemaPage
{

    /** The connection widget */
    protected BrowserConnectionWidgetContributionItem connectionCombo;

    /** The show default schema action */
    protected ShowDefaultSchemaAction showDefaultSchemaAction;

    /** The reload schema action */
    protected ReloadSchemaAction reloadSchemaAction;

    /** The schema browser */
    protected SchemaBrowser schemaBrowser;

    /** The toolkit used to create controls */
    protected FormToolkit toolkit;

    /** The outer form */
    protected Form form;

    /** The sash form, used to split the master and detail form */
    protected SashForm sashForm;

    /** The master form, contains the schema element list */
    protected ScrolledForm masterForm;

    /** The detail form, contains the schema details */
    protected ScrolledForm detailForm;

    /** The schema details page */
    protected SchemaDetailsPage detailsPage;

    /** The section of the master form */
    protected Section section;

    /** The filter field of the master form */
    protected Text filterText;

    /** The list with all schema elements */
    protected TableViewer viewer;

    /** Flag indicating if the viewer's selection is changed programatically */
    protected boolean inChange;


    /**
     * Creates a new instance of SchemaPage.
     *
     * @param schemaBrowser the schema browser
     */
    public SchemaPage( SchemaBrowser schemaBrowser )
    {
        this.schemaBrowser = schemaBrowser;
        this.inChange = false;
    }


    /**
     * Refreshes this schema page.
     */
    public void refresh()
    {
        Schema schema = null;
        if ( showDefaultSchemaAction.isChecked() )
        {
            schema = Schema.DEFAULT_SCHEMA;
        }
        else if ( getConnection() != null )
        {
            schema = getConnection().getSchema();
        }

        if ( viewer.getInput() != schema )
        {
            viewer.setInput( schema );
            viewer.setSelection( StructuredSelection.EMPTY );
        }

        form.setText( getTitle() );
        viewer.refresh();
    }


    /**
     * Gets the title of this schema page.
     *
     * @return the title
     */
    protected abstract String getTitle();


    /**
     * Gets the filter description.
     *
     * @return the filter description
     */
    protected abstract String getFilterDescription();


    /**
     * Gets the content provider.
     * 
     * @return the content provider
     */
    protected abstract IStructuredContentProvider getContentProvider();


    /**
     * Gets the label provider.
     * 
     * @return the label provider
     */
    protected abstract ITableLabelProvider getLabelProvider();


    /**
     * Gets the sorter.
     * 
     * @return the sorter
     */
    protected abstract ViewerSorter getSorter();


    /**
     * Gets the filter.
     * 
     * @return the filter
     */
    protected abstract ViewerFilter getFilter();


    /**
     * Gets the details page.
     * 
     * @return the details page
     */
    protected abstract SchemaDetailsPage getDetailsPage();


    /**
     * Creates the master page.
     *
     * @param body the parent composite
     */
    //protected abstract void createMaster( Composite body );
    private void createMaster( Composite parent )
    {
        // create section
        section = toolkit.createSection( parent, Section.DESCRIPTION );
        section.marginWidth = 10;
        section.marginHeight = 12;
        section.setText( getTitle() );
        section.setDescription( getFilterDescription() );
        toolkit.createCompositeSeparator( section );

        // create client
        Composite client = toolkit.createComposite( section, SWT.WRAP );
        GridLayout layout = new GridLayout( 2, false );
        layout.marginWidth = 5;
        layout.marginHeight = 5;
        client.setLayout( layout );
        section.setClient( client );

        // create filter field
        toolkit.createLabel( client, "Filter:" );
        this.filterText = toolkit.createText( client, "", SWT.NONE );
        this.filterText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        this.filterText.setData( FormToolkit.KEY_DRAW_BORDER, FormToolkit.TREE_BORDER );
        this.filterText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                viewer.refresh();
            }
        } );

        // create table
        Table t = toolkit.createTable( client, SWT.NONE );
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.horizontalSpan = 2;
        gd.heightHint = 20;
        gd.widthHint = 100;
        t.setLayoutData( gd );
        toolkit.paintBordersFor( client );

        // setup viewer
        viewer = new TableViewer( t );
        viewer.setContentProvider( getContentProvider() );
        viewer.setLabelProvider( getLabelProvider() );
        viewer.setSorter( getSorter() );
        viewer.addFilter( getFilter() );
    }


    /**
     * Creates the detail page.
     *
     * @param body the parent composite
     */
    private void createDetail( Composite body )
    {
        detailsPage = getDetailsPage();
        detailsPage.createContents( this.detailForm );
    }


    /**
     * Selects the given object in the list. Causes also an input
     * change of the details page.
     *
     * @param obj the object to select
     */
    public void select( Object obj )
    {
        ISelection newSelection = new StructuredSelection( obj );
        ISelection oldSelection = this.viewer.getSelection();

        if ( !newSelection.equals( oldSelection ) )
        {
            inChange = true;
            this.viewer.setSelection( newSelection, true );
            if ( this.viewer.getSelection().isEmpty() )
            {
                this.filterText.setText( "" );
                this.viewer.setSelection( newSelection, true );
            }
            inChange = false;
        }
    }


    /**
     * Disposed this page and the details page.
     */
    public void dispose()
    {
        this.detailsPage.dispose();

        this.schemaBrowser = null;
        this.toolkit.dispose();
        this.toolkit = null;
    }


    /**
     * Creates this schema page and details page. 
     *
     * @param parent the parent composite
     * @return the created composite.
     */
    Control createControl( Composite parent )
    {
        this.toolkit = new FormToolkit( parent.getDisplay() );
        this.form = this.toolkit.createForm( parent );
        this.form.getBody().setLayout( new FillLayout() );

        this.sashForm = new SashForm( this.form.getBody(), SWT.HORIZONTAL );
        this.sashForm.setLayout( new FillLayout() );

        this.masterForm = this.toolkit.createScrolledForm( this.sashForm );
        this.detailForm = new ScrolledForm( this.sashForm, SWT.V_SCROLL | this.toolkit.getOrientation() );
        this.detailForm.setExpandHorizontal( true );
        this.detailForm.setExpandVertical( true );
        this.detailForm.setBackground( this.toolkit.getColors().getBackground() );
        this.detailForm.setForeground( this.toolkit.getColors().getColor( IFormColors.TITLE ) );
        this.detailForm.setFont( JFaceResources.getHeaderFont() );
        this.sashForm.setWeights( new int[]
            { 50, 50 } );

        this.masterForm.getBody().setLayout( new FillLayout() );
        this.createMaster( this.masterForm.getBody() );

        this.detailForm.getBody().setLayout( new FillLayout() );
        this.createDetail( this.detailForm.getBody() );
        viewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                ISelection selection = event.getSelection();
                if ( selection.isEmpty() )
                {
                    detailsPage.setInput( null );
                }
                else
                {
                    Object obj = ( ( StructuredSelection ) selection ).getFirstElement();
                    detailsPage.setInput( obj );

                    // Do not set the input of the schema browser if 
                    // the selection was changed programatically.
                    if ( !inChange && obj instanceof AbstractSchemaDescription )
                    {
                        schemaBrowser.setInput( new SchemaBrowserInput( getConnection(), ( AbstractSchemaDescription ) obj ) );
                    }
                }
            }
        } );

        connectionCombo = new BrowserConnectionWidgetContributionItem( this );
        this.form.getToolBarManager().add( connectionCombo );
        this.form.getToolBarManager().add( new Separator() );
        showDefaultSchemaAction = new ShowDefaultSchemaAction( schemaBrowser );
        this.form.getToolBarManager().add( showDefaultSchemaAction );
        this.form.getToolBarManager().add( new Separator() );
        reloadSchemaAction = new ReloadSchemaAction( this );
        this.form.getToolBarManager().add( reloadSchemaAction );
        this.form.updateToolBar();

        this.refresh();

        return this.form;
    }


    /**
     * Gets the schema browser.
     * 
     * @return the schema browser
     */
    public SchemaBrowser getSchemaBrowser()
    {
        return schemaBrowser;
    }


    /**
     * Gets the connection.
     * 
     * @return the connection
     */
    public IBrowserConnection getConnection()
    {
        return connectionCombo.getConnection();
    }


    /**
     * Sets the connection.
     * 
     * @param connection the connection
     */
    public void setConnection( IBrowserConnection connection )
    {
        connectionCombo.setConnection( connection );
        reloadSchemaAction.updateEnabledState();
        refresh();
    }


    /**
     * Checks if is show default schema.
     * 
     * @return true, if is show default schema
     */
    public boolean isShowDefaultSchema()
    {
        return showDefaultSchemaAction.isChecked();
    }


    /**
     * Sets the show default schema flag.
     * 
     * @param b the show default schema flag
     */
    public void setShowDefaultSchema( boolean b )
    {
        showDefaultSchemaAction.setChecked( b );
        connectionCombo.updateEnabledState();
        reloadSchemaAction.updateEnabledState();
        refresh();
    }
}
