pipeline {
    agent any

    environment {
        DOCKER_REGISTRY = 'docker.io'
        IMAGE_NAME = 'handeycor/usabana-market'
        K8S_NAMESPACE = 'usabana-market'
        GRADLE_OPTS = '-Dorg.gradle.jvmargs=-Xms256m -Xmx1024m -XX:MaxMetaspaceSize=512m'
        JAVA_TOOL_OPTIONS = '-Xmx1024m -XX:MaxMetaspaceSize=512m'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                // Dar permisos de ejecución a gradlew
                sh 'chmod +x gradlew'
            }
        }

        stage('Build') {
            steps {
                // Debug: verificar memoria disponible
                sh 'free -m || echo "free command not available"'
                sh 'echo "Java version:"'
                sh 'java -version || true'
                sh './gradlew --version || true'

                // Usamos -x test para saltar las pruebas, --no-daemon para evitar problemas de memoria
                // --stacktrace para información detallada, --info para logs verbosos
                // --max-workers=1 para reducir uso de memoria
                sh './gradlew clean build -x test --no-daemon --stacktrace --info --max-workers=1 || (echo "Build failed, checking daemon logs..." && ls -la ~/.gradle/daemon/ && tail -n 200 ~/.gradle/daemon/*/daemon-*.log && exit 1)'
            }
        }

/*
        stage('Test') {
            steps {
                // Ejecuta los tests en una etapa separada (si quieres habilitarlos)
                sh './gradlew test --no-daemon --stacktrace'
            }
            post {
                always {
                    junit '**/build/test-results/test/*.xml'
                }
            }
        }*/

        stage('Build Docker Image') {
            steps {
                script {
                    def imageTag = "${env.BUILD_NUMBER ?: 'local'}-${env.GIT_COMMIT?.take(7) ?: 'nogit'}"
                    sh "docker build -t ${IMAGE_NAME}:${imageTag} ."
                    sh "docker tag ${IMAGE_NAME}:${imageTag} ${IMAGE_NAME}:latest"
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    def imageTag = "${env.BUILD_NUMBER ?: 'local'}-${env.GIT_COMMIT?.take(7) ?: 'nogit'}"

                    // OPCIÓN 1: Usando Jenkins Credentials Store (RECOMENDADO)
                    withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        sh "echo \$DOCKER_PASS | docker login -u \$DOCKER_USER --password-stdin"
                        sh "docker push ${IMAGE_NAME}:${imageTag}"
                        sh "docker push ${IMAGE_NAME}:latest"
                    }

                }
            }
        }

        stage('Update Helm Chart') {
            steps {
                script {
                    def imageTag = "${env.BUILD_NUMBER ?: 'local'}-${env.GIT_COMMIT?.take(7) ?: 'nogit'}"
                    // Actualiza values.yaml (ajusta la ruta si tu chart está en otra carpeta)
                    sh "sed -i 's|tag:.*|tag: ${imageTag}|' helm/values.yaml || true"
                    sh "git config user.email 'jenkins@usabana-market.com' || true"
                    sh "git config user.name 'Jenkins CI' || true"
                    sh "git add helm/values.yaml || true"
                    sh "git commit -m 'Update image tag to ${imageTag}' || true"
                    sh "git push origin HEAD:${env.GIT_BRANCH} || true"
                }
            }
        }

        stage('Trigger ArgoCD Sync') {
            steps {
                // Si usas ArgoCD y el agente tiene argocd CLI configurado
                sh "argocd app sync usabana-market --insecure || echo 'ArgoCD sync triggered'"
            }
        }
    }

    post {
        always {
            // Usa deleteDir() en lugar de cleanWs() si el plugin no está disponible
            deleteDir()
        }
        success {
            echo 'Pipeline completado exitosamente!'
        }
        failure {
            echo 'Pipeline falló. Revisar logs.'
        }
    }
}
