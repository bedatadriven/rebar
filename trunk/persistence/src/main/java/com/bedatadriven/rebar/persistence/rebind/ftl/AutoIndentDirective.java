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

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

/**
 * @author Alex Bertram
 */
public class AutoIndentDirective implements TemplateDirectiveModel {


  public void execute(Environment env,
                      Map params, TemplateModel[] loopVars,
                      TemplateDirectiveBody body) throws TemplateException, IOException {

    StringWriter inWriter = new StringWriter();
    body.render(inWriter);

    int depth = 0;

    StringBuffer buffer = new StringBuffer();

    boolean nextLineIsIdented = false;
    boolean continuation = false;
    boolean inMultiLineComment = false;

    BufferedReader lineReader = new BufferedReader(new StringReader(inWriter.getBuffer().toString()));
    while(lineReader.ready()) {

      String line = lineReader.readLine();
      if(line == null)
        break;
      line = line.trim();

      if(line.length() == 0) {
        buffer.append("\n");
        continue;
      }

      if(line.startsWith("}"))
        depth --;

      if(nextLineIsIdented)
        depth ++;

      for(int i=0;i<depth;++i) {
        buffer.append("  ");
      }
      if(continuation)
        buffer.append("    ");

      if(inMultiLineComment && line.startsWith("*"))
        buffer.append(" ");

      buffer.append(line).append("\n");

      if(nextLineIsIdented) {
        depth --;
        nextLineIsIdented = false;
      }

      if(line.startsWith("/*"))
        inMultiLineComment = true;

      String statement = "";
      if(!inMultiLineComment)
        statement = stripComments(line);

      continuation = false;
      if(statement.endsWith("{")) {
        depth++;
      } else if((statement.startsWith("if") || statement.startsWith("else"))) {
        if(!statement.endsWith(";") && !statement.endsWith("}")) {
          nextLineIsIdented = true;
        }
      } else if(
          !inMultiLineComment &&
           statement.length()>0 &&
          !statement.endsWith(";") && 
          !statement.endsWith("}") &&
          !statement.startsWith("@")) {
        continuation = true;
      }

      if(line.endsWith("*/"))
         inMultiLineComment = false;

    }

    env.getOut().write(buffer.toString());
  }



  private String stripComments(String line) {
    int commentStart = line.indexOf("//");
    if(commentStart != -1)
      return line.substring(0, commentStart).trim();

    commentStart = line.indexOf("/*");
    if(commentStart != -1)
      return line.substring(0, commentStart).trim();

    return line.trim();
  }

}
