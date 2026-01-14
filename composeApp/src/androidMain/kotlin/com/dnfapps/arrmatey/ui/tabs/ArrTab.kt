package com.dnfapps.arrmatey.ui.tabs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.dnfapps.arrmatey.api.arr.model.AnyArrMedia
import com.dnfapps.arrmatey.api.arr.model.ReleaseParams
import com.dnfapps.arrmatey.compose.utils.ReleaseFilterBy
import com.dnfapps.arrmatey.model.Instance
import com.dnfapps.arrmatey.model.InstanceType
import com.dnfapps.arrmatey.navigation.ArrScreen
import com.dnfapps.arrmatey.navigation.ArrTabNavigation
import com.dnfapps.arrmatey.ui.screens.ArrLibraryScreen
import com.dnfapps.arrmatey.ui.screens.ArrSearchScreen
import com.dnfapps.arrmatey.ui.screens.InteractiveSearchScreen
import com.dnfapps.arrmatey.ui.screens.MediaDetailsScreen
import com.dnfapps.arrmatey.ui.screens.MediaPreviewScreen
import com.dnfapps.arrmatey.ui.screens.MovieFilesScreen
import com.dnfapps.arrmatey.ui.viewmodel.ArrViewModel
import com.dnfapps.arrmatey.ui.viewmodel.InstanceViewModel
import com.dnfapps.arrmatey.ui.viewmodel.rememberArrViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

val LocalInstance = compositionLocalOf<Instance?> { null }
val LocalArrViewModel = compositionLocalOf<ArrViewModel?> { null }
val LocalArrTabNavigation = compositionLocalOf<ArrTabNavigation> { object: ArrTabNavigation() {} }

@Composable
fun ArrTab(type: InstanceType) {
    val viewModel = viewModel<InstanceViewModel>()
    val allInstances by viewModel.allInstancesFlow.collectAsStateWithLifecycle()
    val instance = allInstances.firstOrNull { it.type == type && it.selected }

    val arrViewModel = rememberArrViewModel(instance)

    val navigation = koinInject<ArrTabNavigation> { parametersOf(type) }

    CompositionLocalProvider(
        LocalInstance provides instance,
        LocalArrViewModel provides arrViewModel,
        LocalArrTabNavigation provides navigation
    ) {
        NavDisplay(
            backStack = navigation.backStack,
            onBack = { navigation.popBackStack() },
            entryProvider = entryProvider {
                entry<ArrScreen.Library> {
                    ArrLibraryScreen(type, viewModel, instance)
                }
                entry<ArrScreen.Details> { details ->
                    MediaDetailsScreen(details.id)
                }
                entry<ArrScreen.Search> { search ->
                    ArrSearchScreen(search.query, type)
                }
                entry<ArrScreen.Preview<AnyArrMedia>> { preview ->
                    MediaPreviewScreen(preview.item)
                }
                entry<ArrScreen.MovieReleases> { params ->
                    val releaseParams = ReleaseParams.Movie(params.movieId)
                    InteractiveSearchScreen(releaseParams, canFilter = false)
                }
                entry<ArrScreen.SeriesRelease> { params ->
                    val releaseParams = ReleaseParams.Series(
                        params.seriesId,
                        params.seasonNumber,
                        params.episodeId
                    )
                    InteractiveSearchScreen(
                        releaseParams = releaseParams,
                        canFilter = true,
                        defaultFilter = if (params.episodeId != null) {
                            ReleaseFilterBy.SingleEpisode
                        } else ReleaseFilterBy.SeasonPack
                    )
                }
                entry<ArrScreen.MovieFiles> { params ->
                    MovieFilesScreen(movie = params.movie)
                }
            }
        )
    }
}