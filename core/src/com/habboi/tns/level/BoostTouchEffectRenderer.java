package com.habboi.tns;

public class BoostTouchEffectRenderer implements TouchEffectRenderer {
    Cell cell;
    ModelInstance arrowInstance;

    public void init(Cell cell) {
        this.cell = cell;

        arrowInstance = new ModelInstance(Models.getFloorArrowModel());
        arrowInstance.transform.setTranslation();
    }
}
