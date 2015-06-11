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

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * This class defines a label provider for a SSF wrapper viewer.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SsfLabelProvider extends LabelProvider
{
    /**
     * Construct the label for a TCPBuffer. It can be one of :
     * 
     */
    public String getText( Object element )
    {
        if ( element instanceof SsfWrapper )
        {
            String ssfText = ( ( SsfWrapper ) element ).toString();

            return ssfText;
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
}
