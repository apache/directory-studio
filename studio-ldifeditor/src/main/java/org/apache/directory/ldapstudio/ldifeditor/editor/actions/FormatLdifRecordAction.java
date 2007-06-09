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

package org.apache.directory.ldapstudio.ldifeditor.editor.actions;


import org.apache.directory.ldapstudio.ldifeditor.editor.LdifEditor;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.container.LdifContainer;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.container.LdifRecord;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;


public class FormatLdifRecordAction extends AbstractLdifAction
{

    public FormatLdifRecordAction( LdifEditor editor )
    {
        super( "Format Record", editor );
    }


    protected void doRun()
    {

        LdifContainer[] containers = super.getSelectedLdifContainers();
        if ( containers.length > 0 )
        {
            IDocument document = editor.getDocumentProvider().getDocument( editor.getEditorInput() );
            String old = document.get();
            StringBuffer sb = new StringBuffer();
            sb.append( old.substring( 0, containers[0].getOffset() ) );

            for ( int i = 0; i < containers.length; i++ )
            {
                LdifContainer container = containers[i];
                sb.append( container.toFormattedString() );
            }

            sb.append( old.substring( containers[containers.length - 1].getOffset()
                + containers[containers.length - 1].getLength(), old.length() ) );

            ISourceViewer sourceViewer = ( ISourceViewer ) editor.getAdapter( ISourceViewer.class );
            int topIndex = sourceViewer.getTopIndex();
            document.set( sb.toString() );
            sourceViewer.setTopIndex( topIndex );
        }
    }


    public void update()
    {
        LdifContainer[] ldifContainers = super.getSelectedLdifContainers();
        for ( int i = 0; i < ldifContainers.length; i++ )
        {
            LdifContainer container = ldifContainers[i];
            if ( !( container instanceof LdifRecord ) )
            {
                super.setEnabled( false );
                return;
            }
        }

        super.setEnabled( true );
    }

}
