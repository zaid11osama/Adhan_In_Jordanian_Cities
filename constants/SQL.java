package com.arabbank.hdf.uam.brain.constants;

public class SQL {
	public static class Column {
		public static class NonAdUsers {
			public static final String OU = "OU";
		}

		public static class ExpiryDays {
			public static final String USER_CLASS = "userClass";
			public static final String EXPIRY_DAYS = "expiryDays";
		}
	}

	public static class Table {
		public static final String UAM_VALIDATION_AD_GROUPS = "UAM_VALIDATION_AD_GROUPS";
		public static final String UAM_CONFIG_ADGROUP_NAME_CONVENTION = "UAM_CONFIG_AD_GROUP_NAMING_CONVENTION";
		public static final String UAM_CONFIG_AD_GROUPS_OU_LOCATION = "UAM_CONFIG_AD_GROUPS_OU_LOCATION";
		public static final String UAM_AUTH_MATRIX_CURRENT = "UAM_AUTH_MATRIX_CURRENT";
		public static final String UAM_VALIDATION_NON_AD_INVALID_PRIVILEGE_USERS = "UAM_VALIDATION_NON_AD_INVALID_PRIVILEGE_USERS";
		public static final String UAM_TEMP_AND_THIRDPARTY_USERS = "UAM_Temp_ThirdParty_Users";
		public static final String VALIDATE_ACTIVE_DIRECTORY_GENERIC_USERS = "UAM_VALIDATE_ACTIVE_DIRECTORY_GENERIC_ACCOUNTS";
		public static final String UAM_VALIDATE_ACTIVE_DIRECTORY_SERVICE_ACCOUNTS = "UAM_VALIDATE_ACTIVE_DIRECTORY_SERVICE_ACCOUNTS";
		public static final String UAM_VALIDATE_ACTIVE_DIRECTORY_DUPLICATED_USERS = "UAM_VALIDATE_ACTIVE_DIRECTORY_DUPLICATED_USERS";
		public static final String UAM_VALIDATE_ACTIVE_DIRECTORY_FLAGGED_USERS = "UAM_VALIDATE_ACTIVE_DIRECTORY_FLAGGED_USERS";
		public static final String UAM_CLASSIFICATION = "UAM_CLASSIFICATION";
	}

	public static class File {
		public static final String insert_ADD_CLASSIFICATION_USERS = "insert_all_classification_users.sql";
		public static final String insert_ACTIVE_DIRECTORY_GENERIC_ACCOUNTS = "insert_ad_generic_account.sql";
		public static final String INSERT_AD_SERVICE_USERS = "insert_ad_service_users.sql";

		public static final String READ_CLASSIFICATION_RECORDS = "read_non_ad_manual_classification.sql";
		public static final String INSERT_NON_AD_USERS_WITH_INVALID_GROUPS = "insert_non_ad_users_with_invalid_groups.sql";
		public static final String INSERT_FLAGGED_AD_GROUPS = "insert_flagged_ad_groups.sql";
		public static final String INSERT_THIRD_TEMP_USERS = "insert_temp_third_users.sql";
		public static final String READ_UAM_CHECKER_EMAIL = "read_uam_checker_email.sql";
		public static final String SQL_ALL_CLASSIFICATION = "read_all_classification_users.sql";
        public static final String INSERT_JOB_LOG = "insert_job_log.sql";
        public static final String INSERT_AD_DUPLICATE_USERS = "insert_ad_duplicate_users.sql";
        public static final String INSERT_AD_FLAGGED_USERS = "insert_ad_flagged_users.sql";
		public static final String INSERT_AD_ALERTS = "insert_ad_alerts.sql";
		public static final String READ_NON_ACTIVE_FLAGGED = "read_non_active_flagged.sql";
		public static final String READ_NON_ACTIVE_DUPLICATE = "read_non_active_duplicate.sql";
		public static final String READ_NON_ACTIVE_AUTHORITY_DEVIATION = "read_non_active_authority_deviation.sql";
		public static final String READ_ACTIVE_FLAGGED = "read_active_flagged.sql";
		public static final String READ_ACTIVE_DUPLICATE = "read_active_duplicate.sql";
		public static final String READ_EXPIRED_TEMP = "read_expired_temp.sql";
		public static final String READ_FLAGGED_GROUP = "read_flagged_group.sql";
		public static final String READ_PASSWORD_EXPIRY = "read_password_expiry.sql";
		public static final String READ_GENERIC_ACCOUNTS = "read_generic_accounts.sql";
	}

	public static class Statement {
		public static final String SELECT_NEVER_LOGGED_IN_THRESHOLD = "select neverLoggedinThreshold from UAM_CONFIG_NEVER_LOGGEDIN_THRESHOLD";
	}
}
