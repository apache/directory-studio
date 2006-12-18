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

package org.apache.directory.ldapstudio.browser.ui.editors.ldif;


import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.texteditor.BasicTextEditorActionContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.RetargetTextEditorAction;


public class LdifEditorContributor extends BasicTextEditorActionContributor
{

    private static final String CONTENTASSIST_ACTION = "org.apache.directory.ldapstudio.browser.ContentAssist";

    private RetargetTextEditorAction contentAssist;


    public LdifEditorContributor()
    {
        super();

        contentAssist = new RetargetTextEditorAction( BrowserUIPlugin.getDefault().getResourceBundle(),
            "ContentAssistProposal." );
        contentAssist.setActionDefinitionId( ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS );
    }


    public void setActiveEditor( IEditorPart part )
    {
        super.setActiveEditor( part );
        ITextEditor editor = ( part instanceof ITextEditor ) ? ( ITextEditor ) part : null;
        contentAssist.setAction( getAction( editor, CONTENTASSIST_ACTION ) );
    }


    public void init( IActionBars bars, IWorkbenchPage page )
    {
        super.init( bars, page );
        bars.setGlobalActionHandler( CONTENTASSIST_ACTION, contentAssist );
    }


    public void dispose()
    {
        setActiveEditor( null );
        super.dispose();
    }

}
