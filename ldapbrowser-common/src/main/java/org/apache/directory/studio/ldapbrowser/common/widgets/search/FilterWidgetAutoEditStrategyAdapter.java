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

package org.apache.directory.studio.ldapbrowser.common.widgets.search;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.ldapbrowser.common.filtereditor.FilterAutoEditStrategy;
import org.apache.directory.studio.ldapbrowser.common.filtereditor.FilterAutoEditStrategy.AutoEditParameters;
import org.apache.directory.studio.ldapbrowser.core.model.filter.parser.LdapFilterParser;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;


/**
 * The FilterWidgetAutoEditStrategyAdapter is used to integrate the {@link FilterAutoEditStrategy} 
 * into an combo field.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class FilterWidgetAutoEditStrategyAdapter
{

    /** The auto edit strategy. */
    private FilterAutoEditStrategy autoEditStrategy;

    /** The combo. */
    private Combo combo;

    /** The old texts. */
    private List<String> oldTexts;

    /** The verify events. */
    private List<VerifyEvent> verifyEvents;

    /** The in apply combo customization flag. */
    private boolean inApplyComboCustomization;


    /**
     * Creates a new instance of FilterWidgetAutoEditStrategyAdapter.
     * 
     * @param combo the combo
     * @param parser the filter parser
     */
    public FilterWidgetAutoEditStrategyAdapter( Combo combo, LdapFilterParser parser )
    {
        this.combo = combo;

        this.oldTexts = new ArrayList<String>();
        this.verifyEvents = new ArrayList<VerifyEvent>();
        this.inApplyComboCustomization = false;

        this.autoEditStrategy = new FilterAutoEditStrategy( parser );
        combo.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                prepareComboCustomization( e );
            }
        } );
        combo.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                applyComboCustomization( e );
            }
        } );
    }


    /**
     * Prepares combo customization.
     * 
     * @param e the verify event
     */
    public void prepareComboCustomization( VerifyEvent e )
    {
        if ( !inApplyComboCustomization )
        {
            String oldText = combo.getText();
            //parser.parse( oldText );

            if( !oldTexts.isEmpty() )
            {
                oldTexts.clear();
                verifyEvents.clear();
            }
            oldTexts.add( oldText );
            verifyEvents.add( e );
        }
    }


    /**
     * Applies combo customization.
     * 
     * @param e the modify event
     */
    public void applyComboCustomization( ModifyEvent e )
    {
        if ( !inApplyComboCustomization && !verifyEvents.isEmpty() )
        {
            String oldText = oldTexts.remove( 0 );
            VerifyEvent verifyEvent = verifyEvents.remove( 0 );
            inApplyComboCustomization = true;

            // extract modification details
            String text = verifyEvent.text;
            int offset = verifyEvent.start <= verifyEvent.end ? verifyEvent.start : verifyEvent.end;
            int length = verifyEvent.start <= verifyEvent.end ? verifyEvent.end - verifyEvent.start : verifyEvent.start
                - verifyEvent.end;

            // apply auto edit strategy
            AutoEditParameters autoEditParameters = new AutoEditParameters( text, offset, length, -1, true );
            autoEditStrategy.customizeAutoEditParameters( oldText, autoEditParameters );

            // get current selection
            Point oldSelection = combo.getSelection();

            // compose new text
            String newText = ""; //$NON-NLS-1$
            newText += oldText.substring( 0, autoEditParameters.offset );
            newText += autoEditParameters.text;
            newText += oldText.substring( autoEditParameters.offset + autoEditParameters.length, oldText.length() );

            // determine new cursor position
            Point newSelection;
            if ( autoEditParameters.caretOffset != -1 )
            {
                int x = autoEditParameters.caretOffset;
                newSelection = new Point( x, x );
            }
            else
            {
                newSelection = new Point( oldSelection.x, oldSelection.y );
            }

            // set new text and cursor position
            if ( verifyEvents.isEmpty() )
            {
                combo.setText( newText );
                combo.setSelection( newSelection );
            }

            inApplyComboCustomization = false;
        }

    }
}
