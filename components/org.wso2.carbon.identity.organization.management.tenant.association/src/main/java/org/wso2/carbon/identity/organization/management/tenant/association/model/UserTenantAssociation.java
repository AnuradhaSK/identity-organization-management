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

package org.wso2.carbon.identity.organization.management.tenant.association.model;

/**
 * User tenant association model.
 */
public class UserTenantAssociation {

    private String userUUID;
    private String tenantUUID;
    private boolean associationStatus;

    public UserTenantAssociation(String userUUID, String tenantUUID, boolean associationStatus) {

        this.userUUID = userUUID;
        this.tenantUUID = tenantUUID;
        this.associationStatus = associationStatus;
    }

    public UserTenantAssociation(String userUUID, String tenantUUID) {

        this.userUUID = userUUID;
        this.tenantUUID = tenantUUID;
    }

    public UserTenantAssociation() {

    }

    public String getUserUUID() {

        return userUUID;
    }

    public void setUserUUID(String userUUID) {

        this.userUUID = userUUID;
    }

    public String getTenantUUID() {

        return tenantUUID;
    }

    public void setTenantUUID(String tenantUUID) {

        this.tenantUUID = tenantUUID;
    }

    public boolean isAssociationStatus() {

        return associationStatus;
    }

    public void setAssociationStatus(boolean associationStatus) {

        this.associationStatus = associationStatus;
    }
}
