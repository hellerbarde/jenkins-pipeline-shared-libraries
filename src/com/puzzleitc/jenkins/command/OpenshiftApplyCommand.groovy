package com.puzzleitc.jenkins.command

import com.puzzleitc.jenkins.command.context.PipelineContext

class OpenshiftApplyCommand {

    private static final DEFAULT_CREDENTIAL_ID_SUFFIX = "-cicd-deployer"
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

        ctx.oc.login(cluster, credentialId)
        ctx.oc.project(project)
    }

}
