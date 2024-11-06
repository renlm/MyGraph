/*
构建参数：
	Version：发布版本（0.0.1）
	Profile：部署环境（prod）
	DockerCredential：Docker认证（aliyuncs）
	DockerRegistry：Docker仓库（https://registry.cn-hangzhou.aliyuncs.com）
	DockerImage：Docker镜像（registry.cn-hangzhou.aliyuncs.com/rlm/mygraph）
安装插件：
	Docker Pipeline
	Pipeline: Stage View
*/
pipeline {
	agent any
    environment {
        TAG = "${params.Version}"
    }
    stages {
        stage('Docker Build') {
            steps {
                script {
                	echo "构建镜像..."
                	dir("${WORKSPACE}") {
	                	docker.withRegistry("${params.DockerRegistry}", "${params.DockerCredential}") {
	                        docker.build("${params.DockerImage}:${TAG}", "--build-arg PROFILES_ACTIVE=${params.Profile} -f Dockerfile .")
	                    }
                    }
                }
            }
        }
	    stage('Publish Image') {
            steps {
                script {
                	echo "推送镜像..."
                    docker.withRegistry("${params.DockerRegistry}", "${params.DockerCredential}") {
                        docker.image("${params.DockerImage}:${TAG}").push()
                    }
                }
            }
        }
    }
}
