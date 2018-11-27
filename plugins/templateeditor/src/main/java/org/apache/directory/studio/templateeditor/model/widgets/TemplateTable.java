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
package org.apache.directory.studio.templateeditor.model.widgets;


/**
 * This class implements a template checkbox.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class TemplateTable extends AbstractTemplateWidget
{
    /** The default show add button value */
    public static boolean DEFAULT_SHOW_ADD_BUTTON = true;

    /** The default show edit button value */
    public static boolean DEFAULT_SHOW_EDIT_BUTTON = true;

    /** The default show delete button value */
    public static boolean DEFAULT_SHOW_DELETE_BUTTON = true;

    /** The flag which indicated if a "<em>Add...</em>" button should be shown */
    private boolean showAddButton = DEFAULT_SHOW_ADD_BUTTON;

    /** The flag which indicated if a "<em>Edit...</em>" button should be shown */
    private boolean showEditButton = DEFAULT_SHOW_EDIT_BUTTON;

    /** The flag which indicated if a "<em>Delete...</em>" button should be shown */
    private boolean showDeleteButton = DEFAULT_SHOW_DELETE_BUTTON;


    /**
     * Creates a new instance of TemplateTable.
     *
     * @param parent
     *      the parent element
     */
    public TemplateTable( TemplateWidget parent )
    {
        super( parent );
    }


    /**
     * Indicates if a "<em>Add...</em>" button should be shown.
     *
     * @return
     *      <code>true</code> if a "<em>Add...</em>" button should be 
     *      shown, <code>false</code> if not.
     */
    public boolean isShowAddButton()
    {
        return showAddButton;
    }


    /**
     * Indicates if a "<em>Delete...</em>" button should be shown.
     *
     * @return
     *      <code>true</code> if a "<em>Delete...</em>" button should be 
     *      shown, <code>false</code> if not.
     */
    public boolean isShowDeleteButton()
    {
        return showDeleteButton;
    }


    /**
     * Indicates if a "<em>Edit...</em>" button should be shown.
     *
     * @return
     *      <code>true</code> if a "<em>Edit...</em>" button should be 
     *      shown, <code>false</code> if not.
     */
    public boolean isShowEditButton()
    {
        return showEditButton;
    }


    /**
    * Sets the flag which indicates if a "<em>Add...</em>" button should 
    * be shown.
    *
    * @param showAddButton
    *      <code>true</code> if a "<em>Add...</em>" button should be 
    *      shown, <code>false</code> if not.
    */
    public void setShowAddButton( boolean showAddButton )
    {
        this.showAddButton = showAddButton;
    }


    /**
    * Sets the flag which indicates if a "<em>Delete...</em>" button should 
    * be shown.
    *
    * @param showDeleteButton
    *      <code>true</code> if a "<em>Delete...</em>" button should be 
    *      shown, <code>false</code> if not.
    */
    public void setShowDeleteButton( boolean showDeleteButton )
    {
        this.showDeleteButton = showDeleteButton;
    }


    /**
     * Sets the flag which indicates if a "<em>Edit...</em>" button should 
     * be shown.
     *
     * @param showEditButton
     *      <code>true</code> if a "<em>Edit...</em>" button should be 
     *      shown, <code>false</code> if not.
     */
    public void setShowEditButton( boolean showEditButton )
    {
        this.showEditButton = showEditButton;
    }
}
