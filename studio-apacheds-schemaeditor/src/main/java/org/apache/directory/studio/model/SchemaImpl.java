package org.apache.directory.studio.model;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.shared.ldap.schema.AttributeType;
import org.apache.directory.shared.ldap.schema.MatchingRule;
import org.apache.directory.shared.ldap.schema.ObjectClass;
import org.apache.directory.shared.ldap.schema.Syntax;


public class SchemaImpl implements Schema
{
    /** The name */
    private String name;

    /** The AttributeType List */
    private List<AttributeType> attributeTypes = new ArrayList<AttributeType>();

    /** The ObjectClass List */
    private List<ObjectClass> objectClasses = new ArrayList<ObjectClass>();

    /** The MatchingRule List */
    private List<MatchingRule> matchingRules = new ArrayList<MatchingRule>();

    /** The Syntax List */
    private List<Syntax> syntaxes = new ArrayList<Syntax>();


    public SchemaImpl( String name )
    {
        this.name = name;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.model.Schema#addAttributeType(org.apache.directory.shared.ldap.schema.AttributeType)
     */
    public boolean addAttributeType( AttributeType at )
    {
        return attributeTypes.add( at );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.model.Schema#addMatchingRule(org.apache.directory.shared.ldap.schema.MatchingRule)
     */
    public boolean addMatchingRule( MatchingRule mr )
    {
        return matchingRules.add( mr );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.model.Schema#addObjectClass(org.apache.directory.shared.ldap.schema.ObjectClass)
     */
    public boolean addObjectClass( ObjectClass oc )
    {
        return objectClasses.add( oc );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.model.Schema#addSyntax(org.apache.directory.shared.ldap.schema.Syntax)
     */
    public boolean addSyntax( Syntax syntax )
    {
        return syntaxes.add( syntax );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.model.Schema#getAttributeType(java.lang.String)
     */
    public AttributeType getAttributeType( String id )
    {
        // TODO Auto-generated method stub
        return null;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.model.Schema#getAttributeTypes()
     */
    public List<AttributeType> getAttributeTypes()
    {
        return attributeTypes;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.model.Schema#getMatchingRule(java.lang.String)
     */
    public MatchingRule getMatchingRule( String id )
    {
        // TODO Auto-generated method stub
        return null;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.model.Schema#getMatchingRules()
     */
    public List<MatchingRule> getMatchingRules()
    {
        return matchingRules;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.model.Schema#getName()
     */
    public String getName()
    {
        return name;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.model.Schema#getObjectClass(java.lang.String)
     */
    public ObjectClass getObjectClass( String id )
    {
        // TODO Auto-generated method stub
        return null;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.model.Schema#getObjectClasses()
     */
    public List<ObjectClass> getObjectClasses()
    {
        return objectClasses;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.model.Schema#getSyntax(java.lang.String)
     */
    public MatchingRule getSyntax( String id )
    {
        // TODO Auto-generated method stub
        return null;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.model.Schema#getSyntaxes()
     */
    public List<Syntax> getSyntaxes()
    {
        return syntaxes;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.model.Schema#removeAttributeType(org.apache.directory.shared.ldap.schema.AttributeType)
     */
    public boolean removeAttributeType( AttributeType at )
    {
        return attributeTypes.remove( at );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.model.Schema#removeMatchingRule(org.apache.directory.shared.ldap.schema.MatchingRule)
     */
    public boolean removeMatchingRule( MatchingRule mr )
    {
        return matchingRules.remove( mr );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.model.Schema#removeObjectClass(org.apache.directory.shared.ldap.schema.ObjectClass)
     */
    public boolean removeObjectClass( ObjectClass oc )
    {
        return objectClasses.remove( oc );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.model.Schema#removeSyntax(org.apache.directory.shared.ldap.schema.Syntax)
     */
    public boolean removeSyntax( Syntax syntax )
    {
        return syntaxes.remove( syntax );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.model.Schema#setName(java.lang.String)
     */
    public void setName( String name )
    {
        this.name = name;
    }
}
