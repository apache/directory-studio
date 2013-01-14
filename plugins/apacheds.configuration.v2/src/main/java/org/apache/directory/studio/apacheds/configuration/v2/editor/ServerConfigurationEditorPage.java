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
package org.apache.directory.studio.apacheds.configuration.v2.editor;


import org.apache.directory.server.config.beans.ConfigBean;
import org.apache.directory.server.config.beans.DirectoryServiceBean;
import org.apache.directory.studio.apacheds.configuration.v2.actions.EditorExportConfigurationAction;
import org.apache.directory.studio.apacheds.configuration.v2.actions.EditorImportConfigurationAction;
import org.apache.directory.studio.connection.core.Connection;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;


/**
 * This class represents the General Page of the Server Configuration Editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class ServerConfigurationEditorPage extends FormPage
{
    protected static final Color GRAY_COLOR = new Color( null, 120, 120, 120 );
    protected static final String TABULATION = "      "; //$NON-NLS-1$

    // Dirty listeners
    private ModifyListener dirtyModifyListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            setEditorDirty();
        }
    };
    private SelectionListener dirtySelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            setEditorDirty();
        }
    };
    private ISelectionChangedListener dirtySelectionChangedListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            setEditorDirty();
        }
    };


    /**
     * Creates a new instance of GeneralPage.
     *
     * @param editor
     *      the associated editor
     */
    public ServerConfigurationEditorPage( ServerConfigurationEditor editor, String id, String title )
    {
        super( editor, id, title );
    }


    /**
     * Gets the ServerConfigurationEditor object associated with the page.
     *
     * @return
     *      the ServerConfigurationEditor object associated with the page
     */
    public ServerConfigurationEditor getServerConfigurationEditor()
    {
        return ( ServerConfigurationEditor ) getEditor();
    }


    /**
     * Sets the associated editor dirty.
     */
    protected void setEditorDirty()
    {
        getServerConfigurationEditor().setDirty( true );
    }


    /**
     * Gets the configuration bean associated with the editor.
     *
     * @return
     *      the configuration bean associated with the editor
     */
    public ConfigBean getConfigBean()
    {
        ConfigBean configBean = getServerConfigurationEditor().getConfigBean();

        if ( configBean == null )
        {
            configBean = new ConfigBean();
            getServerConfigurationEditor().setConfiguration( configBean );
        }

        return configBean;
    }


    /**
     * Gets the directory service associated with the editor.
     *
     * @return
     *      the directory service bean associated with the editor
     */
    public DirectoryServiceBean getDirectoryServiceBean()
    {
        DirectoryServiceBean directoryServiceBean = getConfigBean().getDirectoryServiceBean();

        if ( directoryServiceBean == null )
        {
            directoryServiceBean = new DirectoryServiceBean();
            getConfigBean().addDirectoryService( directoryServiceBean );
        }

        return directoryServiceBean;
    }


    /**
     * Gets the connection associated with the editor.
     *
     * @return the connection
     */
    public Connection getConnection()
    {
        IEditorInput editorInput = getEditorInput();

        if ( editorInput instanceof ConnectionServerConfigurationInput )
        {
            return ( ( ConnectionServerConfigurationInput ) editorInput ).getConnection();
        }

        return null;
    }


    /**
     * {@inheritDoc}
     */
    protected void createFormContent( IManagedForm managedForm )
    {
        ScrolledForm form = managedForm.getForm();
        form.setText( getTitle() );

        Composite parent = form.getBody();
        parent.setLayout( new GridLayout() );

        FormToolkit toolkit = managedForm.getToolkit();
        toolkit.decorateFormHeading( form.getForm() );

        ServerConfigurationEditor editor = ( ServerConfigurationEditor ) getEditor();

        IToolBarManager toolbarManager = form.getToolBarManager();
        toolbarManager.add( new EditorImportConfigurationAction( editor ) );
        toolbarManager.add( new Separator() );
        toolbarManager.add( new EditorExportConfigurationAction( editor ) );

        toolbarManager.update( true );

        createFormContent( parent, toolkit );
    }


    /**
     * Subclasses must implement this method to create the content of their form.
     *
     * @param parent
     *      the parent element
     * @param toolkit
     *      the form toolkit
     */
    protected abstract void createFormContent( Composite parent, FormToolkit toolkit );


    /**
     * Refreshes the UI.
     */
    protected abstract void refreshUI();


    /**
     * Creates a Text that can be used to enter a port number.
     *
     * @param toolkit
     *      the toolkit
     * @param parent
     *      the parent
     * @return
     *      a Text that can be used to enter a port number
     */
    protected Text createPortText( FormToolkit toolkit, Composite parent )
    {
        Text portText = toolkit.createText( parent, "" ); //$NON-NLS-1$
        GridData gd = new GridData( SWT.NONE, SWT.NONE, false, false );
        gd.widthHint = 42;
        portText.setLayoutData( gd );
        portText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
                {
                    e.doit = false;
                }
            }
        } );
        portText.setTextLimit( 5 );

        return portText;
    }


    /**
     * Creates a Text that can be used to enter an integer.
     *
     * @param toolkit
     *      the toolkit
     * @param parent
     *      the parent
     * @return
     *      a Text that can be used to enter a port number
     */
    protected Text createIntegerText( FormToolkit toolkit, Composite parent )
    {
        Text integerText = toolkit.createText( parent, "" ); //$NON-NLS-1$
        integerText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
                {
                    e.doit = false;
                }
            }
        } );

        return integerText;
    }


    /**
     * Creates default value Label.
     *
     * @param toolkit
     *      the toolkit
     * @param parent
     *      the parent
     * @param text
     *      the text string
     * @return
     *      a default value Label
     */
    protected Label createDefaultValueLabel( FormToolkit toolkit, Composite parent, String text )
    {
        Label label = toolkit.createLabel( parent, NLS.bind( Messages.getString("ServerConfigurationEditorPage.DefaultWithValue"), text ), SWT.WRAP ); //$NON-NLS-1$
        label.setForeground( GRAY_COLOR );

        return label;
    }


    /**
     * Adds a modify listener to the given Text.
     *
     * @param text
     *      the Text control
     * @param listener
     *      the listener
     */
    protected void addModifyListener( Text text, ModifyListener listener )
    {
        if ( ( text != null ) && ( !text.isDisposed() ) && ( listener != null ) )
        {
            text.addModifyListener( listener );
        }
    }


    /**
     * Adds a selection changed listener to the given Viewer.
     *
     * @param viewer
     *      the viewer control
     * @param listener
     *      the listener
     */
    protected void addSelectionChangedListener( Viewer viewer, ISelectionChangedListener listener )
    {
        if ( ( viewer != null ) && ( viewer.getControl() != null ) && ( !viewer.getControl().isDisposed() )
            && ( listener != null ) )
        {
            viewer.addSelectionChangedListener( listener );
        }
    }


    /**
     * Adds a selection listener to the given Button.
     *
     * @param button
     *      the Button control
     * @param listener
     *      the listener
     */
    protected void addSelectionListener( Button button, SelectionListener listener )
    {
        if ( ( button != null ) && ( !button.isDisposed() ) && ( listener != null ) )
        {
            button.addSelectionListener( listener );
        }
    }


    /**
     * Removes a modify listener to the given Text.
     *
     * @param text
     *      the Text control
     * @param listener
     *      the listener
     */
    protected void removeModifyListener( Text text, ModifyListener listener )
    {
        if ( ( text != null ) && ( !text.isDisposed() ) && ( listener != null ) )
        {
            text.removeModifyListener( listener );
        }
    }


    /**
     * Removes a selection changed listener to the given Viewer.
     *
     * @param viewer
     *      the viewer control
     * @param listener
     *      the listener
     */
    protected void removeSelectionChangedListener( Viewer viewer, ISelectionChangedListener listener )
    {
        if ( ( viewer != null ) && ( viewer.getControl() != null ) && ( !viewer.getControl().isDisposed() )
            && ( listener != null ) )
        {
            viewer.removeSelectionChangedListener( listener );
        }
    }


    /**
     * Removes a selection listener to the given Button.
     *
     * @param button
     *      the Button control
     * @param listener
     *      the listener
     */
    protected void removeSelectionListener( Button button, SelectionListener listener )
    {
        if ( ( button != null ) && ( !button.isDisposed() ) && ( listener != null ) )
        {
            button.removeSelectionListener( listener );
        }
    }


    /**
     * Adds a 'dirty' listener to the given Text.
     *
     * @param text
     *      the Text control
     */
    protected void addDirtyListener( Text text )
    {
        addModifyListener( text, dirtyModifyListener );
    }


    /**
     * Adds a 'dirty' listener to the given Button.
     *
     * @param button
     *      the Button control
     */
    protected void addDirtyListener( Button button )
    {
        addSelectionListener( button, dirtySelectionListener );
    }


    /**
     * Adds a 'dirty' listener to the given Viewer.
     *
     * @param viewer
     *      the viewer control
     */
    protected void addDirtyListener( Viewer viewer )
    {
        addSelectionChangedListener( viewer, dirtySelectionChangedListener );
    }


    /**
     * Removes a 'dirty' listener to the given Text.
     *
     * @param text
     *      the Text control
     */
    protected void removeDirtyListener( Text text )
    {
        removeModifyListener( text, dirtyModifyListener );
    }


    /**
     * Removes a 'dirty' listener to the given Button.
     *
     * @param button
     *      the Button control
     */
    protected void removeDirtyListener( Button button )
    {
        removeSelectionListener( button, dirtySelectionListener );
    }


    /**
     * Removes a 'dirty' listener to the given Viewer.
     *
     * @param viewer
     *      the viewer control
     */
    protected void removeDirtyListener( Viewer viewer )
    {
        removeSelectionChangedListener( viewer, dirtySelectionChangedListener );
    }


    /**
     * Sets the selection state of the button widget.
     * <p>
     * Verifies that the button exists and is not disposed 
     * before applying the new selection state.
     *
     * @param button
     *      the button
     * @param selected
     *      the new selection state
     */
    protected void setSelection( Button button, boolean selected )
    {
        if ( ( button != null ) && ( !button.isDisposed() ) )
        {
            button.setSelection( selected );
        }
    }


    /**
     * Sets the selection of the viewer widget.
     * <p>
     * Verifies that the viewer exists and is not disposed 
     * before applying the new selection.
     *
     * @param button
     *      the button
     * @param selection
     *      the new selection
     */
    protected void setSelection( Viewer viewer, Object selection )
    {
        if ( ( viewer != null ) && ( viewer.getControl() != null ) && ( !viewer.getControl().isDisposed() ) )
        {
            viewer.setSelection( new StructuredSelection( selection ) );
        }
    }


    /**
     * Sets the contents of the text widget.
     * <p>
     * Verifies that the button exists and is not disposed 
     * before applying the new text.
     *
     * @param text
     *      the text
     * @param string
     *       the new text
     */
    protected void setText( Text text, String string )
    {
        if ( ( text != null ) && ( !text.isDisposed() ) )
        {
            if ( string == null )
            {
                string = ""; //$NON-NLS-1$
            }

            text.setText( string );
        }
    }


    /**
     * Sets the focus to the given control.
     *
     * @param control the control
     */
    protected void setFocus( Control control )
    {
        if ( ( control != null ) && ( !control.isDisposed() ) )
        {
            control.setFocus();
        }
    }


    /**
     * Sets the enabled state to the given control.
     *
     * @param control the control
     * @param enabled the enabled state
     */
    protected void setEnabled( Control control, boolean enabled )
    {
        if ( ( control != null ) && ( !control.isDisposed() ) )
        {
            control.setEnabled( enabled );
        }
    }


    /**
     * Sets the given {@link GridData} to the control
     * and sets the width to a default value.
     *
     * @param control the control
     * @param gd the grid data
     */
    protected void setGridDataWithDefaultWidth( Control control, GridData gd )
    {
        gd.widthHint = 50;
        control.setLayoutData( gd );
    }
}
