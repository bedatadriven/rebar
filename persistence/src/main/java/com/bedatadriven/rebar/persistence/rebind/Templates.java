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

package com.bedatadriven.rebar.persistence.rebind;

import com.bedatadriven.rebar.persistence.rebind.ftl.SingleLineDirective;
import com.bedatadriven.rebar.persistence.rebind.ftl.CsvDirective;
import com.bedatadriven.rebar.persistence.rebind.ftl.AutoIndentDirective;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;

/**
 * @author Alex Bertram
 */
public class Templates {

  public static Configuration get() {
    Configuration cfg = new Configuration();
    // Specify the data source where the template files come from.
    cfg.setClassForTemplateLoading(Templates.class, "/persistence");
    cfg.setDefaultEncoding("UTF-8");

    // Specify how templates will see the data-model. This is an advanced topic...
    // but just use this:
    cfg.setObjectWrapper(new DefaultObjectWrapper());

    cfg.setSharedVariable("singleline", new SingleLineDirective());
    cfg.setSharedVariable("csv", new CsvDirective());
    cfg.setSharedVariable("autoindent", new AutoIndentDirective());

    return cfg;
  }
}
