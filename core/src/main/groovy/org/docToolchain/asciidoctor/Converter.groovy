package org.docToolchain.asciidoctor

import org.asciidoctor.Asciidoctor
import org.asciidoctor.Attributes
import org.asciidoctor.AttributesBuilder
import org.asciidoctor.Options
import org.asciidoctor.OptionsBuilder
import org.asciidoctor.SafeMode
import org.docToolchain.configuration.ConfigService

import java.nio.file.Path
import java.nio.file.Paths

class Converter {

    Asciidoctor asciidoctor

    ConfigService configService

    OptionsBuilder optionsBuilder

    Converter(ConfigService configService) {
        this.configService = configService
        this.asciidoctor = Asciidoctor.Factory.create()
        this.optionsBuilder = Options.builder()
    }

    def convertToHtml(){
        String inputPath = configService.getConfigProperty("inputPath")
        String outputPath = configService.getConfigProperty("targetDir")
        String docDir = configService.getConfigProperty("docDir")
        String srcDir = Paths.get(docDir, inputPath).toString()
        println("inputPath: " + inputPath)
        println("outputPath: " + outputPath)
        println("docDir: " + docDir)
        List<File> files = configService.getConfigProperty("inputFiles").findAll {
            it.formats.contains("html")
        }.collect {
            new File(Paths.get(docDir, inputPath, it.file).toString())
        }

        println("Files: " + files.toString())

        configService.getConfigProperty("imageDirs").each {
            File imageDir = new File(Paths.get(docDir, it as String).toString())
            File destination = new File(Paths.get(outputPath, "/html5/images").toString())

            if(!destination.exists()){
                destination.mkdirs()
            }

            imageDir.eachFileRecurse { file ->
                Path relativePath = Paths.get(imageDir.toURI()).relativize(Paths.get(file.toURI()))
                println("Relative path: " + relativePath.toString())
                File destFile = new File(Paths.get(destination.absolutePath, relativePath.toString()).toString())
                println("Copying file: " + file.absolutePath + " to " + destFile.absolutePath)
                if (file.isDirectory()) {
                    destFile.mkdirs()
                } else {
                    file.withInputStream { input ->
                        destFile.withOutputStream { output ->
                            output << input
                        }
                    }
                }
            }

            Attributes attributes = Attributes.builder().attribute(Attributes.IMAGESDIR, "images/").build()

            optionsBuilder
                .backend("html5")
                .attributes(attributes)
                .toDir(new File(Paths.get(outputPath, "/html5/").toString()))
                .safe(SafeMode.UNSAFE)
            asciidoctor.convertFiles(files, optionsBuilder.build())
        }

    }


}
