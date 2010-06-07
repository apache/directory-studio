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

package org.apache.directory.studio.ldapbrowser.core.model.impl;


import java.io.Serializable;
import java.util.Set;

import org.apache.directory.studio.connection.core.jobs.StudioBulkRunnableWithProgress;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;


/**
 * A ChildrenInfo is used to hold the list of children entries
 * of a parent entry.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ChildrenInfo implements Serializable
{

    private static final long serialVersionUID = -4642987611142312896L;

    /** The children initialized flag. */
    protected volatile boolean childrenInitialized = false;

    /** The children set. */
    protected volatile Set<IEntry> childrenSet = null;

    /** The has more children flag. */
    protected volatile boolean hasMoreChildren = false;

    /** The runnable used to fetch the top page of children. */
    protected StudioBulkRunnableWithProgress topPageChildrenRunnable;

    /** The runnable used to fetch the next page of children. */
    protected StudioBulkRunnableWithProgress nextPageChildrenRunnable;


    /**
     * Creates a new instance of ChildrenInfo.
     */
    public ChildrenInfo()
    {
    }

}
