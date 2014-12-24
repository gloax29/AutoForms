package eu.livotov.labs.vaadin.autoforms.ann;

import eu.livotov.labs.vaadin.autoforms.api.FormFieldType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * (c) Livotov Labs Ltd. 2012
 * Date: 02.09.12
 *
 * Placeholders, allowed in string attributes:
 *
 * #field - name of the corresponding bean field, this annotation is bound to
 * @{fieldName} - value of the specified bean field, for instance: @name, @address, @zipCode. If specified field is
 * a class itself, you can specify nested properties via "." separator: @address.city , etc... If not specified,
 * nested class toString method value will be used.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FormField
{

	public abstract String title() default "#field";
    public abstract String hint() default "";
    public abstract String description() default "";
    public abstract boolean required() default false;
    /**
     * sintaxe location="colum,row" or "colum,row, number colum , number row "
     * @return
     */
    public abstract String location();
    public abstract String width() default "100%";
    public abstract String height() default "";
    public abstract boolean visible() default true;
    public abstract FormFieldType type() default FormFieldType.Auto;
    public abstract String requiredFieldErrorMessage() default "";
    public abstract boolean immediate() default true;


}
