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

package org.apache.directory.ldapstudio.browser.controller.actions;


import org.apache.directory.ldapstudio.browser.Activator;
import org.apache.directory.ldapstudio.browser.view.ImageKeys;
import org.apache.directory.ldapstudio.browser.view.views.AttributesView;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class implements the Attribute Edit Action.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AttributeEditAction extends Action
{
    private AttributesView view;


    /**
     * Creates a new instance of AttributeEditAction.
     * @param view the associated view
     * @param text the string used as the text for the action
     */
    public AttributeEditAction( AttributesView view, String text )
    {
        super( text );
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, ImageKeys.ATTRIBUTE_EDIT ) );
        setToolTipText( "Edit attribute" );
        this.view = view;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run()
    {
        new RenameAttributeAction( view, "Rename attribute" ).run();
    }
}
