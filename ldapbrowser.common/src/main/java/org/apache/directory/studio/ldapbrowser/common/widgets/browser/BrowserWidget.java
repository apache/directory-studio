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

package org.apache.directory.studio.ldapbrowser.common.widgets.browser;


import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.common.ui.widgets.ViewFormWidget;
import org.apache.directory.studio.ldapbrowser.common.dialogs.SelectEntryDialog;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;


/**
 * The BrowserWidget is a reusable widget that displays the DIT, searches
 * and bookmarks of a connection a tree viewer.
 * It is used by {@link BrowserView} and {@link SelectEntryDialog}.
 * 
 * It provides a context menu and a local toolbar with actions to
 * manage entries, searches and bookmarks.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class BrowserWidget extends ViewFormWidget
{

    /** The widget's configuration with the content provider, label provider and menu manager */
    private BrowserConfiguration configuration;

    /** The quick search widget. */
    private BrowserQuickSearchWidget quickSearchWidget;

    /** The action bars. */
    private IActionBars actionBars;

    /** The tree widget used by the tree viewer */
    private Tree tree;

    /** The tree viewer. */
    private TreeViewer viewer;


    /**
     * Creates a new instance of BrowserWidget.
     *
     * @param configuration the configuration
     * @param actionBars the action bars
     */
    public BrowserWidget( BrowserConfiguration configuration, IActionBars actionBars )
    {
        this.configuration = configuration;
        this.actionBars = actionBars;
    }


    /**
     * {@inheritDoc}
     */
    public void createWidget( Composite parent )
    {
        if ( actionBars == null )
        {
            super.createWidget( parent );
        }
        else
        {
            createContent( parent );
        }
    }


    /**
     * {@inheritDoc}
     */
    public IToolBarManager getToolBarManager()
    {
        if ( actionBars == null )
        {
            return super.getToolBarManager();
        }
        else
        {
            return actionBars.getToolBarManager();
        }
    }


    /**
     * {@inheritDoc}
     */
    public IMenuManager getMenuManager()
    {
        if ( actionBars == null )
        {
            return super.getMenuManager();
        }
        else
        {
            return actionBars.getMenuManager();
        }
    }


    /**
     * {@inheritDoc}
     */
    public IMenuManager getContextMenuManager()
    {
        if ( actionBars == null )
        {
            return super.getContextMenuManager();
        }
        else
        {
            return configuration.getContextMenuManager( viewer );
        }
    }


    /**
     * {@inheritDoc}
     */
    protected Control createContent( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );
        GridLayout gl = new GridLayout( 1, false );
        gl.marginHeight = gl.marginWidth = 0;
        gl.verticalSpacing = gl.horizontalSpacing = 0;
        composite.setLayout( gl );
        GridData gd = new GridData( GridData.FILL_BOTH );
        composite.setLayoutData( gd );

        quickSearchWidget = new BrowserQuickSearchWidget( this );
        quickSearchWidget.createComposite( composite );

        // create tree widget and viewer
        tree = new Tree( composite, SWT.VIRTUAL | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
        GridData data = new GridData( GridData.FILL_BOTH );
        data.widthHint = 450;
        data.heightHint = 250;
        tree.setLayoutData( data );
        viewer = new TreeViewer( tree );
        viewer.setUseHashlookup( true );

        // setup sorter, filter and layout
        configuration.getSorter().connect( viewer );
        configuration.getPreferences().connect( viewer );

        // setup providers
        viewer.setContentProvider( configuration.getContentProvider( viewer ) );
        viewer.setLabelProvider( configuration.getLabelProvider( viewer ) );

        return tree;
    }


    /**
     * Sets the input to the tree viewer.
     *
     * @param input the input
     */
    public void setInput( Object input )
    {
        viewer.setInput( input );
    }


    /**
     * Sets focus to the table viewer.
     */
    public void setFocus()
    {
        viewer.getTree().setFocus();
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        if ( this.viewer != null )
        {
            this.configuration.dispose();
            this.configuration = null;

            if ( quickSearchWidget != null )
            {
                quickSearchWidget.dispose();
                quickSearchWidget = null;
            }

            this.tree.dispose();
            this.tree = null;
            this.viewer = null;
        }
    }


    /**
     * Gets the quick search widget.
     * 
     * @return the quick search widget
     */
    public BrowserQuickSearchWidget getQuickSearchWidget()
    {
        return quickSearchWidget;
    }


    /**
     * Gets the tree viewer.
     * 
     * @return the tree viewer
     */
    public TreeViewer getViewer()
    {
        return viewer;
    }

}
