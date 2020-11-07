package com.gm910.silvania.magic;

import com.gm910.silvania.api.language.Translate;
import com.gm910.silvania.api.util.ServerPos;
import com.gm910.silvania.magic.data.SpellDataType;

public class SpellException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1680822204290287093L;

	private SpellPart part;
	private ServerPos pos;

	public SpellException(SpellPart part, String message) {
		super(message);
	}

	public ServerPos getPos() {
		return pos;
	}

	public void setPos(ServerPos pos) {
		this.pos = pos;
	}

	public SpellPart getPart() {
		return part;
	}

	public void setPart(SpellPart part) {
		this.part = part;
	}

	public static class NullValueSpellException extends SpellException {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2216328721182067374L;

		public NullValueSpellException(SpellPart part) {
			super(part, Translate.translate("null.value", SpellDataType.ENTITY_CONDITION.getDisplayText()));
		}

	}

}
