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

package org.apache.directory.studio.ldapbrowser.core.model.schema;


/**
 * A SyntaxValueEditorRelation is used to set the relation 
 * from a syntax to its value editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SyntaxValueEditorRelation
{

    /** The syntax OID. */
    private String syntaxOID;

    private String valueEditorClassName;


    /**
     * Creates a new instance of SyntaxValueEditorRelation.
     */
    public SyntaxValueEditorRelation()
    {
    }


    /**
     * Creates a new instance of SyntaxValueEditorRelation.
     * 
     * @param syntaxOID the syntax OID
     * @param valueEditorClassName the value editor class name
     */
    public SyntaxValueEditorRelation( String syntaxOID, String valueEditorClassName )
    {
        this.syntaxOID = syntaxOID;
        this.valueEditorClassName = valueEditorClassName;
    }


    /**
     * Gets the syntax OID.
     * 
     * @return the syntax OID
     */
    public String getSyntaxOID()
    {
        return syntaxOID;
    }


    /**
     * Sets the syntax OID.
     * 
     * @param syntaxOID the new syntax OID
     */
    public void setSyntaxOID( String syntaxOID )
    {
        this.syntaxOID = syntaxOID;
    }


    /**
     * Gets the value editor class name.
     * 
     * @return the value editor class name
     */
    public String getValueEditorClassName()
    {
        return valueEditorClassName;
    }


    /**
     * Sets the value editor class name.
     * 
     * @param valueEditorClassName the new value editor class name
     */
    public void setValueEditorClassName( String valueEditorClassName )
    {
        this.valueEditorClassName = valueEditorClassName;
    }

}
