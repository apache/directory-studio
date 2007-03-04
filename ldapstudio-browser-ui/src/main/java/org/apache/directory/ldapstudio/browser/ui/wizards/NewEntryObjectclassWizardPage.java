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
import java.util.Set;

import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.internal.model.Attribute;
import org.apache.directory.ldapstudio.browser.core.internal.model.DummyEntry;
import org.apache.directory.ldapstudio.browser.core.internal.model.Value;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.core.model.ModelModificationException;
import org.apache.directory.ldapstudio.browser.core.model.schema.ObjectClassDescription;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.widgets.BaseWidgetUtils;
import org.eclipse.jface.fieldassist.DecoratedField;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.IControlCreator;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


public class NewEntryObjectclassWizardPage extends WizardPage
{

    private final static int SIZING_SELECTION_WIDGET_HEIGHT = 250;

    private final static int SIZING_SELECTION_WIDGET_WIDTH = 400;

    private NewEntryWizard wizard;

    private List<ObjectClassDescription> availableObjectClasses;

    private Text availabeObjectClassesInstantSearch;

    private ListViewer availableObjectClassesViewer;

    private List<ObjectClassDescription> selectedObjectClasses;

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

        this.availableObjectClasses = new ArrayList<ObjectClassDescription>();
        this.selectedObjectClasses = new ArrayList<ObjectClassDescription>();
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
                .getObjectClassDescriptions() ) );

            DummyEntry newEntry = wizard.getNewEntry();
            IAttribute ocAttribute = newEntry.getAttribute( IAttribute.OBJECTCLASS_ATTRIBUTE );
            if ( ocAttribute != null )
            {
                String[] ocValues = ocAttribute.getStringValues();
                for ( int i = 0; i < ocValues.length; i++ )
                {
                    String ocValue = ocValues[i];
                    ObjectClassDescription ocd = wizard.getSelectedConnection().getSchema().getObjectClassDescription(
                        ocValue );
                    availableObjectClasses.remove( ocd );
                    selectedObjectClasses.add( ocd );
                }
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
                newEntry.addAttribute( ocAttribute );
            }
            IValue[] values = ocAttribute.getValues();
            for ( int i = 0; i < values.length; i++ )
            {
                ocAttribute.deleteValue( values[i] );
            }
            for ( Iterator<ObjectClassDescription> it = selectedObjectClasses.iterator(); it.hasNext(); )
            {
                ObjectClassDescription ocd = it.next();
                ocAttribute.addValue( new Value( ocAttribute, ocd.getNames()[0] ) );
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
            availabeObjectClassesInstantSearch.setFocus();
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

        Composite availableObjectClassesComposite = BaseWidgetUtils.createColumnContainer( composite, 1, 1 );

        if ( FieldDecorationRegistry.getDefault().getFieldDecoration( getClass().getName() ) == null )
        {
            FieldDecoration dummy = FieldDecorationRegistry.getDefault().getFieldDecoration(
                FieldDecorationRegistry.DEC_CONTENT_PROPOSAL );
            FieldDecorationRegistry.getDefault().registerFieldDecoration( getClass().getName(),
                "You may enter a filter to restrict the list below", dummy.getImage() );
        }
        final FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(
            getClass().getName() );
        final DecoratedField availabeObjectClassesInstantSearchField = new DecoratedField(
            availableObjectClassesComposite, SWT.BORDER, new IControlCreator()
            {
                public Control createControl( Composite parent, int style )
                {
                    return BaseWidgetUtils.createText( parent, "", 1 );
                }
            } );
        availabeObjectClassesInstantSearchField.addFieldDecoration( fieldDecoration, SWT.TOP | SWT.LEFT, true );
        availabeObjectClassesInstantSearchField.getLayoutControl().setLayoutData(
            new GridData( SWT.FILL, SWT.CENTER, true, false ) );
        availabeObjectClassesInstantSearch = ( Text ) availabeObjectClassesInstantSearchField.getControl();
        availabeObjectClassesInstantSearch.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                availableObjectClassesViewer.refresh();
                if ( availableObjectClassesViewer.getList().getItemCount() == 1 )
                {
                    Object item = availableObjectClassesViewer.getElementAt( 0 );
                    availableObjectClassesViewer.setSelection( new StructuredSelection( item ) );
                }
            }
        } );

        availableObjectClassesViewer = new ListViewer( availableObjectClassesComposite );
        GridData data = new GridData( GridData.FILL_BOTH );
        data.heightHint = SIZING_SELECTION_WIDGET_HEIGHT;
        data.widthHint = ( int ) ( SIZING_SELECTION_WIDGET_WIDTH * 0.4 );
        availableObjectClassesViewer.getList().setLayoutData( data );
        availableObjectClassesViewer.setContentProvider( new ArrayContentProvider() );
        availableObjectClassesViewer.setLabelProvider( new LabelProvider() );
        availableObjectClassesViewer.setSorter( new ViewerSorter() );
        availableObjectClassesViewer.addFilter( new InstantSearchFilter( availabeObjectClassesInstantSearch ) );
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
        Iterator<ObjectClassDescription> it = selection.iterator();
        while ( it.hasNext() )
        {
            ObjectClassDescription ocd = it.next();
            if ( availableObjectClasses.contains( ocd ) && !selectedObjectClasses.contains( ocd ) )
            {
                availableObjectClasses.remove( ocd );
                selectedObjectClasses.add( ocd );

                // recursively add superior object classes
                ObjectClassDescription[] superiorObjectClassDescriptions = ocd.getSuperiorObjectClassDescriptions();
                if ( superiorObjectClassDescriptions.length > 0 )
                {
                    add( new StructuredSelection( superiorObjectClassDescriptions ) );
                }
            }
        }

        availableObjectClassesViewer.refresh();
        selectedObjectClassesViewer.refresh();
        this.validate();

        if ( !"".equals( availabeObjectClassesInstantSearch.getText() ) )
        {
            availabeObjectClassesInstantSearch.setText( "" );
            availabeObjectClassesInstantSearch.setFocus();
        }
    }


    private void remove( ISelection iselection )
    {
        IStructuredSelection selection = ( IStructuredSelection ) iselection;
        Iterator<ObjectClassDescription> it = selection.iterator();
        while ( it.hasNext() )
        {
            ObjectClassDescription ocd = it.next();
            if ( !availableObjectClasses.contains( ocd ) && selectedObjectClasses.contains( ocd ) )
            {
                selectedObjectClasses.remove( ocd );
                availableObjectClasses.add( ocd );

                // recursively remove sub object classes
                ObjectClassDescription[] subObjectClassDescriptions = ocd.getSubObjectClassDescriptions();
                if ( subObjectClassDescriptions.length > 0 )
                {
                    remove( new StructuredSelection( subObjectClassDescriptions ) );
                }
            }
        }

        availableObjectClassesViewer.refresh();
        selectedObjectClassesViewer.refresh();
        this.validate();
    }


    public void saveDialogSettings()
    {

    }

    private class InstantSearchFilter extends ViewerFilter
    {
        private Text filterText;


        private InstantSearchFilter( Text filterText )
        {
            this.filterText = filterText;
        }


        /**
         * {@inheritDoc}
         */
        public boolean select( Viewer viewer, Object parentElement, Object element )
        {
            if ( element instanceof ObjectClassDescription )
            {
                ObjectClassDescription ocd = ( ObjectClassDescription ) element;
                Set lowerCaseIdentifierSet = ocd.getLowerCaseIdentifierSet();
                for ( Iterator<String> it = lowerCaseIdentifierSet.iterator(); it.hasNext(); )
                {
                    String s = it.next();
                    if ( s.toLowerCase().startsWith( filterText.getText().toLowerCase() ) )
                    {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
