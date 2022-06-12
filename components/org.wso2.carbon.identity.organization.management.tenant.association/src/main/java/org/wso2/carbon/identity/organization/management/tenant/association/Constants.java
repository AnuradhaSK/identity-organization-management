/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com).
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.organization.management.tenant.association;

/**
 * Constants related to user-tenant association.
 */
public class Constants {

//    private static final String ORGANIZATION_MANAGEMENT_ERROR_CODE_PREFIX = "ORG-";
//
//    public enum ErrorMessages {
//        ERROR_CODE_ERROR_ADDING_USER_TENANT_ASSOC("65201", "Unable to create the user tenant association.",
//                "Server encountered an error while creating the association for user: %s , with tenant: %s.");
//
//        private final String code;
//        private final String message;
//        private final String description;
//
//        ErrorMessages(String code, String message, String description) {
//
//            this.code = code;
//            this.message = message;
//            this.description = description;
//        }
//
//        public String getCode() {
//
//            return ORGANIZATION_MANAGEMENT_ERROR_CODE_PREFIX + code;
//        }
//
//        public String getMessage() {
//
//            return message;
//        }
//
//        public String getDescription() {
//
//            return description;
//        }
//    }

    public static class SQLConstants {

    public static final String ADD_USER_TENANT_ASSOCIATION = "INSERT INTO UM_USER_TENANT_ASC (UM_USER_UUID, UM_TENANT_UUID, UM_ACTIVE_STATUS) VALUES (?, ?, ?)";
    public static final String IS_USER_TENANT_ASSOCIATION_ACTIVE = "SELECT COUNT(1) FROM UM_USER_TENANT_ASC WHERE UM_USER_UUID = ? AND UM_TENANT_UUID = ? AND UM_ACTIVE_STATUS = ?";

    }
}
