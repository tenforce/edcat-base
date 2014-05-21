package eu.lod2.hooks.handlers;

import eu.lod2.hooks.constraints.Priority;

import java.util.Collection;

/**
 * Superclass interface of all hook handlers.
 *
 * There are two ways to implement a hook:
 * - required hook: If you require the hook to exist you should implement the
 *   hook-specific interface supplied by the hook's implementer.  This approach
 *   makes your code dependent on the hook and will ensure your hook is loaded.
 * - optional hook: In case you want to supply code for a hook, assuming the hook
 *   exists (but don't mind if the hook doesn't exist), you can implement the
 *   {@link eu.lod2.hooks.handlers.OptionalHookHandler} interface instead.  This
 *   allows you to plugins without knowing about their existence, but it drops
 *   static checking and is generally less clean to implement.
 *
 * You MUST also add a line containing only the fully qualified name of the class in
 * which you implemented the hook in to the following file in your project:
 * resources/META-INF/services/eu.lod2.hooks.handlers.HookHandler
 */
@SuppressWarnings( { "UnusedDeclaration" } )
public interface HookHandler {

    /**
     * Return all constraints which apply to the execution of the supplied hook.
     *
     * Constraints are instances of Priority, but con most easily be constructed
     * through the static methods of {@link eu.lod2.hooks.constraints.Constraint}.
     * If you have no explicit Constraints, you may return null.
     *
     * @param hook Canonical name of the hook for which we want to know the constraints.
     *             The name of the hook should be the Canonical name of the interface
     *             describing the specific implementation of the hook.
     * @return A collection of Priority objects, or null if there are no constraints.
     */
    public Collection<Priority> getConstraints(String hook);
}