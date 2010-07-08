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

package org.apache.directory.studio.ldapbrowser.core;


import org.eclipse.osgi.util.NLS;


/**
 * This class contains most of the Strings used by the Plugin
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class BrowserCoreMessages extends NLS
{
    private static final String BUNDLE_NAME = "org.apache.directory.studio.ldapbrowser.core.browsercoremessages"; //$NON-NLS-1$


    /**
     * Creates a new instance of BrowserCoreMessages.
     */
    private BrowserCoreMessages()
    {
    }

    static
    {
        // initialize resource bundle
        NLS.initializeMessages( BUNDLE_NAME, BrowserCoreMessages.class );
    }

    public static String copy_n_of_s;

    public static String event__added_att_to_dn;

    public static String event__deleted_att_from_dn;

    public static String event__dn_attributes_initialized;

    public static String event__dn_children_initialized;

    public static String event__bulk_modification;

    public static String event__empty_value_added_to_att_at_dn;

    public static String event__empty_value_deleted_from_att_at_dn;

    public static String event__added_dn;

    public static String event__deleted_dn;

    public static String event__moved_oldrdn_from_oldparent_to_newparent;

    public static String event__renamed_olddn_to_newdn;

    public static String event__added_val_to_att_at_dn;

    public static String event__deleted_val_from_att_at_dn;

    public static String event__replaced_oldval_by_newval_at_att_at_dn;

    public static String event__renamed_oldval_by_newval_at_dn;

    public static String jobs__copy_entries_source_and_target_are_equal;

    public static String model__empty_connection;

    public static String model__empty_entry;

    public static String model__empty_attribute;

    public static String model__empty_value;

    public static String model__empty_url;

    public static String model__empty_dn;

    public static String model__empty_rdn;

    public static String model__empty_password;

    public static String model__invalid_url;

    public static String model__invalid_protocol;

    public static String model__attribute_does_not_exist;

    public static String model__attribute_already_exists;

    public static String model__attributes_entry_is_not_myself;

    public static String model__url_no_attributes;

    public static String model__url_no_dn;

    public static String model__url_no_extensions;

    public static String model__url_no_filter;

    public static String model__url_no_host;

    public static String model__url_no_port;

    public static String model__url_no_protocol;

    public static String model__url_no_scope;

    public static String model__values_attribute_is_not_myself;

    public static String model__move_between_different_connections_not_supported;

    public static String model__copied_n_entries;

    public static String model__deleted_n_entries;

    public static String model__retrieved_n_entries;

    public static String model__retrieved_1_entry;

    public static String model__no_connection_provider;

    public static String model__connecting;

    public static String model__binding;

    public static String model__loading_rootdse;

    public static String model__error_loading_rootdse;

    public static String model__setting_base_dn;

    public static String model__error_setting_base_dn;

    public static String model__error_setting_metadata;

    public static String model__loading_schema;

    public static String model__no_schema_information;

    public static String model__missing_schema_location;

    public static String model__error_loading_schema;

    public static String model__no_such_entry;

    public static String model__error_logging_modification;

    public static String model__no_hash;

    public static String model__unsupported_hash;

    public static String model__invalid_hash;

    public static String ldif__imported_n_entries_m_errors;

    public static String ldif__n_errors_see_logfile;

    public static String ldif__imported_into_host_port_on_date;

    public static String ldif__import_into_host_port_failed_on_date;

    public static String ldif__error_msg;

    public static String dsml__n_errors_see_responsefile;

    public static String model__no_connection;

    public static String model__no_auth_handler;

    public static String model__no_credentials;

    public static String model__no_referral_handler;

    public static String model__no_referral_connection;

    public static String model__invalid_record;

    public static String model__unknown_host;

    public static String jobs__error_occurred;

    public static String jobs__check_bind_name;

    public static String jobs__check_bind_task;

    public static String jobs__check_bind_error;

    public static String jobs__check_network_name;

    public static String jobs__check_network_task;

    public static String jobs__check_network_error;

    public static String jobs__fetch_basedns_name;

    public static String jobs__fetch_basedns_task;

    public static String jobs__fetch_basedns_error;

    public static String jobs__copy_entries_name_1;

    public static String jobs__copy_entries_name_n;

    public static String jobs__copy_entries_task_1;

    public static String jobs__copy_entries_task_n;

    public static String jobs__copy_entries_error_1;

    public static String jobs__copy_entries_error_n;

    public static String jobs__create_entry_name_1;

    public static String jobs__create_entry_name_n;

    public static String jobs__create_entry_task_1;

    public static String jobs__create_entry_task_n;

    public static String jobs__create_entry_error_1;

    public static String jobs__create_entry_error_n;

    public static String jobs__create_values_name_1;

    public static String jobs__create_values_name_n;

    public static String jobs__create_values_task_1;

    public static String jobs__create_values_task_n;

    public static String jobs__create_values_error_1;

    public static String jobs__create_values_error_n;

    public static String jobs__delete_attributes_name_1;

    public static String jobs__delete_attributes_name_n;

    public static String jobs__delete_attributes_task_1;

    public static String jobs__delete_attributes_task_n;

    public static String jobs__delete_attributes_error_1;

    public static String jobs__delete_attributes_error_n;

    public static String jobs__delete_entries_name_1;

    public static String jobs__delete_entries_name_n;

    public static String jobs__delete_entries_task_1;

    public static String jobs__delete_entries_task_n;

    public static String jobs__delete_entries_error_1;

    public static String jobs__delete_entries_error_n;

    public static String jobs__execute_ldif_name;

    public static String jobs__execute_ldif_task;

    public static String jobs__execute_ldif_error;

    public static String jobs__export_cvs_error;

    public static String jobs__export_csv_name;

    public static String jobs__export_csv_task;

    public static String jobs__export_ldif_name;

    public static String jobs__export_ldif_task;

    public static String jobs__export_ldif_error;

    public static String jobs__export_dsml_name;

    public static String jobs__export_dsml_task;

    public static String jobs__export_dsml_error;

    public static String jobs__export_progress;

    public static String jobs__progressmonitor_check_cancellation;

    public static String jobs__progressmonitor_report_progress;

    public static String jobs__export_xls_name;

    public static String jobs__export_xls_task;

    public static String jobs__export_xls_error;

    public static String jobs__export_odf_name;

    public static String jobs__export_odf_task;

    public static String jobs__export_odf_error;

    public static String jobs__import_ldif_name;

    public static String jobs__import_ldif_task;

    public static String jobs__import_ldif_error;

    public static String jobs__import_dsml_name;

    public static String jobs__import_dsml_task;

    public static String jobs__import_dsml_error;

    public static String jobs__init_entries_title_attandsub;

    public static String jobs__init_entries_title_subonly;

    public static String jobs__init_entries_title_attonly;

    public static String jobs__init_entries_title;

    public static String jobs__init_entries_task;

    public static String jobs__init_entries_progress_att;

    public static String jobs__init_entries_progress_sub;

    public static String jobs__init_entries_progress_subcount;

    public static String jobs__init_entries_error_1;

    public static String jobs__init_entries_error_n;

    public static String jobs__modify_value_name;

    public static String jobs__modify_value_task;

    public static String jobs__modify_value_error;

    public static String jobs__open_connections_name_1;

    public static String jobs__open_connections_name_n;

    public static String jobs__open_connections_task;

    public static String jobs__open_connections_error_1;

    public static String jobs__open_connections_error_n;

    public static String jobs__read_entry_name;

    public static String jobs__read_entry_task;

    public static String jobs__read_entry_error;

    public static String jobs__reload_schemas_name_1;

    public static String jobs__reload_schemas_name_n;

    public static String jobs__reload_schemas_task;

    public static String jobs__reload_schemas_error_1;

    public static String jobs__reload_schemas_error_n;

    public static String jobs__move_entry_name_1;

    public static String jobs__move_entry_name_n;

    public static String jobs__move_entry_task_1;

    public static String jobs__move_entry_task_n;

    public static String jobs__move_entry_error_1;

    public static String jobs__move_entry_error_n;

    public static String jobs__rename_entry_name;

    public static String jobs__rename_entry_task;

    public static String jobs__rename_entry_error;

    public static String jobs__rename_value_name_1;

    public static String jobs__rename_value_name_n;

    public static String jobs__rename_value_task_1;

    public static String jobs__rename_value_task_n;

    public static String jobs__rename_value_error_1;

    public static String jobs__rename_value_error_n;

    public static String jobs__search_name;

    public static String jobs__search_task;

    public static String jobs__search_error_1;

    public static String jobs__search_error_n;

    public static String model__empty_string_value;

    public static String model__empty_binary_value;

    public static String model__invalid_rdn;

    public static String model_filter_missing_closing_parenthesis;

    public static String model_filter_missing_filter_expression;

    public static String model__quick_search_name;

}
