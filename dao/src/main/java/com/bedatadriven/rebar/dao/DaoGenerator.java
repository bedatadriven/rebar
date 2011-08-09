package com.bedatadriven.rebar.dao;

import com.bedatadriven.rebar.dao.model.DaoModel;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.helpers.DefaultValidationEventHandler;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DaoGenerator {

  private Configuration cfg;
  private ValidationEventHandler validationHandler = new DefaultValidationEventHandler();


  public DaoGenerator() {
    cfg = new Configuration();
    // Specify the data source where the template files come from.
    // We are using the resource folder
    cfg.setClassForTemplateLoading(getClass(), "");
    cfg.setObjectWrapper(new DefaultObjectWrapper());
  }

  public void setValidationHandler(ValidationEventHandler validationHandler) {
    this.validationHandler = validationHandler;
  }

  public void generate(File daoSource, File outputSourceRoot) {


    DaoModel model = parseModel(daoSource);
    processTemplate(model,
      "SyncDAO.java.ftl",
      model.computeSourcePath(outputSourceRoot, "server.dao." + model.getName() + "SyncDAO"));
  }

  private void processTemplate(DaoModel model, String templateName, String outputFile) {
    Template temp;
    try {
      temp = cfg.getTemplate(templateName);
    } catch (IOException e) {
      throw new RuntimeException("Could not load template '" + templateName + "'", e);
    }
    try {
      temp.process(model, new FileWriter(outputFile));
    } catch (Exception e) {
      throw new RuntimeException("Exception processing template '" + templateName + "'", e);
    }
  }


  private DaoModel parseModel(File file) {
    try {
      JAXBContext jc = JAXBContext.newInstance(DaoModel.class);
      Unmarshaller unmarshaller = jc.createUnmarshaller();
      unmarshaller.setEventHandler(validationHandler);
      return (DaoModel) unmarshaller.unmarshal(file);
    } catch (JAXBException e) {
      throw new RuntimeException("Could not parse DAO source file '" + file.getAbsolutePath() + "'", e);
    }
  }


}
