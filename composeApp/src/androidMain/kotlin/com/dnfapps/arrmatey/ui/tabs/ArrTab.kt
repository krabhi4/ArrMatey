package com.dnfapps.arrmatey.ui.tabs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.dnfapps.arrmatey.api.arr.model.AnyArrMedia
import com.dnfapps.arrmatey.api.arr.model.ReleaseParams
import com.dnfapps.arrmatey.model.Instance
import com.dnfapps.arrmatey.model.InstanceType
import com.dnfapps.arrmatey.navigation.ArrScreen
import com.dnfapps.arrmatey.navigation.ArrTabNavigation
import com.dnfapps.arrmatey.ui.screens.ArrLibraryTab
import com.dnfapps.arrmatey.ui.screens.ArrSearchScreen
import com.dnfapps.arrmatey.ui.screens.InteractiveSearchScreen
import com.dnfapps.arrmatey.ui.screens.MediaDetailsScreen
import com.dnfapps.arrmatey.ui.screens.MediaPreviewScreen
import com.dnfapps.arrmatey.ui.viewmodel.ArrViewModel
import com.dnfapps.arrmatey.ui.viewmodel.rememberArrViewModel
import com.dnfapps.arrmatey.ui.viewmodel.rememberInstanceFor
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

val LocalInstance = compositionLocalOf<Instance?> { null }
val LocalArrViewModel = compositionLocalOf<ArrViewModel?> { null }
val LocalArrTabNavigation = compositionLocalOf<ArrTabNavigation> { object: ArrTabNavigation() {} }

@Composable
fun ArrTab(type: InstanceType) {
    val instance = rememberInstanceFor(type)
    val arrViewModel = rememberArrViewModel(instance)

    val navigation = koinInject<ArrTabNavigation> { parametersOf(type) }

    CompositionLocalProvider(LocalInstance provides instance) {
        CompositionLocalProvider(LocalArrViewModel provides arrViewModel) {
            CompositionLocalProvider(LocalArrTabNavigation provides navigation) {
                NavDisplay(
                    backStack = navigation.backStack,
                    onBack = { navigation.popBackStack() },
                    entryProvider = entryProvider {
                        entry<ArrScreen.Library> {
                            ArrLibraryTab(type)
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
                            InteractiveSearchScreen(releaseParams)
                        }
                        entry<ArrScreen.SeriesRelease> { params ->
                            val releaseParams = ReleaseParams.Series(
                                params.seriesId,
                                params.seasonNumber,
                                params.episodeId
                            )
                            InteractiveSearchScreen(releaseParams)
                        }
                    }
                )
            }
        }
    }
}