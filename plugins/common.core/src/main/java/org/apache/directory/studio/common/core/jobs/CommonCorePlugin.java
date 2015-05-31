/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */

package org.apache.directory.studio.common.core.jobs;


import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;


/**
 * The activator class controls the plug-in life cycle
 */
public class CommonCorePlugin extends Plugin
{
    /** The shared plugin instance. */
    private static CommonCorePlugin plugin;

    /** The watcher job */
    private StudioProgressMonitorWatcherJob studioProgressMonitorWatcherJob;


    public CommonCorePlugin()
    {
        plugin = this;
    }


    /**
     * @see Plugin#start(org.osgi.framework.BundleContext)
     */
    public void start( BundleContext context ) throws Exception
    {
        super.start( context );

        studioProgressMonitorWatcherJob = new StudioProgressMonitorWatcherJob();
        studioProgressMonitorWatcherJob.setSystem( true );
        studioProgressMonitorWatcherJob.schedule();
    }


    /**
     * @see Plugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop( BundleContext context ) throws Exception
    {
        plugin = null;

        studioProgressMonitorWatcherJob.stop();
        studioProgressMonitorWatcherJob.join();
        studioProgressMonitorWatcherJob = null;

        super.stop( context );
    }


    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static CommonCorePlugin getDefault()
    {
        return plugin;
    }


    public StudioProgressMonitorWatcherJob getStudioProgressMonitorWatcherJob()
    {
        return studioProgressMonitorWatcherJob;
    }

}
