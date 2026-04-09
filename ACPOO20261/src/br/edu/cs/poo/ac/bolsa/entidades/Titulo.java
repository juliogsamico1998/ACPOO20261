package br.edu.cs.poo.ac.bolsa.entidades;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;

public class Titulo {
    private InvestidorPessoa investidorPessoa;
    private InvestidorEmpresa investidorEmpresa;
    private Ativo ativo;
    private BigDecimal valorInvestido;
    private BigDecimal valorAtual;
    private BigDecimal taxaDiaria;
    private LocalDate dataAplicacao;
    private LocalDate dataVencimento;
    private LocalDate dataUltimoRendimento;
    private StatusTitulo status;

    public Titulo(InvestidorPessoa investidorPessoa, InvestidorEmpresa investidorEmpresa,
                  Ativo ativo, BigDecimal valorInvestido, BigDecimal valorAtual,
                  BigDecimal taxaDiaria, LocalDate dataAplicacao, LocalDate dataVencimento,
                  LocalDate dataUltimoRendimento, StatusTitulo status) {
        this.investidorPessoa = investidorPessoa;
        this.investidorEmpresa = investidorEmpresa;
        this.ativo = ativo;
        this.valorInvestido = valorInvestido;
        this.valorAtual = valorAtual;
        this.taxaDiaria = taxaDiaria;
        this.dataAplicacao = dataAplicacao;
        this.dataVencimento = dataVencimento;
        this.dataUltimoRendimento = dataUltimoRendimento;
        this.status = status;
    }

    public InvestidorPessoa getInvestidorPessoa() {
        return investidorPessoa;
    }

    public void setInvestidorPessoa(InvestidorPessoa investidorPessoa) {
        this.investidorPessoa = investidorPessoa;
    }

    public InvestidorEmpresa getInvestidorEmpresa() {
        return investidorEmpresa;
    }

    public void setInvestidorEmpresa(InvestidorEmpresa investidorEmpresa) {
        this.investidorEmpresa = investidorEmpresa;
    }

    public Ativo getAtivo() {
        return ativo;
    }

    public void setAtivo(Ativo ativo) {
        this.ativo = ativo;
    }

    public BigDecimal getValorInvestido() {
        return valorInvestido;
    }

    public void setValorInvestido(BigDecimal valorInvestido) {
        this.valorInvestido = valorInvestido;
    }

    public BigDecimal getValorAtual() {
        return valorAtual;
    }

    public void setValorAtual(BigDecimal valorAtual) {
        this.valorAtual = valorAtual;
    }

    public BigDecimal getTaxaDiaria() {
        return taxaDiaria;
    }

    public void setTaxaDiaria(BigDecimal taxaDiaria) {
        this.taxaDiaria = taxaDiaria;
    }

    public LocalDate getDataAplicacao() {
        return dataAplicacao;
    }

    public void setDataAplicacao(LocalDate dataAplicacao) {
        this.dataAplicacao = dataAplicacao;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public LocalDate getDataUltimoRendimento() {
        return dataUltimoRendimento;
    }

    public void setDataUltimoRendimento(LocalDate dataUltimoRendimento) {
        this.dataUltimoRendimento = dataUltimoRendimento;
    }

    public StatusTitulo getStatus() {
        return status;
    }

    public void setStatus(StatusTitulo status) {
        this.status = status;
    }

    public boolean render() {
        LocalDate hoje = LocalDate.now();

        if (status != StatusTitulo.ATIVO) {
            return false;
        }

        if (hoje.isEqual(dataVencimento) || hoje.isAfter(dataVencimento)) {
            return false;
        }

        if (hoje.isEqual(dataAplicacao) || hoje.isBefore(dataAplicacao)) {
            return false;
        }

        if (dataUltimoRendimento != null &&
            (hoje.isEqual(dataUltimoRendimento) || hoje.isBefore(dataUltimoRendimento))) {
            return false;
        }

        long dias;
        if (dataUltimoRendimento == null) {
            dias = ChronoUnit.DAYS.between(dataAplicacao, hoje);
        } else {
            dias = ChronoUnit.DAYS.between(dataUltimoRendimento, hoje);
        }

        if (dias <= 0) {
            return false;
        }

        BigDecimal fator = BigDecimal.ONE.add(
                taxaDiaria.divide(new BigDecimal("100"), MathContext.DECIMAL128)
        );

        valorAtual = valorAtual.multiply(fator.pow((int) dias, MathContext.DECIMAL128));
        dataUltimoRendimento = hoje;

        return true;
    }

    public String getNumero() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String dataFormatada = dataAplicacao.format(formatter) + "0000";

        if (investidorPessoa != null) {
            return "000" + investidorPessoa.getCpf() + ativo.getCodigo() + dataFormatada;
        }

        if (investidorEmpresa != null) {
            return investidorEmpresa.getCnpj() + ativo.getCodigo() + dataFormatada;
        }

        return null;
    }
}