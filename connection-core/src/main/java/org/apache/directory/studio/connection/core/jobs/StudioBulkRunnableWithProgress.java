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

package org.apache.directory.studio.connection.core.jobs;


/**
 * A runnable with a progess monitor. When invoked by the {@link StudioConnectionJob} 
 * during the run() method all event notifications are blocked and the runNotification()
 * method is called afterwards to fire event notifications.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface StudioBulkRunnableWithProgress extends StudioRunnableWithProgress
{

    /**
     * Runs notification, called by {@link StudioConnectionJob} after the run() method.
     * 
     * @param monitor the monitor
     */
    public void runNotification( StudioProgressMonitor monitor );

}
