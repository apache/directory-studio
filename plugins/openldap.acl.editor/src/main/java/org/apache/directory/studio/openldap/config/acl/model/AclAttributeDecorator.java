/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.studio.openldap.config.acl.model;

import org.apache.directory.studio.common.ui.TableDecorator;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.openldap.config.acl.dialogs.AclAttributeDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

/**
 * The AclAttribute decorator class.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AclAttributeDecorator extends TableDecorator<AclAttributeWrapper>
{
    /** The associated image, if any */
    private Image image;

    /**
     * Create a new instance of AclAttributeDecorator
     * @param parentShell The parent Shell
     * @param connection The Connection to the LDAP server
     */
    public AclAttributeDecorator( Shell parentShell, Connection connection )
    {
        setDialog( new AclAttributeDialog( parentShell, connection ) );
    }
    
    
    /** 
     * Adds an Image to this decorator 
     * @param image The Image
     */
    public void setImage( Image image )
    {
        this.image = image;
    }
    

    /**
     * Construct the label for a AclAttribute.
     * 
     * @param element the Element for which we want the value
     * @return a String representation of the element
     */
    public String getText( Object element )
    {
        if ( element instanceof AclAttributeWrapper )
        {
            return ( ( AclAttributeWrapper ) element ).getAclAttribute().toString();
        }

        return super.getText( element );
    };


    /**
     * Get the image. Here, We have none
     * 
     * @param element The element for which we want the image
     * @return The associated Image, or Null
     */
    public Image getImage( Object element )
    {
        return image;
    };


    /**
     * {@inheritDoc}
     */
    @Override
    public int compare( AclAttributeWrapper e1, AclAttributeWrapper e2 )
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
