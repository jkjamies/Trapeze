/*
 * Copyright 2026 Jason Jamieson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jkjamies.trapeze

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * The Entry Point: Bridges the Logic and the UI.
 * Generic constraints ensure S is identical in both.
 */
@Composable
fun <T : TrapezeScreen, S : TrapezeState, E : TrapezeEvent> TrapezeContent(
    modifier: Modifier = Modifier,
    screen: T,
    stateHolder: TrapezeStateHolder<T, S, E>,
    ui: TrapezeUi<S>
) {
    val state = stateHolder.produceState(screen)
    ui(modifier, state)
}