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

package org.apache.directory.studio.ldifeditor.editor.actions;


import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldifeditor.editor.LdifEditor;


public class OpenDefaultValueEditorAction extends AbstractLdifAction
{

    private OpenBestValueEditorAction proxy;


    public OpenDefaultValueEditorAction( LdifEditor editor, OpenBestValueEditorAction proxy )
    {
        super( Messages.getString( "OpenDefaultValueEditorAction.EditValue" ), editor ); //$NON-NLS-1$
        super.setActionDefinitionId( BrowserCommonConstants.ACTION_ID_EDIT_VALUE );
        this.proxy = proxy;
    }


    public void update()
    {
        proxy.update();
        setEnabled( proxy.isEnabled() );
        setImageDescriptor( proxy.getImageDescriptor() );
    }


    protected void doRun()
    {
        proxy.run();
    }

}
