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

package com.bedatadriven.gears.persistence.rebind.ftl;

import freemarker.core.Environment;
import freemarker.template.*;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * @author Alex Bertram
 */
public class SingleLineDirective implements TemplateDirectiveModel {


  public void execute(Environment env,
                      Map params, TemplateModel[] loopVars,
                      TemplateDirectiveBody body)
      throws TemplateException, IOException {
    // Check if no parameters were given:
    if (!params.isEmpty()) {
      throw new TemplateModelException(
          "This directive doesn't allow parameters.");
    }
    if (loopVars.length != 0) {
      throw new TemplateModelException(
          "This directive doesn't allow loop variables.");
    }

    // If there is non-empty nested content:
    if (body != null) {
      // Executes the nested body. Same as <#nested> in FTL, except
      // that we use our own writer instead of the current output writer.
      body.render(new UpperCaseFilterWriter(env.getOut()));
    } else {
      throw new RuntimeException("missing body");
    }
  }

  /**
   * A {@link Writer} that transforms the character stream to upper case
   * and forwards it to another {@link Writer}.
   */
  private static class UpperCaseFilterWriter extends Writer {

    private final Writer inner;

    UpperCaseFilterWriter(Writer inner) throws IOException {
      this.inner = inner;
    }

    public void write(char[] cbuf, int off, int len)
        throws IOException {
      char[] transformedCbuf = new char[len];
      int i = 0, j = 0;
      boolean lastWasWhitespace = false;
      while (i < len) {
        char c = cbuf[(off + i)];
        if (c == 10 || c == 13)
          c = ' ';
        if (!Character.isWhitespace(c) || !lastWasWhitespace)
          transformedCbuf[j++] = c;

        lastWasWhitespace = Character.isWhitespace(c);
        i++;
      }
      inner.write(transformedCbuf, 0, j);
    }

    public void flush() throws IOException {
      inner.flush();
    }

    public void close() throws IOException {
      inner.close();
    }
  }

}
