package org.apache.directory.studio.openldap.config.editor.databases;


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import org.apache.directory.studio.openldap.config.model.OlcLdifConfig;


/**
 * This interface represents a block for None Specific Details.
 */
public class LdifDatabaseSpecificDetailsBlock implements DatabaseSpecificDetailsBlock
{
    /** The database */
    private OlcLdifConfig database;

    // UI Widgets
    private Text directoryText;


    /**
     * Creates a new instance of LdifDatabaseSpecificDetailsBlock.
     * 
     * @param database the database
     */
    public LdifDatabaseSpecificDetailsBlock( OlcLdifConfig database )
    {
        this.database = database;
    }


    /**
     * {@inheritDoc}
     */
    public void createFormContent( Composite parent, FormToolkit toolkit )
    {
        // Directory Text
        toolkit.createLabel( parent, "Directory:" );
        directoryText = toolkit.createText( parent, "" );
        directoryText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }


    /**
     * {@inheritDoc}
     */
    public void refresh()
    {

        if ( database == null )
        {
            // Blank out all fields
            // TODO
        }
        else
        {
            // Directory Text
            String directory = database.getOlcDbDirectory();
            directoryText.setText( ( directory == null ) ? "" : directory ); //$NON-NLS-1$
        }
    }
}
