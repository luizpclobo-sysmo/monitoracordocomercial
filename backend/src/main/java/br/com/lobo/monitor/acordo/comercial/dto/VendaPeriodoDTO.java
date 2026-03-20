package br.com.lobo.monitor.acordo.comercial.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class VendaPeriodoDTO {
    private int cdEmpresa;
    private int cdProduto;
    private String txProdutoDescricao;
    private LocalDate dtInicio;
    private LocalDate dtFim;
    private BigDecimal quantidadeVendida;
    private BigDecimal valorTotal;
    private int quantidadeNotas;
    private BigDecimal pcUnidade;
    private BigDecimal vlUnidade;

    public int getCdEmpresa() { return cdEmpresa; }
    public void setCdEmpresa(int cdEmpresa) { this.cdEmpresa = cdEmpresa; }

    public int getCdProduto() { return cdProduto; }
    public void setCdProduto(int cdProduto) { this.cdProduto = cdProduto; }

    public String getTxProdutoDescricao() { return txProdutoDescricao; }
    public void setTxProdutoDescricao(String txProdutoDescricao) { this.txProdutoDescricao = txProdutoDescricao; }

    public LocalDate getDtInicio() { return dtInicio; }
    public void setDtInicio(LocalDate dtInicio) { this.dtInicio = dtInicio; }

    public LocalDate getDtFim() { return dtFim; }
    public void setDtFim(LocalDate dtFim) { this.dtFim = dtFim; }

    public BigDecimal getQuantidadeVendida() { return quantidadeVendida; }
    public void setQuantidadeVendida(BigDecimal quantidadeVendida) { this.quantidadeVendida = quantidadeVendida; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }

    public int getQuantidadeNotas() { return quantidadeNotas; }
    public void setQuantidadeNotas(int quantidadeNotas) { this.quantidadeNotas = quantidadeNotas; }

    public BigDecimal getPcUnidade() { return pcUnidade; }
    public void setPcUnidade(BigDecimal pcUnidade) { this.pcUnidade = pcUnidade; }

    public BigDecimal getVlUnidade() { return vlUnidade; }
    public void setVlUnidade(BigDecimal vlUnidade) { this.vlUnidade = vlUnidade; }
}
