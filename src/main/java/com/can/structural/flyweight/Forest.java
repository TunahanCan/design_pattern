package com.can.structural.flyweight;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Forest {

    private final TreeFactory treeFactory;
    private final List<Tree> trees = new ArrayList<>();
    private final Set<TreeType> usedTreeTypes = new HashSet<>();

    public Forest(TreeFactory treeFactory) {
        this.treeFactory = Objects.requireNonNull(treeFactory, "treeFactory cannot be null");
    }

    public void plantTree(int x, int y, String name, String color, String texture) {
        TreeType treeType = treeFactory.getTreeType(name, color, texture);
        usedTreeTypes.add(treeType);
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

    public int getUsedTreeTypeCount() {
        return usedTreeTypes.size();
    }

    public int getFactoryTreeTypeCount() {
        return getUniqueTreeTypeCount();
    }
}
