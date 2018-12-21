package com.lesports.sms.data.util.JsonPath;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.jayway.jsonpath.JsonPath;
import com.lesports.sms.data.util.JsonPath.annotation.JsonPathContent;
import com.lesports.sms.data.util.JsonPath.annotation.JsonPathQQ;
import com.lesports.sms.data.util.JsonPath.annotation.JsonPathQQ_1;
import com.lesports.sms.data.util.JsonPath.annotation.JsonPathQQ_2;
import com.lesports.utils.math.LeNumberUtils;
import com.lesports.utils.xml.Parser;
import com.lesports.utils.xml.annotation.*;
import com.lesports.utils.xml.exception.ParserException;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.io.IOUtils.toInputStream;

/**
 * lesports-projects
 *
 * @author qiaohx
 * @since 14-11-16
 */
public class JsonParserFactory {
    private static final Logger LOG = LoggerFactory.getLogger(JsonParserFactory.class);

    private final PrimitiveFieldParserFactory primitiveFieldParserFactory;

    public JsonParserFactory() {
        primitiveFieldParserFactory = new PrimitiveFieldParserFactory();
    }

    /**
     * Creates a new parser
     *
     * @param clazz The class to be parsed
     * @param <T>   The class to be parsed
     * @return Parser
     */
    public <T> JsonParser<T> createJsonParser(Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Could not create parser for null class");
        }
        final List<FieldParser> parseableElements = new ArrayList<>();
        for (Field iteratorField : clazz.getDeclaredFields()) {
            final Annotation[] annotation = iteratorField.getAnnotations();
            iteratorField.setAccessible(true);
            FieldParser fieldParser = null;
            if (List.class.isAssignableFrom(iteratorField.getType())) {
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

    //deal with the annotation
    private List<JsonPathContent> getValidAnonationPaths(Annotation[] annotations) {
        List<JsonPathContent> list = Lists.newArrayList();
        for (int index = 0; index < annotations.length; index++) {
            String type = "";
            String jsonPath = null;
            Function function = null;
            if (annotations[index].annotationType().isAssignableFrom(JsonPathQQ.class)) {
                JsonPathQQ data = (JsonPathQQ) annotations[index];
                type = "JsonPathQQ";
                jsonPath = data.value();
                function = getFunctionByFormatter(data.formatter());
            } else if (annotations[index].annotationType().isAssignableFrom(JsonPathQQ_1.class)) {
                JsonPathQQ_1 data = (JsonPathQQ_1) annotations[index];
                type = "JsonPathQQ_1";
                jsonPath = data.value();
                function = getFunctionByFormatter(data.formatter());
            } else if (annotations[index].annotationType().isAssignableFrom(JsonPathQQ_2.class)) {
                JsonPathQQ_2 data = (JsonPathQQ_2) annotations[index];
                type = "JsonPathQQ_2";
                jsonPath = data.value();
                function = getFunctionByFormatter(data.formatter());
            } else {
                continue;
            }
            list.add(new JsonPathContent(type, jsonPath, function));
        }
        return list;
    }


    //create ListParser
    private FieldParser createFieldParsers(Field iteratorField, Annotation[] annotations) {
        final FieldParser fieldParsers = null;
        List<JsonPathContent> list = getValidAnonationPaths(annotations);
        FieldParser fieldParser = primitiveFieldParserFactory.createFieldParser(iteratorField, list, iteratorField.getType().toString());
        return fieldParser;
    }


    private FieldParser createListParsers(Field iteratorField, Annotation[] annotations) {
        final FieldParser fieldParsers = null;
        List<JsonPathContent> list = getValidAnonationPaths(annotations);
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


    private abstract class FieldParser<T> {
        final Field field;
        final List<JsonPathContent> xPath;
        final String classType;

        public abstract void parseElement(String type, T obj, String context) throws IllegalAccessException, ParserException;

        public FieldParser(Field field, List<JsonPathContent> xPath, String classType) {
            this.field = field;
            this.xPath = xPath;
            this.classType = classType;
        }
    }

    private abstract class CollectionsFieldParser<T> extends FieldParser {
        protected JsonParser<T> parser;
        protected FieldParser fieldParser;
        protected final Class<T> elementClazz;

        protected class ObjectWrapper<T> {
            public T object;
        }

        public CollectionsFieldParser(Field field, List<JsonPathContent> xPath, Class<T> elementClazz) {
            super(field, xPath, "");
            this.elementClazz = elementClazz;

            parser = JsonParserFactory.this.createJsonParser(elementClazz);
        }

    }

    private class ListFieldParser<T> extends CollectionsFieldParser<T> {
        public ListFieldParser(Field field, List<JsonPathContent> xPath, Class<T> elementClazz) {
            super(field, xPath, elementClazz);
        }

        @Override
        public void parseElement(String type, Object obj, String doc) throws IllegalAccessException, ParserException {
            ObjectWrapper<T> wrapper = new ObjectWrapper<>();
            List<T> objList = (List<T>) field.get(obj);
            if (objList == null) {
                objList = new ArrayList<>();
                field.set(obj, objList);
            }
            String vaidXpath = null;
            for (int index = 0; index < xPath.size(); index++) {
                JsonPathContent JsonPathContent = (JsonPathContent) xPath.get(index);
                if (JsonPathContent.getPath1() == null) continue;
                if (JsonPathContent.getType().equals(type)) {
                    vaidXpath = JsonPathContent.getPath1();
                    break;
                }
                if (type.contains(JsonPathContent.getType()) && vaidXpath == null)
                    vaidXpath = JsonPathContent.getPath1();
            }
            List<Object> elements = JsonPath.parse(doc).read(vaidXpath);
            if (CollectionUtils.isEmpty(elements)) return;
            for (Object element : elements) {
                if (fieldParser != null) {
                    fieldParser.parseElement("", wrapper, element.toString());
                    objList.add(wrapper.object);
                } else if (parser != null) {
                    if (Map.class.isAssignableFrom(element.getClass()) ) {
                        String toJson = JSONObject.toJSONString(element);
                        T returnObj = parser.parse(type, toJson);
                        objList.add(returnObj);
                    }
                }
            }
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


        public FieldParser createFieldParser(Field field, List<JsonPathContent> xPath, String clazz) {
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
            PrimitiveFieldParser(Field field, List<JsonPathContent> xPath, String classType) {
                super(field, xPath, classType);
            }
        }

        private class StringFieldParser extends PrimitiveFieldParser<String> {
            public StringFieldParser(Field field, List<JsonPathContent> xPath, String classType) {
                super(field, xPath, classType);

            }

            @Override
            public void parseElement(String type, Object obj, String doc) throws IllegalAccessException {
                if (CollectionUtils.isEmpty(xPath)) return;
                JsonPathContent validJsonPathContent = null;
                String vaidXpath = null;
                for (int index = 0; index < xPath.size(); index++) {
                    JsonPathContent JsonPathContent = (JsonPathContent) xPath.get(index);
                    if (JsonPathContent.getPath1() == null) continue;
                    if (JsonPathContent.getType().equals(type)) {
                        validJsonPathContent = JsonPathContent;
                        vaidXpath = JsonPathContent.getPath1();
                        break;
                    }
                    if (type.contains(JsonPathContent.getType()) && vaidXpath == null) {
                        vaidXpath = JsonPathContent.getPath1();
                        validJsonPathContent = JsonPathContent;
                    }
                }
                if (null == vaidXpath) return;
                Object value = JsonPath.parse(doc).read(vaidXpath.toString());
                if (value == null) return;
                if (null == validJsonPathContent.getFunction() || validJsonPathContent.getFunction().getClass().isAssignableFrom(DefaultFormatter.class)) {
                    if (classType.equals("class java.lang.Integer")) {
                        value = LeNumberUtils.toInt(value.toString());
                    } else if (classType.equals("class java.lang.Double")) {
                        value = LeNumberUtils.toDouble(value.toString());
                    } else if (classType.equals("class java.lang.Float")) {
                        value = LeNumberUtils.toFloat(value.toString());
                    }
                } else if (null != validJsonPathContent.getFunction()) {
                    if (classType.equals("interface java.util.Map")) {
                        value = validJsonPathContent.getFunction().apply(value);
                    } else {
                        value = validJsonPathContent.getFunction().apply(value);
                    }
                }
                if (value != null) {
                    field.set(obj, value);
                    return;
                }

            }
        }
    }


    private static class ParserImpl<T> implements JsonParser<T> {
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


        public T parse(String jsonString) {
            return parse(jsonString);
        }


        public T parse(String annotaionType, String json) {
            try {
                return _parse(annotaionType, json);
            } catch (Exception e) {
                throw new ParserException("Could not read json stream", e);
            }
        }

        private T _parse(String type, String jsonString) {
            T returnObject;
            try {
                returnObject = clazz.newInstance();
                for (FieldParser action : parseableElements) {
                    action.parseElement(type, returnObject, jsonString);
                    Object value = action.field.get(returnObject);
                }
            } catch (IllegalAccessException e) {
                throw new ParserException("Could not access field in object", e);
            } catch (InstantiationException e) {
                throw new ParserException("Could not create new instance of object", e);
            }
            return returnObject;
        }
    }
}






