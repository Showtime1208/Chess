# Chess

A local, two-player Java/Swing chess game. White starts at the bottom; **Flip board** changes perspective without changing the turn.

## Play

On the prepared Mac, open the sibling **Chess.app**. It bundles Java and needs no installation. The app is built for Apple Silicon, for local use; it is not a notarized public release.

To run from source on macOS or Linux, install a JDK 17 or later, then run:

```sh
./build.sh run
```

On macOS you can also double-click **Play Chess.command**. The build script respects `JAVA_HOME`, detects Homebrew OpenJDK, then falls back to Java on your path. It creates `build/Chess.jar`, with all piece images included. The JAR runs with Java 8 or later from any directory:

```sh
java -jar /path/to/Chess/build/Chess.jar
```

## Controls and rules

- Click your piece, then a highlighted square. Click the piece again to deselect, or another friendly piece to change selection. Keyboard focus and Space also activate squares.
- Legal moves always protect your king. Check, checkmate and stalemate are displayed; kings cannot be captured.
- Castling moves the king and rook, enforces unmoved pieces and safe transit squares, and supports both sides and colors.
- En passant is available only immediately after a two-square pawn move and must not expose the king.
- Promotion offers queen, rook, bishop or knight. Cancelling leaves the pawn unmoved.
- **Undo move** restores the previous position, captures, turn, castling rights, en passant, move counters and repetition state. It can also undo a draw claim.
- **Claim draw** becomes available when the current position has occurred three times or 100 half-moves have elapsed without a capture or pawn move. Claims based on a proposed next move are not supported.
- Fivefold repetition, 150 half-moves, stalemate and standard insufficient-material positions end the game automatically. Checkmate takes precedence over the move-count draw.
- **New game** clears the board and history after confirmation if a game is underway.

Move history uses coordinate notation, with capture and check indicators, castling notation and promotion choices. Capture scores show points won by each player. Games are held in memory; closing the app ends the current game. This version does not include AI, online play, clocks or saved games.

To build a new self-contained Mac app with a JDK that includes `jpackage`:

```sh
./package-mac.sh                  # writes build/mac/Chess.app
./package-mac.sh /new/output/dir  # preserves any existing app
```

The final matching-source Mac build is also supplied as **Chess-Mac.zip**. See [TESTING.md](TESTING.md) for measured verification results.

## Tests

No test framework, network access or Python installation is needed to run the checked-in tests:

```sh
./build.sh test       # Rule, model, controller, perft and oracle tests
./build.sh test-ui    # Real Swing controls; needs a graphical desktop
./coverage.sh --ui    # Optional JaCoCo report; first run downloads checksum-pinned tooling
```

On a headless Linux machine, use `xvfb-run -a ./build.sh test-ui`. Coverage without `--ui` runs only the headless suites. Reports are written to `build/coverage/index.html`. A macOS sandbox that cannot register windows may abort a direct Java GUI process; the packaged Mac application works through the desktop launcher. `DesktopTestRunner` supports the same desktop-launched route for UI verification.

The suite contains:

- 28 focused regression groups for rules, API errors, atomic rejected moves, copying, scores, promotion, repetition, draw clocks, undo and controller behavior.
- Perft checks for the starting position through depth 4 (197,281 leaf positions), Kiwipete through depth 3 (97,862), and an en passant endgame through depth 3 (2,812), including all underpromotion branches.
- 3,346 independent static fixtures and 50 deterministic game replays: 10,751 verified positions in total, 200,645 legal moves compared, and every replayed move undone and reapplied.
- Real Swing tests covering both orientations, square input, undo, restart/cancel, draw claims, game-over input, all promotion dialogs, cancellation and startup.

The offline oracle fixtures were generated with [python-chess 1.11.2](https://python-chess.readthedocs.io/en/latest/core.html), seed 1208. To regenerate them in a separate Python environment, install `chess==1.11.2` and run `python test/tools/generate_oracle.py`. Python-chess is used only to generate test data; the application does not depend on or bundle it.

GitHub Actions is configured to run the rules and desktop tests on JDK 17, 21 and 25 after a push or pull request. The workflow has not been run on GitHub as part of the local delivery.

These are broad regression and differential tests, not a proof over every reachable chess position. Insufficient-material detection covers standard material cases, not arbitrary blocked-pawn dead-position analysis.

## Structure

- `src/model/board/ChessBoard.java`: authoritative legal moves, attacks, state, special moves and results.
- `src/model/piece/`: piece movement geometry and cached image resources. `getValidMoves` returns candidate moves; use `ChessBoard.getLegalMoves` for king-safe moves.
- `src/controller/`: user input and promotion coordination.
- `src/view/`: shared Swing board renderer and reversed perspective.
- `src/Main.java`: startup on the Swing event thread.
- `test/model/`, `test/fixtures/`: executable tests and independent reference positions.

Coordinates use row 0 for rank 1 and column 0 for file a. `set` and `removePiece` are position-editing operations: they invalidate history and transient state. Normal gameplay must use `movePiece`. `makeCopy` copies current state, but intentionally excludes the undo stack. `getBoard` returns a detached snapshot.
