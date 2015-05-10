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
import java.util.Arrays;
import java.util.List;

import static org.baswell.layouts.SharedMethods.*;

/**
 * <p>
 * By default a request will be a candidate for a layout unless one the following conditions is met:
 * </p>
 *
 * <ul>
 * <li>The HTTP header _Accept_ is set with a non-HTML mime type.</li>
 * <li>The URL ends in a known, non-HTML file extension such as _css_, _js_, or _png_.</li>
 * <li>The HTTP header _X-Requested-With_ is set with the value _XMLHttpRequest_ (Ajax request).</li>
 * </ul>
 *
 */
public class DefaultHtmlPageDecider implements UseLayoutDecider
{
  @Override
  public boolean isCandidateForLayout(HttpServletRequest request)
  {
    return isHtmlRequest(request) && !isAjaxRequest(request);
  }

  protected boolean isHtmlRequest(HttpServletRequest request)
  {
    return isHtmlContent(request.getHeader("Accept")) && !knownNonHtmlFile(request.getRequestURI());
  }

  protected boolean isAjaxRequest(HttpServletRequest request)
  {
    return "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
  }

  static protected boolean knownNonHtmlFile(String uri)
  {
    int extensionIndex = uri.lastIndexOf('.');
    if (extensionIndex > 0)
    {
      int lastSlashIndex = uri.lastIndexOf('/');
      if ((extensionIndex > lastSlashIndex) && (extensionIndex < uri.length()))
      {
        String extension = uri.substring(extensionIndex + 1, uri.length()).toLowerCase();
        return knownNonHtmlFileExtensions.contains(extension);
      }
      else
      {
        return false;
      }
    }
    else
    {
      return false;
    }
  }

  static final List<String> knownNonHtmlFileExtensions = Arrays.asList("ace", "aif", "ani", "api", "art", "asc", "asm", "asp", "avi", "bak", "bas", "bat", "bfc", "bin", "bin", "bmp", "bud", "bz2", "c", "cat", "cbl", "cbt", "cda", "cdt", "cgi", "class", "clp", "cmd", "cmf", "com", "cpl", "cpp", "css", "csv", "cur", "dao", "dat", "dd", "deb", "dev", "dic", "dir", "dll", "doc", "docx", "dot", "drv", "ds", "dun", "dwg", "dxf", "emf", "eml", "eps", "eps2", "exe", "ffl", "ffo", "fla", "fnt", "gid", "gif", "grp", "gz", "hex", "hlp", "hqx", "ht", "icl", "icm", "ico", "inf", "ini", "jar", "jpeg", "jpg", "js", "lab", "lgo", "lit", "lnk", "log", "lsp", "maq", "mar", "mdb", "mdl", "mid", "mod", "mov", "mp3", "mpeg", "mpp", "msg", "msg", "ncf", "nlm", "o", "ocx", "ogg", "ost", "pak", "pcl", "pct", "pdf", "pdf", "pdr", "pif", "pif", "pif", "pl", "pm", "pm3", "pm4", "pm5", "pm6", "png", "pol", "pot", "ppd", "pps", "ppt", "prn", "ps", "psd", "psp", "pst", "pub", "qif", "ram", "rar", "raw", "rdo", "reg", "rm", "rpm", "rsc", "rtf", "s pwl", "scr", "sea", "sh", "sit", "smd", "svg", "swf", "swp", "sys", "tar", "tga", "tiff", "tmp", "ttf", "txt", "udf", "uue", "vbx", "vm", "vxd", "wav", "wmf", "wri", "wsz", "xcf", "xif", "xif", "xif", "xls", "xlsx", "xlt", "xml", "xsl", "zip");
}
