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


import java.util.ArrayList;
import java.util.List;


/**
 * This class implements a template checkbox.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class TemplateListbox extends AbstractTemplateWidget
{
    /** The default show browse button value */
    public static boolean DEFAULT_MULTIPLE_SELECTION = true;

    /** The default enabled value */
    public static boolean DEFAULT_ENABLED = true;

    /** The enabled flag */
    private boolean enabled = DEFAULT_ENABLED;

    /** The flag which indicates if the listbox allows multiple selection */
    private boolean multipleSelection = DEFAULT_MULTIPLE_SELECTION;

    /** The list of value items */
    private List<ValueItem> items = new ArrayList<ValueItem>();


    /**
     * Creates a new instance of TemplateListbox.
     *
     * @param parent
     *      the parent element
     */
    public TemplateListbox( TemplateWidget parent )
    {
        super( parent );
    }


    /**
     * Adds a value.
     *
     * @param value
     *      the value
     * @return
     *      <code>true</code> if the listbox did not already 
     *      contain the specified element.
     */
    public boolean addValue( ValueItem value )
    {
        return items.add( value );
    }


    /**
     * Gets the items.
     *
     * @return
     *      the items
     */
    public List<ValueItem> getItems()
    {
        return items;
    }


    /**
     * Indicates if the the listbox is enabled.
     *
     * @return
     *      <code>true</code> if the listbox is enabled,
     *      <code>false</code> if the listbox is disabled
     */
    public boolean isEnabled()
    {
        return enabled;
    }


    /**
     * Indicates if the listbox allows multiple selection.
     *
     * @return
     *      <code>true</code> if the listbox allows multiple selection,
     *      <code>false</code> if not.
     */
    public boolean isMultipleSelection()
    {
        return multipleSelection;
    }


    /**
     * Enables or disables the listbox.
     *
     * @param enabled
     *      <code>true</code> if the listbox is enabled,
     *      <code>false</code> if the listbox is disabled
     */
    public void setEnabled( boolean enabled )
    {
        this.enabled = enabled;
    }


    /**
     * Sets the items.
     *
     * @param items
     *      the items
     */
    public void setItems( List<ValueItem> items )
    {
        this.items = items;
    }


    /**
     * Sets the flag which indicates if the listbox allows multiple selection.
     *
     * @param multipleSelection
     *      <code>true</code> if the listbox allows multiple selection,
     *      <code>false</code> if not.
     */
    public void setMultipleSelection( boolean multipleSelection )
    {
        this.multipleSelection = multipleSelection;
    }
}
