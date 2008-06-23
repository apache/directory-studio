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

package org.apache.directory.studio.ldapbrowser.core.jobs;


import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;


/**
 * Base class for jobs that modify attributes of a single entry.
 * Reloads the modified attributes after modification.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public abstract class AbstractAttributeModificationJob extends AbstractNotificationJob
{

    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractNotificationJob#executeNotificationJob(org.apache.directory.studio.connection.core.jobs.StudioProgressMonitor)
     */
    protected void executeNotificationJob( StudioProgressMonitor monitor )
    {
        try
        {
            executeAttributeModificationJob( monitor );
        }
        finally
        {
            if( !getModifiedEntry().getBrowserConnection().getConnection().isReadOnly() )
            {
                // reload affected attributes
                String[] attributeDescriptions = getAffectedAttributeDescriptions();
                InitializeAttributesRunnable.initializeAttributes( getModifiedEntry(), attributeDescriptions, monitor );
            }
        }
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getConnections()
     */
    protected Connection[] getConnections()
    {
        return new Connection[]
            { getModifiedEntry().getBrowserConnection().getConnection() };
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getLockedObjects()
     */
    protected Object[] getLockedObjects()
    {
        return new Object[]
            { getModifiedEntry() };
    }


    /**
     * Execute the attribute modification job.
     * 
     * @param monitor the progress monitor
     */
    protected abstract void executeAttributeModificationJob( StudioProgressMonitor monitor );


    /**
     * Gets the modified entry.
     * 
     * @return the modified entry
     */
    protected abstract IEntry getModifiedEntry();


    /**
     * Gets the affected attribute descriptions.
     * Implementations must return all attribute descriptions of
     * added, modified or removed attributes.
     * 
     * @return the affected attribute descriptions
     */
    protected abstract String[] getAffectedAttributeDescriptions();

}
