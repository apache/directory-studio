
package org.apache.directory.studio.connection.ui.actions;

/**
 * A ActionHandlerManager activates and deactives the action handlers.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface ActionHandlerManager
{

    /**
     * Deactivates global action handlers.
     */
    public void deactivateGlobalActionHandlers();

    
    /**
     * Activates global action handlers.
     */
    public void activateGlobalActionHandlers();

}
