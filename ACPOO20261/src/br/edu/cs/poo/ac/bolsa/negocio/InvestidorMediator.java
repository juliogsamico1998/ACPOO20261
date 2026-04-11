package br.edu.cs.poo.ac.bolsa.negocio;

import java.time.LocalDate;

import br.edu.cs.poo.ac.bolsa.dao.DAOInvestidorEmpresa;
import br.edu.cs.poo.ac.bolsa.dao.DAOInvestidorPessoa;
import br.edu.cs.poo.ac.bolsa.entidade.Contatos;
import br.edu.cs.poo.ac.bolsa.entidade.Endereco;
import br.edu.cs.poo.ac.bolsa.entidade.FaixaRenda;
import br.edu.cs.poo.ac.bolsa.entidade.InvestidorEmpresa;
import br.edu.cs.poo.ac.bolsa.entidade.InvestidorPessoa;
import br.edu.cs.poo.ac.bolsa.util.MensagensValidacao;
import br.edu.cs.poo.ac.bolsa.util.ResultadoValidacao;
import br.edu.cs.poo.ac.bolsa.util.ValidadorCpfCnpj;

public class InvestidorMediator {

    private boolean ehBranco(String s) {
        return s == null || s.trim().isEmpty();
    }

    private boolean soNumeros(String s) {
        return s != null && s.matches("\\d+");
    }

    private boolean emailValido(String email) {
        return email != null && email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    }

    private MensagensValidacao validarEndereco(Endereco endereco) {
        MensagensValidacao msgs = new MensagensValidacao();

        if (endereco == null) {
            msgs.adicionar("Endere\u00e7o \u00e9 obrigat\u00f3rio.");
            return msgs;
        }

        if (ehBranco(endereco.getLogradouro())) {
            msgs.adicionar("Logradouro \u00e9 obrigat\u00f3rio.");
        }
        if (ehBranco(endereco.getNumero())) {
            msgs.adicionar("N\u00famero \u00e9 obrigat\u00f3rio.");
        }
        if (ehBranco(endereco.getPais())) {
            msgs.adicionar("Pa\u00eds \u00e9 obrigat\u00f3rio.");
        }
        if (ehBranco(endereco.getEstado())) {
            msgs.adicionar("Estado \u00e9 obrigat\u00f3rio.");
        }
        if (ehBranco(endereco.getCidade())) {
            msgs.adicionar("Cidade \u00e9 obrigat\u00f3rio.");
        }

        return msgs;
    }

    private MensagensValidacao validarContatos(Contatos contatos, boolean ehPJ) {
        MensagensValidacao msgs = new MensagensValidacao();

        if (contatos == null) {
            msgs.adicionar("Contatos \u00e9 obrigat\u00f3rio.");
            return msgs;
        }

        if (!emailValido(contatos.getEmail())) {
            msgs.adicionar("E-mail inv\u00e1lido.");
        }

        boolean temFixo = !ehBranco(contatos.getTelefoneFixo());
        boolean temCel = !ehBranco(contatos.getTelefoneCelular());
        boolean temWhats = !ehBranco(contatos.getNumeroWhatsApp());

        if (!temFixo && !temCel && !temWhats) {
            msgs.adicionar("Pelo menos um telefone deve ser informado.");
        }

        if (temFixo && !soNumeros(contatos.getTelefoneFixo())) {
            msgs.adicionar("Telefone fixo deve conter apenas n\u00fameros.");
        }
        if (temCel && !soNumeros(contatos.getTelefoneCelular())) {
            msgs.adicionar("Telefone celular deve conter apenas n\u00fameros.");
        }
        if (temWhats && !soNumeros(contatos.getNumeroWhatsApp())) {
            msgs.adicionar("WhatsApp deve conter apenas n\u00fameros.");
        }

        if (ehPJ && ehBranco(contatos.getNomeParaContato())) {
            msgs.adicionar("Nome para contato \u00e9 obrigat\u00f3rio para pessoa jur\u00eddica.");
        }

        return msgs;
    }

    private MensagensValidacao validar(DadosInvestidor d) {
        MensagensValidacao msgs = new MensagensValidacao();

        if (d == null) {
            msgs.adicionar("Investidor \u00e9 obrigat\u00f3rio.");
            return msgs;
        }

        if (d.getEndereco() != null) {
            msgs.adicionar(validarEndereco(d.getEndereco()));
        }

        if (d.getContatos() != null) {
            msgs.adicionar(validarContatos(d.getContatos(), d.ehInvestidorEmpresa()));
        }

        if (ehBranco(d.getNome())) {
            msgs.adicionar("Nome \u00e9 obrigat\u00f3rio.");
        }

        if (d.getEndereco() == null) {
            msgs.adicionar("Endere\u00e7o \u00e9 obrigat\u00f3rio.");
        }

        if (d.getDataCriacao() == null) {
            msgs.adicionar("Data de cria\u00e7\u00e3o \u00e9 obrigat\u00f3ria.");
        } else if (d.getDataCriacao().isAfter(LocalDate.now())) {
            msgs.adicionar("Data de cria\u00e7\u00e3o deve ser menor ou igual \u00e0 data atual.");
        }

        if (d.getBonus() == null) {
            msgs.adicionar("B\u00f4nus \u00e9 obrigat\u00f3rio.");
        } else if (d.getBonus().doubleValue() < 0) {
            msgs.adicionar("B\u00f4nus deve ser maior ou igual a zero.");
        }

        if (d.getContatos() == null) {
            msgs.adicionar("Contatos \u00e9 obrigat\u00f3rio.");
        }

        return msgs;
    }

