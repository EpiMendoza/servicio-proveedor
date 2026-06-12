pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
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
    }

    post {
        always {
            junit allowEmptyResults: true,
                    testResults: 'target/surefire-reports/*.xml'
        }

        success {
            archiveArtifacts artifacts: 'target/*.jar',
                             fingerprint: true

            echo 'Proveedor compilado y probado correctamente'
        }

        failure {
            echo 'El pipeline del proveedor falló'
        }
    }
}
