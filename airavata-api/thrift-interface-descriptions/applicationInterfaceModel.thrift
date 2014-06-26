ut/*
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

namespace java org.apache.airavata.model.application.interface
namespace php Airavata.Model.Application.Interface

const string DEFAULT_ID = "DO_NOT_SET_AT_CLIENTS"


/**
 * Data Types Supported in Airavata.
 *
 *
*/
enum DataType{
	STRING,
	INTEGER,
	FLOAT,
	URI
}

/**
* Aplication Inputs
*
*/
struct InputDataObjectType {
    1: required string key,
    2: optional string value,
    3: optional DataType type,
    4: optional string metaData
    5: optional string applicationParameter,
    6: optional string applicationUIDescription
}

/**
* Aplication Outputs
*
*/
struct OutputDataObjectType {
    1: required string key,
    2: optional string value,
    3: optional DataType type,
    4: optional string metaData
}

/**
 * Application Interface Description
 *
 *
 * appDeploymentId:
 *   Corelated the interface to a particular application deployment
 *
 *
*/
struct ApplicationInterfaceDescription {
    1: required bool isEmpty = 0,
    2: required string applicationInterfaceId = DEFAULT_ID,
    3: required string applicationName,
    4: optional list<appModuleId> applicationModules,
    5: optional list<InputDataObjectType> applicationInputs,
    6: optional list<outputDataObjectType> applicationOutputs,
}