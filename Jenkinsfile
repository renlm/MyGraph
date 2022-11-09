pipeline {
    agent any
    environment {
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
                echo "创建工作目录..."
                sh 'mkdir -p /var/jenkins_home/workspace/renlm'
                sh 'rm -fr /var/jenkins_home/workspace/renlm/MyGraph'
                sh 'rm -fr /var/jenkins_home/workspace/renlm/study-notes'
                echo "下载代码..."
                dir('/var/jenkins_home/workspace/renlm/MyGraph') {
                	git branch: 'main', credentialsId: "${githubCredential}", url: 'git@github.com:renlm/MyGraph.git'
                }
                dir('/var/jenkins_home/workspace/renlm/study-notes') {
                	git branch: 'master', credentialsId: "${giteeCredential}", url: 'https://gitee.com/renlm/study-notes.git'
                }
            }
        }
        stage ('Maven Build') {
            steps {
                echo "Maven构建..."
                dir('/var/jenkins_home/workspace/renlm/MyGraph') {
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
                	dir('/var/jenkins_home/workspace/renlm/MyGraph') {
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