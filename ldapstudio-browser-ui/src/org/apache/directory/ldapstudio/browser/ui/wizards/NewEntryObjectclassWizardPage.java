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

package org.apache.directory.ldapstudio.browser.ui.wizards;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.internal.model.Attribute;
import org.apache.directory.ldapstudio.browser.core.internal.model.DummyEntry;
import org.apache.directory.ldapstudio.browser.core.internal.model.Value;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.core.model.ModelModificationException;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.widgets.BaseWidgetUtils;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;


public class NewEntryObjectclassWizardPage extends WizardPage
{

    private final static int SIZING_SELECTION_WIDGET_HEIGHT = 250;

    private final static int SIZING_SELECTION_WIDGET_WIDTH = 400;

    private NewEntryWizard wizard;

    private List availableObjectClasses;

    private ListViewer availableObjectClassesViewer;

    private List selectedObjectClasses;

    private ListViewer selectedObjectClassesViewer;

    private Button addButton;

    private Button removeButton;


    public NewEntryObjectclassWizardPage( String pageName, NewEntryWizard wizard )
    {
        super( pageName );
        super.setTitle( "Object Classes" );
        super
            .setDescription( "Please select object classes of the new entry. Select at least one structural object class." );
        super
            .setImageDescriptor( BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_ENTRY_WIZARD ) );
        super.setPageComplete( false );

        this.wizard = wizard;

