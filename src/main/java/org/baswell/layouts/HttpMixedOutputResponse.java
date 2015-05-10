/*
 * Copyright 2015 Corey Baswell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.baswell.layouts;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/*
 * A HttpServletResponse that backs the PrintWriter with the ServletOutputStream. This allows the binary content of the view to be written to the output stream
 * and the JSP content of the layout to be written to the PrintWriter.
 */
class HttpMixedOutputResponse extends HttpServletResponseWrapper
{
  private final OutputStream outputStream;

  private PrintWriter printWriter;

  HttpMixedOutputResponse(HttpServletResponse response) throws IOException
  {
    super(response);
    outputStream = response.getOutputStream();
  }

  @Override
  public PrintWriter getWriter()
  {
    if (printWriter == null)
    {
      printWriter = new PrintWriter(new OutputStreamWriter(outputStream));
    }

    return printWriter;
  }
}
