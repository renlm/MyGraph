pipeline {
	agent any
    tools {
        maven 'maven-3.6.3'
    }
    environment {
    	DATE = new Date().format('yy.M')
        TAG = "${DATE}.${BUILD_NUMBER}"
		rancherCredential = 'Rancher'
        aliyuncsCredential = 'Aliyuncs'
		dockerRegistry = 'https://registry.cn-hangzhou.aliyuncs.com'
    	projectDir = "${WORKSPACE}"
		dockerImage = 'registry.cn-hangzhou.aliyuncs.com/rlm/mygraph'
		workloadUrl = '/project/c-m-5hjgrb22:p-jcjsv/workloads/deployment:renlm:mygraph'
    }
    stages {
        stage ('Maven Build') {
            steps {
                echo "Maven构建..."
                dir("${projectDir}") { 
	                script {
	                	def profile = params.Profile == null ? 'prod' : params.Profile
		            	sh "mvn clean package -P ${profile} -T 1C -Dmaven.test.skip=true -Dmaven.compile.fork=true"
	                }
                }
            }
        }
        stage('Docker Build') {
            steps {
                script {
                	echo "构建镜像..."
                	dir("${projectDir}") {
	                	docker.withRegistry("${dockerRegistry}", "${aliyuncsCredential}") {
	                        docker.build("${dockerImage}:${TAG}", "-f ${projectDir}/Dockerfile .")
	                    }
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
                rancherRedeploy alwaysPull: true, credential: "${rancherCredential}", images: "${dockerImage}", workload: "${workloadUrl}"
            }
        }
    }
}