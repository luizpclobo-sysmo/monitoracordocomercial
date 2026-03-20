package br.com.lobo.monitor.acordo.comercial.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ProdutoVerbaDTO {

    private int cdEmpresa;
    private int cdProduto;
    private String txProdutoDescricao;
    private BigDecimal vlVerbaAntecipacao;
    private BigDecimal vlVerbaSellOut;
    private BigDecimal vlVerbaSellIn;
    private BigDecimal vlVerbaContaComercial;
    private int nrAcordoVigente;
    private BigDecimal vlUnidadeAcordo;
    private BigDecimal pcUnidadeAcordo;
    private String txTipoAcordo;
    private String txSubtipoAcordo;
    private BigDecimal vlPrecoVenda;
    private BigDecimal vlVerbaEsperada;
    private boolean vigente;
    private boolean correto;
    private String motivoInconsistencia;
    private LocalDate dtAcordoInicio;
    private LocalDate dtAcordoFim;
    private LocalDate dtCadastro;

    public int getCdEmpresa() { return cdEmpresa; }
    public void setCdEmpresa(int cdEmpresa) { this.cdEmpresa = cdEmpresa; }

    public int getCdProduto() { return cdProduto; }
    public void setCdProduto(int cdProduto) { this.cdProduto = cdProduto; }

    public String getTxProdutoDescricao() { return txProdutoDescricao; }
    public void setTxProdutoDescricao(String txProdutoDescricao) { this.txProdutoDescricao = txProdutoDescricao; }

    public BigDecimal getVlVerbaAntecipacao() { return vlVerbaAntecipacao; }
    public void setVlVerbaAntecipacao(BigDecimal vlVerbaAntecipacao) { this.vlVerbaAntecipacao = vlVerbaAntecipacao; }

    public BigDecimal getVlVerbaSellOut() { return vlVerbaSellOut; }
    public void setVlVerbaSellOut(BigDecimal vlVerbaSellOut) { this.vlVerbaSellOut = vlVerbaSellOut; }

    public BigDecimal getVlVerbaSellIn() { return vlVerbaSellIn; }
    public void setVlVerbaSellIn(BigDecimal vlVerbaSellIn) { this.vlVerbaSellIn = vlVerbaSellIn; }

    public BigDecimal getVlVerbaContaComercial() { return vlVerbaContaComercial; }
    public void setVlVerbaContaComercial(BigDecimal vlVerbaContaComercial) { this.vlVerbaContaComercial = vlVerbaContaComercial; }

    public int getNrAcordoVigente() { return nrAcordoVigente; }
    public void setNrAcordoVigente(int nrAcordoVigente) { this.nrAcordoVigente = nrAcordoVigente; }

    public BigDecimal getVlUnidadeAcordo() { return vlUnidadeAcordo; }
    public void setVlUnidadeAcordo(BigDecimal vlUnidadeAcordo) { this.vlUnidadeAcordo = vlUnidadeAcordo; }

    public BigDecimal getPcUnidadeAcordo() { return pcUnidadeAcordo; }
    public void setPcUnidadeAcordo(BigDecimal pcUnidadeAcordo) { this.pcUnidadeAcordo = pcUnidadeAcordo; }

    public String getTxTipoAcordo() { return txTipoAcordo; }
    public void setTxTipoAcordo(String txTipoAcordo) { this.txTipoAcordo = txTipoAcordo; }

    public String getTxSubtipoAcordo() { return txSubtipoAcordo; }
    public void setTxSubtipoAcordo(String txSubtipoAcordo) { this.txSubtipoAcordo = txSubtipoAcordo; }

    public BigDecimal getVlPrecoVenda() { return vlPrecoVenda; }
    public void setVlPrecoVenda(BigDecimal vlPrecoVenda) { this.vlPrecoVenda = vlPrecoVenda; }

    public BigDecimal getVlVerbaEsperada() { return vlVerbaEsperada; }
    public void setVlVerbaEsperada(BigDecimal vlVerbaEsperada) { this.vlVerbaEsperada = vlVerbaEsperada; }

    public boolean isVigente() { return vigente; }
    public void setVigente(boolean vigente) { this.vigente = vigente; }

    public boolean isCorreto() { return correto; }
    public void setCorreto(boolean correto) { this.correto = correto; }

    public String getMotivoInconsistencia() { return motivoInconsistencia; }
    public void setMotivoInconsistencia(String motivoInconsistencia) { this.motivoInconsistencia = motivoInconsistencia; }

    @JsonIgnore
    public LocalDate getDtAcordoInicio() { return dtAcordoInicio; }
    public void setDtAcordoInicio(LocalDate dtAcordoInicio) { this.dtAcordoInicio = dtAcordoInicio; }

    @JsonIgnore
    public LocalDate getDtAcordoFim() { return dtAcordoFim; }
    public void setDtAcordoFim(LocalDate dtAcordoFim) { this.dtAcordoFim = dtAcordoFim; }

    @JsonIgnore
    public LocalDate getDtCadastro() { return dtCadastro; }
    public void setDtCadastro(LocalDate dtCadastro) { this.dtCadastro = dtCadastro; }

    public String getTipoAcordoDescricao() {
        if (txTipoAcordo == null) return "-";
        String tipo = txTipoAcordo.trim() + (txSubtipoAcordo != null ? txSubtipoAcordo.trim() : "");
        return switch (tipo) {
            case "BP" -> "Bonif. Produto";
            case "SI" -> "Sell-In";
            case "SO" -> "Sell-Out";
            case "AV" -> "Antecip. Verba";
            default -> tipo;
        };
    }
}
