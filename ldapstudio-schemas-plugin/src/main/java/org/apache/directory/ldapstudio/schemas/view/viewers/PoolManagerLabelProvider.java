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

package org.apache.directory.ldapstudio.schemas.view.viewers;


import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.DisplayableTreeElement;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;


/**
 * Label provider for the schema-pool manager
 *
 */
public class PoolManagerLabelProvider extends LabelProvider
{

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
     */
    public String getText( Object obj )
    {
        if ( obj instanceof DisplayableTreeElement )
            return ( ( DisplayableTreeElement ) obj ).getDisplayName();

        //default
        return obj.toString();
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
     */
    public Image getImage( Object obj )
    {
        if ( obj instanceof DisplayableTreeElement )
            return ( ( DisplayableTreeElement ) obj ).getDisplayImage();

        //default
        return PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_WARN_TSK );

    }
}
