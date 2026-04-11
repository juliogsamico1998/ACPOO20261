package br.edu.cs.poo.ac.bolsa.negocio;

import br.edu.cs.poo.ac.bolsa.dao.DAOAtivo;
import br.edu.cs.poo.ac.bolsa.entidade.Ativo;
import br.edu.cs.poo.ac.bolsa.util.MensagensValidacao;

public class AtivoMediator {

    private static AtivoMediator instancia = new AtivoMediator();

    private AtivoMediator() {
    }

    public static AtivoMediator getInstancia() {
        return instancia;
    }

    private boolean ehBranco(String s) {
        return s == null || s.trim().isEmpty();
    }

    private MensagensValidacao validar(Ativo ativo) {
        MensagensValidacao msgs = new MensagensValidacao();

        if (ativo == null) {
            msgs.adicionar("Ativo \u00e9 obrigat\u00f3rio.");
            return msgs;
        }

        if (ativo.getCodigo() <= 0) {
            msgs.adicionar("C\u00f3digo deve ser maior que zero.");
        }

        if (ehBranco(ativo.getDescricao())) {
            msgs.adicionar("Descri\u00e7\u00e3o \u00e9 obrigat\u00f3ria.");
        }

        if (ativo.getValorMinimoAplicacao() <= 0) {
            msgs.adicionar("Valor m\u00ednimo de aplica\u00e7\u00e3o deve ser maior que zero.");
        }

        if (ativo.getValorMaximoAplicacao() <= 0) {
            msgs.adicionar("Valor m\u00e1ximo de aplica\u00e7\u00e3o deve ser maior que zero.");
        }

        if (ativo.getValorMinimoAplicacao() > 0
                && ativo.getValorMaximoAplicacao() > 0
                && ativo.getValorMinimoAplicacao() > ativo.getValorMaximoAplicacao()) {
            msgs.adicionar("Valor m\u00ednimo de aplica\u00e7\u00e3o deve ser menor ou igual a valor m\u00e1ximo de aplica\u00e7\u00e3o.");
        }

        if (ativo.getTaxaMensalMinima() < 0) {
            msgs.adicionar("Taxa mensal m\u00ednima deve ser maior ou igual a zero.");
        }

        if (ativo.getTaxaMensalMaxima() < 0) {
            msgs.adicionar("Taxa mensal m\u00e1xima deve ser maior ou igual a zero.");
        }

        if (ativo.getTaxaMensalMinima() > ativo.getTaxaMensalMaxima()) {
            msgs.adicionar("Taxa mensal m\u00ednima deve ser menor ou igual a taxa mensal m\u00e1xima.");
        }

        if (ativo.getFaixaMinimaPermitida() == null) {
            msgs.adicionar("Faixa m\u00ednima permitida \u00e9 obrigat\u00f3ria.");
        }

        if (ativo.getPrazoEmMeses() <= 0) {
            msgs.adicionar("Prazo em meses deve ser maior que zero.");
        }

        return msgs;
    }

    public MensagensValidacao incluir(Ativo ativo) {
        MensagensValidacao msgs = validar(ativo);

        if (msgs.estaVazio()) {
            DAOAtivo dao = new DAOAtivo();
            if (!dao.incluir(ativo)) {
                msgs.adicionar("Ativo j\u00e1 existente.");
            }
        }

        return msgs;
    }

    public MensagensValidacao alterar(Ativo ativo) {
        MensagensValidacao msgs = validar(ativo);

        if (msgs.estaVazio()) {
            DAOAtivo dao = new DAOAtivo();
            if (!dao.alterar(ativo)) {
                msgs.adicionar("Ativo n\u00e3o existente.");
            }
        }

        return msgs;
    }

    public MensagensValidacao excluir(long codigo) {
        MensagensValidacao msgs = new MensagensValidacao();

        if (codigo <= 0) {
            msgs.adicionar("C\u00f3digo deve ser maior que zero.");
            return msgs;
        }

        DAOAtivo dao = new DAOAtivo();
        if (!dao.excluir(codigo)) {
            msgs.adicionar("Ativo n\u00e3o existente.");
        }

        return msgs;
    }

    public Ativo buscar(long codigo) {
        if (codigo <= 0) {
            return null;
        }

        DAOAtivo dao = new DAOAtivo();
        return dao.buscar(codigo);
    }
}