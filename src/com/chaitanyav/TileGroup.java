package com.chaitanyav;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Chaitanya V
 */
public class TileGroup {
    TreeMap<Integer,ArrayList<Integer>> tiles = new TreeMap<>();
    public void add(int i,int j){
        tiles.putIfAbsent(i, new ArrayList<>());
        tiles.get(i).add(j);
    }
    public boolean hasMoreRows(){
        return tiles.size()>0;
    }
    public Row popNextRow(){
        Map.Entry<Integer, ArrayList<Integer>> e = tiles.pollFirstEntry();
        return new Row(e.getKey(),e.getValue());
    }
    
    
    static class Row{
        int rowIndex=0,ptr=0;
        ArrayList<Integer> tilesIndices = new ArrayList<>();
        public Row(int ri,ArrayList<Integer> ti){rowIndex=ri;tilesIndices=ti;}
        public int getRowIndex(){return rowIndex;}
        public boolean hasMoreTiles(){return tilesIndices.size()-ptr>0;}
        public int nextTile(){return tilesIndices.get(ptr++);}
    }
}
