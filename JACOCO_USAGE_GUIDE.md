# How to Use Jacoco Gradle Configuration

## Overview
Your project now uses a separate `jacoco.gradle` file for all Jacoco test coverage configuration. This provides better organization and maintainability.

## Current Setup

### 1. Project Structure
```
UserApp/
├── jacoco.gradle          # ✅ Separate Jacoco configuration
├── app/
│   └── build.gradle.kts   # ✅ Applies jacoco.gradle
└── ...
```

### 2. How It Works

#### `jacoco.gradle` - Central Configuration
- Contains all Jacoco settings
- Enhanced exclusion patterns for synthetic Kotlin classes
- Automatic cleanup of synthetic class HTML files
- Applies the Jacoco plugin

#### `app/build.gradle.kts` - Application Module
- Applies the `jacoco.gradle` file: `apply(from = "../jacoco.gradle")`
- Contains Android-specific configuration
- No duplicate Jacoco settings

## How to Execute Jacoco Report

### Option 1: From Project Root (Recommended)
```bash
cd /Users/nrawat/AndroidStudioProjects/UserApp
./gradlew jacocoTestReport
```

### Option 2: From Android Studio Terminal
1. Open Android Studio
2. Open Terminal tab at bottom
3. Run: `./gradlew jacocoTestReport`

### Option 3: From Gradle Tasks
1. In Android Studio, open Gradle panel (usually right side)
2. Navigate to: `UserApp > app > Tasks > reporting`
3. Double-click `jacocoTestReport`

## What Happens When You Run It

1. **Pre-generation**: Cleans old `htmlReport` folder
2. **Test Execution**: Runs unit tests with coverage
3. **Report Generation**: Creates XML and HTML reports
4. **Post-generation Cleanup**: Removes synthetic class HTML files automatically

You'll see output like:
```
Removed synthetic class report: /path/to/AddUserScreenKt$AddUserContent$4$1$3.html
Removed synthetic class report: /path/to/ComposableSingletons$LoginScreenKt$lambda-2$1.html
...
```

## Report Locations

After running, reports are available at:
- **HTML Report**: `app/build/reports/jacoco/jacocoTestReport/html/index.html`
- **XML Report**: `app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml`

## Key Features

✅ **Clean Reports**: No synthetic Kotlin class files cluttering the report
✅ **Automatic Cleanup**: Old reports removed before generation
✅ **Modular Configuration**: Separate `jacoco.gradle` file for better organization
✅ **Enhanced Exclusions**: Filters out Compose, Hilt, Room, and other generated classes

## Troubleshooting

### If Java Runtime Issues Occur:
1. Ensure JDK 11+ is installed
2. Set `JAVA_HOME` environment variable
3. Restart Android Studio

### If Task Not Found:
- Ensure `jacoco.gradle` is in the project root
- Check that `apply(from = "../jacoco.gradle")` is in `app/build.gradle.kts`

### If Reports Still Have Synthetic Classes:
- The cleanup runs after generation, so you might see the files briefly
- Check console output for "Removed synthetic class report" messages

## Customization

To modify Jacoco settings, edit `jacoco.gradle`:
- **Exclusion patterns**: Modify `coverageExclusions` list
- **Report formats**: Change `reports` block
- **Cleanup patterns**: Modify the `doLast` block regex patterns

---

**Ready to use!** Run `./gradlew jacocoTestReport` to generate your clean coverage reports.</content>
<parameter name="filePath">/Users/nrawat/AndroidStudioProjects/UserApp/JACOCO_USAGE_GUIDE.md
