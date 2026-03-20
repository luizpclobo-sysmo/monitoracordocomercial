package br.com.lobo.monitor.acordo.comercial.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AcordoComercial {

    private BigDecimal idAcordo;
    private int nrAcordo;
    private String txSituacao;
    private String txTipo;
    private String txSubtipo;
    private int cdEmpresa;
    private int cdUsuario;
    private LocalDate dtAcordoInicio;
    private LocalDate dtAcordoFim;
    private LocalDateTime dtCadastro;
    private LocalDateTime dtManutencao;

    public BigDecimal getIdAcordo() { return idAcordo; }
    public void setIdAcordo(BigDecimal idAcordo) { this.idAcordo = idAcordo; }

    public int getNrAcordo() { return nrAcordo; }
    public void setNrAcordo(int nrAcordo) { this.nrAcordo = nrAcordo; }

    public String getTxSituacao() { return txSituacao; }
    public void setTxSituacao(String txSituacao) { this.txSituacao = txSituacao; }

    public String getTxTipo() { return txTipo; }
    public void setTxTipo(String txTipo) { this.txTipo = txTipo; }

    public String getTxSubtipo() { return txSubtipo; }
    public void setTxSubtipo(String txSubtipo) { this.txSubtipo = txSubtipo; }

    public int getCdEmpresa() { return cdEmpresa; }
    public void setCdEmpresa(int cdEmpresa) { this.cdEmpresa = cdEmpresa; }

    public int getCdUsuario() { return cdUsuario; }
    public void setCdUsuario(int cdUsuario) { this.cdUsuario = cdUsuario; }

    public LocalDate getDtAcordoInicio() { return dtAcordoInicio; }
    public void setDtAcordoInicio(LocalDate dtAcordoInicio) { this.dtAcordoInicio = dtAcordoInicio; }

    public LocalDate getDtAcordoFim() { return dtAcordoFim; }
    public void setDtAcordoFim(LocalDate dtAcordoFim) { this.dtAcordoFim = dtAcordoFim; }

    public LocalDateTime getDtCadastro() { return dtCadastro; }
    public void setDtCadastro(LocalDateTime dtCadastro) { this.dtCadastro = dtCadastro; }

    public LocalDateTime getDtManutencao() { return dtManutencao; }
    public void setDtManutencao(LocalDateTime dtManutencao) { this.dtManutencao = dtManutencao; }

    public String getTipoDescricao() {
        if (txTipo == null) return "Desconhecido";
        String tipo = txTipo.trim() + (txSubtipo != null ? txSubtipo.trim() : "");
        return switch (tipo) {
            case "BP" -> "Bonificação de Produto";
            case "SI" -> "Sell-In";
            case "SO" -> "Sell-Out";
            case "AV" -> "Antecipação de Verba";
            default -> tipo;
        };
    }

    public String getSituacaoDescricao() {
        if (txSituacao == null) return "Desconhecida";
        return switch (txSituacao) {
            case "D" -> "Digitando";
            case "F" -> "Digitação finalizada";
            case "U" -> "Usado nas Compras";
            case "P" -> "Parcialmente Recebido";
            case "X" -> "Finalizado";
            case "E" -> "Excluído";
            default -> txSituacao;
        };
    }
}
