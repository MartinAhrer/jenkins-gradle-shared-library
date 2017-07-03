package at.softwarecraftsmen.gradle

def run (Map arguments = [:], String command) {
    def defaults = [image: 'openjdk:8-jdk', workdir: "${env.JENKINS_AGENT_WORKSPACE}/${env.JOB_NAME}", returnStdout: false]
    defaults <<= env.GRADLE_OPTS ? [GRADLE_OPTS : env.GRADLE_OPTS] : [:]

    def configuration = defaults << arguments

    def workdir = '/project'
    def dockerRunOptions = "--rm -v ${configuration.workdir}:${workdir} -v dot_gradle:/root/.gradle --workdir ${workdir}"
    for (e in configuration.environment) {
        dockerRunOptions <<= " -e ${e.key}=\"${e.value}\""
    }
    sh(script: "docker run ${dockerRunOptions} ${configuration.image} ${command}", returnStdout: configuration.returnStdout)
}
