package eu.lod2.hooks.handlers.dcat.distribution;

import eu.lod2.hooks.handlers.HookHandler;
import org.springframework.stereotype.Service;

/**
 * Implement if you are a provider for the AtCreateHook for Distributions.
 * <p/>
 * Implementing this interface requires the hook to exist.  If you don't want to depend
 * on the hook being loaded, check out {@link eu.lod2.hooks.handlers.OptionalHookHandler}.
 * The supplied {@code args} are the same as the ones specified in this interface.
 * The name for this hook is {@code "eu.lod2.hooks.handlers.dcat.distribution.AtCreateHandler"}.
 */
@Service("DistributionAtCreateHandler")
public interface AtCreateHandler extends HookHandler{
}
