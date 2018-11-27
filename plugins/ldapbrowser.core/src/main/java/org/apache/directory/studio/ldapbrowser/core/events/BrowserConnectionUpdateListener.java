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

package org.apache.directory.studio.ldapbrowser.core.events;


import java.util.EventListener;

import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;


/**
 * A listener for {@link BrowserConnectionUpdateEvent}s
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface BrowserConnectionUpdateListener extends EventListener
{
    /**
     * Called when an {@link IBrowserConnection} was updated.
     *
     * @param browserConnectionUpdateEvent the browser connection update event
     */
    void browserConnectionUpdated( BrowserConnectionUpdateEvent browserConnectionUpdateEvent );
}
