package com.dnfapps.arrmatey.api.arr

import com.dnfapps.arrmatey.api.arr.model.AnyArrMedia
import com.dnfapps.arrmatey.api.arr.model.QualityProfile
import com.dnfapps.arrmatey.api.arr.model.RootFolder
import com.dnfapps.arrmatey.api.arr.model.Tag
import com.dnfapps.arrmatey.api.client.NetworkResult
import com.dnfapps.arrmatey.api.client.safeGet
import com.dnfapps.arrmatey.model.Instance
import io.ktor.client.HttpClient
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

abstract class BaseArrClient<T: AnyArrMedia>(
    instance: Instance
): KoinComponent, IArrClient<T> {
    protected val httpClient: HttpClient by inject { parametersOf(instance) }

    override suspend fun getQualityProfiles(): NetworkResult<List<QualityProfile>> {
        val resp = httpClient.safeGet<List<QualityProfile>>("api/v3/qualityprofile")
        return resp
    }

    override suspend fun getRootFolders(): NetworkResult<List<RootFolder>> {
        val resp = httpClient.safeGet<List<RootFolder>>("api/v3/rootfolder")
        return resp
    }

    override suspend fun getTags(): NetworkResult<List<Tag>> {
        val resp = httpClient.safeGet<List<Tag>>("api/v3/tag")
        return resp
    }
}