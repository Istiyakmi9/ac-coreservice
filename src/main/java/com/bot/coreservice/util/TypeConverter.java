package com.bot.coreservice.util;

import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TypeConverter<T> {
    private final Class<T> targetType;
    private final SimpleDateFormat dateFormat;

    public TypeConverter(Class<T> targetType) {
        this.targetType = targetType;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    public TypeConverter(Class<T> targetType, String dateFormat) {
        this.targetType = targetType;
        this.dateFormat = new SimpleDateFormat(dateFormat);
    }

    public List<T> convert(List<Map<String, Object>> mapList) throws Exception {
        List<T> resultList = new ArrayList<>();

        for (Map<String, Object> map : mapList) {
            T obj = targetType.getDeclaredConstructor().newInstance();
            populateObjectFromMap(obj, map, dateFormat);
            resultList.add(obj);
        }

        return resultList;
    }

    private T createInstance() throws Exception {
        Constructor<T> constructor = targetType.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    private Method getSetterMethod(String fieldName, Class<?> fieldType) throws NoSuchMethodException {
        String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        return targetType.getMethod(setterName, fieldType);
    }

    private void populateObjectFromMap(T obj, Map<String, Object> map, SimpleDateFormat dateFormat) throws Exception {
        try {
            for (Field field : targetType.getDeclaredFields()) {
                String fieldName = field.getName();
                if (map.containsKey(fieldName)) {
                    Object value = map.get(fieldName);

                    if (value == null) {
                        // Handle null values
                        Method method = getSetterMethod(field.getName(), field.getType());
                        method.invoke(obj, (Object) null);
                    } else {
                        // Perform type casting if necessary
                        if (field.getType() == Integer.class || field.getType() == int.class) {
                            Method method = getSetterMethod(field.getName(), field.getType());
                            if (value instanceof Integer) {
                                method.invoke(obj, value);
                            } else {
                                method.invoke(obj, Integer.parseInt(value.toString()));
                            }
                        } else if (field.getType() == String.class) {
                            Method method = getSetterMethod(field.getName(), field.getType());
                            if (value instanceof String) {
                                method.invoke(obj, value);
                            } else {
                                method.invoke(obj, value.toString());
                            }
                        } else if (field.getType() == Float.class) {
                            Method method = getSetterMethod(field.getName(), field.getType());
                            if (value instanceof Float) {
                                method.invoke(obj, value);
                            } else {
                                method.invoke(obj, Float.parseFloat(value.toString()));
                            }
                        } else if (field.getType() == Boolean.class) {
                            Method method = getSetterMethod(field.getName(), field.getType());
                            if (value instanceof Boolean) {
                                method.invoke(obj, value);
                            } else {
                                if (value.toString().equals("1")) {
                                    method.invoke(obj, true);
                                } else {
                                    method.invoke(obj, false);
                                }
                            }
                            method.invoke(obj, value);
                        } else if (field.getType() == Date.class) {
                            if (value instanceof Date) {
                                Method method = getSetterMethod(field.getName(), field.getType());
                                method.invoke(obj, value);
                            } else if (value instanceof String) {
                                try {
                                    Date date = this.dateFormat.parse((String) value);
                                    Method method = getSetterMethod(field.getName(), field.getType());
                                    method.invoke(obj, date);
                                } catch (java.text.ParseException e) {
                                    // Handle parsing error
                                    Method method = getSetterMethod(field.getName(), field.getType());
                                    method.invoke(obj, (Object) null);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
