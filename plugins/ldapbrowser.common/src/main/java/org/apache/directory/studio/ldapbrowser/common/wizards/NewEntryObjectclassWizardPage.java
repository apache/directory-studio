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

package org.apache.directory.studio.ldapbrowser.common.wizards;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.api.ldap.model.constants.SchemaConstants;
import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.apache.directory.api.ldap.model.schema.ObjectClassTypeEnum;
import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.connection.ui.RunnableContextRunner;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.jobs.ReloadSchemaRunnable;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Attribute;
import org.apache.directory.studio.ldapbrowser.core.model.impl.DummyEntry;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Value;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


/**
 * The NewEntryTypeWizardPage is used to select the entry's object classes.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class NewEntryObjectclassWizardPage extends WizardPage
{

    /** The Constant SIZING_SELECTION_WIDGET_HEIGHT. */
    private final static int SIZING_SELECTION_WIDGET_HEIGHT = 250;

    /** The Constant SIZING_SELECTION_WIDGET_WIDTH. */
    private final static int SIZING_SELECTION_WIDGET_WIDTH = 400;

    /** The wizard. */
    private NewEntryWizard wizard;

    /** The available object classes. */
    private List<ObjectClass> availableObjectClasses;

    /** The available object classes instant search. */
    private Text availableObjectClassesInstantSearch;

    /** The available object classes viewer. */
    private TableViewer availableObjectClassesViewer;

    /** The selected object classes. */
    private List<ObjectClass> selectedObjectClasses;

    /** The selected object classes viewer. */
    private TableViewer selectedObjectClassesViewer;

    /** The add button. */
    private Button addButton;

    /** The remove button. */
    private Button removeButton;

    private LabelProvider labelProvider = new LabelProvider()
    {
        /**
         * {@inheritDoc}
         */
        public String getText( Object element )
        {
            if ( element instanceof ObjectClass )
            {
                ObjectClass ocd = ( ObjectClass ) element;
                return SchemaUtils.toString( ocd );
            }

            // Default
            return super.getText( element );
        }


        /**
         * {@inheritDoc}
         */
        public Image getImage( Object element )
        {
            if ( element instanceof ObjectClass )
            {
                ObjectClass ocd = ( ObjectClass ) element;
                switch ( ocd.getType() )
                {
                    case STRUCTURAL:
                        return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_OCD_STRUCTURAL );
                    case ABSTRACT:
                        return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_OCD_ABSTRACT );
                    case AUXILIARY:
                        return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_OCD_AUXILIARY );
                    default:
                        return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_OCD );
                }
            }

            // Default
            return super.getImage( element );
        }
    };


    /**
     * Creates a new instance of NewEntryObjectclassWizardPage.
     * 
     * @param pageName the page name
     * @param wizard the wizard
     */
    public NewEntryObjectclassWizardPage( String pageName, NewEntryWizard wizard )
    {
        super( pageName );
        setTitle( Messages.getString( "NewEntryObjectclassWizardPage.ObjectClasses" ) ); //$NON-NLS-1$
        setDescription( Messages.getString( "NewEntryObjectclassWizardPage.ObjectClassesDescription" ) ); //$NON-NLS-1$
        setImageDescriptor( BrowserCommonActivator.getDefault().getImageDescriptor(
            BrowserCommonConstants.IMG_ENTRY_WIZARD ) );
        setPageComplete( false );

        this.wizard = wizard;
        this.availableObjectClasses = new ArrayList<ObjectClass>();
        this.selectedObjectClasses = new ArrayList<ObjectClass>();
    }


    /**
     * Validates the input fields.
     */
    private void validate()
    {
        if ( !selectedObjectClasses.isEmpty() )
        {
            boolean hasOneStructuralOC = false;
            for ( ObjectClass ocd : selectedObjectClasses )
            {
                if ( ocd.getType() == ObjectClassTypeEnum.STRUCTURAL )
                {
                    hasOneStructuralOC = true;
                    break;
                }
            }
            if ( !hasOneStructuralOC )
            {
                setMessage(
                    Messages.getString( "NewEntryObjectclassWizardPage.SelectStructuralObjectClass" ), WizardPage.WARNING ); //$NON-NLS-1$
            }
            else
            {
                setMessage( null );
            }

            setPageComplete( true );
            saveState();
        }
        else
        {
            setPageComplete( false );
            setMessage( null );
        }
    }


    /**
     * Loads the state of selected and available object classes from
     * the prototype entry. Called when this page becomes visible.
     */
    private void loadState()
    {
        availableObjectClasses.clear();
        selectedObjectClasses.clear();

        if ( wizard.getSelectedConnection() != null )
        {
            availableObjectClasses.addAll( wizard.getSelectedConnection().getSchema().getObjectClassDescriptions() );

            DummyEntry newEntry = wizard.getPrototypeEntry();
            IAttribute ocAttribute = newEntry.getAttribute( SchemaConstants.OBJECT_CLASS_AT );
            if ( ocAttribute != null )
            {
                for ( IValue ocValue : ocAttribute.getValues() )
                {
                    if ( !ocValue.isEmpty() )
                    {
                        ObjectClass ocd = wizard.getSelectedConnection().getSchema()
                            .getObjectClassDescription( ocValue.getStringValue() );
                        availableObjectClasses.remove( ocd );
                        selectedObjectClasses.add( ocd );
                    }
                }
            }
        }

        availableObjectClassesViewer.refresh();
        selectedObjectClassesViewer.refresh();
    }


    /**
     * Saves the state of selected object classes to the entry.
     */
    private void saveState()
    {
        DummyEntry newEntry = wizard.getPrototypeEntry();

        try
        {
            EventRegistry.suspendEventFiringInCurrentThread();

            // set new objectClass values
            IAttribute ocAttribute = newEntry.getAttribute( SchemaConstants.OBJECT_CLASS_AT );
            if ( ocAttribute == null )
            {
                ocAttribute = new Attribute( newEntry, SchemaConstants.OBJECT_CLASS_AT );
                newEntry.addAttribute( ocAttribute );
            }
            IValue[] values = ocAttribute.getValues();
            for ( IValue value : values )
            {
                ocAttribute.deleteValue( value );
            }
            for ( ObjectClass ocd : selectedObjectClasses )
            {
                ocAttribute.addValue( new Value( ocAttribute, ocd.getNames().get( 0 ) ) );
            }
        }
        finally
        {
            EventRegistry.resumeEventFiringInCurrentThread();
        }
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation initializes the list of available and selected
     * object classes when this page becomes visible.
     */
    public void setVisible( boolean visible )
    {
        super.setVisible( visible );

        if ( visible )
        {
            loadState();
            validate();
            availableObjectClassesInstantSearch.setFocus();
        }
    }


    /**
     * {@inheritDoc}
     */
    public void createControl( Composite parent )
    {

        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout gl = new GridLayout( 3, false );
        composite.setLayout( gl );
        composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );

        Label availableLabel = new Label( composite, SWT.NONE );
        availableLabel.setText( Messages.getString( "NewEntryObjectclassWizardPage.AvailableObjectClasses" ) ); //$NON-NLS-1$
        Label buttonLabel = new Label( composite, SWT.NONE );
        buttonLabel.setText( "" ); //$NON-NLS-1$
        Label selectedLabel = new Label( composite, SWT.NONE );
        selectedLabel.setText( Messages.getString( "NewEntryObjectclassWizardPage.SelectedObjectClasses" ) ); //$NON-NLS-1$

        Composite availableObjectClassesComposite = BaseWidgetUtils.createColumnContainer( composite, 1, 1 );

        Composite availableObjectClassesInstantSearchComposite = BaseWidgetUtils.createColumnContainer(
            availableObjectClassesComposite, 2, 1 );
        availableObjectClassesInstantSearch = new Text( availableObjectClassesInstantSearchComposite, SWT.NONE
            | SWT.BORDER | SWT.SEARCH | SWT.CANCEL );
        availableObjectClassesInstantSearch.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        availableObjectClassesInstantSearch.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                availableObjectClassesViewer.refresh();
                if ( availableObjectClassesViewer.getTable().getItemCount() >= 1 )
                {
                    Object item = availableObjectClassesViewer.getElementAt( 0 );
                    availableObjectClassesViewer.setSelection( new StructuredSelection( item ) );
                }
            }
        } );
        availableObjectClassesInstantSearch.addKeyListener( new KeyAdapter()
        {
            public void keyPressed( KeyEvent e )
            {
                if ( e.keyCode == SWT.ARROW_DOWN )
                {
                    availableObjectClassesViewer.getTable().setFocus();
                }
                else if ( e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR )
                {
                    add( availableObjectClassesViewer.getSelection() );
                }
            }
        } );
        ControlDecoration availableObjectClassesInstantSearchDecoration = new ControlDecoration(
            availableObjectClassesInstantSearch, SWT.TOP | SWT.LEFT, composite );
        availableObjectClassesInstantSearchDecoration.setDescriptionText( Messages
            .getString( "NewEntryObjectclassWizardPage.FilterDescription" ) ); //$NON-NLS-1$
        availableObjectClassesInstantSearchDecoration.setImage( FieldDecorationRegistry.getDefault()
            .getFieldDecoration( FieldDecorationRegistry.DEC_CONTENT_PROPOSAL ).getImage() );
        Button reloadButton = new Button( availableObjectClassesInstantSearchComposite, SWT.PUSH | SWT.FLAT );
        reloadButton.setToolTipText( Messages.getString( "NewEntryObjectclassWizardPage.ReloadSchema" ) ); //$NON-NLS-1$
        reloadButton.setImage( BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_REFRESH ) );
        reloadButton.addSelectionListener( new SelectionAdapter()
        {
            @Override
            public void widgetSelected( SelectionEvent e )
            {
                IBrowserConnection browserConnection = wizard.getSelectedConnection();
                ReloadSchemaRunnable runnable = new ReloadSchemaRunnable( browserConnection );
                RunnableContextRunner.execute( runnable, wizard.getContainer(), true );
                setVisible( true );
            }
        } );

        availableObjectClassesViewer = new TableViewer( availableObjectClassesComposite );
        GridData data = new GridData( GridData.FILL_BOTH );
        data.heightHint = SIZING_SELECTION_WIDGET_HEIGHT;
        data.widthHint = ( int ) ( SIZING_SELECTION_WIDGET_WIDTH * 0.4 );
        availableObjectClassesViewer.getTable().setLayoutData( data );
        availableObjectClassesViewer.setContentProvider( new ArrayContentProvider() );
        availableObjectClassesViewer.setLabelProvider( labelProvider );
        availableObjectClassesViewer.setSorter( new ViewerSorter() );
        availableObjectClassesViewer.addFilter( new InstantSearchFilter( availableObjectClassesInstantSearch ) );
        availableObjectClassesViewer.setInput( availableObjectClasses );
        availableObjectClassesViewer.addDoubleClickListener( new IDoubleClickListener()
        {
            public void doubleClick( DoubleClickEvent event )
            {
                add( event.getSelection() );
            }
        } );
        availableObjectClassesViewer.getTable().addKeyListener( new KeyAdapter()
        {
            public void keyPressed( KeyEvent e )
            {
                if ( e.keyCode == SWT.ARROW_UP )
                {
                    if ( availableObjectClassesViewer.getTable().getSelectionIndex() <= 0 )
                    {
                        availableObjectClassesInstantSearch.setFocus();
                    }
                }
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
        addButton = BaseWidgetUtils.createButton( buttonComposite, Messages
            .getString( "NewEntryObjectclassWizardPage.AddButton" ), 1 ); //$NON-NLS-1$
        removeButton = BaseWidgetUtils.createButton( buttonComposite, Messages
            .getString( "NewEntryObjectclassWizardPage.RemoveButton" ), 1 ); //$NON-NLS-1$
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

        selectedObjectClassesViewer = new TableViewer( composite );
        data = new GridData( GridData.FILL_BOTH );
        data.heightHint = SIZING_SELECTION_WIDGET_HEIGHT;
        data.widthHint = ( int ) ( SIZING_SELECTION_WIDGET_WIDTH * 0.4 );
        selectedObjectClassesViewer.getTable().setLayoutData( data );
        selectedObjectClassesViewer.setContentProvider( new ArrayContentProvider() );
        selectedObjectClassesViewer.setLabelProvider( labelProvider );
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


    /**
     * Adds the selected object classes and all superiors
     * to the list of selected object classes.
     * 
     * @param iselection the selection
     */
    private void add( ISelection iselection )
    {
        IStructuredSelection selection = ( IStructuredSelection ) iselection;
        Schema schema = wizard.getSelectedConnection().getSchema();
        Iterator<?> it = selection.iterator();
        while ( it.hasNext() )
        {
            ObjectClass ocd = ( ObjectClass ) it.next();
            if ( availableObjectClasses.contains( ocd ) && !selectedObjectClasses.contains( ocd ) )
            {
                availableObjectClasses.remove( ocd );
                selectedObjectClasses.add( ocd );

                // recursively add superior object classes
                List<ObjectClass> superiorObjectClassDescriptions = SchemaUtils
                    .getSuperiorObjectClassDescriptions( ocd, schema );
                if ( !superiorObjectClassDescriptions.isEmpty() )
                {
                    add( new StructuredSelection( superiorObjectClassDescriptions ) );
                }
            }
        }

        availableObjectClassesViewer.refresh();
        selectedObjectClassesViewer.refresh();
        validate();

        if ( !"".equals( availableObjectClassesInstantSearch.getText() ) ) //$NON-NLS-1$
        {
            availableObjectClassesInstantSearch.setText( "" ); //$NON-NLS-1$
            availableObjectClassesInstantSearch.setFocus();
        }
    }


    /**
     * Removes the selected object classes and all sub classes
     * from the list of selected object classes.
     * 
     * @param iselection the iselection
     */
    private void remove( ISelection iselection )
    {
        IStructuredSelection selection = ( IStructuredSelection ) iselection;
        Schema schema = wizard.getSelectedConnection().getSchema();
        Iterator<?> it = selection.iterator();
        while ( it.hasNext() )
        {
            ObjectClass ocd = ( ObjectClass ) it.next();
            if ( !availableObjectClasses.contains( ocd ) && selectedObjectClasses.contains( ocd ) )
            {
                selectedObjectClasses.remove( ocd );
                availableObjectClasses.add( ocd );

                // recursively remove sub object classes
                List<ObjectClass> subObjectClassDescriptions = SchemaUtils
                    .getSuperiorObjectClassDescriptions( ocd, schema );
                if ( !subObjectClassDescriptions.isEmpty() )
                {
                    remove( new StructuredSelection( subObjectClassDescriptions ) );
                }
            }
        }

        // re-add superior object classes of remaining object classes
        List<ObjectClass> copy = new ArrayList<ObjectClass>( selectedObjectClasses );
        for ( ObjectClass ocd : copy )
        {
            List<ObjectClass> superiorObjectClassDescriptions = SchemaUtils
                .getSuperiorObjectClassDescriptions( ocd, schema );
            if ( !superiorObjectClassDescriptions.isEmpty() )
            {
                add( new StructuredSelection( superiorObjectClassDescriptions ) );
            }
        }

        availableObjectClassesViewer.refresh();
        selectedObjectClassesViewer.refresh();
        validate();
    }

    /**
     * The Class InstantSearchFilter.
     */
    private class InstantSearchFilter extends ViewerFilter
    {

        /** The filter text. */
        private Text filterText;


        /**
         * Creates a new instance of InstantSearchFilter.
         * 
         * @param filterText the filter text
         */
        private InstantSearchFilter( Text filterText )
        {
            this.filterText = filterText;
        }


        /**
         * {@inheritDoc}
         */
        public boolean select( Viewer viewer, Object parentElement, Object element )
        {
            if ( element instanceof ObjectClass )
            {
                ObjectClass ocd = ( ObjectClass ) element;
                Collection<String> lowerCaseIdentifiers = SchemaUtils.getLowerCaseIdentifiers( ocd );
                for ( String s : lowerCaseIdentifiers )
                {
                    if ( Strings.toLowerCase( s ).startsWith( Strings.toLowerCase( filterText.getText() ) ) )
                    {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
