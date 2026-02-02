# Trapeze Framework

A type-safe, MESA-inspired architecture for Jetpack Compose. This framework enforces a strict separation between logic (StateHolder), presentation (UI), and identity (Screen).

## Libraries

- **Trapeze**: The core types (`TrapezeStateHolder`, `TrapezeState`, `TrapezeScreen`, `TrapezeEvent`, `TrapezeContent`).
- **TrapezeNavigation**: The navigation layer (`TrapezeNavigator`, `TrapezeNavHost`, `TrapezeInterop`).

## Core Architecture

### 1. The Screen (Identity)
The `TrapezeScreen` is a `Parcelable` data structure. It acts as the unique identifier for a feature and carries all necessary initialization parameters.

### 2. The StateHolder (Logic)
The `TrapezeStateHolder` is the brain of the feature. It manages the lifecycle and business logic, producing an immutable `State`. It is implemented as a standard class that exposes a `produceState` Composable function.

### 3. The UI (Stateless View)
The `TrapezeUi` is a pure mapping function. It takes a `State` and a `Modifier` and emits UI. It communicates user interactions back to the StateHolder via lambda events defined in the State.

### 4. The Runtime (The Weld)
The `TrapezeContent` bridge connects a `Screen` to its `StateHolder` and `UI`.

## Clean Architecture & Modules
Trapeze encourages a 4-layer modular architecture per feature:
1. **API (`:features:foo:api`)**: Public interfaces (UseCases, Screen, Event).
2. **Domain (`:features:foo:domain`)**: Pure Kotlin business logic (UseCases, Interactors).
3. **Data (`:features:foo:data`)**: Repository implementations and data sources.
4. **Presentation (`:features:foo:presentation`)**: Android-specific UI and StateHolder.

## Dependency Injection
We use **Metro** (Dagger-compatible) for statically verified dependency injection.
- Bind implementations in `Data/Domain` using `@ContributesBinding(AppScope::class)`.
- Inject interfaces in `Presentation`.

## Example: Counter Feature

```kotlin
import android.os.Parcelable
import androidx.compose.runtime.*
import androidx.compose.material3.*
import com.jkjamies.trapeze.*
import kotlinx.parcelize.Parcelize

// 1. Definition (Screen, State, Event)
@Parcelize
data class CounterScreen(val initialValue: Int) : TrapezeScreen, Parcelable

data class CounterState(
    val count: Int,
    val eventSink: (CounterEvent) -> Unit
) : TrapezeState

sealed interface CounterEvent : TrapezeEvent {
    data object Increment : CounterEvent
}

// 2. StateHolder Implementation
class CounterStateHolder : TrapezeStateHolder<CounterScreen, CounterState, CounterEvent>() {
    @Composable
    override fun produceState(screen: CounterScreen): CounterState {
        // use rememberSaveable to persist state across navigation/process death
        var count by rememberSaveable { mutableIntStateOf(screen.initialValue) }

        return CounterState(
            count = count,
            eventSink = { event ->
                when (event) {
                    CounterEvent.Increment -> count++
                }
            }
        )
    }
}

// 3. UI Implementation
@Composable
fun CounterUi(modifier: Modifier = Modifier, state: CounterState) {
    Button(
        onClick = { state.eventSink(CounterEvent.Increment) },
        modifier = modifier
    ) {
        Text("Count: ${state.count}")
    }
}
```

## Navigation Host

Use `TrapezeNavHost` to drive navigation.

- Features call `navigator.navigate(...)` and `navigator.pop()`.
- The host owns the stack state and handles persistence.

```kotlin
import androidx.compose.runtime.Composable
import com.jkjamies.trapeze.navigation.TrapezeNavHost
import com.jkjamies.trapeze.TrapezeContent

@Composable
fun AppRoot() {
    TrapezeNavHost(initialScreen = CounterScreen(0)) { screen ->
        // Use composition locals or DI to get navigator
        val navigator = LocalTrapezeNavigator.current
        
        when(screen) {
            is CounterScreen -> {
                TrapezeContent(
                    screen = screen,
                    stateHolder = CounterStateHolder(),
                    ui = ::CounterUi
                )
            }
            // ... other screens
        }
    }
}
```

## Interop (Advanced)

For legacy apps, global events, or system alerts, use the `TrapezeInterop` interface.

1. **Define Events**:
   ```kotlin
   sealed interface AppInteropEvent : TrapezeInteropEvent {
       data class ShowAlert(val message: String) : AppInteropEvent
   }
   ```

2. **Send from StateHolder**:
   ```kotlin
   // Inject 'interop: TrapezeInterop' into your StateHolder
   interop.send(AppInteropEvent.ShowAlert("Hello"))
   ```

3. **Handle in Host**:
   ```kotlin
   val interop = remember {
       object : TrapezeInterop {
           override fun send(event: TrapezeInteropEvent) {
               when(event) {
                   is AppInteropEvent.ShowAlert -> showToast(event.message)
               }
           }
       }
   }
   
   // Pass 'interop' to your StateHolders via DI or constructor
   ```

## Strata (Interactors)
Trapeze uses **Strata** for business logic, providing a uniform `Interactor` API.

### SuspendingWorkInteractor
For one-shot async operations (e.g., API calls, Database writes).
```kotlin
@Inject
class SaveCount(private val repo: CountRepo) : SuspendingWorkInteractor<Int, Unit>() {
    override suspend fun doWork(params: Int): Unit = repo.save(params)
}

// Usage:
launchOrThrow {
    saveCount.invoke(5).onFailure { error -> /* handle error */ }
}
```

### SubjectInteractor
For observing data streams (Flows). Requires explicit invocation to start emission.
```kotlin
@Inject
class ObserveCount(private val repo: CountRepo) : SubjectInteractor<Unit, Int>() {
    override fun createObservable(params: Unit): Flow<Int> = repo.observeCount()
}

// Usage in StateHolder:
LaunchedEffect(Unit) {
    observeCount.invoke(Unit)
}
val count by observeCount.flow.collectAsState(initial = 0)
```
