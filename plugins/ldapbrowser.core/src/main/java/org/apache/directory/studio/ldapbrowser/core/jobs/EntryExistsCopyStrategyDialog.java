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
package org.apache.directory.studio.ldapbrowser.core.jobs;


import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.name.Rdn;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;


/**
 * A dialog to select the copy strategy if an entry already exists.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface EntryExistsCopyStrategyDialog
{

    /**
     * Sets the existing entry.
     * 
     * @param browserConnection the browser connection
     * @param newLdapDn the new DN
     */
    void setExistingEntry( IBrowserConnection browserConnection, LdapDN newLdapDn );


    /**
     * Gets the copy strategy.
     * 
     * @return the copy strategy
     */
    EntryExistsCopyStrategy getStrategy();


    /**
     * Gets the RDN if {@link EntryExistsCopyStrategy.RENAME_AND_CONTINUE} was selected.
     * Returns null if another strategy was selected.
     * 
     * @return the RDN
     */
    Rdn getRdn();


    /**
     * Returns true to remember the selected copy strategy.
     * 
     * @return true, to remember the selected copy strategy
     */
    boolean isRememberSelection();


    /**
     * Opens the dialog.
     * 
     * @return the status code
     */
    int open();

    /**
     * Enum for the copy strategy.
     * 
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    public enum EntryExistsCopyStrategy
    {

        /** Break strategy, don't continue copy process. */
        BREAK,

        /** Ignore the entry to copy and continue the copy process. */
        IGNORE_AND_CONTINUE,

        /** Overwrite the entry to copy and continue the copy process. */
        OVERWRITE_AND_CONTINUE,

        /** Rename the entry to copy and continue the copy process. */
        RENAME_AND_CONTINUE;
    }

}
