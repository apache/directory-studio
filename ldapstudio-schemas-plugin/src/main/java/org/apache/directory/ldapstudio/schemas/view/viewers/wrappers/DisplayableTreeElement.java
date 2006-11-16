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

package org.apache.directory.ldapstudio.schemas.view.viewers.wrappers;


import org.eclipse.swt.graphics.Image;


/**
 * All objects that want to be displayed in the tree view must implement
 * this interface.
 * Note: implementation of E.Lecharny suggestion after the 04/06/06 briefing
 *
 */
public interface DisplayableTreeElement
{
    /**
     * Use this method to get the image that should be displayed in
     * the tree view.
     * @return the display image
     */
    public Image getDisplayImage();


    /**
     * Use this method to get the name that should be displayed in
     * the tree view.
     * @return the display name
     */
    public String getDisplayName();
}
