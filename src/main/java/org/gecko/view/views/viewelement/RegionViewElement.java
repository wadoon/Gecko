package org.gecko.view.views.viewelement;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import org.gecko.viewmodel.RegionViewModel;

public class RegionViewElement extends Rectangle implements ViewElement<RegionViewModel>{

    @Override
    public Node drawElement() {
        return null;
    }

    @Override
    public RegionViewModel getTarget() {
        return null;
    }

    @Override
    public Point2D getPosition() {
        return null;
    }

    @Override
    public void bindTo(RegionViewModel target) {

    }

    @Override
    public void accept(ViewElementVisitor visitor) {

    }
}
