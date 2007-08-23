package org.apache.directory.studio.connection.core.io;


import org.apache.directory.studio.connection.core.StudioProgressMonitor;


/**
 * A ConnectionWrapper is a wrapper for a real directory connection implementation.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface ConnectionWrapper
{

    /**
     * Connects to the directory server.
     * 
     * @param monitor the progres monitor
     */
    public void connect( StudioProgressMonitor monitor );


    /**
     * Disconnects from the directory server.
     */
    public void disconnect();


    /**
     * Binds to the directory server.
     * 
     * @param monitor the progress monitor
     */
    public void bind( StudioProgressMonitor monitor );


    /**
     * Unbinds from the directory server.
     */
    public void unbind();


    /**
     * Checks if is connected.
     * 
     * @return true, if is connected
     */
    public boolean isConnected();

}
