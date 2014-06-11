package rest;

import io.vertigo.kernel.lang.Assertion;
import io.vertigo.kernel.lang.MessageText;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Implémentation Standard de la gestion des erreurs d'un processus métier.
 *
 * @author plepaisant, pchretien, jcassignol
 * @version $Id: ErrorProcessImpl.java,v 1.16 2011/05/10 15:03:49 dchallas Exp $
 */
public final class ErrorProcessImpl implements ErrorProcess {
	//
	//	private static final long serialVersionUID = 6810787915884791702L;
	//
	//	/**
	//	 * DTO en erreur identifiés par leur nom dans le contexte
	//	 */
	//	private final Map<DtObjectInput<?>, ErrorDtObject> dtoErrorStackMap = new HashMap<DtObjectInput<?>, ErrorDtObject>();
	//
	//	/**
	//	 * DTC en erreur identifiés par leur nom dans le contexte
	//	 */
	//	private final Map<String, ErrorDtCollection> dtcErrorStackMap = new HashMap<String, ErrorDtCollection>();

	/**
	 * Pile des erreurs simples
	 */
	private final Collection<MessageText> stackErrorSimple = new ArrayList<>();

	/** {@inheritDoc} */
	public void registerErrorSimple(final MessageText message) {
		Assertion.checkNotNull(message);
		// ----------------------------------------------------------------------
		stackErrorSimple.add(message);
	}

	/** {@inheritDoc} */
	public Collection<MessageText> getErrorSimpleMessage() {
		return stackErrorSimple;
	}

	//
	//	/** {@inheritDoc} */
	//	public void registerErrorDtObject(final DtObjectInput<?> dtoInput, final ErrorDtObject errors) {
	//		Assertion.precondition(!dtoErrorStackMap.containsKey(dtoInput), "Le dto est déjà enregistré dans la pile d'erreur");
	//		// ----------------------------------------------------------------------
	//		//PFO: Le test sur l'erreur est retiré, si on veut enregistrer, c'est qu'on veut enregistrer
	//		// La présence d'erreur est vérifiée ailleurs.
	//		if (dtoInput.getRow() == null) {
	//			dtoErrorStackMap.put(dtoInput, errors);
	//		} else {
	//			registerErrorDtObject(dtoInput.getKey(), dtoInput.getRow(), errors);
	//		}
	//	}
	//
	//	private void registerErrorDtObject(final String dtcId, final int row, final ErrorDtObject errors) {
	//		ErrorDtCollection errorDtc = dtcErrorStackMap.get(dtcId);
	//		if (errorDtc == null) {
	//			errorDtc = new ErrorDtCollectionImpl();
	//			dtcErrorStackMap.put(dtcId, errorDtc);
	//		}
	//		Assertion.precondition(!errorDtc.hasErrorDtObject(row), "Le dto est déjà enregistré dans la pile d'erreur");
	//		errorDtc.setErrorDtObject(row, errors);
	//	}
	//
	//	/** {@inheritDoc} */
	//	public ErrorDtObject getErrorDtObject(final DtObjectInput<?> dtoId) {
	//		final ErrorDtObject errorDtObject;
	//		if (dtoId.getRow() != null && dtcErrorStackMap.containsKey(dtoId.getKey())) {
	//			final ErrorDtCollection errorDtCollection = dtcErrorStackMap.get(dtoId.getKey());
	//			errorDtObject = errorDtCollection.getErrorDtObject(dtoId.getRow());
	//		} else {
	//			errorDtObject = dtoErrorStackMap.get(dtoId);
	//		}
	//		return errorDtObject;
	//	}
	//
	//	/** {@inheritDoc} */
	//	public ErrorDtCollection obtainErrorDtCollection(final String dtcId) {
	//		ErrorDtCollection errorDtc = dtcErrorStackMap.get(dtcId);
	//		if (errorDtc == null) {
	//			errorDtc = new ErrorDtCollectionImpl();
	//			dtcErrorStackMap.put(dtcId, errorDtc);
	//		}
	//		return errorDtc;
	//	}
	//
	//	/** {@inheritDoc} */
	//	public Set<DtObjectInput<?>> getDtObjectErrorSet() {
	//		return dtoErrorStackMap.keySet();
	//	}
	//
	//	/** {@inheritDoc} */
	//	public Set<String> getDtCollectionErrorSet() {
	//		return dtcErrorStackMap.keySet();
	//	}
	//
	//	/** {@inheritDoc} */
	//	public boolean hasError(final DtInput<?> dtInput) {
	//		if (dtInput instanceof DtCollectionInput) {
	//			final ErrorDtCollection errorDtCollection = dtcErrorStackMap.get(dtInput.getKey());
	//			return errorDtCollection != null && errorDtCollection.hasError();
	//		}
	//		final DtObjectInput<?> dtObjectInput = (DtObjectInput<?>) dtInput;
	//		final ErrorDtObject errorDtObject;
	//		if (dtObjectInput.getRow() != null && dtcErrorStackMap.containsKey(dtInput.getKey())) {
	//			final ErrorDtCollection errorDtCollection = dtcErrorStackMap.get(dtInput.getKey());
	//			errorDtObject = errorDtCollection.getErrorDtObject(dtObjectInput.getRow());
	//		} else {
	//			errorDtObject = dtoErrorStackMap.get(dtObjectInput);
	//		}
	//		return errorDtObject != null && errorDtObject.hasError();
	//	}
	//
	//	/** {@inheritDoc} */
	//	public boolean hasError() {
	//		for (final ErrorDtObject errorDtObject : dtoErrorStackMap.values()) {
	//			if (errorDtObject.hasError()) {
	//				// Il suffit qu'une structure ne soit pas vide
	//				return true;
	//			}
	//		}
	//		for (final ErrorDtCollection errorDtCollection : dtcErrorStackMap.values()) {
	//			if (errorDtCollection.hasError()) {
	//				// Il suffit qu'une structure ne soit pas vide
	//				return true;
	//			}
	//		}
	//		return !stackErrorSimple.isEmpty();
	//	}

	/** {@inheritDoc} */
	public void clearErrors() {
		//		dtoErrorStackMap.clear();
		//		dtcErrorStackMap.clear();
		stackErrorSimple.clear();
	}
}
