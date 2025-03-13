@echo off
echo Compiling the application...

:: Create bin directory if it doesn't exist
if not exist bin mkdir bin

:: Compile all Java files
javac -d bin src\main\java\com\trackese\models\*.java src\main\java\com\trackese\utils\*.java src\main\java\com\trackese\ui\*.java

:: Check if compilation was successful
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Compilation successful!
echo Running the application...

:: Run the application
java -cp bin com.trackese.ui.MainFrame

pause 