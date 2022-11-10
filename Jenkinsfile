pipeline {
    agent any
    tools {
        maven 'maven-3.6.3'
        dockerTool 'docker'
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
		workloadUrl = 'https://rancher.renlm.cn/v3/project/c-m-lz7w89vx:p-jhxwf/workloads/deployment:renlm:mygraph'
    }
    stages {
        stage ('Maven Build') {
            steps {
                echo "Maven构建..."
                git branch: 'master', credentialsId: "${giteeCredential}", url: 'https://gitee.com/renlm/study-notes.git'
                sh 'rm -fr src/main/resources/properties/prod'
            	sh 'cp -r study-notes/MyGraph/properties/prod src/main/resources/properties'
            	sh 'mvn clean package -P prod -Dmaven.test.skip=true'
            }
        }
        stage('Docker Build') {
            steps {
                script {
                	echo "构建镜像..."
                	docker.build("${dockerImage}", "-f ./Dockerfile .")
                }
            }
        }
	    stage('Publish Image') {
            steps {
                script {
                	echo "推送镜像..."
                    docker.image("${dockerImage}:${TAG}")
                    docker.image("${dockerImage}:${TAG}").push("latest")
                }
            }
        }
        stage('Deploy') {
            steps {
                script {
                	echo "部署应用..."
                	rancherRedeploy alwaysPull: true, credential: "${githubCredential}", images: "${dockerImage}", workload: "${workloadUrl}"
            	}
            }
        }
    }
}