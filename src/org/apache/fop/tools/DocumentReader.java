/*
 * $Id$
 * ============================================================================
 *                    The Apache Software License, Version 1.1
 * ============================================================================
 * 
 * Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include the following acknowledgment: "This product includes software
 *    developed by the Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself, if
 *    and wherever such third-party acknowledgments normally appear.
 * 
 * 4. The names "FOP" and "Apache Software Foundation" must not be used to
 *    endorse or promote products derived from this software without prior
 *    written permission. For written permission, please contact
 *    apache@apache.org.
 * 
 * 5. Products derived from this software may not be called "Apache", nor may
 *    "Apache" appear in their name, without prior written permission of the
 *    Apache Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ============================================================================
 * 
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the Apache Software Foundation and was originally created by
 * James Tauber <jtauber@jtauber.com>. For more information on the Apache
 * Software Foundation, please see <http://www.apache.org/>.
 */ 
package org.apache.fop.tools;

import java.io.IOException;

// DOM
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

// SAX
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;

/**
 * This presents a DOM as an XMLReader to make it easy to use a Document
 * with a SAX-based implementation.
 *
 * @author Kelly A Campbell
 *
 */

public class DocumentReader implements XMLReader {

    // //////////////////////////////////////////////////////////////////
    // Configuration.
    // //////////////////////////////////////////////////////////////////
    private boolean namespaces = true;
    private boolean namespacePrefixes = true;