        this.availableObjectClasses = new ArrayList();
        this.selectedObjectClasses = new ArrayList();
    }


    private void validate()
    {
        if ( !this.selectedObjectClasses.isEmpty() )
        {
            super.setPageComplete( true );
            saveState();
        }
        else
        {
            super.setPageComplete( false );
        }
    }


    private void loadState()
    {
        availableObjectClasses.clear();
        selectedObjectClasses.clear();

        if ( wizard.getSelectedConnection() != null )
        {
            availableObjectClasses.addAll( Arrays.asList( wizard.getSelectedConnection().getSchema()
                .getObjectClassDescriptionNames() ) );
        }

        DummyEntry newEntry = wizard.getNewEntry();
        IAttribute ocAttribute = newEntry.getAttribute( IAttribute.OBJECTCLASS_ATTRIBUTE );
        if ( ocAttribute != null )
        {
            String[] ocValues = ocAttribute.getStringValues();
            for ( int i = 0; i < ocValues.length; i++ )
            {
                String oc = ocValues[i];
                availableObjectClasses.remove( oc );
                selectedObjectClasses.add( oc );
            }
        }

        availableObjectClassesViewer.refresh();
        selectedObjectClassesViewer.refresh();
    }


    private void saveState()
    {
        DummyEntry newEntry = wizard.getNewEntry();

        try
        {
            EventRegistry.suspendEventFireingInCurrentThread();

            // set new objectClass values
            IAttribute ocAttribute = newEntry.getAttribute( IAttribute.OBJECTCLASS_ATTRIBUTE );
            if ( ocAttribute == null )
            {
                ocAttribute = new Attribute( newEntry, IAttribute.OBJECTCLASS_ATTRIBUTE );
                newEntry.addAttribute( ocAttribute, wizard );
            }
            IValue[] values = ocAttribute.getValues();
            for ( int i = 0; i < values.length; i++ )
            {
                ocAttribute.deleteValue( values[i], wizard );
            }
            for ( Iterator it = selectedObjectClasses.iterator(); it.hasNext(); )
            {
                Object oc = ( Object ) it.next();
                ocAttribute.addValue( new Value( ocAttribute, oc ), wizard );
            }

        }
        catch ( ModelModificationException e )
        {
            e.printStackTrace();
        }
        finally
        {
            EventRegistry.resumeEventFireingInCurrentThread();
        }
    }


    public void setVisible( boolean visible )
    {
        super.setVisible( visible );

        if ( visible )
        {
            loadState();
            validate();
        }
    }


    public boolean canFlipToNextPage()
    {
        return isPageComplete();
    }


    public IWizardPage getNextPage()
    {
        return super.getNextPage();
    }


    public IWizardPage getPreviousPage()
    {
        return super.getPreviousPage();
    }


    public void createControl( Composite parent )
    {

        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout gl = new GridLayout( 3, false );
        composite.setLayout( gl );
        composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );

        Label availableLabel = new Label( composite, SWT.NONE );
        availableLabel.setText( "Available object classes" );
        Label buttonLabel = new Label( composite, SWT.NONE );
        buttonLabel.setText( "" );
        Label selectedLabel = new Label( composite, SWT.NONE );
        selectedLabel.setText( "Selected object classes" );

        availableObjectClassesViewer = new ListViewer( composite );
        GridData data = new GridData( GridData.FILL_BOTH );
        data.heightHint = SIZING_SELECTION_WIDGET_HEIGHT;
        data.widthHint = ( int ) ( SIZING_SELECTION_WIDGET_WIDTH * 0.4 );
        availableObjectClassesViewer.getList().setLayoutData( data );
        availableObjectClassesViewer.setContentProvider( new ArrayContentProvider() );
        availableObjectClassesViewer.setLabelProvider( new LabelProvider() );
        availableObjectClassesViewer.setSorter( new ViewerSorter() );
        availableObjectClassesViewer.setInput( availableObjectClasses );
        availableObjectClassesViewer.addDoubleClickListener( new IDoubleClickListener()
        {
            public void doubleClick( DoubleClickEvent event )
            {
                add( event.getSelection() );
            }
        } );

        Composite buttonComposite = new Composite( composite, SWT.NONE );
        gl = new GridLayout( 1, true );
        buttonComposite.setLayout( gl );
        data = new GridData( GridData.FILL_BOTH );
        data.heightHint = SIZING_SELECTION_WIDGET_HEIGHT;
        // data.widthHint = (int)(SIZING_SELECTION_WIDGET_WIDTH * 0.2);
        data.horizontalAlignment = SWT.CENTER;
        buttonComposite.setLayoutData( data );
        Label label0 = new Label( buttonComposite, SWT.NONE );
        data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        label0.setLayoutData( data );
        addButton = BaseWidgetUtils.createButton( buttonComposite, "&Add", 1 );
        removeButton = BaseWidgetUtils.createButton( buttonComposite, "&Remove", 1 );
        Label label3 = new Label( buttonComposite, SWT.NONE );
        data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        label3.setLayoutData( data );

        addButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                add( availableObjectClassesViewer.getSelection() );
            }
        } );

        removeButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                remove( selectedObjectClassesViewer.getSelection() );
            }
        } );

        selectedObjectClassesViewer = new ListViewer( composite );
        data = new GridData( GridData.FILL_BOTH );
        data.heightHint = SIZING_SELECTION_WIDGET_HEIGHT;
        data.widthHint = ( int ) ( SIZING_SELECTION_WIDGET_WIDTH * 0.4 );
        selectedObjectClassesViewer.getList().setLayoutData( data );
        selectedObjectClassesViewer.setContentProvider( new ArrayContentProvider() );
        selectedObjectClassesViewer.setLabelProvider( new LabelProvider() );
        selectedObjectClassesViewer.setSorter( new ViewerSorter() );
        selectedObjectClassesViewer.setInput( selectedObjectClasses );
        selectedObjectClassesViewer.addDoubleClickListener( new IDoubleClickListener()
        {
            public void doubleClick( DoubleClickEvent event )
            {
                remove( event.getSelection() );
            }
        } );

        setControl( composite );
    }


    private void add( ISelection iselection )
    {
        IStructuredSelection selection = ( IStructuredSelection ) iselection;
        Iterator it = selection.iterator();
        while ( it.hasNext() )
        {
            Object oc = it.next();
            availableObjectClasses.remove( oc );
            selectedObjectClasses.add( oc );
            availableObjectClassesViewer.refresh();
            selectedObjectClassesViewer.refresh();
            this.validate();
        }
    }


    private void remove( ISelection iselection )
    {
        IStructuredSelection selection = ( IStructuredSelection ) iselection;
        Iterator it = selection.iterator();
        while ( it.hasNext() )
        {
            Object oc = it.next();
            selectedObjectClasses.remove( oc );
            availableObjectClasses.add( oc );
            availableObjectClassesViewer.refresh();
            selectedObjectClassesViewer.refresh();
            this.validate();
        }
    }


    public void saveDialogSettings()
    {

    }

}
