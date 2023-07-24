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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import static org.baswell.layouts.Layouts.*;
import static org.baswell.layouts.SharedMethods.*;

class HttpBufferedResponse extends HttpServletResponseWrapper
{
  private final HttpServletRequest request;

  private ByteArrayOutputStream buffer;

  private PrintWriter printWriter;

  private ServletOutputStream outputStream;

  private boolean nonHtmlContent;

  private Integer contentLength;

  public HttpBufferedResponse(HttpServletRequest request, HttpServletResponse response)
  {
    super(response);
    this.request = request;
  }

  boolean hasBufferedContent()
  {
    return buffer != null && buffer.size() > 0;
  }

  boolean isHtmlContent()
  {
    return !nonHtmlContent;
  }

  byte[] getContent()
  {
    if (buffer != null)
    {
      if (printWriter != null)
      {
        printWriter.flush();
      }

      return buffer.toByteArray();
    }
    else
    {
      return null;
    }
  }

  void pushContent() throws IOException
  {
    if (buffer != null)
    {
      if (contentLength != null)
      {
        super.setContentLength(contentLength);
      }

      /*
       * If a PrintWriter is being used make sure all bytes have been pushed to our buffer
       */
      if (printWriter != null)
      {
        printWriter.flush();
      }

      super.getOutputStream().write(getContent());
    }
  }

  @Override
  public void setContentLength(int contentLength)
  {
    /*
     * If this request ends up getting a layout the actual Content-Length returned to the client will be different.
     */
    this.contentLength = contentLength;
  }

  @Override
  public void setContentType(String contentType)
  {
    super.setContentType(contentType);
    nonHtmlContent = !SharedMethods.isHtmlContent(contentType);
  }

  @Override
  public PrintWriter getWriter() throws IOException
  {
    if (printWriter == null)
    {
      if (inNonBufferState())
      {
        printWriter = super.getWriter();
      }
      else
      {
        if (buffer == null)
        {
          buffer = new ByteArrayOutputStream();
        }
        printWriter = new PrintWriter(new OutputStreamWriter(buffer));
      }
    }
    
    return printWriter;
  }
  
  @Override
  public ServletOutputStream getOutputStream() throws IOException
  {
    if (outputStream == null)
    {
      if (inNonBufferState())
      {
        outputStream = super.getOutputStream();
      }
      else
      {
        buffer = new ByteArrayOutputStream();
        outputStream = new LayoutsOutputStream(buffer);
      }
    }
    return outputStream;
  }

  private boolean inNonBufferState()
  {
    return nonHtmlContent || trueValue(request.getAttribute(NO_LAYOUT));
  }

  private static class LayoutsOutputStream extends ServletOutputStream
  {
    private OutputStream outStream;
    
    public LayoutsOutputStream(OutputStream outStream)
    {
      this.outStream = outStream;
    }

    @Override
    public void write(int b) throws IOException
    {
      outStream.write(b);
    }

    @Override
    public void write(byte[] buffer) throws IOException
    {
      outStream.write(buffer);
    }

    @Override
    public void write(byte[] buffer, int offset, int length) throws IOException
    {
      outStream.write(buffer, offset, length);
    }

    @Override
    public boolean isReady() {
      return true;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {}
  }
}
