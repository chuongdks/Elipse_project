import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.NodeChangeEvent;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.internal.localstore.Bucket.Visitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class JDT_Test {

	public void run() throws IOException {
		// Read source file content 
		String content = FileUtils.readFileToString(new File("./src/Person.java")); // ContentReader
		
		// Set up the AST Parser
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		// Give the content to the parser
		parser.setSource(content.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		
		// Store method signatures (Key: Method name, Value: Method Signature concatenated together)
	    List<String> methodDeclarations = new ArrayList<>();												// List of Method Declaration and vars declared inside its body			
	    List<String> methodSignature = new ArrayList<>();												// List of Method Declaration and vars declared inside its body				 
	    Map<String, List<String>> methodReceivers = new HashMap<>();
	    Map<String, List<String>> methodArguments = new HashMap<>();
        
		// AST Visitor
		cu.accept(new ASTVisitor() {
			@Override
			public boolean visit(MethodDeclaration node) {													// Method Declaration eg: public void Foo() {}
				// Get Method's name, return type, start and end line		
				String methodName = node.getName().getIdentifier();											// Method's name
		        int currLine = cu.getLineNumber(node.getStartPosition()); 									// Current line number
		        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength()); 				// End line number		       
				
				// List all variables in the method's body
		        List<String> variablesInMethod = new ArrayList<>();
		        for (Object statement : node.getBody().statements()) {										// get all lines ("statement") from the method's body
		            if (statement instanceof VariableDeclarationStatement) {								// check if current line is an instance of var declaration (eg: int x = 10)
		                VariableDeclarationStatement varDecl = (VariableDeclarationStatement) statement;		
		                for (Object varFragment : varDecl.fragments()) {									// return "fragment" inside varDecl (eg: x = 10, y = 10)
		                    VariableDeclarationFragment var = (VariableDeclarationFragment) varFragment;
		                    variablesInMethod.add(var.getName().toString());
		                }
		            }
		        }
		        
		        // Add to the List
		        methodDeclarations.add("Method Declaration: " + methodName + 
		        					   "\nStart Line: " + currLine + 
		        					   "\nEnd Line: " + endLine + 
		        					   "\nVariables declared in method : " + (variablesInMethod.isEmpty() ? "No Variables" : "" + String.join(", ", variablesInMethod)) + 
		        					   "\n");
		                		        
		        return super.visit(node);
			}
			
			@Override
			public boolean visit(MethodInvocation node) {													// Method Invocation eg: Foo() or println("")
				// 
				String methodName = node.getName().getIdentifier();											// Method's name
				int currLine = cu.getLineNumber(node.getStartPosition()); 									// Current line number
				int paramSize = node.arguments().size();
				
		        // Get method arguments 
		        List<String> paramTypes = new ArrayList<>();
		        for (Object arg : node.arguments()) {
		            if (arg instanceof Expression) {														// check argument is an instance of an Expression (eg: it does sthing, a + b)
		                Expression expr = (Expression) arg;
		                paramTypes.add(expr.toString());		
//		                // Don't need this part really, just checking if an argument is valid or not (Got error, check later)
//		                if (expr instanceof NumberLiteral) {
//		                	paramTypes.add(expr.toString());	
//		                } else if (expr instanceof StringLiteral) {
//		                	paramTypes.add(expr.toString());	
//		                } 
//		                // Check for other expression types (e.g., variable or field references)
//		                else if (expr.resolveTypeBinding() != null) {
//		                    paramTypes.add(expr.resolveTypeBinding().getName());  // Add resolved type
//		                } else {
//		                    paramTypes.add("Unknown");  // Handle unresolved types
//		                }
		            }
		        }

		        // Add to the List
		        methodSignature.add("Method Signature: " + methodName + 
		        					"\nStart Line: " + currLine + 
		        					"\nNumber of parameters: " + paramSize + 
		        					"\nParameters list: " + (paramTypes.isEmpty() ? "" : "" + String.join(", ", paramTypes)) + 
		        					"\n");		        
		     
		        
		        /**/
		        // Capture receiver variable if present
		        if (node.getExpression() != null) {
		            String receiver = node.getExpression().toString();
		            methodReceivers.computeIfAbsent(receiver, k -> new ArrayList<>()).add(methodName);
		        }

		        // Track method arguments if the receiver is a variable
		        if (node.getExpression() != null && node.getExpression() instanceof SimpleName) {
		            String receiver = node.getExpression().toString();
		            List<String> methodsUsingReceiver = methodReceivers.get(receiver);
		            if (methodsUsingReceiver != null) {
		                methodArguments.put(methodName, methodsUsingReceiver);
		            }
		        }
		        
				return super.visit(node);
			}
			
		});	
		
		
	    // Testing
		/* Question 1*/
//	    System.out.println("First Question");
//	    for (String declaration : methodDeclarations) {
//	        System.out.println(declaration);	     
//	    }
	    
	    /* Question 2*/
	    System.out.println("Second Question");
	    for (String signature : methodSignature) {
	    	System.out.println(signature);	     
	    }
	    
	    
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JDT_Test driver = new JDT_Test();
		try {
			driver.run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
