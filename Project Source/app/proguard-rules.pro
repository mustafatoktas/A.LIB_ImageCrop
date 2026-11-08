# R8 rules for the sample app. Libraries (Compose, Hilt, Room, Coil) ship their own keep rules,
# so only size and release-output tweaks live here.

# Move all obfuscated classes into one package: shorter names, smaller dex.
-repackageclasses

# The view model prints debug messages; release builds drop them.
-assumenosideeffects class java.io.PrintStream {
    public void println(...);
}
