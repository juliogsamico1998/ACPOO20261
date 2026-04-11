package br.edu.cs.poo.ac.bolsa.util;

public class ValidadorCpfCnpj {

    // ================= CPF =================

    public static ResultadoValidacao validarCpf(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return ResultadoValidacao.NAO_INFORMADO;
        }

        String numero = cpf.replace(".", "").replace("-", "").trim();

        if (numero.length() != 11 || !numero.matches("\\d{11}") || todosIguais(numero)) {
            return ResultadoValidacao.FORMATO_INVALIDO;
        }

        int dv1 = calcularDvCpf(numero.substring(0, 9), 10);
        int dv2 = calcularDvCpf(numero.substring(0, 9) + dv1, 11);

        if (dv1 != Character.getNumericValue(numero.charAt(9)) ||
            dv2 != Character.getNumericValue(numero.charAt(10))) {
            return ResultadoValidacao.DV_INVALIDO;
        }

        return null;
    }

    private static int calcularDvCpf(String base, int pesoInicial) {
        int soma = 0;
        int peso = pesoInicial;

        for (int i = 0; i < base.length(); i++) {
            soma += Character.getNumericValue(base.charAt(i)) * peso;
            peso--;
        }

        int resto = soma % 11;
        return (resto < 2) ? 0 : 11 - resto;
    }

    // ================= CNPJ =================

    public static ResultadoValidacao validarCnpj(String cnpj) {
        if (cnpj == null || cnpj.trim().isEmpty()) {
            return ResultadoValidacao.NAO_INFORMADO;
        }

        String numero = cnpj.replace(".", "")
                            .replace("/", "")
                            .replace("-", "")
                            .trim();

        if (numero.length() != 14 || !numero.matches("\\d{14}") || todosIguais(numero)) {
            return ResultadoValidacao.FORMATO_INVALIDO;
        }

        int dv1 = calcularDvCnpj(numero.substring(0, 12));
        int dv2 = calcularDvCnpj(numero.substring(0, 12) + dv1);

        if (dv1 != Character.getNumericValue(numero.charAt(12)) ||
            dv2 != Character.getNumericValue(numero.charAt(13))) {
            return ResultadoValidacao.DV_INVALIDO;
        }

        return null;
    }

    private static int calcularDvCnpj(String base) {
        int[] pesos;

        if (base.length() == 12) {
            pesos = new int[]{5,4,3,2,9,8,7,6,5,4,3,2};
        } else {
            pesos = new int[]{6,5,4,3,2,9,8,7,6,5,4,3,2};
        }

        int soma = 0;

        for (int i = 0; i < base.length(); i++) {
            soma += Character.getNumericValue(base.charAt(i)) * pesos[i];
        }

        int resto = soma % 11;
        return (resto < 2) ? 0 : 11 - resto;
    }

    // ================= AUX =================

    private static boolean todosIguais(String valor) {
        char primeiro = valor.charAt(0);

        for (int i = 1; i < valor.length(); i++) {
            if (valor.charAt(i) != primeiro) {
                return false;
            }
        }
        return true;
    }
}