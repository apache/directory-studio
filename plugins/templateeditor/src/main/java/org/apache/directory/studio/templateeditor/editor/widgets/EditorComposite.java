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
package org.apache.directory.studio.templateeditor.editor.widgets;


import org.apache.directory.studio.entryeditors.IEntryEditor;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import org.apache.directory.studio.templateeditor.model.widgets.TemplateComposite;


/**
 * This class implements an editor composite.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EditorComposite extends EditorWidget<TemplateComposite>
{
    /**
     * Creates a new instance of EditorComposite.
     * 
     * @param editor
     *      the associated editor
     * @param templateComposite
     *      the associated template composite
     * @param toolkit
     *      the associated toolkit
     */
    public EditorComposite( IEntryEditor editor, TemplateComposite templateComposite, FormToolkit toolkit )
    {
        super( templateComposite, editor, toolkit );
    }


    /**
     * {@inheritDoc}
     */
    public Composite createWidget( Composite parent )
    {
        Composite composite = getToolkit().createComposite( parent );
        composite.setLayout( new GridLayout( getWidget().getNumberOfColumns(), getWidget().isEqualColumns() ) );
        composite.setLayoutData( getGridata() );

        return composite;
    }


    /**
     * {@inheritDoc}
     */
    public void update()
    {
        // Nothing to do
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        // Nothing to do
    }
}