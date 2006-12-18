package org.apache.directory.ldapstudio.browser.view.views.wrappers;

/**
 * AttributeValueWrapper used to display an attribute value in the Attributes
 * View
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class AttributeValueWrapper {
    private Object value;

    private AttributeWrapper parent;

    public AttributeValueWrapper(Object value, AttributeWrapper parent) {
	this.value = value;
	this.parent = parent;
    }

    /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object,
         *      int)
         */
    public String getColumnText(Object element, int columnIndex) {
	if (columnIndex == 0) {
	    return ""; // The first column needs an entry String
	} else if (columnIndex == 1) {
	    if (value instanceof String) {
		return (String) value;
	    }
	}

	return "";
    }

    /**
         * Gets the parent element
         * 
         * @return the parent element
         */
    public AttributeWrapper getParent() {
	return parent;
    }
}
