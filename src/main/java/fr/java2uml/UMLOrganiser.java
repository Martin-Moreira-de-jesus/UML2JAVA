package fr.java2uml;

import org.json.JSONArray;

import java.util.ArrayList;

public class UMLOrganiser {

    private int mat_size;
    private JSONArray classes;
    private ArrayList<UMLClassView> classViews;
    private ArrayList<UMLSourceTargetRelation> links;

    public UMLOrganiser(){}

    public JSONArray organizeClasses (UMLDiagram diagram){
        this.mat_size = (diagram.getMyClasses().size()  * 1000) / 5;
        if (diagram.toJson().has("ownedViews")) {
            this.classes = diagram.toJson().getJSONArray("ownedViews");}
        else {
            return null;
        }

        createClassViews();
        createLinks();
        randomDisplay();
        resolveUsingForceDirectedV2();
        exportClassViewValues();


        return this.classes;
    }

    public void createClassViews() {
        this.classViews = new ArrayList<UMLClassView>();
        for (int i = 0; i < this.classes.length(); ++i) {
            if (this.classes.getJSONObject(i).getString("_type").equals("UMLClassView")) {
                UMLClassView classView = new UMLClassView(this.classes.getJSONObject(i).getString("_id"));
                classViews.add(classView);
            }
        }
    };

    public void createLinks(){
        this.links = new ArrayList<UMLSourceTargetRelation>();
        for (int i = 0; i < this.classes.length(); ++i) {
            if (this.classes.getJSONObject(i).getString("_type").equals("UMLGeneralizationView")
                    && this.classes.getJSONObject(i).getString("_type").equals("UMLAssociationView")
                    && this.classes.getJSONObject(i).getString("_type").equals("UMLDependencyView")
                    && this.classes.getJSONObject(i).getString("_type").equals("UMLInterfaceRealizationView")) {
                UMLSourceTargetRelation link = new UMLSourceTargetRelation();
                link.setSource(this.classes.getJSONObject(i).getJSONObject("head").getString("$ref"));
                link.setTarget(this.classes.getJSONObject(i).getJSONObject("tail").getString("$ref"));
                this.links.add(link);
            }
        }
    }
    public void exportClassViewValues(){
        for (int i = 0; i < this.classes.length(); ++i) {
            for (UMLClassView classView : this.classViews){
                if (this.classes.getJSONObject(i).getString("_id").equals(classView.getId())) {
                    this.classes.getJSONObject(i).remove("left");
                    this.classes.getJSONObject(i).put("left", classView.getLeft());

                    this.classes.getJSONObject(i).remove("top");
                    this.classes.getJSONObject(i).put("top", classView.getTop());
                }

            }
        }
    }
    public void randomDisplay (){
        for (UMLClassView classView : this.classViews) {
            double left = 10 + (Math.random() * (this.mat_size - 10));
            classView.setLeft((int)left);
            double top = 10 + (Math.random() * (this.mat_size - 10));
            classView.setTop((int)top);
        }
    }
    public UMLClassView linkIt (String id) {
        for (UMLClassView view : this.classViews){
            if (view.getId().equals(id)) {
                return view;
            }
        }
        return null;
    }

    // Sources des calculs utilisés
    // https://www.labri.fr/perso/bourqui/downloads/cours/Master/2019/TP2/TP_Force_Directed.pdf
    // https://www.youtube.com/watch?v=WWm-g2nLHds
    //Optimisation du diagramme à l'aide de l'algorithme de fruchterman-reingold
    public void resolveUsingForceDirectedV2(){
        double temp = mat_size/10;
        double epsilon = 0.0000001;
        int max_iteration = 700;
        double k =  0.75 * (Math.sqrt((mat_size*mat_size) / this.classViews.size()));
        for (int i = 0; i< max_iteration; ++i){
            for (UMLClassView v : this.classViews){
                for ( UMLClassView u : this.classViews) {
                    if (!u.getId().equals(v.getId())) {
                        double dx = v.getLeft() - u.getLeft();
                        double dy = v.getTop() - u.getTop();

                        double distance = Math.max(epsilon, Math.sqrt((dx*dx) + (dy*dy)));
                        double force = (k*k)/distance;

                        v.setDx(v.getDx() + ((dx/distance)*force));
                        v.setDy(v.getDy() + ((dy/distance) * force));
                    }
                }
            }

            for (UMLSourceTargetRelation link : this.links){
                UMLClassView tempSrc = linkIt(link.getSource());
                UMLClassView tempTarget = linkIt(link.getTarget());

                double dx = tempSrc.getLeft() - tempTarget.getLeft();
                double dy = tempSrc.getTop() - tempTarget.getTop();

                double distance = Math.max(epsilon, Math.sqrt((dx*dx) + (dy*dy)));
                double force = (distance*distance)/k;

                double ForceX = (dx/distance) * force;
                double ForceY = (dy/distance) * force;

                tempSrc.setDx(tempSrc.getDx() - ForceX);
                tempSrc.setDy(tempSrc.getDy() - ForceY);

                tempTarget.setDx(tempTarget.getDx() + ForceX);
                tempTarget.setDy(tempTarget.getDy() + ForceY);
            }

            for (UMLClassView v : this.classViews){
                double distance = Math.max(epsilon, Math.sqrt((v.getDx()*v.getDx()) + (v.getDy()*v.getDy())));

                double ForceX = (v.getDx()/distance) * Math.min(distance, temp);
                double ForceY = (v.getDy()/distance) * Math.min(distance, temp);

                v.setLeft(v.getLeft() + (int)ForceX);
                v.setTop(v.getTop() + (int)ForceY);

                double bordure = this.mat_size / 50.0 ;

                double left = v.getLeft();
                if (left < (0 + bordure)) {
                    left = bordure + Math.random() * bordure * 2.0;
                }else if (left > (mat_size - bordure)){
                    left = mat_size - bordure - Math.random() * bordure *2.0;
                }
                double top = v.getTop();
                if (top < (0 + bordure)) {
                    top = bordure + Math.random() * bordure * 2.0;
                }else if (top > (mat_size - bordure)){
                    top = mat_size - bordure - Math.random() * bordure *2.0;
                }

                v.setLeft((int)left);
                v.setTop((int)top);
            }

            temp = temp * (1.0 - i / (double) max_iteration);
        }

    }


}
