#!/bin/sh
set -eu
cd "$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
if [ -n "${JAVA_HOME:-}" ] && [ -x "$JAVA_HOME/bin/javac" ]; then
  CHESS_JAVA="$JAVA_HOME/bin"
elif [ -x /opt/homebrew/opt/openjdk/bin/javac ]; then
  CHESS_JAVA=/opt/homebrew/opt/openjdk/bin
else
  CHESS_JAVA="$(dirname "$(command -v javac)")"
fi
case "${1:-}" in
  ""|run|test|test-ui|compile-tests) ;;
  *) echo "Usage: ./build.sh [run|test|test-ui|compile-tests]" >&2; exit 2 ;;
esac
mkdir -p build/classes build/test-classes
find build/classes build/test-classes -name '*.class' -delete
find src -name '*.java' -print > build/sources.txt
"$CHESS_JAVA/javac" --release 8 -Xlint:-options -encoding UTF-8 -d build/classes @build/sources.txt
cp -R pieceImages build/classes/
"$CHESS_JAVA/jar" cfe build/Chess.jar Main -C build/classes .
if [ "${1:-}" = test ] || [ "${1:-}" = test-ui ] || [ "${1:-}" = compile-tests ]; then
  find test -name '*.java' -print > build/tests.txt
  "$CHESS_JAVA/javac" --release 8 -Xlint:-options -encoding UTF-8 -cp build/classes -d build/test-classes @build/tests.txt
  if [ "${1:-}" = test-ui ]; then
    "$CHESS_JAVA/java" -cp build/classes:build/test-classes model.SwingTests
  elif [ "${1:-}" = test ]; then
    "$CHESS_JAVA/java" -Djava.awt.headless=true -cp build/classes:build/test-classes model.ChessTests
    "$CHESS_JAVA/java" -Djava.awt.headless=true -cp build/classes:build/test-classes model.OracleTests
  fi
elif [ "${1:-}" = run ]; then
  exec "$CHESS_JAVA/java" -jar build/Chess.jar
fi
