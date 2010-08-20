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


import org.apache.directory.studio.ldifeditor.editor.LdifEditor;
import org.apache.directory.studio.ldifparser.model.LdifFile;
import org.apache.directory.studio.ldifparser.model.LdifPart;
import org.apache.directory.studio.ldifparser.model.container.LdifContainer;
import org.apache.directory.studio.ldifparser.model.container.LdifModSpec;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Point;


public abstract class AbstractLdifAction extends Action 
{

    protected LdifEditor editor;


    public AbstractLdifAction( String text, LdifEditor editor )
    {
        super( text );
        this.editor = editor;
    }


    public final void run()
    {
        if ( this.isEnabled() )
        {
            doRun();
        }
    }


    protected abstract void doRun();


    public boolean isEnabled()
    {
//        update();
        return super.isEnabled();
    }


    protected LdifFile getLdifModel()
    {
        LdifFile model = editor.getLdifModel();
        return model;
    }


    protected LdifContainer[] getSelectedLdifContainers()
    {

        LdifContainer[] containers = null;

//        ISourceViewer sourceViewer = ( ISourceViewer ) editor.getAdapter( ISourceViewer.class );
//        if ( sourceViewer != null )
//        {
//            LdifFile model = editor.getLdifModel();
//            Point selection = sourceViewer.getSelectedRange();
//            containers = LdifFile.getContainers( model, selection.x, selection.y );
//        }

        return containers != null ? containers : new LdifContainer[0];

    }


    protected LdifPart[] getSelectedLdifParts()
    {

        LdifPart[] parts = null;

//        ISourceViewer sourceViewer = ( ISourceViewer ) editor.getAdapter( ISourceViewer.class );
//        if ( sourceViewer != null )
//        {
//            LdifFile model = editor.getLdifModel();
//            Point selection = sourceViewer.getSelectedRange();
//            parts = LdifFile.getParts( model, selection.x, selection.y );
//
//        }

        return parts != null ? parts : new LdifPart[0];

    }


    protected LdifModSpec getSelectedLdifModSpec()
    {

        LdifModSpec modSpec = null;

        LdifContainer[] containers = getSelectedLdifContainers();
//        if ( containers.length == 1 )
//        {
//            ISourceViewer sourceViewer = ( ISourceViewer ) editor.getAdapter( ISourceViewer.class );
//            if ( sourceViewer != null )
//            {
//                Point selection = sourceViewer.getSelectedRange();
//                modSpec = LdifFile.getInnerContainer( containers[0], selection.x );
//            }
//        }

        return modSpec;

    }

}
