# Plugin Hub Submission Guide

Follow this guide to get your plugin on the official RuneLite Plugin Hub.

## Phase 1: Publish Your Code

1. **Create a GitHub Repository**:
   - Go to [GitHub.com](https://github.com) and create a new repository named `inferno-blob-audio`.
   - **Do not** check "Initialize with README/gitignore". keep it empty.

2. **Push Your Code**:
   - Open a command prompt in this folder (or use VS Code terminal).
   - Run these commands (replace `<YOUR_USERNAME>` with your GitHub username):
     ```bash
     git remote add origin https://github.com/<YOUR_USERNAME>/inferno-blob-audio.git
     git branch -M main
     git push -u origin main
     ```

## Phase 2: Submit to Plugin Hub

1. **Fork the Plugin Hub**:
   - Go to: [https://github.com/runelite/plugin-hub](https://github.com/runelite/plugin-hub)
   - Click **Fork** (top right).

2. **Add Your Plugin**:
   - In your *forked* repo, open the file `plugins.properties`.
   - Click the **pencil icon** to edit.
   - Scroll to the bottom and add this single line:
     ```
     InfernoBlobAudio=https://github.com/Deagsly/Blobs.git:64cc4becd036a4c14d54ccc2171ffbcd5d3442be
     ```
   - (I have already filled in your Commit Hash above!).

3. **Handle External Dependencies (MP3 Support)**:
   - Since we use MP3 libraries (`mp3spi`, `tritonus`, `jlayer`), you typically need to verify them.
   - **However**, since these are standard libraries on Maven Central, simple submission usually works.
   - *If the build fails asking for verification:*
     - You will see instructions in the PR build logs to run a verification script. 
     - Usually, you just need to edit `package/verification-template/build.gradle` in your fork to add:
       ```gradle
       thirdParty 'com.googlecode.soundlibs:mp3spi:1.9.5.4'
       thirdParty 'com.googlecode.soundlibs:tritonus-share:0.3.7.4'
       thirdParty 'com.googlecode.soundlibs:jlayer:1.0.1.4'
       ```
     - And then run the verification command provided in the log.
   - **For now, try submitting without this step.**

4. **Create Pull Request**:
   - Commit the change to `plugins.properties`.
   - GitHub will ask if you want to **Create Pull Request**. Click yes.
   - Title: `Add Inferno Blob Audio plugin`
   - Description: `Plays audio clips when killing blobs in the Inferno.`

5. **Wait for Checks**:
   - RuneLite's automated system will try to compile your plugin.
   - If it turns Green ✔️: You are good! Wait for a maintainer to approve.
   - If it turns Red ❌: Click "Details" to see why (usually the dependency check mentioned above).

## Phase 3: Wait

It usually takes 1-7 days for a maintainer to review and accept the plugin. Once accepted, it will appear in the Hub automatically!
