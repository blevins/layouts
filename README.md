Layouts
=======

Layouts is a Java library for creating HTML content. A layout is a JSP based template that defines the surroundings of an HTML page. Layouts are combined with views to create a full HTML response. For example
if the JSP view:

```JSP
<head>
    <title>This Is The Title</title>
</head>
<body>
    <h1>This Is The Content</h1>
</body>
```

Was rendered with the layout:

```JSP
<html>
  <head>
    <link href="<%=request.getContextPath() %>/assets/css/bootstrap.min.css"
        rel="stylesheet">
    ${view.yield("head", pageContext)}
  </head>
  <body>
    ${view.yield("body", pageContext)}
    <script src="<%=request.getContextPath() %>/assets/js/bootstrap.min.js"></script>
  </body>
</html>
```

The resulting HTML page returned to the client would be:

```HTML
<html>
  <head>
    <link href="/assets/css/bootstrap.min.css" rel="stylesheet">
    <title>This Is The Title</title>
  </head>
  <body>
    <h1>This Is The Content</h1>
    <script src="/assets/js/bootstrap.min.js"></script>
  </body>
</html>
```

Layouts uses basic JSP and XHTML and is web framework agnostic.

## Getting Started

### Direct Download
You can download <a href="https://github.com/baswerc/layouts/releases/download/v1.0/layouts-1.0.jar">layouts-1.0.jar</a> directly and place in your project.

### Using Maven
Add the following dependency into your Maven project:

````xml
<dependency>
    <groupId>org.baswell</groupId>
    <artifactId>layouts</artifactId>
    <version>1.0</version>
</dependency>
````

### Dependencies
Layouts runs within a Java Servlet container at API 2.4 or greater. Layouts has no other external dependencies.

## Layouts Filter
Layouts is used within a Servlet container by configuring the <a href="http://baswerc.github.io/layouts/javadoc/org/baswell/layouts/LayoutsFilter.html">LayoutsFilter</a>.

````xml
<filter>
    <filter-name>LayoutsFilter</filter-name>
    <filter-class>org.baswell.layouts.LayoutsFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>LayoutsFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
````

The `LayoutsFilter` must be in the processing chain for all requests you want rendered with a layout. It should be placed before any other filters that might
generate content for the HTTP response.

You can use the filter parameters _ONLY_ and _EXCEPT_ for finer grained control (than _url-pattern_) over which HTTP requests the `LayoutsFilter` is engaged for.

````xml
<init-param>
  <param-name>ONLY</param-name>
  <param-value>/home/.*,/users/.*</param-value>
</init-param>
````

The _ONLY_ parameter must be a list (comma delimited) of valid Java regular expression. If specified, only request URIs that match one of these
patterns will be candidates for layouts. The URI from the HTTP request matched against these patterns will not include the context path of your application.

The other supported parameter is _EXCEPT_:

````xml
<init-param>
  <param-name>EXCEPT</param-name>
  <param-value>.*\.html$,.*\.htm$</param-value>
</init-param>
````

The _EXCEPT_ parameter must be a list (comma delimited) of valid Java regular expression. If specified, only request URIs that don't match any of these patterns
will be candidates for layouts. The URI from the HTTP request matched against these patterns will not include the context path of your application.

If both _ONLY_ and _EXCEPT_ are specified then a request will only be a candidate for a layout if a match is made one of the _ONLY_ patterns and no match is made on any of the _EXCEPT_ patterns.

## Layout Files

Each of your layouts should be a valid JSP file. The name of the layout will be the file name minus the extension (case sensitive). So for example the layout
_/WEB-INF/jsps/layout/basic.jsp_ will have the name _basic_.

Within the context of a layout, `yield` identifies a section where content from the view should be inserted. The simplest way to use this is to have a
single yield, into which the entire contents of the view currently being rendered is inserted:

````JSP
<html>
  <head>
  </head>
  <body>
    ${view.yield(pageContext)}
  </body>
</html>
````
You can also create a layout with multiple yielding regions:

````JSP
<html>
  <head>
    ${view.yield("head", pageContext)}
  </head>
  <body>
    ${view.yield("body", pageContext)}
  </body>
