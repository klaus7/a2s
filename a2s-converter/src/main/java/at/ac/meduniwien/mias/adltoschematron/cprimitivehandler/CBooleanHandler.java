package at.ac.meduniwien.mias.adltoschematron.cprimitivehandler;

import org.openehr.rm.datatypes.basic.DvBoolean;

import at.ac.meduniwien.mias.adltoschematron.TreeElement;
import at.ac.meduniwien.mias.adltoschematron.helpers.Utils;

public class CBooleanHandler extends AbstractCPrimitiveHandler {

	public CBooleanHandler(final TreeElement te) {
		super(te);
	}

	@Override
	public void generateTests() {

		super.generateTests();

	}

	@Override
	public String wrapValue(final Object value) {
		DvBoolean dv = (DvBoolean) value;

		if (dv.getValue()) {
			return Utils.quote("true");
		} else {
			return Utils.quote("false");
		}
	}

}
