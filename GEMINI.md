# Trapeze Project Guide

## Project Overview
A Pure-Compose driven architectural library implementing the MESA framework. The library facilitates a rigid UDF flow where the UI is a stateless projection of a single State object.

## MESA Pillars
- **Modular:** Feature isolation by design; components are decoupled and portable.
- **Explicit:** All interactions are defined through the Screen, State, and Event contracts.
- **State-driven:** The State object is the Single Source of Truth (SSoT) and contains the event processing hook.
- **Architecture:** Provides the structural "Trapeze" to swing between Logic and UI.

## Technical Contract
- **State:** An immutable data class containing all display data AND an `eventSink: (TrapezeEvent) -> Unit`.
- **Logic (StateHolder):** Responsible for producing the State object.
- **UI (TrapezeUi):** A stateless Composable that consumes the State.
- **Glue (TrapezeContent):** The entry point that bridges a Screen to its StateHolder and UI.

## Coding Standards
- **UDF Flow:** UI sends Events to the `eventSink` -> StateHolder processes -> New State is emitted -> UI recomposes.
- **Injection Agnostic:** The library accepts StateHolder instances; consumers choose the injection strategy (Manual, Dagger, etc.).
- **No ViewModels:** Logic belongs in `TrapezeStateHolder` to maintain MESA alignment.
- **Stateless UI:** Composables must never hold business logic or persistent state.
- **License Headers:** All source files must include the Apache 2.0 license header. The year should be `2026` (if current year) or `2026-<currentYear>`.

## Clean Architecture & Modules
- **API (`:features:foo:api`):** Public interfaces (UseCase definitions, Screen arguments). Stable API.
- **Domain (`:features:foo:domain`):** Business logic implementation (UseCases, Interactors). Internal details.
- **Data (`:features:foo:data`):** Repository implementations and data sources.
- **Presentation (`:features:foo:presentation`):** UI, StateHolder, and Metro bindings.

## Dependency Injection (Metro)
- **Graph:** Use `AppGraph` (AppScope) for global dependencies.
- **Binding:** Use `@ContributesBinding(AppScope::class)` in Domain/Data to expose implementations.
- **Injection:** Inject interfaces. Use `Lazy<T>` for heavy dependencies or to delay initialization.

## Event Safety
- **Sink:** Use `eventSink: (Event) -> Unit` in State.
- **Wrapper:** In StateHolder, wrap the sink using `wrapEventSink` helper to ensure CoroutineScope is active before delivery.

## Strata Patterns
- **Interactors:**
    - `SuspendingWorkInteractor<P, R>`: For one-shot async work. Returns `Result<R>`.
    - `SubjectInteractor<P, T>`: For observing flows. Requires `invoke(params)` to start emission.
- **Error Handling:** Use `launchOrThrow` in StateHolders to catch exceptions from Interactors. Use `.onFailure { }` on Interactor results.
- **Triggering:** `SubjectInteractor` flows MUST be triggered by invoking them. Triggering should happen in the UI/Logic layer (e.g., `LaunchedEffect` or explicit call) rather than in the UseCase `init` block, to maintain explicit control.

## The Trapeze Contract
1. **Screen:** `Parcelable` destination key.
2. **Event:** Sealed interface of user intents.
3. **State:** Data class with values and the `eventSink` closure.
4. **StateHolder:** `@Composable produceState(screen: T): S`.
5. **UI:** `@Composable (Modifier, State) -> Unit`.