package com.can.structural.flyweight;

import java.util.ArrayList;
import java.util.List;

public class Forest {

    private final TreeFactory treeFactory;
    private final List<Tree> trees = new ArrayList<>();

    public Forest(TreeFactory treeFactory) {
        this.treeFactory = treeFactory;
    }

    public void plantTree(int x, int y, String name, String color, String texture) {
        TreeType treeType = treeFactory.getTreeType(name, color, texture);
        trees.add(new Tree(x, y, treeType));
    }

    public List<String> drawAll() {
        return trees.stream().map(Tree::draw).toList();
    }

    public int getTreeCount() {
        return trees.size();
    }

    public int getUniqueTreeTypeCount() {
        return treeFactory.getTreeTypeCount();
    }
}
