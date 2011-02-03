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

package com.bedatadriven.rebar.appcache.linker;

import com.google.gwt.core.ext.UnableToCompleteException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class Html5ManifestWriterTest {

  @Test
  public void escape() throws UnableToCompleteException {
    assertThat(Html5ManifestWriter.escape(null, "path/image.png"), equalTo("path/image.png"));
    assertThat(Html5ManifestWriter.escape(null, "path/image with space.png"), equalTo("path/image%20with%20space.png"));
    assertThat(Html5ManifestWriter.escape(null, "path/élite.png"), equalTo("path/%E9lite.png"));

  }

}
