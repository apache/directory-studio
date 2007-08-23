
package org.apache.directory.studio.connection.ui;

import org.apache.directory.studio.connection.core.event.EventRunnable;
import org.apache.directory.studio.connection.core.event.EventRunner;
import org.eclipse.swt.widgets.Display;


/**
 * Implementation of {@link EventRunner} that executes an {@link EventRunnable}
 * withing the SWT UI thread.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class UiThreadEventRunner implements EventRunner
{

    /**
     * {@inheritDoc}
     *
     * This implementation executes the given {@link EventRunnable} within
     * the SWT UI thread.
     */
    public void execute( EventRunnable runnable )
    {
        Display.getDefault().asyncExec( runnable );
    }

}
