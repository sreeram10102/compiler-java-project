

package tinycc.implementation.statement;
import java.util.Iterator;
import java.util.List;

import tinycc.diagnostic.Locatable;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.statement.Statement;

public class BlockStatement extends Statement{
    private Locatable loc;
    private List<Statement> statements;
    public BlockStatement(Locatable loc, List<Statement> statements){
     this.loc=loc;
     this.statements =statements;
 
    }
 @Override
 public String toString() {
    int i =0;
    StringBuilder build = new StringBuilder();
    Statement[] size = new Statement[statements.size()];
     Iterator<Statement> iterator = statements.iterator();
     build.append("Block[");
     if(iterator.hasNext()){
        while(iterator.hasNext()){
            size[i]=iterator.next();
            build.append(size[i]);

            if(!iterator.hasNext()){
                break;
            }
            build.append(",");
            i++;
         }
         build.append("]");
     
     }
     else{
        build.append("Block[Block[]]");
     }
     return build.toString();
 }
}