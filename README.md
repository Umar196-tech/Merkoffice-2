# MerkOffice

MerkOffice is an offline-first Android document editor project.

## Current starter features
- Create and edit plain-text documents.
- Save documents privately inside the app on the device.
- Open text files using Android's file picker.
- Export a document to a user-selected location.
- Share document text with other Android apps.
- Send documents to Android's print service / save as PDF where the print service supports it.

## Build an APK
The GitHub Actions workflow builds a debug APK whenever code is pushed to `main`, and can also be started manually from the Actions tab.

After a successful build:
1. Open the repository's **Actions** tab.
2. Select **Build MerkOffice APK**.
3. Open the completed workflow run.
4. Download the **MerkOffice-debug-apk** artifact.

## Important limitations of this first build
This is the working foundation, not the complete planned office suite. It currently edits plain text, not full Microsoft Word DOCX formatting. PDF editing, document scanning/OCR, ID-card designer, signature tools, Hausa interface, spreadsheet/statistics tools, and stronger document protection still need to be implemented and tested. Printing depends on Android's print service.

All document content is handled locally by this starter app; no account or subscription is required.
