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


import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.directory.server.config.beans.ConfigBean;
import org.apache.directory.server.config.beans.DirectoryServiceBean;
import org.apache.directory.studio.apacheds.configuration.v2.actions.EditorExportConfigurationAction;
import org.apache.directory.studio.apacheds.configuration.v2.actions.EditorImportConfigurationAction;
import org.apache.directory.studio.connection.core.Connection;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;


/**
 * This class represents the General Page of the Server Configuration Editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class ServerConfigurationEditorPage extends FormPage
{
    /** The default LDAP port */
    protected static final int DEFAULT_PORT_LDAP = 10389;
    
    /** The default LDAPS port */
    protected static final int DEFAULT_PORT_LDAPS = 10636;
    
    /** The default Kerberos port */
    protected static final int DEFAULT_PORT_KERBEROS = 60088;
    
    /** The default LDAPS port */
    protected static final int DEFAULT_PORT_CHANGE_PASSWORD = 60464;
    
    /** The default IPV4 address for servers */
    protected static final String DEFAULT_ADDRESS = "0.0.0.0"; //$NON-NLS-1$
    
    protected static final Color GRAY_COLOR = new Color( null, 120, 120, 120 );
    protected static final String TABULATION = "      "; //$NON-NLS-1$
    
    /** A flag to indicate if the page is initialized */
    protected boolean isInitialized = false;

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
     * @param editor the associated editor
     * @param id the unique identifier
     * @param title The page title
     */
    public ServerConfigurationEditorPage( ServerConfigurationEditor editor, String id, String title )
    {
        super( editor, id, title );
    }


    /**
     * Gets the ServerConfigurationEditor object associated with the page.
     *
     * @return the ServerConfigurationEditor object associated with the page
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
     * @return the configuration bean associated with the editor
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
     * @return the directory service bean associated with the editor
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
        
        isInitialized = true;
    }


    /**
     * Subclasses must implement this method to create the content of their form.
     *
     * @param parent the parent element
     * @param toolkit the form toolkit
     */
    protected abstract void createFormContent( Composite parent, FormToolkit toolkit );


    /**
     * Refreshes the UI.
     */
    protected abstract void refreshUI();
    
    
    /**
     * Indicates if the page is initialized.
     *
     * @return <code>true</code> if the page is initialized,
     *         <code>false</code> if not.
     */
    public boolean isInitialized()
    {
        return isInitialized;
    }


    /**
     * Creates a Text that can be used to enter a port number.
     *
     * @param toolkit the toolkit
     * @param parent the parent
     * @return a Text that can be used to enter a port number
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
                // Check that it's a valid port. It should be
                // any value between 0 and 65535
                // Skip spaces on both sides
                char[] port = e.text.trim().toCharArray();

                if ( port.length > 0 )
                {
                    for ( char c : port )
                    {
                        if ( ( c < '0' ) || ( c > '9' ) )
                        {
                            // This is an error
                            e.doit = false;
                            break;
                        }
                    }
                }
            }
        } );
        
        
        // the port can only have 5 chars max
        portText.setTextLimit( 5 );

        return portText;
    }


    /**
     * Creates a Text that can be used to enter an address. If the address is incorrect, 
     * it will be in red while typing until it gets correct.
     *
     * @param toolkit the toolkit
     * @param parent the parent
     * @return a Text that can be used to enter an address
     */
    protected Text createAddressText( FormToolkit toolkit, Composite parent )
    {
        final Text addressText = toolkit.createText( parent, "" ); //$NON-NLS-1$
        GridData gd = new GridData( SWT.NONE, SWT.NONE, false, false );
        gd.widthHint = 200;
        addressText.setLayoutData( gd );
        
        addressText.addModifyListener( new ModifyListener()
        {
            Display display = addressText.getDisplay();

            // Check that the address is valid
            public void modifyText( ModifyEvent e )
            {
                Text addressText = (Text)e.widget;
                String address = addressText.getText();
                
                try
                {
                    InetAddress.getAllByName( address );
                    addressText.setForeground( null );
                }
                catch ( UnknownHostException uhe )
                {
                    addressText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                }
            }
        } );
        
        // An address can be fairly long...
        addressText.setTextLimit( 256 );

        return addressText;
    }


    /**
     * Creates a Text that can be used to enter the number of threads
     * (which limit is 999)
     *
     * @param toolkit the toolkit
     * @param parent the parent
     * @return a Text that can be used to enter the number of threads
     */
    protected Text createNbThreadsText( FormToolkit toolkit, Composite parent )
    {
        Text nbThreadsText = toolkit.createText( parent, "" ); //$NON-NLS-1$
        GridData gd = new GridData( SWT.NONE, SWT.NONE, false, false );
        gd.widthHint = 42;
        nbThreadsText.setLayoutData( gd );
        
        nbThreadsText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                // Check that it's a valid number of threads. It should be
                // any value between 0 and 999
                // Skip spaces on both sides
                char[] nbThreads = e.text.trim().toCharArray();

                if ( nbThreads.length > 0 )
                {
                    for ( char c : nbThreads )
                    {
                        if ( ( c < '0' ) || ( c > '9' ) )
                        {
                            // This is an error
                            e.doit = false;
                            break;
                        }
                    }
                }
            }
        } );
        
        
        // We can't have more than 999 threads
        nbThreadsText.setTextLimit( 3 );

        return nbThreadsText;
    }


    /**
     * Creates a Text that can be used to enter the backLog size
     *
     * @param toolkit the toolkit
     * @param parent the parent
     * @return a Text that can be used to enter the backlog size
     */
    protected Text createBackLogSizeText( FormToolkit toolkit, Composite parent )
    {
        Text backLogSizetText = toolkit.createText( parent, "" ); //$NON-NLS-1$
        GridData gd = new GridData( SWT.NONE, SWT.NONE, false, false );
        gd.widthHint = 42;
        backLogSizetText.setLayoutData( gd );
        
        backLogSizetText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                // Check that it's a valid size. It should be
                // any value between 0 and 99999
                // Skip spaces on both sides
                char[] backlogSize = e.text.trim().toCharArray();

                if ( backlogSize.length > 0 )
                {
                    for ( char c : backlogSize )
                    {
                        if ( ( c < '0' ) || ( c > '9' ) )
                        {
                            // This is an error
                            e.doit = false;
                            break;
                        }
                    }
                }
            }
        } );
        
        
        // the backlog size can only have 5 chars max
        backLogSizetText.setTextLimit( 5 );

        return backLogSizetText;
    }


    /**
     * Creates a Text that can be used to enter an integer.
     *
     * @param toolkit the toolkit
     * @param parent the parent
     * @return a Text that can be used to enter a port number
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
     * @param toolkit the toolkit
     * @param parent the parent
     * @param text the text string
     * @return a default value Label
     */
    protected Label createDefaultValueLabel( FormToolkit toolkit, Composite parent, String text )
    {
        Label label = toolkit.createLabel( parent,
            NLS.bind( Messages.getString( "ServerConfigurationEditorPage.DefaultWithValue" ), text ), SWT.WRAP ); //$NON-NLS-1$
        label.setForeground( GRAY_COLOR );

        return label;
    }


    /**
     * Set some Label to Bold
     *
     * @param label the Label we want to see as Bold
     * @return a Label with bold text
     */
    protected Label setBold( Label label )
    {
        FontData fontData = label.getFont().getFontData()[0];
        Font font = new Font( label.getDisplay(), new FontData( fontData.getName(), fontData.getHeight(), SWT.BOLD ) );
        label.setFont( font );

        return label;
    }


    /**
     * Adds a modify listener to the given Text.
     *
     * @param text the Text control
     * @param listener the listener
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
     * @param viewer the viewer control
     * @param listener the listener
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
     * Adds a double click listener to the given StructuredViewer.
     *
     * @param viewer the viewer control
     * @param listener the listener
     */
    protected void addDoubleClickListener( StructuredViewer viewer, IDoubleClickListener listener )
    {
        if ( ( viewer != null ) && ( viewer.getControl() != null ) && ( !viewer.getControl().isDisposed() )
            && ( listener != null ) )
        {
            viewer.addDoubleClickListener( listener );
        }
    }


    /**
     * Adds a selection listener to the given Button.
     *
     * @param button the Button control
     * @param listener the listener
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
     * @param text the Text control
     * @param listener the listener
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
     * @param viewer the viewer control
     * @param listener the listener
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
     * Removes a selection changed listener to the given Viewer.
     *
     * @param viewer the viewer control
     * @param listener the listener
     */
    protected void removeDoubleClickListener( StructuredViewer viewer, IDoubleClickListener listener )
    {
        if ( ( viewer != null ) && ( viewer.getControl() != null ) && ( !viewer.getControl().isDisposed() )
            && ( listener != null ) )
        {
            viewer.removeDoubleClickListener( listener );
        }
    }


    /**
     * Removes a selection listener to the given Button.
     *
     * @param button the Button control
     * @param listener the listener
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
     * @param text the Text control
     */
    protected void addDirtyListener( Text text )
    {
        addModifyListener( text, dirtyModifyListener );
    }


    /**
     * Adds a 'dirty' listener to the given Button.
     *
     * @param button the Button control
     */
    protected void addDirtyListener( Button button )
    {
        addSelectionListener( button, dirtySelectionListener );
    }


    /**
     * Adds a 'dirty' listener to the given Viewer.
     *
     * @param viewer the viewer control
     */
    protected void addDirtyListener( Viewer viewer )
    {
        addSelectionChangedListener( viewer, dirtySelectionChangedListener );
    }


    /**
     * Removes a 'dirty' listener to the given Text.
     *
     * @param text the Text control
     */
    protected void removeDirtyListener( Text text )
    {
        removeModifyListener( text, dirtyModifyListener );
    }


    /**
     * Removes a 'dirty' listener to the given Button.
     *
     * @param button the Button control
     */
    protected void removeDirtyListener( Button button )
    {
        removeSelectionListener( button, dirtySelectionListener );
    }


    /**
     * Removes a 'dirty' listener to the given Viewer.
     *
     * @param viewer the viewer control
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
     * @param button the button
     * @param selected the new selection state
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
     * @param button the button
     * @param selection the new selection
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
     * @param text the text
     * @param string the new text
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
    
    
    /**
     * A shared method used to create a Section, based on a GridLayout.
     * 
     * @param toolkit The Form toolkit
     * @param parent The parent 
     * @param title The Section title
     * @param nbColumns The number of columns for the inner grid
     * 
     * @return The created Composite
     */
    protected Composite createSection( FormToolkit toolkit, Composite parent, String title, int nbColumns, int style )
    {
        Section section = toolkit.createSection( parent, style );
        section.setText( Messages.getString( title ) );
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout gridLayout = new GridLayout( nbColumns, false );
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        composite.setLayout( gridLayout );
        section.setClient( composite );

        return composite;
    }
}
