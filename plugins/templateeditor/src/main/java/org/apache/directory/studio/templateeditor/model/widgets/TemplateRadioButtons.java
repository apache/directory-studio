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
 * This class implements templates radio buttons.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class TemplateRadioButtons extends AbstractTemplateWidget
{
    /** The default enabled value */
    public static boolean DEFAULT_ENABLED = true;

    /** The enable flag */
    private boolean enabled = DEFAULT_ENABLED;

    /** The list of buttons */
    private List<ValueItem> buttons = new ArrayList<ValueItem>();


    /**
     * Creates a new instance of TemplateRadioButtons.
     *
     * @param parent
     *      the parent element
     */
    public TemplateRadioButtons( TemplateWidget parent )
    {
        super( parent );
    }


    /**
     * Adds a button.
     *
     * @param button
     *      the button
     * @return
     *      <code>true</code> if the radio buttons did not already 
     *      contain the specified element.
     */
    public boolean addButton( ValueItem button )
    {
        return buttons.add( button );
    }


    /**
     * Gets the buttons.
     *
     * @return
     *      the buttons
     */
    public List<ValueItem> getButtons()
    {
        return buttons;
    }


    /**
     * Indicates if the the checkbox is enabled.
     *
     * @return
     *      <code>true</code> if the checkbox is enabled,
     *      <code>false</code> if the checkbox is disabled
     */
    public boolean isEnabled()
    {
        return enabled;
    }


    /**
     * Sets the buttons.
     *
     * @param buttons
     *      the buttons
     */
    public void setButtons( List<ValueItem> buttons )
    {
        this.buttons = buttons;
    }


    /**
     * Enables or disables the checkbox.
     *
     * @param enabled
     *      <code>true</code> if the checkbox is enabled,
     *      <code>false</code> if the checkbox is disabled
     */
    public void setEnabled( boolean enabled )
    {
        this.enabled = enabled;
    }
}
