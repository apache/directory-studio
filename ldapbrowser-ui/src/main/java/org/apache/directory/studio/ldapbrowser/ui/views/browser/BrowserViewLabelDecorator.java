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

package org.apache.directory.studio.ldapbrowser.ui.views.browser;


import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;


/**
 * This class implements the label decorator for the browser view. It adds
 * an overlay image to the main image to mark search results, filtered
 * entries and inconsistent entries.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BrowserViewLabelDecorator extends LabelProvider implements ILightweightLabelDecorator
{

    /**
     * {@inheritDoc}
     */
    public void decorate( Object element, IDecoration decoration )
    {
        IEntry entry = null;

        if ( element instanceof ISearchResult )
        {
            ISearchResult searchResult = ( ISearchResult ) element;
            entry = searchResult.getEntry();
            decoration.addOverlay( BrowserUIPlugin.getDefault().getImageDescriptor(
                BrowserUIConstants.IMG_OVR_SEARCHRESULT ), IDecoration.BOTTOM_RIGHT );
        }
        else if ( element instanceof IEntry )
        {
            entry = ( IEntry ) element;
            if ( entry.getChildrenFilter() != null )
            {
                decoration.addOverlay( BrowserUIPlugin.getDefault().getImageDescriptor(
                    BrowserUIConstants.IMG_OVR_FILTERED ), IDecoration.BOTTOM_RIGHT );
            }
        }
        else
        {
            decoration.addOverlay( null, IDecoration.BOTTOM_RIGHT );
        }

        if ( entry != null )
        {
            decoration.addOverlay( null, IDecoration.BOTTOM_LEFT );
        }
    }

}
