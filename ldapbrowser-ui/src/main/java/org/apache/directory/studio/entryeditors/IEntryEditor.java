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


import org.apache.directory.studio.ldapbrowser.core.model.IEntry;


/**
 * An Entry Editor is used to display and edit an LDAP entry.  
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface IEntryEditor
{
    /** 
     * This method indicates if the editor can handle the given entry.
     * 
     * @param entry the entry.
     * 
     * @return true if this editor can handle the entry, false otherwise.
     */
    public boolean canHandle( IEntry entry );

    
    /**
     * Informs the entry editor that the working copy was modified.
     * 
     * @param source the source of the modification, may be null
     */
    public void workingCopyModified( Object source );
    

    /**
     * Gets the entry editor input.
     * 
     * @return the entry editor input, null if no input was set
     */
    public EntryEditorInput getEntryEditorInput();


    /**
     * Checks if the editor uses auto save, i.e. if each modification is
     * automatically committed to the directory server.
     * 
     * @return true, if the editor uses auto save
     */
    public boolean isAutoSave();

}
