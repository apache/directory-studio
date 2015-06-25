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
import org.apache.directory.studio.openldap.config.editor.dialogs.StringValueDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

/**
 * A decorator for a String Value table.
 *  
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StringValueDecorator extends TableDecorator<StringValueWrapper>
{
    /**
     * Create a new instance of StringValueDecorator
     * @param parentShell The parent Shell
     */
    public StringValueDecorator( Shell parentShell, String attributeName )
    {
        setDialog( new StringValueDialog( parentShell, attributeName ) );
    }
    

    /**
     * Construct the label for a String.
     * 
     */
    public String getText( Object element )
    {
        if ( element instanceof StringValueWrapper )
        {
            return ( ( StringValueWrapper ) element ).getValue();
        }

        return super.getText( element );
    };


    /**
     * Get the image. We have none
     */
    public Image getImage( Object element )
    {
        return null;
    };

    
    /**
     * {@inheritDoc}
     */
    @Override
    public int compare( StringValueWrapper e1, StringValueWrapper e2 )
    {
        if ( e1 != null )
        {
            if ( e2 == null )
            {
                return 1;
            }
            else
            {
                return e1.compareTo( e2 );
            }
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
