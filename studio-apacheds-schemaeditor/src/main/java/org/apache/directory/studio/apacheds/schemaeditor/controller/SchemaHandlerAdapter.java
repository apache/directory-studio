
package org.apache.directory.studio.apacheds.schemaeditor.controller;

import org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.MatchingRuleImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.Schema;
import org.apache.directory.studio.apacheds.schemaeditor.model.SyntaxImpl;

/**
 * This adapter class provides default implementations for the methods 
 * described by the SchemaHandlerListener interface.
 * <p>
 * Classes that wish to deal with schema handling events can extend this class 
 * and override only the methods which they are interested in. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public abstract class SchemaHandlerAdapter implements SchemaHandlerListener
{
    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaHandlerListener#attributeTypeAdded(org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl)
     */
    public void attributeTypeAdded( AttributeTypeImpl at )
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaHandlerListener#attributeTypeModified(org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl)
     */
    public void attributeTypeModified( AttributeTypeImpl at )
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaHandlerListener#attributeTypeRemoved(org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl)
     */
    public void attributeTypeRemoved( AttributeTypeImpl at )
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaHandlerListener#matchingRuleAdded(org.apache.directory.studio.apacheds.schemaeditor.model.MatchingRuleImpl)
     */
    public void matchingRuleAdded( MatchingRuleImpl mr )
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaHandlerListener#matchingRuleModified(org.apache.directory.studio.apacheds.schemaeditor.model.MatchingRuleImpl)
     */
    public void matchingRuleModified( MatchingRuleImpl mr )
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaHandlerListener#matchingRuleRemoved(org.apache.directory.studio.apacheds.schemaeditor.model.MatchingRuleImpl)
     */
    public void matchingRuleRemoved( MatchingRuleImpl mr )
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaHandlerListener#objectClassAdded(org.apache.directory.studio.apacheds.schemaeditor.model.ObjectClassImpl)
     */
    public void objectClassAdded( ObjectClassImpl oc )
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaHandlerListener#objectClassModified(org.apache.directory.studio.apacheds.schemaeditor.model.ObjectClassImpl)
     */
    public void objectClassModified( ObjectClassImpl oc )
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaHandlerListener#objectClassRemoved(org.apache.directory.studio.apacheds.schemaeditor.model.ObjectClassImpl)
     */
    public void objectClassRemoved( ObjectClassImpl oc )
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaHandlerListener#schemaAdded(org.apache.directory.studio.apacheds.schemaeditor.model.Schema)
     */
    public void schemaAdded( Schema schema )
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaHandlerListener#schemaRemoved(org.apache.directory.studio.apacheds.schemaeditor.model.Schema)
     */
    public void schemaRemoved( Schema schema )
    {   
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaHandlerListener#syntaxAdded(org.apache.directory.studio.apacheds.schemaeditor.model.SyntaxImpl)
     */
    public void syntaxAdded( SyntaxImpl syntax )
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaHandlerListener#syntaxModified(org.apache.directory.studio.apacheds.schemaeditor.model.SyntaxImpl)
     */
    public void syntaxModified( SyntaxImpl syntax )
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaHandlerListener#syntaxRemoved(org.apache.directory.studio.apacheds.schemaeditor.model.SyntaxImpl)
     */
    public void syntaxRemoved( SyntaxImpl syntax )
    {
    }
}
