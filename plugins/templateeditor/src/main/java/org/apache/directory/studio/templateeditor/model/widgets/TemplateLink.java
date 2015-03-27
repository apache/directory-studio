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
public class TemplateLink extends AbstractTemplateWidget
{
    /** The default value value */
    public static String DEFAULT_VALUE = null;

    /** The label value */
    private String value = DEFAULT_VALUE;


    /**
     * Creates a new instance of TemplateLink.
     *
     * @param parent
     *      the parent element
     */
    public TemplateLink( TemplateWidget parent )
    {
        super( parent );
    }


    /**
     * Gets the value.
     *
     * @return
     *      the value
     */
    public String getValue()
    {
        return value;
    }


    /**
     * Sets the value.
     *
     * @param value
     *      the value
     */
    public void setValue( String value )
    {
        this.value = value;
    }
}
