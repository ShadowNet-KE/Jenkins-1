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
  String label = "Terraform Plan - App: $APP, Environment: $ENVIRONMENT"
  // must differentiate lock because Terraform Plan and Terraform Apply must share the same lock
  String lock  = "Terraform - App: $APP, Environment: $ENVIRONMENT"
  echo "Acquiring Terraform Plan Lock: $lock"
  // plan still locks on normal backends outside Terraform Cloud
  lock(resource: lock, inversePrecedence: true) {
    // forbids older plans from starting
    milestone(ordinal: 50, label: "Milestone: $label")

    // XXX: set Terraform version in the docker image tag in jenkins-agent-pod.yaml
    container('terraform') {
      timeout(time: timeoutMinutes, unit: 'MINUTES') {
        //dir ("components/${COMPONENT}") {
        ansiColor('xterm') {
          // terraform docker image doesn't have bash
          //sh '''#/usr/bin/env bash -euxo pipefail
          //sh '''#/bin/sh -eux
          echo 'Terraform Workspace List'
          sh (
            label: 'Workspace List',
            script: 'terraform workspace list || : ' // 'workspaces not supported' if using Terraform Cloud as a backend
          )
          echo "$label"
          sh (
            label: "$label",
            script: 'terraform plan -out=plan.zip -input=false'  // # -var-file=base.tfvars -var-file="$ENV.tfvars"
          )
        }
      }
    }
  }
}
