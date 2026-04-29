package com.yumaoem.core_ui.utils.animation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween

const val navigationAnimationDuration = 300

val defaultEnterTransition: AnimatedContentTransitionScope<*>.() -> EnterTransition = {
    slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Start,
        tween(navigationAnimationDuration)
    )
}

val defaultExitTransition: AnimatedContentTransitionScope<*>.() -> ExitTransition = {
    slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.Start,
        tween(navigationAnimationDuration)
    )
}

val defaultPopEnterTransition: AnimatedContentTransitionScope<*>.() -> EnterTransition = {
    slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.End,
        tween(navigationAnimationDuration)
    )
}

val defaultPopExitTransition: AnimatedContentTransitionScope<*>.() -> ExitTransition = {
    slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.End,
        tween(navigationAnimationDuration)
    )
}
