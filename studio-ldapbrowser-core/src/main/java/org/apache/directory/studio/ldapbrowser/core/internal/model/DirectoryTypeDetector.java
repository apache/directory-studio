
package org.apache.directory.studio.ldapbrowser.core.internal.model;

import org.apache.directory.studio.ldapbrowser.core.model.IRootDSE;

public interface DirectoryTypeDetector
{
    /**
     * Tries to detect the directory type from the given Root DSE.
     * 
     * @param rootDSE the Root DSE
     * @return the directory type or null if unknown
     */
    public String detectDirectoryType( IRootDSE rootDSE );
}
