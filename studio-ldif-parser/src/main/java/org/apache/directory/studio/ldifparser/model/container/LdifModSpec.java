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
import java.util.Iterator;
import java.util.List;

import org.apache.directory.studio.ldifparser.model.LdifPart;
import org.apache.directory.studio.ldifparser.model.lines.LdifAttrValLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifModSpecSepLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifModSpecTypeLine;


public class LdifModSpec extends LdifContainer implements LdifPart
{

    private static final long serialVersionUID = 6708749639253050273L;


    protected LdifModSpec()
    {
    }


    public LdifModSpec( LdifModSpecTypeLine modSpecTypeLine )
    {
        super( modSpecTypeLine );
    }


    public void addAttrVal( LdifAttrValLine attrVal )
    {
        if ( attrVal == null )
            throw new IllegalArgumentException( "null argument" );
        this.parts.add( attrVal );
    }


    public void finish( LdifModSpecSepLine modSpecSepLine )
    {
        if ( modSpecSepLine == null )
            throw new IllegalArgumentException( "null argument" );
        this.parts.add( modSpecSepLine );
    }


    public LdifModSpecTypeLine getModSpecType()
    {
        return ( LdifModSpecTypeLine ) this.parts.get( 0 );
    }


    public LdifAttrValLine[] getAttrVals()
    {
        List l = new ArrayList();
        for ( Iterator it = this.parts.iterator(); it.hasNext(); )
        {
            Object o = it.next();
            if ( o instanceof LdifAttrValLine )
            {
                l.add( o );
            }
        }
        return ( LdifAttrValLine[] ) l.toArray( new LdifAttrValLine[l.size()] );
    }


    public LdifModSpecSepLine getModSpecSep()
    {
        if ( getLastPart() instanceof LdifModSpecSepLine )
        {
            return ( LdifModSpecSepLine ) getLastPart();
        }
        else
        {
            return null;
        }
    }


    public boolean isAdd()
    {
        return this.getModSpecType().isAdd();
    }


    public boolean isReplace()
    {
        return this.getModSpecType().isReplace();
    }


    public boolean isDelete()
    {
        return this.getModSpecType().isDelete();
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

        if ( this.getModSpecType() == null )
        {
            return false;
        }

        LdifAttrValLine[] attrVals = this.getAttrVals();
        if ( attrVals.length > 0 )
        {
            String att = this.getModSpecType().getUnfoldedAttributeDescription();
            for ( int i = 0; i < attrVals.length; i++ )
            {
                if ( !att.equalsIgnoreCase( attrVals[i].getUnfoldedAttributeDescription() ) )
                {
                    return false;
                }
            }
        }

        if ( isAdd() )
        {
            return attrVals.length > 0;
        }
        else if ( isDelete() )
        {
            return true;
        }
        else if ( isReplace() )
        {
            return true;
        }
        else
        {
            return false;
        }
    }


    public String getInvalidString()
    {
        if ( this.getModSpecType() == null )
        {
            return "Missing mod spec line ";
        }
        else if ( isAdd() && this.getAttrVals().length == 0 )
        {
            return "Modification must contain attribute value lines ";
        }

        LdifAttrValLine[] attrVals = this.getAttrVals();
        if ( attrVals.length > 0 )
        {
            String att = this.getModSpecType().getUnfoldedAttributeDescription();
            for ( int i = 0; i < attrVals.length; i++ )
            {
                if ( !att.equalsIgnoreCase( attrVals[i].getUnfoldedAttributeDescription() ) )
                {
                    return "Attribute descriptions don't match";
                }
            }
        }

        return null;
    }

}
