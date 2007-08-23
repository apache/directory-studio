package org.apache.directory.studio.connection.core.event;


/**
 * An EventRunner is used to execute an {@link EventRunnable}.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface EventRunner
{

    /**
     * Executes the given {@link EventRunnable}.
     *
     * @param runnable the event runnable to run
     */
    public void execute( EventRunnable runnable );

}
