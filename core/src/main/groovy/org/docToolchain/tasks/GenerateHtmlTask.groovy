package org.docToolchain.tasks

import org.docToolchain.asciidoctor.Converter

class GenerateHtmlTask extends DocToolchainTask {

    GenerateHtmlTask(ConfigObject config) {
        super(config)
    }

    @Override
    void execute() {
        def converter = new Converter(configService)
        converter.convertToHtml()

    }
}
