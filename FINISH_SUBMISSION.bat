@echo off
set "LINE=InfernoBlobAudio=https://github.com/Deagsly/Blobs.git:64cc4becd036a4c14d54ccc2171ffbcd5d3442be"

echo.
echo ==========================================
echo      AUTO-SUBMISSION HELPER
echo ==========================================
echo.
echo 1. I am copying the secret line to your clipboard...
echo    [%LINE%]
echo.
echo %LINE%| clip

echo 2. I am opening the Plugin Hub page...
echo    (GitHub will ask you to "Fork" -> Click "Propose changes")
echo.
timeout /t 2 >nul
start "" "https://github.com/runelite/plugin-hub/edit/master/plugins.properties"

echo ==========================================
echo             WHAT YOU DO NEXT
echo ==========================================
echo.
echo 1. Scroll to the VERY BOTTOM of the web page.
echo 2. Press CTRL+V to paste the line.
echo 3. Click the green "Commit changes" (or "Propose changes") button.
echo 4. Click "Create Pull Request".
echo.
echo DONE.
pause
