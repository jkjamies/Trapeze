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

package com.jkjamies.strata

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest

class LaunchOrThrowTest : BehaviorSpec({

    Given("a coroutine scope") {
        When("launchOrThrow is called") {
            Then("it runs the block successfully") {
                runTest {
                    var ran = false
                    val job = launchOrThrow {
                        ran = true
                    }
                    job.join()
                    ran shouldBe true
                }
            }
        }

        When("launchOrThrow is called on a cancelled scope") {
            Then("it throws IllegalStateException") {
                val scope = TestScope()
                scope.cancel()

                shouldThrow<IllegalStateException> {
                    scope.launchOrThrow { }
                }
            }
        }
    }
})
