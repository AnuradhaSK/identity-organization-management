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

package org.wso2.carbon.identity.organization.management.tenant.association.listeners;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.core.AbstractIdentityTenantMgtListener;
import org.wso2.carbon.identity.organization.management.service.exception.OrganizationManagementServerException;
import org.wso2.carbon.identity.organization.management.tenant.association.internal.TenantAssociationDataHolder;
import org.wso2.carbon.identity.organization.management.tenant.association.manager.TenantAssociationManagerImpl;
import org.wso2.carbon.stratos.common.beans.TenantInfoBean;
import org.wso2.carbon.user.api.Tenant;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.common.AbstractUserStoreManager;
import org.wso2.carbon.user.core.common.User;
import org.wso2.carbon.user.core.service.RealmService;

/**
 * This class contains the implementation of the tenant management listener.  This listener will be used to add tenant
 * associations for the tenant admin during the tenant creation flow and also deactivate all the user associations for a
 * tenant during the tenant deactivation.
 */
public class TenantAssociationManagementListener extends AbstractIdentityTenantMgtListener {

    private static final Log LOG = LogFactory.getLog(TenantAssociationManagementListener.class);

    @Override
    public void onTenantCreate(TenantInfoBean tenantInfo) {

        if (!isEnable()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Organization management related TenantAssociationManagementListener is not enabled");
            }
            return;
        }
        int tenantId = tenantInfo.getTenantId();
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                    "Organization management related TenantAssociationManagementListener fired for tenant creation for Tenant ID: " +
                            tenantId);
        }
        try {
            RealmService realmService = TenantAssociationDataHolder.getRealmService();
            Tenant tenant = realmService.getTenantManager().getTenant(tenantId);
            // Association will be created only if tenant created with isExternalOwnerAssociation = true;
            if (!tenant.isExternalOwnerAssociation()) {
                return;
            }
            String adminUsername = realmService.getTenantUserRealm(tenantId).getRealmConfiguration().getAdminUserName();
            String tenantUuid = tenant.getTenantUniqueID();
            if (StringUtils.isBlank(tenantUuid)) {
                LOG.error("Tenant UUID was not found for tenant: " + tenantId + ". Therefore, tenant association " +
                        "will not set");
                return;
            }
            // TODO: this need to be passed as a data
            int tenantOwnerBelongingTenantId = -1234;
            // Get userstore manager of owner belonging tenant
            AbstractUserStoreManager userStoreManager = (AbstractUserStoreManager) realmService.
                    getTenantUserRealm(tenantOwnerBelongingTenantId).getUserStoreManager();
            User adminUser = userStoreManager.getUser(null, adminUsername);
            String adminUUID = adminUser != null ? adminUser.getUserID() : StringUtils.EMPTY;
            if (StringUtils.isBlank(adminUUID)) {
                LOG.error("User UUID was not found for user: " + adminUsername + ". Therefore, tenant association " +
                        "will not set with tenant: " + tenantUuid);
                return;
            }
            TenantAssociationManagerImpl.getInstance().addUserTenantAssociations(adminUUID, tenantUuid);
        } catch (UserStoreException | OrganizationManagementServerException e) {
            String error = "Error occurred while adding user-tenant association for the tenant id: " + tenantId;
            LOG.error(error);
        }
    }
}
