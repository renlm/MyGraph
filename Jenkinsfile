pipeline {
	agent any
    tools {
        maven 'maven-3.6.3'
        dockerTool 'docker-latest'
    }
    environment {
    	DATE = new Date().format('yy.M')
        TAG = "${DATE}.${BUILD_NUMBER}"
		rancherCredential = 'Rancher'
		githubCredential = 'Github'
		giteeCredential = 'Gitee'
        aliyuncsCredential = 'Aliyuncs'
		dockerRegistry = 'https://registry.cn-hangzhou.aliyuncs.com'
		dockerImage = 'registry.cn-hangzhou.aliyuncs.com/rlm/mygraph'
		workloadUrl = '/v3/project/c-m-59hh87sj:p-2qj8x/workloads/deployment:renlm:mygraph'
    }
    stages {
        stage('Deploy') {
            steps {
                echo "部署应用..."
                rancherRedeploy alwaysPull: true, credential: "${rancherCredential}", images: "${dockerImage}", workload: "${workloadUrl}"
            }
        }
    }
}