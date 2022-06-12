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

package org.wso2.carbon.identity.organization.management.tenant.association.manager;

import org.wso2.carbon.identity.organization.management.service.exception.OrganizationManagementServerException;
import org.wso2.carbon.user.core.common.User;

/**
 * This is the service interface for managing user-tenant associations.
 */
public interface TenantAssociationManager {

    void addUserTenantAssociations(String userUUID, String tenantUUID) throws OrganizationManagementServerException;

    void deactivateUserTenantAssociation();

    User getTenantOwner(String tenantUUID);

    boolean hasActiveOwnership(org.wso2.carbon.identity.application.common.model.User user, String tenantDomain);

}
