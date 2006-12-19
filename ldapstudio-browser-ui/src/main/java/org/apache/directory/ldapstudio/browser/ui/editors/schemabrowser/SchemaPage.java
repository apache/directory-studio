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

package org.apache.directory.ldapstudio.browser.ui.editors.schemabrowser;


import org.apache.directory.ldapstudio.browser.core.events.ConnectionUpdateEvent;
import org.apache.directory.ldapstudio.browser.core.events.ConnectionUpdateListener;
import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.ui.actions.SelectionUtils;
import org.apache.directory.ldapstudio.browser.ui.views.connection.ConnectionView;

import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;


public abstract class SchemaPage implements ISelectionListener, ConnectionUpdateListener
{

    protected IConnection selectedConnection;

    protected SchemaBrowser schemaBrowser;

    protected FormToolkit toolkit;

    protected Form form;

    protected SashForm sashForm;

    protected ScrolledForm masterForm;

    protected ScrolledForm detailForm;

    protected SchemaDetailsPage detailsPage;


    public SchemaPage( SchemaBrowser schemaBrowser )
    {
        this.schemaBrowser = schemaBrowser;

        this.selectedConnection = null;
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().addSelectionListener( this );
        EventRegistry.addConnectionUpdateListener( this );
    }


    protected abstract void refresh();


    protected abstract void createMaster( Composite body );


    protected abstract void createDetail( Composite body );


    public void selectionChanged( IWorkbenchPart part, ISelection selection )
    {
        if ( part.getClass() == ConnectionView.class )
        {
            IConnection[] connections = SelectionUtils.getConnections( selection );
            if ( connections.length == 1 )
                this.connectionSelected( connections[0] );
            else
                this.connectionSelected( null );
        }
    }


    void connectionSelected( IConnection connection )
    {
        this.selectedConnection = connection;
        if ( this.toolkit != null && this.form != null && !this.form.isDisposed() )
        {
            this.refresh();
        }
    }


    public final void connectionUpdated( ConnectionUpdateEvent connectionUpdateEvent )
    {
        if ( this.toolkit != null && this.form != null && !this.form.isDisposed() )
        {
            this.refresh();
        }
    }


    public void dispose()
    {
        if ( this.selectedConnection != null )
        {
            this.selectedConnection = null;
        }
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().removeSelectionListener( this );
        EventRegistry.removeConnectionUpdateListener( this );

        this.detailsPage.dispose();

        this.schemaBrowser = null;
        this.toolkit.dispose();
        this.toolkit = null;

    }


    public Control createControl( Composite parent )
    {

        this.toolkit = new FormToolkit( parent.getDisplay() );
        this.form = this.toolkit.createForm( parent );
        this.form.getBody().setLayout( new FillLayout() );

        this.sashForm = new SashForm( this.form.getBody(), SWT.HORIZONTAL );
        this.sashForm.setLayout( new FillLayout() );

        this.masterForm = this.toolkit.createScrolledForm( this.sashForm );
        // this.detailForm = this.toolkit.createScrolledForm(this.sashForm);
        this.detailForm = new ScrolledForm( this.sashForm, SWT.V_SCROLL | this.toolkit.getOrientation() );
        this.detailForm.setExpandHorizontal( true );
        this.detailForm.setExpandVertical( true );
        this.detailForm.setBackground( this.toolkit.getColors().getBackground() );
        this.detailForm.setForeground( this.toolkit.getColors().getColor( FormColors.TITLE ) );
        this.detailForm.setFont( JFaceResources.getHeaderFont() );
        this.sashForm.setWeights( new int[]
            { 50, 50 } );

        this.masterForm.getBody().setLayout( new FillLayout() );
        this.createMaster( this.masterForm.getBody() );

        this.detailForm.getBody().setLayout( new FillLayout() );
        this.createDetail( this.detailForm.getBody() );

        this.form.getToolBarManager().add( schemaBrowser.getShowDefaultSchemaAction() );
        this.form.getToolBarManager().add( new Separator() );
        this.form.getToolBarManager().add( schemaBrowser.getReloadSchemaAction() );
        this.form.getToolBarManager().add( new Separator() );
        this.form.getToolBarManager().add( schemaBrowser.getBackAction() );
        this.form.getToolBarManager().add( schemaBrowser.getForwardAction() );
        this.form.updateToolBar();

        if ( schemaBrowser.getEditorSite().getPage().getSelection() != null
            && !schemaBrowser.getEditorSite().getPage().getSelection().isEmpty()
            && schemaBrowser.getEditorSite().getPage().getSelection() instanceof IStructuredSelection )
        {

            IConnection[] connections = SelectionUtils.getConnections( ( IStructuredSelection ) schemaBrowser
                .getEditorSite().getPage().getSelection() );
            IEntry[] entries = SelectionUtils.getEntries( ( IStructuredSelection ) schemaBrowser.getEditorSite()
                .getPage().getSelection() );
            IAttribute[] attributes = SelectionUtils.getAttributes( ( IStructuredSelection ) schemaBrowser
                .getEditorSite().getPage().getSelection() );
            IValue[] values = SelectionUtils.getValues( ( IStructuredSelection ) schemaBrowser.getEditorSite()
                .getPage().getSelection() );
            if ( connections != null && connections.length == 1 )
            {
                this.selectedConnection = connections[0];
            }
            else if ( entries != null && entries.length == 1 )
            {
                this.selectedConnection = entries[0].getConnection();
            }
            else if ( attributes != null && attributes.length == 1 )
            {
                this.selectedConnection = attributes[0].getEntry().getConnection();
            }
            else if ( values != null && values.length == 1 )
            {
                this.selectedConnection = values[0].getAttribute().getEntry().getConnection();
            }

            // Object obj =
            // ((IStructuredSelection)schemaBrowser.getEditorSite().getPage().getSelection()).getFirstElement();
            // if (obj instanceof IConnection) {
            // this.selectedConnection = (IConnection) obj;
            // }
        }
        this.refresh();

        return this.form;
    }


    public IConnection getSelectedConnection()
    {
        return selectedConnection;
    }

}
