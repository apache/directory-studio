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
package org.apache.directory.studio.templateeditor.model;


import java.util.List;

import org.apache.directory.studio.templateeditor.model.widgets.TemplateForm;


/**
 * This interface defines a template.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface Template
{
    /**
     * Adds an auxiliary object class.
     *
     * @param objectClass
     *      the auxiliary object class
     * @return
     *      <code>true</code> if the template did not already contain the 
     *      specified element.
     */
    public boolean addAuxiliaryObjectClass( String objectClass );


    /**
     * Gets the auxiliary object classes.
     *
     * @return
     *      the auxiliary object classes
     */
    public List<String> getAuxiliaryObjectClasses();


    /**
     * Gets the form.
     *
     * @return
     *      the form
     */
    public TemplateForm getForm();


    /**
     * Gets the ID.
     * 
     * @return
     *      the ID
     */
    public String getId();


    /**
     * Gets the structural object class.
     * 
     * @return
     *      the structural object class
     */
    public String getStructuralObjectClass();


    /**
     * Gets the title.
     * 
     * @return
     *      the title
     */
    public String getTitle();


    /**
     * Removes an auxiliary object class.
     *
     * @param objectClass
     *      the auxiliary object class
     * @return
     *      <code>true</code> if the template contained the specified element.
     */
    public boolean removeAuxiliaryObjectClass( String objectClass );


    /**
     * Sets the auxiliary object classes.
     *
     * @param objectClasses
     *      the auxiliary object classes
     */
    public void setAuxiliaryObjectClasses( List<String> objectClasses );


    /**
     * Sets the form.
     *
     * @param form
     *      the form
     */
    public void setForm( TemplateForm form );


    /**
     * Sets the ID.
     *
     * @param id
     *      the ID
     */
    public void setId( String id );


    /**
     * Sets the structural object class.
     * 
     * @param objectClass
     *      the structural object class
     */
    public void setStructuralObjectClass( String objectClass );


    /**
     * Sets the title.
     *
     * @param title
     *      the title
     */
    public void setTitle( String title );
}
