$ErrorActionPreference = "Stop"

echo "=== EMERGENCY REBUILDER V2 ==="
echo "[0/4] Cleaning old JAR..."
if (Test-Path "READY_TO_DEPLOY\dist\Appujian.jar") { 
    Remove-Item "READY_TO_DEPLOY\dist\Appujian.jar" -Force 
}

echo "[1/4] Preparing directories..."
if (Test-Path "build\classes") { Remove-Item "build\classes" -Recurse -Force }
New-Item -ItemType Directory -Path "build\classes" -Force | Out-Null

$javac = ".\READY_TO_DEPLOY\dist\jre\bin\javac.exe"
$jar = ".\READY_TO_DEPLOY\dist\jre\bin\jar.exe"

echo "[2/4] Listing Source Files..."
$srcFiles = Get-ChildItem -Path "src" -Recurse -Filter "*.java" | Select-Object -ExpandProperty FullName
$srcFiles | Out-File "sources.txt" -Encoding ascii

echo "[3/4] Compiling (Target Java 17)..."
$cp = "lib\mysql-connector-j-8.2.0.jar;lib\pdfbox-app-3.0.3.jar"

# Force source/target 17 just in case
& $javac -J-Xmx512m -d "build\classes" -cp $cp -source 17 -target 17 "@sources.txt" -encoding UTF-8
if ($LASTEXITCODE -ne 0) {
    echo "COMPILATION FAILED!"
    exit 1
}

echo "[3b/4] Copying Assets (Images/Icons)..."
Copy-Item "src\*.png" "build\classes" -Recurse -Force -ErrorAction SilentlyContinue
Copy-Item "src\*.jpg" "build\classes" -Recurse -Force -ErrorAction SilentlyContinue
Copy-Item "src\*.properties" "build\classes" -Recurse -Force -ErrorAction SilentlyContinue

echo "[4/4] Packaging JAR..."
$manifest = "Manifest-Version: 1.0`r`nMain-Class: id.ac.campus.antiexam.AplikasiUjian`r`nClass-Path: lib/pdfbox-app-3.0.3.jar lib/mysql-connector-j-8.2.0.jar`r`n"
$manifest | Out-File "build\manifest.mf" -Encoding ascii

& $jar cvfm "READY_TO_DEPLOY\dist\Appujian.jar" "build\manifest.mf" -C "build\classes" .

echo "=== SUCCESS! JAR REBUILT & UPDATED ==="
