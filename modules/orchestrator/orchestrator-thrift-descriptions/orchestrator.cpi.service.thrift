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

/*
 * Component Programming Interface definition for Apache Airavata Orchestration Service.
 *
*/

include "../../../airavata-api/thrift-interface-descriptions/airavataErrors.thrift"
namespace java org.apache.airavata.orchestrator.cpi

const string ORCHESTRATOR_CPI_VERSION = "0.13.0"

service OrchestratorService {

  /** Query orchestrator server to fetch the CPI version */
  string getOrchestratorCPIVersion(),

    /**
     * After creating the experiment Data user have the
     * experimentID as the handler to the experiment, during the launchExperiment
     * We just have to give the experimentID
     *
     * @param experimentID
     * @return sucess/failure
     *
    **/
  bool launchExperiment (1: required string experimentId, 2: required string airavataCredStoreToken),

    /**
     * In order to run single applications users should create an associating 
     * WorkflowNodeDetails and a TaskDetails for it and hand it over for execution
     * along with a credential store token for authentication
     *
     * @param taskId
     * @param airavataCredStoreToken
     * @return sucess/failure
     *
    **/
  bool launchTask (1: required string taskId, 2: required string airavataCredStoreToken),

    /**
     *
     * Validate funcations which can verify if the experiment is ready to be launced.
     *
     * @param experimentID
     * @return sucess/failure
     *
    **/
  bool validateExperiment(1: required string experimentId)
  throws (1: airavataErrors.LaunchValidationException lve)
    /**
     *
     * Terminate the running experiment.
     *
     * @param experimentID
     * @return sucess/failure
     *
    **/
  bool terminateExperiment (1: required string experimentId)
}
