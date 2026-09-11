# Verification report

Verified locally on September 11, 2026. The executable Mac app was launched successfully and the separate desktop test app reported **PASS**.

## Results

- **28 regression groups passed.** Piece movement, board state, illegal moves, check protection, both castlings, en passant, all promotions, capture scoring, copy isolation, undo, draw rules and controller input.
- **309,569 perft leaf positions matched** across ten reference counts: initial position depths 1–4, Kiwipete depths 1–3 and an en passant endgame depths 1–3.
- **10,751 independent positions matched**, comprising 3,346 static positions and 7,405 replayed moves from 50 seeded games. Complete move sets agree on **200,645 legal moves**. Every replayed move was undone and reapplied.
- **Desktop integration and startup passed.** Both board orientations, input mapping, undo, new-game confirmation/cancellation, draw claims, checkmate input blocking, four promotion choices and cancellation were exercised through real Swing controls.
- The source compiles to Java 8-compatible bytecode. Headless verification used OpenJDK 25; the packaged desktop verification used bundled Temurin 21.

## Coverage

Measured by JaCoCo 0.8.14, combining the headless and desktop runs:

| Component | Lines covered | Branches covered |
|---|---:|---:|
| Main | 9/10 (90.00%) | 0/0 (—) |
| model/piece | 300/300 (100.00%) | 243/266 (91.35%) |
| view | 120/123 (97.56%) | 65/70 (92.86%) |
| controller | 34/34 (100.00%) | 32/34 (94.12%) |
| model/board | 208/208 (100.00%) | 340/346 (98.27%) |

The full report is in [build/coverage/index.html](build/coverage/index.html). Headless output is in [build/test-results.txt](build/test-results.txt); the desktop result is in [build/desktop-tests.txt](build/desktop-tests.txt).

Uncovered branches include defensive resource failures, redundant bounds guards and some visual-only paths. These counts are reported as measured; they are not a claim that every reachable chess position has been tested. The independent fixture generator covers every piece/color across board squares and mixes sparse, crowded and game-derived positions.

## Reproduce

```sh
./build.sh test
./build.sh test-ui
./coverage.sh --ui
```

The normal test suite is offline and dependency-free. The coverage script downloads two checksum-pinned JaCoCo files on first use. UI tests require a graphical desktop; on Linux use `xvfb-run -a`. GitHub Actions is configured for JDK 17, 21 and 25, but hosted CI has not been run because these changes have not been pushed.

The Mac sandbox did not allow direct shell-launched Java windows to register with the desktop. Packaging and launching the game and test runner as native applications succeeded. The regular packaged app contains no test code or coverage agent.
