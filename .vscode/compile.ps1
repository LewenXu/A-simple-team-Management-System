$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$compiler = "C:\Program Files\Java\jdk1.8.0_351\bin\javac.exe"
$output = Join-Path $root "bin"

if (-not (Test-Path -LiteralPath $compiler)) {
    throw "Java 8 compiler not found: $compiler"
}

New-Item -ItemType Directory -Path $output -Force | Out-Null
$sources = Get-ChildItem -Path @(
    (Join-Path $root "ManagerPanelApp.java"),
    (Join-Path $root "au"),
    (Join-Path $root "controller"),
    (Join-Path $root "model")
) -Recurse -Filter "*.java" | Select-Object -ExpandProperty FullName

& $compiler -Xlint:all -encoding UTF-8 -d $output $sources
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}
