/*
 * Copyright 2019 copyright eastar Jeong
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.eastar.pref.annotation.generator

import com.google.auto.service.AutoService
import dev.eastar.pref.annotation.Pref
import dev.eastar.pref.annotation.generator.ImplClassBuilder.Companion.GENERATED_CLASS_TAIL_FIX
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

@AutoService(Processor::class) // For registering the service
@SupportedSourceVersion(SourceVersion.RELEASE_8) // to support Java 8
@SupportedOptions(PrefGenerator.KAPT_KOTLIN_GENERATED_OPTION_NAME)
public class PrefGenerator : AbstractProcessor() {
    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"

    }

    override fun init(processingEnvironment: ProcessingEnvironment?) {
        super.init(processingEnvironment)
        processingEnvironment?.let { Log.processingEnvironment = it }

    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Pref::class.java.name)
    }

    override fun process(set: MutableSet<out TypeElement>?, roundEnvironment: RoundEnvironment?): Boolean {
        Log.w("===========================================================")
        roundEnvironment
                ?.getElementsAnnotatedWith(Pref::class.java)
                ?.forEach { Log.environmentTree(it) }
        Log.w("===========================================================")

        roundEnvironment
                ?.getElementsAnnotatedWith(Pref::class.java)
                ?.forEach {
                    val packageName = processingEnv.elementUtils.getPackageOf(it).toString()
                    generateImplClass(packageName, it)
                }

        roundEnvironment
                ?.getElementsAnnotatedWith(Pref::class.java)
                ?.let {
                    generateInitializerClass(it)
                }
        return true
    }

    private fun generateInitializerClass(roundEnvironment: Set<Element>) {
        if (roundEnvironment.isEmpty())
            return
        val fileName = "Initializer"
        val fileContent = InitializerClassBuilder(roundEnvironment).getContent()
        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        val file = File(kaptKotlinGeneratedDir, "$fileName.kt")
        file.writeText(fileContent)
    }

    private fun generateImplClass(packageName: String, roundEnvironment: Element) {
        val className = "${roundEnvironment.simpleName}$GENERATED_CLASS_TAIL_FIX"
        val fileContent = ImplClassBuilder(packageName, roundEnvironment).getContent()
        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        val file = File(kaptKotlinGeneratedDir, "$className.kt")
        file.writeText(fileContent)
    }
}
