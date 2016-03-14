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
 * This class implements a template link.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class TemplateDate extends AbstractTemplateWidget
{
    /** The default format value */
    public static String DEFAULT_FORMAT = null;

    /** The default show edit button value */
    public static boolean DEFAULT_SHOW_EDIT_BUTTON = true;

    /** The format value */
    private String format = DEFAULT_FORMAT;

    /** The flag which indicates if an "<em>Edit...</em>" button should be shown */
    private boolean showEditButton = DEFAULT_SHOW_EDIT_BUTTON;


    /**
     * Creates a new instance of TemplateLink.
     *
     * @param parent
     *      the parent element
     */
    public TemplateDate( TemplateWidget parent )
    {
        super( parent );
    }


    /**
     * Gets the format.
     *
     * @return
     *      the format
     */
    public String getFormat()
    {
        return format;
    }


    /**
     * Indicates if an "<em>Edit...</em>" button should be shown.
     *
     * @return
     *      <code>true</code> if an "<em>Edit...</em>" button should be 
     *      shown, <code>false</code> if not.
     */
    public boolean isShowEditButton()
    {
        return showEditButton;
    }


    /**
     * Sets the format.
     *
     * @param format
     *      the format
     */
    public void setFormat( String format )
    {
        this.format = format;
    }


    /**
     * Sets the flag which indicates if an "<em>Edit...</em>" button should 
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
