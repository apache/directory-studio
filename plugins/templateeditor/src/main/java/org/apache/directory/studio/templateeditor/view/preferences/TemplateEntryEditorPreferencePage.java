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
package org.apache.directory.studio.templateeditor.view.preferences;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import org.apache.directory.studio.templateeditor.EntryTemplatePlugin;
import org.apache.directory.studio.templateeditor.EntryTemplatePluginConstants;
import org.apache.directory.studio.templateeditor.EntryTemplatePluginUtils;
import org.apache.directory.studio.templateeditor.model.FileTemplate;
import org.apache.directory.studio.templateeditor.model.Template;
import org.apache.directory.studio.templateeditor.view.ColumnsTableViewerComparator;
import org.apache.directory.studio.templateeditor.view.wizards.ExportTemplatesWizard;
import org.apache.directory.studio.templateeditor.view.wizards.ImportTemplatesWizard;


/**
 * This class implements the Template Entry Editor preference page.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class TemplateEntryEditorPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
    /** The root object for the templates viewer */
    private static final Object TEMPLATES_VIEWER_ROOT = new Object();

    /** The preferences store */
    private IPreferenceStore store;

    /** The preferences templates manager */
    private PreferencesTemplatesManager manager;

    // UI Fields
    private ToolItem objectClassPresentationToolItem;
    private ToolItem templatePresentationToolItem;
    private Composite templatesViewerComposite;
    private CheckboxTreeViewer templatesViewer;
    private Button importTemplatesButton;
    private Button exportTemplatesButton;
    private Button removeTemplateButton;
    private Button setDefaultTemplateButton;
    private Button useForAnyEntryButton;
    private Button useForOnlyEntriesWithTemplateButton;

    /** The selection listener for the templates viewer */
    private Listener templatesViewerSelectionListener = new Listener()
    {
        public void handleEvent( Event event )
        {
            if ( event.detail == SWT.CHECK )
            {
                templatesViewer.refresh();
                TreeItem item = ( TreeItem ) event.item;
                boolean checked = item.getChecked();
                checkItems( item, checked );
                checkPath( item.getParentItem(), checked, false );
                updateButtonsStates();
            }
        }


        /**
         * Checks the path of the item in the tree viewer.
         *
         * @param item
         *      the item
         * @param checked
         *      whether the item is checked or not
         * @param grayed
         *      whether the item is grayed or not
         */
        private void checkPath( TreeItem item, boolean checked, boolean grayed )
        {
            if ( item == null )
                return;
            if ( grayed )
            {
                checked = true;
            }
            else
            {
                int index = 0;
                TreeItem[] items = item.getItems();
                while ( index < items.length )
                {
                    TreeItem child = items[index];
                    if ( child.getGrayed() || checked != child.getChecked() )
                    {
                        checked = grayed = true;
                        break;
                    }
                    index++;
                }
            }
            item.setChecked( checked );
            item.setGrayed( grayed );
            checkPath( item.getParentItem(), checked, grayed );
        }


        /**
         * Checks the item and the children items.
         *
         * @param item
         *      the item
         * @param checked
         *      whether the item is checked or not
         */
        private void checkItems( TreeItem item, boolean checked )
        {
            item.setGrayed( false );
            item.setChecked( checked );
            TreeItem[] items = item.getItems();
            for ( int i = 0; i < items.length; i++ )
            {
                checkItems( items[i], checked );
            }
        }
    };

    /** The selection change listener for the templates viewer */
    private ISelectionChangedListener templatesViewerSelectionChangedListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            updateButtonsStates();
        }
    };


    /**
     * Creates a new instance of TemplateEntryEditorPreferencePage.
     */
    public TemplateEntryEditorPreferencePage()
    {
        super();
        super.setPreferenceStore( EntryTemplatePlugin.getDefault().getPreferenceStore() );
        super.setDescription( Messages.getString( "TemplateEntryEditorPreferencePage.PrefPageDescription" ) ); //$NON-NLS-1$

        store = EntryTemplatePlugin.getDefault().getPreferenceStore();
        manager = new PreferencesTemplatesManager( EntryTemplatePlugin.getDefault().getTemplatesManager() );
    }


    /**
     * {@inheritDoc}
     */
    protected Control createContents( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayout( new GridLayout() );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        createUI( composite );
        initListeners();
        initUI();

        return composite;
    }


    /**
     * Creates the user interface.
     *
     * @param parent
     *      the parent composite
     */
    private void createUI( Composite parent )
    {
        // Main Composite
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        // Templates Group
        createTemplatesGroup( composite );

        // Use Template Editor group
        createUseTemplateEditorGroup( composite );
    }


    /**
     * Creates the templates group.
     *
     * @param composite
     *      the parent composite
     */
    private void createTemplatesGroup( Composite parent )
    {
        // Templates Group
        Group templatesGroup = BaseWidgetUtils.createGroup( parent, Messages
            .getString( "TemplateEntryEditorPreferencePage.Templates" ), 1 ); //$NON-NLS-1$
        templatesGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        templatesGroup.setLayout( new GridLayout( 2, false ) );

        // ToolBar
        createToolbar( templatesGroup );

        // ToolBar Filler (to fill the right part of the row)
        new Label( templatesGroup, SWT.NONE );

        // Templates Viewer Composite
        createTemplatesViewerComposite( templatesGroup );

        // Buttons
        createTemplatesTableButtons( templatesGroup );
    }


    /**
     * Creates the templates viewer's composite.
     *
     * @param composite
     *      the parent composite
     */
    private void createTemplatesViewerComposite( Composite parent )
    {
        templatesViewerComposite = new Composite( parent, SWT.NONE );
        GridLayout gl = new GridLayout();
        gl.horizontalSpacing = 0;
        gl.verticalSpacing = 0;
        gl.marginHeight = 0;
        gl.marginWidth = 0;
        templatesViewerComposite.setLayout( gl );
        templatesViewerComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 1, 5 ) );

        templatesViewerComposite.addControlListener( new ControlAdapter()
        {
            public void controlResized( ControlEvent e )
            {
                // Resizing columns when the preference window (hence the composite) is resized
                resizeColumsToFit();
            }
        } );
    }


    /**
     * Creates the toolbar.
     * 
     * @param composite
     *      the parent composite
     */
    private void createToolbar( Composite composite )
    {
        Composite toolbarComposite = BaseWidgetUtils.createColumnContainer( composite, 2, 1 );
        toolbarComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        Label toolbarLabel = BaseWidgetUtils.createLabel( toolbarComposite, Messages
            .getString( "TemplateEntryEditorPreferencePage.Presentation" ), 1 ); //$NON-NLS-1$
        toolbarLabel.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, true, false ) );

        // ToolBar
        ToolBar toolbar = new ToolBar( toolbarComposite, SWT.HORIZONTAL | SWT.FLAT );
        toolbar.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false ) );

        // Hierarchical object class oriented presentation toolitem
        objectClassPresentationToolItem = new ToolItem( toolbar, SWT.RADIO );
        objectClassPresentationToolItem.setImage( EntryTemplatePlugin.getDefault().getImage(
            EntryTemplatePluginConstants.IMG_OBJECT_CLASS ) );
        objectClassPresentationToolItem.setToolTipText( Messages
            .getString( "TemplateEntryEditorPreferencePage.HierarchicalObjectClassOrientedPresentation" ) ); //$NON-NLS-1$
        objectClassPresentationToolItem.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                if ( store.getInt( EntryTemplatePluginConstants.PREF_TEMPLATES_PRESENTATION ) != EntryTemplatePluginConstants.PREF_TEMPLATES_PRESENTATION_OBJECT_CLASS )
                {
                    objectClassPresentationToolItemSelected();
                }
            }
        } );

        // Flat template oriented presentation toolitem
        templatePresentationToolItem = new ToolItem( toolbar, SWT.RADIO );
        templatePresentationToolItem.setImage( EntryTemplatePlugin.getDefault().getImage(
            EntryTemplatePluginConstants.IMG_TEMPLATE ) );
        templatePresentationToolItem.setToolTipText( Messages
            .getString( "TemplateEntryEditorPreferencePage.FlatTemplateOrientedPresentation" ) ); //$NON-NLS-1$
        templatePresentationToolItem.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                if ( store.getInt( EntryTemplatePluginConstants.PREF_TEMPLATES_PRESENTATION ) != EntryTemplatePluginConstants.PREF_TEMPLATES_PRESENTATION_TEMPLATE )
                {
                    templatePresentationToolItemSelected();
                }
            }
        } );
    }


    /**
     * This method is called when the flat template oriented presentation
     * toolitem is selected.
     */
    private void templatePresentationToolItemSelected()
    {
        // Saving the setting in the preferences
        store.setValue( EntryTemplatePluginConstants.PREF_TEMPLATES_PRESENTATION,
            EntryTemplatePluginConstants.PREF_TEMPLATES_PRESENTATION_TEMPLATE );

        // Removing listeners
        removeTemplatesViewerListeners();

        // Disposing the old templates viewer
        if ( ( templatesViewer != null ) && ( !templatesViewer.getTree().isDisposed() ) )
        {
            templatesViewer.getTree().dispose();
            templatesViewer = null;
        }

        // Creating a new one
        createTemplatesViewer();

        Tree templatesTree = templatesViewer.getTree();

        // Title column
        TreeColumn titleColumn = new TreeColumn( templatesTree, SWT.SINGLE );
        titleColumn.setText( Messages.getString( "TemplateEntryEditorPreferencePage.Title" ) ); //$NON-NLS-1$

        // Object classes column
        TreeColumn objectClassesColumn = new TreeColumn( templatesTree, SWT.SINGLE );
        objectClassesColumn.setText( Messages.getString( "TemplateEntryEditorPreferencePage.ObjectClasses" ) ); //$NON-NLS-1$

        // Setting the default sort column
        templatesTree.setSortColumn( titleColumn );
        templatesTree.setSortDirection( SWT.UP );

        // Setting the columns so they can be sorted
        ColumnViewerSortColumnUtils.addSortColumn( templatesViewer, titleColumn );
        ColumnViewerSortColumnUtils.addSortColumn( templatesViewer, objectClassesColumn );

        // Showing the columns header
        templatesTree.setHeaderVisible( true );

        // Settings the templates to the templates viewer
        templatesViewer.setInput( TEMPLATES_VIEWER_ROOT );

        // Adding listeners
        addTemplatesViewerListeners();

        // Updating the parent composite
        templatesViewerComposite.layout();

        // Setting the state for checked and grayed elements
        setStateForCheckedAndGrayedElements();

        // Resizing columns
        resizeColumsToFit();

        // Hiding the 'Set Default' button 
        setDefaultTemplateButton.setVisible( false );
    }


    /**
     * This method is called when the hierarchical object class oriented 
     * presentation toolitem is selected.
     */
    private void objectClassPresentationToolItemSelected()
    {
        // Saving the setting in the preferences
        store.setValue( EntryTemplatePluginConstants.PREF_TEMPLATES_PRESENTATION,
            EntryTemplatePluginConstants.PREF_TEMPLATES_PRESENTATION_OBJECT_CLASS );

        // Removing listeners
        removeTemplatesViewerListeners();

        // Disposing the old templates viewer
        if ( ( templatesViewer != null ) && ( !templatesViewer.getTree().isDisposed() ) )
        {
            templatesViewer.getTree().dispose();
            templatesViewer = null;
        }

        // Creating a new one
        createTemplatesViewer();

        Tree templatesTree = templatesViewer.getTree();

        // Title column
        TreeColumn titleColumn = new TreeColumn( templatesTree, SWT.SINGLE );

        // Setting the columns so they can be sorted
        ColumnViewerSortColumnUtils.addSortColumn( templatesViewer, titleColumn );

        // Hiding the columns header
        templatesTree.setHeaderVisible( false );

        // Settings the templates to the templates viewer
        templatesViewer.setInput( TEMPLATES_VIEWER_ROOT );

        // Adding listeners
        addTemplatesViewerListeners();

        // Updating the parent composite
        templatesViewerComposite.layout();

        // Setting the state for checked and grayed elements
        setStateForCheckedAndGrayedElements();

        // Resizing columns
        resizeColumsToFit();

        // Showing the 'Set Default' button 
        setDefaultTemplateButton.setVisible( true );
        setDefaultTemplateButton.setEnabled( false );
    }


    /**
     * Creates the templates viewer.
     */
    private void createTemplatesViewer()
    {
        // Templates tree
        Tree templatesTree = new Tree( templatesViewerComposite, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION
            | SWT.CHECK );
        templatesTree.setLinesVisible( false );

        // Templates table viewer
        templatesViewer = new CheckboxTreeViewer( templatesTree );
        GridData gridData2 = new GridData( SWT.FILL, SWT.NONE, true, false );
        gridData2.heightHint = 160;
        templatesViewer.getTree().setLayoutData( gridData2 );

        // Templates content and label providers, and comparator
        TemplatesContentProvider contentProvider = new TemplatesContentProvider( this, manager );
        templatesViewer.setContentProvider( contentProvider );
        TemplatesCheckStateListener checkStateProviderListener = new TemplatesCheckStateListener( contentProvider,
            manager );
        templatesViewer.addCheckStateListener( checkStateProviderListener );
        TemplatesLabelProvider labelProvider = new TemplatesLabelProvider( manager );
        templatesViewer.setLabelProvider( labelProvider );
        templatesViewer.setComparator( new ColumnsTableViewerComparator( labelProvider ) );
        templatesViewer.addDoubleClickListener( new IDoubleClickListener()
        {
            @SuppressWarnings("unchecked")
            public void doubleClick( DoubleClickEvent event )
            {
                StructuredSelection selection = ( StructuredSelection ) templatesViewer.getSelection();
                if ( !selection.isEmpty() )
                {
                    Iterator<Object> selectionIterator = selection.iterator();
                    while ( selectionIterator.hasNext() )
                    {
                        Object selectedElement = ( Object ) selectionIterator.next();
                        if ( templatesViewer.getExpandedState( selectedElement ) )
                        {

                            templatesViewer.collapseToLevel( selectedElement, 1 );
                        }
                        else
                        {
                            templatesViewer.expandToLevel( selectedElement, 1 );
                        }
                    }
                }
            }
        } );
    }


    /**
     * Creates the buttons associated with the templates table.
     * 
     * @param composite
     *      the parent composite
     */
    private void createTemplatesTableButtons( Group composite )
    {
        importTemplatesButton = BaseWidgetUtils.createButton( composite, Messages
            .getString( "TemplateEntryEditorPreferencePage.Import" ), 1 ); //$NON-NLS-1$
        exportTemplatesButton = BaseWidgetUtils.createButton( composite, Messages
            .getString( "TemplateEntryEditorPreferencePage.Export" ), 1 ); //$NON-NLS-1$
        removeTemplateButton = BaseWidgetUtils.createButton( composite, Messages
            .getString( "TemplateEntryEditorPreferencePage.Remove" ), 1 ); //$NON-NLS-1$
        setDefaultTemplateButton = BaseWidgetUtils.createButton( composite, Messages
            .getString( "TemplateEntryEditorPreferencePage.SetDefault" ), 1 ); //$NON-NLS-1$
    }


    /**
     * Creates the Use Template Editor group.
     *
     * @param composite
     *      the parent composite
     */
    private void createUseTemplateEditorGroup( Composite composite )
    {
        // Use Template Editor Group
        Group editorActivationGroup = BaseWidgetUtils.createGroup( composite, Messages
            .getString( "TemplateEntryEditorPreferencePage.UseTheTemplateEntryEditor" ), 1 ); //$NON-NLS-1$
        editorActivationGroup.setLayout( new GridLayout() );
        editorActivationGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // With For Entry Button
        useForAnyEntryButton = BaseWidgetUtils.createRadiobutton( editorActivationGroup, Messages
            .getString( "TemplateEntryEditorPreferencePage.ForAnyEntry" ), 1 ); //$NON-NLS-1$
        useForAnyEntryButton.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // For Only Entries With Template Button
        useForOnlyEntriesWithTemplateButton = BaseWidgetUtils.createRadiobutton( editorActivationGroup, Messages
            .getString( "TemplateEntryEditorPreferencePage.OnlyForEntriesMatchingAtLeastOneEnabledTemplate" ), 1 ); //$NON-NLS-1$
        useForOnlyEntriesWithTemplateButton.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }


    /**
     * Initializes the listeners
     */
    private void initListeners()
    {
        importTemplatesButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                importTemplatesAction();
            }
        } );

        exportTemplatesButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                exportTemplatesAction();
            }
        } );

        removeTemplateButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                removeTemplateAction();
            }
        } );

        setDefaultTemplateButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                setDefaultTemplateAction();
            }
        } );

        useForAnyEntryButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                useForAnyEntryAction();
            }
        } );

        useForOnlyEntriesWithTemplateButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                useForOnlyEntriesWithTemplateAction();
            }
        } );
    }


    /**
     * Adds the listeners to the templates viewer.
     */
    private void addTemplatesViewerListeners()
    {
        if ( ( templatesViewer != null ) && ( !templatesViewer.getTree().isDisposed() ) )
        {
            templatesViewer.getTree().addListener( SWT.Selection, templatesViewerSelectionListener );
            templatesViewer.addSelectionChangedListener( templatesViewerSelectionChangedListener );
        }
    }


    /**
     * Removes the listeners to the templates viewer.
     */
    private void removeTemplatesViewerListeners()
    {
        if ( ( templatesViewer != null ) && ( !templatesViewer.getTree().isDisposed() ) )
        {
            templatesViewer.getTree().removeListener( SWT.Selection, templatesViewerSelectionListener );
            templatesViewer.removeSelectionChangedListener( templatesViewerSelectionChangedListener );
        }
    }


    /**
     * Updates the states of the buttons.
     */
    @SuppressWarnings("unchecked")
    private void updateButtonsStates()
    {
        StructuredSelection selection = ( StructuredSelection ) templatesViewer.getSelection();
        if ( !selection.isEmpty() )
        {
            if ( selection.size() == 1 )
            {
                // Only one row is selected

                Object selectedObject = selection.getFirstElement();
                removeTemplateButton.setEnabled( ( selectedObject instanceof FileTemplate )
                    || ( selectedObject instanceof PreferencesFileTemplate ) );

                // If we're in the object class presentation, we need to update the 'Set Default' button
                if ( store.getInt( EntryTemplatePluginConstants.PREF_TEMPLATES_PRESENTATION ) == EntryTemplatePluginConstants.PREF_TEMPLATES_PRESENTATION_OBJECT_CLASS )
                {
                    if ( selectedObject instanceof Template )
                    {
                        Template selectedTemplate = ( Template ) selectedObject;
                        setDefaultTemplateButton.setEnabled( ( manager.isEnabled( selectedTemplate ) && ( !manager
                            .isDefaultTemplate( selectedTemplate ) ) ) );
                    }
                    else
                    {
                        setDefaultTemplateButton.setEnabled( false );
                    }
                }
            }
            else
            {
                // More than one row is selected

                removeTemplateButton.setEnabled( true );
                Iterator<Object> selectionIterator = ( ( StructuredSelection ) templatesViewer.getSelection() )
                    .iterator();
                while ( selectionIterator.hasNext() )
                {
                    Object selectedObject = selectionIterator.next();
                    if ( !( ( selectedObject instanceof FileTemplate ) || ( selectedObject instanceof PreferencesFileTemplate ) ) )
                    {
                        removeTemplateButton.setEnabled( false );
                        break;
                    }
                }

                // If we're in the object class presentation, we need to update the 'Set Default' button
                if ( store.getInt( EntryTemplatePluginConstants.PREF_TEMPLATES_PRESENTATION ) == EntryTemplatePluginConstants.PREF_TEMPLATES_PRESENTATION_OBJECT_CLASS )
                {
                    setDefaultTemplateButton.setEnabled( false );
                }
            }

        }
        else
        {
            removeTemplateButton.setEnabled( false );
            setDefaultTemplateButton.setEnabled( false );
        }
    }


    /**
     * Implements the import templates action.
     */
    private void importTemplatesAction()
    {
        WizardDialog dialog = new WizardDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            new ImportTemplatesWizard( manager ) );
        dialog.create();
        dialog.open();
    }


    /**
     * Implements the export templates action.
     */
    @SuppressWarnings("unchecked")
    private void exportTemplatesAction()
    {
        // Creating the Export Templates wizard
        ExportTemplatesWizard wizard = new ExportTemplatesWizard();

        // Collecting the selected objects
        List<Object> selectedObjects = new ArrayList<Object>();
        Iterator<Object> selectionIterator = ( ( StructuredSelection ) templatesViewer.getSelection() ).iterator();
        while ( selectionIterator.hasNext() )
        {
            selectedObjects.add( selectionIterator.next() );
        }

        // Assigning these objects to the wizard
        wizard.setPreCheckedObjects( selectedObjects.toArray() );

        // Opening the wizard
        WizardDialog dialog = new WizardDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard );
        dialog.create();
        dialog.open();
    }


    /**
     * Implements the remove template action.
     */
    @SuppressWarnings("unchecked")
    private void removeTemplateAction()
    {
        StructuredSelection selection = ( StructuredSelection ) templatesViewer.getSelection();
        if ( !selection.isEmpty() )
        {
            Iterator<Object> selectionIterator = ( ( StructuredSelection ) templatesViewer.getSelection() ).iterator();
            while ( selectionIterator.hasNext() )
            {
                Object selectedObject = selectionIterator.next();
                if ( selectedObject instanceof Template )
                {
                    Template template = ( Template ) selectedObject;

                    if ( !manager.removeTemplate( template ) )
                    {
                        // Creating and opening the dialog
                        String dialogTitle = Messages
                            .getString( "TemplateEntryEditorPreferencePage.UnableToRemoveTheTemplate" ); //$NON-NLS-1$
                        String dialogMessage = MessageFormat
                            .format(
                                Messages.getString( "TemplateEntryEditorPreferencePage.TheTemplateCouldNotBeRemoved" ) //$NON-NLS-1$
                                    + EntryTemplatePluginUtils.LINE_SEPARATOR
                                    + EntryTemplatePluginUtils.LINE_SEPARATOR
                                    + Messages
                                        .getString( "TemplateEntryEditorPreferencePage.SeeTheLogsFileForMoreInformation" ), template.getTitle() ); //$NON-NLS-1$
                        MessageDialog dialog = new MessageDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                            .getShell(), dialogTitle, null, dialogMessage, MessageDialog.ERROR, new String[]
                            { IDialogConstants.OK_LABEL }, MessageDialog.OK );
                        dialog.open();
                    }
                }
            }
        }
    }


    /**
     * Implements the set default template action.
     */
    private void setDefaultTemplateAction()
    {
        StructuredSelection selection = ( StructuredSelection ) templatesViewer.getSelection();
        if ( !selection.isEmpty() )
        {
            Object selectedObject = selection.getFirstElement();
            if ( selectedObject instanceof Template )
            {
                manager.setDefaultTemplate( ( Template ) selectedObject );
                templatesViewer.refresh();
                updateButtonsStates();
            }
        }
    }


    /**
     * Implements the use for any entry action.
     */
    private void useForAnyEntryAction()
    {
        useForAnyEntryButton.setSelection( true );
        useForOnlyEntriesWithTemplateButton.setSelection( false );
    }


    /**
     * Implements the use for only entries with template action.
     */
    private void useForOnlyEntriesWithTemplateAction()
    {
        useForAnyEntryButton.setSelection( false );
        useForOnlyEntriesWithTemplateButton.setSelection( true );
    }


    /**
     * Initializes the User Interface.
     */
    private void initUI()
    {
        // Setting the presentation of the templates viewer.
        int templatesPresentation = store.getInt( EntryTemplatePluginConstants.PREF_TEMPLATES_PRESENTATION );
        if ( templatesPresentation == EntryTemplatePluginConstants.PREF_TEMPLATES_PRESENTATION_TEMPLATE )
        {
            templatePresentationToolItem.setSelection( true );
            templatePresentationToolItemSelected();
        }
        else if ( templatesPresentation == EntryTemplatePluginConstants.PREF_TEMPLATES_PRESENTATION_OBJECT_CLASS )
        {
            objectClassPresentationToolItem.setSelection( true );
            objectClassPresentationToolItemSelected();
        }

        // Disabling the 'Remove Template' button
        removeTemplateButton.setEnabled( false );

        // Selecting the 'Use Template Editor' mode
        int useTemplateEditorFor = store.getInt( EntryTemplatePluginConstants.PREF_USE_TEMPLATE_EDITOR_FOR );
        if ( useTemplateEditorFor == EntryTemplatePluginConstants.PREF_USE_TEMPLATE_EDITOR_FOR_ANY_ENTRY )
        {
            useForAnyEntryButton.setSelection( true );
            useForOnlyEntriesWithTemplateButton.setSelection( false );
        }
        else if ( useTemplateEditorFor == EntryTemplatePluginConstants.PREF_USE_TEMPLATE_EDITOR_FOR_ENTRIES_WITH_TEMPLATE )
        {
            useForAnyEntryButton.setSelection( false );
            useForOnlyEntriesWithTemplateButton.setSelection( true );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbench workbench )
    {
        // Nothing to do
    }


    /**
     * Refreshes the templates viewer.
     */
    public void refreshViewer()
    {
        // Refreshing the viewer
        templatesViewer.refresh();

        // Setting the state for checked and grayed elements
        setStateForCheckedAndGrayedElements();

        // Resizing the columns to fit.
        resizeColumsToFit();
    }


    /**
     * Resizes the columns to fit the size of the cells
     */
    private void resizeColumsToFit()
    {
        // Getting the tree and number of columns in the tree
        Tree tree = templatesViewer.getTree();
        int columnCount = tree.getColumnCount();

        // Computing the available width for the tree
        // The checkbox column width needs to be removed
        int width = templatesViewerComposite.getClientArea().width - 21; // 21 pixels need to be removed on Mac OS X

        // Subtracting the vertical scrollbar width from the total column width
        width -= tree.getVerticalBar().getSize().x;

        // Resizing equally each columns
        for ( int i = 0; i < columnCount; i++ )
        {
            tree.getColumn( i ).setWidth( width / columnCount );
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean performOk()
    {
        if ( useForAnyEntryButton.getSelection() )
        {
            store.setValue( EntryTemplatePluginConstants.PREF_USE_TEMPLATE_EDITOR_FOR,
                EntryTemplatePluginConstants.PREF_USE_TEMPLATE_EDITOR_FOR_ANY_ENTRY );
        }

        if ( useForOnlyEntriesWithTemplateButton.getSelection() )
        {
            store.setValue( EntryTemplatePluginConstants.PREF_USE_TEMPLATE_EDITOR_FOR,
                EntryTemplatePluginConstants.PREF_USE_TEMPLATE_EDITOR_FOR_ENTRIES_WITH_TEMPLATE );
        }

        return manager.saveModifications();
    }


    /**
     * Sets the state for checked and grayed elements 
     * (whether checked, grayed or not checked at all).
     */
    private void setStateForCheckedAndGrayedElements()
    {
        // Backing up expanded elements
        Object[] expandedElements = templatesViewer.getExpandedElements();

        // Forcing the loading of all the nodes
        templatesViewer.expandAll();
        templatesViewer.collapseAll();

        // Creating lists for checked and grayed elements
        List<Object> checkedElements = new ArrayList<Object>();
        List<Object> grayedElements = new ArrayList<Object>();

        // Filling the list
        fillCheckedElementsAndGrayedElementsLists( checkedElements, grayedElements );

        // Assigning checked and grayed elements
        templatesViewer.setCheckedElements( checkedElements.toArray() );
        templatesViewer.setGrayedElements( grayedElements.toArray() );

        // Restoring expanded elements
        templatesViewer.setExpandedElements( expandedElements );
    }


    /**
     * Fills the two given lists with checked and grayed elements.
     *
     * @param checkedElements
     *      the checked elements list
     * @param grayedElements
     *      the grayed elements list
     */
    private void fillCheckedElementsAndGrayedElementsLists( List<Object> checkedElements, List<Object> grayedElements )
    {
        // Getting the content provider
        TemplatesContentProvider contentProvider = ( TemplatesContentProvider ) templatesViewer.getContentProvider();

        // Creating a list we'll use to go through the tree elements
        List<Object> elements = new ArrayList<Object>();

        // Adding to this list the base elements returned by the content provider
        elements.addAll( Arrays.asList( contentProvider.getElements( TEMPLATES_VIEWER_ROOT ) ) );

        while ( !elements.isEmpty() )
        {
            // Getting the firs object of the list
            Object element = elements.get( 0 );

            // Is the element checked?
            if ( isChecked( contentProvider, element ) )
            {
                checkedElements.add( element );
            }

            // Is the element grayed?
            if ( isGrayed( contentProvider, element ) )
            {
                grayedElements.add( element );
            }

            // Adding the children of the element in the elements list
            elements.addAll( Arrays.asList( contentProvider.getChildren( element ) ) );

            // Removing the element from the elements list
            elements.remove( element );
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean isChecked( TemplatesContentProvider contentProvider, Object element )
    {
        // Object class presentation
        if ( contentProvider.isObjectClassPresentation() )
        {
            if ( element instanceof Template )
            {
                // Returning the enabled state of the template
                return manager.isEnabled( ( Template ) element );
            }
            else if ( element instanceof ObjectClass )
            {
                // This string is the object class name

                // Getting the children of the node
                Object[] children = contentProvider.getChildren( element );
                if ( children != null )
                {
                    for ( Object child : children )
                    {
                        Template template = ( Template ) child;
                        if ( manager.isEnabled( template ) )
                        {
                            // We return true as soon as we find one enabled template.
                            // The grayed state (indicating that not all children of the node are
                            // checked will be determined by the separate isGrayed(...) method).
                            return true;
                        }
                    }
                }

                return false;
            }
        }
        // Template presentation
        else if ( contentProvider.isTemplatePresentation() )
        {
            // Returning the enabled state of the template
            return manager.isEnabled( ( Template ) element );
        }

        return false;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isGrayed( TemplatesContentProvider contentProvider, Object element )
    {
        // Object class presentation
        if ( contentProvider.isObjectClassPresentation() )
        {
            if ( element instanceof Template )
            {
                // Returning false for template element as they are only children
                return false;
            }
            else if ( element instanceof ObjectClass )
            {
                // This string is the object class name

                // Getting the children of the node
                Object[] children = contentProvider.getChildren( element );
                if ( children != null )
                {
                    for ( Object child : children )
                    {
                        Template template = ( Template ) child;
                        if ( !manager.isEnabled( template ) )
                        {
                            // We return true as soon as we find one disabled template.
                            return true;
                        }
                    }
                }

                return false;
            }
        }
        // Template presentation
        else if ( contentProvider.isTemplatePresentation() )
        {
            // Returning false for Template presentation
            return false;
        }

        return false;
    }
}