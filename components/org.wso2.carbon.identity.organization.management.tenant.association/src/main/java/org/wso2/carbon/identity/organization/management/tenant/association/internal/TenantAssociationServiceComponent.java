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

package org.wso2.carbon.identity.organization.management.tenant.association.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.wso2.carbon.identity.organization.management.tenant.association.listeners.TenantAssociationManagementListener;
import org.wso2.carbon.identity.organization.management.tenant.association.manager.TenantAssociationManager;
import org.wso2.carbon.identity.organization.management.tenant.association.manager.TenantAssociationManagerImpl;
import org.wso2.carbon.stratos.common.listeners.TenantMgtListener;
import org.wso2.carbon.user.core.service.RealmService;

/**
 * This class contains the implementation of the service component of the TenantAssociationManagement.
 */
@Component(
        name = "org.wso2.carbon.identity.organization.management.tenant.association.component",
        immediate = true
)
public class TenantAssociationServiceComponent {

    private static Log LOG = LogFactory.getLog(TenantAssociationServiceComponent.class);

    @Activate
    protected void activate(ComponentContext context) {

        TenantAssociationManager userTenantAssociationManager = TenantAssociationManagerImpl.getInstance();
        context.getBundleContext().registerService(TenantAssociationManager.class.getName(),
                userTenantAssociationManager, null);
        LOG.info("UserTenantAssociationManager service registered successfully.");

        TenantAssociationManagementListener tenantManagementListener = new TenantAssociationManagementListener();
        context.getBundleContext().registerService(TenantMgtListener.class.getName(), tenantManagementListener, null);
        LOG.info("Organization management related TenantAssociationManagementListener registered successfully.");
    }

    @Deactivate
    protected void deactivate(ComponentContext context) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Organization management related TenantAssociationManagement bundle is deactivated.");
        }
    }

    @Reference(
            name = "RealmService",
            service = org.wso2.carbon.user.core.service.RealmService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetRealmService")
    protected void setRealmService(RealmService realmService) {

        TenantAssociationDataHolder.setRealmService(realmService);
    }

    protected void unsetRealmService(RealmService realmService) {

        TenantAssociationDataHolder.setRealmService(null);
    }
}
