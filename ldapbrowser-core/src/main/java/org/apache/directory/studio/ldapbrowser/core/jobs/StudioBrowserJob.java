
package org.apache.directory.studio.ldapbrowser.core.jobs;

import org.apache.directory.studio.connection.core.jobs.StudioConnectionJob;
import org.apache.directory.studio.connection.core.jobs.StudioRunnableWithProgress;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;

public class StudioBrowserJob extends StudioConnectionJob
{

    public StudioBrowserJob( StudioRunnableWithProgress runnable )
    {
        super( runnable );
    }

    
    @Override
    protected void suspendEventFireingInCurrentThread()
    {
        EventRegistry.suspendEventFireingInCurrentThread();
        super.suspendEventFireingInCurrentThread();
    }
    
    @Override
    protected void resumeEventFireingInCurrentThread()
    {
        EventRegistry.resumeEventFireingInCurrentThread();
        super.resumeEventFireingInCurrentThread();
    }
}
