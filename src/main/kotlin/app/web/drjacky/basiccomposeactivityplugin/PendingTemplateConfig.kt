package app.web.drjacky.basiccomposeactivityplugin

/**
 * Passes template parameters from the wizard UI to the [BasicComposeProjectSetupActivity].
 *
 * The normal recipe pipeline may never execute because Android Studio's internal
 * `generateAndroidModule` can fail before our template recipe runs.  By storing a
 * lazy reference to the package-name parameter we can still retrieve the user's
 * input from the post-startup activity that finishes project generation.
 */
object PendingTemplateConfig {

    @Volatile
    private var packageNameProvider: (() -> String)? = null

    @Volatile
    private var storedTimestamp: Long = 0L

    fun store(provider: () -> String) {
        packageNameProvider = provider
        storedTimestamp = System.currentTimeMillis()
    }

    /**
     * Returns the stored package name if it was stored within the last two minutes,
     * then clears the stored state.  Returns `null` if nothing is pending or
     * the stored data is stale.
     */
    fun consumeIfRecent(): String? {
        val provider = packageNameProvider ?: return null
        val ageMs = System.currentTimeMillis() - storedTimestamp
        if (ageMs > 120_000) {
            clear()
            return null
        }
        val value = provider()
        clear()
        return value.takeIf { it.isNotBlank() }
    }

    fun clear() {
        packageNameProvider = null
        storedTimestamp = 0L
    }
}
