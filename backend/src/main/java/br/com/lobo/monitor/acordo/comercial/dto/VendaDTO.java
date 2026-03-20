package br.com.lobo.monitor.acordo.comercial.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class VendaDTO {

    private int cdEmpresa;
    private int cdProduto;
    private String nrNumero;
    private String txSerie;
    private LocalDate dtRegistro;
    private BigDecimal quantidade;
    private BigDecimal valorTotal;
    private String op1;
    private String op2;
    private String mod;
    private int cfl;
    private BigDecimal valorMercadoria;

    public int getCdEmpresa() { return cdEmpresa; }
    public void setCdEmpresa(int cdEmpresa) { this.cdEmpresa = cdEmpresa; }

    public int getCdProduto() { return cdProduto; }
    public void setCdProduto(int cdProduto) { this.cdProduto = cdProduto; }

    public String getNrNumero() { return nrNumero; }
    public void setNrNumero(String nrNumero) { this.nrNumero = nrNumero; }

    public String getTxSerie() { return txSerie; }
    public void setTxSerie(String txSerie) { this.txSerie = txSerie; }

    public LocalDate getDtRegistro() { return dtRegistro; }
    public void setDtRegistro(LocalDate dtRegistro) { this.dtRegistro = dtRegistro; }

    public BigDecimal getQuantidade() { return quantidade; }
    public void setQuantidade(BigDecimal quantidade) { this.quantidade = quantidade; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }

    public String getOp1() { return op1; }
    public void setOp1(String op1) { this.op1 = op1; }

    public String getOp2() { return op2; }
    public void setOp2(String op2) { this.op2 = op2; }

    public String getMod() { return mod; }
    public void setMod(String mod) { this.mod = mod; }

    public int getCfl() { return cfl; }
    public void setCfl(int cfl) { this.cfl = cfl; }

    public BigDecimal getValorMercadoria() { return valorMercadoria; }
    public void setValorMercadoria(BigDecimal valorMercadoria) { this.valorMercadoria = valorMercadoria; }
}
