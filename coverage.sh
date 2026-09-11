#!/bin/sh
# Optional coverage tooling; the regular game and tests have no downloaded dependencies.
set -eu
cd "$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
./build.sh compile-tests
if [ -n "${JAVA_HOME:-}" ] && [ -x "$JAVA_HOME/bin/java" ]; then
  CHESS_JAVA="$JAVA_HOME/bin/java"
elif [ -x /opt/homebrew/opt/openjdk/bin/java ]; then
  CHESS_JAVA=/opt/homebrew/opt/openjdk/bin/java
else
  CHESS_JAVA=java
fi
mkdir -p build/tools
fetch() {
  file="$1"; checksum="$2"; url="$3"
  if [ ! -f "$file" ]; then curl -fsSL "$url" -o "$file"; fi
  printf '%s  %s\n' "$checksum" "$file" | shasum -a 256 -c -
}
fetch build/tools/jacoco-agent.jar 3fb76eea65f81bd9415202bab34b6571728841dff1ab8e6bbe81adc2e299face https://repo.maven.apache.org/maven2/org/jacoco/org.jacoco.agent/0.8.14/org.jacoco.agent-0.8.14-runtime.jar
fetch build/tools/jacoco-cli.jar 811c7f8c6b358c5d68a8973cfa867f6892be7a671b697a4b13c4b447e6daf75c https://repo.maven.apache.org/maven2/org/jacoco/org.jacoco.cli/0.8.14/org.jacoco.cli-0.8.14-nodeps.jar
"$CHESS_JAVA" -javaagent:build/tools/jacoco-agent.jar=destfile=build/jacoco.exec,append=false -Djava.awt.headless=true -cp build/classes:build/test-classes model.ChessTests
"$CHESS_JAVA" -javaagent:build/tools/jacoco-agent.jar=destfile=build/jacoco.exec -Djava.awt.headless=true -cp build/classes:build/test-classes model.OracleTests
if [ "${1:-}" = --ui ]; then
  "$CHESS_JAVA" -javaagent:build/tools/jacoco-agent.jar=destfile=build/jacoco.exec -cp build/classes:build/test-classes model.SwingTests
fi
"$CHESS_JAVA" -jar build/tools/jacoco-cli.jar report build/jacoco.exec --classfiles build/classes --sourcefiles src --html build/coverage --xml build/coverage.xml
printf 'Coverage report: build/coverage/index.html\n'
