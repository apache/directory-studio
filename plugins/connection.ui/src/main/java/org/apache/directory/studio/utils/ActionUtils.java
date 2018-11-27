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

package org.apache.directory.studio.utils;


import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;


/**
 * Utils for Eclipse IAction objects.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class ActionUtils
{
    /**
     * A private constructor to make this class an utility class we can't instanciate.
     **/
    private ActionUtils()
    {
    }
    
    
    /**
     * Deactivates the action handler, if the handler's action is equal to 
     * the given action.
     * 
     * @param action the action
     */
    public static void deactivateActionHandler( IAction action )
    {
        ICommandService commandService = PlatformUI.getWorkbench().getAdapter(
            ICommandService.class );
        
        if ( commandService != null )
        {
            Command command = commandService.getCommand( action.getActionDefinitionId() );
            IHandler handler = command.getHandler();
            
            if ( handler instanceof ActionHandler )
            {
                ActionHandler actionHandler = ( ActionHandler ) handler;
                
                if ( actionHandler.getAction() == action )
                {
                    command.setHandler( null );
                }
            }
            else if ( handler != null )
            {
                command.setHandler( null );
            }
        }
    }


    /**
     * Activates the action handler
     * 
     * @param action the action
     */
    public static void activateActionHandler( IAction action )
    {
        ICommandService commandService = PlatformUI.getWorkbench().getAdapter(
            ICommandService.class );
        
        if ( commandService != null )
        {
            ActionHandler actionHandler = new ActionHandler( action );
            commandService.getCommand( action.getActionDefinitionId() ).setHandler( actionHandler );
        }
    }
}
