package com.raizlabs.android.dbflow.processor.definition

import com.raizlabs.android.dbflow.processor.ClassNames
import com.raizlabs.android.dbflow.processor.definition.method.DatabaseDefinition
import com.raizlabs.android.dbflow.processor.handler.DatabaseHandler
import com.raizlabs.android.dbflow.processor.model.ProcessorManager
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier

/**
 * Description: Top-level writer that handles writing all [DatabaseDefinition]
 * and [com.raizlabs.android.dbflow.annotation.TypeConverter]
 */
class FlowManagerHolderDefinition(private val processorManager: ProcessorManager) : TypeDefinition {

    private var className = ""

    init {

        val options = this.processorManager.processingEnvironment.options

        if (options.containsKey(OPTION_TARGET_MODULE_NAME)) {
            className = options[OPTION_TARGET_MODULE_NAME] ?: ""
        }

        className += ClassNames.DATABASE_HOLDER_STATIC_CLASS_NAME
    }

    override val typeSpec: TypeSpec
        get() {
            val typeBuilder = TypeSpec.classBuilder(this.className)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .superclass(ClassNames.DATABASE_HOLDER)

            val constructor = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)

            for (typeConverterDefinition in processorManager.getTypeConverters()) {
                constructor.addStatement("\$L.put(\$T.class, new \$T())", DatabaseHandler.TYPE_CONVERTER_MAP_FIELD_NAME,
                        typeConverterDefinition.modelTypeName,
                        typeConverterDefinition.className)
            }

            for (databaseDefinition in processorManager.getDatabaseDefinitionMap()) {
                if (databaseDefinition.databaseDefinition != null) {
                    constructor.addStatement("new \$T(this)", databaseDefinition.databaseDefinition.outputClassName)
                }
            }

            typeBuilder.addMethod(constructor.build())


            return typeBuilder.build()
        }

    companion object {

        private val OPTION_TARGET_MODULE_NAME = "targetModuleName"
    }
}