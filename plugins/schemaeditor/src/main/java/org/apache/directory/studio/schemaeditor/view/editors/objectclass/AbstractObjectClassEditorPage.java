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

package org.apache.directory.studio.schemaeditor.view.editors.objectclass;


import org.apache.directory.api.ldap.model.schema.MutableObjectClass;
import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.apache.directory.studio.schemaeditor.view.editors.AbstractSchemaObjectEditorPage;


/**
 * This abstract class defines an object class editor page.
 */
public abstract class AbstractObjectClassEditorPage extends AbstractSchemaObjectEditorPage<ObjectClassEditor>
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
    public AbstractObjectClassEditorPage( ObjectClassEditor editor, String id, String title )
    {
        super( editor, id, title );
    }


    /**
     * Gets the original object class.
     *
     * @return
     *      the original object class
     */
    public ObjectClass getOriginalObjectClass()
    {
        return getEditor().getOriginalObjectClass();
    }


    /**
     * Gets the modified object class.
     *
     * @return
     *      the modified object class
     */
    public MutableObjectClass getModifiedObjectClass()
    {
        return getEditor().getModifiedObjectClass();
    }


    /**
     * Sets the editor as dirty
     */
    protected void setEditorDirty()
    {
        getEditor().setDirty( true );
    }
}
