/*
 * Copyright 2009-2010 BeDataDriven (alex@bedatadriven.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.

 */

package com.bedatadriven.rebar.persistence.rebind.ftl;

import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.core.Environment;

import java.util.Map;
import java.io.IOException;
import java.io.StringWriter;

/**
 * FreeMarker template directive that removes the last comma from a list,
 * if the comma is present.
 *
 * @author Alex Bertram
 */
public class CsvDirective implements TemplateDirectiveModel {

  public void execute(Environment environment,
                      Map map,
                      TemplateModel[] templateModels,
                      TemplateDirectiveBody body)
      throws TemplateException, IOException {


    StringWriter writer = new StringWriter();
    body.render(writer);

    String s = writer.toString().trim();
    if(s.endsWith(",")) {
      s = s.substring(0, s.length()-1);
    }

    environment.getOut().write(s);

  }
}
