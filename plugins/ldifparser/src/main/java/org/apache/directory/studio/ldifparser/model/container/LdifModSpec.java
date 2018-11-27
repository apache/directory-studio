/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */

package org.apache.directory.studio.ldifparser.model.container;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.ldifparser.model.LdifPart;
import org.apache.directory.studio.ldifparser.model.lines.LdifAttrValLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifModSpecSepLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifModSpecTypeLine;


/**
 * A LDIF container for a ModSpec
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdifModSpec extends LdifContainer
{
    public LdifModSpec( LdifModSpecTypeLine modSpecTypeLine )
    {
        super( modSpecTypeLine );
    }


    public void addAttrVal( LdifAttrValLine attrVal )
    {
        if ( attrVal == null )
        {
            throw new IllegalArgumentException( "null argument" ); //$NON-NLS-1$
        }

        ldifParts.add( attrVal );
    }


    public void finish( LdifModSpecSepLine modSpecSepLine )
    {
        if ( modSpecSepLine == null )
        {
            throw new IllegalArgumentException( "null argument" ); //$NON-NLS-1$
        }

        ldifParts.add( modSpecSepLine );
    }


    public LdifModSpecTypeLine getModSpecType()
    {
        return ( LdifModSpecTypeLine ) ldifParts.get( 0 );
    }


    public LdifAttrValLine[] getAttrVals()
    {
        List<LdifAttrValLine> ldifAttrValLines = new ArrayList<LdifAttrValLine>();

        for ( LdifPart ldifPart : ldifParts )
        {
            if ( ldifPart instanceof LdifAttrValLine )
            {
                ldifAttrValLines.add( ( LdifAttrValLine ) ldifPart );
            }
        }

        return ldifAttrValLines.toArray( new LdifAttrValLine[ldifAttrValLines.size()] );
    }


    public LdifModSpecSepLine getModSpecSep()
    {
        LdifPart lastPart = getLastPart();

        if ( lastPart instanceof LdifModSpecSepLine )
        {
            return ( LdifModSpecSepLine ) lastPart;
        }
        else
        {
            return null;
        }
    }


    public boolean isAdd()
    {
        return getModSpecType().isAdd();
    }


    public boolean isReplace()
    {
        return getModSpecType().isReplace();
    }


    public boolean isDelete()
    {
        return getModSpecType().isDelete();
    }


    public static LdifModSpec createAdd( String attributeName )
    {
        return new LdifModSpec( LdifModSpecTypeLine.createAdd( attributeName ) );
    }


    public static LdifModSpec createReplace( String attributeName )
    {
        return new LdifModSpec( LdifModSpecTypeLine.createReplace( attributeName ) );
    }


    public static LdifModSpec createDelete( String attributeName )
    {
        return new LdifModSpec( LdifModSpecTypeLine.createDelete( attributeName ) );
    }


    public boolean isValid()
    {
        if ( !super.isAbstractValid() )
        {
            return false;
        }

        if ( getModSpecType() == null )
        {
            return false;
        }

        String att = getModSpecType().getUnfoldedAttributeDescription();
        int sizeAttrVals = 0;

        for ( LdifPart ldifPart : ldifParts )
        {
            if ( ldifPart instanceof LdifAttrValLine )
            {
                if ( !att.equalsIgnoreCase( ( ( LdifAttrValLine ) ldifPart ).getUnfoldedAttributeDescription() ) )
                {
                    return false;
                }
                else
                {
                    sizeAttrVals++;
                }
            }
        }

        if ( isAdd() )
        {
            return sizeAttrVals > 0;
        }
        else
        {
            return isDelete() || isReplace();
        }
    }


    public String getInvalidString()
    {
        if ( getModSpecType() == null )
        {
            return "Missing mod spec line ";
        }

        int sizeAttrVals = 0;
        String att = getModSpecType().getUnfoldedAttributeDescription();

        for ( LdifPart ldifPart : ldifParts )
        {
            if ( ldifPart instanceof LdifAttrValLine )
            {
                if ( !att.equalsIgnoreCase( ( ( LdifAttrValLine ) ldifPart ).getUnfoldedAttributeDescription() ) )
                {
                    return "Attribute descriptions don't match";
                }

                sizeAttrVals++;
            }
        }

        if ( isAdd() && sizeAttrVals == 0 )
        {
            return "Modification must contain attribute value lines ";
        }

        return null;
    }
}
