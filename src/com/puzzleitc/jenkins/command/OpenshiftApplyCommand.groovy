package com.puzzleitc.jenkins.command

import com.puzzleitc.jenkins.command.context.PipelineContext

class OpenshiftApplyCommand {

    private static final DEFAULT_CREDENTIAL_ID_SUFFIX = "-cicd-deployer"
    private static final DEFAULT_OC_TOOL_NAME = "oc_3_11"
    private static final DEFAULT_CLUSTER = "https://openshift.puzzle.ch"

    private final PipelineContext ctx

    OpenshiftApplyCommand(PipelineContext ctx) {
        this.ctx = ctx
    }

    void execute() {
        ctx.info("-- openshiftApply --")
        def configuration = ctx.stepParams.getRequired("configuration")
        def project = ctx.stepParams.getRequired("project") as String
        def cluster = ctx.stepParams.getOptional("cluster", DEFAULT_CLUSTER) as String
        def app = ctx.stepParams.getOptional("app", null) as String
        def credentialId = ctx.stepParams.getOptional("credentialId", "${project}${DEFAULT_CREDENTIAL_ID_SUFFIX}") as String

        login(credentialId, cluster, project)

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

    Object invokeOcCommand(String command, boolean returnStdout = false, boolean returnStatus = false) {
        def ocHome = ctx.tool(DEFAULT_OC_TOOL_NAME)
        ctx.withEnv(["PATH+OC_HOME=${ocHome}/bin"]) {
            ctx.sh(script: command, returnStdout: returnStdout, returnStatus: returnStatus)
        }
    }

}
