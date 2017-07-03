import at.softwarecraftsmen.gradle.GradleContainer

def call(Map arguments = [:], String command) {
    def container = new GradleContainer()
    container.run(arguments, command)
}

