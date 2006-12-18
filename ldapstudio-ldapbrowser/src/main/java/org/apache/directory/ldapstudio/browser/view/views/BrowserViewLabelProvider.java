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

package org.apache.directory.ldapstudio.browser.view.views;


import org.apache.directory.ldapstudio.browser.view.views.wrappers.DisplayableTreeViewerElement;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;


/**
 * This class is the Label Provider for the Browser View
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class BrowserViewLabelProvider extends LabelProvider implements ILabelProvider
{

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
     */
    @Override
    public Image getImage( Object element )
    {
        if ( element instanceof DisplayableTreeViewerElement )
        {
            return ( ( DisplayableTreeViewerElement ) element ).getDisplayImage();
        }

        // Default return (Should never be used)
        return null;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
     */
    @Override
    public String getText( Object element )
    {
        if ( element instanceof DisplayableTreeViewerElement )
        {
            return ( ( DisplayableTreeViewerElement ) element ).getDisplayName();
        }
        // Default return (Should never be used)
        return element.toString();
    }

}
