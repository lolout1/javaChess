@echo off
echo Creating Windows-accessible copy of Chess Game...

REM Create directory in Windows Documents folder
set WINDOWS_PATH=%USERPROFILE%\Documents\JavaChess
if not exist "%WINDOWS_PATH%" mkdir "%WINDOWS_PATH%"

REM Copy project files
echo Copying files to %WINDOWS_PATH%...
xcopy /E /I /Y "%~dp0src" "%WINDOWS_PATH%\src"
copy /Y "%~dp0run-chess.bat" "%WINDOWS_PATH%"

echo.
echo Project copied to: %WINDOWS_PATH%
echo.
echo To run the chess game:
echo 1. Open: %WINDOWS_PATH%
echo 2. Double-click run-chess.bat
echo.
start "" "%WINDOWS_PATH%"
pause
