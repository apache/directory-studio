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
package org.apache.directory.studio.openldap.config.editor.wrappers;

import org.apache.directory.studio.common.ui.TableDecorator;
import org.apache.directory.studio.openldap.config.editor.dialogs.ServerIdDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

/**
 * A decorator for the ServerID table.
 *  
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ServerIdDecorator extends TableDecorator<ServerIdWrapper>
{
    /**
     * Create a new instance of ServerIdDecorator
     * @param parentShell The parent Shell
     */
    public ServerIdDecorator( Shell parentShell )
    {
        setDialog( new ServerIdDialog( parentShell ) );
    }

    /**
     * Construct the label for a ServerID. It can be a number in [0..999], or an URL
     */
    public String getText( Object element )
    {
        if ( element instanceof ServerIdWrapper )
        {
            String serverIdtext = ( ( ServerIdWrapper ) element ).toString();

            return serverIdtext;
        }

        return super.getText( element );
    };


    /**
     * Get the image. We have none (may be we could add one for URLs ?)
     */
    public Image getImage( Object element )
    {
        return null;
    };

    
    /**
     * {@inheritDoc}
     */
    @Override
    public int compare( ServerIdWrapper e1, ServerIdWrapper e2 )
    {
        if ( e1 != null )
        {
            return e1.compareTo( e2 );
        }
        else
        {
            if ( e2 == null )
            {
                return 0;
            }
            else
            {
                return 1;
            }
        }
    }
}
