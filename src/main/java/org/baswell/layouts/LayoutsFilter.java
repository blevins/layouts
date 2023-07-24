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

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.baswell.layouts.Layouts.*;
import static org.baswell.layouts.SharedMethods.*;

/**
 * <p>
 * The {@code LayoutsFilter} must be in the processing chain for all requests you want rendered with a layout. It should be placed before any other filters that might
 * generate content for the HTTP response.
 * </p>
 *
 * <p>
 * You can use the filter parameters <i>ONLY</i> and <i>EXCEPT</i> for finer grained control (than <i>url-pattern</i>) over which HTTP requests the {@code LayoutsFilter} is engaged for.
 * </p>
 *
 * <pre>
 * {@code
 * <init-param>
 *   <param-name>ONLY</param-name>
 *   <param-value>/home/.*,/users/.*</param-value>
 * </init-param>
 * }
 * </pre>
 *
 * <p>
 * The <i>ONLY</i> parameter must be a list (comma delimited) of valid Java regular expression. If specified, only request URIs that match one of these
 * patterns will be candidates for layouts. The URI from the HTTP request matched against these patterns will not include the context path of your application.
 * </p>
 *
 * <p>
 * The other supported parameter is <i>EXCEPT</i>:
 * </p>
 *
 * <pre>
 * {@code
 * <init-param>
 *   <param-name>EXCEPT</param-name>
 *   <param-value>.*\.html$,.*\.htm$</param-value>
 * </init-param>
 * }
 * </pre>
 *
 * <p>
 * The <i>EXCEPT</i> parameter must be a list (comma delimited) of valid Java regular expression. If specified, only request URIs that don't match any of these patterns
 * will be candidates for layouts. The URI from the HTTP request matched against these patterns will not include the context path of your application.
 * </p>
 *
 * <p>
 * If both <i>ONLY</i> and <i>EXCEPT</i> are specified then a request will only be a candidate for a layout if a match is made one of the <i>ONLY</i> patterns and no match is made on any of the <i>EXCEPT</i> patterns.
 * </p>
 */
public class LayoutsFilter implements Filter
{
  private List<Pattern> onlyPatterns;

  private List<Pattern> exceptPatterns;

  private UseLayoutDecider layoutDecider;

  private Layout defaultLayout;