</html>
````

The content of the outer most level `<head>` element in the view will be rendered inside the `<head>` element of the layout.
Likewise the content of the outer most level `<body>` element in the view view will be rendered inside the `<body>` element of
the layout. You can use any tag element name to create content sections within your view.

### Yield Method Shortcuts

You can use the two shortcut yield methods `${view.yieldHead(pageContext)}` and `${view.yieldBody(pageContext)}` which are equivalent to `${view.yield("head", pageContext)}`
and `${view.yield("body", pageContext)}`.

### Finding Layouts
By default all your application layouts should go in _/WEB-INF/jsps/layouts/_. This can be changed by using the _LAYOUTS_DIRECTORY_ init parameter for the `LayoutsFilter`.

````xml
<init-param>
  <param-name>LAYOUTS_DIRECTORY</param-name>
  <param-value>/WEB-INF/layouts</param-value>
</init-param>
````

The default layout used for views is _application.jsp_. This can be changed by using the _DEFAULT_LAYOUT_ init parameter for the `LayoutsFilter`. For example to change the default
layout to _basic.jsp_:


````xml
<init-param>
  <param-name>DEFAULT_LAYOUT</param-name>
  <param-value>basic</param-value>
</init-param>
````

### Specifying Layouts

A different layout from the default can be specified by setting an `HttpServletRequest` attribute for the current request. Use the constant <a href="http://baswerc.github.io/layouts/javadoc/org/baswell/layouts/Layouts.html#LAYOUT">Layouts.LAYOUT</a> with the name
of the layout to use. For example:

```Java
httpServletRequest.setAttribute(Layouts.LAYOUT, "rightMenuBase");
```

The request attribute can be set in the Servlet or controller portion of your code or in the JSP page of the view.

### Rendering Without Layouts

To disable the layout for a view use the `HttpServletRequest` attribute <a href="http://baswerc.github.io/layouts/javadoc/org/baswell/layouts/Layouts.html#NO_LAYOUT">Layouts.NO_LAYOUT</a>.

```Java
httpServletRequest.setAttribute(Layouts.NO_LAYOUT, true);
```

### View Types
Your views can by anything that writes to either the `OutputStream` or `PrintWriter` of the `HttpServletRequest`. This means your views (for example) can be Servlets, JSP files, or static HTML files.

## Determining Layout Candidates

By default any request the `LayoutsFilter` processes will be rendered with a layout unless one the following conditions is met:

* The HTTP header _Accept_ is set with a non-HTML mime type.
* The URL ends in a known, non-HTML file extension such as _css_, _js_, or _png_.
* The HTTP header _X-Requested-With_ is set with the value _XMLHttpRequest_ (Ajax request).
* The `HttpServletRequest` attribute <a href="http://baswerc.github.io/layouts/javadoc/org/baswell/layouts/Layouts.html#NO_LAYOUT">Layouts.NO_LAYOUT</a> is set to `true`.

You can override this behavior by implementing the interface <a href="http://baswerc.github.io/layouts/javadoc/org/baswell/layouts/UseLayoutDecider.html">UseLayoutDecider</a>. The `LayoutsFilter` init parameter _USE_LAYOUT_DECIDER_ should be set with
with the full qualified class name of your implementation.

````xml
<init-param>
  <param-name>USE_LAYOUT_DECIDER</param-name>
  <param-value>com.example.MyUseLayoutDecider</param-value>
</init-param>
````

Your implementation must have a default constructor that the `LayoutsFilter` will call at initialization time.

## Processing Order
The full content of your view will be rendered before the layout for the view is executed. This means if your view is a Servlet or JSP file you can set request attributes used in your layout. For example in the JSP view:


````JSP
<%
request.setAttribute("title", "This Is The Title");
%>
<h1>This Is The Content</h1>
````

The _title_ attribute is set that can then be used in the layout as:

````JSP
<html>
  <head>
    <title><%= request.getAttribute("title") %></title>
  </head>
  <body>
    ${view.yield(pageContext)}
  </body>
</html>
````
