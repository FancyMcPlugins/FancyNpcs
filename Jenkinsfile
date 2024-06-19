/*
    Required env: java 21, git
    Required plugins: discord notifier
    Required credentials: MODRINTH_PUBLISH_API_TOKEN, HANGAR_PUBLISH_API_TOKEN
*/

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
                echo 'Built the plugin!'
            }
        }

        stage('Deploy') {
            steps {
                // Load the secrets and make them available as environment variables
                withCredentials([
                    string(credentialsId: 'MODRINTH_PUBLISH_API_TOKEN', variable: 'MODRINTH_PUBLISH_API_TOKEN'),
                    string(credentialsId: 'HANGAR_PUBLISH_API_TOKEN', variable: 'HANGAR_PUBLISH_API_TOKEN')
                ]) {
//                     sh 'export MODRINTH_PUBLISH_API_TOKEN=${MODRINTH_PUBLISH_API_TOKEN} && ./gradlew modrinth'
//                     echo 'Published to Modrinth!'

                    sh 'export HANGAR_PUBLISH_API_TOKEN=${HANGAR_PUBLISH_API_TOKEN} && ./gradlew publishAllPublicationsToHangar'
                    echo 'Published to Hangar!'
                }
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: '**/build/libs/FancyNpcs-*.jar', allowEmptyArchive: true
        }
        success {
            script {
                discordSend description: "**Build:** ${env.BUILD_NUMBER} \n**Status:** ${currentBuild.currentResult} \n**Download:** https://modrinth.com/plugin/fancynpcs/versions",
                 footer: "Jenkins Pipeline", link: env.BUILD_URL, result: 'SUCCESS', title: "FancyNpcs #${env.BUILD_NUMBER}", webhookURL: "https://discord.com/api/webhooks/1146819356668477530/LWXgRBXdBzbFPJIf_9KP9AKYdaEFnd2aTIy9l4V0K03R-Xl07vWYahNxuvkRAX5YahwM"
            }
            echo 'Build was successful!'
        }
        failure {
            script {
                discordSend description: "**Build:** ${env.BUILD_NUMBER} \n**Status:** ${currentBuild.currentResult}", footer: "Jenkins Pipeline", link: env.BUILD_URL, result: 'FAILURE', title: "FancyNpcs #${env.BUILD_NUMBER}", webhookURL: "https://discord.com/api/webhooks/1146819356668477530/LWXgRBXdBzbFPJIf_9KP9AKYdaEFnd2aTIy9l4V0K03R-Xl07vWYahNxuvkRAX5YahwM"
            }
            echo 'Build failed!'
        }
    }
}
