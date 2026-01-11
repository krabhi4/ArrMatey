package com.dnfapps.arrmatey.navigation

import androidx.compose.runtime.mutableStateListOf
import com.dnfapps.arrmatey.compose.TabItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class Navigation<T>(initialScreen: T) {
    val backStack = mutableStateListOf(initialScreen)

    fun navigateTo(screen: T) {
        backStack.add(screen)
    }

    fun popBackStack() {
        backStack.removeLastOrNull()
    }

    fun replaceCurrent(screen: T) {
        backStack.add(screen)
//        CoroutineScope(Dispatchers.Main).launch {
//            delay(500)
            backStack.removeAt(backStack.size - 2)
//        }
    }
}

class SettingsNavigation: Navigation<SettingsScreen>(SettingsScreen.Landing)
abstract class ArrTabNavigation: Navigation<ArrScreen>(ArrScreen.Library)
class SeriesTabNavigation: ArrTabNavigation()
class MoviesTabNavigation: ArrTabNavigation()