    private MensagensValidacao validarInvestidorEmpresa(InvestidorEmpresa ie) {
        MensagensValidacao msgs = new MensagensValidacao();

        if (ie == null) {
            msgs.adicionar("Investidor Empresa \u00e9 obrigat\u00f3rio.");
            return msgs;
        }

        msgs.adicionar(validar(new DadosInvestidor(ie, null)));

        ResultadoValidacao r = ValidadorCpfCnpj.validarCnpj(ie.getCnpj());
        if (r != null) {
            msgs.adicionar(r.getMensagem());
        }

        if (ie.getFaturamento() < 100000.0) {
            msgs.adicionar("Faturamento deve ser maior ou igual a 100000.0.");
        }

        return msgs;
    }

    private FaixaRenda calcularFaixa(double renda) {
        if (renda >= FaixaRenda.PREMIUM.getValorInicial()) {
            return FaixaRenda.PREMIUM;
        }
        if (renda >= FaixaRenda.DIFERENCIADA.getValorInicial()) {
            return FaixaRenda.DIFERENCIADA;
        }
        return FaixaRenda.REGULAR;
    }

    private MensagensValidacao validarInvestidorPessoa(InvestidorPessoa ip) {
        MensagensValidacao msgs = new MensagensValidacao();

        if (ip == null) {
            msgs.adicionar("Investidor Pessoa \u00e9 obrigat\u00f3rio.");
            return msgs;
        }

        msgs.adicionar(validar(new DadosInvestidor(null, ip)));

        ResultadoValidacao r = ValidadorCpfCnpj.validarCpf(ip.getCpf());
        if (r != null) {
            msgs.adicionar(r.getMensagem());
        }

        if (ip.getRenda() < 10000.0) {
            msgs.adicionar("Renda deve ser maior ou igual a 10000.0.");
        } else {
            ip.setFaixaRenda(calcularFaixa(ip.getRenda()));
        }

        return msgs;
    }

    public MensagensValidacao incluirInvestidorPessoa(InvestidorPessoa ip) {
        MensagensValidacao msgs = validarInvestidorPessoa(ip);

        if (msgs.estaVazio()) {
            DAOInvestidorPessoa dao = new DAOInvestidorPessoa();
            if (!dao.incluirInvestidorPessoa(ip)) {
                msgs.adicionar("Investidor Pessoa j\u00e1 existente.");
            }
        }
        return msgs;
    }

    public MensagensValidacao alterarInvestidorPessoa(InvestidorPessoa ip) {
        MensagensValidacao msgs = validarInvestidorPessoa(ip);

        if (msgs.estaVazio()) {
            DAOInvestidorPessoa dao = new DAOInvestidorPessoa();
            if (!dao.alterarInvestidorPessoa(ip)) {
                msgs.adicionar("Investidor Pessoa n\u00e3o existente.");
            }
        }
        return msgs;
    }

    public MensagensValidacao excluirInvestidorPessoa(String cpf) {
        MensagensValidacao msgs = new MensagensValidacao();

        ResultadoValidacao r = ValidadorCpfCnpj.validarCpf(cpf);
        if (r != null) {
            msgs.adicionar(r.getMensagem());
            return msgs;
        }

        DAOInvestidorPessoa dao = new DAOInvestidorPessoa();
        if (!dao.excluirInvestidorPessoa(cpf)) {
            msgs.adicionar("Investidor Pessoa n\u00e3o existente.");
        }

        return msgs;
    }

    public InvestidorPessoa buscarInvestidorPessoa(String cpf) {
        if (ValidadorCpfCnpj.validarCpf(cpf) != null) {
            return null;
        }
        return new DAOInvestidorPessoa().buscarInvestidorPessoa(cpf);
    }

    public MensagensValidacao incluirInvestidorEmpresa(InvestidorEmpresa ie) {
        MensagensValidacao msgs = validarInvestidorEmpresa(ie);

        if (msgs.estaVazio()) {
            DAOInvestidorEmpresa dao = new DAOInvestidorEmpresa();
            if (!dao.incluirInvestidorEmpresa(ie)) {
                msgs.adicionar("Investidor Empresa j\u00e1 existente.");
            }
        }
        return msgs;
    }

    public MensagensValidacao alterarInvestidorEmpresa(InvestidorEmpresa ie) {
        MensagensValidacao msgs = validarInvestidorEmpresa(ie);

        if (msgs.estaVazio()) {
            DAOInvestidorEmpresa dao = new DAOInvestidorEmpresa();
            if (!dao.alterarInvestidorEmpresa(ie)) {
                msgs.adicionar("Investidor Empresa n\u00e3o existente.");
            }
        }
        return msgs;
    }

    public MensagensValidacao excluirInvestidorEmpresa(String cnpj) {
        MensagensValidacao msgs = new MensagensValidacao();

        ResultadoValidacao r = ValidadorCpfCnpj.validarCnpj(cnpj);
        if (r != null) {
            msgs.adicionar(r.getMensagem());
            return msgs;
        }

        DAOInvestidorEmpresa dao = new DAOInvestidorEmpresa();
        if (!dao.excluirInvestidorEmpresa(cnpj)) {
            msgs.adicionar("Investidor Empresa n\u00e3o existente.");
        }

        return msgs;
    }

    public InvestidorEmpresa buscarInvestidorEmpresa(String cnpj) {
        if (ValidadorCpfCnpj.validarCnpj(cnpj) != null) {
            return null;
        }
        return new DAOInvestidorEmpresa().buscarInvestidorEmpresa(cnpj);
    }
}