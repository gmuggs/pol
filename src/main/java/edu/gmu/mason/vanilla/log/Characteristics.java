package edu.gmu.mason.vanilla.log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * General description_________________________________________________________
 * Characteristics annotation interface
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface Characteristics {

}