  private Map<String, Layout> layouts;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException
  {
    String onlyInitParam = filterConfig.getInitParameter("ONLY");
    if (onlyInitParam != null)
    {
      List<Pattern> onlyPatterns = new ArrayList<Pattern>();
      String[] onlyInitParams = onlyInitParam.split(",");
      for (String pattern : onlyInitParams)
      {
        if (!pattern.trim().isEmpty())
        {
          onlyPatterns.add(Pattern.compile(pattern));
        }
      }

      if (!onlyPatterns.isEmpty()) this.onlyPatterns = onlyPatterns;
    }

    String exceptInitParam = filterConfig.getInitParameter("EXCEPT");
    if (exceptInitParam != null)
    {
      List<Pattern> exceptPatterns = new ArrayList<Pattern>();
      String[] exceptInitParams = exceptInitParam.split(",");
      for (String pattern : exceptInitParams)
      {
        if (!pattern.trim().isEmpty())
        {
          exceptPatterns.add(Pattern.compile(pattern));
        }
      }

      if (!exceptPatterns.isEmpty()) this.exceptPatterns = exceptPatterns;
    }

    String useLayoutDeciderClass = filterConfig.getInitParameter("USE_LAYOUT_DECIDER");
    if (useLayoutDeciderClass == null)
    {
      layoutDecider = new DefaultHtmlPageDecider();
    }
    else
    {
      try
      {
        layoutDecider = (UseLayoutDecider) Class.forName(useLayoutDeciderClass).getConstructor().newInstance();
      }
      catch (Exception e)
      {
        throw new ServletException(e);
      }
    }

    String layoutsDirPath = filterConfig.getInitParameter("LAYOUTS_DIRECTORY");
    if (layoutsDirPath == null)
    {
      layoutsDirPath = "/WEB-INF/jsps/layouts";
    }
    else if (!layoutsDirPath.startsWith("/"))
    {
      layoutsDirPath = "/" + layoutsDirPath;
    }

    if (!layoutsDirPath.endsWith("/"))
    {
      layoutsDirPath += "/";
    }

    String layoutsParameter = filterConfig.getInitParameter("LAYOUTS");
    if (layoutsParameter == null || layoutsParameter.trim().isEmpty())
    {
      File layoutsDir = new File(filterConfig.getServletContext().getRealPath(layoutsDirPath));
      if (!layoutsDir.isDirectory())
      {
        throw new ServletException("Layouts directory: " + layoutsDirPath + " does not exists");
      }
      else
      {
        layouts = new HashMap<String, Layout>();
        File[] layoutFiles = layoutsDir.listFiles();
        if (layoutFiles != null)
        {
          for (File layoutFile : layoutFiles)
          {
            if (layoutFile.isFile())
            {
              String layoutFileName = layoutFile.getName();
              if (layoutFileName.toLowerCase().endsWith("jsp") || layoutFileName.toLowerCase().endsWith("jspx"))
              {
                String layoutName = layoutFileName;
                int index = layoutName.indexOf('.');
                if (index > -1)
                {
                  layoutName = layoutName.substring(0, index);
                }
                layouts.put(layoutName, new Layout(layoutName, layoutsDirPath + layoutFileName));
              }
            }
          }
        }
      }
    }
    else
    {
      layouts = new HashMap<String, Layout>();
      String[] layoutParameterValues = layoutsParameter.split(",");
      for (String layoutParameterValue : layoutParameterValues)
      {
        if (!layoutParameterValue.trim().isEmpty())
        {
          String layoutPath = layoutParameterValue.trim();
          if (!layoutPath.startsWith("/"))
          {
            layoutPath = layoutsDirPath + layoutPath;
          }


          String layoutName = layoutPath;

          int index = layoutPath.lastIndexOf('/');
          layoutName = layoutName.substring(index + 1, layoutName.length());
          index = layoutName.indexOf('.');
          if (index > -1)
          {
            layoutName = layoutName.substring(0, index);
          }
          layouts.put(layoutName, new Layout(layoutName, layoutPath));

        }
      }

    }

    String defaultLayoutName = filterConfig.getInitParameter("DEFAULT_LAYOUT");
    if (defaultLayoutName != null)
    {
      if (layouts.containsKey(defaultLayoutName))
      {
        defaultLayout = layouts.get(defaultLayoutName);
      }
      else
      {
        throw new ServletException("Default layout: " + defaultLayoutName + " not found.");
      }
    }
    else
    {
      defaultLayout = layouts.get("application");
    }

  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException
  {
    HttpServletRequest httpRequest = (HttpServletRequest)servletRequest;
    HttpServletResponse httpResponse = (HttpServletResponse)servletResponse;

    if (!requestExcludedFromPatterns(httpRequest) && layoutDecider.isCandidateForLayout(httpRequest))
    {
      HttpBufferedResponse httpResponseBuffer = new HttpBufferedResponse(httpRequest, httpResponse);
      chain.doFilter(httpRequest, httpResponseBuffer);

      if (httpResponseBuffer.hasBufferedContent() && httpResponseBuffer.isHtmlContent() && !trueValue(httpRequest.getAttribute(NO_LAYOUT)))
      {
        String layoutName = (String) httpRequest.getAttribute(LAYOUT);
        Layout layout = (layoutName == null) ? defaultLayout : layouts.get(layoutName);
        if (layout != null)
        {
          httpResponse = new HttpMixedOutputResponse(httpResponse);
          httpRequest.setAttribute(VIEW, new View(httpResponseBuffer.getContent(), httpResponse));
          httpRequest.getRequestDispatcher(layout.jspPath).forward(httpRequest, httpResponse);
        }
        else
        {
          httpResponse.setStatus(500);
          httpResponse.getWriter().write("<html><body>No layout defined with named: <i>" + layoutName + "</i></body></html>");
        }
      }
      else
      {
        httpResponseBuffer.pushContent();
      }
    }
    else
    {
      chain.doFilter(httpRequest, httpResponse);
    }
  }

  @Override
  public void destroy()
  {}

  boolean requestExcludedFromPatterns(HttpServletRequest httpRequest)
  {
    if (onlyPatterns != null || exceptPatterns != null)
    {
      String requestPath = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

      if (onlyPatterns != null)
      {
        boolean matchFound = false;
        for (Pattern onlyPattern : onlyPatterns)
        {
          if (onlyPattern.matcher(requestPath).matches())
          {
            matchFound = true;
            break;
          }
        }

        if (!matchFound)
        {
          return true;
        }
      }

      if (exceptPatterns != null)
      {
        for (Pattern exceptPattern : exceptPatterns)
        {
          if (exceptPattern.matcher(requestPath).matches())
          {
            return true;
          }
        }
      }
    }

    return false;
  }
}
