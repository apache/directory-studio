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
    boolean addAuxiliaryObjectClass( String objectClass );


    /**
     * Gets the auxiliary object classes.
     *
     * @return
     *      the auxiliary object classes
     */
    List<String> getAuxiliaryObjectClasses();


    /**
     * Gets the form.
     *
     * @return
     *      the form
     */
    TemplateForm getForm();


    /**
     * Gets the ID.
     * 
     * @return
     *      the ID
     */
    String getId();


    /**
     * Gets the structural object class.
     * 
     * @return
     *      the structural object class
     */
    String getStructuralObjectClass();


    /**
     * Gets the title.
     * 
     * @return
     *      the title
     */
    String getTitle();


    /**
     * Removes an auxiliary object class.
     *
     * @param objectClass
     *      the auxiliary object class
     * @return
     *      <code>true</code> if the template contained the specified element.
     */
    boolean removeAuxiliaryObjectClass( String objectClass );


    /**
     * Sets the auxiliary object classes.
     *
     * @param objectClasses
     *      the auxiliary object classes
     */
    void setAuxiliaryObjectClasses( List<String> objectClasses );


    /**
     * Sets the form.
     *
     * @param form
     *      the form
     */
    void setForm( TemplateForm form );


    /**
     * Sets the ID.
     *
     * @param id
     *      the ID
     */
    void setId( String id );


    /**
     * Sets the structural object class.
     * 
     * @param objectClass
     *      the structural object class
     */
    void setStructuralObjectClass( String objectClass );


    /**
     * Sets the title.
     *
     * @param title
     *      the title
     */
    void setTitle( String title );
}
