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
package org.apache.directory.studio.templateeditor.view.preferences;


import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;

import org.apache.directory.studio.templateeditor.model.Template;


/**
 * This class is used to respond to a check state event.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class TemplatesCheckStateListener implements ICheckStateListener
{
    /** The associated content provider */
    private TemplatesContentProvider contentProvider;

    /** The templates manager */
    private PreferencesTemplatesManager manager;


    /**
     * Creates a new instance of TemplatesCheckStateProviderListener.
     *
     * @param contentProvider
     *      the associated content provider
     */
    public TemplatesCheckStateListener( TemplatesContentProvider contentProvider, PreferencesTemplatesManager manager )
    {
        this.contentProvider = contentProvider;
        this.manager = manager;
    }


    /**
     * {@inheritDoc}
     */
    public void checkStateChanged( CheckStateChangedEvent event )
    {
        // Getting the element of the event
        Object element = event.getElement();

        // Object class presentation
        if ( contentProvider.isObjectClassPresentation() )
        {
            if ( element instanceof Template )
            {
                setTemplateEnabled( ( Template ) element, event.getChecked() );
            }
            else if ( element instanceof ObjectClass )
            {
                // Getting the children of the node
                Object[] children = contentProvider.getChildren( element );
                if ( children != null )
                {
                    for ( Object child : children )
                    {
                        setTemplateEnabled( ( Template ) child, event.getChecked() );
                    }
                }
            }
        }
        // Template presentation
        else if ( contentProvider.isTemplatePresentation() )
        {
            setTemplateEnabled( ( Template ) element, event.getChecked() );
        }
    }


    /**
     * Enables or disables a template.
     *
     * @param template
     *      the template
     * @param enabled
     *      <code>true</code> if the template needs to be enabled,
     *      <code>false</code> if the template needs to be disabled
     */
    private void setTemplateEnabled( Template template, boolean enabled )
    {
        if ( enabled )
        {
            manager.enableTemplate( template );
        }
        else
        {
            manager.disableTemplate( template );
        }
    }
}
