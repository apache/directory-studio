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
package org.apache.directory.studio.test.integration.ui.bots;


import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;


public class NewWizardBot extends WizardBot
{

    public NewWizardBot()
    {
        super( "Select a wizard" );
    }


    public void selectLdifFile()
    {
        select( "LDAP Browser", "LDIF File" );
    }


    public void select( String parent, String child )
    {
        SWTBotTree tree = bot.tree();
        tree.expandNode( parent ).select( child );
    }

}
