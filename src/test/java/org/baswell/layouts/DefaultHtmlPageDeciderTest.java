package org.baswell.layouts;/*
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

import org.junit.Test;

import static org.baswell.layouts.DefaultHtmlPageDecider.*;
import static org.junit.Assert.*;

public class DefaultHtmlPageDeciderTest
{
  @Test
  public void testKnownNonHtmlFile()
  {
    assertTrue(knownNonHtmlFile("/one/two/hello.txt"));
    assertTrue(knownNonHtmlFile("/one/two/hello.doc"));
    assertTrue(knownNonHtmlFile("/one.xlsx"));
    assertTrue(knownNonHtmlFile("hello.pdf"));

    assertFalse(knownNonHtmlFile("/hellopdf"));
    assertFalse(knownNonHtmlFile("/one/two.html"));
    assertFalse(knownNonHtmlFile("/one/two.htm"));
    assertFalse(knownNonHtmlFile("/one/two.jsp"));
    assertFalse(knownNonHtmlFile("/one/two.pdf/test"));
  }
}
