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
package org.apache.directory.studio.templateeditor.editor;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.directory.studio.entryeditors.EntryEditorInput;
import org.apache.directory.studio.entryeditors.IEntryEditor;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import org.apache.directory.studio.templateeditor.EntryTemplatePlugin;
import org.apache.directory.studio.templateeditor.EntryTemplatePluginUtils;
import org.apache.directory.studio.templateeditor.actions.DisplayEntryInTemplateAction;
import org.apache.directory.studio.templateeditor.actions.DisplayEntryInTemplateMenuManager;
import org.apache.directory.studio.templateeditor.actions.EditorPagePropertiesAction;
import org.apache.directory.studio.templateeditor.actions.RefreshAction;
import org.apache.directory.studio.templateeditor.actions.SimpleActionProxy;
import org.apache.directory.studio.templateeditor.editor.widgets.EditorCheckbox;
import org.apache.directory.studio.templateeditor.editor.widgets.EditorComposite;
import org.apache.directory.studio.templateeditor.editor.widgets.EditorDate;
import org.apache.directory.studio.templateeditor.editor.widgets.EditorFileChooser;
import org.apache.directory.studio.templateeditor.editor.widgets.EditorImage;
import org.apache.directory.studio.templateeditor.editor.widgets.EditorLabel;
import org.apache.directory.studio.templateeditor.editor.widgets.EditorLink;
import org.apache.directory.studio.templateeditor.editor.widgets.EditorListbox;
import org.apache.directory.studio.templateeditor.editor.widgets.EditorPassword;
import org.apache.directory.studio.templateeditor.editor.widgets.EditorRadioButtons;
import org.apache.directory.studio.templateeditor.editor.widgets.EditorSection;
import org.apache.directory.studio.templateeditor.editor.widgets.EditorSpinner;
import org.apache.directory.studio.templateeditor.editor.widgets.EditorTable;
import org.apache.directory.studio.templateeditor.editor.widgets.EditorTextField;
import org.apache.directory.studio.templateeditor.editor.widgets.EditorWidget;
import org.apache.directory.studio.templateeditor.model.Template;
import org.apache.directory.studio.templateeditor.model.widgets.TemplateCheckbox;
import org.apache.directory.studio.templateeditor.model.widgets.TemplateComposite;
import org.apache.directory.studio.templateeditor.model.widgets.TemplateDate;
import org.apache.directory.studio.templateeditor.model.widgets.TemplateFileChooser;
import org.apache.directory.studio.templateeditor.model.widgets.TemplateForm;
import org.apache.directory.studio.templateeditor.model.widgets.TemplateImage;
import org.apache.directory.studio.templateeditor.model.widgets.TemplateLabel;
import org.apache.directory.studio.templateeditor.model.widgets.TemplateLink;
import org.apache.directory.studio.templateeditor.model.widgets.TemplateListbox;
import org.apache.directory.studio.templateeditor.model.widgets.TemplatePassword;
import org.apache.directory.studio.templateeditor.model.widgets.TemplateRadioButtons;
import org.apache.directory.studio.templateeditor.model.widgets.TemplateSection;
import org.apache.directory.studio.templateeditor.model.widgets.TemplateSpinner;
import org.apache.directory.studio.templateeditor.model.widgets.TemplateTable;
import org.apache.directory.studio.templateeditor.model.widgets.TemplateTextField;
import org.apache.directory.studio.templateeditor.model.widgets.TemplateWidget;


