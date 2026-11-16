# MerkOffice

MerkOffice is an offline-first Android document studio. It does not require an account, subscription, or internet connection for its editor features.

## Implemented in the current build
- Rich-text editing in an offline editor (bold, italic, underline, headings, text sizing, alignment, lists, tables, undo/redo).
- Create new documents and save them in the app's private local storage.
- Browse and reopen documents saved by MerkOffice.
- Export documents as HTML to a location selected with Android's file picker.
- Share document text with other Android apps.
- Print documents through Android's print service; choose Save as PDF when the device offers it.
- Letter, invoice, ID-card layout, meeting-notes, and Hausa text templates.
- Word/character counter and a basic arithmetic helper.
- Local document handling; no server or account is used.

## Build and download
Every push to `main` starts the GitHub Actions workflow **Build MerkOffice APK**.
1. Open the repository's **Actions** tab.
2. Select the latest **Build MerkOffice APK** run.
3. Wait for the run to finish successfully.
4. Download the **MerkOffice-debug-apk** artifact and extract the APK from the ZIP.

## Important limitations
This is an offline document editor foundation, not a complete Microsoft Office replacement. It currently saves and exports HTML documents, not native Microsoft Word DOCX or Excel XLSX files. It does not yet provide PDF text editing, camera scanning/OCR, real biometric/thumbprint capture, cryptographic password protection, a full ID-card photo/signature workflow, spreadsheets, or advanced statistical analysis. The ID-card template is a printable layout only; it does not verify identity. Printing and PDF output depend on Android's installed print service.

## Privacy
Documents saved with the Save button remain in the app's private storage on the device. Export and Share use Android system dialogs and only send data when the user chooses a destination or app.
