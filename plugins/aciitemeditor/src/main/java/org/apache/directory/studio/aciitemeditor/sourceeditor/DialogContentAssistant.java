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
package org.apache.directory.studio.aciitemeditor.sourceeditor;


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


/**
 * This class implements the Content Assistant Dialog used in the ACI Item 
 * Source Editor for displaying proposals
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DialogContentAssistant extends SubjectControlContentAssistant implements FocusListener
{
    private Control control;

    private IHandlerActivation handlerActivation;

    private boolean possibleCompletionsVisible;


    /**
     * Creates a new instance of DialogContentAssistant.
     */
    public DialogContentAssistant()
    {
        super();
        this.possibleCompletionsVisible = false;
    }


    /**
     * Installs content assist support on the given subject.
     *
     * @param text
     *      the one who requests content assist
     */
    public void install( Text text )
    {
        this.control = text;
        this.control.addFocusListener( this );
        super.install( new TextContentAssistSubjectAdapter( text ) );
    }


    /**
     * Installs content assist support on the given subject.
     *
     * @param combo
     *      the one who requests content assist
     */
    public void install( Combo combo )
    {
        this.control = combo;
        this.control.addFocusListener( this );
        super.install( new ComboContentAssistSubjectAdapter( combo ) );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.text.contentassist.ContentAssistant#install(org.eclipse.jface.text.ITextViewer)
     */
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


    /* (non-Javadoc)
     * @see org.eclipse.jface.text.contentassist.ContentAssistant#uninstall()
     */
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


    /* (non-Javadoc)
     * @see org.eclipse.jface.text.contentassist.ContentAssistant#restoreCompletionProposalPopupSize()
     */
    protected Point restoreCompletionProposalPopupSize()
    {
        possibleCompletionsVisible = true;
        return super.restoreCompletionProposalPopupSize();
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.text.contentassist.ContentAssistant#showPossibleCompletions()
     */
    public String showPossibleCompletions()
    {
        possibleCompletionsVisible = true;
        return super.showPossibleCompletions();
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.text.contentassist.ContentAssistant#possibleCompletionsClosed()
     */
    protected void possibleCompletionsClosed()
    {
        this.possibleCompletionsVisible = false;
        super.possibleCompletionsClosed();
    }


    /* (non-Javadoc)
     * @see org.eclipse.swt.events.FocusListener#focusGained(org.eclipse.swt.events.FocusEvent)
     */
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


    /* (non-Javadoc)
     * @see org.eclipse.swt.events.FocusListener#focusLost(org.eclipse.swt.events.FocusEvent)
     */
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