    /**
     * Look up the value of a feature.
     *
     * <p>The feature name is any fully-qualified URI.  It is
     * possible for an XMLReader to recognize a feature name but
     * to be unable to return its value; this is especially true
     * in the case of an adapter for a SAX1 Parser, which has
     * no way of knowing whether the underlying parser is
     * performing validation or expanding external entities.</p>
     *
     * <p>All XMLReaders are required to recognize the
     * http://xml.org/sax/features/namespaces and the
     * http://xml.org/sax/features/namespace-prefixes feature names.</p>
     *
     * <p>Some feature values may be available only in specific
     * contexts, such as before, during, or after a parse.</p>
     *
     * <p>Typical usage is something like this:</p>
     *
     * <pre>
     * XMLReader r = new MySAXDriver();
     *
     * // try to activate validation
     * try {
     * r.setFeature("http://xml.org/sax/features/validation", true);
     * } catch (SAXException e) {
     * System.err.println("Cannot activate validation.");
     * }
     *
     * // register event handlers
     * r.setContentHandler(new MyContentHandler());
     * r.setErrorHandler(new MyErrorHandler());
     *
     * // parse the first document
     * try {
     * r.parse("http://www.foo.com/mydoc.xml");
     * } catch (IOException e) {
     * System.err.println("I/O exception reading XML document");
     * } catch (SAXException e) {
     * System.err.println("XML exception reading document.");
     * }
     * </pre>
     *
     * <p>Implementors are free (and encouraged) to invent their own features,
     * using names built on their own URIs.</p>
     *
     * @param name The feature name, which is a fully-qualified URI.
     * @return The current state of the feature (true or false).
     * @exception SAXNotRecognizedException When the
     * XMLReader does not recognize the feature name.
     * @exception SAXNotSupportedException When the
     * XMLReader recognizes the feature name but
     * cannot determine its value at this time.
     * @see #setFeature
     */
    public boolean getFeature(String name)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/features/namespaces".equals(name)) {
            return namespaces;
        } else if ("http://xml.org/sax/features/namespace-prefixes".equals(name)) {
            return namespacePrefixes;
        } else {
            throw new SAXNotRecognizedException("Feature '" + name
                    + "' not recognized or supported by Document2SAXAdapter");
        }

    }



    /**
     * Set the state of a feature.
     *
     * <p>The feature name is any fully-qualified URI.  It is
     * possible for an XMLReader to recognize a feature name but
     * to be unable to set its value; this is especially true
     * in the case of an adapter for a SAX1 {@link org.xml.sax.Parser Parser},
     * which has no way of affecting whether the underlying parser is
     * validating, for example.</p>
     *
     * <p>All XMLReaders are required to support setting
     * http://xml.org/sax/features/namespaces to true and
     * http://xml.org/sax/features/namespace-prefixes to false.</p>
     *
     * <p>Some feature values may be immutable or mutable only
     * in specific contexts, such as before, during, or after
     * a parse.</p>
     *
     * @param name The feature name, which is a fully-qualified URI.
     * @param value The requested state of the feature (true or false).
     * @exception SAXNotRecognizedException When the
     * XMLReader does not recognize the feature name.
     * @exception SAXNotSupportedException When the
     * XMLReader recognizes the feature name but
     * cannot set the requested value.
     * @see #getFeature
     */
    public void setFeature(String name, boolean value)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/features/namespaces".equals(name)) {
            namespaces = value;
        } else if ("http://xml.org/sax/features/namespace-prefixes".equals(name)) {
            namespacePrefixes = value;
        } else {
            throw new SAXNotRecognizedException("Feature '" + name
                    + "' not recognized or supported by Document2SAXAdapter");
        }

    }



    /**
     * Look up the value of a property.
     *
     * <p>The property name is any fully-qualified URI.  It is
     * possible for an XMLReader to recognize a property name but
     * to be unable to return its state; this is especially true
     * in the case of an adapter for a SAX1 {@link org.xml.sax.Parser
     * Parser}.</p>
     *
     * <p>XMLReaders are not required to recognize any specific
     * property names, though an initial core set is documented for
     * SAX2.</p>
     *
     * <p>Some property values may be available only in specific
     * contexts, such as before, during, or after a parse.</p>
     *
     * <p>Implementors are free (and encouraged) to invent their own properties,
     * using names built on their own URIs.</p>
     *
     * @param name The property name, which is a fully-qualified URI.
     * @return The current value of the property.
     * @exception SAXNotRecognizedException When the
     * XMLReader does not recognize the property name.
     * @exception SAXNotSupportedException When the
     * XMLReader recognizes the property name but
     * cannot determine its value at this time.
     * @see #setProperty
     */
    public Object getProperty(String name)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotRecognizedException("Property '" + name
                + "' not recognized or supported by Document2SAXAdapter");
    }



    /**
     * Set the value of a property.
     *
     * <p>The property name is any fully-qualified URI.  It is
     * possible for an XMLReader to recognize a property name but
     * to be unable to set its value; this is especially true
     * in the case of an adapter for a SAX1 {@link org.xml.sax.Parser
     * Parser}.</p>
     *
     * <p>XMLReaders are not required to recognize setting
     * any specific property names, though a core set is provided with
     * SAX2.</p>
     *
     * <p>Some property values may be immutable or mutable only
     * in specific contexts, such as before, during, or after
     * a parse.</p>
     *
     * <p>This method is also the standard mechanism for setting
     * extended handlers.</p>
     *
     * @param name The property name, which is a fully-qualified URI.
     * @param value The requested value for the property.
     * @exception SAXNotRecognizedException When the
     * XMLReader does not recognize the property name.
     * @exception SAXNotSupportedException When the
     * XMLReader recognizes the property name but
     * cannot set the requested value.
     */
    public void setProperty(String name, Object value)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotRecognizedException("Property '" + name
                + "' not recognized or supported by Document2SAXAdapter");
    }



    // //////////////////////////////////////////////////////////////////
    // Event handlers.
    // //////////////////////////////////////////////////////////////////
    private EntityResolver entityResolver = null;
    private DTDHandler dtdHandler = null;
    private ContentHandler contentHandler = null;
    private ErrorHandler errorHandler = null;


    /**
     * Allow an application to register an entity resolver.
     *
     * <p>If the application does not register an entity resolver,
     * the XMLReader will perform its own default resolution.</p>
     *
     * <p>Applications may register a new or different resolver in the
     * middle of a parse, and the SAX parser must begin using the new
     * resolver immediately.</p>
     *
     * @param resolver The entity resolver.
     * @see #getEntityResolver
     */
    public void setEntityResolver(EntityResolver resolver) {
        entityResolver = resolver;
    }



    /**
     * Return the current entity resolver.
     *
     * @return The current entity resolver, or null if none
     * has been registered.
     * @see #setEntityResolver
     */
    public EntityResolver getEntityResolver() {
        return entityResolver;
    }



    /**
     * Allow an application to register a DTD event handler.
     *
     * <p>If the application does not register a DTD handler, all DTD
     * events reported by the SAX parser will be silently ignored.</p>
     *
     * <p>Applications may register a new or different handler in the
     * middle of a parse, and the SAX parser must begin using the new
     * handler immediately.</p>
     *
     * @param handler The DTD handler.
     * @see #getDTDHandler
     */
    public void setDTDHandler(DTDHandler handler) {
        dtdHandler = handler;
    }



    /**
     * Return the current DTD handler.
     *
     * @return The current DTD handler, or null if none
     * has been registered.
     * @see #setDTDHandler
     */
    public DTDHandler getDTDHandler() {
        return dtdHandler;
    }



    /**
     * Allow an application to register a content event handler.
     *
     * <p>If the application does not register a content handler, all
     * content events reported by the SAX parser will be silently
     * ignored.</p>
     *
     * <p>Applications may register a new or different handler in the
     * middle of a parse, and the SAX parser must begin using the new
     * handler immediately.</p>
     *
     * @param handler The content handler.
     * @see #getContentHandler
     */
    public void setContentHandler(ContentHandler handler) {
        contentHandler = handler;
    }



    /**
     * Return the current content handler.
     *
     * @return The current content handler, or null if none
     * has been registered.
     * @see #setContentHandler
     */
    public ContentHandler getContentHandler() {
        return contentHandler;
    }



    /**
     * Allow an application to register an error event handler.
     *
     * <p>If the application does not register an error handler, all
     * error events reported by the SAX parser will be silently
     * ignored; however, normal processing may not continue.  It is
     * highly recommended that all SAX applications implement an
     * error handler to avoid unexpected bugs.</p>
     *
     * <p>Applications may register a new or different handler in the
     * middle of a parse, and the SAX parser must begin using the new
     * handler immediately.</p>
     *
     * @param handler The error handler.
     * @see #getErrorHandler
     */
    public void setErrorHandler(ErrorHandler handler) {
        errorHandler = handler;
    }

    /**
     * Return the current error handler.
     *
     * @return The current error handler, or null if none
     * has been registered.
     * @see #setErrorHandler
     */
    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }



    // //////////////////////////////////////////////////////////////////
    // Parsing.
    // //////////////////////////////////////////////////////////////////

    /**
     * Parse an XML DOM document.
     *
     *
     *
     * @param input The input source for the top-level of the
     * XML document.
     * @exception SAXException Any SAX exception, possibly
     * wrapping another exception.
     * @exception IOException An IO exception from the parser,
     * possibly from a byte stream or character stream
     * supplied by the application.
     * @see org.xml.sax.InputSource
     * @see #parse(java.lang.String)
     * @see #setEntityResolver
     * @see #setDTDHandler
     * @see #setContentHandler
     * @see #setErrorHandler
     */
    public void parse(InputSource input) throws IOException, SAXException {
        if (input instanceof DocumentInputSource) {
            Document document = ((DocumentInputSource)input).getDocument();
            if (contentHandler == null) {
                throw new SAXException("ContentHandler is null. Please use setContentHandler()");
            }

            // refactored from org.apache.fop.apps.Driver
            /* most of this code is modified from John Cowan's */

            Node currentNode;
            AttributesImpl currentAtts;

            /* temporary array for making Strings into character arrays */
            char[] array = null;

            currentAtts = new AttributesImpl();

            /* start at the document element */
            currentNode = document;
            while (currentNode != null) {
                switch (currentNode.getNodeType()) {
                case Node.DOCUMENT_NODE:
                    contentHandler.startDocument();
                    break;
                case Node.CDATA_SECTION_NODE:
                case Node.TEXT_NODE:
                    String data = currentNode.getNodeValue();
                    int datalen = data.length();
                    if (array == null || array.length < datalen) {
                        /*
                         * if the array isn't big enough, make a new
                         * one
                         */
                        array = new char[datalen];
                    }
                    data.getChars(0, datalen, array, 0);
                    contentHandler.characters(array, 0, datalen);
                    break;
                case Node.PROCESSING_INSTRUCTION_NODE:
                    contentHandler.processingInstruction(currentNode.getNodeName(),
                                                          currentNode.getNodeValue());
                    break;
                case Node.ELEMENT_NODE:
                    NamedNodeMap map = currentNode.getAttributes();
                    currentAtts.clear();
                    for (int i = map.getLength() - 1; i >= 0; i--) {
                        Attr att = (Attr)map.item(i);
                        currentAtts.addAttribute(att.getNamespaceURI(),
                                                 att.getLocalName(),
                                                 att.getName(), "CDATA",
                                                 att.getValue());
                    }
                    contentHandler.startElement(currentNode.getNamespaceURI(),
                                                 currentNode.getLocalName(),
                                                 currentNode.getNodeName(),
                                                 currentAtts);
                    break;
                }

                Node nextNode = currentNode.getFirstChild();
                if (nextNode != null) {
                    currentNode = nextNode;
                    continue;
                }

                while (currentNode != null) {
                    switch (currentNode.getNodeType()) {
                    case Node.DOCUMENT_NODE:
                        contentHandler.endDocument();
                        break;
                    case Node.ELEMENT_NODE:
                        contentHandler.endElement(currentNode.getNamespaceURI(),
                                                   currentNode.getLocalName(),
                                                   currentNode.getNodeName());
                        break;
                    }

                    nextNode = currentNode.getNextSibling();
                    if (nextNode != null) {
                        currentNode = nextNode;
                        break;
                    }

                    currentNode = currentNode.getParentNode();
                }
            }

        } else {
            throw new SAXException("DocumentReader only supports parsing of a DocumentInputSource");
        }

    }



    /**
     * DocumentReader requires a DocumentInputSource, so this is not
     * implements and simply throws a SAXException. Use parse(DocumentInputSource)
     * instead
     *
     * @param systemId The system identifier (URI).
     * @exception SAXException Any SAX exception, possibly
     * wrapping another exception.
     * @exception IOException An IO exception from the parser,
     * possibly from a byte stream or character stream
     * supplied by the application.
     * @see #parse(org.xml.sax.InputSource)
     */
    public void parse(String systemId) throws IOException, SAXException {
        throw new SAXException("DocumentReader only supports parsing of a DocumentInputSource");
    }

}


