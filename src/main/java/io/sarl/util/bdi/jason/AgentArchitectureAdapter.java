package io.sarl.util.bdi.jason;

import io.sarl.lang.core.Agent;
import jason.JasonException;
import jason.architecture.AgArch;
import jason.asSemantics.ActionExec;
import jason.asSemantics.TransitionSystem;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;
import jason.asSyntax.parser.ParseException;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AgentArchitectureAdapter extends AgArch {

	private Agent owner;
	private jason.asSemantics.Agent jasonAgent = null;

	public AgentArchitectureAdapter(Agent owner) {
		this.owner = owner;
		jasonAgent = new jason.asSemantics.Agent();
		new TransitionSystem(jasonAgent, null, null, this);	
	}
	
	public void load(String path) throws JasonException{
		jasonAgent.initAg();
		jasonAgent.load(path);
	}
	
	public void reason(){
		  getTS().reasoningCycle();
	}

	public String getAgName() {
        return owner.getID().toString();
    }
	
	// this method just add some perception for the agent
	@Override
	public List<Literal> perceive() {
		List<Literal> l = new ArrayList<Literal>();
		l.add(Literal.parseLiteral("x(10)"));
		return l;
	}

	// this method get the agent actions
	@Override
	public void act(ActionExec action, List<ActionExec> feedback) {
		getTS().getLogger()
				.info("Agent " + getAgName() + " is doing: "
						+ action.getActionTerm());
		// set that the execution was ok
		action.setResult(true);
		feedback.add(action);
		List<Term> terms = action.getActionTerm().getTerms();
		Class[] termClasses = new Class[terms.size()];
		Object[] args = new Object[terms.size()];
		
		for(int i = 0; i < termClasses.length; i++){
			Term t = terms.get(i);
			termClasses[i]=t.isNumeric()?Integer.class:String.class;
			args[i]=t.isNumeric()?Integer.parseInt(t.toString()):t.toString();
		}
				
				
		Method method;
		try {
			method = owner.getClass().getMethod(action.getActionTerm().getFunctor(), termClasses);
			method.invoke(owner,args);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	@Override
	public boolean canSleep() {
		return true;
	}


	// a very simple implementation of sleep
	// Not a really good approach but good enough for now
	@Override
	public void sleep() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
	}

	// Not used methods
	// This simple agent does not need messages/control/...
	@Override
	public void sendMsg(jason.asSemantics.Message m) throws Exception {
	}

	@Override
	public void broadcast(jason.asSemantics.Message m) throws Exception {
	}

	@Override
	public void checkMail() {
	}

}
