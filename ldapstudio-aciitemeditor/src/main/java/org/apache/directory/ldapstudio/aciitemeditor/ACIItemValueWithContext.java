package org.apache.directory.ldapstudio.aciitemeditor;


import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;


/**
 * The ACIItemValueContext is used to pass contextual
 * information to the opened ACIItemDialog.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ACIItemValueWithContext
{

    /** The connection, used to browse the directory. */
    private IConnection connection;

    /** The entry. */
    private IEntry entry;

    /** The ACI item. */
    private String aciItemValue;


    /**
     * Creates a new instance of ACIItemValueContext.
     * 
     * @param aciItemValue the ACI item value
     * @param connection the connection
     * @param entry the entry
     */
    public ACIItemValueWithContext( IConnection connection, IEntry entry, String aciItemValue )
    {
        this.connection = connection;
        this.entry = entry;
        this.aciItemValue = aciItemValue;
    }


    /**
     * Gets the aci item value.
     * 
     * @return the aciItemValue
     */
    public String getACIItemValue()
    {
        return aciItemValue;
    }


    /**
     * @return the connection
     */
    public IConnection getConnection()
    {
        return connection;
    }


    /**
     * @return the entry
     */
    public IEntry getEntry()
    {
        return entry;
    }

}