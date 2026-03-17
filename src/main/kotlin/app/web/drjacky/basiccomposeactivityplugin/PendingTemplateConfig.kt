package app.web.drjacky.basiccomposeactivityplugin

/**
 * Passes template parameters from the wizard UI to [BasicComposeProjectSetupActivity].
 *
 * A lazy reference to the package-name parameter is stored when the template
 * builder runs, so we can retrieve the user's input from the post-startup
 * activity that finishes project generation.
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
