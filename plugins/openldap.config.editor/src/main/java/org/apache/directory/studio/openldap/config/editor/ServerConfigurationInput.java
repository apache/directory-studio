package org.apache.directory.studio.openldap.config.editor;


import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPlugin;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPluginConstants;


/**
 * This class represents the Non Existing Server Configuration Input.
 */
public class ServerConfigurationInput implements IEditorInput
{
    /**
     * {@inheritDoc}
     */
    public String getToolTipText()
    {
        return getName();
    }


    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return "ServerConfigurationInput";
    }


    /**
     * {@inheritDoc}
     */
    public boolean exists()
    {
        return true;
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return OpenLdapConfigurationPlugin.getDefault().getImageDescriptor(
            OpenLdapConfigurationPluginConstants.IMG_EDITOR );
    }


    /**
     * {@inheritDoc}
     */
    public IPersistableElement getPersistable()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public Object getAdapter( Class adapter )
    {
        return null;
    }
}
