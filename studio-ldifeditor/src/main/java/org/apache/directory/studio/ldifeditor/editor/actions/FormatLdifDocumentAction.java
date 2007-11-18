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


import org.apache.directory.studio.ldapbrowser.core.utils.Utils;
import org.apache.directory.studio.ldifeditor.editor.LdifEditor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;


public class FormatLdifDocumentAction extends AbstractLdifAction
{

    public FormatLdifDocumentAction( LdifEditor editor )
    {
        super( "Format Document", editor );
    }


    protected void doRun()
    {
        IDocument document = editor.getDocumentProvider().getDocument( editor.getEditorInput() );
        ISourceViewer sourceViewer = ( ISourceViewer ) editor.getAdapter( ISourceViewer.class );
        int topIndex = sourceViewer.getTopIndex();
        document.set( super.getLdifModel().toFormattedString( Utils.getLdifFormatParameters() ) );
        sourceViewer.setTopIndex( topIndex );
    }


    public void update()
    {
    }

}
