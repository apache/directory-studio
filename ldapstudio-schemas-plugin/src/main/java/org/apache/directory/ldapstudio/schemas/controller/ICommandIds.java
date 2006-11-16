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

package org.apache.directory.ldapstudio.schemas.controller;


/**
 * Interface defining the application's command IDs.
 * Key bindings can be defined for specific commands.
 * To associate an action with a command, use IAction.setActionDefinitionId(commandId).
 *
 * @see org.eclipse.jface.action.IAction#setActionDefinitionId(String)
 */
public interface ICommandIds
{
    public static final String CMD_DELETE = Application.PLUGIN_ID + ".delete"; //$NON-NLS-1$
    public static final String CMD_OPEN_LOCAL = Application.PLUGIN_ID + ".openlocal"; //$NON-NLS-1$
    public static final String CMD_OPEN_SCHEMA_SOURCE_CODE = Application.PLUGIN_ID + ".openschemasourcecode"; //$NON-NLS-1$
    public static final String CMD_SAVE = Application.PLUGIN_ID + ".save"; //$NON-NLS-1$
    public static final String CMD_SAVE_AS = Application.PLUGIN_ID + ".saveas"; //$NON-NLS-1$
    public static final String CMD_SAVE_ALL = Application.PLUGIN_ID + ".saveall"; //$NON-NLS-1$
    public static final String CMD_REMOVE_SCHEMA = Application.PLUGIN_ID + ".removeschema"; //$NON-NLS-1$
    public static final String CMD_CREATE_A_NEW_SCHEMA = Application.PLUGIN_ID + ".createanewschema"; //$NON-NLS-1$
    public static final String CMD_CREATE_A_NEW_OBJECTCLASS = Application.PLUGIN_ID + ".createanewobjectclass"; //$NON-NLS-1$
    public static final String CMD_CREATE_A_NEW_ATTRIBUTETYPE = Application.PLUGIN_ID + ".createanewattributetype"; //$NON-NLS-1$
    public static final String CMD_SORT_HIERACHICAL_VIEWER = Application.PLUGIN_ID + ".sorthierarchicalviewer"; //$NON-NLS-1$
    public static final String CMD_SORT_POOL_MANAGER = Application.PLUGIN_ID + ".sortpoolmanager"; //$NON-NLS-1$
}
