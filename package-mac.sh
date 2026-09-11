#!/bin/sh
set -eu
cd "$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
./build.sh
if [ -n "${JAVA_HOME:-}" ] && [ -x "$JAVA_HOME/bin/jpackage" ]; then
  CHESS_PACKAGER="$JAVA_HOME/bin/jpackage"
elif [ -x /opt/homebrew/opt/openjdk/bin/jpackage ]; then
  CHESS_PACKAGER=/opt/homebrew/opt/openjdk/bin/jpackage
else
  CHESS_PACKAGER=jpackage
fi
mkdir -p build/package
cp build/Chess.jar build/package/
"$CHESS_PACKAGER" --type app-image --name Chess --input build/package --main-jar Chess.jar \
  --dest "${1:-build/mac}" --mac-package-identifier com.showtime1208.chess --app-version 1.0.1 \
  --java-options '-Dapple.awt.application.name=Chess'
