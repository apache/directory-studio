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


import org.apache.directory.studio.ldapbrowser.common.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.BrowserWidget;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;


/**
 * The AliasesDereferencingWidget could be used to select the
 * alias dereferencing method. It is composed of a group with 
 * two check boxes.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class AliasesDereferencingWidget extends BrowserWidget
{

    /** The initial aliases dereferencing method */
    private int initialAliasesDereferencingMethod;

    /** The group. */
    private Group group;

    /** The finding button. */
    private Button findingButton;

    /** The search button. */
    private Button searchButton;


    /**
     * Creates a new instance of AliasesDereferencingWidget with the given
     * derefenencing method. This must be one of {@link IBrowserConnection#DEREFERENCE_ALIASES_NEVER},
     * {@link IBrowserConnection#DEREFERENCE_ALIASES_SEARCH}, {@link IBrowserConnection#DEREFERENCE_ALIASES_FINDING}
     * or {@link IBrowserConnection#DEREFERENCE_ALIASES_ALWAYS}.
     * 
     * @param initialAliasesDereferencingMethod the initial aliases dereferencing method
     */
    public AliasesDereferencingWidget( int initialAliasesDereferencingMethod )
    {
        this.initialAliasesDereferencingMethod = initialAliasesDereferencingMethod;
    }


    /**
     * Creates a new instance of AliasesDereferencingWidget. The initial 
     * dereferncing method is set to {@link IBrowserConnection#DEREFERENCE_ALIASES_NEVER}.
     */
    public AliasesDereferencingWidget()
    {
        this.initialAliasesDereferencingMethod = IBrowserConnection.DEREFERENCE_ALIASES_NEVER;
    }


    /**
     * Creates the widget.
     * 
     * @param parent the parent
     */
    public void createWidget( Composite parent )
    {

        group = BaseWidgetUtils.createGroup( parent, "Aliases Dereferencing", 1 );
        Composite groupComposite = BaseWidgetUtils.createColumnContainer( group, 1, 1 );

        findingButton = BaseWidgetUtils.createCheckbox( groupComposite, "Finding Base DN", 1 );
        findingButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                notifyListeners();
            }
        } );

        searchButton = BaseWidgetUtils.createCheckbox( groupComposite, "Search", 1 );
        searchButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                notifyListeners();
            }
        } );

        setAliasesDereferencingMethod( initialAliasesDereferencingMethod );
    }


    /**
     * Sets the aliases dereferencing method, must be one of {@link IBrowserConnection#DEREFERENCE_ALIASES_NEVER},
     * {@link IBrowserConnection#DEREFERENCE_ALIASES_SEARCH}, {@link IBrowserConnection#DEREFERENCE_ALIASES_FINDING}
     * or {@link IBrowserConnection#DEREFERENCE_ALIASES_ALWAYS}.
     * 
     * @param aliasesDereferencingMethod the aliases dereferencing method
     */
    public void setAliasesDereferencingMethod( int aliasesDereferencingMethod )
    {
        initialAliasesDereferencingMethod = aliasesDereferencingMethod;
        findingButton.setSelection( initialAliasesDereferencingMethod == IBrowserConnection.DEREFERENCE_ALIASES_FINDING
            || initialAliasesDereferencingMethod == IBrowserConnection.DEREFERENCE_ALIASES_ALWAYS );
        searchButton.setSelection( initialAliasesDereferencingMethod == IBrowserConnection.DEREFERENCE_ALIASES_SEARCH
            || initialAliasesDereferencingMethod == IBrowserConnection.DEREFERENCE_ALIASES_ALWAYS );
    }


    /**
     * Gets the aliases dereferencing method, one of {@link IBrowserConnection#DEREFERENCE_ALIASES_NEVER},
     * {@link IBrowserConnection#DEREFERENCE_ALIASES_SEARCH}, {@link IBrowserConnection#DEREFERENCE_ALIASES_FINDING}
     * or {@link IBrowserConnection#DEREFERENCE_ALIASES_ALWAYS}.
     * 
     * @return the aliases dereferencing method
     */
    public int getAliasesDereferencingMethod()
    {
        if ( findingButton.getSelection() && searchButton.getSelection() )
        {
            return IBrowserConnection.DEREFERENCE_ALIASES_ALWAYS;
        }
        else if ( findingButton.getSelection() )
        {
            return IBrowserConnection.DEREFERENCE_ALIASES_FINDING;
        }
        else if ( searchButton.getSelection() )
        {
            return IBrowserConnection.DEREFERENCE_ALIASES_SEARCH;
        }
        else
        {
            return IBrowserConnection.DEREFERENCE_ALIASES_NEVER;
        }
    }


    /**
     * Sets the enabled state of the widget.
     * 
     * @param b true to enable the widget, false to disable the widget
     */
    public void setEnabled( boolean b )
    {
        group.setEnabled( b );
        findingButton.setEnabled( b );
        searchButton.setEnabled( b );
    }

}
