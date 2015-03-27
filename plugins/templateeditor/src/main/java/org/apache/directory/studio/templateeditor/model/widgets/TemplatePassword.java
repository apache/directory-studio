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
public class TemplatePassword extends AbstractTemplateWidget
{
    /** The default hidden value */
    public static boolean DEFAULT_HIDDEN = true;

    /** The default show edit button value */
    public static boolean DEFAULT_SHOW_EDIT_BUTTON = true;

    /** The default show password checkbox value */
    public static boolean DEFAULT_SHOW_PASSWORD_CHECKBOX = true;

    /** The flag which indicated if the password should be hidden */
    private boolean hidden = DEFAULT_HIDDEN;

    /** The flag which indicated if a "<em>Edit...</em>" button should be shown */
    private boolean showEditButton = DEFAULT_SHOW_EDIT_BUTTON;

    /** The flag which indicated if a "<em>Show Password</em>" checkbox should be shown */
    private boolean showShowPasswordCheckbox = DEFAULT_SHOW_PASSWORD_CHECKBOX;


    /**
     * Creates a new instance of TemplatePassword.
     *
     * @param parent
     *      the parent element
     */
    public TemplatePassword( TemplateWidget parent )
    {
        super( parent );
    }


    /**
     * Indicates if the password should be displayed hidden 
     * ("&bull;&bull;&bull;&bull;") or not.
     *
     * @return
     *      <code>true</code> if should be displayed hidden 
     * ("&bull;&bull;&bull;&bull;"), <code>false</code> if not.
     */
    public boolean isHidden()
    {
        return hidden;
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
     * Indicates if a "<em>Show Password</em>" checkbox should be shown.
     *
     * @return
     *      <code>true</code> if a "<em>Show Password</em>" checkbox should be 
     *      shown, <code>false</code> if not.
     */
    public boolean isShowShowPasswordCheckbox()
    {
        return showShowPasswordCheckbox;
    }


    /**
     * Sets the flag which indicates if the password should be displayed hidden
     * ("&bull;&bull;&bull;&bull;") or not.
     *
     * @param showBrowseButton
     *      <code>true</code> if a "<em>Change...</em>" button should be 
     *      shown, <code>false</code> if not.
     */
    public void setHidden( boolean hidden )
    {
        this.hidden = hidden;
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


    /**
     * Sets the flag which indicated if a "<em>Show Password</em>" checkbox 
     * should be shown.
     *
     * @param showShowPasswordCheckbox
     *      <code>true</code> if a "<em>Show Password</em>" checkbox should 
     *      be shown,
     *      <code>false</code> if not.
     */
    public void setShowShowPasswordCheckbox( boolean showShowPasswordCheckbox )
    {
        this.showShowPasswordCheckbox = showShowPasswordCheckbox;
    }
}
