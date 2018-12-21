package com.lesports.sms.data.util.xml;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.lesports.sms.data.util.xml.annotation.*;
import com.lesports.sms.data.util.xml.exception.ParserException;
import com.lesports.utils.math.LeNumberUtils;
import org.apache.commons.collections.CollectionUtils;
import org.dom4j.*;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.dom4j.xpath.DefaultXPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.apache.commons.io.IOUtils.toInputStream;

/**
 * lesports-projects
 *
 * @author qiaohx
 * @since 14-11-16
 */
public class ParserFactory2 {
    private static final Logger LOG = LoggerFactory.getLogger(com.lesports.sms.data.util.xml.ParserFactory2.class);

    private final PrimitiveFieldParserFactory primitiveFieldParserFactory;

    public ParserFactory2() {
        primitiveFieldParserFactory = new PrimitiveFieldParserFactory();
    }

    /**
     * Creates a new parser
     *
     * @param clazz The class to be parsed
     * @param <T>   The class to be parsed
     * @return Parser
     */
    public <T> Parser<T> createXmlParser(Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Could not create parser for null class");
        }
        final List<FieldParser> parseableElements = new ArrayList<>();
        for (Field iteratorField : clazz.getDeclaredFields()) {
            final Annotation[] annotation = iteratorField.getAnnotations();
            iteratorField.setAccessible(true);
            FieldParser fieldParser = null;
            if (iteratorField.getType().isArray()) {
                fieldParser = createArrayParsers(iteratorField, annotation);
            } else if (List.class.isAssignableFrom(iteratorField.getType())) {
                fieldParser = createListParsers(iteratorField, annotation);
            } else {
                fieldParser = createFieldParsers(iteratorField, annotation);
            }
            if (null != fieldParser) {
                parseableElements.add(fieldParser);
            }
        }
        return new ParserImpl<>(clazz, parseableElements);
    }

    private List<XpathContent> getValidAnonationPaths(Annotation[] annotations) {
        List<XpathContent> list = Lists.newArrayList();
        for (int index = 0; index < annotations.length; index++) {
            String type = "";
            XPath xPath = null;
            Function function = null;
            if (annotations[index].annotationType().isAssignableFrom(XPathSportrard.class)) {
                XPathSportrard data = (XPathSportrard) annotations[index];
                type = "XPathSportrard";
                xPath = createXPath(data.value());
                function = getFunctionByFormatter(data.formatter());
            } else if (annotations[index].annotationType().isAssignableFrom(XPathSoda.class)) {
                XPathSoda data = (XPathSoda) annotations[index];
                type = "XPathSoda";
                xPath = createXPath(data.value());
                function = getFunctionByFormatter(data.formatter());
            } else if (annotations[index].annotationType().isAssignableFrom(XPathStats.class)) {
                XPathStats data = (XPathStats) annotations[index];
                type = "XPathStats";
                xPath = createXPath(data.value());
                function = getFunctionByFormatter(data.formatter());
            } else if (annotations[index].annotationType().isAssignableFrom(XPathStats2.class)) {
                XPathStats2 data = (XPathStats2) annotations[index];
                type = "XPathStats2";
                xPath = createXPath(data.value());
                function = getFunctionByFormatter(data.formatter());
            } else if (annotations[index].annotationType().isAssignableFrom(XPathStats3.class)) {
                XPathStats3 data = (XPathStats3) annotations[index];
                type = "XPathStats3";
                xPath = createXPath(data.value());
                function = getFunctionByFormatter(data.formatter());
            } else if (annotations[index].annotationType().isAssignableFrom(XPathStats4.class)) {
                XPathStats4 data = (XPathStats4) annotations[index];
                type = "XPathStats4";
                xPath = createXPath(data.value());
                function = getFunctionByFormatter(data.formatter());
            } else if (annotations[index].annotationType().isAssignableFrom(XPathSportrard2.class)) {
                XPathSportrard2 data = (XPathSportrard2) annotations[index];
                type = "XPathSportrard2";
                xPath = createXPath(data.value());
                function = getFunctionByFormatter(data.formatter());
            } else if (annotations[index].annotationType().isAssignableFrom(XPathSoda2.class)) {
                XPathSoda2 data = (XPathSoda2) annotations[index];
                type = "XPathSoda2";
                xPath = createXPath(data.value());
                function = getFunctionByFormatter(data.formatter());
            } else {
                continue;
            }
            list.add(new XpathContent(type, xPath, function));
        }
        return list;
    }

    private FieldParser createFieldParsers(Field iteratorField, Annotation[] annotations) {
        final FieldParser fieldParsers = null;
        List<XpathContent> list = getValidAnonationPaths(annotations);
        FieldParser fieldParser = primitiveFieldParserFactory.createFieldParser(iteratorField, list, iteratorField.getType().toString());
        return fieldParser;
    }

    private FieldParser createArrayParsers(Field iteratorField, Annotation[] annotations) {
        final FieldParser fieldParsers = null;
        List<XpathContent> list = getValidAnonationPaths(annotations);
        FieldParser fieldParser = new ArrayFieldParser(iteratorField, list, iteratorField.getType().getComponentType());
        return fieldParser;


    }

    private FieldParser createListParsers(Field iteratorField, Annotation[] annotations) {
        final FieldParser fieldParsers = null;
        List<XpathContent> list = getValidAnonationPaths(annotations);
        Type genericType = iteratorField.getGenericType();
        if (!(genericType instanceof ParameterizedType)) {
            throw new IllegalArgumentException("List does not have a defined generic type");
        }
        ParameterizedType type = (ParameterizedType) genericType;
        Type[] generics = type.getActualTypeArguments();
        Class elementClazz = null;
        if (generics.length >= 1) {
            elementClazz = (Class) generics[0];
        }
        if (null == elementClazz) {
            return null;
        }
        return new ListFieldParser(iteratorField, list, elementClazz);

    }

    private XPath createXPath(String value) {
        XPath xPath = new DefaultXPath(value);

        return xPath;
    }

    private Function getFunctionByFormatter(Class formatter) {
        if (null == formatter) {
            return null;
        }
        try {
            return (Function) formatter.newInstance();
        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
        }
        return null;
    }

    public <T> Parser<List<T>> createXmlListParser(Class<T> clazz, String XPathForList) {
        return null;
    }

    private abstract class FieldParser<T> {
        final Field field;
        final List<XpathContent> xPath;
        final String classType;

        public abstract void parseElement(String type, T obj, Object context) throws IllegalAccessException, ParserException;

        public FieldParser(Field field, List<XpathContent> xPath, String classType) {
            this.field = field;
            this.xPath = xPath;
            this.classType = classType;
        }
    }


    private abstract class CollectionsFieldParser<T> extends FieldParser {
        protected Parser<T> parser;
        protected FieldParser fieldParser;
        protected final Class<T> elementClazz;

        protected class ObjectWrapper<T> {
            public T object;
        }

        public CollectionsFieldParser(Field field, List<XpathContent> xPath, Class<T> elementClazz) {
            super(field, xPath, "");
            this.elementClazz = elementClazz;
            try {
                fieldParser = primitiveFieldParserFactory.createSubFieldParser(ObjectWrapper.class.getField("object"), xPath, elementClazz.toString());
            } catch (NoSuchFieldException e) {
                throw new ParserException("Could not create List parser", e);
            }
            if (fieldParser == null) {
                parser = ParserFactory2.this.createXmlParser(elementClazz);
            }
        }
    }

    private class ListFieldParser<T> extends CollectionsFieldParser<T> {
        public ListFieldParser(Field field, List<XpathContent> xPath, Class<T> elementClazz) {
            super(field, xPath, elementClazz);
        }

        @Override
        public void parseElement(String type, Object obj, Object doc) throws IllegalAccessException, ParserException {
            ObjectWrapper<T> wrapper = new ObjectWrapper<>();
            List<T> objList = (List<T>) field.get(obj);
            if (objList == null) {
                objList = new ArrayList<>();
                field.set(obj, objList);
            }
            XPath vaidXpath = null;
            for (int index = 0; index < xPath.size(); index++) {
                XpathContent xpathContent = (XpathContent) xPath.get(index);
                if (xpathContent.getXpath1() == null) continue;
                if (xpathContent.getType().equals(type)) {
                    vaidXpath = xpathContent.getXpath1();
                    break;
                }
                if (type.contains(xpathContent.getType()) && vaidXpath == null) vaidXpath = xpathContent.getXpath1();
            }
            List<Object> list = Lists.newArrayList();
            try {
                list = vaidXpath.selectNodes(doc);
            } catch (Exception e) {
                return;
            }
            if (CollectionUtils.isEmpty(list)) return;
            for (Object element : list) {
                if (fieldParser != null) {
                    fieldParser.parseElement("", wrapper, element);
                    objList.add(wrapper.object);
                } else if (parser != null) {
                    if (element instanceof Element) {
                        T returnObj = parser.parse(type, (Element) element);
                        objList.add(returnObj);
                    }
                }
            }
        }
    }

    private class ArrayFieldParser<T> extends CollectionsFieldParser<T> {
        public ArrayFieldParser(Field field, List<XpathContent> xPath, Class elementClazz) {
            super(field, xPath, elementClazz);
        }

        @Override
        public void parseElement(String type, Object obj, Object doc) throws IllegalAccessException, ParserException {
            ObjectWrapper<T> wrapper = new ObjectWrapper<>();
            XPath vaidXpath = null;
            for (int index = 0; index < xPath.size(); index++) {
                XpathContent xpathContent = (XpathContent) xPath.get(index);
                if (xpathContent.getXpath1() == null) continue;
                if (xpathContent.getType().equals(type)) {
                    vaidXpath = xpathContent.getXpath1();
                    break;
                }
                if (type.contains(xpathContent.getType()) && vaidXpath == null) vaidXpath = xpathContent.getXpath1();
            }
            List<Element> list = vaidXpath.selectNodes(doc);
            if (CollectionUtils.isEmpty(list)) return;
            T[] objArray = (T[]) Array.newInstance(elementClazz, list.size());
            for (int i = 0; i < objArray.length; i++) {
                if (fieldParser != null) {
                    fieldParser.parseElement(type, wrapper, list.get(i));
                    objArray[i] = wrapper.object;
                } else {
                    T returnObj = parser.parse(type, list.get(i));
                    objArray[i] = returnObj;
                }
            }
            field.set(obj, objArray);
        }
    }

    private class PrimitiveFieldParserFactory {
        private final HashMap<Class, Class<FieldParser>> parserMap;

        PrimitiveFieldParserFactory() {
            parserMap = new HashMap<>();
            for (Class clazz : this.getClass().getDeclaredClasses()) {
                if (FieldParser.class.isAssignableFrom(clazz)) {
                    Type type = clazz.getGenericSuperclass();
                    if (type instanceof ParameterizedType) {
                        Type[] generics = ((ParameterizedType) type).getActualTypeArguments();
                        parserMap.put((Class) generics[0], (Class<FieldParser>) clazz);
                        try {
                            Field primitive = ((Class) generics[0]).getDeclaredField("TYPE");
                            parserMap.put((Class) primitive.get(null), (Class<FieldParser>) clazz);
                        } catch (NoSuchFieldException ignore) {
                        } catch (IllegalAccessException ignore) {
                        }
                    }
                }
            }
        }

        public FieldParser createSubFieldParser(Field field, List<XpathContent> xPath, String clazzType) {
            if (clazzType.equals("interface org.dom4j.Element")) {

                try {
                    //            if (StringFieldParser.class.isAssignableFrom(fieldParserClass)||LongFieldParser.class.isAssignableFrom(fie)) {
                    return new ElementFieldParser(field, xPath);
                    //            }
                    //         Constructor<FieldParser> parserConstructor = fieldParserClass.getDeclaredConstructor(PrimitiveFieldParserFactory.class, Field.class, List.class);
//                return parserConstructor.newInstance(this, field, xPath);
                } catch (Exception e) {
                    throw new ParserException("Could not create new parser for " + field.getType(), e);
                }
            }
            return null;
        }

        public FieldParser createFieldParser(Field field, List<XpathContent> xPath, String clazz) {
            try {
                //            if (StringFieldParser.class.isAssignableFrom(fieldParserClass)||LongFieldParser.class.isAssignableFrom(fie)) {
                return new StringFieldParser(field, xPath, clazz);
                //            }
                //         Constructor<FieldParser> parserConstructor = fieldParserClass.getDeclaredConstructor(PrimitiveFieldParserFactory.class, Field.class, List.class);
//                return parserConstructor.newInstance(this, field, xPath);
            } catch (Exception e) {
                throw new ParserException("Could not create new parser for " + field.getType(), e);
            }
        }

        private abstract class PrimitiveFieldParser<T> extends FieldParser {
            PrimitiveFieldParser(Field field, List<XpathContent> xPath, String classType) {
                super(field, xPath, classType);
            }
        }

        private class ElementFieldParser extends PrimitiveFieldParser<Element> {

            @Override
            public void parseElement(String type, Object obj, Object context) throws IllegalAccessException, ParserException {
                Element element = (Element) context;
                field.set(obj, element);
            }

            public ElementFieldParser(Field field, List<XpathContent> xPath) {
                super(field, xPath, "");
            }
        }

        private class StringFieldParser extends PrimitiveFieldParser<String> {
            public StringFieldParser(Field field, List<XpathContent> xPath, String classType) {
                super(field, xPath, classType);

            }

            @Override
            public void parseElement(String type, Object obj, Object doc) throws IllegalAccessException {
                if (CollectionUtils.isEmpty(xPath)) return;
                XpathContent validXpathContent = null;
                XPath vaidXpath = null;
                for (int index = 0; index < xPath.size(); index++) {
                    XpathContent xpathContent = (XpathContent) xPath.get(index);
                    if (xpathContent.getXpath1() == null) continue;
                    if (xpathContent.getType().equals(type)) {
                        validXpathContent = xpathContent;
                        vaidXpath = xpathContent.getXpath1();
                        break;
                    }
                    if (type.contains(xpathContent.getType()) && vaidXpath == null) {
                        vaidXpath = xpathContent.getXpath1();
                        validXpathContent = xpathContent;
                    }
                }
                if (null == vaidXpath) return;
                Object element = vaidXpath.evaluate(doc);
                if (element == null) {
                    return;
                }
                Object value = null;
                Element element1 = null;
                if (element instanceof Element) {
                    element1 = (Element) element;
                    value = ((Element) element).getTextTrim();
                } else if (element instanceof Attribute) {
                    value = ((Attribute) element).getValue();
                } else if (element instanceof String) {
                    value = (String) element;
                }
                if (value == null && element1 == null) {
                    return;
                }
                if (null == validXpathContent.getFunction() || validXpathContent.getFunction().getClass().isAssignableFrom(DefaultFormatter.class)) {
                    if (classType.equals(" interface org.dom4j.Element")) {
                        value = element1;
                    } else if (classType.equals("class java.lang.Integer")) {
                        value = LeNumberUtils.toInt(value.toString());
                    } else if (classType.equals("class java.lang.Double")) {
                        value = LeNumberUtils.toDouble(value.toString());
                    } else if (classType.equals("class java.lang.Float")) {
                        value = LeNumberUtils.toFloat(value.toString());
                    }
                } else if (null != validXpathContent.getFunction()) {
                    if (classType.equals("interface java.util.Map")) {
                        value = validXpathContent.getFunction().apply(element1);
                    } else {
                        value = validXpathContent.getFunction().apply(value);
                    }
                }
                if (value != null) {
                    field.set(obj, value);
                    return;
                }

            }
        }


        private class ParserImpl<T> implements Parser<T> {
            private final Class<T> clazz;
            private final List<FieldParser> parseableElements;
            private Constructor constructor;

            public ParserImpl(Class<T> clazz, List<FieldParser> parseableElements) {
                this.clazz = clazz;
                this.parseableElements = parseableElements;
                Constructor<?>[] constructors = clazz.getConstructors();
                for (Constructor constructor : constructors) {
                    if (constructor.getParameterTypes().length == 0) {
                        this.constructor = constructor;
                    }
                }
                if (this.constructor == null) {
                    throw new IllegalArgumentException("Class " + clazz.toString() + " used in parser does not have a default constructor");
                }
            }

            private T _parse(String type, Object context) {
                T returnObject;
                try {
                    returnObject = clazz.newInstance();
                    for (FieldParser action : parseableElements) {
                        action.parseElement(type, returnObject, context);
                        Object value = action.field.get(returnObject);
                        if (null != value && value instanceof List && CollectionUtils.isNotEmpty((List) value)) {
                            break;
                        }
                        if (null != value && !(value instanceof List)) {
                            break;
                        }

                    }
                } catch (IllegalAccessException e) {
                    throw new ParserException("Could not access field in object", e);
                } catch (InstantiationException e) {
                    throw new ParserException("Could not create new instance of object", e);
                }
                return returnObject;
            }

            public T parse(String xml) {
                return parse(toInputStream(xml));
            }

            public T parse(String type, Element element) {
                return _parse(type, element);
            }

            public T parse(InputStream stream) {
                try {
                    Document doc = new SAXReader().read(stream);
                    return _parse("", doc);
                } catch (DocumentException e) {
                    throw new ParserException("Could not read XML stream", e);
                }
            }

            public T parse(String annotaionType, InputStream stream) {
                try {
                    Document doc = new SAXReader().read(stream);
                    return _parse(annotaionType, doc);
                } catch (DocumentException e) {
                    throw new ParserException("Could not read XML stream", e);
                }
            }
        }
    }

    private static class ParserImpl<T> implements Parser<T> {
        private final Class<T> clazz;
        private final List<FieldParser> parseableElements;
        private Constructor constructor;

        public ParserImpl(Class<T> clazz, List<FieldParser> parseableElements) {
            this.clazz = clazz;
            this.parseableElements = parseableElements;
            Constructor<?>[] constructors = clazz.getConstructors();
            for (Constructor constructor : constructors) {
                if (constructor.getParameterTypes().length == 0) {
                    this.constructor = constructor;
                }
            }
            if (this.constructor == null) {
                throw new IllegalArgumentException("Class " + clazz.toString() + " used in parser does not have a default constructor");
            }
        }

        private T _parse(String type, Object context) {
            T returnObject;
            try {
                returnObject = clazz.newInstance();
                for (FieldParser action : parseableElements) {

                    action.parseElement(type, returnObject, context);
                    Object value = action.field.get(returnObject);
                }
            } catch (IllegalAccessException e) {
                throw new ParserException("Could not access field in object", e);
            } catch (InstantiationException e) {
                throw new ParserException("Could not create new instance of object", e);
            }
            return returnObject;
        }

        public T parse(String xml) {
            return parse(toInputStream(xml));
        }

        public T parse(String type, Element element) {
            return _parse(type, element);
        }

        public T parse(InputStream stream) {
            try {
                Document doc = new SAXReader().read(stream);
                return _parse("", doc);
            } catch (DocumentException e) {
                throw new ParserException("Could not read XML stream", e);
            }
        }

        public T parse(String type, InputStream stream) {
            try {
                Document doc = new SAXReader().read(stream);
                return _parse(type, doc);
            } catch (DocumentException e) {
                throw new ParserException("Could not read XML stream", e);
            }
        }
    }
}




