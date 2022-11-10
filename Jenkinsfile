pipeline {
    agent any
    tools {
        maven 'maven-3.6.3'
        dockerTool 'docker'
    }
    environment {
    	DATE = new Date().format('yy.M')
        TAG = "${DATE}.${BUILD_NUMBER}"
    	workDir = '/var/jenkins_home/workspace/renlm'
		rancherCredential = 'Rancher'
		githubCredential = 'Github'
		giteeCredential = 'Gitee'
        aliyuncsCredential = 'Aliyuncs'
		dockerRegistry = 'https://registry.cn-hangzhou.aliyuncs.com'
		dockerImage = 'registry.cn-hangzhou.aliyuncs.com/rlm/mygraph'
		workloadUrl = 'https://rancher.renlm.cn/v3/project/c-m-lz7w89vx:p-jhxwf/workloads/deployment:renlm:mygraph'
    }
    stages {
        stage ('Prepare') {
            steps {
                echo "WORKSPACE：${WORKSPACE}"
                echo "BUILD_NUMBER：${BUILD_NUMBER}"
                sh "mkdir -p ${workDir}"
                sh "rm -fr ${workDir}/MyGraph"
                sh "rm -fr ${workDir}/study-notes"
                echo "下载代码..."
                dir("${workDir}/MyGraph") {
                	git branch: 'main', credentialsId: "${githubCredential}", url: 'git@github.com:renlm/MyGraph.git'
                }
                dir("${workDir}/study-notes") {
                	git branch: 'master', credentialsId: "${giteeCredential}", url: 'https://gitee.com/renlm/study-notes.git'
                }
            }
        }
        stage ('Maven Build') {
            steps {
                echo "Maven构建..."
                dir("${workDir}/MyGraph") {
                	sh 'rm -fr src/main/resources/properties/prod'
                	sh 'cp -r ../study-notes/MyGraph/properties/prod src/main/resources/properties'
                	sh 'mvn clean package -P prod -Dmaven.test.skip=true'
                }
            }
        }
        stage('Docker Build') {
            steps {
                script {
                	echo "构建镜像..."
                	dir("${workDir}/MyGraph") {
                    	docker.build("${dockerImage}", "-f ./Dockerfile .")
                    }
                }
            }
        }
	    stage('Publish Image') {
            steps {
                script {
                	echo "推送镜像..."
                    docker.withRegistry("${dockerRegistry}", "${aliyuncsCredential}") {
                        docker.image("${dockerImage}").push("latest")
                    }
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