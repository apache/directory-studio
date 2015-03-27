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


import java.util.HashSet;
import java.util.Set;


/**
 * This class implements a template file chooser.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class TemplateFileChooser extends AbstractTemplateWidget
{
    /** The default icon value */
    public static String DEFAULT_ICON = null;

    /** The default show save icon value */
    public static boolean DEFAULT_SHOW_ICON = true;

    /** The default show save as button value */
    public static boolean DEFAULT_SHOW_SAVE_AS_BUTTON = true;

    /** The default show clear button value */
    public static boolean DEFAULT_SHOW_CLEAR_BUTTON = true;

    /** The default show browse button value */
    public static boolean DEFAULT_SHOW_BROWSE_BUTTON = true;

    /** The icon */
    private String icon = DEFAULT_ICON;

    /** The set of extensions for the file */
    private Set<String> extensions = new HashSet<String>();

    /** The flag which indicates if an should be shown */
    private boolean showIcon = DEFAULT_SHOW_ICON;

    /** The flag which indicates if a "<em>Save As...</em>" button should be shown */
    private boolean showSaveAsButton = DEFAULT_SHOW_SAVE_AS_BUTTON;

    /** The flag which indicates if a "<em>Clear</em>" button should be shown */
    private boolean showClearButton = DEFAULT_SHOW_CLEAR_BUTTON;

    /** The flag which indicates if a "<em>Browse...</em>" button should be shown */
    private boolean showBrowseButton = DEFAULT_SHOW_BROWSE_BUTTON;


    /**
     * Creates a new instance of TemplateFileChooser.
     *
     * @param parent
     *      the parent element
     */
    public TemplateFileChooser( TemplateWidget parent )
    {
        super( parent );
    }


    /**
     * Adds an extension.
     *
     * @param extension
     *      the extension
     * @return
     *      <code>true</code> if the template file chooser did not already 
     *      contain the specified element.
     */
    public boolean addExtension( String extension )
    {
        return extensions.add( extension );
    }


    /**
     * Gets the extensions.
     *
     * @return
     *      the extensions
     */
    public Set<String> getExtensions()
    {
        return extensions;
    }


    /**
     * Gets the icon.
     *
     * @return
     *      the icon
     */
    public String getIcon()
    {
        return icon;
    }


    /**
     * Indicates if a "<em>Browse...</em>" button should be shown.
     *
     * @return
     *      <code>true</code> if a "<em>Browse...</em>" button should be 
     *      shown, <code>false</code> if not.
     */
    public boolean isShowBrowseButton()
    {
        return showBrowseButton;
    }


    /**
     * Indicates if a "<em>Clear</em>" button should be shown.
     *
     * @return
     *      <code>true</code> if a "<em>Clear</em>" button should be 
     *      shown, <code>false</code> if not.
     */
    public boolean isShowClearButton()
    {
        return showClearButton;
    }


    /**
     * Indicates if an icon should be shown.
     *
     * @return
     *      <code>true</code> if an icon should be shown, 
     *      <code>false</code> if not.
     */
    public boolean isShowIcon()
    {
        return showIcon;
    }


    /**
     * Indicates if a "<em>Save As...</em>" button should be shown.
     *
     * @return
     *      <code>true</code> if a "<em>Save As...</em>" button should be 
     *      shown, <code>false</code> if not.
     */
    public boolean isShowSaveAsButton()
    {
        return showSaveAsButton;
    }


    /**
     * Set the extensions.
     *
     * @param extensions
     *      the extensions
     */
    public void setExtensions( Set<String> extensions )
    {
        this.extensions = extensions;
    }


    /**
     * Sets the icon.
     *
     * @param icon
     *      the icon
     */
    public void setIcon( String icon )
    {
        this.icon = icon;
    }


    /**
     * Sets the flag which indicates if a "<em>Browse...</em>" button should 
     * be shown.
     *
     * @param showBrowseButton
     *      <code>true</code> if a "<em>Browse...</em>" button should be 
     *      shown, <code>false</code> if not.
     */
    public void setShowBrowseButton( boolean showBrowseButton )
    {
        this.showBrowseButton = showBrowseButton;
    }


    /**
     * Sets the flag which indicates if a "<em>Clear</em>" button should 
     * be shown.
     *
     * @param showClearButton
     *      <code>true</code> if a "<em>Clear</em>" button should be 
     *      shown, <code>false</code> if not.
     */
    public void setShowClearButton( boolean showClearButton )
    {
        this.showClearButton = showClearButton;
    }


    /**
     * Sets the flag which indicates if an icon should be shown.
     *
     * @param showIcon
     *      <code>true</code> if an icon should be shown, 
     *      <code>false</code> if not.
     */
    public void setShowIcon( boolean showIcon )
    {
        this.showIcon = showIcon;
    }


    /**
     * Sets the flag which indicates if a "<em>Save As...</em>" button should 
     * be shown.
     *
     * @param showSaveAsButton
     *      <code>true</code> if a "<em>Save As...</em>" button should be 
     *      shown, <code>false</code> if not.
     */
    public void setShowSaveAsButton( boolean showSaveAsButton )
    {
        this.showSaveAsButton = showSaveAsButton;
    }
}
