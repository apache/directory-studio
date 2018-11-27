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
public class TemplateCheckbox extends AbstractTemplateWidget
{
    /** The default enabled value */
    public static boolean DEFAULT_ENABLED = true;

    /** The default enabled value */
    public static String DEFAULT_LABEL = ""; //$NON-NLS-1$

    /** The default checked value value */
    public static String DEFAULT_CHECKED_VALUE = null;

    /** The default unchecked value value */
    public static String DEFAULT_UNCHECKED_VALUE = null;

    /** The label associated with the checkbox */
    private String label = DEFAULT_LABEL;

    /** The enabled flag */
    private boolean enabled = DEFAULT_ENABLED;

    /** The value when the checkbox is checked */
    private String checkedValue = DEFAULT_CHECKED_VALUE;

    /** The value when the checkbox is unchecked */
    private String uncheckedValue = DEFAULT_UNCHECKED_VALUE;


    /**
     * Creates a new instance of TemplateCheckbox.
     *
     * @param parent
     *      the parent element
     */
    public TemplateCheckbox( TemplateWidget parent )
    {
        super( parent );
    }


    /**
     * Get the value when the checkbox is checked.
     *
     * @return
     *      the value when the checkbox is checked
     */
    public String getCheckedValue()
    {
        return checkedValue;
    }


    /**
     * Gets the label.
     *
     * @return
     *      the label
     */
    public String getLabel()
    {
        return label;
    }


    /**
     * Gets the value when the checkbox is not checked.
     *
     * @return
     *      the value when the checkbox is not checked
     */
    public String getUncheckedValue()
    {
        return uncheckedValue;
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
     * Sets the value when the checkbox is checked.
     *
     * @param checkedValue
     *      the value
     */
    public void setCheckedValue( String checkedValue )
    {
        this.checkedValue = checkedValue;
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


    /**
     * Sets the label.
     *
     * @param label
     *      the label
     */
    public void setLabel( String label )
    {
        this.label = label;
    }


    /**
     * Sets the value when the checkbox is not checked.
     *
     * @param uncheckedValue
     *      the value
     */
    public void setUncheckedValue( String uncheckedValue )
    {
        this.uncheckedValue = uncheckedValue;
    }
}
