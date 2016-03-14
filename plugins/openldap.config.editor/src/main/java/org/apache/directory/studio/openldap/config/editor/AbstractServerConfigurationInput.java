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
package org.apache.directory.studio.openldap.config.editor;


import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;

import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPlugin;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPluginConstants;
import org.apache.directory.studio.openldap.config.jobs.EntryBasedConfigurationPartition;


/**
 * This class represents the Server Configuration Input.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class AbstractServerConfigurationInput implements ServerConfigurationInput
{
    /** The original configuration partition */
    protected EntryBasedConfigurationPartition originalPartition;


    /**
     * {@inheritDoc}
     */
    public EntryBasedConfigurationPartition getOriginalPartition()
    {
        return originalPartition;
    }


    /**
     * {@inheritDoc}
     */
    public void setOriginalPartition( EntryBasedConfigurationPartition originalPartition )
    {
        this.originalPartition = originalPartition;
    }


    /**
     * {@inheritDoc}
     */
    public String getToolTipText()
    {
        return getName();
    }


    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return "OpenLDAP Configuration";
    }


    /**
     * {@inheritDoc}
     */
    public boolean exists()
    {
        return true;
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return OpenLdapConfigurationPlugin.getDefault().getImageDescriptor(
            OpenLdapConfigurationPluginConstants.IMG_EDITOR );
    }


    /**
     * {@inheritDoc}
     */
    public IPersistableElement getPersistable()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public Object getAdapter( Class adapter )
    {
        return null;
    }
}
