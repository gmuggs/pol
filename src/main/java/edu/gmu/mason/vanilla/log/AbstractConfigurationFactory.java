package edu.gmu.mason.vanilla.log;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

import edu.gmu.mason.vanilla.log.ReservedLogChannels.Setting;

/**
 * General description_________________________________________________________
 * A class that assists in contructing the simulation environment
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */

public abstract class AbstractConfigurationFactory extends ConfigurationFactory {
	Configuration createConfiguration(final String name, ConfigurationBuilder<BuiltConfiguration> builder) {
        builder.setConfigurationName(name);
        builder.setStatusLevel(Level.WARN);
        builder.setShutdownTimeout(30, TimeUnit.SECONDS);
        
        AppenderComponentBuilder appenderBuilder;
        RootLoggerComponentBuilder loggerBuilder;
        
        // CONSOLE log
        appenderBuilder = builder.newAppender("Stdout", "CONSOLE").addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);
        appenderBuilder.add(builder.newLayout("PatternLayout").addAttribute("pattern", "[%p] %c %M - %msg%n"));
        builder.add(appenderBuilder);
        
		ComponentBuilder<?> comBuilder;
		
        loggerBuilder = builder.newRootLogger(Level.ALL);
        loggerBuilder.add(builder.newAppenderRef("Stdout").addAttribute("level", Level.INFO));
        
        Level[] levels = Level.values();
        Arrays.sort(levels, new Comparator<Level>() {
			@Override
			public int compare(Level o1, Level o2) {
				return o1.compareTo(o2);
			}});
        
        for(int i=0; i<levels.length; i++) {
        	Level lv = levels[i];
        	Setting params = getSetting(lv);
        	if((lv.compareTo(Level.TRACE) > 0 || lv.compareTo(Level.INFO) == 0)
        			&& lv.intLevel() != Integer.MAX_VALUE && params != null) {
        		appenderBuilder = builder.newAppender(lv.name(), params.outputType).addAttribute("fileName", params.fileName());
        		if(params.outputType.equals("File")) {
        			// nothing to do
        		} else if (params.outputType.equals("RollingFile")) {
        			appenderBuilder.addAttribute("filePattern", params.filePattern());
        			comBuilder = builder.newComponent("Policies").addComponent(builder.newComponent("SizeBasedTriggeringPolicy").addAttribute("size", params.size + " MB"));
        			appenderBuilder.addComponent(comBuilder);
        			appenderBuilder.addComponent(builder.newComponent("DefaultRolloverStrategy").addAttribute("max", "1000"));
        		}
        		appenderBuilder.addAttribute("append", "false");
				if (lv.compareTo(Level.INFO) == 0) {
					appenderBuilder.add(builder.newLayout("PatternLayout").addAttribute("pattern", "[%p] %c %M - %msg%n"));
				} else {
					appenderBuilder.add(builder.newLayout("PatternLayout").addAttribute("pattern", "%msg%n"));
					appenderBuilder.add(builder.newFilter("ThresholdFilter", Filter.Result.DENY, Filter.Result.NEUTRAL).addAttribute("Level", levels[i-1].name()));
				}
        		
        		builder.add(appenderBuilder);
        		loggerBuilder.add(builder.newAppenderRef(lv.name()).addAttribute("level", lv.name()));
        	}
        }
        
        builder.add(loggerBuilder);
        
        // Debugging purpose
//        try(FileOutputStream output = new FileOutputStream("log_settings.xml")) {
//        	builder.writeXmlConfiguration(output);
//        } catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
        return builder.build();
    }
	
	protected abstract Setting getSetting(Level level);
}
