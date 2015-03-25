package org.apache.directory.studio.openldap.config.editor.databases;


import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import org.apache.directory.studio.openldap.config.editor.ServerConfigurationEditor;
import org.apache.directory.studio.openldap.config.editor.ServerConfigurationEditorPage;


/**
 * This class represents the General Page of the Server Configuration Editor.
 */
public class DatabasesPage extends ServerConfigurationEditorPage
{
    /** The Page ID*/
    public static final String ID = DatabasesPage.class.getName(); //$NON-NLS-1$

    /** The Page Title */
    private static final String TITLE = "Databases";


    /**
     * Creates a new instance of GeneralPage.
     *
     * @param editor
     *      the associated editor
     */
    public DatabasesPage( ServerConfigurationEditor editor )
    {
        super( editor, ID, TITLE );
    }


    /**
     * {@inheritDoc}
     */
    protected void createFormContent( Composite parent, FormToolkit toolkit )
    {
        DatabasesMasterDetailsBlock masterDetailsBlock = new DatabasesMasterDetailsBlock( this );
        masterDetailsBlock.createContent( getManagedForm() );
    }


    /**
     * {@inheritDoc}
     */
    public void refreshUI()
    {
    }
}
