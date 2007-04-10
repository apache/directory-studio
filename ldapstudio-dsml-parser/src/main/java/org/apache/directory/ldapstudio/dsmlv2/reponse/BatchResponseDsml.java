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
package org.apache.directory.ldapstudio.dsmlv2.reponse;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.ldapstudio.dsmlv2.DsmlDecorator;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;


/**
 * This class represents the Batch Response. It can be used to generate an the XML String of a BatchResponse.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BatchResponseDsml
{
    /** The Responses list */
    private List<DsmlDecorator> responses;


    /**
     * Creates a new instance of BatchResponseDsml.
     */
    public BatchResponseDsml()
    {
        responses = new ArrayList<DsmlDecorator>();
    }


    /**
     * Adds a request to the Batch Response DSML.
     *
     * @param response
     *      the request to add
     * @return
     *      true (as per the general contract of the Collection.add method).
     */
    public boolean addResponse( DsmlDecorator response )
    {
        return responses.add( response );
    }


    /**
     * Removes a request from the Batch Response DSML.
     *
     * @param response
     *      the request to remove
     * @return
     *      true if this list contained the specified element.
     */
    public boolean removeResponse( DsmlDecorator response )
    {
        return responses.remove( response );
    }


    /**
     * Converts the Batch Response to its XML representation in the DSMLv2 format.
     */
    public String toDsml()
    {
        Document document = DocumentHelper.createDocument();
        Element element = document.addElement( "batchResponse" );

        for ( DsmlDecorator response : responses )
        {
            response.toDsml( element );
        }

        return document.asXML();
    }
}
