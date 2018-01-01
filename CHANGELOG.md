
## 4.2.0


### Added

- Made OK/Cancel strings into library strings for easy overridability [\#157](https://github.com/spacecowboy/NoNonsense-FilePicker/pull/157) ([spacecowboy](https://github.com/spacecowboy))

### Changed

- Update build tools to 26.0.2 and update Dropbox sample to v2 API [\#155](https://github.com/spacecowboy/NoNonsense-FilePicker/pull/155) ([mitchyboy9](https://github.com/mitchyboy9))

## 4.1.0


### Added

- added a static helper method for parsing activity results [\#138](https://github.com/spacecowboy/NoNonsense-FilePicker/pull/138) ([spacecowboy](https://github.com/spacecowboy))


    Thanks to @F43nd1r for #121

## 4.0.1


### Fixed

- Destroy Loader after finish to avoid clearing selections [\#137](https://github.com/spacecowboy/NoNonsense-FilePicker/pull/137) ([spacecowboy](https://github.com/spacecowboy))

## 4.0.0


### Breaking changes

- You are now required to define a `FileProvider` in your manifest for the SD-card picker [\#118](https://github.com/spacecowboy/NoNonsense-FilePicker/pull/118) ([spacecowboy](https://github.com/spacecowboy))


    Due to recent changes in Android 7.0 Nougat, bare File URIs can no longer be returned in a safe way. This change requires you to add an entry to your manifest to use the included `FilePickerFragment` and change how you handle the results.
    - You need to add the following to your app's `AndroidManifest.xml`:

    ``` xml
            <provider
                android:name="android.support.v4.content.FileProvider"
                android:authorities="${applicationId}.provider"
                android:exported="false"
                android:grantUriPermissions="true">
                <meta-data
                    android:name="android.support.FILE_PROVIDER_PATHS"
                    android:resource="@xml/nnf_provider_paths" />
            </provider>
    ```
    - Then you must change your result handling. Here is a code snippet illustrating the change for a single result (the same applies to multiple results):

    ``` java
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // The URI will now be something like content://PACKAGE-NAME/root/path/to/file
        Uri uri = intent.getData();
        // A new utility method is provided to transform the URI to a File object
        File file = com.nononsenseapps.filepicker.Utils.getFileForUri(uri);
        // If you want a URI which matches the old return value, you can do
        Uri fileUri = Uri.fromFile(file);
        // Do something with the result...
    }
    ```

    This change was required in order to fix `FileUriExposedException` being thrown on Android 7.0 Nougat, as reported in [#115](https://github.com/spacecowboy/NoNonsense-FilePicker/issues/115) and [#107](https://github.com/spacecowboy/NoNonsense-FilePicker/issues/107).

    Please see the updated [activity in the sample app](https://github.com/spacecowboy/NoNonsense-FilePicker/blob/master/sample/src/main/java/com/nononsenseapps/filepicker/sample/NoNonsenseFilePicker.java) for more examples.

## 3.1.0


### Changed

- Bump versions [\#113](https://github.com/spacecowboy/NoNonsense-FilePicker/pull/113) ([spacecowboy](https://github.com/spacecowboy))

## 3.0.1


### Added

- Add ability to define a list divider in theme [\#99](https://github.com/spacecowboy/NoNonsense-FilePicker/pull/99) ([spacecowboy](https://github.com/spacecowboy))

### Changed

- Update back button example [d20afa1](https://github.com/spacecowboy/NoNonsense-FilePicker/commit/d20afa13d261c51a4b4c0a0c6ea17b1932854b14)

    Fixes [\#106](https://github.com/spacecowboy/NoNonsense-FilePicker/issues/106)

### Fixed

- Fix some of the samples [\#108](https://github.com/spacecowboy/NoNonsense-FilePicker/pull/108) ([spacecowboy](https://github.com/spacecowboy))
- Use `srcCompat` for vector drawables [\#112](https://github.com/spacecowboy/NoNonsense-FilePicker/pull/112) ([spacecowboy](https://github.com/spacecowboy))

## 3.0.0


### Added

- Add ability to enter a new filename [\#83](https://github.com/spacecowboy/NoNonsense-FilePicker/pull/83) ([spacecowboy](https://github.com/spacecowboy))
- Select one file with a single click [\#92](https://github.com/spacecowboy/NoNonsense-FilePicker/pull/92) ([rastikw](https://github.com/rastikw))

## 2.5.3


### Added

- Add a getItem method to FileItemAdapter [\#88](https://github.com/spacecowboy/NoNonsense-FilePicker/pull/88) ([spacecowboy](https://github.com/spacecowboy))
- Add FastScroller sample implementation [\#89](https://github.com/spacecowboy/NoNonsense-FilePicker/pull/89) ([spacecowboy](https://github.com/spacecowboy))

### Changed

- Pass path into permission and refresh handlers [c9d7035](https://github.com/spacecowboy/NoNonsense-FilePicker/commit/c9d70351e183bf65ed85b2acab5488118c9b1dae)

    This allows for better handling in case of denied/missing permissions,
    as well the ability to request more fine-grained permissions.

    Fixes [\#85](https://github.com/spacecowboy/NoNonsense-FilePicker/issues/85), [\#84](https://github.com/spacecowboy/NoNonsense-FilePicker/issues/84)

### Fixed

- Fix crash when creating dropbox directory [0a511ac](https://github.com/spacecowboy/NoNonsense-FilePicker/commit/0a511acb59fe02ad38d16bc0e4fd05c4a2cc6edb)

    Also improves loading screen usage for directory creation.

    Fixes [\#76](https://github.com/spacecowboy/NoNonsense-FilePicker/issues/76)

## 2.5.2


### Changed

- Show a progress bar when loading dropbox directory [9880562](https://github.com/spacecowboy/NoNonsense-FilePicker/commit/9880562413b7cd42c778b49dc219fb71d07c0e00)

    Fixes [\#74](https://github.com/spacecowboy/NoNonsense-FilePicker/issues/74)
- Add missing underscore [0351a69](https://github.com/spacecowboy/NoNonsense-FilePicker/commit/0351a698587378cbd519cd68a648a0ed9e420ecf)

    Fixes [\#63](https://github.com/spacecowboy/NoNonsense-FilePicker/issues/63)

### Fixed

- Do not load directory again if already loading [c83ad0a](https://github.com/spacecowboy/NoNonsense-FilePicker/commit/c83ad0afc3c7f9503e9e59f5f2fc47a6bcd1cf04)

    Fixes crash if user quickly taps on two different directories,
    where loading directories take a while, like Dropbox or any
    other network source.

    Fixes [\#73](https://github.com/spacecowboy/NoNonsense-FilePicker/issues/73)
- Fix concurrent modification of adapter in dropbox sample [b7baea3](https://github.com/spacecowboy/NoNonsense-FilePicker/commit/b7baea37113435e2a8cb07ca5126b075a67ff128)

    Fixes [\#75](https://github.com/spacecowboy/NoNonsense-FilePicker/issues/75)

### Misc

- Update README.md [\#68](https://github.com/spacecowboy/NoNonsense-FilePicker/pull/68) ([callmepeanut](https://github.com/callmepeanut))

## 2.5.1


### Changed

- Change to the MPL [e9211ff](https://github.com/spacecowboy/NoNonsense-FilePicker/commit/e9211ff53f0f127e56b07888e0905036d764614e)

    To actually be compatible with Android and because it
    is more aligned with my interests.

    Fixes [\#66](https://github.com/spacecowboy/NoNonsense-FilePicker/issues/66)

## 2.5.0


### Changed

- Hide hidden files in SD card picker by default [\#58](https://github.com/spacecowboy/NoNonsense-FilePicker/pull/58) ([dvrajan](https://github.com/dvrajan))
- Add separator above OK/Cancel buttons [846c5e2](https://github.com/spacecowboy/NoNonsense-FilePicker/commit/846c5e209e0478ee8cc284b8a812b1688ee0cc51)

    Fixes [\#60](https://github.com/spacecowboy/NoNonsense-FilePicker/issues/60)

## v2.4.2


### Added

- Add Video Thumbnail preview and set default theme to light [\#53](https://github.com/spacecowboy/NoNonsense-FilePicker/pull/53) ([alishari](https://github.com/alishari))

### Changed

- Lowered minSdk to API 9 [\#55](https://github.com/spacecowboy/NoNonsense-FilePicker/pull/55) ([marbat87](https://github.com/marbat87))

### Fixed

- Change to special image viewtypes for sample [aa53b90](https://github.com/spacecowboy/NoNonsense-FilePicker/commit/aa53b9091eb4bc2aad5c2fbf59cc241bc66cad47)

    This Fixes an issue on older android versions (4.0.3)
    where setting a tint on an imageview would incorrectly
    color the entire image.

    Fixes [\#50](https://github.com/spacecowboy/NoNonsense-FilePicker/issues/50)

## v2.4.1


### Changed

- Update to latest build tools and support library 23.0.1 [\#51](https://github.com/spacecowboy/NoNonsense-FilePicker/pull/51) ([alishari](https://github.com/alishari))

## v2.4.0


### Added

- Make click events overridable [\#49](https://github.com/spacecowboy/NoNonsense-FilePicker/pull/49) ([spacecowboy](https://github.com/spacecowboy))

## v2.3.1


### Fixed

- Make list focusable. [\#45](https://github.com/spacecowboy/NoNonsense-FilePicker/pull/45) ([spacecowboy](https://github.com/spacecowboy))

## v2.3.0


### Added

- Handle runtime permissions in Android M. [c2f4f05](https://github.com/spacecowboy/NoNonsense-FilePicker/commit/c2f4f054b714a218bd368bd2d51876ad79b5d8e9)

    Fixes [\#24](https://github.com/spacecowboy/NoNonsense-FilePicker/issues/24)

## v2.2.3


### Fixed

- Do not override existing arguments [\#41](https://github.com/spacecowboy/NoNonsense-FilePicker/pull/41) ([spacecowboy](https://github.com/spacecowboy))

### Misc

- Update travis config to run on faster containers [\#42](https://github.com/spacecowboy/NoNonsense-FilePicker/pull/42) ([spacecowboy](https://github.com/spacecowboy))

## v2.2.2


### Changed

- Update Dropbox library version [7090da9](https://github.com/spacecowboy/NoNonsense-FilePicker/commit/7090da920c75d4c91a09628e7cf584920d065ac0)

    And remove mention of now deprecated API.
    Fixes [\#35](https://github.com/spacecowboy/NoNonsense-FilePicker/issues/35)

### Fixed

- Ensure toasts don't get queued up [\#39](https://github.com/spacecowboy/NoNonsense-FilePicker/pull/39) ([hcoosthuizen](https://github.com/hcoosthuizen))

## v2.2.1


### Fixed

- Remove all tags from library manifest [4a6e0ae](https://github.com/spacecowboy/NoNonsense-FilePicker/commit/4a6e0ae0341cbd2fe9ee8af5577a9a739550c681)

    They are not needed in libraries and only result in
    conflicts. Fixes [\#34](https://github.com/spacecowboy/NoNonsense-FilePicker/issues/34).

### Misc

- Include link to change log in README [15c59b5](https://github.com/spacecowboy/NoNonsense-FilePicker/commit/15c59b5b0d28d48698a176b08787ffbb90829928)

    Fixes [\#33](https://github.com/spacecowboy/NoNonsense-FilePicker/issues/33)

## 2.2.0


### Added

- Fragment can now be used together with action bar [2ede72d](https://github.com/spacecowboy/NoNonsense-FilePicker/commit/2ede72dca975ed5f27da91a623df9b058d114a2e)

    Now possible to load the fragment even with existing
    toolbar, as long as setupToolbar() is overriden.

    Fixes [\#32](https://github.com/spacecowboy/NoNonsense-FilePicker/issues/32)

### Changed

- Specifying GPL3 or later [\#30](https://github.com/spacecowboy/NoNonsense-FilePicker/pull/30) ([spacecowboy](https://github.com/spacecowboy))
- Prefix resources [4124e8e](https://github.com/spacecowboy/NoNonsense-FilePicker/commit/4124e8ea20b31b2fddb01f0ef929d54106bb74d9)

    And add an overridable toolbar theme. Fixes [\#31](https://github.com/spacecowboy/NoNonsense-FilePicker/issues/31)

### Misc

- README example was slightly wrong. [5e14cb2](https://github.com/spacecowboy/NoNonsense-FilePicker/commit/5e14cb248130df26399e24c359837bd9fa2fc02a)

    Fixes [\#26](https://github.com/spacecowboy/NoNonsense-FilePicker/issues/26)
- Update LICENSE to mention or later [4f5c2de](https://github.com/spacecowboy/NoNonsense-FilePicker/commit/4f5c2de6f1a362c4198e7f3a293c017c1eeb3c92)

    Fixes [\#29](https://github.com/spacecowboy/NoNonsense-FilePicker/issues/29)

## 2.1.0


### Changed

- Make createdir depend only on CREATE_DIR argument [38f4ee2](https://github.com/spacecowboy/NoNonsense-FilePicker/commit/38f4ee21c4b727dcb20f32c82a561c22001f46a2)

    Fixes [\#25](https://github.com/spacecowboy/NoNonsense-FilePicker/issues/25)

### Misc

- Mention that start-path is configurable [\#20](https://github.com/spacecowboy/NoNonsense-FilePicker/pull/20) ([spacecowboy](https://github.com/spacecowboy))

## v2.0.5


### Fixed

- Ic_launcher conflict with app one [\#16](https://github.com/spacecowboy/NoNonsense-FilePicker/pull/16) ([NitroG42](https://github.com/NitroG42))

## v2.0.4


### Fixed

- Handle rotation correctly [9857de8](https://github.com/spacecowboy/NoNonsense-FilePicker/commit/9857de8523976105d1f666360c397c1158244eb8)

    Fixes [\#15](https://github.com/spacecowboy/NoNonsense-FilePicker/issues/15)

## v2.0.0


### Added

- Adding Dropbox Core sample [d860af7](https://github.com/spacecowboy/NoNonsense-FilePicker/commit/d860af7efe00ce241ae37ddb8bb73f8fb4a9b28c)

    Fixes [\#5](https://github.com/spacecowboy/NoNonsense-FilePicker/issues/5)

### Fixed

- NPE on empty dir [\#6](https://github.com/spacecowboy/NoNonsense-FilePicker/pull/6) ([arkty](https://github.com/arkty))

## v1.1.3


### Fixed

- Handle case if directory does not exist in FilePicker [a17d655](https://github.com/spacecowboy/NoNonsense-FilePicker/commit/a17d6552d7ec436795469e44ecb336c270072b66)

    Fixes [\#4](https://github.com/spacecowboy/NoNonsense-FilePicker/issues/4)

## v1.1.2


### Misc

- Update readme with maven info [3afc546](https://github.com/spacecowboy/NoNonsense-FilePicker/commit/3afc546037639d74eb24afe83176d090132339c8)

    Fixes [\#3](https://github.com/spacecowboy/NoNonsense-FilePicker/issues/3)

## 1.1.0


### Changed

- Picker now has more options [26b0595](https://github.com/spacecowboy/NoNonsense-FilePicker/commit/26b05955df11e1bdd6e0543b8fa4a2ec3bab23fb)

    Removed onlyDirs in favor of a mode variable. Now possible to
    select between: Files, Dirs, or Both.

    The ability to create directories is now an option as well
    which defaults to false.

    Fixes [\#1](https://github.com/spacecowboy/NoNonsense-FilePicker/issues/1), [\#2](https://github.com/spacecowboy/NoNonsense-FilePicker/issues/2)
