package fr.java2uml;

public class UMLClassView {
    private String id;
    private double dx;
    private double dy;
    int top;
    int left;
    public UMLClassView(String id) {
        this.id = id;
        this.dx = 0;
        this.dy = 0;
        this.left = 0;
        this.top = 0;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getDx() {
        return dx;
    }

    public void setDx(double dx) {
        this.dx = dx;
    }

    public double getDy() {
        return dy;
    }

    public void setDy(double dy) {
        this.dy = dy;
    }
}
