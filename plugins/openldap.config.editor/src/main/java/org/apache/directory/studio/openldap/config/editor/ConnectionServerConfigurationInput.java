package org.apache.directory.studio.openldap.config.editor;


import org.apache.directory.studio.connection.core.Connection;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import org.apache.directory.studio.openldap.config.jobs.EntryBasedConfigurationPartition;


/**
 * This class represents the Non Existing Server Configuration Input.
 */
public class ConnectionServerConfigurationInput implements IEditorInput
{
    /** The connection */
    private Connection connection;

    /** The original configuration partition */
    private EntryBasedConfigurationPartition originalPartition;


    /**
     * Creates a new instance of ConnectionServerConfigurationInput.
     *
     * @param connection
     *      the connection
     */
    public ConnectionServerConfigurationInput( Connection connection )
    {
        this.connection = connection;
    }


    /**
     * Gets the connection.
     *
     * @return
     *      the connection
     */
    public Connection getConnection()
    {
        return connection;
    }


    /**
     * Gets the original configuration partition.
     *
     * @return
     *      the original configuration partition
     */
    public EntryBasedConfigurationPartition getOriginalPartition()
    {
        return originalPartition;
    }


    /**
     * {@inheritDoc}
     */
    public String getToolTipText()
    {
        return NLS.bind( "{0} - Configuration", connection.getName() );
    }


    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return NLS.bind( "{0} - Configuration", connection.getName() );
    }


    /**
     * {@inheritDoc}
     */
    public boolean exists()
    {
        return connection != null;
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public IPersistableElement getPersistable()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public Object getAdapter( Class adapter )
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public boolean equals( Object obj )
    {
        if ( obj == null )
        {
            return false;
        }

        if ( obj instanceof ConnectionServerConfigurationInput )
        {
            ConnectionServerConfigurationInput input = ( ConnectionServerConfigurationInput ) obj;
            if ( input.exists() && exists() )
            {
                Connection inputConnection = input.getConnection();

                if ( ( inputConnection != null ) && ( inputConnection != null ) )
                {
                    return inputConnection.equals( connection );
                }
            }
        }

        return false;
    }


    /**
     * {@inheritDoc}
     */
    public int hashCode()
    {
        return connection.hashCode();
    }


    /**
     * Sets the original configuration partition.
     *
     * @param originalPartition
     *      the original configuration 
     */
    public void setOriginalPartition( EntryBasedConfigurationPartition originalPartition )
    {
        this.originalPartition = originalPartition;
    }
}
