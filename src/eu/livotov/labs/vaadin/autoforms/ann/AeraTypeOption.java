/**
 * 
 */
package eu.livotov.labs.vaadin.autoforms.ann;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Gloax29
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AeraTypeOption {
	
	 public abstract String  value() default "";

}
