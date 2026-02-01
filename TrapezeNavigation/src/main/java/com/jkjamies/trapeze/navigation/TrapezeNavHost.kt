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

package com.jkjamies.trapeze.navigation
import androidx.compose.runtime.Composable
import com.jkjamies.trapeze.TrapezeScreen
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

val LocalTrapezeNavigator = staticCompositionLocalOf<TrapezeNavigator> { 
    error("No TrapezeNavigator provided") 
}

@Composable
fun TrapezeNavHost(
    initialScreen: TrapezeScreen,
    modifier: Modifier = Modifier,
    content: @Composable (TrapezeScreen) -> Unit
) {
    var stack by remember { mutableStateOf(listOf(initialScreen)) }
    val currentScreen = stack.last()
    val saveableStateHolder = rememberSaveableStateHolder()
    
    val navigator = remember {
        object : TrapezeNavigator {
            override fun navigate(screen: TrapezeScreen) {
                stack = stack + screen
            }
            override fun pop() {
                if (stack.size > 1) {
                    val poppedScreen = stack.last()
                    stack = stack.dropLast(1)
                    saveableStateHolder.removeState(poppedScreen)
                }
            }
        }
    }
    
    CompositionLocalProvider(LocalTrapezeNavigator provides navigator) {
        saveableStateHolder.SaveableStateProvider(key = currentScreen) {
             content(currentScreen)
        }
    }
}
