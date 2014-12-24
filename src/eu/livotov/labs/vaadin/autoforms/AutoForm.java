package eu.livotov.labs.vaadin.autoforms;


import java.io.File;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;

import eu.livotov.labs.vaadin.autoforms.ann.FormBean;
import eu.livotov.labs.vaadin.autoforms.ann.FormField;
import eu.livotov.labs.vaadin.autoforms.api.AutoFormFieldFactory;
import eu.livotov.labs.vaadin.autoforms.api.CellConstraint;
import eu.livotov.labs.vaadin.autoforms.api.ForUpload;
import eu.livotov.labs.vaadin.autoforms.api.FormFieldType;

/**
 * (c) Livotov Labs Ltd. 2012 Date: 27/07/2013
 * modif Gloax29 02/10/2014
 * 
 */

public class AutoForm extends VerticalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6455338750075085583L;

	private BeanFieldGroup<?> formController;

	private HorizontalLayout uiButtonsRoot;
	private GridLayout uiFormRoot;
	private Label uiTitle, uiHeader, uiFooter;
	private Button uiBtnCommit, uiBtnCancel;
	private Upload upload;

	private Class<?> formType;
	private FormBean formMetadata;
	private Button.ClickListener uiBtListenerCommit, uiBtListenerCancel;

	private Object formData;

	private boolean internUpload;
	private  ForUpload makeupload ;
	private File fileUpload ;

	private String captionUpload;

	private boolean immediat = true;
	public AutoForm(Object formBean) {
		this();
		setFormData(formBean);
	}

	public AutoForm() {
		super();
		buildRootLayout();
	}

	private void assembleFormCompoments() {
		if (uiTitle != null) {
			addComponent(uiTitle);
		}

		if (uiHeader != null) {
			addComponent(uiHeader);
		}

		addComponent(uiFormRoot);

		if (uiFooter != null) {
			addComponent(uiFooter);
		}
		
		if (upload != null) {
			addComponent(upload);
		}

		if (uiButtonsRoot != null) {
			addComponent(uiButtonsRoot);
		}
		

		setExpandRatio(uiFormRoot, 1.0f);
	}

	private void buildRootLayout() {
		setSizeFull();
		setSpacing(true);
	}

	private void preflightFormAnnotations() {
		if (formMetadata == null) {
			throw new IllegalArgumentException(
					String.format(
							"Specified formData class is not annotated by a @Form annotation: %s",
							formType.getSimpleName()));
		}

		if (formMetadata.rows() <= 0 || formMetadata.columns() <= 0) {
			throw new IllegalArgumentException(
					"Your @Form annotation must specify non-zero positive bumber of rows and columns for a layout.");
		}
	}

	private void buildFormHeaders() {
		if (formMetadata.title() != null
				&& !formMetadata.title().trim().isEmpty()) {
			uiTitle = new Label(translateTextItem(formMetadata.title()));
		}

		if (formMetadata.header() != null
				&& !formMetadata.header().trim().isEmpty()) {
			uiHeader = new Label(translateTextItem(formMetadata.header()));
		}

		if (formMetadata.footer() != null
				&& !formMetadata.footer().trim().isEmpty()) {
			uiFooter = new Label(translateTextItem(formMetadata.footer()));
		}
	}

	/**
	 * 
	 */
	private void buildFormCommitButtons() {
		if (formMetadata.commitButtonVisible()) {
			uiBtnCommit = new Button(
					(formMetadata.commitButtonTitle() != null && !formMetadata
							.commitButtonTitle().trim().isEmpty()) ? translateTextItem(formMetadata
							.commitButtonTitle()) : translateTextItem("Save"));
			if (uiBtListenerCommit != null) {

				uiBtnCommit.addClickListener(uiBtListenerCommit);
			} else {
				uiBtnCommit.addClickListener(new Button.ClickListener() {
					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					public void buttonClick(final Button.ClickEvent event) {
						try {
							commit();
						} catch (FieldGroup.CommitException e) {
							e.printStackTrace();
							new RuntimeException(e);
						}
					}
				});
			}
		}
			if (formMetadata.cancelButtonVisible()) {
				uiBtnCancel = new Button(
						(formMetadata.cancelButtonTitle() != null && !formMetadata
								.cancelButtonTitle().trim().isEmpty()) ? translateTextItem(formMetadata
								.cancelButtonTitle())
								: translateTextItem("Cancel"));
				if (uiBtListenerCancel != null) {

					uiBtnCancel.addClickListener(uiBtListenerCancel);
				} else {
					uiBtnCancel.addClickListener(new Button.ClickListener() {
						/**
						 * 
						 */
						private static final long serialVersionUID = 1L;

						public void buttonClick(final Button.ClickEvent event) {
							reset();
						}
					});
				}
			}

			if (uiBtnCancel != null || uiBtnCommit != null) {
				uiButtonsRoot = new HorizontalLayout();
				uiButtonsRoot.setWidth("100%");
				uiButtonsRoot.setHeight(null);
				uiButtonsRoot.setSpacing(true);

				Label sizer = new Label();
				sizer.setWidth("100%");
				sizer.setHeight(null);

				uiButtonsRoot.addComponent(sizer);

				if (uiBtnCommit != null) {
					uiButtonsRoot.addComponent(uiBtnCommit);
				}

				if (uiBtnCancel != null) {
					uiButtonsRoot.addComponent(uiBtnCancel);
				}

				uiButtonsRoot.setExpandRatio(sizer, 1.0f);
			}
		
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void buldFormUI() {
		this.formController = new BeanFieldGroup( this.formType);
		this.formController.setBuffered(true);

		this.uiFormRoot = new GridLayout(this.formMetadata.columns(), this.formMetadata.rows());
		this.uiFormRoot.setWidth("100%");
		this.uiFormRoot.setHeight(null);
		this.uiFormRoot.setSpacing(true);

		java.lang.reflect.Field[] classFields = this.formType.getDeclaredFields();
		
		
		for (java.lang.reflect.Field beanField : classFields) {
			
		
				
				buildSingleField(beanField);
				
	
		}
	}

	private void buildSingleField(final java.lang.reflect.Field beanField) {
		FormField metadata = (FormField) beanField
				.getAnnotation(FormField.class);

		if (metadata != null) {
			
			if (FormFieldType.component.equals(metadata.type())) {
				beanField.setAccessible(true) ;
				Component ui = null;
				try {
	
						ui = (Component) beanField.get(this.formData);
					
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
				CellConstraint constraint = new CellConstraint(metadata.location());
					if (constraint.getStartCell() == constraint.getEndCell()) {
						this.uiFormRoot.addComponent(ui, constraint.getStartCell(),
							constraint.getStartRow());
				} else {
					ui.setWidth("100%");
					if (constraint.getStartRow() != constraint.getEndRow()) {
						ui.setHeight("100%");
					}
					ui.setCaption(metadata.title());
					this.uiFormRoot.addComponent(ui, constraint.getStartCell(),
							constraint.getStartRow(), constraint.getEndCell(),
							constraint.getEndRow());
				}
			}else{
			CellConstraint constraint = new CellConstraint(metadata.location());
			
			Field<?> uiField = AutoFormFieldFactory.createFormField(beanField,
					metadata);

			if (constraint.getStartCell() == constraint.getEndCell()) {
				this.uiFormRoot.addComponent(uiField, constraint.getStartCell(),
						constraint.getStartRow());
			} else {
				uiField.setWidth("100%");
				if (constraint.getStartRow() != constraint.getEndRow()) {
					uiField.setHeight("100%");
				}
				this.uiFormRoot.addComponent(uiField, constraint.getStartCell(),
						constraint.getStartRow(), constraint.getEndCell(),
						constraint.getEndRow());
			}

			this.formController.bind(uiField, beanField.getName());
		}
		}
	}
	
		
	
	

	private String translateTextItem(final String text) {
		return text;
	}

	public void setFormData(final Object formBean) {
		if (formBean == null) {
			throw new IllegalArgumentException(
					"You cannot set NULL beans here.");
		}

		this.formData = formBean;

		if (this.formType == null || !this.formType.equals(formBean.getClass())) {
			this.formType = formBean.getClass();
			initializeFormWithNewBeantype();
		}

		this.formController.setItemDataSource((Item) this.formData);
	}

	private void initializeFormWithNewBeantype() {
		this.formMetadata = (FormBean) this.formType.getAnnotation(FormBean.class);

		removeAllComponents();

		this.uiTitle = null;
		this.uiHeader = null;
		this.uiFooter = null;
		this.uiBtnCancel = null;
		this.uiBtnCommit = null;
		this.upload = null;

		if (this.uiFormRoot != null) {
			this.uiFormRoot.removeAllComponents();
			this.uiFormRoot = null;
		}
		if(this.internUpload){
	prefUploadInt();
		}
		preflightFormAnnotations();
		buildFormHeaders();
		buildFormCommitButtons();
		buldFormUI();
		assembleFormCompoments();
	}

	private void prefUploadInt() {
		
		this.makeupload = new ForUpload(this.fileUpload);
		this.upload = new Upload(this.captionUpload,this. makeupload );
		this.upload.setImmediate(this.immediat);
		
	}

	public Object getFormData() {
		return this.formData;
	}

	public Object commit() throws FieldGroup.CommitException {
		this.formController.commit();
		return getFormData();
	}
	
	/**
	 * return the  file
	 * 
	 * @return File
	 */
	public File getfileUpload(){
		return this.makeupload.getFile() ;
	}

	public void reset() {
		this.formController.discard();
	}

	/**
	 * @return the uiBtListenerCommit
	 */
	public Button.ClickListener getUiBtListenerCommit() {
		return this.uiBtListenerCommit;
	}

	/**
	 * @param uiBtListenerCommit
	 *            the uiBtListenerCommit to set
	 */
	public void setUiBtListenerCommit(Button.ClickListener uiBtListenerCommit) {
		this.uiBtListenerCommit = uiBtListenerCommit;
	}

	/**
	 * @return the uiBtListenerCancel
	 */
	public Button.ClickListener getUiBtListenerCancel() {
		return uiBtListenerCancel;
	}

	/**
	 * @param uiBtListenerCancel
	 *            the uiBtListenerCancel to set
	 */
	public void setUiBtListenerCancel(Button.ClickListener uiBtListenerCancel) {
		this.uiBtListenerCancel = uiBtListenerCancel;
	}

	/**
	 * @return the uploads
	 */
	public Upload getUploads() {
		return upload;
	}

	/**
	 * @param uploads
	 *            the uploads to set
	 */
	public void setUploads(Upload upload) {
		this.upload = upload;
	}


	/**
	 * if use upload, name and download Imediat
	 * 
	 * @param internUpload
	 * @param captionUpload
	 * @param immediat
	 */
	public void setInternUpload(boolean internUpload,String captionUpload,boolean immediat) {
		this.internUpload =internUpload;
		this.captionUpload =captionUpload ;
		this.immediat =immediat;
		
	}

}
