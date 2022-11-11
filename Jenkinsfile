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
		workloadUrl = 'https://rancher.renlm.cn/v3/project/c-m-59hh87sj:p-2qj8x/workloads/deployment:renlm:mygraph'
    }
    stages {
        stage ('Maven Build') {
            steps {
                echo "Maven构建..."
                dir("${JENKINS_HOME}/study-notes") { 
                	git branch: 'master', credentialsId: "${giteeCredential}", url: 'https://gitee.com/renlm/study-notes.git' 
                }
                dir("${WORKSPACE}") { 
                	sh 'rm -fr src/main/resources/properties/prod'
	            	sh "cp -r ${JENKINS_HOME}/study-notes/MyGraph/properties/prod src/main/resources/properties"
	            	sh 'mvn clean package -P prod -T 1C -Dmaven.test.skip=true -Dmaven.compile.fork=true'
                }
            }
        }
        stage('Docker Build') {
            steps {
                script {
                	echo "构建镜像..."
                	docker.withRegistry("${dockerRegistry}", "${aliyuncsCredential}") {
                        docker.build("${dockerImage}:${TAG}", "-f ${WORKSPACE}/Dockerfile .")
                    }
                }
            }
        }
	    stage('Publish Image') {
            steps {
                script {
                	echo "推送镜像..."
                    docker.withRegistry("${dockerRegistry}", "${aliyuncsCredential}") {
                        docker.image("${dockerImage}:${TAG}").push()
                        docker.image("${dockerImage}:${TAG}").push("latest")
                    }
                }
            }
        }
        stage('Deploy') {
            steps {
                echo "部署应用..."
                rancherRedeploy alwaysPull: true, credential: "${githubCredential}", images: "${dockerImage}", workload: "${workloadUrl}"
            }
        }
    }
}