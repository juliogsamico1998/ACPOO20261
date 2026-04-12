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
    private DAOInvestidorEmpresa daoInvEmp = new DAOInvestidorEmpresa();
    private DAOInvestidorPessoa daoInvPes = new DAOInvestidorPessoa();

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
            msgs.adicionar("Endereço é obrigatório.");
            return msgs;
        }

        if (ehBranco(endereco.getLogradouro())) {
            msgs.adicionar("Logradouro é obrigatório.");
        }
        if (ehBranco(endereco.getNumero())) {
            msgs.adicionar("Número é obrigatório.");
        }
        if (ehBranco(endereco.getPais())) {
            msgs.adicionar("País é obrigatório.");
        }
        if (ehBranco(endereco.getEstado())) {
            msgs.adicionar("Estado é obrigatório.");
        }
        if (ehBranco(endereco.getCidade())) {
            msgs.adicionar("Cidade é obrigatório.");
        }

        return msgs;
    }

    private MensagensValidacao validarContatos(Contatos contatos, boolean ehPessoaJuridica) {
        MensagensValidacao msgs = new MensagensValidacao();

        if (contatos == null) {
            msgs.adicionar("Contatos é obrigatório.");
            return msgs;
        }

        if (!emailValido(contatos.getEmail())) {
            msgs.adicionar("E-mail inválido.");
        }

        boolean temFixo = !ehBranco(contatos.getTelefoneFixo());
        boolean temCelular = !ehBranco(contatos.getTelefoneCelular());
        boolean temWhatsapp = !ehBranco(contatos.getNumeroWhatsApp());

        if (!temFixo && !temCelular && !temWhatsapp) {
            msgs.adicionar("Pelo menos um telefone deve ser informado.");
        }

        if (temFixo && !soNumeros(contatos.getTelefoneFixo())) {
            msgs.adicionar("Telefone fixo deve conter apenas números.");
        }
        if (temCelular && !soNumeros(contatos.getTelefoneCelular())) {
            msgs.adicionar("Telefone celular deve conter apenas números.");
        }
        if (temWhatsapp && !soNumeros(contatos.getNumeroWhatsApp())) {
            msgs.adicionar("WhatsApp deve conter apenas números.");
        }

        if (ehPessoaJuridica && ehBranco(contatos.getNomeParaContato())) {
            msgs.adicionar("Nome para contato é obrigatório para pessoa jurídica.");
        }

        return msgs;
    }

    private MensagensValidacao validar(DadosInvestidor dadosInv) {
        MensagensValidacao msgs = new MensagensValidacao();

        if (dadosInv == null) {
            msgs.adicionar("Investidor é obrigatório.");
            return msgs;
        }

        if (ehBranco(dadosInv.getNome())) {
            msgs.adicionar("Nome é obrigatório.");
        }
        if (dadosInv.getEndereco() == null) {
            msgs.adicionar("Endereço é obrigatório.");
        }
        if (dadosInv.getDataCriacao() == null) {
            msgs.adicionar("Data de criação é obrigatória.");
        } else if (dadosInv.getDataCriacao().isAfter(LocalDate.now())) {
            msgs.adicionar("Data de criação deve ser menor ou igual à data atual.");
        }
        if (dadosInv.getBonus() == null) {
            msgs.adicionar("Bônus é obrigatório.");
        } else if (dadosInv.getBonus().doubleValue() < 0) {
            msgs.adicionar("Bônus deve ser maior ou igual a zero.");
        }
        if (dadosInv.getContatos() == null) {
            msgs.adicionar("Contatos é obrigatório.");
        }

        if (dadosInv.getEndereco() != null) {
            msgs.adicionar(validarEndereco(dadosInv.getEndereco()));
        }
        if (dadosInv.getContatos() != null) {
            msgs.adicionar(validarContatos(dadosInv.getContatos(), dadosInv.ehInvestidorEmpresa()));
        }

        return msgs;
    }

    private MensagensValidacao validarInvestidorEmpresa(InvestidorEmpresa ie) {
        MensagensValidacao msgs = new MensagensValidacao();

        if (ie == null) {
            msgs.adicionar("Investidor Empresa é obrigatório.");
            return msgs;
        }

        DadosInvestidor dados = new DadosInvestidor(ie, null);
        msgs.adicionar(validar(dados));

        ResultadoValidacao resultadoCnpj = ValidadorCpfCnpj.validarCnpj(ie.getCnpj());
        if (resultadoCnpj != null) {
            msgs.adicionar(resultadoCnpj.getMensagem());
        }

        if (ie.getFaturamento() < 100000.0) {
            msgs.adicionar("Faturamento deve ser maior ou igual a 100000.0.");
        }

        return msgs;
    }

    private FaixaRenda calcularFaixaRenda(double renda) {
        if (renda >= FaixaRenda.PREMIUM.getValorInicial()) {
            return FaixaRenda.PREMIUM;
        } else if (renda >= FaixaRenda.DIFERENCIADA.getValorInicial()) {
            return FaixaRenda.DIFERENCIADA;
        } else {
            return FaixaRenda.REGULAR;
        }
    }

    private MensagensValidacao validarInvestidorPessoa(InvestidorPessoa ip) {
        MensagensValidacao msgs = new MensagensValidacao();

        if (ip == null) {
            msgs.adicionar("Investidor Pessoa é obrigatório.");
            return msgs;
        }

        DadosInvestidor dados = new DadosInvestidor(null, ip);
        msgs.adicionar(validar(dados));

        ResultadoValidacao resultadoCpf = ValidadorCpfCnpj.validarCpf(ip.getCpf());
        if (resultadoCpf != null) {
            msgs.adicionar(resultadoCpf.getMensagem());
        }

        if (ip.getRenda() < 10000.0) {
            msgs.adicionar("Renda deve ser maior ou igual a 10000.0.");
        } else {
            ip.setFaixaRenda(calcularFaixaRenda(ip.getRenda()));
        }

        return msgs;
    }

    public MensagensValidacao incluirInvestidorEmpresa(InvestidorEmpresa ie) {
        MensagensValidacao msgs = validarInvestidorEmpresa(ie);

        if (msgs.estaVazio()) {
            if (!daoInvEmp.incluirInvestidorEmpresa(ie)) {
                msgs.adicionar("Investidor Empresa já existente.");
            }
        }

        return msgs;
    }

    public MensagensValidacao alterarInvestidorEmpresa(InvestidorEmpresa ie) {
        MensagensValidacao msgs = validarInvestidorEmpresa(ie);

        if (msgs.estaVazio()) {
            if (!daoInvEmp.alterarInvestidorEmpresa(ie)) {
                msgs.adicionar("Investidor Empresa não existente.");
            }
        }

        return msgs;
    }

    public MensagensValidacao excluirInvestidorEmpresa(String cnpj) {
        MensagensValidacao msgs = new MensagensValidacao();

        ResultadoValidacao resultadoCnpj = ValidadorCpfCnpj.validarCnpj(cnpj);
        if (resultadoCnpj != null) {
            msgs.adicionar(resultadoCnpj.getMensagem());
            return msgs;
        }

        if (!daoInvEmp.excluirInvestidorEmpresa(cnpj)) {
            msgs.adicionar("Investidor Empresa não existente.");
        }

        return msgs;
    }

    public InvestidorEmpresa buscarInvestidorEmpresa(String cnpj) {
        if (ValidadorCpfCnpj.validarCnpj(cnpj) != null) {
            return null;
        }
        return daoInvEmp.buscarInvestidorEmpresa(cnpj);
    }

    public MensagensValidacao incluirInvestidorPessoa(InvestidorPessoa ip) {
        MensagensValidacao msgs = validarInvestidorPessoa(ip);

        if (msgs.estaVazio()) {
            if (!daoInvPes.incluirInvestidorPessoa(ip)) {
                msgs.adicionar("Investidor Pessoa já existente.");
            }
        }

        return msgs;
    }

    public MensagensValidacao alterarInvestidorPessoa(InvestidorPessoa ip) {
        MensagensValidacao msgs = validarInvestidorPessoa(ip);

        if (msgs.estaVazio()) {
            if (!daoInvPes.alterarInvestidorPessoa(ip)) {
                msgs.adicionar("Investidor Pessoa não existente.");
            }
        }

        return msgs;
    }

    public MensagensValidacao excluirInvestidorPessoa(String cpf) {
        MensagensValidacao msgs = new MensagensValidacao();

        ResultadoValidacao resultadoCpf = ValidadorCpfCnpj.validarCpf(cpf);
        if (resultadoCpf != null) {
            msgs.adicionar(resultadoCpf.getMensagem());
            return msgs;
        }

        if (!daoInvPes.excluirInvestidorPessoa(cpf)) {
            msgs.adicionar("Investidor Pessoa não existente.");
        }

        return msgs;
    }

    public InvestidorPessoa buscarInvestidorPessoa(String cpf) {
        if (ValidadorCpfCnpj.validarCpf(cpf) != null) {
            return null;
        }
        return daoInvPes.buscarInvestidorPessoa(cpf);
    }
}