package org.apache.directory.studio.openldap.config.model;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.api.ldap.model.name.Dn;


/**
 * Java bean for the 'olcConfig' object class.
 */
public class OlcConfig
{
    /** The parent DN of the associated entry */
    protected Dn parentDn;
    
    
    /** The list of auxiliary object classes */
    protected List<AuxiliaryObjectClass> auxiliaryObjectClasses = new ArrayList<AuxiliaryObjectClass>();


    /**
     * @param auxiliaryObjectClasses
     */
    public void addAuxiliaryObjectClasses( AuxiliaryObjectClass... auxiliaryObjectClasses )
    {
        for ( AuxiliaryObjectClass auxiliaryObjectClass : auxiliaryObjectClasses )
        {
            this.auxiliaryObjectClasses.add( auxiliaryObjectClass );
        }
    }


    /**
     * Gets the list of objects associated with the auxiliary classes.
     *
     * @return the list of objects associated with auxiliary classes.
     */
    public List<AuxiliaryObjectClass> getAuxiliaryObjectClasses()
    {
        return auxiliaryObjectClasses;
    }


    /**
     * Gets the number of auxiliary object classes.
     *
     * @return the number of auxiliary object classes
     */
    public int getAuxiliaryObjectClassesSize()
    {
        return auxiliaryObjectClasses.size();
    }


    /**
     * Gets the parent DN of the associated entry.
     * 
     * @return the dn the parent DN of the asssociated entry
     */
    public Dn getParentDn()
    {
        return parentDn;
    }


    /**
     * Sets the parent DN of the associated entry.
     * 
     * @param dn the parent dn to set
     */
    public void setParentDn( Dn parentDn )
    {
        this.parentDn = parentDn;
    }
}
