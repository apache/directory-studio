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
package org.apache.directory.studio.openldap.config.editor.pages;


import org.apache.directory.studio.common.ui.CommonUIConstants;
import org.apache.directory.studio.common.ui.widgets.WidgetModifyEvent;
import org.apache.directory.studio.common.ui.widgets.WidgetModifyListener;
import org.apache.directory.studio.openldap.config.actions.EditorExportConfigurationAction;
import org.apache.directory.studio.openldap.config.actions.EditorImportConfigurationAction;
import org.apache.directory.studio.openldap.config.editor.Messages;
import org.apache.directory.studio.openldap.config.editor.OpenLDAPServerConfigurationEditor;
import org.apache.directory.studio.openldap.config.model.OpenLdapConfiguration;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
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
public abstract class OpenLDAPServerConfigurationEditorPage extends FormPage
{
    protected static final String TABULATION = "      ";

    /** A flag to indicate if the page is initialized */
    protected boolean isInitialized = false;

    /**
     * A listener used to set the dirty flag when a Text is updated
     */
    protected ModifyListener dirtyModifyListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            setEditorDirty();
        }
    };

    /**
     * A listener used to set the dirty flag when a widget is selected
     */
    private SelectionListener dirtySelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            setEditorDirty();
        }
    };

    /**
     * A listener used to set the dirty flag when a widget is updated
     */
    protected WidgetModifyListener dirtyWidgetModifyListener = new WidgetModifyListener()
    {
        public void widgetModified( WidgetModifyEvent event )
        {
            setEditorDirty();
        }
    };


    /**
     * Creates a new instance of GeneralPage.
     *
     * @param editor the associated editor
     */
    public OpenLDAPServerConfigurationEditorPage( OpenLDAPServerConfigurationEditor editor, String id, String title )
    {
        super( editor, id, title );
    }


    /**
     * Gets the ServerConfigurationEditor object associated with the page.
     *
     * @return the ServerConfigurationEditor object associated with the page
     */
    public OpenLDAPServerConfigurationEditor getServerConfigurationEditor()
    {
        return ( OpenLDAPServerConfigurationEditor ) getEditor();
    }


    /**
     * Sets the associated editor dirty.
     */
    private void setEditorDirty()
    {
        getServerConfigurationEditor().setDirty( true );
    }


    /**
     * Gets the configuration associated with the editor.
     *
     * @return the configuration associated with the editor
     */
    public OpenLdapConfiguration getConfiguration()
    {
        OpenLdapConfiguration configuration = getServerConfigurationEditor().getConfiguration();

        if ( configuration == null )
        {
            configuration = new OpenLdapConfiguration();
            getServerConfigurationEditor().setConfiguration( configuration );
        }

        return configuration;
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

        OpenLDAPServerConfigurationEditor editor = ( OpenLDAPServerConfigurationEditor ) getEditor();

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
    public abstract void refreshUI();


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
        Label label = toolkit.createLabel( parent, NLS.bind( "(Default: {0})", text ) );
        label.setForeground( CommonUIConstants.M_GREY_COLOR );

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
            text.setText( string );
        }
    }


    protected void setFocus( Control control )
    {
        if ( ( control != null ) && ( !control.isDisposed() ) )
        {
            control.setFocus();
        }
    }
    
    
    /**
     * Create an expandable Section with a title 
     */
    protected Section createSection( FormToolkit toolkit, Composite parent, String title )
    {
        Section section = toolkit.createSection( parent, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        section.setText( title ); //$NON-NLS-1$
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        
        return section;
    }
}
