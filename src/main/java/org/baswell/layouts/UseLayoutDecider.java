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

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * A way to tell Layouts if an HTTP request is a candidate for a layout. If the request is not a candidate then the
 * request will be processed unaltered by Layouts. If a request is a candidate then the content of this request will
 * be rendered in a layout unless:
 *</p>
 *
 * <ul>
 *   <li>The content type of the response is set and is something other than HTML.</li>
 *   <li>The {@link org.baswell.layouts.Layouts#NO_LAYOUT}</li> HttpServletRequest attribute is set to true.</li>
 *   <li>The request returns no content.</li>
 *   <li>The layout specified by {@link org.baswell.layouts.Layouts#LAYOUT} is invalid.</li>
 * </ul>
 */
public interface UseLayoutDecider
{
  /**
   *
   * @param httpRequest The HTTP request.
   * @return <code>true</code> if this HTTP request is a candidate for a layout.
   */
  boolean isCandidateForLayout(HttpServletRequest httpRequest);
}
