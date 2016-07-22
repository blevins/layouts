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

import javax.servlet.ServletResponse;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.io.OutputStream;

/**
 * <p>
 * The content of the view. This object can be accessed in layouts using the {@code HttpServletRequest} attribute {@link Layouts#VIEW}. The Expression
 * Language syntax for this is:
 * </p>
 *
 * <code>
 * ${view.yield(pageContext)}
 * </code>
 */
public class View
{
  private final byte[] content;

  private final ServletResponse response;

  public View(byte[] content, ServletResponse response)
  {
    this.content = content;
    this.response = response;
  }

  /**
   * <p>
   * Yield the content of the <head> tag. Same as:
   * </p>
   *
   * <code>
   * yield("head", pageContext)
   * </code>
   *
   * @param pageContext The PageContext of the layout JSP file.
   * @throws java.io.IOException
   */
  public void yieldHead(PageContext pageContext) throws IOException
  {
    yield("head", pageContext);
  }

  /**
   * <p>
   * Yield the content of the <body> tag. Same as:
   * </p>
   *
   * <code>
   * yield("body", pageContext)
   * </code>
   *
   * @param pageContext The PageContext of the layout JSP file.
   * @throws java.io.IOException
   */
  public void yieldBody(PageContext pageContext) throws IOException
  {
    yield("body", pageContext);
  }

  /**
   * <p>
   * Yield the content of the <footer> tag. Same as:
   * </p>
   *
   * <code>
   * yield("footer", pageContext)
   * </code>
   *
   * @param pageContext The PageContext of the layout JSP file.
   * @throws java.io.IOException
   */
  public void yieldFooter(PageContext pageContext) throws IOException
  {
    yield("footer", pageContext);
  }

  /**
   * Yield the entire content of the view.
   *
   * @param pageContext The PageContext of the layout JSP file.
   * @throws java.io.IOException
   */
  public void yield(PageContext pageContext) throws IOException
  {
    pageContext.getOut().flush();
    response.getOutputStream().write(content);
  }

  /**
   * Yield the content of the outermost tag <tagName>.
   *
   * @param tagName The name of the outermost tag to yield the content of.
   * @param pageContext The PageContext of the layout JSP file.
   * @throws java.io.IOException
   */
  public void yield(String tagName, PageContext pageContext) throws IOException
  {
    /*
     * TODO JSPs using a different encoding then the system default will break here if the values for the characters in the opening
     * or closing tags are different. Not sure how to grab the encoding from the JSP that created the current content automatically.
     * Maybe add a filter parameter to specify the encoding if it's different then default.
     */
    byte[] tagNameBytes = tagName.getBytes();
    byte[] openTagBytes = new byte[tagNameBytes.length + 2];
    openTagBytes[0] = LESS_THAN;
    for (int i = 0; i < tagNameBytes.length; i++)
    {
      openTagBytes[i + 1] = tagNameBytes[i];
    }
    openTagBytes[openTagBytes.length - 1] = GREATER_THAN;

    int openTagIndex = indexOf(content, openTagBytes);
    if (openTagIndex >= 0)
    {
      byte[] closedTagBytes = new byte[tagNameBytes.length + 3];
      closedTagBytes[0] = LESS_THAN;
      closedTagBytes[1] = SOLIDUS;
      for (int i = 0; i < tagNameBytes.length; i++)
      {
        closedTagBytes[i + 2] = tagNameBytes[i];
      }
      closedTagBytes[closedTagBytes.length - 1] = GREATER_THAN;

      int closeTagIndex = lastIndexOf(content, closedTagBytes);
      if (closeTagIndex > openTagIndex)
      {
        int startIndex = openTagIndex + openTagBytes.length;
        int length = closeTagIndex - startIndex;
        /*
         * We're mixing the JSPWriter and the ServletOuptStream here because we don't want to take the hit to turn
         * content back into a String. Need to make sure everything written to JSPWriter to this point is flushed so
         * the content doesn't getting out of order.
         */
        pageContext.getOut().flush();
        OutputStream out = response.getOutputStream();
        out.write(content, startIndex, length);
        out.flush();
      }
    }
  }

  /**
   * Is the tag name defined in the current content?.
   *
   * @param tagName The name of the outermost tag to yield the content of.
   * @param pageContext The PageContext of the layout JSP file.
   * @return <code>true</code> if the given tag name is found in the current page content.
   * @throws java.io.IOException
   */
  public boolean contains(String tagName, PageContext pageContext) throws IOException
  {
    /*
     * TODO JSPs using a different encoding then the system default will break here if the values for the characters in the opening
     * or closing tags are different. Not sure how to grab the encoding from the JSP that created the current content automatically.
     * Maybe add a filter parameter to specify the encoding if it's different then default.
     */
    byte[] tagNameBytes = tagName.getBytes();
    byte[] openTagBytes = new byte[tagNameBytes.length + 2];
    openTagBytes[0] = LESS_THAN;
    for (int i = 0; i < tagNameBytes.length; i++)
    {
      openTagBytes[i + 1] = tagNameBytes[i];
    }
    openTagBytes[openTagBytes.length - 1] = GREATER_THAN;

    int openTagIndex = indexOf(content, openTagBytes);
    if (openTagIndex >= 0)
    {
      byte[] closedTagBytes = new byte[tagNameBytes.length + 3];
      closedTagBytes[0] = LESS_THAN;
      closedTagBytes[1] = SOLIDUS;
      for (int i = 0; i < tagNameBytes.length; i++)
      {
        closedTagBytes[i + 2] = tagNameBytes[i];
      }
      closedTagBytes[closedTagBytes.length - 1] = GREATER_THAN;

      int closeTagIndex = lastIndexOf(content, closedTagBytes);
      return (closeTagIndex > openTagIndex);
    }
    else
    {
      return false;
    }
  }


  static int indexOf(byte[] data, byte[] pattern)
  {
    int matchIndex = 0;

    int finalIndex = pattern.length - 1;
    for (int i = 0; i < data.length; i++)
    {
      if (data[i] == pattern[matchIndex])
      {
        if (matchIndex == finalIndex)
        {
          return i - finalIndex;
        }
        else
        {
          ++matchIndex;
        }
      }
      else
      {
        matchIndex = 0;
      }
    }

    return -1;
  }

  static int lastIndexOf(byte[] data, byte[] pattern)
  {
    int startMatchIndex = pattern.length - 1;
    int matchIndex = startMatchIndex;

    for (int i = data.length - 1; i >= 0; i--)
    {
      if (data[i] == pattern[matchIndex])
      {
        if (matchIndex == 0)
        {
          return i;
        }
        else
        {
          --matchIndex;
        }
      }
      else
      {
        matchIndex = startMatchIndex;
      }
    }

    return -1;
  }

  static final byte LESS_THAN = (byte)'<';

  static final byte SOLIDUS = (byte)'/';

  static final byte GREATER_THAN = (byte)'>';

}

