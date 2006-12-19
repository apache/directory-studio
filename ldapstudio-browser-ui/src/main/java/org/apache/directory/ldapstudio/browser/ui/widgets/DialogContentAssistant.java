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

package org.apache.directory.ldapstudio.browser.ui.widgets;


import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.contentassist.ComboContentAssistSubjectAdapter;
import org.eclipse.jface.contentassist.SubjectControlContentAssistant;
import org.eclipse.jface.contentassist.TextContentAssistSubjectAdapter;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;


public class DialogContentAssistant extends SubjectControlContentAssistant implements FocusListener
{

    private Control control;

    private IHandlerActivation handlerActivation;

    private boolean possibleCompletionsVisible;


    public DialogContentAssistant()
    {
        super();
        this.possibleCompletionsVisible = false;
    }


    public void install( Text text )
    {
        this.control = text;
        this.control.addFocusListener( this );
        super.install( new TextContentAssistSubjectAdapter( text ) );
    }


    public void install( Combo combo )
    {
        this.control = combo;
        this.control.addFocusListener( this );
        super.install( new ComboContentAssistSubjectAdapter( combo ) );
    }


    public void install( ITextViewer viewer )
    {
        this.control = viewer.getTextWidget();
        this.control.addFocusListener( this );

        // stop traversal (ESC) if popup is shown
        this.control.addTraverseListener( new TraverseListener()
        {
            public void keyTraversed( TraverseEvent e )
            {
                if ( possibleCompletionsVisible )
                {
                    e.doit = false;
                }
            }
        } );

        super.install( viewer );
    }


    public void uninstall()
    {
        if ( this.handlerActivation != null )
        {
            IHandlerService handlerService = ( IHandlerService ) PlatformUI.getWorkbench().getAdapter(
                IHandlerService.class );
            handlerService.deactivateHandler( this.handlerActivation );
            this.handlerActivation = null;
        }

        if ( this.control != null )
        {
            this.control.removeFocusListener( this );
        }

        super.uninstall();
    }


    protected Point restoreCompletionProposalPopupSize()
    {
        possibleCompletionsVisible = true;
        return super.restoreCompletionProposalPopupSize();
    }


    public String showPossibleCompletions()
    {
        possibleCompletionsVisible = true;
        return super.showPossibleCompletions();
    }


    protected void possibleCompletionsClosed()
    {
        this.possibleCompletionsVisible = false;
        super.possibleCompletionsClosed();
    }


    public void focusGained( FocusEvent e )
    {
        IHandlerService handlerService = ( IHandlerService ) PlatformUI.getWorkbench().getAdapter(
            IHandlerService.class );
        if ( handlerService != null )
        {
            IHandler handler = new org.eclipse.core.commands.AbstractHandler()
            {
                public Object execute( ExecutionEvent event ) throws org.eclipse.core.commands.ExecutionException
                {
                    showPossibleCompletions();
                    return null;
                }
            };
            this.handlerActivation = handlerService.activateHandler(
                ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS, handler );
        }
    }


    public void focusLost( FocusEvent e )
    {
        if ( this.handlerActivation != null )
        {
            IHandlerService handlerService = ( IHandlerService ) PlatformUI.getWorkbench().getAdapter(
                IHandlerService.class );
            handlerService.deactivateHandler( this.handlerActivation );
            this.handlerActivation = null;
        }
    }

}
