package com.thirtyonetensoftware.android.powergridengineer.util;

import java.util.HashMap;
import java.util.Set;

import android.content.Context;

import com.thirtyonetensoftware.android.powergridengineer.database.DBHelper;
import com.thirtyonetensoftware.android.powergridengineer.model.City;
import com.thirtyonetensoftware.android.powergridengineer.model.Path;

/**
 * Graph
 * <p/>
 * Power Grid Engineer
 * 31Ten Software
 * <p/>
 * Author: Josh Kendrick
 * <p/>
 * Since the portion of the graph that is available can change from game to game, based on which
 * country is being played and the colored areas that have been chosen, the graph is rebuilt each
 * time the algorithm is run.
 */
public enum Graph {

    INSTANCE;

    private static HashMap<City, Set<Path>> graph = new HashMap<>();

    public static void buildGraph(Context context) {
        graph = new HashMap<>();

        DBHelper dbHelper = new DBHelper(context);
        try {
            dbHelper.openReadableDatabase();
            for ( City city : dbHelper.getCities() ) {
                graph.put(city, dbHelper.getPathsFromCity(city));
            }
        }
        finally {
            dbHelper.close();
        }
    }

    public static HashMap<City, Set<Path>> getGraph() {
        return new HashMap<>(graph);
    }
}
