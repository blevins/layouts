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

import static org.junit.Assert.*;
import static org.baswell.layouts.View.*;

public class ViewTest
{
  @Test
  public void testContent()
  {
    /*
    LayoutContent layoutContent = new LayoutContent("<head>THIS IS THE HEAD</head><body>THIS IS THE BODY</body>");
    assertEquals("THIS IS THE HEAD", layoutContent.getHead());
    assertEquals("THIS IS THE BODY", layoutContent.getBody());
    assertEquals("<head>THIS IS THE HEAD</head><body>THIS IS THE BODY</body>", layoutContent.toString());
    */
  }

  @Test
  public void testIndexOf()
  {
    byte[] pattern = "122345".getBytes();
    assertEquals(-1, indexOf("ABC123456789DEF".getBytes(), pattern));
    assertEquals(3, indexOf("ABC1223456789DEF".getBytes(), pattern));

  }

  @Test
  public void testLastIndexOf()
  {
    byte[] pattern = new byte[]{6, 6, 6};
    byte[] content = new byte[]{1, 2, 3, 4, 5, 6, 6, 6, 7, 8};
    assertEquals(5, lastIndexOf(content, pattern));

    pattern = "122345".getBytes();
    assertEquals(3, lastIndexOf("ABC1223456789DEF".getBytes(), pattern));
    assertEquals(-1, lastIndexOf("ABC123456789DEF".getBytes(), pattern));
    assertEquals(16, lastIndexOf("ABC1223456789DEF122345".getBytes(), pattern));
    assertEquals(16, lastIndexOf("ABC1223456789DEF122345XXFFGG".getBytes(), pattern));

  }

}
