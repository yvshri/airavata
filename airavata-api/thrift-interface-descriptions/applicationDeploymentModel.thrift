/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

namespace java org.apache.airavata.model.application.deployment
namespace php Airavata.Model.Application.Deployment

const string DEFAULT_ID = "DO_NOT_SET_AT_CLIENTS"

struct SetEnvPaths {
    1: required string name,
    2: required string value
}

struct ApplicationModule {
    1: required string appModuleId = DEFAULT_ID,
    2: required string appModuleName,
    3: optional string appModuleVersion,
    4: optional string appModuleDescription
}

/**
 * Application Deployment Description
 *
 * appDeploymentId: Airavata Internal Unique Job ID. This is set by the registry.
 *
 * appModuleName:
 *   Application Module Name. This has to be precise describing the binary.
 *
 * computeHostId:
 *   This ID maps application deployment to a particular resource previously described within Airavata.
 *   Example: Stampede is first registered and refered when registering WRF.
 *
 * envModuleLoadCmd:
 *  Command string to load modules. This will be placed in the job submisison
 *  Ex: module load amber
 *
 * libPrependPaths:
 *  prepend to a path variable the value
 *
 * libAppendPaths:
 *  append to a path variable the value
 *
 * setEnvironment:
 *  assigns to the environment variable "NAME" the value
 *
*/
struct ApplicationDeploymentDescription {
    1: required string appDeploymentId = DEFAULT_ID,
    2: required string appModuleId,
    3: required string computeHostId,
    4: required string executablePath,
	5: optional string appDeploymentDescription,
	6: optional string envModuleLoadCmd,
	7: optional list<SetEnvPaths> libPrependPaths,
	8: optional list<SetEnvPaths> libAppendPaths,
	9: optional list<SetEnvPaths> setEnvironment,
}
