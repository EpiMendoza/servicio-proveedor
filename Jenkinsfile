pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        IMAGE_NAME = 'servicio-proveedor'
        CONTAINER_NAME = 'proveedor-dev'
        DOCKER_NETWORK = 'cicd-dev'

        HOST_PORT = '8082'
        CONTAINER_PORT = '9090'

        SPRING_PROFILE = 'dev'
        HEALTH_ENDPOINT = '/api/user/list'
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

                    echo "===== MAVEN ====="
                    ./mvnw --version

                    echo "===== GIT ====="
                    git --version

                    echo "===== DOCKER ====="
                    docker --version
                    docker compose version || true
                '''
            }
        }

        stage('Compilar y probar') {
            steps {
                sh '''
                    chmod +x mvnw
                    ./mvnw clean verify
                '''
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

                echo "Build Jenkins: ${BUILD_NUMBER}"
                echo "Commit corto: ${GIT_SHORT_COMMIT}"
                echo "Versión Docker: ${IMAGE_VERSION}"
            }
        }

        stage('Construir imagen Docker') {
            steps {
                sh '''
                    echo "===== CONSTRUYENDO IMAGEN DOCKER ====="

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
                    echo "===== INSPECCIONANDO IMAGEN ====="

                    docker image inspect \
                      ${IMAGE_NAME}:${IMAGE_VERSION}

                    echo "===== IMÁGENES DISPONIBLES ====="

                    docker images \
                      --format 'table {{.Repository}}\\t{{.Tag}}\\t{{.ID}}\\t{{.Size}}' \
                      ${IMAGE_NAME}
                '''
            }
        }

        stage('Validar red DEV') {
            steps {
                sh '''
                    echo "===== VALIDANDO RED DOCKER ====="

                    if ! docker network inspect ${DOCKER_NETWORK} >/dev/null 2>&1; then
                        echo "La red ${DOCKER_NETWORK} no existe"
                        exit 1
                    fi

                    echo "La red ${DOCKER_NETWORK} existe"
                '''
            }
        }

        stage('Desplegar en DEV') {
            steps {
                sh '''
                    echo "===== ELIMINANDO CONTENEDOR ANTERIOR ====="

                    docker rm -f ${CONTAINER_NAME} || true

                    echo "===== DESPLEGANDO DEV ====="
                    echo "Imagen: ${IMAGE_NAME}:${IMAGE_VERSION}"
                    echo "Contenedor: ${CONTAINER_NAME}"
                    echo "Puerto host: ${HOST_PORT}"
                    echo "Puerto interno: ${CONTAINER_PORT}"
                    echo "Perfil Spring: ${SPRING_PROFILE}"

                    docker run -d \
                      --name ${CONTAINER_NAME} \
                      --restart unless-stopped \
                      --network ${DOCKER_NETWORK} \
                      -p ${HOST_PORT}:${CONTAINER_PORT} \
                      -e SPRING_PROFILES_ACTIVE=${SPRING_PROFILE} \
                      -e SERVER_PORT=${CONTAINER_PORT} \
                      --label environment=dev \
                      --label jenkins.build=${BUILD_NUMBER} \
                      --label git.commit=${GIT_SHORT_COMMIT} \
                      --label image.version=${IMAGE_VERSION} \
                      ${IMAGE_NAME}:${IMAGE_VERSION}
                '''
            }
        }

        stage('Validar despliegue DEV') {
            steps {
                sh '''
                    echo "===== ESPERANDO AL SERVICIO ====="

                    rm -f /tmp/proveedor-response.json

                    for intento in $(seq 1 20); do
                        echo "Intento ${intento}/20"

                        if curl --fail --silent \
                            http://${CONTAINER_NAME}:${CONTAINER_PORT}${HEALTH_ENDPOINT} \
                            > /tmp/proveedor-response.json; then

                            echo "===== SERVICIO DEV DISPONIBLE ====="
                            cat /tmp/proveedor-response.json
                            exit 0
                        fi

                        sleep 3
                    done

                    echo "===== EL SERVICIO NO RESPONDIÓ ====="
                    echo "Estado del contenedor:"
                    docker ps -a --filter name=${CONTAINER_NAME}

                    echo "Logs del contenedor:"
                    docker logs ${CONTAINER_NAME} || true

                    exit 1
                '''
            }
        }

        stage('Mostrar despliegue') {
            steps {
                sh '''
                    echo "===== CONTENEDOR DESPLEGADO ====="

                    docker inspect ${CONTAINER_NAME} \
                      --format 'Contenedor={{.Name}} Imagen={{.Config.Image}} Estado={{.State.Status}}'

                    echo "===== PUERTOS ====="

                    docker port ${CONTAINER_NAME}

                    echo "===== URL LOCAL ====="
                    echo "http://localhost:${HOST_PORT}${HEALTH_ENDPOINT}"
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
            echo "Contenedor DEV: ${CONTAINER_NAME}"
            echo "URL DEV: http://localhost:${HOST_PORT}${HEALTH_ENDPOINT}"
        }

        failure {
            echo 'El pipeline del proveedor falló'
            echo 'Revisa la etapa marcada en rojo y el Console Output'

            sh '''
                echo "===== DIAGNÓSTICO FINAL ====="
                docker ps -a --filter name=${CONTAINER_NAME} || true
                docker logs ${CONTAINER_NAME} || true
            '''
        }

        cleanup {
            echo 'Pipeline finalizado'
        }
    }
}
