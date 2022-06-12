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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.database.utils.jdbc.NamedJdbcTemplate;
import org.wso2.carbon.database.utils.jdbc.exceptions.DataAccessException;
import org.wso2.carbon.identity.core.persistence.UmPersistenceManager;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.identity.organization.management.service.exception.OrganizationManagementServerException;
import org.wso2.carbon.identity.organization.management.service.util.Utils;
import org.wso2.carbon.identity.organization.management.tenant.association.internal.TenantAssociationDataHolder;
import org.wso2.carbon.identity.organization.management.tenant.association.listeners.TenantAssociationManagementListener;
import org.wso2.carbon.identity.organization.management.tenant.association.model.UserTenantAssociation;
import org.wso2.carbon.user.api.Tenant;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.common.AbstractUserStoreManager;
import org.wso2.carbon.user.core.common.User;
import org.wso2.carbon.user.core.service.RealmService;

import static org.wso2.carbon.identity.organization.management.service.constant.OrganizationManagementConstants.ErrorMessages.ERROR_CODE_ERROR_ADDING_USER_TENANT_ASSOC;
import static org.wso2.carbon.identity.organization.management.service.constant.OrganizationManagementConstants.ErrorMessages.ERROR_CODE_ERROR_CHECKING_ACTIVE_USER_TENANT_ASSOC;
import static org.wso2.carbon.identity.organization.management.tenant.association.Constants.SQLConstants.ADD_USER_TENANT_ASSOCIATION;
import static org.wso2.carbon.identity.organization.management.tenant.association.Constants.SQLConstants.IS_USER_TENANT_ASSOCIATION_ACTIVE;

public class TenantAssociationManagerImpl implements TenantAssociationManager {

    private static volatile TenantAssociationManager instance = new TenantAssociationManagerImpl();
    private static final Log LOG = LogFactory.getLog(TenantAssociationManagerImpl.class);

    private TenantAssociationManagerImpl() {

    }

    /**
     * @return
     */
    public static TenantAssociationManager getInstance() {

        return instance;
    }

    @Override
    public void addUserTenantAssociations(String userUUID, String tenantUUID)
            throws OrganizationManagementServerException {

        // TODO validate tenant and user are valid entities.

        // Check whether user already has an association with the tenant.

        // Do the association.
        UserTenantAssociation userTenantAssoc = new UserTenantAssociation(userUUID, tenantUUID, true);
        NamedJdbcTemplate namedJdbcTemplate = getNewTemplate();
        try {
            namedJdbcTemplate.executeInsert(ADD_USER_TENANT_ASSOCIATION,
                    namedPreparedStatement -> {
                        namedPreparedStatement.setString(1, userTenantAssoc.getUserUUID());
                        namedPreparedStatement.setString(2, userTenantAssoc.getTenantUUID());
                        namedPreparedStatement.setBoolean(3, userTenantAssoc.isAssociationStatus());
                    }, userTenantAssoc, false);
        } catch (DataAccessException e) {
            throw Utils.handleServerException(ERROR_CODE_ERROR_ADDING_USER_TENANT_ASSOC, e,
                    userTenantAssoc.getUserUUID(), userTenantAssoc.getTenantUUID());
        }
    }

    @Override
    public void deactivateUserTenantAssociation() {

    }

    @Override
    public User getTenantOwner(String tenantUUID) {

        return null;
    }

    @Override
    public boolean hasActiveOwnership(org.wso2.carbon.identity.application.common.model.User user, String tenantDomain) {

        // TODO : could find a constructor to convert app model user to carbon core user.
//        User coreUser = new User();
        UserTenantAssociation userTenantAssoc = new UserTenantAssociation();
        try {
            // Resolve UserID.
            int userTenantID = IdentityTenantUtil.getTenantId(user.getTenantDomain());
            RealmService realmService = TenantAssociationDataHolder.getRealmService();
            AbstractUserStoreManager userStoreManager = (AbstractUserStoreManager) realmService.
                    getTenantUserRealm(userTenantID).getUserStoreManager();
            User adminUser = userStoreManager.getUser(null, user.getUserName());
            String userUUID = adminUser != null ? adminUser.getUserID() : StringUtils.EMPTY;
            if (StringUtils.isBlank(userUUID)) {
                LOG.error("User UUID was not found for user: " + user.getUserName());
                return false;
            }
            // Resolve tenantUUID.
            int tenantID = IdentityTenantUtil.getTenantId(tenantDomain);
            Tenant tenant = realmService.getTenantManager().getTenant(tenantID);
            String tenantUUID = tenant.getTenantUniqueID();
            // Build association.
            userTenantAssoc.setUserUUID(userUUID);
            userTenantAssoc.setTenantUUID(tenantUUID);
            userTenantAssoc.setAssociationStatus(true);
            NamedJdbcTemplate namedJdbcTemplate = getNewTemplate();

            // TODO: Move to DAO layer.
            return namedJdbcTemplate.fetchSingleRecord(IS_USER_TENANT_ASSOCIATION_ACTIVE, (resultSet, rowNumber) ->
                            resultSet.getInt(1) > 0,
                    namedPreparedStatement -> {
                        namedPreparedStatement.setString(1, userTenantAssoc.getUserUUID());
                        namedPreparedStatement.setString(2, userTenantAssoc.getTenantUUID());
                        namedPreparedStatement.setBoolean(3, userTenantAssoc.isAssociationStatus());
                    });
        } catch (DataAccessException | UserStoreException e) {
             // TODO: throw error and handle in upper level.
//            throw Utils.handleServerException(ERROR_CODE_ERROR_CHECKING_ACTIVE_USER_TENANT_ASSOC, e,
//                    userTenantAssoc.getUserUUID(), userTenantAssoc.getTenantUUID());
        }
        return false;
    }

    private NamedJdbcTemplate getNewTemplate() {

        return new NamedJdbcTemplate(UmPersistenceManager.getInstance().getDataSource());
    }
}
