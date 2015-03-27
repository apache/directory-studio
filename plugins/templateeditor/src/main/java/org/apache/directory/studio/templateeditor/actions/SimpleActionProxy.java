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
package org.apache.directory.studio.templateeditor.actions;


import org.apache.directory.studio.ldapbrowser.common.actions.BrowserAction;
import org.eclipse.jface.action.Action;


/**
 * This class wraps a {@link BrowserAction} as a standard JFace {@link Action}.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SimpleActionProxy extends Action
{
    /** The {@link BrowserAction}*/
    protected BrowserAction action;


    /**
     * Creates a new instance of SimpleActionProxy.
     *
     * @param action
     *      the {@link BrowserAction}
     * @param style
     *      the style for the {@link Action}
     */
    public SimpleActionProxy( BrowserAction action, int style )
    {
        super( action.getText(), style );
        this.action = action;

        super.setImageDescriptor( action.getImageDescriptor() );
        super.setActionDefinitionId( action.getCommandId() );
    }


    /**
     * Creates a new instance of SimpleActionProxy.
     *
     * @param action
     *      the {@link BrowserAction}
     */
    public SimpleActionProxy( BrowserAction action )
    {
        this( action, action.getStyle() );
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        if ( action != null )
        {
            action.run();
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        if ( action != null )
        {
            return action.isEnabled();
        }
        else
        {
            return false;
        }
    }
}
