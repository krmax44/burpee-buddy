package com.apps.adrcotfas.burpeebuddy.db.exercisetype;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class MetricConverter {

    @TypeConverter
    public String fromListOfString(List<String> optionValues) {
        if (optionValues == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {
        }.getType();
        return gson.toJson(optionValues, type);
    }

    @TypeConverter
    public List<String> toListOfString(String valuesString) {
        if (valuesString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {
        }.getType();
        return gson.fromJson(valuesString, type);
    }
}
