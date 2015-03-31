package org.apache.directory.studio.combinededitor.editor;


import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;

import org.apache.directory.studio.templateeditor.EntryTemplatePlugin;
import org.apache.directory.studio.templateeditor.EntryTemplatePluginConstants;
import org.apache.directory.studio.templateeditor.editor.TemplateEditorWidget;
import org.apache.directory.studio.templateeditor.model.Template;


/**
 * This class implements an editor page for the Template Editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class TemplateEditorPage extends AbstractCombinedEntryEditorPage
{
    /** The template editor widget */
    private TemplateEditorWidget templateEditorWidget;


    /**
     * Creates a new instance of TemplateEditorPage.
     *
     * @param editor
     *      the associated editor
     */
    public TemplateEditorPage( CombinedEntryEditor editor )
    {
        super( editor );

        // Creating and assigning the tab item
        CTabItem tabItem = new CTabItem( editor.getTabFolder(), SWT.NONE );
        tabItem.setText( Messages.getString( "TemplateEditorPage.TemplateEditor" ) ); //$NON-NLS-1$
        tabItem.setImage( EntryTemplatePlugin.getDefault().getImage( EntryTemplatePluginConstants.IMG_TEMPLATE ) );
        setTabItem( tabItem );

        // Creating the template editor widget
        templateEditorWidget = new TemplateEditorWidget( editor );
    }


    /**
     * {@inheritDoc}
     */
    public void init()
    {
        if ( templateEditorWidget != null )
        {
            // Initializing the template editor widget
            templateEditorWidget.init( getEditor().getTabFolder() );

            // Updating the editor's tab folder to force the attachment of the new form
            getTabItem().setControl( templateEditorWidget.getForm() );
            getEditor().getTabFolder().update();
        }
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        if ( templateEditorWidget != null )
        {
            // Disposing the template editor widget
            templateEditorWidget.dispose();
        }
    }


    /**
     * {@inheritDoc}
     */
    public void update()
    {
        if ( templateEditorWidget != null )
        {
            // Updating the template editor widget
            templateEditorWidget.update();

            // Updating the editor's tab folder to force the attachment of the new form
            getTabItem().setControl( templateEditorWidget.getForm() );
            getEditor().getTabFolder().update();
        }
    }


    /**
     * {@inheritDoc}
     */
    public void setFocus()
    {
        if ( templateEditorWidget != null )
        {
            // Setting focus on the template editor widget
            templateEditorWidget.setFocus();
        }
    }


    /**
     * {@inheritDoc}
     */
    public void editorInputChanged()
    {
        if ( templateEditorWidget != null )
        {
            // Changing the editor input on the template editor widget
            templateEditorWidget.editorInputChanged();

            // Updating the editor's tab folder to force the attachment of the new form
            getTabItem().setControl( templateEditorWidget.getForm() );
            getEditor().getTabFolder().update();
        }
    }


    /**
     * This method is called by the editor when a 'Switch Template' event occurs.
     *
     * @param templateEditorWidget
     *      the template editor widget
     * @param template
     *      the template
     */
    public void templateSwitched( TemplateEditorWidget templateEditorWidget, Template template )
    {
        // Updating the editor's tab folder to force the attachment of the new form
        getTabItem().setControl( templateEditorWidget.getForm() );
        getEditor().getTabFolder().update();
    }
}
