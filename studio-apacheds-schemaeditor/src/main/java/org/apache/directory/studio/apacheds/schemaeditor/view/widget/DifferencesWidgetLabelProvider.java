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
package org.apache.directory.studio.apacheds.schemaeditor.view.widget;


import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.PluginConstants;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AbstractAddDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AbstractModifyDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AbstractRemoveDifference;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class implements the LabelProvider for the DifferencesWidget.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DifferencesWidgetLabelProvider extends LabelProvider
{
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
     */
    public Image getImage( Object element )
    {
        if ( element instanceof AbstractAddDifference )
        {
            return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, PluginConstants.IMG_DIFFERENCE_ADD )
                .createImage();
        }
        else if ( element instanceof AbstractModifyDifference )
        {
            return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                PluginConstants.IMG_DIFFERENCE_MODIFY ).createImage();
        }
        else if ( element instanceof AbstractRemoveDifference )
        {
            return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                PluginConstants.IMG_DIFFERENCE_REMOVE ).createImage();
        }

        // Default
        return null;
    }
}
