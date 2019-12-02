package dev.eastar.pref.annotation.generator

import dev.eastar.pref.annotation.generator.ClassBuilderPref.Companion.GENERATED_CLASS_PRE_FIX
import javax.lang.model.element.Element

/**
 * Custom Kotlin Class Builder which returns file content string
 * This is for learning purpose only.
 * Use KotlinPoet for production app
 * KotlinPoet can be found at https://github.com/square/kotlinpoet
 */
class ClassBuilderInitializer(environments: Set<Element>) {
    private val contentTemplate = """
package dev.eastar.sharedpreferences

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import androidx.preference.PreferenceManager

class ${GENERATED_CLASS_PRE_FIX}Initializer : ContentProvider() {
    override fun onCreate(): Boolean {
${environments.joinToString("\n") {
        """        ${it.enclosingElement}.$GENERATED_CLASS_PRE_FIX${it.simpleName}.preferences =  context?.getSharedPreferences("$it}", Context.MODE_PRIVATE)!!"""
    }}
        return true
    }

    override fun getType(uri: Uri): String? = null
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? = null
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int = 0
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0
}
"""

    fun getContent() = contentTemplate
}