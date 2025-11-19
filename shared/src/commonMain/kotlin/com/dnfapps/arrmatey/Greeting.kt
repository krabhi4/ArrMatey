package com.dnfapps.arrmatey

import com.dnfapps.arrmatey.ktor.demo.RocketComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class Greeting {
    private val rocketComponent = RocketComponent()

    fun greet(): Flow<String> = flow {
        emit(rocketComponent.launchPhrase())
    }
}