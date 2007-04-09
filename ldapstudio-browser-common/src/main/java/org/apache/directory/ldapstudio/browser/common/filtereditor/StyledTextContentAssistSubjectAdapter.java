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

package org.apache.directory.ldapstudio.browser.common.filtereditor;


import java.util.HashMap;

import org.eclipse.jface.contentassist.AbstractControlContentAssistSubjectAdapter;
import org.eclipse.jface.text.Assert;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;


public class StyledTextContentAssistSubjectAdapter extends AbstractControlContentAssistSubjectAdapter
{

    private StyledText styledText;

    private ITextViewer viewer;

    private HashMap modifyListeners;


    public StyledTextContentAssistSubjectAdapter( ITextViewer viewer )
    {
        Assert.isNotNull( viewer );
        this.styledText = viewer.getTextWidget();
        this.viewer = viewer;
        this.modifyListeners = new HashMap();
    }


    public Control getControl()
    {
        return styledText;
    }


    public int getLineHeight()
    {
        return styledText.getLineHeight();
    }


    public int getCaretOffset()
    {
        return styledText.getCaretOffset();
    }


    public Point getLocationAtOffset( int offset )
    {
        return styledText.getLocationAtOffset( offset );
    }


    public Point getWidgetSelectionRange()
    {
        return new Point( styledText.getSelection().x, Math.abs( styledText.getSelection().y
            - styledText.getSelection().x ) );
    }


    public Point getSelectedRange()
    {
        return new Point( styledText.getSelection().x, Math.abs( styledText.getSelection().y
            - styledText.getSelection().x ) );
    }


    public void setSelectedRange( int i, int j )
    {
        styledText.setSelection( new Point( i, i + j ) );
    }


    public void revealRange( int i, int j )
    {
        styledText.setSelection( new Point( i, i + j ) );
    }


    public IDocument getDocument()
    {
        return viewer.getDocument();
    }


    public boolean addSelectionListener( final SelectionListener selectionListener )
    {
        styledText.addSelectionListener( selectionListener );
        Listener listener = new Listener()
        {
            public void handleEvent( Event e )
            {
                selectionListener.widgetSelected( new SelectionEvent( e ) );
            }
        };
        styledText.addListener( SWT.Modify, listener );
        modifyListeners.put( selectionListener, listener );
        return true;
    }


    public void removeSelectionListener( SelectionListener selectionListener )
    {
        styledText.removeSelectionListener( selectionListener );
        Object listener = modifyListeners.get( selectionListener );
        if ( listener instanceof Listener )
        {
            styledText.removeListener( SWT.Modify, ( Listener ) listener );
        }
    }

}
