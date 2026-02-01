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

package com.jkjamies.trapeze.counter.feature.counter

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import com.jkjamies.trapeze.TrapezeStateHolder
import com.jkjamies.trapeze.counter.common.AppInterop
import com.jkjamies.trapeze.counter.common.AppInteropEvent
import com.jkjamies.trapeze.counter.feature.summary.SummaryScreen
import com.jkjamies.trapeze.navigation.TrapezeNavigator

class CounterStateHolder(
    private val interop: AppInterop,
    private val navigator: TrapezeNavigator
) : TrapezeStateHolder<CounterScreen, CounterState, CounterEvent>() {

    @Composable
    override fun produceState(screen: CounterScreen): CounterState {
        var count by rememberSaveable { mutableIntStateOf(screen.initialCount) }

        return CounterState(
            count = count,
            eventSink = { event ->
                when (event) {
                    CounterEvent.Increment -> count++
                    CounterEvent.Decrement -> count--
                    CounterEvent.Divide -> count /= 2
                    CounterEvent.GoToSummary -> {
                        navigator.navigate(SummaryScreen(count))
                    }
                    CounterEvent.GetHelp -> {
                        interop.send(object : AppInteropEvent {
                            override fun toString(): String = "Help Requested!"
                        })
                    }
                }
            }
        )
    }
}