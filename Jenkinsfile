pipeline {
    agent any

    environment {
        GRADLE_OPTS = '-Dorg.gradle.daemon=false'
    }

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/FancyMcPlugins/FancyNpcs', branch: 'main'
            }
        }

        stage('Build') {
            steps {
                sh 'chmod +x gradlew'
                sh './gradlew clean shadowJar'
            }
        }

        stage('Test') {
            steps {
                // Define your test steps here
                sh './gradlew test'
            }
        }

        stage('Deploy') {
            steps {
                // Load the secrets and make them available as environment variables
                withCredentials([
                    string(credentialsId: 'MODRINTH_PUBLISH_API_TOKEN', variable: 'MODRINTH_PUBLISH_API_TOKEN'),
                    string(credentialsId: 'HANGAR_PUBLISH_API_TOKEN', variable: 'HANGAR_PUBLISH_API_TOKEN')
                ]) {
                    sh 'export MODRINTH_PUBLISH_API_TOKEN=${MODRINTH_PUBLISH_API_TOKEN} && ./gradlew modrinth'
                    sh 'export HANGAR_PUBLISH_API_TOKEN=${HANGAR_PUBLISH_API_TOKEN} && ./gradlew publishAllPublicationsToHangar'
                }
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: '**/build/libs/*.jar', allowEmptyArchive: true
        }
        success {
            script {
                discordSend description: "Build was successful!", footer: "Jenkins Pipeline", link: env.BUILD_URL, result: 'SUCCESS', title: "Build Success", webhookURL: "https://discord.com/api/webhooks/1146819356668477530/LWXgRBXdBzbFPJIf_9KP9AKYdaEFnd2aTIy9l4V0K03R-Xl07vWYahNxuvkRAX5YahwM"
            }
            echo 'Build was successful!'
        }
        failure {
            script {
                discordSend description: "Build failed!", footer: "Jenkins Pipeline", link: env.BUILD_URL, result: 'FAILURE', title: "Build Failed", webhookURL: "https://discord.com/api/webhooks/1146819356668477530/LWXgRBXdBzbFPJIf_9KP9AKYdaEFnd2aTIy9l4V0K03R-Xl07vWYahNxuvkRAX5YahwM"
            }
            echo 'Build failed!'
        }
    }
}
