package com.dschulz.rucconv.util;

/**
 *
 * @author Diego Schulz
 */
public class DigitoVerificador {

    /**
     * Base numérica por defecto.
     */
    private static final int DEFAULT_MAX_BASE = 11;

    /**
     * Invierte un String.
     */
    private static String revString(String str) {

        if (str.length() > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < str.length(); i++) {
                sb.insert(0, str.charAt(i));
            }

            return sb.toString();
        }

        return str;
    }

    /**
     * Elimina caracteres no numéricos.
     */
    private static String sanitize(String docno) {

        StringBuilder sb = new StringBuilder();

        for (char c : docno.toCharArray()) {
            if (Character.isDigit(c)) {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
     * Calcula el digito verificador para docno.
     *
     * @param docno Número de documento.
     * @param max_base Máxima base numérica.
     * @return Dígito verificador
     */
    public static int para(String docno, int max_base) {

        String m_docno;
        m_docno = sanitize(docno);

        int dv;
        int k = 2;
        int suma = 0;
        int resto;

        for (char c : revString(m_docno).toCharArray()) {
            int digito = Character.getNumericValue(c);
            k = k > max_base ? 2 : k;
            suma += digito * k++;
        }

        resto = suma % DEFAULT_MAX_BASE;
        dv = resto > 1 ? DEFAULT_MAX_BASE - resto : 0;

        return dv;
    }

    /**
     * Calcula el digito verificador para docno usando base numérica 11.
     *
     * @param docno Documento
     * @return dígito verificador.
     */
    public static int para(String docno) {
        return para(docno, DEFAULT_MAX_BASE);
    }

}
