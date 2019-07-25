package com.liuzg.def;


public class DefUtils {
    public static ConstructorInstance decorate(ConstructorDefinition decoratorConstructorDefinition, Instance target) {
        ConstructorInstance constructorInstance = new ConstructorInstance(decoratorConstructorDefinition);
        constructorInstance.setProperty(decoratorConstructorDefinition.decoratorProperty, target);
        return constructorInstance;
    }
}
