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

public class
    Layouts
{
  /**
   * <p>To disable any layout for the current request do:</p>
   * <code>
   *   httpServletRequest.setAttribute(Layouts.NO_LAYOUT, true);
   * </code>
   */
  static public final String NO_LAYOUT = "noLayout";

  /**
   * <p>To override the default layout for the current request do:</p>
   * <code>
   *   httpServletRequest.setAttribute(Layouts.LAYOUT, "customLayout");
   * </code>
   * <p>The name of the layout should be the file name minus the file extension.</p>
   */
  static public final String LAYOUT = "layout";

  /**
   * <p>The view can be rendered in a layout in one of two ways:</p>
   * <code>
   * <html>
   *  <head>
   *    ${view.yieldHead(pageContext)}
   *  </head>
   *  <body>
   *    <% ((View)request.getAttribute(Layouts.VIEW)).yieldBody(pageContext); %>
   *  </body>
   * </html>
   * </code>
   */
  static public final String VIEW = "view";
}
