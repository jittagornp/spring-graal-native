/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.autoconfigure.web.servlet;

import java.util.concurrent.Callable;

import org.apache.catalina.authenticator.jaspic.AuthConfigFactoryImpl;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.AbstractHttp11JsseProtocol;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.apache.tomcat.websocket.server.WsContextListener;
import org.springframework.boot.autoconfigure.web.reactive.ReactiveWebServerFactoryAutoConfiguration.BeanPostProcessorsRegistrar;
import org.springframework.boot.autoconfigure.web.servlet.error.DefaultErrorViewResolver;
import org.springframework.boot.web.embedded.tomcat.TomcatEmbeddedWebappClassLoader;
import org.springframework.boot.web.server.ErrorPageRegistrarBeanPostProcessor;
import org.springframework.boot.web.server.WebServerFactoryCustomizerBeanPostProcessor;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.graal.extension.NativeImageHint;
import org.springframework.graal.extension.NativeImageConfiguration;
import org.springframework.graal.extension.TypeInfo;
import org.springframework.graal.type.AccessBits;
import org.springframework.http.MediaTypeFactory;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.function.support.HandlerFunctionAdapter;
import org.springframework.web.servlet.function.support.RouterFunctionMapping;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter;
import org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter;
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import org.springframework.web.servlet.support.SessionFlashMapManager;
import org.springframework.web.servlet.theme.FixedThemeResolver;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.util.HtmlUtils;


// These types come from DispatcherServlet.properties - maybe this hint should point to a properties file containing class names?
@NativeImageHint(trigger=DispatcherServletAutoConfiguration.class,typeInfos = {
	@TypeInfo(types= {
		HtmlUtils.class,MediaTypeFactory.class,
		AcceptHeaderLocaleResolver.class,FixedThemeResolver.class,BeanNameUrlHandlerMapping.class,RequestMappingHandlerMapping.class,RouterFunctionMapping.class,
		HttpRequestHandlerAdapter.class,SimpleControllerHandlerAdapter.class,RequestMappingHandlerAdapter.class,
		HandlerFunctionAdapter.class,ExceptionHandlerExceptionResolver.class,ResponseStatusExceptionResolver.class,
		DefaultHandlerExceptionResolver.class,DefaultRequestToViewNameTranslator.class,InternalResourceViewResolver.class,SessionFlashMapManager.class,
		AuthConfigFactoryImpl.class
		}),
		@TypeInfo(types= {DefaultServlet.class},access=AccessBits.CLASS|AccessBits.DECLARED_CONSTRUCTORS|AccessBits.DECLARED_METHODS)
})
@NativeImageHint(trigger=WebMvcAutoConfiguration.class, typeInfos = {
		@TypeInfo(types= {AnnotationConfigServletWebServerApplicationContext.class,
				DefaultErrorViewResolver.class,
				//WsContextListener.class,
				// TODO Maybe the first and last of these 3 needs to be in a more generic configuration hint working for both reactive and servlet
				ConfigurableWebApplicationContext.class,TomcatEmbeddedWebappClassLoader.class,WebApplicationContext.class,
	ProtocolHandler.class,AbstractProtocol.class,AbstractHttp11Protocol.class,AbstractHttp11JsseProtocol.class,Http11NioProtocol.class,
	ErrorPage.class,DefaultErrorViewResolver.class,BeanNameViewResolver.class,
				ErrorPageRegistrarBeanPostProcessor.class},
				typeNames= {"org.springframework.web.servlet.handler.AbstractHandlerMethodMapping$EmptyHandler",
						//TODO I wonder if this one should be conditional on jax2b being on the classpath, this is the stacktrace from securing-web
//Caused by: javax.xml.transform.TransformerFactoryConfigurationError: Provider com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl not found
//	at javax.xml.transform.FactoryFinder.newInstance(FactoryFinder.java:181) ~[na:na]
//	at javax.xml.transform.FactoryFinder.find(FactoryFinder.java:261) ~[na:na]
//	at javax.xml.transform.TransformerFactory.newInstance(TransformerFactory.java:106) ~[na:na]
//	at org.springframework.http.converter.xml.AbstractXmlHttpMessageConverter.<init>(AbstractXmlHttpMessageConverter.java:52) ~[na:na]
//	at org.springframework.http.converter.xml.AbstractJaxb2HttpMessageConverter.<init>(AbstractJaxb2HttpMessageConverter.java:38) ~[na:na]
//	at org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter.<init>(Jaxb2RootElementHttpMessageConverter.java:64) ~[na:na]
//	at org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter.<init>(AllEncompassingFormHttpMessageConverter.java:72) ~[na:na]
//	at org.springframework.web.filter.FormContentFilter.<init>(FormContentFilter.java:61) ~[securing-web:na]
						"com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl"
						}),
		@TypeInfo(types= {Callable.class},access=AccessBits.CLASS|AccessBits.DECLARED_METHODS|AccessBits.DECLARED_CONSTRUCTORS)},abortIfTypesMissing = true)
// TODO this is an interesting one as it is hinted at by both flavours of BeanPostProcessorsRegistrar (reactive and servlet)
@NativeImageHint(trigger=BeanPostProcessorsRegistrar.class,typeInfos= {
		@TypeInfo(types= {WebServerFactoryCustomizerBeanPostProcessor.class},access=AccessBits.CLASS|AccessBits.DECLARED_CONSTRUCTORS)
})
public class Hints implements NativeImageConfiguration {
}
