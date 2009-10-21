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
