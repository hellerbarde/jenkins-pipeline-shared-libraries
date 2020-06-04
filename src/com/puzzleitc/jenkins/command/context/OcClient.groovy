package com.puzzleitc.jenkins.command.context

class OcClient {

    private static final DEFAULT_OC_TOOL_NAME = "oc_3_11"

    private final String ocHome
    private final PipelineContext ctx

    OcClient(PipelineContext ctx) {
        this.ocHome = ctx.tool(DEFAULT_OC_TOOL_NAME)
        this.ctx = ctx
    }

    void login(String credentialId, String cluster, String project) {
        def saToken = ctx.lookupTokenFromCredentials(credentialId)
        if (!saToken) {
            ctx.fail("Token for credentialId '${credentialId}' cannot be found in Jenkins")
        }
        ctx.echo("Logging into to OpenShift cluster")
        def exitCode = invokeOcCommand("oc login ${cluster} --insecure-skip-tls-verify=true --token=${saToken}", false, true) as int
        if (exitCode != 0) {
            ctx.fail("Logging into OpenShift cluster ${cluster} failed")
        }

        ctx.echo(invokeOcCommand("oc project ${project}", true) as String)
        ctx.echo("openshift project: ${project}")
    }

    private Object invokeOcCommand(String command, boolean returnStdout = false, boolean returnStatus = false) {
        ctx.withEnv(["PATH+OC_HOME=${ocHome}/bin"]) {
            ctx.sh(script: command, returnStdout: returnStdout, returnStatus: returnStatus)
        }
    }

}