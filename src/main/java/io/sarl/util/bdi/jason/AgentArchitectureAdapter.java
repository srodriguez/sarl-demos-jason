package io.sarl.util.bdi.jason;


import io.sarl.lang.core.Address;
import io.sarl.lang.core.Agent;
import io.sarl.lang.core.Event;
import io.sarl.util.AddressScope;
import jason.JasonException;
import jason.architecture.AgArch;
import jason.asSemantics.ActionExec;
import jason.asSemantics.Circumstance;
import jason.asSemantics.Message;
import jason.asSemantics.TransitionSystem;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import com.google.common.base.CaseFormat;

/**
 * Jason Agent Architecture adapter for SARL agents.
 *
 * @author $Author: srodriguez$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class AgentArchitectureAdapter extends AgArch {

	private Logger log = Logger.getLogger(AgentArchitectureAdapter.class.getSimpleName());
	private Agent owner;
	private JasonAgentSpeakReasonerSkill skill;
	private jason.asSemantics.Agent jasonAgent = null;
	private BlockingQueue<Literal> events = new LinkedBlockingQueue<Literal>(100);
	private BlockingQueue<Message> messages = new LinkedBlockingQueue<Message>(100);

	public AgentArchitectureAdapter(Agent owner, JasonAgentSpeakReasonerSkill skill) {
		this.owner = owner;
		this.skill = skill;
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

	public void enqueueBelief(String belief) throws Exception {

		Literal beliefLietral = Literal.parseLiteral(belief);
		log.info("Parsing belief String " + belief+" - Obtained literal : "+beliefLietral.toString() );
		events.put(beliefLietral);
	}

	public void enqueueMessage(KQMLMessage msg) throws Exception {
		messages.put(msg.message);
	}

	// this method just add some perception for the agent
	@Override
	public List<Literal> perceive() {

		List<Literal> ps = new ArrayList<Literal>();
		for(int i = 0 ; i < 10 && !events.isEmpty(); i++){
			try {
				ps.add(events.take());
			} catch (InterruptedException e) {
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
				.fine("Agent " + getAgName() + " requesting action : "
						+ action.getActionTerm());
		// set that the execution was ok
		action.setResult(true);
		feedback.add(action);
		List<Term> terms = action.getActionTerm().getTerms();
		Class[] termClasses = new Class[terms.size()];
		Object[] args = new Object[terms.size()];

		for (int i = 0; i < termClasses.length; i++) {
			Term t = terms.get(i);
			Object arg = ASUtils.termToObject(t);
			termClasses[i] = arg.getClass();
			args[i] = arg;
		}

		try {
			Method method = owner.getClass().getDeclaredMethod(
					action.getActionTerm().getFunctor(), termClasses);
			method.setAccessible(true);//make the method accessible if protected or package
			method.invoke(owner, args);
		} catch (Exception e) {
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
		KQMLMessage kqml = new KQMLMessage();
		kqml.message = m;
		Address aa = skill.getDefaultSpace().getAddress(UUID.fromString(m.getReceiver()));
		log.fine("Sending to "+aa+" KQMLMessage :"+kqml);
		skill.emit(kqml, AddressScope.getScope(aa));
	}

	@Override
	public void broadcast(jason.asSemantics.Message m) throws Exception {
		KQMLMessage kqml = new KQMLMessage();
		kqml.message = m;
		skill.emit(kqml);
	}

	@Override
	public void checkMail() {
        Circumstance C = getTS().getC();
        Message im = messages.poll();
        while (im != null) {
            C.addMsg(im);
            log.fine("received message: " + im);
            im = messages.poll();
        }
	}


	protected Literal toLiteral(Event e) throws Exception {
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
				fieldsString = fieldsString + ASUtils.objectToTerm(fields[i].get(e)).toString();
				if (i < (fields.length-2)) { //-2 since serialVersionUID will not be included
					fieldsString = fieldsString + ",";
				}
			}
		}

		if (!fieldsString.isEmpty()) {
			literalString = literalString + "(" + fieldsString + ")";
		}
		log.finest("Generated lieteral string to parse: "+literalString);
		Literal l = Literal.parseLiteral(literalString);
		log.finest("Parsed Literal toString: "+l.toString());
		return l;
	}



}
