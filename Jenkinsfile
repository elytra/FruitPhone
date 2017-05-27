node {
	checkout scm
	sh 'git submodule update --init --recursive'
	sh './gradlew setupCiWorkspace clean build'
	archive 'build/libs/*jar'
}
