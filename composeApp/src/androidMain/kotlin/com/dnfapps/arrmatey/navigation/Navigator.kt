package com.dnfapps.arrmatey.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay

val LocalNavigator = compositionLocalOf<Navigator?> { null }

interface Navigator {
    fun navigate(route: NavKey)
    fun popBackStack()
    fun getLabel(): String?
    fun getParent(): Navigator?
    fun getDescendent(label: String): Navigator?
}

@Composable
fun <T: NavKey> Navigator(
    entryRoute: T,
    entryProvider: (T) -> NavEntry<T>,
    label: String? = null,
    onBack: (MutableList<T>) -> Unit = { it.removeLastOrNull() },
) {
    val backStack = remember { mutableStateListOf(entryRoute) }
    val expectedType = remember { entryRoute::class }

    val parentNavigator = LocalNavigator.current

    val navigatorImpl = object : Navigator {
        override fun navigate(route: NavKey) {
            if (route::class != expectedType) {
                error("Route type ${route::class.simpleName} doesn't match expected type ${expectedType.simpleName}")
            }
            @Suppress("UNCHECKED_CAST")
            backStack.add(route as T)
        }
        override fun popBackStack() {
            onBack(backStack)
        }
        override fun getLabel(): String? = label
        override fun getParent(): Navigator? = parentNavigator
        override fun getDescendent(label: String): Navigator? {
            var curr: Navigator? = this
            while(curr != null && curr.getLabel() != label) {
                curr = curr.getParent()
            }
            return curr
        }
    }

    CompositionLocalProvider(LocalNavigator provides navigatorImpl) {
        NavDisplay(
            backStack = backStack,
            onBack = { navigatorImpl.popBackStack() },
            entryProvider = entryProvider
        )
    }
}