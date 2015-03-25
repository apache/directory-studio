package org.apache.directory.studio.openldap.config.editor.overlays;


/**
 * This enum describes the various type of overlays.
 */
public enum OverlayType
{
    /** None */
    NONE,

    /** Acess Log */
    ACCESS_LOG,

    /** Audit Log */
    AUDIT_LOG,
    
    /** Chain */
    CHAIN,
    
    /** Dist Proc */
    DIST_PROC,
    
    /** PBind */
    PBIND,
    
    /** Password Policy */
    PASSWORD_POLICY,
    
    /** SyncProv */
    SYNC_PROV;
}