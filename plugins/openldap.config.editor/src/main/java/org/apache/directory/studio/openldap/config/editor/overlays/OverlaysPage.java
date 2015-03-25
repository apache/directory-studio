package org.apache.directory.studio.openldap.config.editor.overlays;


import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import org.apache.directory.studio.openldap.config.editor.ServerConfigurationEditor;
import org.apache.directory.studio.openldap.config.editor.ServerConfigurationEditorPage;


/**
 * This class represents the General Page of the Server Configuration Editor.
 */
public class OverlaysPage extends ServerConfigurationEditorPage
{
    /** The Page ID*/
    public static final String ID = OverlaysPage.class.getName(); //$NON-NLS-1$

    /** The Page Title */
    private static final String TITLE = "Overlays";


    // UI Controls

    /**
     * Creates a new instance of GeneralPage.
     *
     * @param editor
     *      the associated editor
     */
    public OverlaysPage( ServerConfigurationEditor editor )
    {
        super( editor, ID, TITLE );
    }


    /**
     * {@inheritDoc}
     */
    protected void createFormContent( Composite parent, FormToolkit toolkit )
    {
        OverlaysMasterDetailsBlock masterDetailsBlock = new OverlaysMasterDetailsBlock( this );
        masterDetailsBlock.createContent( getManagedForm() );
    }


    /**
     * {@inheritDoc}
     */
    public void refreshUI()
    {
    }
}
