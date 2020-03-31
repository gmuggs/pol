package edu.gmu.mason.vanilla.log;

import java.net.URI;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Order;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.config.plugins.Plugin;

import edu.gmu.mason.vanilla.log.ReservedLogChannels.Setting;

/**
 * General description_________________________________________________________
 * Configuration for logging.
 * In order to execute, choose one of two options 
 * 1. DO maven build. 
 * 2. Add -Dlog4j2.configurationFactory=edu.gmu.mason.vanilla.log.CustomConfigurationFactory
 * 
 * In order to extend log channels, you can simply inherit this class and override getSetting() method.
 *
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 *
 */
@Plugin(name = "CustomConfigurationFactory", category = ConfigurationFactory.CATEGORY)
@Order(50)
public class CustomConfigurationFactory extends AbstractConfigurationFactory {
    @Override
    public Configuration getConfiguration(final LoggerContext loggerContext, final ConfigurationSource source) {
        return getConfiguration(loggerContext, source.toString(), null);
    }

    @Override
    public Configuration getConfiguration(final LoggerContext loggerContext, final String name, final URI configLocation) {
        ConfigurationBuilder<BuiltConfiguration> builder = newConfigurationBuilder();
        return createConfiguration(name, builder);
    }

    @Override
    protected String[] getSupportedTypes() {
        return new String[] {"*"};
    }

	@Override
	protected Setting getSetting(Level level) {
		return ReservedLogChannels.get(level);
	}
}
