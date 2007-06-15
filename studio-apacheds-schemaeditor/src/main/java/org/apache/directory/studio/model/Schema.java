package org.apache.directory.studio.model;


import java.util.List;

import org.apache.directory.shared.ldap.schema.AttributeType;
import org.apache.directory.shared.ldap.schema.MatchingRule;
import org.apache.directory.shared.ldap.schema.ObjectClass;
import org.apache.directory.shared.ldap.schema.Syntax;


/**
 * This interface represents a Schema.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface Schema
{
    /**
     * Gets the name of the Schema.
     * 
     * @return
     * 		the name of the Schema
     */
    public String getName();


    /**
     * Sets the name of the Schema.
     * 
     * @param name
     * 		the name of the schema
     */
    public void setName( String name );


    /**
     * Gets all the ObjectClass objects contained in the Schema.
     * 
     * @return
     * 		all the ObjectClass objects contained in the Schema
     */
    public List<ObjectClass> getObjectClasses();


    /**
     * Gets all the AttributeType objects contained in the Schema.
     * 
     * @return
     * 		all the AttributeType objects contained in the Schema
     */
    public List<AttributeType> getAttributeTypes();


    /**
     * Gets all the MatchingRule objects contained in the Schema.
     * 
     * @return
     *      all the MatchingRule objects contained in the Schema
     */
    public List<MatchingRule> getMatchingRules();


    /**
     * Gets all the Syntax objects contained in the Schema.
     * 
     * @return
     *      all the Syntax objects contained in the Schema
     */
    public List<Syntax> getSyntaxes();


    /**
     * Gets the ObjectClass identified by the given id.
     * 
     * @param id
     * 		the name or the oid of the ObjectClass
     * @return
     * 		the ObjectClass identified by the given id, or null if the 
     * ObjectClass has not been found
     */
    public ObjectClass getObjectClass( String id );


    /**
     * Gets the AttributeType identified by the given id.
     * 
     * @param id
     * 		the name or the oid of the AttributeType
     * @return
     * 		the AttributeType identified by the given id, or null if the 
     * AttributeType has not been found
     */
    public AttributeType getAttributeType( String id );


    /**
     * Gets the MatchingRule identified by the given id.
     * 
     * @param id
     *      the name or the oid of the AttributeType
     * @return
     *      the MatchingRule identified by the given id, or null if the 
     * MatchingRule has not been found
     */
    public MatchingRule getMatchingRule( String id );


    /**
     * Gets the Syntax identified by the given id.
     * 
     * @param id
     *      the name or the oid of the AttributeType
     * @return
     *      the Syntax identified by the given id, or null if the 
     * Syntax has not been found
     */
    public MatchingRule getSyntax( String id );


    /**
     * Adds an ObjectClass to the Schema.
     * 
     * @param oc
     *      the ObjectClass
     */
    public boolean addObjectClass( ObjectClass oc );


    /**
     * Adds an AttributeType to the Schema.
     * 
     * @param at
     *      the AttributeType
     */
    public boolean addAttributeType( AttributeType at );


    /**
     * Adds a MatchingRule from the Schema.
     * 
     * @param mr
     *      the MatchingRule
     */
    public boolean addMatchingRule( MatchingRule mr );


    /**
     * Adds a Syntax from the Schema.
     * 
     * @param syntax
     *      the Syntax
     */
    public boolean addSyntax( Syntax syntax );


    /**
     * Removes an ObjectClass from the Schema.
     *
     * @param oc
     *      the ObjectClass
     */
    public boolean removeObjectClass( ObjectClass oc );


    /**
     * Removes an AttributeType from the Schema.
     * 
     * @param at
     *      the AttributeType
     */
    public boolean removeAttributeType( AttributeType at );


    /**
     * Removes a MatchingRule from the Schema.
     * 
     * @param mr
     *      the MatchingRule
     */
    public boolean removeMatchingRule( MatchingRule mr );


    /**
     * Removes a Syntax from the Schema.
     * 
     * @param syntax
     *      the Syntax
     */
    public boolean removeSyntax( Syntax syntax );
}
