# Fix backpack rapid-open causing item duplication

## Goal

Fix a Slimefun backpack vulnerability where opening it rapidly/repeatedly could spawn multiple InventoryViews (different snapshot instances), allowing items to be duplicated (item farming). After the fix, the same player can have at most one "pending load" or "opened" backpack view at a time; repeated open requests are rejected outright.

## Background / Repro

Reproduction steps provided by the user (sending rapid right-click packets via the Wraith Client's AirPlace feature):

1. Install the Wraith Client, enable the AirPlace feature
2. Prepare 1 backpack, name it, put the item to be farmed inside
3. Log out and back into the server
4. Look at the sky, right-click to open the backpack
5. Take out one item, close the backpack
6. Open the backpack again — the item just taken out is still in the backpack, and can be taken out again

Confirmed root cause (verified, not speculative):

- `BackpackListener.openBackpackInternal` does not block duplicate open requests while the backpack is loading asynchronously; `openingPlayers.add(...)`'s return value is not checked, so multiple cache-miss loads can run in parallel, generating multiple `PlayerBackpack`/Inventory instances for the same logical backpack (`ProfileDataController.getBackpackAsync`'s check-then-load does not coalesce).
- When the player already has a backpack view open, the current logic actively closes the old view and reopens it, which can switch back to the old snapshot instance, making the already-removed item "reappear".

## Requirements

- R1: While a backpack is loading asynchronously, duplicate open requests from the same player must be rejected (using the return value of `openingPlayers.add` for atomic interception).
- R2: If a player already has an open backpack view, a new open request must be rejected instead of performing "force close and reopen".
- R3: The player's state in `openingPlayers` must be released on async-load success, on returning null, and on exceptional completion alike (unified cleanup in the main-thread completion callback), to prevent the player being permanently locked out after a failure.
- R4: Preserve existing behavior: ownership validation, backpack metadata validation, "backpack already opened by another player" protection, open/close sounds, and save-on-close (`onClose` → `saveBackpackInventory`).
- R5: Scope the fix to `BackpackListener.java`; do not modify `ProfileDataController` / cache / storage layers (to avoid scope creep and compatibility risk).

## Constraints

- Minimal correct fix: no refactoring, formatting, or fallback branches unrelated to this vulnerability.
- Follow the repository's coding standards (Palantir format, Spotless checks).
- The workspace already has 13 dirty paths unrelated to this task; do not touch or revert them.

## Acceptance Criteria

- [ ] AC1: The same player can have at most one pending-load or opened backpack view; duplicate Use packets do not trigger parallel loading, nor do they trigger close-and-reopen.
- [ ] AC2: Following the repro steps (including AirPlace rapid right-click): take out an item → close → reopen, the item does not reappear and cannot be duplicated.
- [ ] AC3: Reopening after a normal backpack close works correctly (does not break normal UX).
- [ ] AC4: After an async load failure (exception/null), the player can still try to open the backpack again (no permanent lockout).
- [ ] AC5: The same backpack still cannot be opened by two players at once (existing protection does not regress).
- [ ] AC6: The JetBrains project build passes; `./gradlew test` and `./gradlew spotlessCheck` pass.

## Verification

- JetBrains build (repository requirement takes priority) → `./gradlew test` → `./gradlew spotlessCheck`.
- Manual regression: verify on a test server following the repro steps above (including the AirPlace scenario and the normal open/close scenario).

## Out of Scope

- Refactoring load coalescing (in-flight load coalescing) at the storage/cache layer.
- Other listener or utility class changes unrelated to the backpack-open race condition.