/**
 * This class implements a widget for the Template Editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class TemplateEditorWidget
{
    /** The associated editor */
    private IEntryEditor editor;

    /** The flag to know whether or not the widget has been initialized */
    private boolean initialized = false;

    /** The parent {@link Composite} of the widget */
    private Composite parent;

    /** The associated {@link FormToolkit} */
    private FormToolkit toolkit;

    /** The associated {@link ScrolledForm} */
    private ScrolledForm form;

    /** The currently selected template */
    private Template selectedTemplate;

    /** The context menu */
    private Menu contextMenu;

    /** The list of editor widgets */
    private Map<TemplateWidget, EditorWidget<? extends TemplateWidget>> editorWidgets = new HashMap<TemplateWidget, EditorWidget<? extends TemplateWidget>>();


    /**
     * Creates a new instance of TemplateEditorWidget.
     *
     * @param editor
     *      the editor
     */
    public TemplateEditorWidget( IEntryEditor editor )
    {
        this.editor = editor;
    }


    /**
     * {@inheritDoc}
     */
    public void init( Composite parent )
    {
        initialized = true;
        this.parent = parent;

        // Creating the toolkit
        toolkit = new FormToolkit( parent.getDisplay() );

        // Creating the new form
        form = toolkit.createScrolledForm( parent );
        form.getBody().setLayout( new GridLayout() );

        form.getToolBarManager().add( new RefreshAction( getEditor() ) );
        form.getToolBarManager().add( new Separator() );
        form.getToolBarManager().add( new DisplayEntryInTemplateAction( this ) );
        form.getToolBarManager().update( true );

        // Creating the new menu manager
        MenuManager menuManager = new MenuManager();
        contextMenu = menuManager.createContextMenu( form );
        form.setMenu( contextMenu );

        // Adding actions to the menu manager
        menuManager.add( new DisplayEntryInTemplateMenuManager( this ) );
        menuManager.add( new Separator() );
        menuManager.add( new RefreshAction( getEditor() ) );
        menuManager.add( new Separator() );
        menuManager.add( new SimpleActionProxy( new EditorPagePropertiesAction( getEditor() ) ) );

        createFormContent();

        parent.layout();
    }


    /**
     * Creates the from content
     */
    private void createFormContent()
    {
        EntryEditorInput entryEditorInput = getEditor().getEntryEditorInput();

        // Checking if the input is null
        if ( entryEditorInput == null )
        {
            createFormContentUnableToDisplayTheEntry();
        }
        else
        {
            // Getting the entry and the template
            IEntry entry = entryEditorInput.getSharedWorkingCopy( getEditor() );

            // Special case in the case the entry is null
            if ( entry == null )
            {
                // Hiding the context menu
                form.setMenu( null );

                // Creating the form content
                createFormContentNoEntrySelected();
            }
            else
            {
                // Showing the context menu
                form.setMenu( contextMenu );

                // Checking if a template is selected
                if ( selectedTemplate == null )
                {
                    List<Template> matchingTemplates = EntryTemplatePluginUtils.getMatchingTemplates( entry );
                    if ( ( matchingTemplates != null ) && ( matchingTemplates.size() > 0 ) )
                    {
                        // Looking for the default template
                        for ( Template matchingTemplate : matchingTemplates )
                        {
                            if ( EntryTemplatePlugin.getDefault().getTemplatesManager().isDefaultTemplate(
                                matchingTemplate ) )
                            {
                                selectedTemplate = matchingTemplate;
                                break;
                            }
                        }

                        // If no default template has been found,
                        // select the first one
                        if ( selectedTemplate == null )
                        {
                            // Assigning the first template as the selected one
                            selectedTemplate = matchingTemplates.get( 0 );
                        }

                        // Creating the form content
                        createFormContentFromTemplate();
                    }
                    else
                    {
                        // Creating the form content
                        createFormContentNoTemplateMatching();
                    }
                }
                else
                {
                    // Creating the form content
                    createFormContentFromTemplate();
                }
            }
        }

        form.layout( true, true );
    }


    /**
     * Gets the associated editor.
     *
     * @return
     */
    public IEntryEditor getEditor()
    {
        return editor;
    }


    /**
     * Creates the form UI in case where the entry cannot be displayed.
     */
    private void createFormContentUnableToDisplayTheEntry()
    {
        // Displaying an error message
        form.setText( Messages.getString( "TemplateEditorWidget.UnableToDisplayTheEntry" ) ); //$NON-NLS-1$
        form.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
    }


    /**
     * Creates the form UI from the template.
     * 
     * @param managedForm
     *            the form
     */
    private void createFormContentFromTemplate()
    {
        form.setText( selectedTemplate.getTitle() );

        // Getting the template form
        TemplateForm templateForm = selectedTemplate.getForm();

        // Creating the children widgets
        if ( templateForm.hasChildren() )
        {
            for ( TemplateWidget templateWidget : templateForm.getChildren() )
            {
                createFormTemplateWidget( form.getBody(), templateWidget );
            }
        }
    }


    /**
     * Creates the editor widget associated with the {@link TemplateWidget} object .
     * 
     * @param parent
     *      the parent composite
     * @param templateWidget
     *      the template widget
     */
    private void createFormTemplateWidget( Composite parent, TemplateWidget templateWidget )
    {
        // The widget composite
        Composite widgetComposite = null;

        // Creating the widget according to its type
        if ( templateWidget instanceof TemplateCheckbox )
        {
            // Creating the editor checkbox
            EditorCheckbox editorCheckbox = new EditorCheckbox( getEditor(), ( TemplateCheckbox ) templateWidget,
                getToolkit() );
            editorWidgets.put( templateWidget, editorCheckbox );

            // Creating the UI
            widgetComposite = editorCheckbox.createWidget( parent );
        }
        else if ( templateWidget instanceof TemplateComposite )
        {
            // Creating the editor composite
            EditorComposite editorComposite = new EditorComposite( getEditor(), ( TemplateComposite ) templateWidget,
                getToolkit() );
            editorWidgets.put( templateWidget, editorComposite );

            // Creating the UI
            widgetComposite = editorComposite.createWidget( parent );
        }
        else if ( templateWidget instanceof TemplateDate )
        {
            // Creating the editor date
            EditorDate editorDate = new EditorDate( getEditor(), ( TemplateDate ) templateWidget, getToolkit() );
            editorWidgets.put( templateWidget, editorDate );

            // Creating the UI
            widgetComposite = editorDate.createWidget( parent );
        }
        else if ( templateWidget instanceof TemplateFileChooser )
        {
            // Creating the editor file chooser
            EditorFileChooser editorFileChooser = new EditorFileChooser( getEditor(),
                ( TemplateFileChooser ) templateWidget, getToolkit() );
            editorWidgets.put( templateWidget, editorFileChooser );

            // Creating the UI
            widgetComposite = editorFileChooser.createWidget( parent );
        }
        else if ( templateWidget instanceof TemplateImage )
        {
            // Creating the editor image
            EditorImage editorImage = new EditorImage( getEditor(), ( TemplateImage ) templateWidget, getToolkit() );
            editorWidgets.put( templateWidget, editorImage );

            // Creating the UI
            widgetComposite = editorImage.createWidget( parent );
        }
        else if ( templateWidget instanceof TemplateLabel )
        {
            // Creating the editor label
            EditorLabel editorLabel = new EditorLabel( getEditor(), ( TemplateLabel ) templateWidget, getToolkit() );
            editorWidgets.put( templateWidget, editorLabel );

            // Creating the UI
            widgetComposite = editorLabel.createWidget( parent );
        }
        else if ( templateWidget instanceof TemplateLink )
        {
            // Creating the editor link
            EditorLink editorLink = new EditorLink( getEditor(), ( TemplateLink ) templateWidget, getToolkit() );
            editorWidgets.put( templateWidget, editorLink );

            // Creating the UI
            widgetComposite = editorLink.createWidget( parent );
        }
        else if ( templateWidget instanceof TemplateListbox )
        {
            // Creating the editor link
            EditorListbox editorListbox = new EditorListbox( getEditor(), ( TemplateListbox ) templateWidget,
                getToolkit() );
            editorWidgets.put( templateWidget, editorListbox );

            // Creating the UI
            widgetComposite = editorListbox.createWidget( parent );
        }
        else if ( templateWidget instanceof TemplatePassword )
        {
            // Creating the editor password
            EditorPassword editorPassword = new EditorPassword( getEditor(), ( TemplatePassword ) templateWidget,
                getToolkit() );
            editorWidgets.put( templateWidget, editorPassword );

            // Creating the UI
            widgetComposite = editorPassword.createWidget( parent );
        }
        else if ( templateWidget instanceof TemplateRadioButtons )
        {
            // Creating the editor radio buttons
            EditorRadioButtons editorRadioButtons = new EditorRadioButtons( getEditor(),
                ( TemplateRadioButtons ) templateWidget, getToolkit() );
            editorWidgets.put( templateWidget, editorRadioButtons );

            // Creating the UI
            widgetComposite = editorRadioButtons.createWidget( parent );
        }
        else if ( templateWidget instanceof TemplateSection )
        {
            // Creating the editor section
            EditorSection editorSection = new EditorSection( getEditor(), ( TemplateSection ) templateWidget,
                getToolkit() );
            editorWidgets.put( templateWidget, editorSection );

            // Creating the UI
            widgetComposite = editorSection.createWidget( parent );
        }
        else if ( templateWidget instanceof TemplateSpinner )
        {
            // Creating the editor spinner
            EditorSpinner editorSpinner = new EditorSpinner( getEditor(), ( TemplateSpinner ) templateWidget,
                getToolkit() );
            editorWidgets.put( templateWidget, editorSpinner );

            // Creating the UI
            widgetComposite = editorSpinner.createWidget( parent );
        }
        else if ( templateWidget instanceof TemplateTable )
        {
            // Creating the editor table
            EditorTable editorTable = new EditorTable( getEditor(), ( TemplateTable ) templateWidget, getToolkit() );
            editorWidgets.put( templateWidget, editorTable );

            // Creating the UI
            widgetComposite = editorTable.createWidget( parent );
        }
        else if ( templateWidget instanceof TemplateTextField )
        {
            // Creating the editor text field
            EditorTextField editorTextField = new EditorTextField( getEditor(), ( TemplateTextField ) templateWidget,
                getToolkit() );
            editorWidgets.put( templateWidget, editorTextField );

            // Creating the UI
            widgetComposite = editorTextField.createWidget( parent );
        }

        // Recursively looping on children
        if ( templateWidget.hasChildren() )
        {
            for ( TemplateWidget templateWidgetChild : templateWidget.getChildren() )
            {
                createFormTemplateWidget( widgetComposite, templateWidgetChild );
            }
        }
    }


    /**
     * Creates the form UI in case where no entry is selected.
     */
    private void createFormContentNoEntrySelected()
    {
        // Displaying an error message
        form.setText( Messages.getString( "TemplateEditorWidget.NoEntrySelected" ) ); //$NON-NLS-1$
    }


    /**
     * Creates the form UI in case where no template is matching.
     */
    private void createFormContentNoTemplateMatching()
    {
        // Displaying an error message
        form.setText( Messages.getString( "TemplateEditorWidget.NoTemplateIsMatchingThisEntry" ) ); //$NON-NLS-1$
    }


    /**
     * Gets the {@link FormToolkit} associated with the editor page.
     *
     * @return
     *      the {@link FormToolkit} associated with the editor page
     */
    public FormToolkit getToolkit()
    {
        return toolkit;
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        //
        // Disposing the toolkit, form and widgets
        //

        // Toolkit
        if ( toolkit != null )
        {
            toolkit.dispose();
        }

        // Form
        if ( ( form != null ) && ( !form.isDisposed() ) )
        {
            form.dispose();
        }

        // Widgets
        for ( TemplateWidget key : editorWidgets.keySet() )
        {
            EditorWidget<?> widget = editorWidgets.get( key );
            widget.dispose();
        }
    }


    /**
     * {@inheritDoc}
     */
    public void update()
    {
        if ( isInitialized() )
        {
            // Updating widgets
            for ( TemplateWidget key : editorWidgets.keySet() )
            {
                EditorWidget<?> widget = editorWidgets.get( key );
                widget.update();
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void setFocus()
    {
        if ( ( form != null ) && ( !form.isDisposed() ) )
        {
            form.setFocus();
        }
    }


    /**
     * {@inheritDoc}
     */
    public void editorInputChanged()
    {
        if ( isInitialized() )
        {
            // Resetting the template
            selectedTemplate = null;

            // Updating the UI
            disposeAndRecreateUI();
        }
    }


    /**
     * Gets the {@link List} of templates matching the current entry.   
     *
     * @return
     *      the {@link List} of templates matching the current entry
     */
    public List<Template> getMatchingTemplates()
    {
        return EntryTemplatePluginUtils.getMatchingTemplates( getEditor().getEntryEditorInput().getSharedWorkingCopy(
            getEditor() ) );
    }


    /**
     * Gets the selected template.
     *
     * @return
     *      the selected template
     */
    public Template getSelectedTemplate()
    {
        return selectedTemplate;
    }


    /**
     * Displays the entry with the given template.
     *
     * @param selectedTemplate
     *      the selected template
     */
    public void switchTemplate( Template selectedTemplate )
    {
        // Assigning the selected template
        this.selectedTemplate = selectedTemplate;

        // Updating the UI
        disposeAndRecreateUI();
    }


    /**
     * Disposes and re-creates the UI (if the editor page has been initialized).
     */
    private void disposeAndRecreateUI()
    {
        if ( isInitialized() )
        {
            // Disposing the previously created form
            if ( ( form != null ) && ( !form.isDisposed() ) )
            {
                // Disposing the from (and all it's children elements
                form.dispose();

                // Disposing template widgets
                for ( TemplateWidget key : editorWidgets.keySet() )
                {
                    EditorWidget<?> widget = editorWidgets.get( key );
                    widget.dispose();
                }
            }

            // Clearing all previously created editor widgets (which are now disposed)
            editorWidgets.clear();

            // Recreating the UI
            init( parent );
        }
    }


    /**
     * Gets the associated {@link Form}.
     *
     * @return
     *      the associated {@link Form}
     */
    public ScrolledForm getForm()
    {
        return form;
    }


    /**
     * Indicated if the widget has been initialized.
     *
     * @return
     *      <code>true</code> if the widget has been initialized,
     *      <code>false</code> if not
     */
    public boolean isInitialized()
    {
        return initialized;
    }
}