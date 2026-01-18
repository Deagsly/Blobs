@echo off
echo Pushing code to https://github.com/Deagsly/Blobs.git ...
git push -u origin main
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Push failed.
    echo Please make sure you are logged in to GitHub.
    echo You may need to create a Personal Access Token if password fails.
) else (
    echo.
    echo [SUCCESS] Code pushed to GitHub!
)
pause
