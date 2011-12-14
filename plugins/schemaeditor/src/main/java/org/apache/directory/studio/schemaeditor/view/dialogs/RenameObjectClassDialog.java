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
package org.apache.directory.studio.schemaeditor.view.dialogs;


import java.util.List;

import org.eclipse.swt.widgets.Shell;


/**
 * This dialog is used to rename an object class.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class RenameObjectClassDialog extends EditAttributeTypeAliasesDialog
{
    /**
     * Creates a new instance of RenameObjectClassDialog.
     *
     * @param aliases an array of aliases
     */
    public RenameObjectClassDialog( List<String> aliases )
    {
        super( aliases );
    }


    /**
     * {@inheritDoc}
     */
    protected void configureShell( Shell newShell )
    {
        super.configureShell( newShell );
        newShell.setText( Messages.getString( "RenameObjectClassDialog.RenameObjectClass" ) ); //$NON-NLS-1$
    }
}
