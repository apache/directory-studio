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

package org.apache.directory.ldapstudio.ldifeditor.editor.text;


import org.apache.directory.ldapstudio.browser.core.model.ldif.LdifFile;
import org.apache.directory.ldapstudio.browser.core.model.ldif.LdifPart;
import org.apache.directory.ldapstudio.browser.core.model.ldif.container.LdifContainer;
import org.apache.directory.ldapstudio.browser.core.model.ldif.lines.LdifValueLineBase;
import org.apache.directory.ldapstudio.ldifeditor.editor.ILdifEditor;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;


public class LdifTextHover implements ITextHover
{

    private ILdifEditor editor;


    public LdifTextHover( ILdifEditor editor )
    {
        this.editor = editor;
    }


    public String getHoverInfo( ITextViewer textViewer, IRegion hoverRegion )
    {

        if ( this.editor != null )
        {

            LdifContainer container = LdifFile.getContainer( this.editor.getLdifModel(), hoverRegion.getOffset() );
            if ( container != null )
            {
                LdifPart part = LdifFile.getContainerContent( container, hoverRegion.getOffset() );
                if ( part != null )
                {
                    if ( part instanceof LdifValueLineBase )
                    {
                        LdifValueLineBase line = ( LdifValueLineBase ) part;
                        if ( line.isValueTypeBase64() )
                        {
                            return line.getValueAsString();
                        }
                    }
                }
            }
        }

        return null;
    }


    public IRegion getHoverRegion( ITextViewer textViewer, int offset )
    {

        if ( this.editor != null )
        {
            return new Region( offset, 0 );
        }

        return null;
    }

}
