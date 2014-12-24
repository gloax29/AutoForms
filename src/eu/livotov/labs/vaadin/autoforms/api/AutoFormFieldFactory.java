package eu.livotov.labs.vaadin.autoforms.api;


import com.vaadin.ui.*;

import eu.livotov.labs.vaadin.autoforms.ann.DateTypeOptions;
import eu.livotov.labs.vaadin.autoforms.ann.FormField;
import eu.livotov.labs.vaadin.autoforms.ann.TextTypeOptions;
import eu.livotov.labs.vaadin.autoforms.api.validators.DateFieldValidator;

import java.util.Date;



/**
 * (c) Livotov Labs Ltd. 2012
 * Date: 28/07/2013
 * 
 * modif Gloax29 02/10/2014
 */
public class AutoFormFieldFactory
{

    public static Field<?> createFormField(java.lang.reflect.Field field, FormField metadata)
    {
        Field<?> ui = null;
        FormFieldType realType = metadata.type() == FormFieldType.Auto ? autodetectFieldType(field) : metadata.type();

        switch (realType)
        {
            case Boolean:
                ui = createBooleanField(field, metadata);
                break;

            case Text:
                ui = createTextField(field, metadata);
                break;

            case Date:
                ui = createDateField(field, metadata);
                break;

            case Decimal:
                ui = createDecimalField(field, metadata);
                break;

            case Integer:
                ui = createIntegerField(field, metadata);
                break;

            case List:
                ui = createListField(field, metadata);
                break;

            case MultiList:
                ui = createMultiSelectField(field, metadata);
                break;
                
            case Aeratext:
                ui = createAeratextSelectField(field, metadata);
                break;
                

            default:
                ui = createTextField(field, metadata);
                break;
        }

        buildFieldTitle(ui, field, metadata);

        ui.setRequired(metadata.required());
        ui.setWidth((metadata.width() != null && metadata.width().trim().isEmpty()) ? null : metadata.width());
        ui.setHeight((metadata.height() != null && metadata.height().trim().isEmpty()) ? null : metadata.height());

        if (metadata.required() && metadata.requiredFieldErrorMessage()!=null && !metadata.requiredFieldErrorMessage().trim().isEmpty())
        {
            ui.setRequiredError(metadata.requiredFieldErrorMessage());
        }

        return ui;
    }

	private static void buildFieldTitle(final Field<?> ui, final java.lang.reflect.Field field, final FormField metadata)
    {
        if (metadata.title() == null || metadata.title().toString().trim().isEmpty() || metadata.title().toString().equalsIgnoreCase("#field"))
        {
            ui.setCaption(field.getName().toLowerCase());
        } else
        {
            ui.setCaption(metadata.title().toString());
        }
    }

    public static Field<?> createBooleanField(final java.lang.reflect.Field field, final FormField metadata)
    {
        return new CheckBox();
    }

    public static Field<?> createTextField(final java.lang.reflect.Field field, final FormField metadata)
    {
    	
    	TextField ui = new TextField() ;
    	
    	TextTypeOptions textOptions = (TextTypeOptions) field.getAnnotation(TextTypeOptions.class);
    	if(textOptions!=null){
    		
//    		min() default 0;
//    		max() default 100;
//    		multiline() default false;
//    		lines() default 5;
//    		password() default false;
//    		validationRegexp() default "";
//    		validationErrorMessage() default "";
    		
    	ui.addStyleName(textOptions.addstylename()) ;
    		textOptions.min();
    		textOptions.max();
    		textOptions.multiline();
    		textOptions.lines();
    		textOptions.password();
    		textOptions.validationRegexp();
    		textOptions.validationErrorMessage();
    		
    		
    	}
  	
        return ui ;
    }

    public static Field<?> createDateField(final java.lang.reflect.Field field, final FormField metadata)
    {
        DateField ui = new DateField();

        DateTypeOptions dateOptions = (DateTypeOptions)field.getAnnotation(DateTypeOptions.class);

        if (dateOptions!=null)
        {
           ui.setResolution(dateOptions.resulution());
           ui.setDateFormat(dateOptions.format());
         
        }
        ui.setData(new Date());
        ui.removeAllValidators();
        ui.addValidator(new DateFieldValidator());

        return ui;
    }
    
   

    public static Field<?> createIntegerField(final java.lang.reflect.Field field, final FormField metadata)
    {
        return new TextField();
    }

    public static Field<?> createDecimalField(final java.lang.reflect.Field field, final FormField metadata)
    {
        return new TextField();
    }

    public static Field<?> createListField(final java.lang.reflect.Field field, final FormField metadata)
    {
        return new ListSelect();
    }

    public static Field<?> createMultiSelectField(final java.lang.reflect.Field field, final FormField metadata)
    {
        return new ComboBox();
    }
    
    
    public static Field<?> createAeratextSelectField(java.lang.reflect.Field field, FormField metadata) {
		
		return new TextArea();
	}

    public static FormFieldType autodetectFieldType(final java.lang.reflect.Field field)
    {
        if (field.getType().equals(java.util.Date.class) || field.getType().equals(java.sql.Date.class))
        {
            return FormFieldType.Date;
        }

        if (field.getType().equals(Double.class) || field.getType().equals(Double.TYPE) || field.getType().equals(Float.class) || field.getType().equals(Double.TYPE))
        {
            return FormFieldType.Decimal;
        }

        if (field.getType().equals(Boolean.class) || field.getType().equals(Double.TYPE))
        {
            return FormFieldType.Boolean;
        }

        if ((field.getType().equals(Long.class) || field.getType().equals(Long.TYPE)) && (field.getName().toLowerCase().contains("date") || field.getName().toLowerCase().contains("time") || field.getName().toLowerCase().contains("day")))
        {
            return FormFieldType.Date;
        }

        if (field.getType().equals(Integer.class) || field.getType().equals(Integer.TYPE) || field.getType().equals(Long.class) || field.getType().equals(Long.TYPE))
        {
            return FormFieldType.Integer;
        }

        return FormFieldType.Text;

   }
}


