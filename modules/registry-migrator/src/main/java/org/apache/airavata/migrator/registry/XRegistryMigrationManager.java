/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/

package org.apache.airavata.migrator.registry;

import org.apache.airavata.common.registry.api.exception.RegistryException;
import org.apache.airavata.commons.gfac.type.ApplicationDeploymentDescription;
import org.apache.airavata.commons.gfac.type.HostDescription;
import org.apache.airavata.commons.gfac.type.ServiceDescription;
import org.apache.airavata.registry.api.impl.AiravataJCRRegistry;
import org.apache.xmlbeans.XmlException;
import org.ogce.schemas.gfac.beans.ApplicationBean;
import org.ogce.schemas.gfac.beans.HostBean;
import org.ogce.schemas.gfac.beans.ServiceBean;
import org.ogce.xregistry.client.XRegistryClient;
import org.ogce.xregistry.client.XRegistryClientUtil;
import org.ogce.xregistry.utils.XRegistryClientException;
import xregistry.generated.FindAppDescResponseDocument;
import xregistry.generated.HostDescData;
import xregistry.generated.ServiceDescData;

import javax.jcr.RepositoryException;
import javax.xml.namespace.QName;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class XRegistryMigrationManager {
    private static String migrationPropertiesFile = null;
    private static AiravataJCRRegistry jcrRegistry = null;
    private static String jcrRegistryURL = null;
    private static String jcrUsername = null;
    private static String jcrPassword = null;

    public XRegistryMigrationManager(String propertyFile) {
        try {
            migrationPropertiesFile = propertyFile;
            loadProperties(propertyFile);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static void main(String[] args) throws XRegistryClientException {
        XRegistryMigrationManager manager = new XRegistryMigrationManager(args[0]);
        manager.migrate();
    }

    /**
     * Migrates the the resources from XRegistry to the Airavata Registry.
     *
     * @throws XRegistryClientException XRegistryClientException
     */
    public void migrate() throws XRegistryClientException {
        Map<String,String> config = new HashMap<String,String>();
        URI uri = null;
        try {
            uri = new URI(jcrRegistryURL);
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        config.put("org.apache.jackrabbit.repository.uri", uri.toString());

        try {
            jcrRegistry = new AiravataJCRRegistry(uri,
                    "org.apache.jackrabbit.rmi.repository.RmiRepositoryFactory",
                    jcrUsername, jcrPassword, config);
        } catch (RepositoryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        XRegistryClient client = XRegistryClientUtil.CreateGSISecureRegistryInstance(migrationPropertiesFile);
        saveAllHostDescriptions(client);
        saveAllServiceDescriptions(client);

        System.out.println("DONE!");
    }

    private static void loadProperties(String file) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream propertyStream = classLoader.getResourceAsStream(file);
        Properties properties = new Properties();
        if (propertyStream == null) {
            FileInputStream fileInputStream = new FileInputStream(new File(file));
            properties.load(fileInputStream);
        } else {
            properties.load(propertyStream);
        }

        jcrRegistryURL = properties.getProperty(MigrationConstants.JCR_URL);
        jcrUsername = properties.getProperty(MigrationConstants.JCR_USERNAME);
        jcrPassword = properties.getProperty(MigrationConstants.JCR_PASSWORD);
        System.out.println(jcrRegistryURL);
        System.out.println(jcrUsername);
        System.out.println(jcrPassword);

    }

    /**
     * Saves all the host descriptions to the Airavata Registry from the the given XRegistry.
     *
     * @param client client to access the XRegistry
     * @throws XRegistryClientException XRegistryClientException
     */
    private static void saveAllHostDescriptions(XRegistryClient client) throws XRegistryClientException {
        HostDescription host = null;
        HostDescData[] hostDescs = client.findHosts("");
        Map<QName, HostDescData> val = new HashMap<QName, HostDescData>();
        for (HostDescData hostDesc : hostDescs) {
            val.put(hostDesc.getName(), hostDesc);
            String hostDescStr = client.getHostDesc(hostDesc.getName().getLocalPart());
            HostBean hostBean = null;
            try {
                hostBean = org.ogce.schemas.gfac.beans.utils.HostUtils.simpleHostBeanRequest(hostDescStr);
                System.out.println("Host : " + hostBean.getHostName());
                System.out.println(hostDescStr);

            } catch (XmlException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            if(hostBean != null){
                host = MigrationUtil.createHostDescription(hostBean);
            }

            try {
                jcrRegistry.saveHostDescription(host);
            } catch (RegistryException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }

        System.out.println("=== All Hosts are saved ===");
    }

    /**
     * Saves all the host service descriptions to the Airavata Registry from the the given XRegistry.
     *
     * @param client client to access the XRegistry
     * @throws XRegistryClientException XRegistryClientException
     */
    private static void saveAllServiceDescriptions(XRegistryClient client) throws XRegistryClientException {
        ServiceDescription service = null;
        ServiceDescData[] serviceDescDatas = client.findServiceDesc("");
        Map<QName, ServiceDescData> val3 = new HashMap<QName, ServiceDescData>();
        int count = 0;

        for (ServiceDescData serviceDesc : serviceDescDatas) {
            val3.put(serviceDesc.getName(), serviceDesc);
            String serviceDescStr = client.getServiceDesc(serviceDesc.getName());
            ServiceBean serviceBean = null;
            String applicationName = null;

            try {
                serviceBean = org.ogce.schemas.gfac.beans.utils.ServiceUtils.serviceBeanRequest(serviceDescStr);
                applicationName = serviceBean.getApplicationName();
                System.out.println("Service : " + serviceBean.getServiceName());
                System.out.println(serviceDescStr);
            } catch (XmlException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            if(serviceBean != null) {
                try {
                    String serviceName = serviceBean.getServiceName();
                    ServiceDescription serviceDescription = jcrRegistry.getServiceDescription(serviceName);
                    if(serviceDescription == null) {
                        service = MigrationUtil.createServiceDescription(serviceBean);
                        jcrRegistry.saveServiceDescription(service);
                        ApplicationBean appBean = saveApplicationDescriptionWithName(client, applicationName, service);
                        // TODO : should look into this
                        if (appBean != null){
                            jcrRegistry.deployServiceOnHost(service.getType().getName(), appBean.getHostName());
                        }
                    } else {
                        serviceName = serviceName + "_" + count++;
                        service = MigrationUtil.createServiceDescription(serviceName,serviceBean);
                        System.out.println("DEBUG : Service Description named " + service.getType().getName() +
                                " exists in the registry. Therefore, saving it as " +
                                serviceName + " in the registry.");

                        jcrRegistry.saveServiceDescription(service);
                        ApplicationBean appBean = saveApplicationDescriptionWithName(client, applicationName, service);
                        // TODO : should look into this
                        if (appBean != null){
                            jcrRegistry.deployServiceOnHost(service.getType().getName(), appBean.getHostName());
                        }

                    }
                } catch (RegistryException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }

        }
        System.out.println("=== All Service/Applciation descriptors are saved ===");
    }

    /**
     * Saves the application description to the Airavata Registry from the the given XRegistry.
     *
     * @param client client to access the XRegistry
     * @param applicationName name of the application to be saved
     * @param service service name
     * @return ApplicationBean
     * @throws XRegistryClientException XRegistryClientException
     */
    private static ApplicationBean saveApplicationDescriptionWithName(XRegistryClient client, String applicationName, ServiceDescription service) throws XRegistryClientException {
        ApplicationDeploymentDescription app = null;
        FindAppDescResponseDocument.FindAppDescResponse.AppData[] appDatas = client.findAppDesc(applicationName);
        Map<QName, FindAppDescResponseDocument.FindAppDescResponse.AppData> val2 =
                new HashMap<QName, FindAppDescResponseDocument.FindAppDescResponse.AppData>();
        ApplicationBean appBean = null;
        int count = 0;
        for (FindAppDescResponseDocument.FindAppDescResponse.AppData appDesc : appDatas) {
            val2.put(appDesc.getName(), appDesc);
            String appDescStr = client.getAppDesc(appDesc.getName().toString(),appDesc.getHostName());
            try {
                appBean = org.ogce.schemas.gfac.beans.utils.ApplicationUtils.simpleApplicationBeanRequest(appDescStr);
                System.out.println("Application : " + appBean.getApplicationName());
                System.out.println(appDescStr);

            } catch (XmlException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            if(appBean != null){
                try {
                    String name = service.getType().getName();
                    String hostName = appBean.getHostName();
                    /*System.out.println("==== TESTING name : " + name);
                    System.out.println("==== TESTING hostName: " + hostName);*/
                    ApplicationDeploymentDescription appDepDesc = jcrRegistry.getDeploymentDescription(name, hostName);
                    if(appDepDesc == null) {
                        jcrRegistry.saveDeploymentDescription(name, hostName,
                                MigrationUtil.createAppDeploymentDescription(appBean));
                    } else {
                        //Creating a new name for the the duplicated item
                        name = name + "_" + count++;
                        System.out.println("DEBUG : Application Deployment Description named " + service.getType().getName() +
                                " with host " + hostName + " exists in the registry. Therefore, saving it as " +
                                name + " in the registry.");
                        jcrRegistry.saveDeploymentDescription(name, hostName,
                                MigrationUtil.createAppDeploymentDescription(name,appBean));
                    }
//            jcrRegistry.saveDeploymentDescription(service.getType().getName(), host.getType().getHostName(), app);
                } catch (RegistryException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }


        }

        return appBean;
    }

    /**
     * Saves all the application descriptions to the Airavata Registry from the the given XRegistry.
     *
     * @param client client to access the XRegistry
     * @throws XRegistryClientException XRegistryClientException
     */
    private static void saveAllApplicationDescriptions(XRegistryClient client) throws XRegistryClientException {
        ApplicationDeploymentDescription app = null;
        FindAppDescResponseDocument.FindAppDescResponse.AppData[] appDatas = client.findAppDesc("");
        Map<QName, FindAppDescResponseDocument.FindAppDescResponse.AppData> val2 =
                new HashMap<QName, FindAppDescResponseDocument.FindAppDescResponse.AppData>();
        for (FindAppDescResponseDocument.FindAppDescResponse.AppData appDesc : appDatas) {
            val2.put(appDesc.getName(), appDesc);
            String appDescStr = client.getAppDesc(appDesc.getName().toString(),appDesc.getHostName());
            System.out.println(appDescStr);
            ApplicationBean appBean = null;
            try {
                appBean = org.ogce.schemas.gfac.beans.utils.ApplicationUtils.simpleApplicationBeanRequest(appDescStr);
            } catch (XmlException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            if(appBean != null){
                app = MigrationUtil.createAppDeploymentDescription(appBean);
            }

            //jcrRegistry.saveDeploymentDescription(service.getType().getName(), host.getType().getHostName(), app);

        }
    }

}
