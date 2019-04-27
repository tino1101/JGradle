package com.tino.jgradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class JPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.extensions.create('customExt', CustomExt)
        project.task('JTask') {
            doLast {
                println project.customExt.name
            }
        }
    }
}