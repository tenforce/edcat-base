package eu.lod2.hooks.handlers;

/**
 * By implementing the OptionalHookHandler you can require to be called if a hook is called,
 * without requiring the hook to actually exist.
 * <p/>
 * This approach removes type checking and is generally less clean than implementing a specific
 * interface for a hook, yet it allows you to hook into functionality without requiring the
 * hook to be loaded.  Hence, we optionally add code to the hook.
 */
@SuppressWarnings( { "UnusedDeclaration" } )
public interface OptionalHookHandler extends HookHandler {

    /**
     * Indicates that we are handling the specified Hook.
     *
     * @param hook Canonical name of the hook of which we want to know if we implement it.
     *             this is the canonical name of the interface which you would normally implement.
     * @return true iff we implement the specified hook.
     */
    public boolean isHandlingHook(String hook);

    /**
     * Functionality to use when the specified Hook is being called.
     *
     * @param hook Canonical name of the hook which we want to implement.
     * @param args array of arguments supplied by the hook.  In most cases these should be the same
     *             as those supplied to the functions of the dedicated interface, yet they may be
     *             different at the Hook implementer's discretion.
     */
    public void handle(String hook, Object... args);
}
