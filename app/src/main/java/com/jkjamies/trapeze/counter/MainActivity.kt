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

package com.jkjamies.trapeze.counter

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.jkjamies.trapeze.TrapezeContent
import com.jkjamies.trapeze.core.presentation.AppInterop
import com.jkjamies.trapeze.core.presentation.AppInteropEvent
import com.jkjamies.trapeze.features.counter.presentation.CounterScreen
import com.jkjamies.trapeze.features.counter.presentation.CounterStateHolder
import com.jkjamies.trapeze.features.counter.presentation.CounterUi
import com.jkjamies.trapeze.features.summary.presentation.SummaryScreen
import com.jkjamies.trapeze.features.summary.presentation.SummaryStateHolder
import com.jkjamies.trapeze.features.summary.presentation.SummaryUi
import com.jkjamies.trapeze.counter.theme.TrapezeTheme
import com.jkjamies.trapeze.navigation.LocalTrapezeNavigator
import com.jkjamies.trapeze.navigation.TrapezeNavHost
import dev.zacsweers.metro.Inject
import com.jkjamies.trapeze.features.summary.api.SaveSummaryValue
import com.jkjamies.trapeze.features.summary.api.ObserveLastSavedValue
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.android.ActivityKey

@ContributesIntoMap(AppScope::class, binding<Activity>())
@ActivityKey(MainActivity::class)
class MainActivity : ComponentActivity() {

    // TODO: inject the stateHolder and UI with the domains, not here
    @Inject lateinit var saveSummaryValue: Lazy<SaveSummaryValue>
    @Inject lateinit var observeLastSavedValue: Lazy<ObserveLastSavedValue>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrapezeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TrapezeNavHost(
                        initialScreen = CounterScreen(initialCount = 0)
                    ) { screen ->
                        val navigator = LocalTrapezeNavigator.current
                        
                        val interop = remember {
                            object : AppInterop {
                                override fun send(event: AppInteropEvent) {
                                    Toast.makeText(this@MainActivity, "App Interop: $event", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                        when(screen) {
                             is CounterScreen -> {
                                 TrapezeContent(
                                     modifier = Modifier.padding(innerPadding),
                                     screen = screen,
                                     stateHolder = CounterStateHolder(interop, navigator),
                                     ui = ::CounterUi
                                 )
                             }
                             is SummaryScreen -> {
                                 TrapezeContent(
                                     modifier = Modifier.padding(innerPadding),
                                     screen = screen,
                                     stateHolder = SummaryStateHolder(navigator, saveSummaryValue, observeLastSavedValue),
                                     ui = ::SummaryUi
                                 )
                             }
                             else -> {} 
                        }
                    }
                }
            }
        }
    }
}