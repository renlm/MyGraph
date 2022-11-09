pipeline {
    agent any
    tools {
        maven 'maven-3.6.3'
    }
    environment {
        DATE = new Date().format('yy.M')
        TAG = "${DATE}.${BUILD_NUMBER}"
    }
    stages {
        stage ('Prepare') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
        stage ('SCM') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
        stage ('Maven Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
        stage('Docker Build') {
            steps {
                script {
                    docker.build("rlm/mygraph:${TAG}", "-f ./Dockerfile .")
                }
            }
        }
	    stage('Publish Image') {
            steps {
                script {
                    docker.withRegistry('https://registry.cn-hangzhou.aliyuncs.com', 'aliyuncs') {
                        docker.image("rlm/mygraph:${TAG}").push()
                        docker.image("rlm/mygraph:${TAG}").push("latest")
                    }
                }
            }
        }
        stage('Deploy') {
            steps {
                rancherRedeploy alwaysPull: true, credential: 'Rancher2.6.9', images: '${BUILD_NUMBER}', workload: 'https://rancher.renlm.cn/v3/p/c-m-lz7w89vx/workloads/deployment:renlm:mygraph'
            }
        }
    }
}