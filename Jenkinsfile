pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        IMAGE_NAME = 'servicio-proveedor'
        IMAGE_TAG = 'latest'
    }

    stages {
        stage('Información del entorno') {
            steps {
                sh '''
                    echo "===== WORKSPACE ====="
                    pwd
                    ls -la

                    echo "===== JAVA ====="
                    java -version

                    echo "===== DOCKER ====="
                    docker --version
                '''
            }
        }

        stage('Compilar y probar') {
            steps {
                sh './mvnw clean verify'
            }
        }

        stage('Verificar artefacto') {
            steps {
                sh 'ls -lh target/*.jar'
            }
        }

        stage('Construir imagen Docker') {
            steps {
                sh '''
                    docker build \
                      -t ${IMAGE_NAME}:${IMAGE_TAG} \
                      .
                '''
            }
        }

        stage('Verificar imagen Docker') {
            steps {
                sh '''
                    docker image inspect ${IMAGE_NAME}:${IMAGE_TAG}
                    docker images ${IMAGE_NAME}
                '''
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true,
                    testResults: 'target/surefire-reports/*.xml'
        }

        success {
            archiveArtifacts artifacts: 'target/*.jar',
                             fingerprint: true

            echo 'Proveedor compilado, probado y empaquetado como imagen Docker'
        }

        failure {
            echo 'El pipeline del proveedor falló'
        }
    }
}
