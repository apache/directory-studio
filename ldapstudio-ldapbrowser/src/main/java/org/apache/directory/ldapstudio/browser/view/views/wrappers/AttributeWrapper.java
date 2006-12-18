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

package org.apache.directory.ldapstudio.browser.view.views.wrappers;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;

/**
 * AttributeWrapper used to display an attribute and the number of its values in
 * the Attributes View
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class AttributeWrapper {
    private Attribute attribute;

    private EntryWrapper entry;

    private List<AttributeValueWrapper> children;

    /**
         * Creates a new instance of AttributeWrapper.
         * 
         * @param attribute
         *                the attribute to wrap
         * @param entry
         *                the associated EntryWrapper
         */
    public AttributeWrapper(Attribute attribute, EntryWrapper entry) {
	this.attribute = attribute;
	this.entry = entry;
    }

    /**
         * Gets the children of the object
         * 
         * @return the children of the object
         */
    public Object[] getChildren() {
	if (children == null) {
	    children = new ArrayList<AttributeValueWrapper>();

	    try {
		NamingEnumeration ne = attribute.getAll();

		while (ne.hasMoreElements()) {
		    AttributeValueWrapper avw = new AttributeValueWrapper(ne
			    .nextElement(), this);
		    children.add(avw);
		}
	    } catch (NamingException e) {
		// TODO Add a log into Eclipse system
		e.printStackTrace();
	    }
	}
	return children.toArray();
    }

    /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object,
         *      int)
         */
    public String getColumnText(Object element, int columnIndex) {
	if (columnIndex == 0) {
	    return attribute.getID();
	} else if (columnIndex == 1) {
	    int nb = getChildren().length;
	    if (nb == 1) {
		// If there's only one value we display it
		try {
		    Object value;
		    value = attribute.get();
		    if (value instanceof String) {
			return (String) value;

		    }
		    return "(Binary value)";

		} catch (NamingException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    } else {
		return "(" + nb + " attributes)";
	    }
	}
	return "";
    }

    /**
         * Gets the HasChildren Flag
         * 
         * @return
         */
    public boolean hasChildren() {
	return (getChildren().length > 1);
    }

    /**
         * Gets the name of the attribute
         * 
         * @return the name of the attribute
         */
    public String getName() {
	return attribute.getID();
    }
}
