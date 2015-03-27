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
package org.apache.directory.studio.templateeditor;


import org.apache.directory.studio.templateeditor.model.Template;


/**
 * This interface defines a listener for the templates manager events.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface TemplatesManagerListener
{
    /**
     * This method is fired when a file template is added to the
     * templates manager.
     *
     * @param template
     *      the added template
     */
    public void templateAdded( Template template );


    /**
     * This method is fired when a file template is removed from the
     * templates manager.
     *
     * @param template
     *      the removed template
     */
    public void templateRemoved( Template template );


    /**
     * This method is fired when a template is enabled.
     *
     * @param template
     *      the enabled template
     */
    public void templateEnabled( Template template );


    /**
     * This method is fired when a template is disabled.
     *
     * @param template
     *      the disabled template
     */
    public void templateDisabled( Template template );
}
