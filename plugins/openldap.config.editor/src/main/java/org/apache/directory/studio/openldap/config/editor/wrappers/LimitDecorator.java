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
import org.apache.directory.studio.openldap.config.editor.dialogs.SizeTimeLimitDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

/**
 * A decorator for the TimeLimitWrapper class.
 *  
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LimitDecorator extends TableDecorator<LimitWrapper>
{
    /**
     * Create a new instance of LimitDecorator
     * @param parentShell The parent Shell
     */
    public LimitDecorator( Shell parentShell, String title )
    {
        setDialog( new SizeTimeLimitDialog( parentShell ) );
    }
    
    
    /**
     * Construct the label for a TimeLimit. It can be one of :
     * 
     */
    public String getText( Object element )
    {
        if ( element instanceof LimitWrapper )
        {
            String limitText = ( ( LimitWrapper ) element ).toString();

            return limitText;
        }

        return super.getText( element );
    };


    /**
     * Get the image. We have none (may be we could add one for URLs ?)
     */
    public Image getImage( Object element )
    {
        return null;
    }


    @Override
    public int compare( LimitWrapper e1, LimitWrapper e2 )
    {
        // TODO Auto-generated method stub
        return 0;
    };
}
