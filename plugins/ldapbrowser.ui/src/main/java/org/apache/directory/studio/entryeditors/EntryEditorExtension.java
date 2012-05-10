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

package org.apache.directory.studio.entryeditors;


import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.resource.ImageDescriptor;


/**
 * A bean class to hold the entry editor extension point properties.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EntryEditorExtension
{
    /** The ID. */
    private String id = null;

    /** The name. */
    private String name = null;

    /** The description. */
    private String description = null;

    /** The icon. */
    private ImageDescriptor icon = null;

    /** The class name. */
    private String className = null;

    /** The editor id. */
    private String editorId = null;

    /** The multi window flag. */
    private boolean multiWindow = true;

    /** The priority. */
    private int priority = 0;

    /** The configuration element. */
    private IConfigurationElement member = null;

    /** The editor instance */
    private IEntryEditor editorInstance = null;


    /**
     * Gets the id.
     * 
     * @return the id
     */
    public String getId()
    {
        return id;
    }


    /**
     * Sets the id.
     * 
     * @param id the new id
     */
    public void setId( String id )
    {
        this.id = id;
    }


    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName()
    {
        return name;
    }


    /**
     * Sets the name.
     * 
     * @param name the new name
     */
    public void setName( String name )
    {
        this.name = name;
    }


    /**
     * Gets the description.
     * 
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }


    /**
     * Sets the description.
     * 
     * @param description the new description
     */
    public void setDescription( String description )
    {
        this.description = description;
    }


    /**
     * Gets the icon.
     * 
     * @return the icon
     */
    public ImageDescriptor getIcon()
    {
        return icon;
    }


    /**
     * Sets the icon.
     * 
     * @param icon the new icon
     */
    public void setIcon( ImageDescriptor icon )
    {
        this.icon = icon;
    }


    /**
     * Gets the class name.
     * 
     * @return the class name
     */
    public String getClassName()
    {
        return className;
    }


    /**
     * Sets the class name.
     * 
     * @param className the new class name
     */
    public void setClassName( String className )
    {
        this.className = className;
    }


    /**
     * Gets the editor id.
     * 
     * @return the editor id
     */
    public String getEditorId()
    {
        return editorId;
    }


    /**
     * Sets the editor id.
     * 
     * @param editorId the new editor id
     */
    public void setEditorId( String editorId )
    {
        this.editorId = editorId;
    }


    /**
     * Checks if is multi window.
     * 
     * @return true, if is multi window
     */
    public boolean isMultiWindow()
    {
        return multiWindow;
    }


    /**
     * Sets the multi window.
     * 
     * @param multiWindow the new multi window
     */
    public void setMultiWindow( boolean multiWindow )
    {
        this.multiWindow = multiWindow;
    }


    /**
     * Gets the priority.
     * 
     * @return the priority
     */
    public int getPriority()
    {
        return priority;
    }


    /**
     * Sets the priority.
     * 
     * @param priority the new priority
     */
    public void setPriority( int priority )
    {
        this.priority = priority;
    }


    /**
     * Gets the member.
     * 
     * @return the member
     */
    public IConfigurationElement getMember()
    {
        return member;
    }


    /**
     * Sets the member.
     * 
     * @param member the new member
     */
    public void setMember( IConfigurationElement member )
    {
        this.member = member;
    }


    /**
     * Gets the editor instance.
     *
     * @return
     *      the editor instance
     */
    public IEntryEditor getEditorInstance()
    {
        return editorInstance;
    }


    /**
     * Sets the editor instance
     *
     * @param editorInstance
     *      the editor instance
     */
    public void setEditorInstance( IEntryEditor editorInstance )
    {
        this.editorInstance = editorInstance;
    }


    @Override
    public String toString()
    {
        return "EntryEditorExtension [className=" + className + ", description=" + description + ", editorId=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            + editorId + ", icon=" + icon + ", id=" + id + ", member=" + member + ", name=" + name + ", priority=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            + priority + ", multiWindow=" + multiWindow + "]"; //$NON-NLS-1$ //$NON-NLS-2$
    }
}
