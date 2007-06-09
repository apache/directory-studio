package org.apache.directory.ldapstudio.browser.core.events;


/**
 * Default implementation of {@link EventRunner} that executes an {@link EventRunnable}
 * withing the current thread.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class CoreEventRunner implements EventRunner
{

    /**
     * {@inheritDoc}
     *
     * This implementation executes the given {@link EventRunnable} within
     * the current thread.
     */
    public void execute( EventRunnable runnable )
    {
        runnable.run();
    }

}
