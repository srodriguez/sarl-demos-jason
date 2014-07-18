package io.sarl.util.bdi.jason;

import jason.NoValueException;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Atom;
import jason.asSyntax.ListTerm;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.StringTerm;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import jason.asSyntax.VarTerm;

import java.util.Map;

/**
 * Conversion Utils.
 *
 * see c4jason.JavaLibrary for original code.
 *
 * @author $Author: srodriguez$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ASUtils {

	public static Object termToObject(Term t) {
		// System.out.println(">> "+t);
		if (t instanceof VarTerm) {
			return null;// should not happen! termToObject(var.getValue());
		} else if (t.isAtom()) {
			Atom t2 = (Atom) t;
			if (t2.equals(Atom.LTrue)) {
				return Boolean.TRUE;
			} else if (t2.equals(Atom.LFalse)) {
				return Boolean.FALSE;
			} else {
				return t2.getFunctor();
			}
		} else if (t.isNumeric()) {
			NumberTerm nt = (NumberTerm) t;
			double d = 0;
			try {
				d = nt.solve();
			} catch (NoValueException e) {
				e.printStackTrace();
			}
			 if (((int) d) == d) {
				return (int) d;
			} else if (((float) d) == d) {
				return (float) d;
			} else if (((long) d) == d) {
				return (long) d;
			} else if (((byte) d) == d) {
				return (byte) d;
			} else {
				return d;
			}
		} else if (t.isString()) {
			// System.out.println("STRING "+t);
			return ((StringTerm) t).getString(); // (t.toString()).substring(1,t.toString().length()-1);
		} else if (t.isList()) {
			// System.out.println("LIST "+t);
			ListTerm lt = (ListTerm) t;
			int i = 0;
			Object[] list = new Object[lt.size()];
			for (Term t1 : lt) {
				list[i++] = termToObject(t1);
			}
			return list;
		} else {
			return t.toString();
		}
	}

	/**
	 * Convert Java Object into a Jason term
	 */
	public static Term objectToTerm(Object value) throws Exception {
		if (value == null) {
			return ASSyntax.createVar("_");
		} else if (value instanceof Term) {
			return (Term) value;
		} else if (value instanceof Number) {
			try {
				return ASSyntax.parseNumber(value.toString());
			} catch (Exception ex) {
				return ASSyntax.createString(value.toString());
			}
		} else if (value instanceof String) {
			return ASSyntax.createString(value.toString());
		} else if (value instanceof Boolean) {
			return ((Boolean) value).booleanValue() ? Literal.LTrue
					: Literal.LFalse;
			// Tricky solution that try to avoiding a "possible" Java bug in
			// converting
			// an array to a collection Arrays.asList(value);
		} else if (value.getClass().isArray()) {
			ListTerm l = new ListTermImpl();
			ListTerm tail = l;
			int lenght = java.lang.reflect.Array.getLength(value);
			for (int i = 0; i < lenght; i++)
				tail = tail.append(objectToTerm(java.lang.reflect.Array.get(
						value, i)));
			return l;
		} else if (value instanceof Map) {
			ListTerm l = new ListTermImpl();
			ListTerm tail = l;
			Map m = (Map) value;
			for (Object k : m.keySet()) {
				Term pair = ASSyntax.createStructure("map", objectToTerm(k),
						objectToTerm(m.get(k)));
				tail = tail.append(pair);
			}
			return l;
		}
		//Seems to be Cartago specific code
//		else if (value instanceof ToProlog) {
//			return ASSyntax.parseTerm(((ToProlog) value).getAsPrologStr());
//			// Collection conversion: intentionally not supported yet
//			/*
//			 * } else if (value instanceof Collection) { ListTerm l = new
//			 * ListTermImpl(); ListTerm tail = l; for (Object e:
//			 * (Collection)value) tail = tail.append(objectToTerm(e)); return l;
//			 */
//		} else if (value instanceof Tuple) {
//			Tuple  t = (Tuple) value;
//			Structure st = ASSyntax.createStructure(t.getLabel());
//			for (int i = 0; i < t.getNArgs(); i++) {
//				st.addTerm(objectToTerm(t.getContent(i)));
//			}
//			return st;
//		}
//		return registerDynamic(value);
		//try a string conversion
		return objectToTerm(value.toString());
		//throw new IllegalArgumentException("Can not transform "+value.toString()+ " to Term");
	}

	public static Term[] objectArray2termArray(Object[] values) throws Exception {
		Term[] result = new Term[values.length];
		for (int i = 0; i < values.length; i++)
			result[i] = objectToTerm(values[i]);
		return result;
	}
}
