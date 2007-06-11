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
package org.apache.directory.studio.proxy.view.wrappers;


import java.util.List;

import org.eclipse.swt.graphics.Image;


/**
 * This interface defines an element that can be displayed in the LDAP Proxy TreeViewer.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface IWrapper
{
    /**
     * Returns the text for the label of the element.
     *
     * @return
     *      the text string used to label the element, or null  if there is no text label for the element
     */
    public String getText();


    /**
     * Returns the image for the label of the element. 
     *
     * @return
     *      the image used to label the element, or null  if there is no image for the element
     */
    public Image getImage();


    /**
     * Returns the child elements of the element.
     *
     * @return
     *      a List of child elements
     */
    public List<IWrapper> getChildren();


    /**
     * Returns whether the element has children.
     *
     * @return
     *      true if the given element has children, and false if it has no children
     */
    public boolean hasChildren();


    /**
     * Returns the parent for the element, or null indicating that the parent can't be computed. 
     * In this case the tree-structured viewer can't expand a given node correctly if requested.
     *
     * @return
     *      the parent element, or null if it has none or if the parent cannot be computed
     */
    public IWrapper getParent();
}
