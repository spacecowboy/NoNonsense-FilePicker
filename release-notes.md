# 2.1

- Now easier to override sort-order and filtering in built in SD-card picker
  with new methods: compareFiles, and isItemVisible.
- SD-card picker: Correctly refuse to browse above the root-path.
- Allow creation of directories even when picking files.
- Upgrade to newest support-library versions and target android 5.1.

# 2.0.5
Remove unnecessary and conflicting ic_launcher icon.

# 2.0.4
Handle device rotation, fixes #15.

# 2.0.3
Fix a crash because of fauly view initiation.
Item layout's imageview now has fixed size.
New sample activity which shows thumbnails for image-files.

# 2.0.2
Fixes some lint warnings, and updates build scripts.

# 2.0.1
Material design refresh. Some breaking API changes introduced.

# 1.2.0
Fixes a crash on browsing to a directory where the user has no read-permission.
