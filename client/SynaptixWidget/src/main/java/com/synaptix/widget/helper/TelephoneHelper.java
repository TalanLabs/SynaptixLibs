package com.synaptix.widget.helper;

import com.jgoodies.validation.util.ValidationUtils;

public final class TelephoneHelper {

	private TelephoneHelper() {
	}

	/**
	 * Cette méthode prend un numéro de téléphone en paramètre et vérifie qu'il
	 * s'agisse d'un numéro au format internationnal . Formats reconnus : belge,
	 * français, italien, allemand et hollandais
	 * 
	 * @param String
	 * @return boolean
	 */
	public static final boolean checkInternationalTelephone(String telephone) {
		boolean res = false;
		if (ValidationUtils.isNotBlank(telephone)) {
			if (telephone.startsWith("+")) { //$NON-NLS-1$
				telephone = telephone.replaceFirst("\\+", "00"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			if (ValidationUtils.isNumeric(telephone)) {
				if (checkFrance(telephone) || checkAllemange(telephone)
						|| checkBelgique(telephone) || checkItalie(telephone)
						|| checkPaysBas(telephone)) {
					res = true;
				}
			}
		}
		return res;
	}

	/**
	 * Cette méthode prend un numéro de téléphone en paramètre et vérifie qu'il
	 * s'agisse d'un numéro belge au format internationnal.
	 * 
	 * @param String
	 * @return boolean
	 */
	private static final boolean checkBelgique(String telephone) {
		if ((telephone.startsWith("00324") && telephone.length() == 13) //$NON-NLS-1$
				|| (telephone.startsWith("0032") && telephone.length() == 12)) { //$NON-NLS-1$
			return true;
		}
		return false;
	}

	/**
	 * Cette méthode prend un numéro de téléphone en paramètre et vérifie qu'il
	 * s'agisse d'un numéro français au format internationnal.
	 * 
	 * @param String
	 * @return boolean
	 */
	private static final boolean checkFrance(String telephone) {
		return telephone.startsWith("0033") && telephone.length() == 13; //$NON-NLS-1$
	}

	/**
	 * Cette méthode prend un numéro de téléphone en paramètre et vérifie qu'il
	 * s'agisse d'un numéro italien au format internationnal.
	 * 
	 * @param String
	 * @return boolean
	 */
	private static final boolean checkItalie(String telephone) {
		return telephone.startsWith("0039"); //$NON-NLS-1$
	}

	/**
	 * Cette méthode prend un numéro de téléphone en paramètre et vérifie qu'il
	 * s'agisse d'un numéro allemand au format internationnal.
	 * 
	 * @param String
	 * @return boolean
	 */
	private static final boolean checkAllemange(String telephone) {
		return telephone.startsWith("0049"); //$NON-NLS-1$
	}

	/**
	 * Cette méthode prend un numéro de téléphone en paramètre et vérifie qu'il
	 * s'agisse d'un numéro hollandais au format internationnal.
	 * 
	 * @param String
	 * @return boolean
	 */
	private static final boolean checkPaysBas(String telephone) {
		return telephone.startsWith("0031") && telephone.length() == 13; //$NON-NLS-1$
	}
}
