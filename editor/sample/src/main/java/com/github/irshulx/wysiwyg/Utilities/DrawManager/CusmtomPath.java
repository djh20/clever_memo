package com.github.irshulx.wysiwyg.Utilities.DrawManager;

import android.graphics.Path;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class CusmtomPath extends Path implements Serializable {

    //    Vector<Float> x;
//    Vector<Float> y;
    int color;
    float stroke;
    private ArrayList<PathAction> actions = new ArrayList<PathAction>();

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getStroke() {
        return stroke;
    }

    public void setStroke(float stroke) {
        this.stroke = stroke;
    }

    public ArrayList<PathAction> getActions() {
        return actions;
    }

    public void setActions(ArrayList<PathAction> actions) {
        this.actions = actions;
    }

    public CusmtomPath() {
        super();
//        x = new Vector<Float>();
//        y = new Vector<Float>();
    }

    CusmtomPath(CusmtomPath cusmtomPath) {
        super(cusmtomPath);
//        this.x = cusmtomPath.x;
//        this.y = cusmtomPath.y;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        drawThisPath();
    }


    @Override
    public void quadTo(float x1, float y1, float x2, float y2) {
        actions.add(new ActionQuad(x1, y1, x2, y2));
        super.quadTo(x1, y1, x2, y2);
    }

    @Override
    public void moveTo(float x, float y) {
        actions.add(new ActionMove(x, y));
        super.moveTo(x, y);
    }

    @Override
    public void lineTo(float x, float y) {
        actions.add(new ActionLine(x, y));
        super.lineTo(x, y);
    }


    private void drawThisPath() {
        for (PathAction p : actions) {
            if (p.getType().equals(PathAction.PathActionType.MOVE_TO)) {
                super.moveTo(p.getX(), p.getY());
            } else if (p.getType().equals(PathAction.PathActionType.LINE_TO)) {
                super.lineTo(p.getX(), p.getY());
            } else if (p.getType().equals(PathAction.PathActionType.QUAD_TO)) {
                ActionQuad aq = (ActionQuad) p;
                super.quadTo(aq.getX1(), aq.getY1(), aq.getX2(), aq.getY2());
            }
        }
    }

    public interface PathAction {
        public enum PathActionType {LINE_TO, MOVE_TO, QUAD_TO}

        ;

        public PathActionType getType();

        public float getX();

        public float getY();
    }

    public class ActionMove implements PathAction, Serializable {
        private static final long serialVersionUID = -7198142191254133295L;

        private float x, y;

        public ActionMove(float x, float y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public PathActionType getType() {
            return PathActionType.MOVE_TO;
        }

        @Override
        public float getX() {
            return x;
        }

        @Override
        public float getY() {
            return y;
        }

    }

    public class ActionLine implements PathAction, Serializable {
        private static final long serialVersionUID = 8307137961494172589L;

        private float x, y;

        public ActionLine(float x, float y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public PathActionType getType() {
            return PathActionType.LINE_TO;
        }

        @Override
        public float getX() {
            return x;
        }

        @Override
        public float getY() {
            return y;
        }

    }

    public class ActionQuad implements PathAction, Serializable {
        private static final long serialVersionUID = 8307137961494172589L;

        private float x1, y1, x2, y2;

        public ActionQuad(float x1, float y1, float x2, float y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        @Override
        public PathActionType getType() {
            return PathActionType.QUAD_TO;
        }

        @Override
        public float getX() {
            return 0;
        }

        @Override
        public float getY() {
            return 0;
        }

        public float getX1() {
            return x1;
        }

        public void setX1(float x1) {
            this.x1 = x1;
        }

        public float getY1() {
            return y1;
        }

        public void setY1(float y1) {
            this.y1 = y1;
        }

        public float getX2() {
            return x2;
        }

        public void setX2(float x2) {
            this.x2 = x2;
        }

        public float getY2() {
            return y2;
        }

        public void setY2(float y2) {
            this.y2 = y2;
        }
    }
}
