pipeline {
    agent any

    tools {
        maven 'Maven_3.9'
    }

    stages {
        stage('Pull Code') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/AKHANSHA2001/SELENIUM_CICD_INTEGRATION.git'
            }
        }

        stage('Run Tests') {
            steps {
                sh '/opt/homebrew/Cellar/maven/3.9.14/libexec/bin/mvn clean test'
            }
        }

        stage('Publish Report') {
            steps {
                publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'test-output',
                    reportFiles: 'ExtentReport.html',
                    reportName: 'JiverJinx Audit Report'
                ])
            }
        }
    }

    post {
        always {
            mail to: 'akhansha000@gmail.com',
                 subject: "JiverJinx Audit - Build #${BUILD_NUMBER} - ${currentBuild.result}",
                 body: """
                 Build Result: ${currentBuild.result}
                 Build Number: ${BUILD_NUMBER}
                 Check Report: ${BUILD_URL}JiverJinx_20Audit_20Report
                 """
        }
    }
}