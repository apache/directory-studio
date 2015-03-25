package org.apache.directory.studio.openldap.config.editor.databases;


import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import org.apache.directory.studio.openldap.config.model.OlcFrontendConfig;


/**
 * This interface represents a block for Frontend Specific Details.
 */
public class FrontendDatabaseSpecificDetailsBlock implements DatabaseSpecificDetailsBlock
{
    /** The database */
    private OlcFrontendConfig database;

    // UI Fields
    private Text defaultSearchBaseText;
    private Text passwordHashText;
    private Text sortValsText;


    /**
     * Creates a new instance of FrontendDatabaseSpecificDetailsBlock.
     *
     * @param database the database
     */
    public FrontendDatabaseSpecificDetailsBlock( OlcFrontendConfig database )
    {
        this.database = database;
    }


    /**
     * {@inheritDoc}
     */
    public void createFormContent( Composite parent, FormToolkit toolkit )
    {
        // Default Search Base Text
        toolkit.createLabel( parent, "Default Search Base:" );
        defaultSearchBaseText = toolkit.createText( parent, "" );
        defaultSearchBaseText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Password Hash Text
        toolkit.createLabel( parent, "Password Hash:" );
        passwordHashText = toolkit.createText( parent, "" );
        passwordHashText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Sort Vals Text
        toolkit.createLabel( parent, "Sort Vals:" );
        sortValsText = toolkit.createText( parent, "" );
        sortValsText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
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
            // Default Search Base Text
            String defaultSearchBase = database.getOlcDefaultSearchBase();
            defaultSearchBaseText.setText( ( defaultSearchBase == null ) ? "" : defaultSearchBase ); //$NON-NLS-1$

            // Password Hash Text
            List<String> passwordHash = database.getOlcPasswordHash();
            passwordHashText.setText( ( passwordHash == null ) ? "" : concatenate( passwordHash ) ); //$NON-NLS-1$

            // Sort Vals Text
            List<String> sortVals = database.getOlcSortVals();
            sortValsText.setText( ( sortVals == null ) ? "" : concatenate( sortVals ) ); //$NON-NLS-1$
        }
    }


    private String concatenate( List<String> list )
    {
        StringBuilder sb = new StringBuilder();

        for ( String string : list )
        {
            sb.append( string );
            sb.append( ", " );
        }

        if ( sb.length() > 1 )
        {
            sb.deleteCharAt( sb.length() - 1 );
            sb.deleteCharAt( sb.length() - 1 );
        }

        return sb.toString();
    }
}
