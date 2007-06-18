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
package org.apache.directory.studio.apacheds.schemaeditor.model.difference;


/**
 * This interface defines a Difference between two objects.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface Difference
{
    public enum DifferenceType
    {
        ADD_ALIAS, REMOVE_ALIAS, 
        ADD_DESCRIPTION, MODIFY_DESCRIPTION, REMOVE_DRESCRIPTION, 
        ADD_SUPERIOR_AT, MODIFY_SUPERIOR_AT, REMOVE_SUPERIOR_AT, 
        ADD_SUPERIOR_OC, REMOVE_SUPERIOR_OC, 
        ADD_USAGE, MODIFY_USAGE, REMOVE_USAGE, 
        ADD_CLASS_TYPE, MODIFY_CLASS_TYPE, REMOVE_CLASS_TYPE, 
        ADD_SYNTAX, MODIFY_SYNTAX, REMOVE_SYNTAX, 
        ADD_SYNTAX_LENGTH, MODIFY_SYNTAX_LENGTH, REMOVE_SYNTAX_LENGTH, 
        SET_OBSOLETE,
        SET_SINGLE_VALUE,
        SET_COLLECTIVE,
        SET_NO_USER_MODIFICATION,
        ADD_EQUALITY, MODIFY_EQUALITY, REMOVE_EQUALITY, 
        ADD_ORDERING, MODIFY_ORDERING, REMOVE_ORDERING, 
        ADD_SUBSTRING, MODIFY_SUBSTRING, REMOVE_SUBSTRING, 
        ADD_MANDATORY_AT, REMOVE_MANDATORY_AT, 
        ADD_OPTIONAL_AT, REMOVE_OPTIONAL_AT
    }


    /**
     * Gets the source Object.
     *
     * @return
     *      the source Object
     */
    public Object getSource();


    /**
     * Sets the source Object.
     *
     * @param source
     *      the source Object
     */
    public void setSource( Object source );


    /**
     * Gets the destination Object.
     *
     * @return
     *      the destination Object
     */
    public Object getDestination();


    /**
     * Sets the destination Object.
     *
     * @param destination
     *      the destination Object
     */
    public void setDestination( Object destination );
}
