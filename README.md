# PR Merge Android App

An Android app that replicates the functionality of your GitHub PR merge script, allowing you to
approve and merge pull requests directly from your mobile device.

## Features

- **GitHub PR Link Input**: Paste any GitHub PR link to extract owner, repository, and PR number
- **Optional PR Approval**: Choose whether to approve the PR before merging
- **Secure Token Storage**: Store your GitHub Personal Access Token securely using SharedPreferences
- **Modern UI**: Clean, Material Design 3 interface built with Jetpack Compose
- **Error Handling**: Comprehensive error messages and success feedback
- **Settings Management**: Dedicated settings screen for token configuration

## Setup Instructions

### 1. GitHub Personal Access Token

1. Go to GitHub Settings → Developer settings → Personal access tokens
2. Click "Generate new token (classic)"
3. Select the following scopes:
    - `repo` (Full control of private repositories)
4. Generate and copy the token

### 2. App Configuration

1. Open the PR Merge app
2. Tap the Settings icon (⚙️) in the top-right corner
3. Paste your GitHub token in the "GitHub Token" field
4. Tap "Save Token"

## Usage

1. **Enter PR Link**: Paste the GitHub PR URL (e.g., `https://github.com/owner/repo/pull/123`)
2. **Choose Approval**: Check "Approve PR before merging" if you want to approve the PR first
3. **Merge**: Tap the "Merge PR" button to execute the operation

## Supported PR Link Formats

The app automatically extracts information from GitHub PR links in this format:

```
https://github.com/{owner}/{repository}/pull/{pr_number}
```

## Technical Details

### Architecture

- **MVVM Pattern**: Uses ViewModel for state management
- **Jetpack Compose**: Modern declarative UI framework
- **Navigation Component**: Screen navigation between main app and settings
- **Retrofit**: HTTP client for GitHub API integration
- **Coroutines**: Asynchronous network operations

### GitHub API Integration

- Uses GitHub REST API v4
- Endpoints used:
    - `POST /repos/{owner}/{repo}/pulls/{pull_number}/reviews` (for approval)
    - `PUT /repos/{owner}/{repo}/pulls/{pull_number}/merge` (for merging)

### Security

- Tokens are stored locally using Android SharedPreferences
- Token input field uses password masking
- Network traffic is logged only in debug builds

## Dependencies

```kotlin
// Networking
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

// UI & Navigation
implementation("androidx.navigation:navigation-compose:2.7.6")
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
implementation("androidx.compose.material:material-icons-extended:1.5.4")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
```

## Error Handling

The app provides clear error messages for common issues:

- Invalid PR link format
- Missing GitHub token
- Network connectivity problems
- GitHub API errors (insufficient permissions, PR already merged, etc.)

## Original Script Comparison

This app replicates the functionality of the original Zsh script:

- ✅ GitHub token authentication
- ✅ PR link parsing with regex
- ✅ Optional PR approval before merging
- ✅ GitHub API integration for approve/merge operations
- ✅ Error handling and user feedback
- ✅ Continuous operation (app remains open for multiple PRs)

## Build & Run

```bash
# Clone and build
./gradlew build

# Install debug version
./gradlew installDebug

# Or run directly
./gradlew run
```

## Requirements

- Android API 24+ (Android 7.0)
- Internet permission for GitHub API calls
- Valid GitHub Personal Access Token with repo permissions