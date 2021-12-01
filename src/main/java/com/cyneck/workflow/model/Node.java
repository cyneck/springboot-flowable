package com.cyneck.workflow.model;

import lombok.Data;

import java.util.List;

/**
 * <p>Description: 图顶点</p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/7/9 11:24
 **/
@Data
public class Node {

    private String id;
    private String name;
    private String color;
    private Node parent;
    private int discoveryTime;
    private int finishTime;
    private List<Node> inComingFLows;
    private List<Node> outGoingFLows;

    public Node() {
        this.color = "white";
    }

}
