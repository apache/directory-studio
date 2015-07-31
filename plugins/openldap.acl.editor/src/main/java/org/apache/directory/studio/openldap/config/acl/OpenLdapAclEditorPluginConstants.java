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
package org.apache.directory.studio.openldap.config.acl;


/**
 * Contains constants for the value editors.
 * Final reference -> class shouldn't be extended
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class OpenLdapAclEditorPluginConstants
{
    /**
     *  Ensures no construction of this class, also ensures there is no need for final keyword above
     *  (Implicit super constructor is not visible for default constructor),
     *  but is still self documenting.
     */
    private OpenLdapAclEditorPluginConstants()
    {
    }

    /** The plug-in ID */
    public static final String PLUGIN_ID = OpenLdapAclEditorPlugin.getDefault().getPluginProperties()
        .getString( "Plugin_id" ); //$NON-NLS-1$

    /** The ID for OpenLDAP ACL Template */
    public static final String TEMPLATE_ID = OpenLdapAclEditorPlugin.getDefault().getPluginProperties()
        .getString( "CtxType_Template_id" ); //$NON-NLS-1$

    public static final String IMG_ADD = "resources/icons/add.gif"; //$NON-NLS-1$
    public static final String IMG_DELETE = "resources/icons/delete.gif"; //$NON-NLS-1$
    public static final String IMG_DOWN = "resources/icons/down.png"; //$NON-NLS-1$
    public static final String IMG_EDITOR = "resources/icons/editor.gif"; //$NON-NLS-1$
    public static final String IMG_KEYWORD = "resources/icons/keyword.gif"; //$NON-NLS-1$
    public static final String IMG_UP = "resources/icons/up.png"; //$NON-NLS-1$
    
    public static final String DIALOGSETTING_KEY_ATTRIBUTES_HISTORY = "attributesHistory"; //$NON-NLS-1$
}
