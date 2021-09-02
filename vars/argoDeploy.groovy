//
//  Author: Hari Sekhon
//  Date: 2021-09-01 14:07:59 +0100 (Wed, 01 Sep 2021)
//
//  vim:ts=4:sts=4:sw=4:noet
//
//  https://github.com/HariSekhon/templates
//
//  License: see accompanying Hari Sekhon LICENSE file
//
//  If you're using my code you're welcome to connect with me on LinkedIn and optionally send me feedback to help steer this or other code I publish
//
//  https://www.linkedin.com/in/HariSekhon
//

def call(app, timeout_seconds=600){
  label 'ArgoCD Deploy'
  container('argocd') {  // container name must match what is defined in jenkins-agent-pod.yaml
    timeout(time: timeout_seconds, unit: 'SECONDS') {
      sh """
        argocd app sync "$app" --grpc-web --force
        argocd app wait "$app" --grpc-web --timeout "$timeout_seconds"
      """
    }
  }
}