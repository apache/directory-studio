
package org.apache.directory.ldapstudio.schemas.controller.actions;

import org.apache.directory.ldapstudio.schemas.controller.Application;
import org.apache.directory.ldapstudio.schemas.view.IImageKeys;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * This action collapses all nodes of the viewer's tree, starting with the root.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class CollapseAllAction extends Action
{
    protected TreeViewer viewer;

    /**
     * Creates a new instance of CollapseAllAction.
     *
     * @param viewer
     *      the attached Viewer
     */
    public CollapseAllAction( TreeViewer viewer )
    {
        super( "Collapse All");
        setToolTipText( getText() );
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Application.PLUGIN_ID, IImageKeys.COLLAPSE_ALL )  );
        setEnabled( true );

        this.viewer = viewer;
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        this.viewer.collapseAll();
    }


    /**
     * Disposes the action delegate.
     */
    public void dispose()
    {
        this.viewer = null;
    }
}
