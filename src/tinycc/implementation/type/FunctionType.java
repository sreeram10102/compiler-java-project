package tinycc.implementation.type;

import java.util.List;

public class  FunctionType extends Type{

    private Type returnType;
    private List<Type> parameters;
 
 
    public   FunctionType(Type returnType,List<Type> parameters){
       this.returnType =returnType;
       this.parameters = parameters;
    }


    @Override
    public String toString() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'toString'");
    }
}