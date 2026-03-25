/*pipeline {
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
	        publishHTML([
	            allowMissing: true,
	            alwaysLinkToLastBuild: true,
	            keepAll: true,
	            reportDir: 'test-output',
	            reportFiles: 'ExtentReport.html',
	            reportName: 'JiverJinx Audit Report'
        ])
	      emailext(
	            to: 'akhansha000@gmail.com',
	            subject: "JiverJinx Audit - Build #${BUILD_NUMBER} - ${currentBuild.result}",
	            body: "Build Result: ${currentBuild.result}\nReport: ${BUILD_URL}JiverJinx_20Audit_20Report",
	            mimeType: 'text/plain'
	        )
	    }
	}
}*/

 pipeline {
    agent any

    tools {
        maven 'Maven_3.9'
    }

    // ✅ ADD — parameters passed in by developer's pipeline
    parameters {
        string(name: 'ENVIRONMENT',
               defaultValue: 'Production',
               description: 'Environment to test: staging or Production')
        string(name: 'TRIGGERED_BY',
               defaultValue: 'manual',
               description: 'What triggered this run')
        string(name: 'RELEASE_VERSION',
               defaultValue: 'unknown',
               description: 'Release version being tested')
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
                // ✅ CHANGE — pass environment and headless flag to Maven
                sh """
                    /opt/homebrew/Cellar/maven/3.9.14/libexec/bin/mvn clean test \
                    -Denvironment=${params.ENVIRONMENT} \
                    -Dheadless=true
                """
                // headless=true because Jenkins has no display screen
            }
        }

        // ✅ REMOVE the old 'Publish Report' stage entirely
        // It is now in post { always } below so it runs even if tests fail
    }

    post {
        always {
            // ✅ CHANGE reportDir to LatestReport — fixed path ExtentManager writes to
            publishHTML([
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'test-output/LatestReport',
                reportFiles: 'ExtentReport.html',
                reportName: 'JiverJinx Audit Report'
            ])

            // ✅ emailext reads your Jenkins SMTP settings correctly
            emailext(
                to: 'akhansha000@gmail.com',
                subject: "🧪 JiverJinx Audit — Build #${BUILD_NUMBER} — ${currentBuild.result}",
                body: """
                    <h2>JiverJinx Regression Test Results</h2>
                    <table>
                        <tr><td><b>Result:</b></td><td>${currentBuild.result}</td></tr>
                        <tr><td><b>Environment:</b></td><td>${params.ENVIRONMENT}</td></tr>
                        <tr><td><b>Release Version:</b></td><td>${params.RELEASE_VERSION}</td></tr>
                        <tr><td><b>Triggered By:</b></td><td>${params.TRIGGERED_BY}</td></tr>
                        <tr><td><b>Build Number:</b></td><td>#${BUILD_NUMBER}</td></tr>
                        <tr><td><b>Full Report:</b></td>
                            <td><a href="${BUILD_URL}JiverJinx_20Audit_20Report">
                                Click here to view Extent Report
                            </a></td>
                        </tr>
                    </table>
                """,
                mimeType: 'text/html'
            )
        }
    }
}