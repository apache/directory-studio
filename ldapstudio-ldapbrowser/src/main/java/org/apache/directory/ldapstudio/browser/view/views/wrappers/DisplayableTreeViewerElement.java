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

package org.apache.directory.ldapstudio.browser.view.views.wrappers;


import org.apache.directory.ldapstudio.browser.model.Connection;
import org.eclipse.swt.graphics.Image;


/**
 * All objects that want to be displayed in the JFace TreeViewer 
 * should implement this interface.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface DisplayableTreeViewerElement
{
    /**
     * Get the image that should be displayed in the TreeViewer
     * @return the display image
     */
    public Image getDisplayImage();


    /**
     * Get the name that should be displayed in the TreeViewer
     * @return the display name
     */
    public String getDisplayName();


    /**
     * Get parent object in the TreeViewer Hierarchy
     * @return the parent
     */
    public Object getParent();


    /**
     * Set the parent object in the TreeViewer Hierarchy
     * @param parent the parent element
     */
    public void setParent( Object parent );


    /**
     * Gets the children of the object
     * @return the children of the object
     */
    public Object[] getChildren();
}
