def rancherCredential = "Rancher"
def githubCredential = "Github"
def giteeCredential = "Gitee"
def aliyuncsCredential = "Aliyuncs"
def dockerRegistry = "https://registry.cn-hangzhou.aliyuncs.com"
def dockerImage = "registry.cn-hangzhou.aliyuncs.com/rlm/mygraph"
def workloadUrl = "https://rancher.renlm.cn/v3/project/c-m-lz7w89vx:p-jhxwf/workloads/deployment:renlm:mygraph"
pipeline {
    agent any
    stages {
        stage ('Prepare') {
            steps {
                echo "创建工作目录..."
                sh 'rm -fr /var/jenkins_home/workspace/renlm/MyGraph'
                sh 'rm -fr /var/jenkins_home/workspace/renlm/study-notes'
                sh 'mkdir -p /var/jenkins_home/workspace/renlm'
            }
        }
        stage ('SCM') {
            steps {
                echo "下载代码..."
                sh 'cd /var/jenkins_home/workspace/renlm'
                git credentialsId: '${githubCredential}', url: 'git@github.com:renlm/MyGraph.git'
                git credentialsId: '${giteeCredential}', url: 'https://gitee.com/renlm/study-notes.git'
            }
        }
        stage ('Maven Build') {
            steps {
                echo "Maven构建..."
                sh 'cd /var/jenkins_home/workspace/renlm/MyGraph'
                sh 'mvn clean package -Dmaven.test.skip=true'
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
                    docker.withRegistry('${dockerRegistry}', '${aliyuncsCredential}') {
                        docker.image("${dockerImage}").push("latest")
                    }
                }
            }
        }
        stage('Deploy') {
            steps {
                script {
                	echo "部署应用..."
                	rancherRedeploy alwaysPull: true, credential: '${githubCredential}', images: '${dockerImage}', workload: '${workloadUrl}'
            	}
            }
        }
    }
}