package com.dnfapps.arrmatey.utils

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.PluralsResource
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.Plural
import dev.icerock.moko.resources.desc.PluralFormatted
import dev.icerock.moko.resources.desc.PluralStringDesc
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import org.koin.compose.koinInject

interface StringResolver {
    fun resolve(stringDesc: StringDesc): String
    fun resolve(pluralsDesc: PluralStringDesc): String
}

class MokoStrings {
    private val resolver: StringResolver = createResolver()

    fun getString(resource: StringResource): String {
        val desc = StringDesc.Resource(resource)
        return resolver.resolve(desc)
    }

    fun getString(resource: StringResource, formatArgs: List<Any>): String {
        val desc = StringDesc.ResourceFormatted(resource, formatArgs)
        return resolver.resolve(desc)
    }

    fun getPlural(resource: PluralsResource, quantity: Int): String {
        val desc = StringDesc.Plural(resource, quantity)
        return resolver.resolve(desc)
    }

    fun getPlural(resource: PluralsResource, quantity: Int, formatArgs: List<Any>): String {
        val desc = StringDesc.PluralFormatted(resource, quantity, formatArgs)
        return resolver.resolve(desc)
    }
}

@Composable
fun mokoString(resource: StringResource): String {
    val moko: MokoStrings = koinInject()
    return moko.getString(resource)
}

@Composable
fun mokoString(resource: StringResource, vararg formatArgs: Any): String {
    val moko: MokoStrings = koinInject()
    return moko.getString(resource, formatArgs.toList())
}

@Composable
fun mokoPlural(resource: PluralsResource, quantity: Int): String {
    val moko: MokoStrings = koinInject()
    return moko.getPlural(resource, quantity, listOf(quantity))
}

@Composable
fun mokoPlural(resource: PluralsResource, quantity: Int, vararg formatArgs: Any): String {
    val moko: MokoStrings = koinInject()
    return moko.getPlural(resource, quantity, formatArgs.toList())
}