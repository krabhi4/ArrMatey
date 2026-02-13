package com.dnfapps.arrmatey.utils

import dev.icerock.moko.resources.desc.PluralStringDesc
import dev.icerock.moko.resources.desc.StringDesc

actual fun createResolver(): StringResolver {
    return object : StringResolver {
        override fun resolve(stringDesc: StringDesc): String {
            return stringDesc.localized()
        }

        override fun resolve(pluralsDesc: PluralStringDesc): String {
            return pluralsDesc.localized()
        }
    }
}