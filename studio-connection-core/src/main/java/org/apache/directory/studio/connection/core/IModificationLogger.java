package org.apache.directory.studio.connection.core;


import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.ModificationItem;
import javax.naming.ldap.Control;


/**
 * Callback interface to log modifications
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface IModificationLogger
{

    /**
     * Logs a changetype:add.
     * 
     * @param dn the dn
     * @param attributes the attributes
     * @param controls the controls
     * @param ex the naming exception if an error occurred, null otherwise
     */
    public void logChangetypeAdd( final String dn, final Attributes attributes, final Control[] controls, NamingException ex );


    /**
     * Logs a changetype:delete.
     * 
     * @param dn the dn
     * @param controls the controls
     * @param ex the naming exception if an error occurred, null otherwise
     */
    public void logChangetypeDelete( final String dn, final Control[] controls, NamingException ex );


    /**
     * Logs a changetype:modify.
     * 
     * @param dn the dn
     * @param modificationItems the modification items
     * @param controls the controls
     * @param ex the naming exception if an error occurred, null otherwise
     */
    public void logChangetypeModify( final String dn, final ModificationItem[] modificationItems, final Control[] controls, NamingException ex );


    /**
     * Logs a changetype:moddn.
     * 
     * @param oldDn the old dn
     * @param newDn the new dn
     * @param deleteOldRdn the delete old rdn
     * @param controls the controls
     * @param ex the naming exception if an error occurred, null otherwise
     */
    public void logChangetypeModDn( final String oldDn, final String newDn, final boolean deleteOldRdn, final Control[] controls, NamingException ex );

}
