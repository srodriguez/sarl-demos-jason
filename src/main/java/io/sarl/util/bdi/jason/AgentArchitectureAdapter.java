package io.sarl.util.bdi.jason;

import io.sarl.demos.jason.KQMLMessage;
import io.sarl.lang.core.Agent;
import io.sarl.lang.core.Event;
import jason.JasonException;
import jason.architecture.AgArch;
import jason.asSemantics.ActionExec;
import jason.asSemantics.Message;
import jason.asSemantics.TransitionSystem;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import com.google.common.base.CaseFormat;

public class AgentArchitectureAdapter extends AgArch {

	private Logger log = Logger.getLogger(AgentArchitectureAdapter.class.getSimpleName());
	private Agent owner;
	private jason.asSemantics.Agent jasonAgent = null;
	private BlockingQueue<Literal> events = new LinkedBlockingQueue<Literal>(100);
	private BlockingQueue<Message> messages = new LinkedBlockingQueue<Message>(100);

	public AgentArchitectureAdapter(Agent owner) {
		this.owner = owner;
		jasonAgent = new jason.asSemantics.Agent();
		new TransitionSystem(jasonAgent, null, null, this);
	}

	public void load(String path) throws JasonException {
		jasonAgent.initAg();
		jasonAgent.load(path);
	}

	public void reason() {
		getTS().reasoningCycle();
	}

	public String getAgName() {
		return owner.getID().toString();
	}

	public void enqueuePerception(Event e) throws Exception {
		events.put(toLiteral(e));
		
	}

	public void enqueueMessage(KQMLMessage msg) {
		// messages.add(msg.getMessage());
	}

	// this method just add some perception for the agent
	@Override
	public List<Literal> perceive() {
		
		List<Literal> ps = new ArrayList<Literal>();
		for(int i = 0 ; i < 10 && !events.isEmpty(); i++){
			try {
				ps.add(events.take());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		log.exiting(getClass().toString(), "percieve", ps);
		return ps;
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

		for (int i = 0; i < termClasses.length; i++) {
			Term t = terms.get(i);
			
			termClasses[i] = t.isNumeric() ? Integer.class : String.class;
			args[i] = t.isNumeric() ? Integer.parseInt(t.toString()) : t
					.toString();
		}

		try {
			Method method = owner.getClass().getMethod(
					action.getActionTerm().getFunctor(), termClasses);
			method.invoke(owner, args);
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

	protected Literal toLiteral(Event e) throws IllegalArgumentException,
			IllegalAccessException {
		Class clazz = e.getClass();
		String literalString = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, clazz.getSimpleName());
		
		String fieldsString = "";

		Field[] fields = e.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].getName() != "serialVersionUID") {
				log.finest("Field : " + fieldsString + fields[i].getName()
						+ "-  Value = " + fields[i].get(e));
				fields[i].setAccessible(true); // You might want to set modifier
												// to public first.
				fieldsString = fieldsString + fields[i].get(e).toString();
				if (i < (fields.length-2)) { //-2 since serialVersionUID will not be included
					fieldsString = fieldsString + ",";
				}
			}
		}

		if (!fieldsString.isEmpty()) {
			literalString = literalString + "(" + fieldsString + ")";
		}
		log.finest(literalString);
		Literal l = Literal.parseLiteral(literalString);
		log.finest("literal toString: "+l.toString());
		return l;
	}

	

}
