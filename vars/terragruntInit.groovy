//
//  Author: Hari Sekhon
//  Date: 2022-01-06 17:35:16 +0000 (Thu, 06 Jan 2022)
//
//  vim:ts=2:sts=2:sw=2:et
//
//  https://github.com/HariSekhon/Jenkins
//
//  License: see accompanying Hari Sekhon LICENSE file
//
//  If you're using my code you're welcome to connect with me on LinkedIn and optionally send me feedback to help steer this or other code I publish
//
//  https://www.linkedin.com/in/HariSekhon
//

def call(timeoutMinutes=10){
  String label = 'Terragrunt Init'

  // forbids older inits from starting
  milestone(ordinal: 10, label: "Milestone: $label")

  // XXX: set Terragrunt version in the docker image tag in jenkins-agent-pod.yaml
  container('terragrunt') {
    timeout(time: timeoutMinutes, unit: 'MINUTES') {
      //dir ("components/${COMPONENT}") {
      ansiColor('xterm') {
        // terraform workspace is not supported if using Terraform Cloud
        // TF_WORKSPACE overrides 'terraform workspace select'
        //
        // alpine/terragrunt docker image doesn't have bash
        //sh '''#/usr/bin/env bash -euxo pipefail
        echo 'Terragrunt Workspace Select'
        sh (
          label: 'Workspace Select',
          script: '''#/bin/sh -eux
            if [ -n "$TF_WORKSPACE" ]; then
                terragrunt workspace new "$TF_WORKSPACE" || echo "Workspace '$TF_WORKSPACE' already exists or using Terraform Cloud as a backend"
                #terragrunt workspace select "$TF_WORKSPACE"  # TF_WORKSPACE takes precedence over this select
            fi
          '''
        )
        echo "$label"
        sh label: "$label",
           script: 'terragrunt init --terragrunt-non-interactive -input=false'  // # -backend-config "bucket=$ACCOUNT-$PROJECT-terraform" -backend-config "key=${ENV}-${PRODUCT}/${COMPONENT}/state.tf"
      }
    }
  }
}
