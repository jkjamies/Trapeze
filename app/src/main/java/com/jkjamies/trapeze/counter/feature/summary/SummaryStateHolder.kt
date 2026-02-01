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

package com.jkjamies.trapeze.counter.feature.summary

import android.util.Log
import androidx.compose.runtime.Composable
import com.jkjamies.trapeze.TrapezeStateHolder
import com.jkjamies.trapeze.navigation.TrapezeNavigator

class SummaryStateHolder(
    private val navigator: TrapezeNavigator
) : TrapezeStateHolder<SummaryScreen, SummaryState, SummaryEvent>() {

    @Composable
    override fun produceState(screen: SummaryScreen): SummaryState {
        return SummaryState(
            finalCount = screen.finalCount,
            eventSink = { event ->
                when (event) {
                    SummaryEvent.Back -> navigator.pop()
                    SummaryEvent.PrintValue -> Log.d("Summary", "Final Count: ${screen.finalCount}")
                }
            }
        )
    }
}
