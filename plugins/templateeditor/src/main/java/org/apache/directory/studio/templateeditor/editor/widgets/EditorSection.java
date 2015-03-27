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
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import org.apache.directory.studio.templateeditor.model.widgets.TemplateSection;


/**
 * This class implements an editor section.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EditorSection extends EditorWidget<TemplateSection>
{
    /**
     * Creates a new instance of EditorSection.
     *
     * @param editor
     *      the associated editor
     * @param templateSection
     *      the associated template section
     * @param toolkit
     *      the associated toolkit
     */
    public EditorSection( IEntryEditor editor, TemplateSection templateSection, FormToolkit toolkit )
    {
        super( templateSection, editor, toolkit );
    }


    /**
     * {@inheritDoc}
     */
    public Composite createWidget( Composite parent )
    {
        // Calculating the style
        int style = Section.TITLE_BAR;
        if ( getWidget().getDescription() != null )
        {
            style |= Section.DESCRIPTION;
        }
        if ( getWidget().isExpandable() )
        {
            style |= Section.TWISTIE;
        }
        if ( getWidget().isExpanded() )
        {
            style |= Section.EXPANDED;
        }

        // Creating the section
        Section section = getToolkit().createSection( parent, style );
        section.setLayoutData( getGridata() );

        // Creating the client composite
        Composite clientComposite = getToolkit().createComposite( section );
        section.setClient( clientComposite );

        // Setting the layout for the client composite
        clientComposite.setLayout( new GridLayout( getWidget().getNumberOfColumns(), getWidget().isEqualColumns() ) );
        clientComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Title
        if ( ( getWidget().getTitle() != null ) && ( !"".equals( getWidget().getTitle() ) ) ) //$NON-NLS-1$
        {
            section.setText( getWidget().getTitle() );
        }

        // Description
        if ( ( getWidget().getDescription() != null ) && ( !"".equals( getWidget().getDescription() ) ) ) //$NON-NLS-1$
        {
            section.setDescription( getWidget().getDescription() );
        }

        return clientComposite;
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