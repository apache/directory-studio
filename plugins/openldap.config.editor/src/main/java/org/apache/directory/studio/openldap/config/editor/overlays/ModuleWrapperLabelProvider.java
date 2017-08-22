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
package org.apache.directory.studio.openldap.config.editor.overlays;

import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPlugin;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPluginConstants;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * This class defines a label provider for a module wrapper viewer.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ModuleWrapperLabelProvider extends LabelProvider
{
    /**
     * Construct the label for a ModuleList. It's the type, followed by the suffixDN.
     */
    @Override
    public String getText( Object element )
    {
        if ( element instanceof ModuleWrapper )
        {
            return ( ( ModuleWrapper ) element ).getModulePathName();
        }

        return super.getText( element );
    }


    /**
     * Get the ModuleList image, if it's a ModuleList
     */
    @Override
    public Image getImage( Object element )
    {
        if ( element instanceof ModuleWrapper )
        {
            return OpenLdapConfigurationPlugin.getDefault().getImage(
                OpenLdapConfigurationPluginConstants.IMG_DATABASE );
        }

        return super.getImage( element );
    }
}
