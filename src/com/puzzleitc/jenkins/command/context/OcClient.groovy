package com.puzzleitc.jenkins.command.context

class OcClient {

    private static final DEFAULT_OC_TOOL_NAME = "oc_3_11"

    private final PipelineContext ctx

    private String ocHome

    OcClient(PipelineContext ctx) {
        this.ctx = ctx
    }

    void login(String cluster, String credentialId) {
        def saToken = ctx.lookupTokenFromCredentials(credentialId)
        if (!saToken) {
            ctx.fail("Token for credentialId '${credentialId}' cannot be found in Jenkins")
        }
        ctx.echo("Logging into to OpenShift cluster")
        def exitCode = invokeOcCommand("oc login ${cluster} --insecure-skip-tls-verify=true --token=${saToken}", false, true) as int
        if (exitCode != 0) {
            ctx.fail("Failed to log into OpenShift cluster ${cluster}")
        }
    }

    void project(String project) {
        def exitCode = invokeOcCommand("oc project ${project}", false, true) as int
        if (exitCode != 0) {
            ctx.fail("Failed to use OpenShift project ${project}")
        }
    }

    private Object invokeOcCommand(String command, boolean returnStdout = false, boolean returnStatus = false) {
        initOcHomeIfNecessary()
        ctx.withEnv(["PATH+OC_HOME=${ocHome}/bin"]) {
            ctx.sh(script: command, returnStdout: returnStdout, returnStatus: returnStatus)
        }
    }

    void initOcHomeIfNecessary() {
        if (!ocHome) {
            ocHome = ctx.tool(DEFAULT_OC_TOOL_NAME)
        }
    }
}