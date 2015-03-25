package org.apache.directory.studio.openldap.config.editor.databases;


import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;


/**
 * This interface represents a block for Database Specific Details.
 */
public interface DatabaseSpecificDetailsBlock
{
    /**
     * Creates the form content.
     *
     * @param parent the parent composite
     * @param toolkit the toolkit
     */
    public void createFormContent( Composite parent, FormToolkit toolkit );


    /**
     * Refreshes the UI based on the input.
     */
    public void refresh();
}
