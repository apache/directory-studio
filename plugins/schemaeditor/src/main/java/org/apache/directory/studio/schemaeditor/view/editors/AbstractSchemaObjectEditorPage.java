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

package org.apache.directory.studio.schemaeditor.view.editors;


import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;


/**
 * This abstract class defines a schema object editor page.
 */
public abstract class AbstractSchemaObjectEditorPage<E extends FormEditor> extends FormPage
{
    /** The flag to indicate if the page has been initialized */
    protected boolean initialized = false;


    /**
     * Default constructor
     * 
     * @param editor the parent editor
     * @param id the unique identifier
     * @param title the page title
     */
    public AbstractSchemaObjectEditorPage( E editor, String id, String title )
    {
        super( editor, id, title );
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public E getEditor()
    {
        return ( E ) super.getEditor();
    }


    /**
     * {@inheritDoc}
     */
    protected void createFormContent( IManagedForm managedForm )
    {
        initialized = true;
    }


    /**
     * Adds listeners to UI fields
     */
    protected void addListeners()
    {
    }


    /**
     * Removes listeners from UI fields
     */
    protected void removeListeners()
    {
    }


    /**
     * Initializes the UI fields from the input.
     */
    protected void fillInUiFields()
    {
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        removeListeners();

        super.dispose();
    }


    /**
     * Refreshes the UI.
     */
    public void refreshUI()
    {
        if ( initialized )
        {
            removeListeners();
            fillInUiFields();
            addListeners();
        }
    }
}
