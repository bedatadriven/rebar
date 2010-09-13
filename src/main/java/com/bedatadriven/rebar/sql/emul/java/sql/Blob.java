

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

package java.sql;

import java.io.InputStream;
import java.io.OutputStream;

public interface Blob {

  long length() throws SQLException;

  byte[] getBytes(long pos, int length) throws SQLException;

  InputStream getBinaryStream() throws SQLException;

  long position(byte pattern[], long start) throws SQLException;

  long position(Blob pattern, long start) throws SQLException;

  int setBytes(long pos, byte[] bytes) throws SQLException;

  int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException;

  OutputStream setBinaryStream(long pos) throws SQLException;

  void truncate(long len) throws SQLException;

  void free() throws SQLException;

  InputStream getBinaryStream(long pos, long length) throws SQLException;
}


