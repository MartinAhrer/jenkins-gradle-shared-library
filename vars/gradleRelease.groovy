
def call (Closure body) {
    // evaluate the body block, and collect configuration into the object
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    // build scripts
    def dockerImage=config.dockerImage ?: "openjdk:8-jdk"
    def dockerOptions="--rm --workdir=/workspace -v ${config.workspace}:/workspace -v dot_gradle:/root/.gradle"
    for (e in config.env) {
        dockerOptions += " -e e.key=\"e.value\""
    }

    def properties=""
    for (p in config.properties) {
        properties += " -P${p.key}=${p.value}"
    }

    def gradleCommand = config.gradleCommand ?: "./gradlew --no-daemon"
    def gradlePrintReleaseTagTask = config.gradlePrintReleaseTagTask ?: "printReleaseTag --quiet"
    def gradleReleaseTask = config.gradleReleaseTask ?: "release --stacktrace"
    def dryRun = config.dryRun?: false

    def printReleaseTagScript= "docker run ${dockerOptions} ${dockerImage} ${gradleCommand} ${properties} ${gradlePrintReleaseTagTask}"
    def releaseScript="docker run ${dockerOptions} ${dockerImage} ${gradleCommand} ${properties} ${gradleReleaseTask}"

    if (dryRun) {
        println printReleaseTagScript
        println releaseScript
    } else {
        def tag = sh (script: printReleaseTagScript, returnStdout:true).trim()
        sh (script: releaseScript)
        config.onRelease (tag)
    }
}

/*

gradleRelease {
    credentials = usernamePassword(credentialsId: credentialsId, usernameVariable: 'GRGIT_USER', passwordVariable: 'GRGIT_PASS')
    env = ['GRADLE_OPTS': ${env.GRADLE_OPTS}]
    properties = ['release.scope': releaseScope,
                  'release.stage': releaseStage]
    workspace = env.JENKINS_AGENT_WORKSPACE

    dryRun = true

    onRelease = { def tag ->
        println tag
    }
}
 */
