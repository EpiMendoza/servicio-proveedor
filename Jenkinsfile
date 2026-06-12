pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        IMAGE_NAME = 'servicio-proveedor'
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

                    echo "===== GIT ====="
                    git --version
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
                sh '''
                    echo "===== JAR GENERADO ====="
                    ls -lh target/*.jar
                '''
            }
        }

        stage('Preparar versión') {
            steps {
                script {
                    env.GIT_SHORT_COMMIT = sh(
                        script: 'git rev-parse --short HEAD',
                        returnStdout: true
                    ).trim()

                    env.IMAGE_VERSION = "${BUILD_NUMBER}-${GIT_SHORT_COMMIT}"
                }

                echo "Build de Jenkins: ${BUILD_NUMBER}"
                echo "Commit corto: ${GIT_SHORT_COMMIT}"
                echo "Versión de imagen: ${IMAGE_VERSION}"
            }
        }

        stage('Construir imagen Docker') {
            steps {
                sh '''
                    echo "===== CONSTRUYENDO IMAGEN ====="

                    docker build \
                      -t ${IMAGE_NAME}:${IMAGE_VERSION} \
                      -t ${IMAGE_NAME}:${BUILD_NUMBER} \
                      -t ${IMAGE_NAME}:latest \
                      .
                '''
            }
        }

        stage('Verificar imagen Docker') {
            steps {
                sh '''
                    echo "===== INSPECCIÓN DE IMAGEN ====="

                    docker image inspect \
                      ${IMAGE_NAME}:${IMAGE_VERSION}

                    echo "===== IMÁGENES DEL PROVEEDOR ====="

                    docker images \
                      --format 'table {{.Repository}}\\t{{.Tag}}\\t{{.ID}}\\t{{.Size}}' \
                      ${IMAGE_NAME}
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

            echo 'Pipeline completado correctamente'
            echo "Imagen generada: ${IMAGE_NAME}:${IMAGE_VERSION}"
            echo "Alias del build: ${IMAGE_NAME}:${BUILD_NUMBER}"
            echo "Alias más reciente: ${IMAGE_NAME}:latest"
        }

        failure {
            echo 'El pipeline del proveedor falló'
            echo 'Revisa la etapa marcada en rojo y el Console Output'
        }

        cleanup {
            echo 'Pipeline finalizado'
        }
    }
}
